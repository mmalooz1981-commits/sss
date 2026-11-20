package com.example.data

object CurriculumRepository {

    val units: List<BookUnit> = CurriculumUnits1to4.all + CurriculumUnits5to8.all

    val dictionary: List<BookWord> = DictionaryData.allWords

    val allListeningTracks: List<ListeningTrack> = units.flatMap { it.listeningTracks }

    val companions: List<UnlockableCompanion> = listOf(
        UnlockableCompanion(
            id = "lina",
            nameAr = "المعلمة لينا",
            titleAr = "معلمتك المفضلة",
            emoji = "👩‍🏫",
            requiredStars = 0,
            greetingAr = "أهلاً بك يا بطل! هيا نتعلم الإنجليزية معاً!"
        ),
        UnlockableCompanion(
            id = "dolphin",
            nameAr = "الدلفين الذكي",
            titleAr = "صديق الوحدة الثانية",
            emoji = "🐬",
            requiredStars = 50,
            greetingAr = "رائع! أنا الدلفين الذكي، أسبح معك نحو التفوق!"
        ),
        UnlockableCompanion(
            id = "butterfly",
            nameAr = "الفراشة الملونة",
            titleAr = "بطلة الطبيعة",
            emoji = "🦋",
            requiredStars = 120,
            greetingAr = "ممتاز! حلق معي في سماء الكلمات الإنجليزية!"
        ),
        UnlockableCompanion(
            id = "eagle",
            nameAr = "النسر القوي",
            titleAr = "بطل قصة المسابقة",
            emoji = "🦅",
            requiredStars = 220,
            greetingAr = "يا لك من بطل ذكي! أنت تتقدم بسرعة مذهلة!"
        ),
        UnlockableCompanion(
            id = "lion",
            nameAr = "الملك الأسد",
            titleAr = "حامل كأس الأبطال",
            emoji = "🦁",
            requiredStars = 350,
            greetingAr = "أحسنت يا عبقري الصف الخامس! أنت الفائز دائماً!"
        )
    )

    fun unitByNumber(unitNumber: Int): BookUnit =
        units.firstOrNull { it.number == unitNumber } ?: units.first()

    fun wordsForUnit(unitNumber: Int): List<BookWord> {
        return when (unitNumber) {
            4 -> dictionary.filter { it.unitNumber in 1..4 }
            8 -> dictionary
            else -> dictionary.filter { it.unitNumber == unitNumber }
        }
    }

    fun fillBlanksForUnit(unitNumber: Int): List<FillBlankItem> {
        return when (unitNumber) {
            4 -> units.filter { it.number in 1..4 }.flatMap { it.fillBlanks }
            8 -> units.flatMap { it.fillBlanks }
            else -> unitByNumber(unitNumber).fillBlanks
        }
    }

    fun sentenceOrdersForUnit(unitNumber: Int): List<SentenceOrderItem> {
        return when (unitNumber) {
            4 -> units.filter { it.number in 1..4 }.flatMap { it.sentenceOrders }
            8 -> units.flatMap { it.sentenceOrders }
            else -> unitByNumber(unitNumber).sentenceOrders
        }
    }

    fun dialoguesForUnit(unitNumber: Int): List<DialogueExercise> {
        return when (unitNumber) {
            4 -> units.filter { it.number in 1..4 }.flatMap { it.dialogues }
            8 -> units.flatMap { it.dialogues }
            else -> unitByNumber(unitNumber).dialogues
        }
    }

    fun readingQuestionsForUnit(unitNumber: Int): List<ReadingQuestion> {
        return when (unitNumber) {
            4 -> units.filter { it.number in 1..4 }.flatMap { it.readingQuestions }
            8 -> units.flatMap { it.readingQuestions }
            else -> unitByNumber(unitNumber).readingQuestions
        }
    }

    fun generateGameSession(
        unitNumber: Int,
        level: DifficultyLevel,
        recentQuestionIds: Set<String>
    ): List<GameQuestion> {
        val candidates = when (level) {
            DifficultyLevel.BEGINNER -> buildLevel1Pool(unitNumber)
            DifficultyLevel.LEARNER -> buildLevel2Pool(unitNumber)
            DifficultyLevel.GENIUS -> buildLevel3Pool(unitNumber)
        }

        val fresh = candidates.filterNot { it.id in recentQuestionIds }.shuffled()
        val fallback = candidates.filter { it.id in recentQuestionIds }.shuffled()
        return (fresh + fallback).distinctBy { it.id }.take(level.questionCount)
    }

    fun generateUnitExam(unitNumber: Int, recentQuestionIds: Set<String>): List<GameQuestion> {
        val combined = (
            buildLevel1Pool(unitNumber).shuffled().take(3) +
                buildLevel2Pool(unitNumber).shuffled().take(4) +
                buildLevel3Pool(unitNumber).shuffled().take(4)
            ).distinctBy { it.id }

        val fresh = combined.filterNot { it.id in recentQuestionIds }.shuffled()
        val fallback = combined.filter { it.id in recentQuestionIds }.shuffled()
        return (fresh + fallback).distinctBy { it.id }.take(10)
    }

    fun generateComprehensiveBookExam(recentQuestionIds: Set<String>): List<GameQuestion> {
        val allPool = mutableListOf<GameQuestion>()
        for (u in 1..8) {
            allPool += buildLevel1Pool(u).shuffled().take(1)
            allPool += buildLevel2Pool(u).shuffled().take(2)
            allPool += buildLevel3Pool(u).shuffled().take(2)
        }
        val fresh = allPool.filterNot { it.id in recentQuestionIds }.shuffled()
        val fallback = allPool.filter { it.id in recentQuestionIds }.shuffled()
        return (fresh + fallback).distinctBy { it.id }.take(16)
    }

    private fun buildLevel1Pool(unitNumber: Int): List<GameQuestion> {
        val words = wordsForUnit(unitNumber)
        if (words.size < 4) return emptyList()
        val result = mutableListOf<GameQuestion>()

        // 1. Word -> Arabic meaning with picture
        words.shuffled().take(6).forEach { target ->
            val opts = (listOf(target) + words.filter { it.english != target.english }.shuffled().take(3)).shuffled()
            result += GameQuestion.MultipleChoice(
                id = "l1_wm_${unitNumber}_${target.english}",
                unitNumber = unitNumber,
                gameBadgeAr = "🎯 اختر المعنى الصحيح",
                linaInstructionAr = "انظر إلى الصورة واستمع للكلمة ثم اختر معناها الصحيح!",
                hintAr = "تلميح من لينا: انظر إلى الرمز (${target.emoji}) واستمع للنطق مرة أخرى.",
                speakableEnglish = target.english,
                promptEmoji = target.emoji,
                promptMainText = target.english,
                promptSubText = target.exampleSentence,
                options = opts.map { ChoiceOption(id = it.english, label = it.arabic) },
                correctOptionId = target.english
            )
        }

        // 2. Listen & Choose (audio-first)
        words.shuffled().take(4).forEach { target ->
            val opts = (listOf(target) + words.filter { it.english != target.english }.shuffled().take(3)).shuffled()
            result += GameQuestion.MultipleChoice(
                id = "l1_lc_${unitNumber}_${target.english}",
                unitNumber = unitNumber,
                gameBadgeAr = "🔊 استمع واختر الكلمة",
                linaInstructionAr = "اضغط على زر الصوت واستمع جيداً، ثم اختر الصورة والكلمة التي سمعتها!",
                hintAr = "تلميح من لينا: هذه الكلمة تعني بالعربية «${target.arabic}».",
                speakableEnglish = target.english,
                promptEmoji = "🎧",
                promptMainText = "استمع إلى الكلمة الإنجليزية",
                promptSubText = "اضغط على زر الاستماع لتسمع المعلمة لينا",
                isListeningOnly = true,
                options = opts.map { ChoiceOption(id = it.english, label = it.arabic, emoji = it.emoji) },
                correctOptionId = target.english
            )
        }

        // 3. Picture Match (Arabic/Emoji -> English Word)
        words.shuffled().take(4).forEach { target ->
            val opts = (listOf(target) + words.filter { it.english != target.english }.shuffled().take(3)).shuffled()
            result += GameQuestion.MultipleChoice(
                id = "l1_pm_${unitNumber}_${target.english}",
                unitNumber = unitNumber,
                gameBadgeAr = "🖼️ طابق الصورة بالكلمة",
                linaInstructionAr = "أي كلمة إنجليزية تطابق هذه الصورة والمعنى؟",
                hintAr = "تلميح من لينا: تبدأ الكلمة الإنجليزية بالحرف (${target.english.first().uppercaseChar()}).",
                speakableEnglish = target.english,
                promptEmoji = target.emoji,
                promptMainText = target.arabic,
                promptSubText = "اختر الكلمة الإنجليزية المطابقة",
                options = opts.map { ChoiceOption(id = it.english, label = it.english, isEnglish = true) },
                correctOptionId = target.english
            )
        }

        // 4. Pair Matching (match 4 English <-> Arabic pairs)
        val matchSet = words.shuffled().take(4)
        if (matchSet.size == 4) {
            result += GameQuestion.PairMatching(
                id = "l1_pair_${unitNumber}_${matchSet.joinToString("_") { it.english }}",
                unitNumber = unitNumber,
                gameBadgeAr = "🃏 لعبة المطابقة",
                linaInstructionAr = "اختر الكلمة الإنجليزية ثم اضغط على معناها العربي المطابق!",
                hintAr = "تلميح من لينا: استعن بالصور المرافقة لكل كلمة عربية.",
                pairs = matchSet
            )
        }

        return result
    }

    private fun buildLevel2Pool(unitNumber: Int): List<GameQuestion> {
        val result = mutableListOf<GameQuestion>()
        val words = wordsForUnit(unitNumber)

        // 1. Letter Scramble for clean single words (length 3..9, letters only)
        val scrambleWords = words
            .filter { w -> w.english.length in 3..9 && w.english.all { it.isLetter() } }
            .shuffled()
            .take(4)
        scrambleWords.forEach { word ->
            val chars = word.english.uppercase().toList()
            var shuffled = chars.shuffled()
            if (shuffled == chars && chars.size > 1) {
                shuffled = chars.reversed()
            }
            result += GameQuestion.LetterScramble(
                id = "l2_scr_${unitNumber}_${word.english}",
                unitNumber = unitNumber,
                gameBadgeAr = "🧩 ترتيب الحروف",
                linaInstructionAr = "رتّب الحروف بالترتيب الصحيح لتكوين الكلمة الإنجليزية!",
                hintAr = "تلميح من لينا: تبدأ الكلمة بالحرف «${word.english.first().uppercaseChar()}» وتنتهي بـ «${word.english.last().uppercaseChar()}».",
                speakableEnglish = word.english,
                targetWord = word,
                scrambledLetters = shuffled
            )
        }

        // 2. Grammar & Pronoun Fill-in-the-Blank from the textbook
        fillBlanksForUnit(unitNumber).forEach { fb ->
            result += GameQuestion.MultipleChoice(
                id = "l2_${fb.id}",
                unitNumber = unitNumber,
                gameBadgeAr = "📝 أكمل الفراغ (قواعد الكتاب)",
                linaInstructionAr = "اقرأ الجملة من الكتاب واختر الكلمة أو الضمير المناسب للفراغ!",
                hintAr = "تلميح من لينا: ${fb.explanationAr}",
                speakableEnglish = fb.sentenceWithBlank.replace("___", fb.correctAnswer),
                promptEmoji = "✏️",
                promptMainText = fb.sentenceWithBlank,
                promptSubText = fb.arabicHint,
                options = fb.options.shuffled().map { ChoiceOption(id = it, label = it, isEnglish = true) },
                correctOptionId = fb.correctAnswer
            )
        }

        // 3. Reading & Book Comprehension Questions
        readingQuestionsForUnit(unitNumber).forEach { rq ->
            result += GameQuestion.MultipleChoice(
                id = "l2_${rq.id}",
                unitNumber = unitNumber,
                gameBadgeAr = "📚 أسئلة الكتاب",
                linaInstructionAr = "تذكر ما قرأناه في «${rq.sourceTitle}» واختر الإجابة الصحيحة!",
                hintAr = "تلميح من لينا: ${rq.hintAr}",
                speakableEnglish = rq.questionEn,
                promptEmoji = "📖",
                promptMainText = rq.questionEn,
                promptSubText = rq.questionAr,
                options = rq.options.shuffled().map { ChoiceOption(id = it, label = it, isEnglish = true) },
                correctOptionId = rq.correctAnswer
            )
        }

        return result
    }

    private fun buildLevel3Pool(unitNumber: Int): List<GameQuestion> {
        val result = mutableListOf<GameQuestion>()

        // 1. Sentence Word Ordering
        sentenceOrdersForUnit(unitNumber).forEach { so ->
            val tokens = so.correctSentence.split(" ")
            var shuffled = tokens.shuffled()
            if (shuffled == tokens && tokens.size > 1) {
                shuffled = tokens.reversed()
            }
            result += GameQuestion.SentenceBuilder(
                id = "l3_${so.id}",
                unitNumber = unitNumber,
                gameBadgeAr = "🧱 رتّب الكلمات لتكوين جملة",
                linaInstructionAr = "اضغط على الكلمات بالترتيب الصحيح لتكوين جملة إنجليزية صحيحة!",
                hintAr = "تلميح من لينا: تبدأ الجملة بكلمة «${tokens.first()}».",
                speakableEnglish = so.correctSentence,
                correctTokens = tokens,
                shuffledTokens = shuffled,
                arabicTranslation = so.arabicTranslation
            )
        }

        // 2. Short Book Dialogues / Conversations
        dialoguesForUnit(unitNumber).forEach { dlg ->
            result += GameQuestion.MultipleChoice(
                id = "l3_${dlg.id}",
                unitNumber = unitNumber,
                gameBadgeAr = "💬 محادثة من الكتاب",
                linaInstructionAr = "اقرأ سؤال ${dlg.speakerName} واختر الرد الصحيح المناسب للمحادثة!",
                hintAr = "تلميح من لينا: ${dlg.explanationAr}",
                speakableEnglish = dlg.promptEn,
                promptEmoji = "🗣️",
                promptMainText = "${dlg.speakerName}: \"${dlg.promptEn}\"",
                promptSubText = dlg.promptAr,
                options = dlg.options.shuffled().map { ChoiceOption(id = it, label = it, isEnglish = true) },
                correctOptionId = dlg.correctReply
            )
        }

        // 3. Sentence Translation (English -> Arabic)
        val unitLessons = if (unitNumber == 4) {
            units.filter { it.number in 1..4 }.flatMap { it.lessons }
        } else if (unitNumber == 8) {
            units.flatMap { it.lessons }
        } else {
            unitByNumber(unitNumber).lessons
        }
        val allSentences = unitLessons.flatMap { it.keySentences }
        if (allSentences.size >= 4) {
            allSentences.shuffled().take(4).forEach { (en, ar) ->
                val distractors = allSentences
                    .filter { it.second != ar }
                    .shuffled()
                    .take(3)
                val opts = (listOf(en to ar) + distractors).shuffled()
                result += GameQuestion.MultipleChoice(
                    id = "l3_tr_${unitNumber}_${en.hashCode()}",
                    unitNumber = unitNumber,
                    gameBadgeAr = "🌍 ترجم الجملة",
                    linaInstructionAr = "استمع للجملة الإنجليزية واختر ترجمتها العربية الدقيقة!",
                    hintAr = "تلميح من لينا: استمع للجملة ببطء وانتبه للكلمات الأساسية فيها.",
                    speakableEnglish = en,
                    promptEmoji = "🌍",
                    promptMainText = en,
                    promptSubText = "اختر الترجمة العربية الصحيحة",
                    options = opts.map { ChoiceOption(id = it.second, label = it.second) },
                    correctOptionId = ar
                )
            }
        }

        // 4. Listening Track Comprehension
        val tracks = if (unitNumber == 4) {
            units.filter { it.number in 1..4 }.flatMap { it.listeningTracks }
        } else if (unitNumber == 8) {
            allListeningTracks
        } else {
            unitByNumber(unitNumber).listeningTracks
        }
        tracks.forEach { track ->
            result += GameQuestion.MultipleChoice(
                id = "l3_trk_${track.trackNumber}",
                unitNumber = unitNumber,
                gameBadgeAr = "🎧 استمع وأجب",
                linaInstructionAr = "استمع إلى النص الصوتي من الكتاب ثم أجب عن السؤال!",
                hintAr = "تلميح من لينا: ${track.summaryAr}",
                speakableEnglish = track.scriptEn,
                promptEmoji = "🎧",
                promptMainText = track.questionEn,
                promptSubText = track.titleAr,
                isListeningOnly = true,
                options = track.options.shuffled().map { ChoiceOption(id = it, label = it, isEnglish = true) },
                correctOptionId = track.correctAnswer
            )
        }

        return result
    }
}
