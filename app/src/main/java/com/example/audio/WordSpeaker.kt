package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class WordSpeaker(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady: Boolean = false
    private var arabicSupported: Boolean = false
    private var onUtteranceDone: (() -> Unit)? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
            isReady = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ukResult = tts?.setLanguage(Locale.UK)
            isReady = ukResult != TextToSpeech.LANG_MISSING_DATA &&
                ukResult != TextToSpeech.LANG_NOT_SUPPORTED
            if (!isReady) {
                val usResult = tts?.setLanguage(Locale.US)
                isReady = usResult != TextToSpeech.LANG_MISSING_DATA &&
                    usResult != TextToSpeech.LANG_NOT_SUPPORTED
            }
            val arResult = tts?.isLanguageAvailable(Locale.forLanguageTag("ar"))
            arabicSupported = arResult != null &&
                arResult >= TextToSpeech.LANG_AVAILABLE
            tts?.setSpeechRate(0.90f)
            tts?.setPitch(1.04f)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    onUtteranceDone?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    onUtteranceDone?.invoke()
                }
            })
        } else {
            isReady = false
        }
    }

    fun speakEnglish(text: String) {
        speakEnglishWithProfile(
            text = text,
            accent = VoiceAccent.EN_GB,
            persona = VoicePersona.MISS_LILY,
            speechMode = SpeechMode.NORMAL
        )
    }

    fun speakEnglishWithProfile(
        text: String,
        accent: VoiceAccent,
        persona: VoicePersona,
        speechMode: SpeechMode,
        onComplete: (() -> Unit)? = null
    ) {
        if (!isReady || text.isBlank()) {
            onComplete?.invoke()
            return
        }
        try {
            onUtteranceDone = onComplete
            val avail = tts?.isLanguageAvailable(accent.locale) ?: TextToSpeech.LANG_NOT_SUPPORTED
            tts?.language = if (avail >= TextToSpeech.LANG_AVAILABLE) accent.locale else Locale.US
            tts?.setSpeechRate(speechMode.fallbackTtsRate)
            tts?.setPitch(persona.fallbackPitch)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "en_${System.nanoTime()}")
        } catch (_: Exception) {
            _isSpeaking.value = false
            onComplete?.invoke()
        }
    }

    fun speakArabic(text: String) {
        if (!isReady || !arabicSupported || text.isBlank()) return
        try {
            onUtteranceDone = null
            tts?.language = Locale.forLanguageTag("ar")
            tts?.setSpeechRate(0.95f)
            tts?.setPitch(1.05f)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ar_${text.hashCode()}")
        } catch (_: Exception) {
            _isSpeaking.value = false
        }
    }

    fun stop() {
        try {
            onUtteranceDone = null
            tts?.stop()
        } catch (_: Exception) {
            // Ignore audio stop errors
        } finally {
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        try {
            onUtteranceDone = null
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
            // Ignore shutdown errors
        } finally {
            tts = null
            isReady = false
            _isSpeaking.value = false
        }
    }
}

class SoundEffects {
    private val toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 65)
    } catch (_: Exception) {
        null
    }

    fun playCorrect() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 160)
        } catch (_: Exception) {
        }
    }

    fun playWrong() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
        } catch (_: Exception) {
        }
    }

    fun playCelebration() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 300)
        } catch (_: Exception) {
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
        } catch (_: Exception) {
        }
    }
}
