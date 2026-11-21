package com.example.audio

import com.example.data.BookUnit
import com.example.data.CurriculumRepository

object CurriculumAudioCatalog {

    val allItems: List<CurriculumAudioItem> by lazy {
        (1..8).flatMap { unitNum -> buildItemsForUnit(unitNum) }
    }

    val itemsByUnit: Map<Int, List<CurriculumAudioItem>> by lazy {
        (1..8).associateWith { unitNum -> buildItemsForUnit(unitNum) }
    }

    val allDialogues: List<DialogueScript> by lazy {
        buildAllDialogueScripts()
    }

    fun itemsForUnit(unitNumber: Int): List<CurriculumAudioItem> =
        itemsByUnit[unitNumber].orEmpty()

    fun dialoguesForUnit(unitNumber: Int): List<DialogueScript> =
        if (unitNumber == 0) allDialogues else allDialogues.filter { it.unitNumber == unitNumber }

    private fun buildItemsForUnit(unitNumber: Int): List<CurriculumAudioItem> {
        val unit: BookUnit = CurriculumRepository.unitByNumber(unitNumber)
        val list = mutableListOf<CurriculumAudioItem>()
        val seenTexts = mutableSetOf<String>()

        fun addUnique(
            lessonNumber: Int,
            category: AudioContentCategory,
            titleAr: String,
            textEn: String,
            preferredPersona: VoicePersona? = null,
            preferExpressiveGemini: Boolean = false
        ) {
            val clean = textEn.trim()
            if (clean.isEmpty()) return
            val normKey = "${category.id}|${preferredPersona?.id ?: "default"}|${clean.lowercase()}"
            if (seenTexts.add(normKey)) {
                list += CurriculumAudioItem(
                    unitNumber = unitNumber,
                    lessonNumber = lessonNumber,
                    category = category,
                    titleAr = titleAr,
                    textEn = clean,
                    preferredPersona = preferredPersona,
                    preferExpressiveGemini = preferExpressiveGemini
                )
            }
        }

        // 1. Words & example sentences belonging directly to this unit
        val directWords = CurriculumRepository.dictionary.filter { it.unitNumber == unitNumber }
        directWords.forEach { w ->
            addUnique(
                lessonNumber = 1,
                category = AudioContentCategory.WORD,
                titleAr = "${w.arabic} (${w.english})",
                textEn = w.english
            )
            if (w.exampleSentence.isNotBlank()) {
                addUnique(
                    lessonNumber = 1,
                    category = AudioContentCategory.SENTENCE,
                    titleAr = "مثال: ${w.english}",
                    textEn = w.exampleSentence
                )
            }
        }

        // 2. Lessons: key sentences, grammar rules & examples, and stories/rhymes
        unit.lessons.forEach { lesson ->
            lesson.keySentences.forEach { (en, ar) ->
                addUnique(
                    lessonNumber = lesson.lessonNumber,
                    category = AudioContentCategory.SENTENCE,
                    titleAr = ar,
                    textEn = en
                )
            }
            lesson.grammarRule?.let { grammar ->
                grammar.examples.forEach { (en, ar) ->
                    addUnique(
                        lessonNumber = lesson.lessonNumber,
                        category = AudioContentCategory.GRAMMAR,
                        titleAr = "${grammar.titleAr}: $ar",
                        textEn = en,
                        preferredPersona = VoicePersona.MISS_LILY
                    )
                }
            }
            if (!lesson.readingTextEn.isNullOrBlank()) {
                addUnique(
                    lessonNumber = lesson.lessonNumber,
                    category = AudioContentCategory.STORY,
                    titleAr = lesson.readingTitle ?: lesson.titleAr,
                    textEn = lesson.readingTextEn,
                    preferredPersona = VoicePersona.MISS_EMMA,
                    preferExpressiveGemini = true
                )
            }
        }

        // 3. Dialogues (multi-speaker turns)
        unit.dialogues.forEach { dlg ->
            val speakerPersona = assignPersonaForSpeaker(dlg.speakerName, isReply = false)
            val replyPersona = assignPersonaForSpeaker("Student", isReply = true)
            addUnique(
                lessonNumber = 3,
                category = AudioContentCategory.DIALOGUE,
                titleAr = "محادثة: ${dlg.speakerName}",
                textEn = dlg.promptEn,
                preferredPersona = speakerPersona,
                preferExpressiveGemini = true
            )
            addUnique(
                lessonNumber = 3,
                category = AudioContentCategory.DIALOGUE,
                titleAr = "رد المحادثة: ${dlg.speakerName}",
                textEn = dlg.correctReply,
                preferredPersona = replyPersona,
                preferExpressiveGemini = true
            )
        }

        // 4. Fill-in-the-blank & reading comprehension exercises/exams
        unit.fillBlanks.forEach { fb ->
            val completedSentence = fb.sentenceWithBlank.replace("___", fb.correctAnswer)
            addUnique(
                lessonNumber = 4,
                category = AudioContentCategory.EXERCISE,
                titleAr = "تدريب قواعد: ${fb.arabicHint}",
                textEn = completedSentence
            )
        }
        unit.readingQuestions.forEach { rq ->
            addUnique(
                lessonNumber = 5,
                category = AudioContentCategory.EXERCISE,
                titleAr = "سؤال: ${rq.sourceTitle}",
                textEn = rq.questionEn
            )
        }

        // 5. Listening Tracks
        unit.listeningTracks.forEach { track ->
            addUnique(
                lessonNumber = 6,
                category = AudioContentCategory.LISTENING_TRACK,
                titleAr = "Track ${track.trackNumber}: ${track.titleAr}",
                textEn = track.scriptEn,
                preferredPersona = VoicePersona.MISS_EMMA,
                preferExpressiveGemini = true
            )
        }

        return list
    }

    private fun buildAllDialogueScripts(): List<DialogueScript> {
        val scripts = mutableListOf<DialogueScript>()

        CurriculumRepository.units.forEach { unit ->
            // Build multi-turn classroom dialogues from each unit's dialogue exercises
            if (unit.dialogues.isNotEmpty()) {
                val turns = mutableListOf<DialogueTurn>()
                unit.dialogues.forEachIndexed { index, dlg ->
                    val askerPersona = assignPersonaForSpeaker(dlg.speakerName, isReply = false)
                    val responderName = if (index % 2 == 0) "Dima" else "Karam"
                    val responderPersona = if (index % 2 == 0) VoicePersona.KIDS_VOICE else VoicePersona.MR_JOHN
                    turns += DialogueTurn(
                        speakerName = dlg.speakerName,
                        persona = askerPersona,
                        textEn = dlg.promptEn,
                        arabicNote = dlg.promptAr
                    )
                    turns += DialogueTurn(
                        speakerName = responderName,
                        persona = responderPersona,
                        textEn = dlg.correctReply,
                        arabicNote = dlg.explanationAr
                    )
                }
                scripts += DialogueScript(
                    id = "unit_${unit.number}_dialogue",
                    unitNumber = unit.number,
                    titleAr = "محادثات الوحدة ${unit.number}: ${unit.titleAr}",
                    titleEn = "Unit ${unit.number} Dialogue — ${unit.titleEn}",
                    turns = turns
                )
            }

            // Build expressive story narration scripts from reading lessons
            unit.lessons.filter { !it.readingTextEn.isNullOrBlank() }.forEach { lesson ->
                val raw = lesson.readingTextEn.orEmpty()
                val sentences = raw.split(Regex("(?<=[.!?])\\s+"))
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                if (sentences.isNotEmpty()) {
                    val turns = sentences.mapIndexed { idx, line ->
                        DialogueTurn(
                            speakerName = if (idx == 0) "Miss Emma (الراوية)" else "Miss Emma",
                            persona = VoicePersona.MISS_EMMA,
                            textEn = line,
                            arabicNote = if (idx == 0) lesson.summaryAr else ""
                        )
                    }
                    scripts += DialogueScript(
                        id = "unit_${unit.number}_story_${lesson.lessonNumber}",
                        unitNumber = unit.number,
                        titleAr = "قصة الوحدة ${unit.number}: ${lesson.readingTitle ?: lesson.titleAr}",
                        titleEn = lesson.readingTitle ?: lesson.titleEn,
                        turns = turns
                    )
                }
            }
        }

        return scripts
    }

    fun assignPersonaForSpeaker(speakerName: String, isReply: Boolean): VoicePersona {
        val lower = speakerName.lowercase()
        return when {
            lower.contains("teacher") || lower.contains("lily") || lower.contains("mum") || lower.contains("mother") ->
                VoicePersona.MISS_LILY
            lower.contains("emma") || lower.contains("narrator") || lower.contains("dima") || lower.contains("jenny") ->
                VoicePersona.MISS_EMMA
            lower.contains("john") || lower.contains("dad") || lower.contains("father") || lower.contains("waiter") ||
                lower.contains("murad") || lower.contains("firas") || lower.contains("sam") || lower.contains("karam") ->
                VoicePersona.MR_JOHN
            isReply -> VoicePersona.KIDS_VOICE
            else -> VoicePersona.KIDS_VOICE
        }
    }
}
