package com.example.data

enum class DifficultyLevel(
    val id: Int,
    val emoji: String,
    val titleAr: String,
    val shortNameAr: String,
    val descriptionAr: String,
    val questionCount: Int
) {
    BEGINNER(
        id = 1,
        emoji = "🌱",
        titleAr = "المستوى الأول: المبتدئ",
        shortNameAr = "المبتدئ",
        descriptionAr = "صور وكلمات واستماع ومطابقة",
        questionCount = 6
    ),
    LEARNER(
        id = 2,
        emoji = "⭐",
        titleAr = "المستوى الثاني: المتعلم",
        shortNameAr = "المتعلم",
        descriptionAr = "ترتيب الحروف وإكمال الجمل والقواعد",
        questionCount = 8
    ),
    GENIUS(
        id = 3,
        emoji = "🏆",
        titleAr = "المستوى الثالث: العبقري",
        shortNameAr = "العبقري",
        descriptionAr = "ترتيب الجمل والمحادثة والترجمة والاستماع",
        questionCount = 10
    );

    companion object {
        fun fromId(id: Int): DifficultyLevel =
            entries.firstOrNull { it.id == id } ?: BEGINNER
    }
}

data class BookWord(
    val english: String,
    val arabic: String,
    val emoji: String,
    val unitNumber: Int,
    val categoryAr: String,
    val exampleSentence: String,
    val isInBookDictionary: Boolean = false
)

data class GrammarRule(
    val titleEn: String,
    val titleAr: String,
    val explanationAr: String,
    val examples: List<Pair<String, String>>
)

data class BookLesson(
    val unitNumber: Int,
    val lessonNumber: Int,
    val titleEn: String,
    val titleAr: String,
    val pageNumber: Int,
    val summaryAr: String,
    val keySentences: List<Pair<String, String>>,
    val readingTitle: String? = null,
    val readingTextEn: String? = null,
    val grammarRule: GrammarRule? = null
)

data class ListeningTrack(
    val trackNumber: Int,
    val unitNumber: Int,
    val titleEn: String,
    val titleAr: String,
    val scriptEn: String,
    val summaryAr: String,
    val questionEn: String,
    val options: List<String>,
    val correctAnswer: String
)

data class FillBlankItem(
    val id: String,
    val unitNumber: Int,
    val sentenceWithBlank: String,
    val arabicHint: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanationAr: String
)

data class SentenceOrderItem(
    val id: String,
    val unitNumber: Int,
    val correctSentence: String,
    val arabicTranslation: String
)

data class DialogueExercise(
    val id: String,
    val unitNumber: Int,
    val speakerName: String,
    val promptEn: String,
    val promptAr: String,
    val options: List<String>,
    val correctReply: String,
    val explanationAr: String
)

data class ReadingQuestion(
    val id: String,
    val unitNumber: Int,
    val sourceTitle: String,
    val questionEn: String,
    val questionAr: String,
    val options: List<String>,
    val correctAnswer: String,
    val hintAr: String
)

data class BookUnit(
    val number: Int,
    val titleEn: String,
    val titleAr: String,
    val icon: String,
    val startPage: Int,
    val isRevision: Boolean = false,
    val lessons: List<BookLesson>,
    val fillBlanks: List<FillBlankItem>,
    val sentenceOrders: List<SentenceOrderItem>,
    val dialogues: List<DialogueExercise>,
    val readingQuestions: List<ReadingQuestion>,
    val listeningTracks: List<ListeningTrack>
)

data class UnlockableCompanion(
    val id: String,
    val nameAr: String,
    val titleAr: String,
    val emoji: String,
    val requiredStars: Int,
    val greetingAr: String
)

sealed interface GameQuestion {
    val id: String
    val unitNumber: Int
    val gameBadgeAr: String
    val linaInstructionAr: String
    val hintAr: String
    val speakableEnglish: String?

    data class MultipleChoice(
        override val id: String,
        override val unitNumber: Int,
        override val gameBadgeAr: String,
        override val linaInstructionAr: String,
        override val hintAr: String,
        override val speakableEnglish: String?,
        val promptEmoji: String? = null,
        val promptMainText: String,
        val promptSubText: String? = null,
        val isListeningOnly: Boolean = false,
        val options: List<ChoiceOption>,
        val correctOptionId: String
    ) : GameQuestion

    data class PairMatching(
        override val id: String,
        override val unitNumber: Int,
        override val gameBadgeAr: String,
        override val linaInstructionAr: String,
        override val hintAr: String,
        override val speakableEnglish: String? = null,
        val pairs: List<BookWord>
    ) : GameQuestion

    data class LetterScramble(
        override val id: String,
        override val unitNumber: Int,
        override val gameBadgeAr: String,
        override val linaInstructionAr: String,
        override val hintAr: String,
        override val speakableEnglish: String?,
        val targetWord: BookWord,
        val scrambledLetters: List<Char>
    ) : GameQuestion

    data class SentenceBuilder(
        override val id: String,
        override val unitNumber: Int,
        override val gameBadgeAr: String,
        override val linaInstructionAr: String,
        override val hintAr: String,
        override val speakableEnglish: String?,
        val correctTokens: List<String>,
        val shuffledTokens: List<String>,
        val arabicTranslation: String
    ) : GameQuestion
}

data class ChoiceOption(
    val id: String,
    val label: String,
    val emoji: String? = null,
    val isEnglish: Boolean = false
)
