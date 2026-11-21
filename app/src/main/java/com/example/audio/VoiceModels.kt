package com.example.audio

import java.security.MessageDigest
import java.util.Locale

enum class VoiceAccent(
    val code: String,
    val labelAr: String,
    val flagEmoji: String,
    val locale: Locale
) {
    EN_GB(
        code = "en-GB",
        labelAr = "الإنجليزية البريطانية (الأساسية)",
        flagEmoji = "🇬🇧",
        locale = Locale.UK
    ),
    EN_US(
        code = "en-US",
        labelAr = "الإنجليزية الأمريكية",
        flagEmoji = "🇺🇸",
        locale = Locale.US
    );

    companion object {
        fun fromCode(code: String?): VoiceAccent =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: EN_GB
    }
}

enum class VoicePersona(
    val id: String,
    val displayNameEn: String,
    val titleAr: String,
    val roleDescriptionAr: String,
    val emoji: String,
    val chirpGbVoice: String,
    val chirpUsVoice: String,
    val geminiVoiceName: String,
    val stylePromptEn: String,
    val fallbackPitch: Float
) {
    MISS_LILY(
        id = "miss_lily",
        displayNameEn = "Miss Lily",
        titleAr = "معلمة الصف",
        roleDescriptionAr = "صوت أنثوي واضح وهادئ للشرح والمفردات",
        emoji = "👩‍🏫",
        chirpGbVoice = "en-GB-Chirp3-HD-Aoede",
        chirpUsVoice = "en-US-Chirp3-HD-Aoede",
        geminiVoiceName = "Aoede",
        stylePromptEn = "Speak warmly, clearly, and calmly like a friendly primary school English teacher.",
        fallbackPitch = 1.06f
    ),
    MISS_EMMA(
        id = "miss_emma",
        displayNameEn = "Miss Emma",
        titleAr = "راوية القصص",
        roleDescriptionAr = "صوت أنثوي تعبيري دافئ لقراءة القصص والمجلات",
        emoji = "📖",
        chirpGbVoice = "en-GB-Chirp3-HD-Kore",
        chirpUsVoice = "en-US-Chirp3-HD-Kore",
        geminiVoiceName = "Kore",
        stylePromptEn = "Read expressively with gentle storytelling warmth suitable for 5th primary students.",
        fallbackPitch = 1.10f
    ),
    MR_JOHN(
        id = "mr_john",
        displayNameEn = "Mr. John",
        titleAr = "محاور الصف",
        roleDescriptionAr = "صوت ذكوري واضح ومتزن للمحادثات والأمثلة",
        emoji = "👨‍🏫",
        chirpGbVoice = "en-GB-Chirp3-HD-Charon",
        chirpUsVoice = "en-US-Chirp3-HD-Charon",
        geminiVoiceName = "Charon",
        stylePromptEn = "Speak clearly in a friendly, articulate male voice suitable for classroom dialogues.",
        fallbackPitch = 0.84f
    ),
    KIDS_VOICE(
        id = "kids_voice",
        displayNameEn = "Kids Voice",
        titleAr = "صوت التلاميذ",
        roleDescriptionAr = "صوت طفولي مرح للمحادثات والأنشطة التفاعلية",
        emoji = "🧒",
        chirpGbVoice = "en-GB-Chirp3-HD-Puck",
        chirpUsVoice = "en-US-Chirp3-HD-Puck",
        geminiVoiceName = "Puck",
        stylePromptEn = "Speak with a cheerful, bright young student tone for children's dialogues and games.",
        fallbackPitch = 1.24f
    );

    fun chirpVoiceFor(accent: VoiceAccent): String =
        if (accent == VoiceAccent.EN_GB) chirpGbVoice else chirpUsVoice

    companion object {
        fun fromId(id: String?): VoicePersona =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MISS_LILY
    }
}

enum class SpeechMode(
    val id: String,
    val labelAr: String,
    val descriptionAr: String,
    val emoji: String,
    val playbackSpeed: Float,
    val cloudSpeakingRate: Double,
    val fallbackTtsRate: Float,
    val repeatPauseMillis: Long?
) {
    SLOW(
        id = "slow",
        labelAr = "Slow (بطيء)",
        descriptionAr = "نطق بطيء وواضح لكل مقطع صوتي",
        emoji = "🐢",
        playbackSpeed = 0.78f,
        cloudSpeakingRate = 0.78,
        fallbackTtsRate = 0.70f,
        repeatPauseMillis = null
    ),
    NORMAL(
        id = "normal",
        labelAr = "Normal (طبيعي)",
        descriptionAr = "نطق طبيعي متوازن للصف الخامس",
        emoji = "🎧",
        playbackSpeed = 1.0f,
        cloudSpeakingRate = 0.96,
        fallbackTtsRate = 0.90f,
        repeatPauseMillis = null
    ),
    PRACTICE(
        id = "practice",
        labelAr = "Practice (ترديد)",
        descriptionAr = "ينطق الكلمة، ينتظر ثانيتين لترددها، ثم يعيدها",
        emoji = "🔁",
        playbackSpeed = 0.90f,
        cloudSpeakingRate = 0.86,
        fallbackTtsRate = 0.82f,
        repeatPauseMillis = 2000L
    );

    companion object {
        fun fromId(id: String?): SpeechMode =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: NORMAL
    }
}

enum class VoiceEnginePreference(
    val id: String,
    val labelAr: String,
    val descriptionAr: String
) {
    HYBRID_AUTO(
        id = "hybrid_auto",
        labelAr = "هجين ذكي (Chirp 3 HD + Gemini TTS)",
        descriptionAr = "يستخدم Chirp 3 HD للدروس والمفردات وGemini TTS للقصص والمحادثات التعبيرية"
    ),
    CHIRP3_HD(
        id = "chirp3_hd",
        labelAr = "Google Cloud Chirp 3 HD",
        descriptionAr = "أصوات تعليمية عالية الوضوح عبر الخادم الآمن"
    ),
    GEMINI_TTS(
        id = "gemini_tts",
        labelAr = "Gemini TTS التعبيري",
        descriptionAr = "توليد صوتي تعبيري مباشر عبر Firebase AI Logic"
    );

    companion object {
        fun fromId(id: String?): VoiceEnginePreference =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: HYBRID_AUTO
    }
}

enum class AudioContentCategory(val id: String, val labelAr: String) {
    WORD("word", "مفردات"),
    SENTENCE("sentence", "جمل"),
    GRAMMAR("grammar", "قواعد وأمثلة"),
    STORY("story", "قصص ومجلات"),
    DIALOGUE("dialogue", "محادثات"),
    LISTENING_TRACK("listening_track", "نصوص الاستماع"),
    EXERCISE("exercise", "تدريبات واختبارات")
}

data class CurriculumAudioItem(
    val unitNumber: Int,
    val lessonNumber: Int,
    val category: AudioContentCategory,
    val titleAr: String,
    val textEn: String,
    val preferredPersona: VoicePersona? = null,
    val preferExpressiveGemini: Boolean = false
)

data class DialogueTurn(
    val speakerName: String,
    val persona: VoicePersona,
    val textEn: String,
    val arabicNote: String = ""
)

data class DialogueScript(
    val id: String,
    val unitNumber: Int,
    val titleAr: String,
    val titleEn: String,
    val turns: List<DialogueTurn>
)

data class UnitAudioStats(
    val unitNumber: Int,
    val totalItems: Int,
    val downloadedItems: Int,
    val totalSizeBytes: Long
) {
    val completionPercent: Int
        get() = if (totalItems <= 0) 0 else ((downloadedItems * 100) / totalItems).coerceIn(0, 100)
}

data class AudioPlaybackUiState(
    val isPlaying: Boolean = false,
    val isLoadingCloud: Boolean = false,
    val activeText: String? = null,
    val activeSourceLabelAr: String? = null,
    val practicePhaseLabelAr: String? = null,
    val activeDialogueId: String? = null,
    val activeDialogueTurnIndex: Int = -1,
    val noticeMessageAr: String? = null,
    val isOfflineMissingNotice: Boolean = false,
    val isRecordingStudent: Boolean = false,
    val hasStudentRecording: Boolean = false,
    val isPlayingStudentRecording: Boolean = false
)

object AudioKeyHasher {
    fun buildCacheKey(
        textEn: String,
        accent: VoiceAccent,
        persona: VoicePersona,
        engineTag: String
    ): String {
        val normalized = textEn.trim().replace(Regex("\\s+"), " ")
        val raw = "${accent.code}|${persona.id}|$engineTag|$normalized"
        return sha256Hex(raw)
    }

    fun sha256Hex(input: String): String = sha256Hex(input.toByteArray(Charsets.UTF_8))

    fun sha256Hex(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
