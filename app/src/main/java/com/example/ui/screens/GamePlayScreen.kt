package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameQuestion
import com.example.ui.components.ConfettiBurstOverlay
import com.example.ui.components.ConfettiShowerOverlay
import com.example.ui.components.TeacherLinaCompanionBar
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.GameSessionState
import com.example.viewmodel.LinaMood

@Composable
fun GamePlayScreen(
    state: GameSessionState,
    isSpeaking: Boolean,
    onSelectMultipleChoice: (String) -> Unit,
    onSelectEnglishPair: (String) -> Unit,
    onSelectArabicPair: (String) -> Unit,
    onTapScrambleLetter: (Int) -> Unit,
    onRemoveScrambleLetter: (Int) -> Unit,
    onClearScramble: () -> Unit,
    onSubmitScramble: () -> Unit,
    onTapSentenceToken: (Int) -> Unit,
    onRemoveSentenceToken: (Int) -> Unit,
    onClearSentence: () -> Unit,
    onSubmitSentence: () -> Unit,
    onToggleHint: () -> Unit,
    onNextQuestion: () -> Unit,
    onSpeakEnglish: (String) -> Unit,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler(onBack = onExit)

    if (state.isFinished) {
        GameVictoryScreen(
            titleAr = state.titleAr,
            isExam = state.isExam,
            points = state.points,
            correctCount = state.correctCount,
            totalQuestions = state.totalQuestions,
            onPlayAgain = onPlayAgain,
            onExit = onExit
        )
        return
    }

    val question = state.currentQuestion ?: return
    val linaColors = LocalLinaColors.current
    val progress by animateFloatAsState(
        targetValue = (state.currentIndex + 1).toFloat() / state.totalQuestions.coerceAtLeast(1).toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "game_progress"
    )

    // Auto-speak listening-focused questions when they appear
    LaunchedEffect(question.id) {
        if (question is GameQuestion.MultipleChoice && question.isListeningOnly) {
            question.speakableEnglish?.let { onSpeakEnglish(it) }
        }
    }

    val linaMood = when {
        state.isAnswered && state.isLastCorrect -> LinaMood.HAPPY
        state.isAnswered && !state.isLastCorrect -> LinaMood.HINTING
        state.showHint -> LinaMood.HINTING
        else -> LinaMood.EXPLAINING
    }

    val linaMessage = when {
        state.isAnswered && state.isLastCorrect -> "ممتاز يا بطل! إجابة صحيحة وحصلت على 10 نجوم 🌟"
        state.isAnswered && !state.isLastCorrect -> question.hintAr
        state.showHint -> question.hintAr
        state.pairWrongFlash -> "قريب جداً! حاول مطابقة الكلمة الإنجليزية بمعناها الصحيح 🌸"
        else -> question.linaInstructionAr
    }

    val confettiTriggerKey = when {
        state.isAnswered && state.isLastCorrect -> "correct_${state.currentIndex}_${state.correctCount}"
        state.matchedEnglishSet.isNotEmpty() && !state.isAnswered -> "pair_${state.currentIndex}_${state.matchedEnglishSet.size}"
        else -> null
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onExit,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("game_exit_button")
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("خروج", style = MaterialTheme.typography.labelLarge)
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = question.gameBadgeAr,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = linaColors.starContainer,
                        modifier = Modifier.testTag("game_points_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = linaColors.starAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${state.points}",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FredokaFontFamily,
                                color = linaColors.onStarContainer
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "السؤال ${state.currentIndex + 1} من ${state.totalQuestions} • ${state.titleAr}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                TeacherLinaCompanionBar(
                    messageAr = linaMessage,
                    mood = linaMood,
                    isSpeaking = isSpeaking,
                    onSpeakMessage = question.speakableEnglish?.let { eng -> { onSpeakEnglish(eng) } },
                    onHintClick = if (!state.isAnswered) onToggleHint else null
                )

                AnimatedContent(
                    targetState = question,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    contentKey = { it.id },
                    label = "game_question_content"
                ) { q ->
                    when (q) {
                        is GameQuestion.MultipleChoice -> MultipleChoiceQuestionBody(
                            question = q,
                            selectedOptionId = state.selectedOptionId,
                            isAnswered = state.isAnswered,
                            onSelectOption = onSelectMultipleChoice,
                            onSpeakEnglish = onSpeakEnglish
                        )

                        is GameQuestion.PairMatching -> PairMatchingQuestionBody(
                            question = q,
                            selectedEnglish = state.selectedEnglishPair,
                            matchedEnglishSet = state.matchedEnglishSet,
                            onSelectEnglish = onSelectEnglishPair,
                            onSelectArabic = onSelectArabicPair,
                            onSpeakEnglish = onSpeakEnglish
                        )

                        is GameQuestion.LetterScramble -> LetterScrambleQuestionBody(
                            question = q,
                            chosenIndices = state.chosenLetterIndices,
                            isAnswered = state.isAnswered,
                            isCorrect = state.isLastCorrect,
                            onTapLetter = onTapScrambleLetter,
                            onRemoveLetter = onRemoveScrambleLetter,
                            onClear = onClearScramble,
                            onSubmit = onSubmitScramble,
                            onSpeakEnglish = onSpeakEnglish
                        )

                        is GameQuestion.SentenceBuilder -> SentenceBuilderQuestionBody(
                            question = q,
                            chosenIndices = state.chosenTokenIndices,
                            isAnswered = state.isAnswered,
                            isCorrect = state.isLastCorrect,
                            onTapToken = onTapSentenceToken,
                            onRemoveToken = onRemoveSentenceToken,
                            onClear = onClearSentence,
                            onSubmit = onSubmitSentence,
                            onSpeakEnglish = onSpeakEnglish
                        )
                    }
                }

                AnimatedVisibility(
                    visible = state.isAnswered,
                    enter = fadeIn() + expandVertically()
                ) {
                    Button(
                        onClick = onNextQuestion,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isLastCorrect) linaColors.success else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("game_next_button")
                    ) {
                        Text(
                            text = if (state.currentIndex + 1 < state.totalQuestions) "السؤال التالي" else "إنهاء وعرض النتيجة 🏆",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
            }

            ConfettiBurstOverlay(
                triggerKey = confettiTriggerKey,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun MultipleChoiceQuestionBody(
    question: GameQuestion.MultipleChoice,
    selectedOptionId: String?,
    isAnswered: Boolean,
    onSelectOption: (String) -> Unit,
    onSpeakEnglish: (String) -> Unit
) {
    val linaColors = LocalLinaColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, linaColors.cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                question.promptEmoji?.let {
                    Text(text = it, fontSize = 62.sp)
                }

                val isEnglishMain = question.promptMainText.any { it in 'A'..'Z' || it in 'a'..'z' }
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (isEnglishMain) LayoutDirection.Ltr else LayoutDirection.Rtl
                ) {
                    Text(
                        text = question.promptMainText,
                        style = if (question.promptMainText.length <= 22) {
                            MaterialTheme.typography.headlineMedium
                        } else {
                            MaterialTheme.typography.titleLarge
                        },
                        fontFamily = if (isEnglishMain) FredokaFontFamily else MaterialTheme.typography.titleLarge.fontFamily,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }

                question.promptSubText?.let { sub ->
                    val subIsEn = sub.any { it in 'A'..'Z' || it in 'a'..'z' } && sub.none { it in '\u0600'..'\u06FF' }
                    CompositionLocalProvider(
                        LocalLayoutDirection provides if (subIsEn) LayoutDirection.Ltr else LayoutDirection.Rtl
                    ) {
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                question.speakableEnglish?.let { eng ->
                    FilledTonalButton(
                        onClick = { onSpeakEnglish(eng) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("mc_speak_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "استمع للنطق",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (question.isListeningOnly) "استمع مرة أخرى" else "استمع للنطق",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            question.options.forEachIndexed { idx, opt ->
                val isCorrect = opt.id == question.correctOptionId
                val isSelected = selectedOptionId == opt.id
                val bg by animateColorAsState(
                    targetValue = when {
                        !isAnswered -> MaterialTheme.colorScheme.primary
                        isCorrect -> linaColors.success
                        isSelected -> linaColors.wrong
                        else -> linaColors.mutedOption
                    },
                    label = "mc_opt_bg_$idx"
                )

                Button(
                    onClick = { onSelectOption(opt.id) },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = bg,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("mc_option_$idx")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        opt.emoji?.let {
                            Text(text = it, fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                        }
                        CompositionLocalProvider(
                            LocalLayoutDirection provides if (opt.isEnglish) LayoutDirection.Ltr else LayoutDirection.Rtl
                        ) {
                            Text(
                                text = opt.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = if (opt.isEnglish) FredokaFontFamily else MaterialTheme.typography.titleMedium.fontFamily,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        if (isAnswered && isCorrect) {
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(22.dp))
                        } else if (isAnswered && isSelected && !isCorrect) {
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Rounded.Cancel, contentDescription = null, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PairMatchingQuestionBody(
    question: GameQuestion.PairMatching,
    selectedEnglish: String?,
    matchedEnglishSet: Set<String>,
    onSelectEnglish: (String) -> Unit,
    onSelectArabic: (String) -> Unit,
    onSpeakEnglish: (String) -> Unit
) {
    val linaColors = LocalLinaColors.current
    val shuffledArabic = remember(question.id) { question.pairs.shuffled() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "الكلمة الإنجليزية",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            question.pairs.forEachIndexed { idx, word ->
                val isMatched = word.english in matchedEnglishSet
                val isSelected = selectedEnglish == word.english
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isMatched -> linaColors.successContainer
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        when {
                            isMatched -> linaColors.success
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> linaColors.cardBorder
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(enabled = !isMatched) {
                            onSpeakEnglish(word.english)
                            onSelectEnglish(word.english)
                        }
                        .testTag("pair_en_$idx")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                text = word.english,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FredokaFontFamily,
                                color = when {
                                    isMatched -> linaColors.onSuccessContainer
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "المعنى والصورة",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            shuffledArabic.forEachIndexed { idx, word ->
                val isMatched = word.english in matchedEnglishSet
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMatched) linaColors.successContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (isMatched) linaColors.success else linaColors.cardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(enabled = !isMatched && selectedEnglish != null) {
                            onSelectArabic(word.arabic)
                        }
                        .testTag("pair_ar_$idx")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = word.emoji, fontSize = 22.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = word.arabic,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isMatched) linaColors.onSuccessContainer else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LetterScrambleQuestionBody(
    question: GameQuestion.LetterScramble,
    chosenIndices: List<Int>,
    isAnswered: Boolean,
    isCorrect: Boolean,
    onTapLetter: (Int) -> Unit,
    onRemoveLetter: (Int) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    onSpeakEnglish: (String) -> Unit
) {
    val linaColors = LocalLinaColors.current
    val targetLen = question.scrambledLetters.size

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, linaColors.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = question.targetWord.emoji, fontSize = 64.sp)
                Text(
                    text = question.targetWord.arabic,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                FilledTonalButton(
                    onClick = { onSpeakEnglish(question.targetWord.english) },
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("استمع للكلمة", style = MaterialTheme.typography.labelLarge)
                }

                // Answer slots (LTR so English spelling flows Left-to-Right!)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (pos in 0 until targetLen) {
                            val chosenIdx = chosenIndices.getOrNull(pos)
                            val ch = chosenIdx?.let { question.scrambledLetters.getOrNull(it) }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when {
                                    isAnswered && isCorrect -> linaColors.successContainer
                                    isAnswered && !isCorrect -> linaColors.wrongContainer
                                    ch != null -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                border = BorderStroke(
                                    1.5.dp,
                                    if (ch != null) MaterialTheme.colorScheme.primary else linaColors.cardBorder
                                ),
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(enabled = !isAnswered && ch != null) {
                                        onRemoveLetter(pos)
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ch?.toString() ?: "",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontFamily = FredokaFontFamily,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                if (isAnswered && !isCorrect) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = "Correct spelling: ${question.targetWord.english}",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FredokaFontFamily,
                            color = linaColors.success
                        )
                    }
                }
            }
        }

        // Letter bank (LTR)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                question.scrambledLetters.forEachIndexed { idx, ch ->
                    val isUsed = idx in chosenIndices
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isUsed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        shadowElevation = if (isUsed) 0.dp else 3.dp,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !isAnswered && !isUsed) { onTapLetter(idx) }
                            .testTag("scramble_letter_$idx")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = ch.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FredokaFontFamily,
                                color = if (isUsed) {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                } else {
                                    MaterialTheme.colorScheme.onPrimary
                                }
                            )
                        }
                    }
                }
            }
        }

        if (!isAnswered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onClear,
                    enabled = chosenIndices.isNotEmpty(),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(Icons.Rounded.Backspace, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("مسح", style = MaterialTheme.typography.labelLarge)
                }
                Button(
                    onClick = onSubmit,
                    enabled = chosenIndices.size == targetLen,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                        .testTag("scramble_submit_button")
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("تحقق من الإجابة", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SentenceBuilderQuestionBody(
    question: GameQuestion.SentenceBuilder,
    chosenIndices: List<Int>,
    isAnswered: Boolean,
    isCorrect: Boolean,
    onTapToken: (Int) -> Unit,
    onRemoveToken: (Int) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    onSpeakEnglish: (String) -> Unit
) {
    val linaColors = LocalLinaColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, linaColors.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = question.arabicTranslation,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = when {
                        isAnswered && isCorrect -> linaColors.successContainer
                        isAnswered && !isCorrect -> linaColors.wrongContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        if (chosenIndices.isEmpty()) {
                            Text(
                                text = "اضغط على الكلمات بالأسفل لتكوين الجملة...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            )
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                chosenIndices.forEachIndexed { pos, idx ->
                                    val token = question.shuffledTokens[idx]
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable(enabled = !isAnswered) { onRemoveToken(pos) }
                                    ) {
                                        Text(
                                            text = token,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontFamily = FredokaFontFamily,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isAnswered) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = question.correctTokens.joinToString(" "),
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FredokaFontFamily,
                            color = linaColors.success,
                            textAlign = TextAlign.Center
                        )
                    }
                    FilledTonalButton(
                        onClick = { onSpeakEnglish(question.correctTokens.joinToString(" ")) }
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("استمع للجملة الصحيحة")
                    }
                }
            }
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                question.shuffledTokens.forEachIndexed { idx, token ->
                    val isUsed = idx in chosenIndices
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isUsed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        shadowElevation = if (isUsed) 0.dp else 2.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !isAnswered && !isUsed) { onTapToken(idx) }
                            .testTag("sentence_token_$idx")
                    ) {
                        Text(
                            text = token,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FredokaFontFamily,
                            color = if (isUsed) {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            },
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        if (!isAnswered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onClear,
                    enabled = chosenIndices.isNotEmpty(),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(Icons.Rounded.Backspace, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("إعادة")
                }
                Button(
                    onClick = onSubmit,
                    enabled = chosenIndices.size == question.shuffledTokens.size,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                        .testTag("sentence_submit_button")
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("تحقق من الجملة")
                }
            }
        }
    }
}

@Composable
private fun GameVictoryScreen(
    titleAr: String,
    isExam: Boolean,
    points: Int,
    correctCount: Int,
    totalQuestions: Int,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val linaColors = LocalLinaColors.current
    val percent = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0

    Scaffold(
        containerColor = linaColors.successContainer,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TeacherLinaCompanionBar(
                    messageAr = if (percent >= 70) {
                        "رائع جداً يا بطل! فخورة بك كثيراً! لقد حصلت على ميدالية جديدة! 🏆🎉"
                    } else {
                        "عمل رائع يا بطل! كل محاولة تجعلك أذكى وأقوى في الإنجليزية! 🌸"
                    },
                    mood = LinaMood.CELEBRATING
                )

                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_trophy_celebration_1795208959361),
                        contentDescription = stringResource(R.string.cd_trophy_celebration),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Text(
                    text = if (isExam) "أتممت الاختبار بنجاح! 🎓" else "أحسنت يا بطل! 🏅",
                    style = MaterialTheme.typography.headlineMedium,
                    color = linaColors.onSuccessContainer,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = titleAr,
                    style = MaterialTheme.typography.titleMedium,
                    color = linaColors.onSuccessContainer.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = linaColors.starContainer,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⭐ +$points نجمة جديدة",
                            style = MaterialTheme.typography.headlineSmall,
                            color = linaColors.onStarContainer
                        )
                        Text(
                            text = "إجابات صحيحة: $correctCount من $totalQuestions ($percent%)",
                            style = MaterialTheme.typography.titleMedium,
                            color = linaColors.onStarContainer
                        )
                    }
                }

                Button(
                    onClick = onExit,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("victory_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("العودة ومتابعة الرحلة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onPlayAgain,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("victory_replay_button")
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("العب بأسئلة جديدة متغيرة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            ConfettiShowerOverlay(
                triggerKey = "${titleAr}_${points}_${correctCount}",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
