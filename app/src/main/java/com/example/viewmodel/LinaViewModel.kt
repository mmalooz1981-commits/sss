package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.CurriculumAudioCatalog
import com.example.audio.PronunciationResult
import com.example.audio.SpeechMode
import com.example.audio.UnitAudioStats
import com.example.audio.VoiceAccent
import com.example.audio.VoiceEnginePreference
import com.example.audio.VoicePersona
import com.example.data.BookUnit
import com.example.data.BookWord
import com.example.data.CurriculumRepository
import com.example.data.DifficultyLevel
import com.example.data.GameQuestion
import com.example.data.ListeningTrack
import com.example.data.local.CachedAudioDao
import com.example.data.local.ChildProfileEntity
import com.example.data.local.ExamResultEntity
import com.example.data.local.LinaLocalRepository
import com.example.data.local.UnitProgressEntity
import com.example.data.local.VoiceSettingsEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LinaMood {
    WELCOMING,
    EXPLAINING,
    HAPPY,
    HINTING,
    CELEBRATING
}

sealed interface AppDestination {
    data object Home : AppDestination
    data object BookUnits : AppDestination
    data class UnitDetail(val unitNumber: Int) : AppDestination
    data class GamePlay(val session: GameSessionState) : AppDestination
    data class Flashcards(val session: FlashcardSessionState) : AppDestination
    data object ListenAndSpeak : AppDestination
    data object Dictionary : AppDestination
    data object Achievements : AppDestination
    data object Exams : AppDestination
    data object ParentDashboard : AppDestination
    data object AudioLibrary : AppDestination
}

data class GameSessionState(
    val titleAr: String,
    val unitNumber: Int, // 0 for comprehensive book exam
    val level: DifficultyLevel,
    val isExam: Boolean,
    val questions: List<GameQuestion>,
    val currentIndex: Int = 0,
    val selectedOptionId: String? = null,
    // Pair matching state
    val selectedEnglishPair: String? = null,
    val matchedEnglishSet: Set<String> = emptySet(),
    val pairWrongFlash: Boolean = false,
    // Letter scramble state (indices into scrambledLetters)
    val chosenLetterIndices: List<Int> = emptyList(),
    // Sentence builder state (indices into shuffledTokens)
    val chosenTokenIndices: List<Int> = emptyList(),
    // Answer evaluation state
    val isAnswered: Boolean = false,
    val isLastCorrect: Boolean = false,
    val showHint: Boolean = false,
    val points: Int = 0,
    val correctCount: Int = 0,
    val isFinished: Boolean = false
) {
    val currentQuestion: GameQuestion?
        get() = questions.getOrNull(currentIndex)

    val totalQuestions: Int
        get() = questions.size
}

data class FlashcardSessionState(
    val unit: BookUnit,
    val words: List<BookWord>,
    val currentIndex: Int = 0,
    val isMeaningRevealed: Boolean = false
) {
    val currentWord: BookWord?
        get() = words.getOrNull(currentIndex)
}

data class LinaUiState(
    val profile: ChildProfileEntity = ChildProfileEntity(),
    val unitProgressMap: Map<Int, UnitProgressEntity> = emptyMap(),
    val examResults: List<ExamResultEntity> = emptyList(),
    val units: List<BookUnit> = CurriculumRepository.units,
    val dictionaryWords: List<BookWord> = CurriculumRepository.dictionary,
    val listeningTracks: List<ListeningTrack> = CurriculumRepository.allListeningTracks,
    val destination: AppDestination = AppDestination.Home,
    val lastPronunciationResult: PronunciationResult? = null,
    val voiceSettings: VoiceSettingsEntity = VoiceSettingsEntity(),
    val unitAudioStats: Map<Int, UnitAudioStats> = emptyMap(),
    val totalDownloadedAudioCount: Int = 0,
    val totalDownloadedAudioBytes: Long = 0L
) {
    val selectedLevel: DifficultyLevel
        get() = DifficultyLevel.fromId(profile.selectedLevel)

    val voiceAccent: VoiceAccent
        get() = VoiceAccent.fromCode(voiceSettings.accentCode)

    val voicePersona: VoicePersona
        get() = VoicePersona.fromId(voiceSettings.personaId)

    val speechMode: SpeechMode
        get() = SpeechMode.fromId(voiceSettings.speechModeId)

    val voiceEnginePreference: VoiceEnginePreference
        get() = VoiceEnginePreference.fromId(voiceSettings.enginePreferenceId)

    val completedUnitsCount: Int
        get() = unitProgressMap.values.count { it.hasMedal }

    val level1CertificateUnlocked: Boolean
        get() = unitProgressMap.values.count { it.level1Completed } >= 4

    val level2CertificateUnlocked: Boolean
        get() = unitProgressMap.values.count { it.level2Completed } >= 4

    val level3CertificateUnlocked: Boolean
        get() = unitProgressMap.values.count { it.level3Completed } >= 4

    val fullBookCertificateUnlocked: Boolean
        get() = completedUnitsCount >= 8 || examResults.any { it.unitNumber == 0 && it.correctAnswers * 2 >= it.totalQuestions }
}

class LinaViewModel(
    private val localRepository: LinaLocalRepository,
    private val cachedAudioDao: CachedAudioDao? = null
) : ViewModel() {

    private val destinationFlow = MutableStateFlow<AppDestination>(AppDestination.Home)
    private val pronunciationResultFlow = MutableStateFlow<PronunciationResult?>(null)
    private val recentQuestionIds = ArrayDeque<String>()

    init {
        viewModelScope.launch {
            localRepository.ensureInitialized()
        }
    }

    private val coreStateFlow = combine(
        localRepository.profileFlow,
        localRepository.unitProgressFlow,
        localRepository.examResultsFlow,
        destinationFlow,
        pronunciationResultFlow
    ) { profile, unitList, exams, dest, pronResult ->
        LinaUiState(
            profile = profile ?: ChildProfileEntity(),
            unitProgressMap = unitList.associateBy { it.unitNumber },
            examResults = exams,
            units = CurriculumRepository.units,
            dictionaryWords = CurriculumRepository.dictionary,
            listeningTracks = CurriculumRepository.allListeningTracks,
            destination = dest,
            lastPronunciationResult = pronResult
        )
    }

    private val audioMetaFlow = combine(
        localRepository.voiceSettingsFlow ?: flowOf(VoiceSettingsEntity()),
        cachedAudioDao?.observeAllAudio() ?: flowOf(emptyList())
    ) { settings, cachedList ->
        val safeSettings = settings ?: VoiceSettingsEntity()
        val byUnit = cachedList.groupBy { it.unitNumber }
        val statsMap = (1..8).associateWith { u ->
            val total = CurriculumAudioCatalog.itemsForUnit(u).size
            val entries = byUnit[u].orEmpty()
            val uniqueDownloaded = entries
                .map { "${it.category}|${it.textEn.trim().lowercase()}" }
                .distinct()
                .size
                .coerceAtMost(total)
            UnitAudioStats(
                unitNumber = u,
                totalItems = total,
                downloadedItems = uniqueDownloaded,
                totalSizeBytes = entries.sumOf { it.fileSizeBytes }
            )
        }
        Triple(safeSettings, statsMap, cachedList.size to cachedList.sumOf { it.fileSizeBytes })
    }

    val uiState: StateFlow<LinaUiState> = combine(
        coreStateFlow,
        audioMetaFlow
    ) { base, (settings, statsMap, totals) ->
        base.copy(
            voiceSettings = settings,
            unitAudioStats = statsMap,
            totalDownloadedAudioCount = totals.first,
            totalDownloadedAudioBytes = totals.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LinaUiState()
    )

    fun navigateTo(destination: AppDestination) {
        destinationFlow.value = destination
    }

    fun navigateHome() {
        destinationFlow.value = AppDestination.Home
    }

    fun navigateBack() {
        val current = destinationFlow.value
        destinationFlow.value = when (current) {
            is AppDestination.Home -> AppDestination.Home
            is AppDestination.UnitDetail -> AppDestination.BookUnits
            is AppDestination.GamePlay -> {
                if (current.session.isExam) AppDestination.Exams
                else AppDestination.UnitDetail(current.session.unitNumber.coerceIn(1, 8))
            }
            is AppDestination.Flashcards -> AppDestination.UnitDetail(current.session.unit.number)
            else -> AppDestination.Home
        }
    }

    fun selectLevel(level: DifficultyLevel) {
        viewModelScope.launch {
            localRepository.updateSelectedLevel(level.id)
        }
    }

    fun updateChildName(name: String) {
        viewModelScope.launch {
            localRepository.updateChildName(name)
        }
    }

    fun selectCompanion(companionId: String) {
        viewModelScope.launch {
            localRepository.selectCompanion(companionId)
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            localRepository.resetAllProgress()
        }
    }

    fun setVoiceAccent(accent: VoiceAccent) {
        viewModelScope.launch {
            localRepository.updateVoiceSettings { it.copy(accentCode = accent.code) }
        }
    }

    fun setVoicePersona(persona: VoicePersona) {
        viewModelScope.launch {
            localRepository.updateVoiceSettings { it.copy(personaId = persona.id) }
        }
    }

    fun setSpeechMode(mode: SpeechMode) {
        viewModelScope.launch {
            localRepository.updateVoiceSettings { it.copy(speechModeId = mode.id) }
        }
    }

    fun setVoiceEngine(preference: VoiceEnginePreference) {
        viewModelScope.launch {
            localRepository.updateVoiceSettings { it.copy(enginePreferenceId = preference.id) }
        }
    }

    fun setCloudTtsEndpointUrl(url: String) {
        viewModelScope.launch {
            localRepository.updateVoiceSettings { it.copy(cloudTtsEndpointUrl = url.trim()) }
        }
    }

    fun recordPronunciation(result: PronunciationResult) {
        pronunciationResultFlow.value = result
        if (result.isSuccess) {
            viewModelScope.launch {
                localRepository.recordSpokenPractice(bonusStars = 5)
            }
        }
    }

    fun clearPronunciationResult() {
        pronunciationResultFlow.value = null
    }

    fun startUnitGame(unitNumber: Int, level: DifficultyLevel = uiState.value.selectedLevel) {
        val unit = CurriculumRepository.unitByNumber(unitNumber)
        val questions = CurriculumRepository.generateGameSession(
            unitNumber = unitNumber,
            level = level,
            recentQuestionIds = recentQuestionIds.toSet()
        )
        if (questions.isEmpty()) return
        rememberAskedIds(questions)

        val session = GameSessionState(
            titleAr = "Unit ${unit.number}: ${unit.titleEn}",
            unitNumber = unitNumber,
            level = level,
            isExam = false,
            questions = questions
        )
        destinationFlow.value = AppDestination.GamePlay(session)
    }

    fun startUnitExam(unitNumber: Int) {
        val unit = CurriculumRepository.unitByNumber(unitNumber)
        val questions = CurriculumRepository.generateUnitExam(
            unitNumber = unitNumber,
            recentQuestionIds = recentQuestionIds.toSet()
        )
        if (questions.isEmpty()) return
        rememberAskedIds(questions)

        val session = GameSessionState(
            titleAr = "اختبار الوحدة ${unit.number}: ${unit.titleEn}",
            unitNumber = unitNumber,
            level = DifficultyLevel.GENIUS,
            isExam = true,
            questions = questions
        )
        destinationFlow.value = AppDestination.GamePlay(session)
    }

    fun startComprehensiveBookExam() {
        val questions = CurriculumRepository.generateComprehensiveBookExam(
            recentQuestionIds = recentQuestionIds.toSet()
        )
        if (questions.isEmpty()) return
        rememberAskedIds(questions)

        val session = GameSessionState(
            titleAr = "الاختبار الشامل لكتاب English for Iraq",
            unitNumber = 0,
            level = DifficultyLevel.GENIUS,
            isExam = true,
            questions = questions
        )
        destinationFlow.value = AppDestination.GamePlay(session)
    }

    fun startFlashcards(unitNumber: Int) {
        val unit = CurriculumRepository.unitByNumber(unitNumber)
        val words = CurriculumRepository.wordsForUnit(unitNumber)
        if (words.isEmpty()) return
        destinationFlow.value = AppDestination.Flashcards(
            FlashcardSessionState(unit = unit, words = words)
        )
    }

    // ==================== GAME INTERACTIONS ====================

    fun toggleHint() {
        updateGameSession { it.copy(showHint = !it.showHint) }
    }

    fun selectMultipleChoice(optionId: String, onCorrect: () -> Unit, onWrong: () -> Unit) {
        val dest = destinationFlow.value as? AppDestination.GamePlay ?: return
        val session = dest.session
        if (session.isAnswered || session.isFinished) return
        val q = session.currentQuestion as? GameQuestion.MultipleChoice ?: return

        val correct = optionId == q.correctOptionId
        if (correct) onCorrect() else onWrong()

        updateGameSession {
            it.copy(
                selectedOptionId = optionId,
                isAnswered = true,
                isLastCorrect = correct,
                points = if (correct) it.points + STARS_PER_CORRECT else it.points,
                correctCount = if (correct) it.correctCount + 1 else it.correctCount
            )
        }
    }

    fun selectEnglishForPair(english: String) {
        updateGameSession { s ->
            if (s.isAnswered || english in s.matchedEnglishSet) return@updateGameSession s
            s.copy(selectedEnglishPair = english, pairWrongFlash = false)
        }
    }

    fun selectArabicForPair(arabic: String, onCorrect: () -> Unit, onWrong: () -> Unit) {
        val dest = destinationFlow.value as? AppDestination.GamePlay ?: return
        val session = dest.session
        val q = session.currentQuestion as? GameQuestion.PairMatching ?: return
        val selectedEn = session.selectedEnglishPair ?: return
        val matchedWord = q.pairs.firstOrNull { it.english == selectedEn } ?: return

        if (matchedWord.arabic == arabic) {
            onCorrect()
            val updatedMatched = session.matchedEnglishSet + selectedEn
            val allDone = updatedMatched.size == q.pairs.size
            updateGameSession {
                it.copy(
                    selectedEnglishPair = null,
                    matchedEnglishSet = updatedMatched,
                    pairWrongFlash = false,
                    isAnswered = allDone,
                    isLastCorrect = allDone,
                    points = if (allDone) it.points + STARS_PER_CORRECT else it.points,
                    correctCount = if (allDone) it.correctCount + 1 else it.correctCount
                )
            }
        } else {
            onWrong()
            updateGameSession {
                it.copy(selectedEnglishPair = null, pairWrongFlash = true)
            }
        }
    }

    fun tapScrambleLetter(index: Int) {
        updateGameSession { s ->
            if (s.isAnswered || index in s.chosenLetterIndices) return@updateGameSession s
            s.copy(chosenLetterIndices = s.chosenLetterIndices + index)
        }
    }

    fun removeScrambleLetter(positionInChosen: Int) {
        updateGameSession { s ->
            if (s.isAnswered) return@updateGameSession s
            val mutable = s.chosenLetterIndices.toMutableList()
            if (positionInChosen in mutable.indices) {
                mutable.removeAt(positionInChosen)
            }
            s.copy(chosenLetterIndices = mutable)
        }
    }

    fun clearScrambleLetters() {
        updateGameSession { s ->
            if (s.isAnswered) return@updateGameSession s
            s.copy(chosenLetterIndices = emptyList())
        }
    }

    fun submitScramble(onCorrect: () -> Unit, onWrong: () -> Unit) {
        val dest = destinationFlow.value as? AppDestination.GamePlay ?: return
        val session = dest.session
        if (session.isAnswered) return
        val q = session.currentQuestion as? GameQuestion.LetterScramble ?: return

        val built = session.chosenLetterIndices.mapNotNull { q.scrambledLetters.getOrNull(it) }.joinToString("")
        val correct = built.equals(q.targetWord.english, ignoreCase = true)
        if (correct) onCorrect() else onWrong()

        updateGameSession {
            it.copy(
                isAnswered = true,
                isLastCorrect = correct,
                points = if (correct) it.points + STARS_PER_CORRECT else it.points,
                correctCount = if (correct) it.correctCount + 1 else it.correctCount
            )
        }
    }

    fun tapSentenceToken(index: Int) {
        updateGameSession { s ->
            if (s.isAnswered || index in s.chosenTokenIndices) return@updateGameSession s
            s.copy(chosenTokenIndices = s.chosenTokenIndices + index)
        }
    }

    fun removeSentenceToken(positionInChosen: Int) {
        updateGameSession { s ->
            if (s.isAnswered) return@updateGameSession s
            val mutable = s.chosenTokenIndices.toMutableList()
            if (positionInChosen in mutable.indices) {
                mutable.removeAt(positionInChosen)
            }
            s.copy(chosenTokenIndices = mutable)
        }
    }

    fun clearSentenceTokens() {
        updateGameSession { s ->
            if (s.isAnswered) return@updateGameSession s
            s.copy(chosenTokenIndices = emptyList())
        }
    }

    fun submitSentence(onCorrect: () -> Unit, onWrong: () -> Unit) {
        val dest = destinationFlow.value as? AppDestination.GamePlay ?: return
        val session = dest.session
        if (session.isAnswered) return
        val q = session.currentQuestion as? GameQuestion.SentenceBuilder ?: return

        val built = session.chosenTokenIndices.mapNotNull { q.shuffledTokens.getOrNull(it) }
        val correct = built == q.correctTokens
        if (correct) onCorrect() else onWrong()

        updateGameSession {
            it.copy(
                isAnswered = true,
                isLastCorrect = correct,
                points = if (correct) it.points + STARS_PER_CORRECT else it.points,
                correctCount = if (correct) it.correctCount + 1 else it.correctCount
            )
        }
    }

    fun advanceQuestion(onFinished: () -> Unit) {
        val dest = destinationFlow.value as? AppDestination.GamePlay ?: return
        val session = dest.session
        if (!session.isAnswered) return

        val nextIdx = session.currentIndex + 1
        if (nextIdx < session.totalQuestions) {
            updateGameSession {
                it.copy(
                    currentIndex = nextIdx,
                    selectedOptionId = null,
                    selectedEnglishPair = null,
                    matchedEnglishSet = emptySet(),
                    pairWrongFlash = false,
                    chosenLetterIndices = emptyList(),
                    chosenTokenIndices = emptyList(),
                    isAnswered = false,
                    isLastCorrect = false,
                    showHint = false
                )
            }
        } else {
            onFinished()
            updateGameSession { it.copy(isFinished = true) }
            viewModelScope.launch {
                if (session.isExam) {
                    localRepository.recordExamResult(
                        examTitleAr = session.titleAr,
                        unitNumber = session.unitNumber,
                        correctAnswers = session.correctCount,
                        totalQuestions = session.totalQuestions,
                        starsEarned = session.points
                    )
                } else {
                    localRepository.recordGameCompletion(
                        unitNumber = session.unitNumber,
                        levelId = session.level.id,
                        earnedStars = session.points,
                        correctCount = session.correctCount,
                        totalQuestions = session.totalQuestions
                    )
                }
            }
        }
    }

    fun replayCurrentGame() {
        val dest = destinationFlow.value as? AppDestination.GamePlay ?: return
        val s = dest.session
        if (s.isExam) {
            if (s.unitNumber == 0) startComprehensiveBookExam()
            else startUnitExam(s.unitNumber)
        } else {
            startUnitGame(s.unitNumber, s.level)
        }
    }

    // ==================== FLASHCARDS ====================

    fun toggleFlashcardMeaning() {
        val dest = destinationFlow.value as? AppDestination.Flashcards ?: return
        destinationFlow.value = AppDestination.Flashcards(
            dest.session.copy(isMeaningRevealed = !dest.session.isMeaningRevealed)
        )
    }

    fun nextFlashcard() {
        val dest = destinationFlow.value as? AppDestination.Flashcards ?: return
        if (dest.session.currentIndex + 1 >= dest.session.words.size) return
        destinationFlow.value = AppDestination.Flashcards(
            dest.session.copy(
                currentIndex = dest.session.currentIndex + 1,
                isMeaningRevealed = false
            )
        )
    }

    fun previousFlashcard() {
        val dest = destinationFlow.value as? AppDestination.Flashcards ?: return
        if (dest.session.currentIndex <= 0) return
        destinationFlow.value = AppDestination.Flashcards(
            dest.session.copy(
                currentIndex = dest.session.currentIndex - 1,
                isMeaningRevealed = false
            )
        )
    }

    private fun updateGameSession(transform: (GameSessionState) -> GameSessionState) {
        destinationFlow.update { dest ->
            if (dest is AppDestination.GamePlay) AppDestination.GamePlay(transform(dest.session))
            else dest
        }
    }

    private fun rememberAskedIds(questions: List<GameQuestion>) {
        questions.forEach { q ->
            recentQuestionIds.addLast(q.id)
            if (recentQuestionIds.size > 40) {
                recentQuestionIds.removeFirst()
            }
        }
    }

    companion object {
        private const val STARS_PER_CORRECT = 10

        fun factory(
            repository: LinaLocalRepository,
            cachedAudioDao: CachedAudioDao? = null
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LinaViewModel(repository, cachedAudioDao) as T
                }
            }
    }
}
