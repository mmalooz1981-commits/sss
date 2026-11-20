package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.min

data class PronunciationResult(
    val targetText: String,
    val recognizedText: String,
    val accuracyPercent: Int,
    val feedbackAr: String,
    val isSuccess: Boolean
)

class PronunciationHelper(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _errorMessageAr = MutableStateFlow<String?>(null)
    val errorMessageAr: StateFlow<String?> = _errorMessageAr.asStateFlow()

    fun isAvailable(): Boolean =
        SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening(
        targetText: String,
        onResult: (PronunciationResult) -> Unit
    ) {
        _errorMessageAr.value = null
        if (!isAvailable()) {
            _errorMessageAr.value = "خدمة التعرف الصوتي غير متوفرة على هذا الجهاز، يمكنك الاستماع والتدرب بصوتك مع المعلمة لينا!"
            return
        }

        stopListening()
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) = Unit
                    override fun onBufferReceived(buffer: ByteArray?) = Unit

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _errorMessageAr.value = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH,
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                                "لم أسمع الصوت بوضوح، اقترب من الميكروفون وحاول مرة أخرى!"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                                "يرجى السماح بصلاحية الميكروفون للتدرب على النطق."
                            else ->
                                "تعذّر التقاط الصوت حالياً، استمع للنطق وحاول مجدداً!"
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (matches.isNullOrEmpty()) {
                            _errorMessageAr.value = "لم أسمع الكلمة بوضوح، حاول مجدداً!"
                            return
                        }
                        val evaluated = evaluateBestMatch(targetText, matches)
                        onResult(evaluated)
                    }

                    override fun onPartialResults(partialResults: Bundle?) = Unit
                    override fun onEvent(eventType: Int, params: Bundle?) = Unit
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            }
            speechRecognizer?.startListening(intent)
        } catch (_: Exception) {
            _isListening.value = false
            _errorMessageAr.value = "تعذّر تشغيل الميكروفون على هذا الجهاز."
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (_: Exception) {
        } finally {
            speechRecognizer = null
            _isListening.value = false
        }
    }

    private fun evaluateBestMatch(target: String, candidates: List<String>): PronunciationResult {
        val cleanTarget = normalize(target)
        var bestCandidate = candidates.first()
        var bestScore = 0

        for (candidate in candidates) {
            val score = similarityPercent(cleanTarget, normalize(candidate))
            if (score > bestScore) {
                bestScore = score
                bestCandidate = candidate
            }
        }

        val isSuccess = bestScore >= 70
        val feedback = when {
            bestScore >= 90 -> "نطق ممتاز ورائع جداً يا بطل! 🌟"
            bestScore >= 70 -> "أحسنت! نطقك قريب جداً وصحيح! 👏"
            bestScore >= 45 -> "محاولة جميلة! استمع إلى المعلمة لينا وجرب مرة أخرى 🌸"
            else -> "لا بأس يا بطل، استمع للكلمة ببطء وكررها معي 🎧"
        }

        return PronunciationResult(
            targetText = target,
            recognizedText = bestCandidate,
            accuracyPercent = bestScore,
            feedbackAr = feedback,
            isSuccess = isSuccess
        )
    }

    private fun normalize(input: String): String =
        input.lowercase().replace(Regex("[^a-z0-9\\s]"), "").replace(Regex("\\s+"), " ").trim()

    private fun similarityPercent(a: String, b: String): Int {
        if (a.isEmpty() && b.isEmpty()) return 100
        if (a.isEmpty() || b.isEmpty()) return 0
        val distance = levenshtein(a, b)
        val maxLen = max(a.length, b.length)
        return (((maxLen - distance).toFloat() / maxLen.toFloat()) * 100f).toInt().coerceIn(0, 100)
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = IntArray(b.length + 1) { it }
        for (i in 1..a.length) {
            var prev = dp[0]
            dp[0] = i
            for (j in 1..b.length) {
                val temp = dp[j]
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[j] = min(min(dp[j] + 1, dp[j - 1] + 1), prev + cost)
                prev = temp
            }
        }
        return dp[b.length]
    }
}
