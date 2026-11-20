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
            val enResult = tts?.setLanguage(Locale.US)
            isReady = enResult != TextToSpeech.LANG_MISSING_DATA &&
                enResult != TextToSpeech.LANG_NOT_SUPPORTED
            val arResult = tts?.isLanguageAvailable(Locale.forLanguageTag("ar"))
            arabicSupported = arResult != null &&
                arResult >= TextToSpeech.LANG_AVAILABLE
            tts?.setSpeechRate(0.86f)
            tts?.setPitch(1.06f)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
        } else {
            isReady = false
        }
    }

    fun speakEnglish(text: String) {
        if (!isReady || text.isBlank()) return
        try {
            tts?.language = Locale.US
            tts?.setSpeechRate(0.86f)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "en_${text.hashCode()}")
        } catch (_: Exception) {
            _isSpeaking.value = false
        }
    }

    fun speakArabic(text: String) {
        if (!isReady || !arabicSupported || text.isBlank()) return
        try {
            tts?.language = Locale.forLanguageTag("ar")
            tts?.setSpeechRate(0.95f)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ar_${text.hashCode()}")
        } catch (_: Exception) {
            _isSpeaking.value = false
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {
            // Ignore audio stop errors
        } finally {
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        try {
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
