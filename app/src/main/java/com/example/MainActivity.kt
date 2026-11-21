package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.audio.AudioContentCategory
import com.example.audio.AudioDownloadWorker
import com.example.audio.CurriculumAudioCatalog
import com.example.audio.HybridAudioManager
import com.example.audio.PronunciationHelper
import com.example.audio.SoundEffects
import com.example.audio.VoicePersona
import com.example.audio.WordSpeaker
import com.example.audio.configureAppCheck
import com.example.data.CurriculumRepository
import com.example.data.local.LinaDatabase
import com.example.data.local.LinaLocalRepository
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.AudioLibraryScreen
import com.example.ui.screens.BookUnitsScreen
import com.example.ui.screens.DictionaryScreen
import com.example.ui.screens.ExamsScreen
import com.example.ui.screens.FlashcardsScreen
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ListenAndSpeakScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.UnitDetailScreen
import com.example.ui.theme.TeacherLinaTheme
import com.example.viewmodel.AppDestination
import com.example.viewmodel.LinaViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var wordSpeaker: WordSpeaker
    private lateinit var hybridAudioManager: HybridAudioManager
    private lateinit var soundEffects: SoundEffects
    private lateinit var pronunciationHelper: PronunciationHelper
    private lateinit var viewModel: LinaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // App Check MUST be configured before any ViewModel or Firebase AI Logic call
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        wordSpeaker = WordSpeaker(this)
        soundEffects = SoundEffects()
        pronunciationHelper = PronunciationHelper(this)

        val database = LinaDatabase.getInstance(applicationContext)
        hybridAudioManager = HybridAudioManager(
            context = this,
            cachedAudioDao = database.cachedAudioDao(),
            voiceSettingsDao = database.voiceSettingsDao(),
            wordSpeaker = wordSpeaker
        )

        val localRepository = LinaLocalRepository(
            dao = database.progressDao(),
            voiceSettingsDao = database.voiceSettingsDao()
        )
        viewModel = ViewModelProvider(
            this,
            LinaViewModel.factory(localRepository, database.cachedAudioDao())
        )[LinaViewModel::class.java]

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isTtsSpeaking by wordSpeaker.isSpeaking.collectAsStateWithLifecycle()
            val playbackState by hybridAudioManager.playbackState.collectAsStateWithLifecycle()
            val isListening by pronunciationHelper.isListening.collectAsStateWithLifecycle()
            val pronunciationErrorAr by pronunciationHelper.errorMessageAr.collectAsStateWithLifecycle()

            val downloadProgressFlow = remember { AudioDownloadWorker.observeProgress(applicationContext) }
            val downloadProgress by downloadProgressFlow.collectAsStateWithLifecycle(initialValue = null)

            val isSpeaking = isTtsSpeaking || playbackState.isPlaying

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                TeacherLinaTheme {
                    AnimatedContent(
                        targetState = uiState.destination,
                        contentKey = { dest ->
                            when (dest) {
                                is AppDestination.UnitDetail -> "unit_${dest.unitNumber}"
                                is AppDestination.GamePlay -> "game_${dest.session.unitNumber}_${dest.session.isExam}"
                                is AppDestination.Flashcards -> "flash_${dest.session.unit.number}"
                                else -> dest::class.simpleName ?: "home"
                            }
                        },
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "teacher_lina_navigation"
                    ) { destination ->
                        when (destination) {
                            AppDestination.Home -> {
                                HomeScreen(
                                    uiState = uiState,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onLevelSelected = { viewModel.selectLevel(it) },
                                    onQuickPlayUnit = { unitNum ->
                                        viewModel.startUnitGame(unitNum, uiState.selectedLevel)
                                    },
                                    onSpeakWelcome = { text ->
                                        hybridAudioManager.playEnglish(
                                            textEn = text,
                                            unitNumber = 0,
                                            category = AudioContentCategory.SENTENCE
                                        )
                                    }
                                )
                            }

                            AppDestination.BookUnits -> {
                                BookUnitsScreen(
                                    units = uiState.units,
                                    unitProgressMap = uiState.unitProgressMap,
                                    onUnitClick = { unitNum ->
                                        viewModel.navigateTo(AppDestination.UnitDetail(unitNum))
                                    },
                                    onBack = { viewModel.navigateHome() }
                                )
                            }

                            is AppDestination.UnitDetail -> {
                                val unit = CurriculumRepository.unitByNumber(destination.unitNumber)
                                UnitDetailScreen(
                                    unit = unit,
                                    progress = uiState.unitProgressMap[destination.unitNumber],
                                    onStartGame = { level ->
                                        viewModel.startUnitGame(unit.number, level)
                                    },
                                    onStartFlashcards = {
                                        viewModel.startFlashcards(unit.number)
                                    },
                                    onStartUnitExam = {
                                        viewModel.startUnitExam(unit.number)
                                    },
                                    onSpeakEnglish = { text ->
                                        val isStoryLike = text.length > 90
                                        hybridAudioManager.playEnglish(
                                            textEn = text,
                                            unitNumber = unit.number,
                                            category = if (isStoryLike) AudioContentCategory.STORY else AudioContentCategory.SENTENCE,
                                            personaOverride = if (isStoryLike) VoicePersona.MISS_EMMA else null,
                                            preferExpressiveGemini = isStoryLike
                                        )
                                    },
                                    onBack = {
                                        hybridAudioManager.stopAllPlayback()
                                        viewModel.navigateBack()
                                    }
                                )
                            }

                            is AppDestination.GamePlay -> {
                                val activeSession = (uiState.destination as? AppDestination.GamePlay)?.session
                                    ?: destination.session
                                GamePlayScreen(
                                    state = activeSession,
                                    isSpeaking = isSpeaking,
                                    onSelectMultipleChoice = { optionId ->
                                        viewModel.selectMultipleChoice(
                                            optionId = optionId,
                                            onCorrect = { soundEffects.playCorrect() },
                                            onWrong = { soundEffects.playWrong() }
                                        )
                                    },
                                    onSelectEnglishPair = { viewModel.selectEnglishForPair(it) },
                                    onSelectArabicPair = { arabic ->
                                        viewModel.selectArabicForPair(
                                            arabic = arabic,
                                            onCorrect = { soundEffects.playCorrect() },
                                            onWrong = { soundEffects.playWrong() }
                                        )
                                    },
                                    onTapScrambleLetter = { viewModel.tapScrambleLetter(it) },
                                    onRemoveScrambleLetter = { viewModel.removeScrambleLetter(it) },
                                    onClearScramble = { viewModel.clearScrambleLetters() },
                                    onSubmitScramble = {
                                        viewModel.submitScramble(
                                            onCorrect = { soundEffects.playCorrect() },
                                            onWrong = { soundEffects.playWrong() }
                                        )
                                    },
                                    onTapSentenceToken = { viewModel.tapSentenceToken(it) },
                                    onRemoveSentenceToken = { viewModel.removeSentenceToken(it) },
                                    onClearSentence = { viewModel.clearSentenceTokens() },
                                    onSubmitSentence = {
                                        viewModel.submitSentence(
                                            onCorrect = { soundEffects.playCorrect() },
                                            onWrong = { soundEffects.playWrong() }
                                        )
                                    },
                                    onToggleHint = { viewModel.toggleHint() },
                                    onNextQuestion = {
                                        viewModel.advanceQuestion(
                                            onFinished = { soundEffects.playCelebration() }
                                        )
                                    },
                                    onSpeakEnglish = { text ->
                                        hybridAudioManager.playEnglish(
                                            textEn = text,
                                            unitNumber = activeSession.unitNumber,
                                            category = AudioContentCategory.EXERCISE
                                        )
                                    },
                                    onPlayAgain = { viewModel.replayCurrentGame() },
                                    onExit = {
                                        hybridAudioManager.stopAllPlayback()
                                        viewModel.navigateBack()
                                    }
                                )
                            }

                            is AppDestination.Flashcards -> {
                                val activeFlashcards = (uiState.destination as? AppDestination.Flashcards)?.session
                                    ?: destination.session
                                FlashcardsScreen(
                                    state = activeFlashcards,
                                    isSpeaking = isSpeaking,
                                    onToggleMeaning = { viewModel.toggleFlashcardMeaning() },
                                    onPrevious = { viewModel.previousFlashcard() },
                                    onNext = { viewModel.nextFlashcard() },
                                    onSpeakWord = { text ->
                                        hybridAudioManager.playEnglish(
                                            textEn = text,
                                            unitNumber = activeFlashcards.unit.number,
                                            category = AudioContentCategory.WORD
                                        )
                                    },
                                    onStartQuiz = {
                                        viewModel.startUnitGame(
                                            activeFlashcards.unit.number,
                                            uiState.selectedLevel
                                        )
                                    },
                                    onExit = {
                                        hybridAudioManager.stopAllPlayback()
                                        viewModel.navigateBack()
                                    }
                                )
                            }

                            AppDestination.ListenAndSpeak -> {
                                ListenAndSpeakScreen(
                                    tracks = uiState.listeningTracks,
                                    dialogues = CurriculumAudioCatalog.allDialogues,
                                    playbackState = playbackState,
                                    isSpeaking = isSpeaking,
                                    isListening = isListening,
                                    pronunciationResult = uiState.lastPronunciationResult,
                                    pronunciationErrorAr = pronunciationErrorAr,
                                    onSpeakEnglish = { text ->
                                        hybridAudioManager.playEnglish(
                                            textEn = text,
                                            unitNumber = 0,
                                            category = AudioContentCategory.WORD
                                        )
                                    },
                                    onPlayTrack = { track ->
                                        hybridAudioManager.playEnglish(
                                            textEn = track.scriptEn,
                                            unitNumber = track.unitNumber,
                                            lessonNumber = 6,
                                            category = AudioContentCategory.LISTENING_TRACK,
                                            personaOverride = VoicePersona.MISS_EMMA,
                                            preferExpressiveGemini = true
                                        )
                                    },
                                    onPracticeWord = { unitNum, wordEn ->
                                        hybridAudioManager.playEnglish(
                                            textEn = wordEn,
                                            unitNumber = unitNum,
                                            category = AudioContentCategory.WORD,
                                            forcePracticeSequence = true
                                        )
                                    },
                                    onPlayFullDialogue = { script ->
                                        hybridAudioManager.playFullDialogue(script)
                                    },
                                    onPlayDialogueTurn = { script, turnIdx ->
                                        hybridAudioManager.playDialogueTurn(script, turnIdx)
                                    },
                                    onStartStudentRecording = {
                                        hybridAudioManager.startStudentRecording()
                                    },
                                    onStopStudentRecording = {
                                        hybridAudioManager.stopStudentRecording()
                                    },
                                    onPlayStudentRecording = {
                                        hybridAudioManager.playStudentRecording()
                                    },
                                    onStopSpeaking = { hybridAudioManager.stopAllPlayback() },
                                    onStartPronunciation = { target ->
                                        hybridAudioManager.stopAllPlayback()
                                        hybridAudioManager.stopStudentRecording()
                                        pronunciationHelper.startListening(target) { result ->
                                            if (result.isSuccess) soundEffects.playCorrect()
                                            else soundEffects.playWrong()
                                            viewModel.recordPronunciation(result)
                                        }
                                    },
                                    onStopPronunciation = { pronunciationHelper.stopListening() },
                                    onClearPronunciationResult = { viewModel.clearPronunciationResult() },
                                    onDismissAudioNotice = { hybridAudioManager.clearNotice() },
                                    onOpenAudioLibrary = {
                                        hybridAudioManager.stopAllPlayback()
                                        viewModel.navigateTo(AppDestination.AudioLibrary)
                                    },
                                    onBack = {
                                        hybridAudioManager.stopAllPlayback()
                                        hybridAudioManager.stopStudentRecording()
                                        viewModel.navigateHome()
                                    }
                                )
                            }

                            AppDestination.Dictionary -> {
                                DictionaryScreen(
                                    words = uiState.dictionaryWords,
                                    isSpeaking = isSpeaking,
                                    onSpeakEnglish = { text ->
                                        val matchingUnit = uiState.dictionaryWords
                                            .firstOrNull { it.english.equals(text, ignoreCase = true) || it.exampleSentence == text }
                                            ?.unitNumber ?: 0
                                        hybridAudioManager.playEnglish(
                                            textEn = text,
                                            unitNumber = matchingUnit,
                                            category = if (text.contains(" ")) AudioContentCategory.SENTENCE else AudioContentCategory.WORD
                                        )
                                    },
                                    onBack = {
                                        hybridAudioManager.stopAllPlayback()
                                        viewModel.navigateHome()
                                    }
                                )
                            }

                            AppDestination.Achievements -> {
                                AchievementsScreen(
                                    uiState = uiState,
                                    onOpenUnit = { unitNum ->
                                        viewModel.navigateTo(AppDestination.UnitDetail(unitNum))
                                    },
                                    onSelectCompanion = { viewModel.selectCompanion(it) },
                                    onBack = { viewModel.navigateHome() }
                                )
                            }

                            AppDestination.Exams -> {
                                ExamsScreen(
                                    uiState = uiState,
                                    onStartUnitExam = { viewModel.startUnitExam(it) },
                                    onStartBookExam = { viewModel.startComprehensiveBookExam() },
                                    onBack = { viewModel.navigateHome() }
                                )
                            }

                            AppDestination.ParentDashboard -> {
                                ParentDashboardScreen(
                                    uiState = uiState,
                                    onUpdateChildName = { viewModel.updateChildName(it) },
                                    onResetProgress = { viewModel.resetAllProgress() },
                                    onBack = { viewModel.navigateHome() }
                                )
                            }

                            AppDestination.AudioLibrary -> {
                                AudioLibraryScreen(
                                    uiState = uiState,
                                    playbackState = playbackState,
                                    downloadProgress = downloadProgress,
                                    onSelectAccent = { viewModel.setVoiceAccent(it) },
                                    onSelectPersona = { viewModel.setVoicePersona(it) },
                                    onSelectSpeechMode = { viewModel.setSpeechMode(it) },
                                    onSelectEngine = { viewModel.setVoiceEngine(it) },
                                    onSaveCloudEndpoint = { viewModel.setCloudTtsEndpointUrl(it) },
                                    onPreviewPersona = { persona ->
                                        val sample = when (persona) {
                                            VoicePersona.MISS_LILY -> "Hello! I am Miss Lily. Let's practise our English words clearly together!"
                                            VoicePersona.MISS_EMMA -> "Hello! I am Miss Emma. Let's read an exciting story from English for Iraq!"
                                            VoicePersona.MR_JOHN -> "Good morning! I am Mr. John. Let's practise our classroom dialogues!"
                                            VoicePersona.KIDS_VOICE -> "Hi! I am your study friend! Let's play and learn English together!"
                                        }
                                        hybridAudioManager.playEnglish(
                                            textEn = sample,
                                            unitNumber = 0,
                                            category = AudioContentCategory.SENTENCE,
                                            personaOverride = persona,
                                            preferExpressiveGemini = persona == VoicePersona.MISS_EMMA
                                        )
                                    },
                                    onDownloadUnit = { unitNum ->
                                        AudioDownloadWorker.enqueueDownload(this@MainActivity, unitNum)
                                    },
                                    onDownloadAll = {
                                        AudioDownloadWorker.enqueueDownload(this@MainActivity, 0)
                                    },
                                    onPauseDownload = {
                                        AudioDownloadWorker.pauseDownload(this@MainActivity)
                                    },
                                    onDeleteUnitAudio = { unitNum ->
                                        lifecycleScope.launch {
                                            hybridAudioManager.deleteUnitAudio(unitNum)
                                        }
                                    },
                                    onDeleteAllAudio = {
                                        lifecycleScope.launch {
                                            hybridAudioManager.deleteAllAudio()
                                        }
                                    },
                                    onBack = {
                                        hybridAudioManager.stopAllPlayback()
                                        viewModel.navigateHome()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        hybridAudioManager.release()
        wordSpeaker.shutdown()
        soundEffects.release()
        pronunciationHelper.stopListening()
        super.onDestroy()
    }
}
