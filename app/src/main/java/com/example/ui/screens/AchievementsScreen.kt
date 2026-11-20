package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BookUnit
import com.example.data.CurriculumRepository
import com.example.data.UnlockableCompanion
import com.example.data.local.UnitProgressEntity
import com.example.ui.components.TeacherLinaCompanionBar
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.LinaMood
import com.example.viewmodel.LinaUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    uiState: LinaUiState,
    onOpenUnit: (Int) -> Unit,
    onSelectCompanion: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val linaColors = LocalLinaColors.current
    val totalStars = uiState.profile.totalStars

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("🏆 إنجازاتي وشهاداتي", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("achievements_back_button")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            val isWide = maxWidth >= 620.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TeacherLinaCompanionBar(
                        messageAr = "أحسنت يا ${uiState.profile.childName}! لديك $totalStars نجمة و${uiState.completedUnitsCount} من 8 ميداليات للوحدات! استمر لتفتح جميع الشهادات والأصدقاء!",
                        mood = if (totalStars > 0) LinaMood.CELEBRATING else LinaMood.WELCOMING
                    )
                }

                // Summary Trophy Banner
                item {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = linaColors.starContainer),
                        border = BorderStroke(1.5.dp, linaColors.starAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_trophy_celebration_1795208959361),
                                contentDescription = "كأس الإنجازات",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(18.dp))
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "تقدمك في كتاب الصف الخامس",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = linaColors.onStarContainer
                                )
                                LinearProgressIndicator(
                                    progress = { uiState.completedUnitsCount / 8f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    color = linaColors.success,
                                    trackColor = MaterialTheme.colorScheme.surface,
                                    strokeCap = StrokeCap.Round
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Text(
                                        text = "⭐ $totalStars نجمة",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = linaColors.onStarContainer
                                    )
                                    Text(
                                        text = "🏅 ${uiState.completedUnitsCount}/8 ميداليات",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = linaColors.onStarContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // 1. Adventure Progress Map (خريطة التقدم)
                item {
                    Text(
                        text = "🗺️ خريطة التقدم وميداليات الوحدات",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    val columns = if (isWide) 4 else 2
                    val rows = uiState.units.chunked(columns)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        rows.forEach { rowUnits ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowUnits.forEach { unit ->
                                    ProgressMapUnitNode(
                                        unit = unit,
                                        progress = uiState.unitProgressMap[unit.number],
                                        accent = linaColors.unitAccents[(unit.number - 1) % linaColors.unitAccents.size],
                                        onClick = { onOpenUnit(unit.number) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                repeat(columns - rowUnits.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // 2. Certificates (الشهادات)
                item {
                    Text(
                        text = "🎓 شهادات التقدير والتفوق",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    val level1Done = uiState.unitProgressMap.values.count { it.level1Completed }
                    val level2Done = uiState.unitProgressMap.values.count { it.level2Completed }
                    val level3Done = uiState.unitProgressMap.values.count { it.level3Completed }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CertificateDiplomaCard(
                            titleAr = "شهادة المستوى الأول (المبتدئ 🌱)",
                            childName = uiState.profile.childName,
                            unlocked = uiState.level1CertificateUnlocked,
                            requirementAr = "أكمل المستوى الأول في 4 وحدات ($level1Done / 4)",
                            tag = "cert_level_1"
                        )
                        CertificateDiplomaCard(
                            titleAr = "شهادة المستوى الثاني (المتعلم ⭐)",
                            childName = uiState.profile.childName,
                            unlocked = uiState.level2CertificateUnlocked,
                            requirementAr = "أكمل المستوى الثاني في 4 وحدات ($level2Done / 4)",
                            tag = "cert_level_2"
                        )
                        CertificateDiplomaCard(
                            titleAr = "شهادة المستوى الثالث (العبقري 🏆)",
                            childName = uiState.profile.childName,
                            unlocked = uiState.level3CertificateUnlocked,
                            requirementAr = "أكمل المستوى الثالث في 4 وحدات ($level3Done / 4)",
                            tag = "cert_level_3"
                        )
                        CertificateDiplomaCard(
                            titleAr = "الشهادة الكبرى لإتمام كتاب English for Iraq 👑",
                            childName = uiState.profile.childName,
                            unlocked = uiState.fullBookCertificateUnlocked,
                            requirementAr = "أكمل الوحدات الـ 8 أو انجح في الاختبار الشامل للكتاب",
                            tag = "cert_full_book"
                        )
                    }
                }

                // 3. Unlockable Companions (الشخصيات القابلة للفتح)
                item {
                    Text(
                        text = "✨ أصدقاء المعلمة لينا (الشخصيات القابلة للفتح)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CurriculumRepository.companions.forEach { companion ->
                            val isUnlocked = totalStars >= companion.requiredStars
                            val isSelected = uiState.profile.selectedCompanionId == companion.id
                            CompanionUnlockCard(
                                companion = companion,
                                isUnlocked = isUnlocked,
                                isSelected = isSelected,
                                onSelect = {
                                    if (isUnlocked) onSelectCompanion(companion.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressMapUnitNode(
    unit: BookUnit,
    progress: UnitProgressEntity?,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val linaColors = LocalLinaColors.current
    val hasMedal = progress?.hasMedal == true

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (hasMedal) linaColors.successContainer else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (hasMedal) 1.5.dp else 1.dp,
            color = if (hasMedal) linaColors.success else linaColors.cardBorder
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag("map_unit_node_${unit.number}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accent,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = unit.icon, fontSize = 24.sp)
                }
            }

            Text(
                text = "الوحدة ${unit.number}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (hasMedal) "🏅 ميدالية مفتوحة" else "ابدأ المغامرة",
                style = MaterialTheme.typography.labelMedium,
                color = if (hasMedal) linaColors.onSuccessContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(if (progress?.level1Completed == true) "🌱✓" else "🌱", fontSize = 12.sp)
                Text(if (progress?.level2Completed == true) "⭐✓" else "⭐", fontSize = 12.sp)
                Text(if (progress?.level3Completed == true) "🏆✓" else "🏆", fontSize = 12.sp)
            }

            if ((progress?.bestScorePercent ?: 0) > 0) {
                Text(
                    text = "أفضل درجة: ${progress?.bestScorePercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FredokaFontFamily,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CertificateDiplomaCard(
    titleAr: String,
    childName: String,
    unlocked: Boolean,
    requirementAr: String,
    tag: String
) {
    val linaColors = LocalLinaColors.current
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) linaColors.starContainer else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (unlocked) 2.dp else 1.dp,
            color = if (unlocked) linaColors.starAccent else linaColors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (unlocked) Icons.Rounded.WorkspacePremium else Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = if (unlocked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = titleAr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (unlocked) linaColors.onStarContainer else MaterialTheme.colorScheme.onSurface
                )
                if (unlocked) {
                    Text(
                        text = "تُمنح للطالب المبدع: $childName — بتوقيع المعلمة لينا 🌟",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = linaColors.onStarContainer
                    )
                } else {
                    Text(
                        text = requirementAr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CompanionUnlockCard(
    companion: UnlockableCompanion,
    isUnlocked: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val linaColors = LocalLinaColors.current
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                isUnlocked -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else linaColors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(enabled = isUnlocked, onClick = onSelect)
            .testTag("companion_card_${companion.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = companion.emoji, fontSize = 30.sp)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = companion.nameAr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = companion.titleAr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!isUnlocked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = linaColors.starAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "يُفتح عند جمع ${companion.requiredStars} نجمة",
                            style = MaterialTheme.typography.labelMedium,
                            color = linaColors.onStarContainer
                        )
                    }
                }
            }

            when {
                isSelected -> {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "مُفعّل",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "مُختار",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                isUnlocked -> {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = linaColors.successContainer
                    ) {
                        Text(
                            text = "اختره",
                            style = MaterialTheme.typography.labelLarge,
                            color = linaColors.onSuccessContainer,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
                else -> {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "مغلق",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
