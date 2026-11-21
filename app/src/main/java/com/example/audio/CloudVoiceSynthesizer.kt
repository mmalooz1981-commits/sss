package com.example.audio

import android.util.Log
import com.example.auth.FirebaseAuthHelper
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.InlineDataPart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

data class SynthesizedAudio(
    val bytes: ByteArray,
    val mimeType: String,
    val fileExtension: String,
    val engineUsed: String
)

sealed interface SynthesisResult {
    data class Success(val audio: SynthesizedAudio) : SynthesisResult
    data class Failure(val reasonAr: String, val isAuthRequired: Boolean = false) : SynthesisResult
}

/**
 * Secure cloud voice synthesis client supporting:
 * 1. Google Cloud Text-to-Speech (Chirp 3 HD) via a secure backend / Firebase Cloud Function
 *    authenticated with Firebase Auth ID token (zero API keys inside Android APK).
 * 2. Gemini TTS (gemini-3.1-flash-tts-preview) via Firebase AI Logic (App Check attested, keyless).
 */
class CloudVoiceSynthesizer {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val geminiTtsModel by lazy {
        Firebase.ai.generativeModel(
            modelName = GEMINI_TTS_MODEL,
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.AUDIO)
            }
        )
    }

    suspend fun synthesize(
        textEn: String,
        accent: VoiceAccent,
        persona: VoicePersona,
        speechMode: SpeechMode,
        enginePreference: VoiceEnginePreference,
        preferExpressiveGemini: Boolean,
        cloudTtsEndpointUrl: String
    ): SynthesisResult {
        val cleanEndpoint = cloudTtsEndpointUrl.trim()
        val hasSecureBackend = cleanEndpoint.startsWith("https://")

        val useGeminiFirst = when (enginePreference) {
            VoiceEnginePreference.GEMINI_TTS -> true
            VoiceEnginePreference.CHIRP3_HD -> false
            VoiceEnginePreference.HYBRID_AUTO -> preferExpressiveGemini || !hasSecureBackend
        }

        if (useGeminiFirst) {
            val geminiResult = synthesizeWithGeminiTts(textEn, accent, persona, speechMode)
            if (geminiResult is SynthesisResult.Success) return geminiResult
            if (hasSecureBackend && enginePreference == VoiceEnginePreference.HYBRID_AUTO) {
                return synthesizeWithChirp3HdBackend(
                    endpointUrl = cleanEndpoint,
                    textEn = textEn,
                    accent = accent,
                    persona = persona,
                    speechMode = speechMode
                )
            }
            return geminiResult
        } else {
            if (hasSecureBackend) {
                val chirpResult = synthesizeWithChirp3HdBackend(
                    endpointUrl = cleanEndpoint,
                    textEn = textEn,
                    accent = accent,
                    persona = persona,
                    speechMode = speechMode
                )
                if (chirpResult is SynthesisResult.Success) return chirpResult
                if (enginePreference == VoiceEnginePreference.HYBRID_AUTO) {
                    val fallbackGemini = synthesizeWithGeminiTts(textEn, accent, persona, speechMode)
                    if (fallbackGemini is SynthesisResult.Success) return fallbackGemini
                }
                return chirpResult
            } else {
                // Fall back to keyless Gemini TTS via Firebase AI Logic when backend URL is not yet set
                return synthesizeWithGeminiTts(textEn, accent, persona, speechMode)
            }
        }
    }

    private suspend fun synthesizeWithChirp3HdBackend(
        endpointUrl: String,
        textEn: String,
        accent: VoiceAccent,
        persona: VoicePersona,
        speechMode: SpeechMode
    ): SynthesisResult = withContext(Dispatchers.IO) {
        try {
            val idToken = FirebaseAuthHelper.getIdTokenOrNull()
                ?: return@withContext SynthesisResult.Failure(
                    reasonAr = "يرجى تسجيل الدخول بحساب Google من مكتبة الأصوات للمصادقة الآمنة مع خادم Chirp 3 HD.",
                    isAuthRequired = true
                )

            val payload = JSONObject().apply {
                put("text", textEn.trim())
                put("languageCode", accent.code)
                put("voiceName", persona.chirpVoiceFor(accent))
                put("speakingRate", speechMode.cloudSpeakingRate)
                put("audioEncoding", "MP3")
            }

            val request = Request.Builder()
                .url(endpointUrl)
                .addHeader("Authorization", "Bearer $idToken")
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            withTimeout(45_000L) {
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Chirp 3 HD backend HTTP ${response.code}")
                        return@use SynthesisResult.Failure(
                            reasonAr = "تعذّر توليد الصوت من خادم Chirp 3 HD (رمز الاستجابة ${response.code})."
                        )
                    }
                    val bodyBytes = response.body?.bytes()
                    if (bodyBytes == null || bodyBytes.isEmpty()) {
                        return@use SynthesisResult.Failure("استلم التطبيق ملفاً صوتياً فارغاً من الخادم.")
                    }
                    val contentType = response.header("Content-Type").orEmpty()
                    val audioBytes = if (contentType.contains("application/json", ignoreCase = true)) {
                        val json = JSONObject(bodyBytes.toString(Charsets.UTF_8))
                        val b64 = json.optString("audioContent", "")
                        if (b64.isBlank()) {
                            return@use SynthesisResult.Failure("استجابة الخادم لا تحتوي على بيانات صوتية صالحة.")
                        }
                        android.util.Base64.decode(b64, android.util.Base64.DEFAULT)
                    } else {
                        bodyBytes
                    }

                    SynthesisResult.Success(
                        SynthesizedAudio(
                            bytes = audioBytes,
                            mimeType = "audio/mpeg",
                            fileExtension = "mp3",
                            engineUsed = "Chirp 3 HD (${persona.chirpVoiceFor(accent)})"
                        )
                    )
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Chirp 3 HD request timed out", e)
            SynthesisResult.Failure("انتهت مهلة الاتصال بخادم الصوت، يرجى المحاولة مجدداً.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Chirp 3 HD synthesis error", e)
            SynthesisResult.Failure(e.localizedMessage ?: "تعذّر الاتصال بخادم Chirp 3 HD.")
        }
    }

    private suspend fun synthesizeWithGeminiTts(
        textEn: String,
        accent: VoiceAccent,
        persona: VoicePersona,
        speechMode: SpeechMode
    ): SynthesisResult {
        return try {
            val accentInstruction = if (accent == VoiceAccent.EN_GB) {
                "British English (en-GB)"
            } else {
                "American English (en-US)"
            }
            val paceInstruction = when (speechMode) {
                SpeechMode.SLOW -> "Speak slowly and clearly with crisp articulation for a child learning English."
                SpeechMode.NORMAL -> "Speak at a natural, clear classroom pace."
                SpeechMode.PRACTICE -> "Speak clearly and slightly slowly so a student can repeat after you."
            }
            val prompt = buildString {
                append("Read the following English educational text aloud in ")
                append(accentInstruction)
                append(". Persona: ")
                append(persona.displayNameEn)
                append(" (")
                append(persona.stylePromptEn)
                append("). ")
                append(paceInstruction)
                append(" Do not add extra words or commentary; speak only the exact text below:\n\n")
                append(textEn.trim())
            }

            val response = withTimeout(45_000L) {
                geminiTtsModel.generateContent(prompt)
            }

            val audioPart = response.candidates
                .asSequence()
                .flatMap { it.content.parts.asSequence() }
                .filterIsInstance<InlineDataPart>()
                .firstOrNull { it.mimeType.startsWith("audio/", ignoreCase = true) }

            if (audioPart == null || audioPart.inlineData.isEmpty()) {
                Log.w(TAG, "Gemini TTS returned no inline audio part.")
                return SynthesisResult.Failure("لم يُرجع محرك Gemini TTS ملفاً صوتياً هذه المرة.")
            }

            val mime = audioPart.mimeType.lowercase()
            val (finalBytes, finalMime, ext) = when {
                mime.contains("mpeg") || mime.contains("mp3") ->
                    Triple(audioPart.inlineData, "audio/mpeg", "mp3")
                mime.contains("wav") ->
                    Triple(audioPart.inlineData, "audio/wav", "wav")
                mime.contains("l16") || mime.contains("pcm") -> {
                    val sampleRate = extractSampleRate(mime) ?: 24000
                    Triple(wrapPcm16AsWav(audioPart.inlineData, sampleRate), "audio/wav", "wav")
                }
                else ->
                    Triple(audioPart.inlineData, audioPart.mimeType, "mp3")
            }

            SynthesisResult.Success(
                SynthesizedAudio(
                    bytes = finalBytes,
                    mimeType = finalMime,
                    fileExtension = ext,
                    engineUsed = "Gemini TTS (${persona.displayNameEn})"
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini TTS timed out", e)
            SynthesisResult.Failure("انتهت مهلة توليد الصوت عبر Gemini TTS.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini TTS failed", e)
            SynthesisResult.Failure(e.localizedMessage ?: "تعذّر توليد الصوت عبر Gemini TTS.")
        }
    }

    private fun extractSampleRate(mimeType: String): Int? {
        val match = Regex("rate=(\\d+)").find(mimeType) ?: return null
        return match.groupValues.getOrNull(1)?.toIntOrNull()
    }

    private fun wrapPcm16AsWav(pcmData: ByteArray, sampleRate: Int, channels: Int = 1): ByteArray {
        val bitsPerSample = 16
        val byteRate = sampleRate * channels * (bitsPerSample / 8)
        val blockAlign = (channels * (bitsPerSample / 8)).toShort()
        val dataSize = pcmData.size
        val chunkSize = 36 + dataSize

        val out = ByteArrayOutputStream(44 + dataSize)
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray(Charsets.US_ASCII))
            putInt(chunkSize)
            put("WAVE".toByteArray(Charsets.US_ASCII))
            put("fmt ".toByteArray(Charsets.US_ASCII))
            putInt(16) // PCM subchunk size
            putShort(1) // PCM format
            putShort(channels.toShort())
            putInt(sampleRate)
            putInt(byteRate)
            putShort(blockAlign)
            putShort(bitsPerSample.toShort())
            put("data".toByteArray(Charsets.US_ASCII))
            putInt(dataSize)
        }
        out.write(header.array())
        out.write(pcmData)
        return out.toByteArray()
    }

    companion object {
        private const val TAG = "CloudVoiceSynthesizer"
        private const val GEMINI_TTS_MODEL = "gemini-3.1-flash-tts-preview"
    }
}
