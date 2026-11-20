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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.PronunciationHelper
import com.example.audio.SoundEffects
import com.example.audio.WordSpeaker
import com.example.data.CurriculumRepository
import com.example.data.local.LinaDatabase
import com.example.data.local.LinaLocalRepository
import com.example.ui.screens.AchievementsScreen
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

class MainActivity : ComponentActivity() {

    private lateinit var wordSpeaker: WordSpeaker
    private lateinit var soundEffects: SoundEffects
    private lateinit var pronunciationHelper: PronunciationHelper
    private lateinit var viewModel: LinaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        wordSpeaker = WordSpeaker(this)
        soundEffects = SoundEffects()
        pronunciationHelper = PronunciationHelper(this)

        val database = LinaDatabase.getInstance(applicationContext)
        val localRepository = LinaLocalRepository(database.progressDao())
        viewModel = ViewModelProvider(
            this,
            LinaViewModel.factory(localRepository)
        )[LinaViewModel::class.java]

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isSpeaking by wordSpeaker.isSpeaking.collectAsStateWithLifecycle()
            val isListening by pronunciationHelper.isListening.collectAsStateWithLifecycle()
            val pronunciationErrorAr by pronunciationHelper.errorMessageAr.collectAsStateWithLifecycle()

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
                                        wordSpeaker.speakArabic(text)
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
                                        wordSpeaker.speakEnglish(text)
                                    },
                                    onBack = { viewModel.navigateBack() }
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
                                    onSpeakEnglish = { wordSpeaker.speakEnglish(it) },
                                    onPlayAgain = { viewModel.replayCurrentGame() },
                                    onExit = {
                                        wordSpeaker.stop()
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
                                    onSpeakWord = { wordSpeaker.speakEnglish(it) },
                                    onStartQuiz = {
                                        viewModel.startUnitGame(
                                            activeFlashcards.unit.number,
                                            uiState.selectedLevel
                                        )
                                    },
                                    onExit = {
                                        wordSpeaker.stop()
                                        viewModel.navigateBack()
                                    }
                                )
                            }

                            AppDestination.ListenAndSpeak -> {
                                ListenAndSpeakScreen(
                                    tracks = uiState.listeningTracks,
                                    isSpeaking = isSpeaking,
                                    isListening = isListening,
                                    pronunciationResult = uiState.lastPronunciationResult,
                                    pronunciationErrorAr = pronunciationErrorAr,
                                    onSpeakEnglish = { wordSpeaker.speakEnglish(it) },
                                    onStopSpeaking = { wordSpeaker.stop() },
                                    onStartPronunciation = { target ->
                                        wordSpeaker.stop()
                                        pronunciationHelper.startListening(target) { result ->
                                            if (result.isSuccess) soundEffects.playCorrect()
                                            else soundEffects.playWrong()
                                            viewModel.recordPronunciation(result)
                                        }
                                    },
                                    onStopPronunciation = { pronunciationHelper.stopListening() },
                                    onClearPronunciationResult = { viewModel.clearPronunciationResult() },
                                    onBack = { viewModel.navigateHome() }
                                )
                            }

                            AppDestination.Dictionary -> {
                                DictionaryScreen(
                                    words = uiState.dictionaryWords,
                                    isSpeaking = isSpeaking,
                                    onSpeakEnglish = { wordSpeaker.speakEnglish(it) },
                                    onBack = {
                                        wordSpeaker.stop()
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
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        wordSpeaker.shutdown()
        soundEffects.release()
        pronunciationHelper.stopListening()
        super.onDestroy()
    }
}
