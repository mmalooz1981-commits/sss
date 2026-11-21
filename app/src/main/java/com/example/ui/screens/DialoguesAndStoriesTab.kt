package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackUiState
import com.example.audio.DialogueScript
import com.example.ui.components.TeacherLinaCompanionBar
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.LinaMood

@Composable
fun HybridPlaybackStatusBanner(
    playbackState: AudioPlaybackUiState,
    onOpenAudioLibrary: () -> Unit,
    onDismissNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val linaColors = LocalLinaColors.current
    val showBanner = playbackState.isLoadingCloud ||
        !playbackState.practicePhaseLabelAr.isNullOrBlank() ||
        !playbackState.noticeMessageAr.isNullOrBlank()

    if (!showBanner) return

    val bgColor = when {
        playbackState.isOfflineMissingNotice -> linaColors.wrongContainer
        !playbackState.practicePhaseLabelAr.isNullOrBlank() -> linaColors.successContainer
        else -> linaColors.starContainer
    }
    val fgColor = when {
        playbackState.isOfflineMissingNotice -> linaColors.onWrongContainer
        !playbackState.practicePhaseLabelAr.isNullOrBlank() -> linaColors.onSuccessContainer
        else -> linaColors.onStarContainer
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = bgColor,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("hybrid_playback_status_banner")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (playbackState.isLoadingCloud) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                }
                val message = playbackState.practicePhaseLabelAr
                    ?: playbackState.noticeMessageAr
                    ?: playbackState.activeSourceLabelAr
                    ?: "جاري تجهيز الصوت..."
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = fgColor,
                    modifier = Modifier.weight(1f)
                )
            }

            if (playbackState.isOfflineMissingNotice || !playbackState.noticeMessageAr.isNullOrBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (playbackState.isOfflineMissingNotice) {
                        FilledTonalButton(
                            onClick = onOpenAudioLibrary,
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("banner_open_audio_library_button")
                        ) {
                            Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("فتح مكتبة الأصوات")
                        }
                    }
                    TextButton(onClick = onDismissNotice) {
                        Text("إغلاق")
                    }
                }
            }
        }
    }
}

@Composable
fun DialoguesAndStoriesTab(
    dialogues: List<DialogueScript>,
    playbackState: AudioPlaybackUiState,
    onPlayFullDialogue: (DialogueScript) -> Unit,
    onPlayDialogueTurn: (DialogueScript, Int) -> Unit,
    onStopSpeaking: () -> Unit
) {
    var selectedUnitFilter by remember { mutableIntStateOf(0) }
    val filtered = remember(dialogues, selectedUnitFilter) {
        if (selectedUnitFilter == 0) dialogues else dialogues.filter { it.unitNumber == selectedUnitFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 820.dp)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "dialogues_lina_bar") {
            TeacherLinaCompanionBar(
                messageAr = "استمع للمحادثات والقصص بأصوات شخصيات متعددة (Miss Lily, Miss Emma, Mr. John, Kids Voice)! اضغط لتشغيل المحادثة كاملة أو اختر أي جملة مفردة.",
                mood = if (playbackState.isPlaying) LinaMood.HAPPY else LinaMood.EXPLAINING,
                isSpeaking = playbackState.isPlaying
            )
        }

        item(key = "dialogues_unit_filter") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedUnitFilter == 0,
                        onClick = { selectedUnitFilter = 0 },
                        label = { Text("الكل (${dialogues.size})") },
                        modifier = Modifier.testTag("dialogue_filter_all")
                    )
                }
                items((1..8).toList()) { u ->
                    FilterChip(
                        selected = selectedUnitFilter == u,
                        onClick = { selectedUnitFilter = u },
                        label = { Text("الوحدة $u") },
                        modifier = Modifier.testTag("dialogue_filter_unit_$u")
                    )
                }
            }
        }

        items(filtered, key = { it.id }) { script ->
            DialogueScriptCard(
                script = script,
                activeDialogueId = playbackState.activeDialogueId,
                activeTurnIndex = playbackState.activeDialogueTurnIndex,
                onPlayFull = { onPlayFullDialogue(script) },
                onPlayTurn = { idx -> onPlayDialogueTurn(script, idx) },
                onStop = onStopSpeaking
            )
        }
    }
}

@Composable
private fun DialogueScriptCard(
    script: DialogueScript,
    activeDialogueId: String?,
    activeTurnIndex: Int,
    onPlayFull: () -> Unit,
    onPlayTurn: (Int) -> Unit,
    onStop: () -> Unit
) {
    val linaColors = LocalLinaColors.current
    val isScriptPlaying = activeDialogueId == script.id
    val distinctPersonas = remember(script.id) { script.turns.map { it.persona }.distinct() }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (isScriptPlaying) 1.8.dp else 1.dp,
            color = if (isScriptPlaying) MaterialTheme.colorScheme.primary else linaColors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dialogue_script_card_${script.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "الوحدة ${script.unitNumber} • ${script.turns.size} جمل",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = onPlayFull,
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("play_full_dialogue_${script.id}")
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("تشغيل الكل")
                    }
                    if (isScriptPlaying) {
                        IconButton(
                            onClick = onStop,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Rounded.Stop, contentDescription = "إيقاف")
                        }
                    }
                }
            }

            Text(
                text = script.titleAr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = script.titleEn,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FredokaFontFamily,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(distinctPersonas, key = { it.id }) { persona ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${persona.emoji} ${persona.displayNameEn}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                script.turns.forEachIndexed { idx, turn ->
                    val isActiveTurn = isScriptPlaying && activeTurnIndex == idx
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isActiveTurn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        border = BorderStroke(
                            width = if (isActiveTurn) 1.5.dp else 1.dp,
                            color = if (isActiveTurn) MaterialTheme.colorScheme.primary else linaColors.cardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onPlayTurn(idx) }
                            .testTag("dialogue_turn_${script.id}_$idx")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = turn.persona.emoji, fontSize = 24.sp)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "${turn.speakerName} (${turn.persona.displayNameEn})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Text(
                                        text = turn.textEn,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontFamily = FredokaFontFamily,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (turn.arabicNote.isNotBlank()) {
                                    Text(
                                        text = turn.arabicNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = { onPlayTurn(idx) },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = "تشغيل هذه الجملة",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
