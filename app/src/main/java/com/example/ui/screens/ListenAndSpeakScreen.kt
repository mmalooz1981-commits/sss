package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.AudioPlaybackUiState
import com.example.audio.DialogueScript
import com.example.audio.PronunciationResult
import com.example.data.BookWord
import com.example.data.CurriculumRepository
import com.example.data.ListeningTrack
import com.example.ui.components.ConfettiBurstOverlay
import com.example.ui.components.TeacherLinaCompanionBar
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.LinaMood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenAndSpeakScreen(
    tracks: List<ListeningTrack>,
    dialogues: List<DialogueScript>,
    playbackState: AudioPlaybackUiState,
    isSpeaking: Boolean,
    isListening: Boolean,
    pronunciationResult: PronunciationResult?,
    pronunciationErrorAr: String?,
    onSpeakEnglish: (String) -> Unit,
    onPlayTrack: (ListeningTrack) -> Unit,
    onPracticeWord: (Int, String) -> Unit,
    onPlayFullDialogue: (DialogueScript) -> Unit,
    onPlayDialogueTurn: (DialogueScript, Int) -> Unit,
    onStartStudentRecording: () -> Unit,
    onStopStudentRecording: () -> Unit,
    onPlayStudentRecording: () -> Unit,
    onStopSpeaking: () -> Unit,
    onStartPronunciation: (String) -> Unit,
    onStopPronunciation: () -> Unit,
    onClearPronunciationResult: () -> Unit,
    onDismissAudioNotice: () -> Unit,
    onOpenAudioLibrary: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Tracks, 1 = Pronunciation Coach, 2 = Multi-speaker Dialogues & Stories
    var listeningCorrectTrigger by remember { mutableStateOf<String?>(null) }

    val confettiKey: Any? = when {
        selectedTab == 1 && pronunciationResult?.isSuccess == true ->
            "pron_${pronunciationResult.targetText}_${pronunciationResult.accuracyPercent}"
        selectedTab == 0 -> listeningCorrectTrigger
        else -> null
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("🔊 استمع وتدرّب على النطق", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onStopSpeaking()
                            onStopPronunciation()
                            onStopStudentRecording()
                            onBack()
                        },
                        modifier = Modifier.testTag("listen_speak_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenAudioLibrary,
                        modifier = Modifier.testTag("listen_open_audio_library_button")
                    ) {
                        Icon(Icons.Rounded.Tune, contentDescription = "إعدادات ومكتبة الأصوات")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.widthIn(max = 820.dp),
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            onStopPronunciation()
                            onStopStudentRecording()
                            selectedTab = 0
                        },
                        modifier = Modifier.testTag("tab_listening_tracks"),
                        text = { Text("🎧 الاستماع", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            onStopSpeaking()
                            selectedTab = 1
                        },
                        modifier = Modifier.testTag("tab_pronunciation_coach"),
                        text = { Text("🎤 النطق", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = {
                            onStopPronunciation()
                            onStopStudentRecording()
                            selectedTab = 2
                        },
                        modifier = Modifier.testTag("tab_dialogues_stories"),
                        text = { Text("💬 المحادثات", fontWeight = FontWeight.Bold) }
                    )
                }

                HybridPlaybackStatusBanner(
                    playbackState = playbackState,
                    onOpenAudioLibrary = onOpenAudioLibrary,
                    onDismissNotice = onDismissAudioNotice,
                    modifier = Modifier.widthIn(max = 820.dp)
                )

                when (selectedTab) {
                    0 -> ListeningTracksTab(
                        tracks = tracks,
                        isSpeaking = isSpeaking || playbackState.isPlaying,
                        onPlayTrack = onPlayTrack,
                        onStopSpeaking = onStopSpeaking,
                        onCorrectTrackAnswer = { trackNum ->
                            listeningCorrectTrigger = "track_${trackNum}_${System.currentTimeMillis()}"
                        }
                    )
                    1 -> PronunciationCoachTab(
                        playbackState = playbackState,
                        isSpeaking = isSpeaking || playbackState.isPlaying,
                        isListening = isListening,
                        pronunciationResult = pronunciationResult,
                        pronunciationErrorAr = pronunciationErrorAr,
                        onSpeakEnglish = onSpeakEnglish,
                        onPracticeWord = onPracticeWord,
                        onStartStudentRecording = onStartStudentRecording,
                        onStopStudentRecording = onStopStudentRecording,
                        onPlayStudentRecording = onPlayStudentRecording,
                        onStartPronunciation = onStartPronunciation,
                        onStopPronunciation = onStopPronunciation,
                        onClearResult = onClearPronunciationResult
                    )
                    else -> DialoguesAndStoriesTab(
                        dialogues = dialogues,
                        playbackState = playbackState,
                        onPlayFullDialogue = onPlayFullDialogue,
                        onPlayDialogueTurn = onPlayDialogueTurn,
                        onStopSpeaking = onStopSpeaking
                    )
                }
            }

            ConfettiBurstOverlay(
                triggerKey = confettiKey,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ListeningTracksTab(
    tracks: List<ListeningTrack>,
    isSpeaking: Boolean,
    onPlayTrack: (ListeningTrack) -> Unit,
    onStopSpeaking: () -> Unit,
    onCorrectTrackAnswer: (Int) -> Unit
) {
    var selectedUnitFilter by remember { mutableIntStateOf(0) } // 0 = All
    val answeredTracks = remember { mutableStateMapOf<Int, String>() }

    val filteredTracks = remember(tracks, selectedUnitFilter) {
        if (selectedUnitFilter == 0) tracks else tracks.filter { it.unitNumber == selectedUnitFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 820.dp)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TeacherLinaCompanionBar(
                messageAr = "هنا نصوص الاستماع الرسمية في كتاب English for Iraq (ص 110–124). استمع للنص ثم أجب عن السؤال!",
                mood = if (isSpeaking) LinaMood.HAPPY else LinaMood.EXPLAINING,
                isSpeaking = isSpeaking
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedUnitFilter == 0,
                        onClick = { selectedUnitFilter = 0 },
                        label = { Text("الكل (${tracks.size})") },
                        modifier = Modifier.testTag("track_filter_all")
                    )
                }
                items((1..8).toList()) { u ->
                    FilterChip(
                        selected = selectedUnitFilter == u,
                        onClick = { selectedUnitFilter = u },
                        label = { Text("الوحدة $u") },
                        modifier = Modifier.testTag("track_filter_unit_$u")
                    )
                }
            }
        }

        items(filteredTracks, key = { it.trackNumber }) { track ->
            ListeningTrackCard(
                track = track,
                selectedOption = answeredTracks[track.trackNumber],
                onSelectOption = { choice ->
                    val alreadyAnswered = answeredTracks.containsKey(track.trackNumber)
                    answeredTracks[track.trackNumber] = choice
                    if (!alreadyAnswered && choice == track.correctAnswer) {
                        onCorrectTrackAnswer(track.trackNumber)
                    }
                },
                onPlay = { onPlayTrack(track) },
                onStop = onStopSpeaking
            )
        }
    }
}

@Composable
private fun ListeningTrackCard(
    track: ListeningTrack,
    selectedOption: String?,
    onSelectOption: (String) -> Unit,
    onPlay: () -> Unit,
    onStop: () -> Unit
) {
    val linaColors = LocalLinaColors.current
    var showScript by remember(track.trackNumber) { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, linaColors.cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("listening_track_card_${track.trackNumber}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "🎧 Track ${track.trackNumber} • الوحدة ${track.unitNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = onPlay,
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("track_play_${track.trackNumber}"),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "تشغيل المقطع",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("استمع", style = MaterialTheme.typography.labelLarge)
                    }
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Stop,
                            contentDescription = "إيقاف الصوت",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = track.titleAr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = track.titleEn,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FredokaFontFamily,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = track.summaryAr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { showScript = !showScript },
                modifier = Modifier
                    .height(42.dp)
                    .testTag("track_toggle_script_${track.trackNumber}")
            ) {
                Text(if (showScript) "إخفاء نص الاستماع" else "عرض نص الاستماع (Script)")
            }

            AnimatedVisibility(visible = showScript) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = track.scriptEn,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FredokaFontFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = linaColors.starContainer.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "سؤال سريع على الاستماع:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = linaColors.onStarContainer
                    )
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = track.questionEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontFamily = FredokaFontFamily,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    track.options.forEach { option ->
                        val isChosen = selectedOption == option
                        val isCorrect = option == track.correctAnswer
                        val bgColor = when {
                            selectedOption == null -> MaterialTheme.colorScheme.surface
                            isCorrect -> linaColors.successContainer
                            isChosen -> linaColors.wrongContainer
                            else -> MaterialTheme.colorScheme.surface
                        }
                        val txtColor = when {
                            selectedOption == null -> MaterialTheme.colorScheme.onSurface
                            isCorrect -> linaColors.onSuccessContainer
                            isChosen -> linaColors.onWrongContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            border = BorderStroke(
                                width = if (isChosen || (selectedOption != null && isCorrect)) 1.5.dp else 1.dp,
                                color = when {
                                    selectedOption != null && isCorrect -> linaColors.success
                                    isChosen -> linaColors.wrong
                                    else -> linaColors.cardBorder
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectOption(option) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontFamily = FredokaFontFamily,
                                        color = txtColor
                                    )
                                }
                                if (selectedOption != null && isCorrect) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "إجابة صحيحة",
                                        tint = linaColors.success,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PronunciationCoachTab(
    playbackState: AudioPlaybackUiState,
    isSpeaking: Boolean,
    isListening: Boolean,
    pronunciationResult: PronunciationResult?,
    pronunciationErrorAr: String?,
    onSpeakEnglish: (String) -> Unit,
    onPracticeWord: (Int, String) -> Unit,
    onStartStudentRecording: () -> Unit,
    onStopStudentRecording: () -> Unit,
    onPlayStudentRecording: () -> Unit,
    onStartPronunciation: (String) -> Unit,
    onStopPronunciation: () -> Unit,
    onClearResult: () -> Unit
) {
    val context = LocalContext.current
    val linaColors = LocalLinaColors.current

    var selectedUnit by remember { mutableIntStateOf(1) }
    var practiceIndex by remember(selectedUnit) { mutableIntStateOf(0) }
    var permissionDeniedNote by remember { mutableStateOf(false) }

    val unitWords: List<BookWord> = remember(selectedUnit) {
        CurriculumRepository.wordsForUnit(selectedUnit).take(14)
    }
    val currentWord = unitWords.getOrNull(practiceIndex) ?: unitWords.firstOrNull()

    var pendingTargetToRecord by remember { mutableStateOf<String?>(null) }
    var pendingSelfCompareRecord by remember { mutableStateOf(false) }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            permissionDeniedNote = false
            if (pendingSelfCompareRecord) {
                onStartStudentRecording()
            } else {
                pendingTargetToRecord?.let { onStartPronunciation(it) }
            }
        } else {
            permissionDeniedNote = true
        }
        pendingTargetToRecord = null
        pendingSelfCompareRecord = false
    }

    fun requestRecordFor(targetText: String) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            permissionDeniedNote = false
            onStartPronunciation(targetText)
        } else {
            pendingSelfCompareRecord = false
            pendingTargetToRecord = targetText
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun requestSelfCompareRecording() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            permissionDeniedNote = false
            onStartStudentRecording()
        } else {
            pendingTargetToRecord = null
            pendingSelfCompareRecord = true
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 820.dp)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            val linaMsg = when {
                isListening -> "أنا أستمع إليك الآن! انطق الكلمة بصوت واضح يا بطل 🎤"
                pronunciationResult?.isSuccess == true -> "${pronunciationResult.feedbackAr} (+5 نجوم)"
                pronunciationResult != null -> pronunciationResult.feedbackAr
                else -> "استمع أولاً إلى نطقي الصحيح، ثم اضغط على زر الميكروفون وكرر الكلمة لنقارن نطقك!"
            }
            val mood = when {
                pronunciationResult?.isSuccess == true -> LinaMood.CELEBRATING
                pronunciationResult != null -> LinaMood.HINTING
                isListening -> LinaMood.HAPPY
                else -> LinaMood.EXPLAINING
            }
            TeacherLinaCompanionBar(
                messageAr = linaMsg,
                mood = mood,
                isSpeaking = isSpeaking || isListening,
                onSpeakMessage = currentWord?.let { { onSpeakEnglish(it.english) } }
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items((1..8).toList()) { u ->
                    FilterChip(
                        selected = selectedUnit == u,
                        onClick = {
                            selectedUnit = u
                            practiceIndex = 0
                            onClearResult()
                        },
                        label = { Text("الوحدة $u") },
                        modifier = Modifier.testTag("pronounce_unit_chip_$u")
                    )
                }
            }
        }

        if (currentWord != null) {
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, linaColors.cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pronunciation_practice_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = currentWord.emoji, fontSize = 68.sp)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                text = currentWord.english,
                                style = MaterialTheme.typography.displaySmall,
                                fontFamily = FredokaFontFamily,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text = currentWord.arabic,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (currentWord.exampleSentence.isNotBlank()) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Text(
                                        text = currentWord.exampleSentence,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FredokaFontFamily,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { onSpeakEnglish(currentWord.english) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("pronounce_listen_word_button")
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("اسمع الكلمة", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (isListening) {
                                        onStopPronunciation()
                                    } else {
                                        requestRecordFor(currentWord.english)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isListening) linaColors.wrong else MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("pronounce_record_button")
                            ) {
                                Icon(
                                    imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (isListening) "إيقاف" else "سجّل نطقك",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // 2-Second Guided Word Practice Sequence (Speak -> Pause 2s -> Repeat + Replay)
                        FilledTonalButton(
                            onClick = { onPracticeWord(selectedUnit, currentWord.english) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("pronounce_practice_repeat_button")
                        ) {
                            Icon(Icons.Rounded.Replay, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("تدريب الترديد (استماع + 2 ثانية + إعادة) 🔁", fontWeight = FontWeight.Bold)
                        }

                        if (currentWord.exampleSentence.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    if (isListening) onStopPronunciation()
                                    else requestRecordFor(currentWord.exampleSentence)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("pronounce_record_sentence_button")
                            ) {
                                Icon(Icons.Rounded.Hearing, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("تحدّي نطق الجملة كاملة 🎤")
                            }
                        }

                        // Student voice recording & honest self-comparison section
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, linaColors.cardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("student_self_compare_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🎙️ سجّل صوتك وقارنه بنطق المعلمة",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "استمع لصوتك بعد التسجيل وقارنه بالنموذج الصوتي للمعلمة لتطوير مخارج الحروف بصدق ووضوح (التعرف الآلي يساعد تقريبياً فقط، والمقارنة السمعية المباشرة هي الأدق).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (playbackState.isRecordingStudent) {
                                                onStopStudentRecording()
                                            } else {
                                                onStopPronunciation()
                                                requestSelfCompareRecording()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (playbackState.isRecordingStudent) linaColors.wrong else MaterialTheme.colorScheme.secondary
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("student_record_voice_button")
                                    ) {
                                        Icon(
                                            imageVector = if (playbackState.isRecordingStudent) Icons.Rounded.Stop else Icons.Rounded.Mic,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = if (playbackState.isRecordingStudent) "إيقاف التسجيل" else "سجّل صوتك",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    FilledTonalButton(
                                        onClick = onPlayStudentRecording,
                                        enabled = playbackState.hasStudentRecording && !playbackState.isRecordingStudent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("student_play_recording_button")
                                    ) {
                                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = if (playbackState.isPlayingStudentRecording) "جاري التشغيل..." else "اسمع تسجيلك",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (permissionDeniedNote) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = linaColors.wrongContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "يرجى منح صلاحية الميكروفون للتدرب على نطق الكلمات ومقارنتها مع المعلمة لينا.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = linaColors.onWrongContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        if (!pronunciationErrorAr.isNullOrBlank()) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = linaColors.starContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pronunciationErrorAr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = linaColors.onStarContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        if (pronunciationResult != null) {
            item {
                val resColor = if (pronunciationResult.isSuccess) linaColors.success else linaColors.wrong
                val resBg = if (pronunciationResult.isSuccess) linaColors.successContainer else linaColors.starContainer
                val resOnBg = if (pronunciationResult.isSuccess) linaColors.onSuccessContainer else linaColors.onStarContainer

                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = resBg),
                    border = BorderStroke(1.5.dp, resColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pronunciation_result_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "نتيجة مقارنة النطق",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = resOnBg
                            )
                            Text(
                                text = "${pronunciationResult.accuracyPercent}%",
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = FredokaFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = resColor
                            )
                        }

                        LinearProgressIndicator(
                            progress = { pronunciationResult.accuracyPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = resColor,
                            trackColor = MaterialTheme.colorScheme.surface,
                            strokeCap = StrokeCap.Round
                        )

                        Text(
                            text = "ما سمعته المعلمة لينا: \"${pronunciationResult.recognizedText}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = resOnBg
                        )
                        Text(
                            text = pronunciationResult.feedbackAr,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = resOnBg
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "اختر كلمة أخرى للتدريب من الوحدة $selectedUnit:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(unitWords.indices.toList()) { idx ->
            val word = unitWords[idx]
            val selected = idx == practiceIndex
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = if (selected) 1.5.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else linaColors.cardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        practiceIndex = idx
                        onClearResult()
                        onSpeakEnglish(word.english)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = word.emoji, fontSize = 26.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                text = word.english,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FredokaFontFamily,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = word.arabic,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onSpeakEnglish(word.english) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "استمع",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
