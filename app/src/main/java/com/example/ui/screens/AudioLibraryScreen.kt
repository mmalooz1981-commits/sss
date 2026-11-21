package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.audio.AudioDownloadProgress
import com.example.audio.AudioPlaybackUiState
import com.example.audio.CurriculumAudioCatalog
import com.example.audio.SpeechMode
import com.example.audio.UnitAudioStats
import com.example.audio.VoiceAccent
import com.example.audio.VoiceEnginePreference
import com.example.audio.VoicePersona
import com.example.auth.FirebaseAuthHelper
import com.example.data.BookUnit
import com.example.ui.components.TeacherLinaCompanionBar
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.LinaMood
import com.example.viewmodel.LinaUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioLibraryScreen(
    uiState: LinaUiState,
    playbackState: AudioPlaybackUiState,
    downloadProgress: AudioDownloadProgress?,
    onSelectAccent: (VoiceAccent) -> Unit,
    onSelectPersona: (VoicePersona) -> Unit,
    onSelectSpeechMode: (SpeechMode) -> Unit,
    onSelectEngine: (VoiceEnginePreference) -> Unit,
    onSaveCloudEndpoint: (String) -> Unit,
    onPreviewPersona: (VoicePersona) -> Unit,
    onDownloadUnit: (Int) -> Unit,
    onDownloadAll: () -> Unit,
    onPauseDownload: () -> Unit,
    onDeleteUnitAudio: (Int) -> Unit,
    onDeleteAllAudio: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val linaColors = LocalLinaColors.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var endpointInput by remember(uiState.voiceSettings.cloudTtsEndpointUrl) {
        mutableStateOf(uiState.voiceSettings.cloudTtsEndpointUrl)
    }
    var endpointSavedNotice by remember { mutableStateOf(false) }

    var signedInEmail by remember { mutableStateOf(FirebaseAuthHelper.currentUser()?.email) }
    var authStatusMessage by remember { mutableStateOf<String?>(null) }
    var isAuthBusy by remember { mutableStateOf(false) }

    var lastRequestedUnit by remember { mutableIntStateOf(0) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var unitPendingDelete by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        signedInEmail = FirebaseAuthHelper.currentUser()?.email
    }

    val totalCatalogItems = CurriculumAudioCatalog.allItems.size
    val totalUniqueDownloaded = uiState.unitAudioStats.values.sumOf { it.downloadedItems }
    val overallPercent = if (totalCatalogItems <= 0) 0 else ((totalUniqueDownloaded * 100) / totalCatalogItems).coerceIn(0, 100)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🎙️ مكتبة الأصوات الذكية",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("audio_library_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(key = "lina_audio_intro") {
                    TeacherLinaCompanionBar(
                        messageAr = "اختر اللهجة البريطانية أو الأمريكية، وحدّد شخصيتك الصوتية المفضلة، وحمّل أصوات الوحدات لتعمل دون إنترنت في أي وقت!",
                        mood = if (playbackState.isPlaying || playbackState.isLoadingCloud) LinaMood.HAPPY else LinaMood.WELCOMING,
                        isSpeaking = playbackState.isPlaying
                    )
                }

                // Live synthesis / playback feedback banner
                if (playbackState.isLoadingCloud || !playbackState.noticeMessageAr.isNullOrBlank()) {
                    item(key = "audio_live_notice") {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (playbackState.isOfflineMissingNotice) linaColors.wrongContainer else linaColors.starContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (playbackState.isLoadingCloud) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                                }
                                Text(
                                    text = playbackState.noticeMessageAr
                                        ?: playbackState.activeSourceLabelAr
                                        ?: "جاري تجهيز الصوت...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (playbackState.isOfflineMissingNotice) linaColors.onWrongContainer else linaColors.onStarContainer,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Storage & overall download overview
                item(key = "audio_storage_summary") {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("audio_storage_summary_card")
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
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "التخزين المحلي للأصوات (دون إنترنت)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "المحفوظ: $totalUniqueDownloaded / $totalCatalogItems مقطع • الحجم: ${formatBytes(uiState.totalDownloadedAudioBytes)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                                Text(
                                    text = "$overallPercent%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            LinearProgressIndicator(
                                progress = { overallPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                strokeCap = StrokeCap.Round
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        lastRequestedUnit = 0
                                        onDownloadAll()
                                    },
                                    enabled = downloadProgress?.isRunning != true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("download_all_audio_button")
                                ) {
                                    Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("تنزيل جميع الأصوات", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }

                                OutlinedButton(
                                    onClick = { showDeleteAllDialog = true },
                                    enabled = uiState.totalDownloadedAudioCount > 0 && downloadProgress?.isRunning != true,
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("delete_all_audio_button")
                                ) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "حذف الكل", modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("حذف الكل")
                                }
                            }
                        }
                    }
                }

                // Active WorkManager background download progress card
                if (downloadProgress != null) {
                    item(key = "active_download_banner") {
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("active_download_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (downloadProgress.unitNumber in 1..8) {
                                            "تنزيل أصوات الوحدة ${downloadProgress.unitNumber}"
                                        } else {
                                            "تنزيل أصوات المنهج الكامل"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${downloadProgress.percent}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FredokaFontFamily,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (downloadProgress.totalCount > 0) {
                                    LinearProgressIndicator(
                                        progress = { downloadProgress.percent / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        strokeCap = StrokeCap.Round
                                    )
                                }

                                Text(
                                    text = downloadProgress.statusMessageAr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (downloadProgress.currentTitleAr.isNotBlank() && downloadProgress.isRunning) {
                                    Text(
                                        text = "الحالي: ${downloadProgress.currentTitleAr}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    if (downloadProgress.isRunning) {
                                        OutlinedButton(
                                            onClick = onPauseDownload,
                                            modifier = Modifier
                                                .height(48.dp)
                                                .testTag("pause_download_button")
                                        ) {
                                            Icon(Icons.Rounded.Pause, contentDescription = null)
                                            Spacer(Modifier.width(6.dp))
                                            Text("إيقاف مؤقت")
                                        }
                                    } else {
                                        FilledTonalButton(
                                            onClick = {
                                                val target = if (downloadProgress.unitNumber in 0..8) {
                                                    downloadProgress.unitNumber
                                                } else {
                                                    lastRequestedUnit
                                                }
                                                if (target == 0) onDownloadAll() else onDownloadUnit(target)
                                            },
                                            modifier = Modifier
                                                .height(48.dp)
                                                .testTag("resume_download_button")
                                        ) {
                                            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                                            Spacer(Modifier.width(6.dp))
                                            Text("استئناف التنزيل")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Accent selection (en-GB primary, en-US optional)
                item(key = "accent_section") {
                    SectionCard(title = "🌍 لهجة النطق الإنجليزي") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            VoiceAccent.entries.forEach { accent ->
                                val selected = uiState.voiceAccent == accent
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary else linaColors.cardBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { onSelectAccent(accent) }
                                        .testTag("accent_option_${accent.code}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = accent.flagEmoji, fontSize = 28.sp)
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            Text(
                                                text = accent.code,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontFamily = FredokaFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            text = accent.labelAr,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4 Voice Personas with live Preview buttons
                item(key = "personas_section") {
                    SectionCard(title = "🎭 الشخصيات الصوتية التعليمية") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            VoicePersona.entries.forEach { persona ->
                                val selected = uiState.voicePersona == persona
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary else linaColors.cardBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { onSelectPersona(persona) }
                                        .testTag("persona_card_${persona.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(MaterialTheme.colorScheme.secondaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = persona.emoji, fontSize = 26.sp)
                                        }

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                    Text(
                                                        text = persona.displayNameEn,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontFamily = FredokaFontFamily,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Text(
                                                    text = "• ${persona.titleAr}",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Text(
                                                text = persona.roleDescriptionAr,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        FilledTonalButton(
                                            onClick = {
                                                onSelectPersona(persona)
                                                onPreviewPersona(persona)
                                            },
                                            contentPadding = PaddingValues(horizontal = 12.dp),
                                            modifier = Modifier
                                                .height(48.dp)
                                                .testTag("preview_persona_${persona.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                                contentDescription = "تجربة صوت ${persona.displayNameEn}",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text("جرّب")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3 Speech Modes (Slow / Normal / Practice)
                item(key = "speech_mode_section") {
                    SectionCard(title = "⏱️ أوضاع سرعة وتدريب النطق") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpeechMode.entries.forEach { mode ->
                                val selected = uiState.speechMode == mode
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = if (selected) linaColors.successContainer else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) linaColors.success else linaColors.cardBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { onSelectSpeechMode(mode) }
                                        .testTag("speech_mode_${mode.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(text = mode.emoji, fontSize = 24.sp)
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = mode.labelAr,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = mode.descriptionAr,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = "محدد",
                                                tint = linaColors.success
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Engine preference & Secure Backend / Google Sign-In
                item(key = "engine_security_section") {
                    SectionCard(title = "☁️ محرك توليد الأصوات الذكي والأمان") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            VoiceEnginePreference.entries.forEach { pref ->
                                val selected = uiState.voiceEnginePreference == pref
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        width = if (selected) 1.8.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.secondary else linaColors.cardBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { onSelectEngine(pref) }
                                        .testTag("engine_pref_${pref.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = pref.labelAr,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = pref.descriptionAr,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Google Sign-In via Jetpack Credential Manager for Cloud Function auth
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Security,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "المصادقة الآمنة للخادم (بدون مفاتيح API داخل التطبيق)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "يعمل Gemini TTS عبر Firebase AI Logic + App Check مباشرة. ولتفعيل Google Cloud Chirp 3 HD عبر Cloud Function، سجّل الدخول بحساب Google وأضف رابط HTTPS للخادم.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (signedInEmail != null) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.AccountCircle,
                                                    contentDescription = null,
                                                    tint = linaColors.success
                                                )
                                                Text(
                                                    text = signedInEmail.orEmpty(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    isAuthBusy = true
                                                    FirebaseAuthHelper.signOut(credentialManager, scope) {
                                                        signedInEmail = null
                                                        authStatusMessage = "تم تسجيل الخروج بنجاح."
                                                        isAuthBusy = false
                                                    }
                                                },
                                                enabled = !isAuthBusy,
                                                modifier = Modifier
                                                    .height(48.dp)
                                                    .testTag("google_sign_out_button")
                                            ) {
                                                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("خروج")
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                isAuthBusy = true
                                                authStatusMessage = null
                                                FirebaseAuthHelper.onGoogleSignInClicked(
                                                    context = context,
                                                    credentialManager = credentialManager,
                                                    scope = scope,
                                                    onAuthSuccess = {
                                                        signedInEmail = FirebaseAuthHelper.currentUser()?.email ?: "حساب Google موثّق"
                                                        authStatusMessage = "تم تسجيل الدخول بحساب Google بنجاح ✓"
                                                        isAuthBusy = false
                                                    },
                                                    onAuthError = { err ->
                                                        authStatusMessage = err
                                                        isAuthBusy = false
                                                    },
                                                    onAuthCancelled = {
                                                        isAuthBusy = false
                                                    }
                                                )
                                            },
                                            enabled = !isAuthBusy,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("google_sign_in_button")
                                        ) {
                                            Icon(Icons.Rounded.AccountCircle, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text(if (isAuthBusy) "جاري تسجيل الدخول..." else "تسجيل الدخول بحساب Google (لخادم Chirp 3 HD)")
                                        }
                                    }

                                    AnimatedVisibility(visible = !authStatusMessage.isNullOrBlank()) {
                                        Text(
                                            text = authStatusMessage.orEmpty(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        OutlinedTextField(
                                            value = endpointInput,
                                            onValueChange = {
                                                endpointInput = it
                                                endpointSavedNotice = false
                                            },
                                            label = { Text("Cloud Function HTTPS URL (اختياري لـ Chirp 3 HD)") },
                                            placeholder = { Text("https://us-central1-your-project.cloudfunctions.net/synthesizeChirp3Hd") },
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("cloud_tts_endpoint_input")
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        FilledTonalButton(
                                            onClick = {
                                                onSaveCloudEndpoint(endpointInput)
                                                endpointSavedNotice = true
                                            },
                                            modifier = Modifier
                                                .height(48.dp)
                                                .testTag("save_cloud_endpoint_button")
                                        ) {
                                            Text("حفظ رابط الخادم")
                                        }
                                        if (endpointSavedNotice) {
                                            Text(
                                                text = "تم الحفظ ✓",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = linaColors.success
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Per-unit audio packs header
                item(key = "units_audio_header") {
                    Text(
                        text = "📦 حزم أصوات الوحدات (الوحدة 1 — الوحدة 8)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(uiState.units, key = { "audio_unit_${it.number}" }) { unit ->
                    val stats = uiState.unitAudioStats[unit.number] ?: UnitAudioStats(
                        unitNumber = unit.number,
                        totalItems = CurriculumAudioCatalog.itemsForUnit(unit.number).size,
                        downloadedItems = 0,
                        totalSizeBytes = 0L
                    )
                    val isDownloadingThisUnit = downloadProgress?.isRunning == true &&
                        (downloadProgress.unitNumber == unit.number || downloadProgress.unitNumber == 0)

                    UnitAudioPackCard(
                        unit = unit,
                        stats = stats,
                        isDownloadingThisUnit = isDownloadingThisUnit,
                        isAnyDownloadRunning = downloadProgress?.isRunning == true,
                        onDownload = {
                            lastRequestedUnit = unit.number
                            onDownloadUnit(unit.number)
                        },
                        onPause = onPauseDownload,
                        onDelete = { unitPendingDelete = unit.number }
                    )
                }

                item(key = "audio_library_bottom_spacer") {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("حذف جميع الأصوات المحفوظة؟") },
            text = { Text("سيتم حذف جميع الملفات الصوتية المخزنة محلياً لتحرير المساحة، ولن يتأثر تقدم الطفل أو نجومه.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAllDialog = false
                        onDeleteAllAudio()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف الكل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    unitPendingDelete?.let { targetUnit ->
        AlertDialog(
            onDismissRequest = { unitPendingDelete = null },
            title = { Text("حذف أصوات الوحدة $targetUnit؟") },
            text = { Text("يمكنك إعادة تنزيل أصوات الوحدة $targetUnit لاحقاً في أي وقت.") },
            confirmButton = {
                Button(
                    onClick = {
                        unitPendingDelete = null
                        onDeleteUnitAudio(targetUnit)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { unitPendingDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    val linaColors = LocalLinaColors.current
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, linaColors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            content()
        }
    }
}

@Composable
private fun UnitAudioPackCard(
    unit: BookUnit,
    stats: UnitAudioStats,
    isDownloadingThisUnit: Boolean,
    isAnyDownloadRunning: Boolean,
    onDownload: () -> Unit,
    onPause: () -> Unit,
    onDelete: () -> Unit
) {
    val linaColors = LocalLinaColors.current
    val accentBg = linaColors.unitAccents[(unit.number - 1) % linaColors.unitAccents.size]
    val isComplete = stats.totalItems > 0 && stats.downloadedItems >= stats.totalItems

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (isComplete) 1.5.dp else 1.dp,
            color = if (isComplete) linaColors.success else linaColors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("unit_audio_pack_card_${unit.number}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = unit.icon, fontSize = 24.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الوحدة ${unit.number}: ${unit.titleAr}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${stats.downloadedItems} / ${stats.totalItems} مقطع • ${formatBytes(stats.totalSizeBytes)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isComplete) {
                    Icon(
                        imageVector = Icons.Rounded.CloudDone,
                        contentDescription = "مكتمل محلياً",
                        tint = linaColors.success,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    Text(
                        text = "${stats.completionPercent}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FredokaFontFamily,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            LinearProgressIndicator(
                progress = { stats.completionPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isComplete) linaColors.success else MaterialTheme.colorScheme.primary,
                strokeCap = StrokeCap.Round
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isDownloadingThisUnit) {
                    OutlinedButton(
                        onClick = onPause,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("unit_pause_audio_${unit.number}")
                    ) {
                        Icon(Icons.Rounded.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("إيقاف مؤقت")
                    }
                } else {
                    FilledTonalButton(
                        onClick = onDownload,
                        enabled = !isAnyDownloadRunning,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("unit_download_audio_${unit.number}")
                    ) {
                        Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            when {
                                isComplete -> "تحديث / تحقق"
                                stats.downloadedItems > 0 -> "استئناف التنزيل"
                                else -> "تنزيل الوحدة ${unit.number}"
                            }
                        )
                    }
                }

                if (stats.downloadedItems > 0 || stats.totalSizeBytes > 0L) {
                    OutlinedButton(
                        onClick = onDelete,
                        enabled = !isAnyDownloadRunning,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("unit_delete_audio_${unit.number}")
                    ) {
                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "حذف أصوات الوحدة ${unit.number}", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("حذف")
                    }
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 KB"
    val kb = bytes / 1024.0
    return if (kb < 1024.0) {
        String.format(Locale.US, "%.0f KB", kb.coerceAtLeast(1.0))
    } else {
        String.format(Locale.US, "%.2f MB", kb / 1024.0)
    }
}
