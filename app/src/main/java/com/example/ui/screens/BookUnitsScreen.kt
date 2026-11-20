package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookLesson
import com.example.data.BookUnit
import com.example.data.DifficultyLevel
import com.example.data.local.UnitProgressEntity
import com.example.ui.components.TeacherLinaCompanionBar
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.LinaMood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookUnitsScreen(
    units: List<BookUnit>,
    unitProgressMap: Map<Int, UnitProgressEntity>,
    onUnitClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val linaColors = LocalLinaColors.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("📚 كتابي — English for Iraq", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("book_units_back_button")) {
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
            val isWide = maxWidth >= 600.dp

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
                        messageAr = "هذه جميع وحدات كتاب الصف الخامس الابتدائي الثماني! اختر أي وحدة لنتعلم دروسها وقواعدها ونلعب معاً!",
                        mood = LinaMood.WELCOMING
                    )
                }

                if (isWide) {
                    val pairs = units.chunked(2)
                    items(pairs.size) { rowIdx ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pairs[rowIdx].forEach { unit ->
                                UnitDirectoryCard(
                                    unit = unit,
                                    progress = unitProgressMap[unit.number],
                                    accent = linaColors.unitAccents[(unit.number - 1) % linaColors.unitAccents.size],
                                    onClick = { onUnitClick(unit.number) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pairs[rowIdx].size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                } else {
                    items(units, key = { it.number }) { unit ->
                        UnitDirectoryCard(
                            unit = unit,
                            progress = unitProgressMap[unit.number],
                            accent = linaColors.unitAccents[(unit.number - 1) % linaColors.unitAccents.size],
                            onClick = { onUnitClick(unit.number) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitDirectoryCard(
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(if (hasMedal) 1.5.dp else 1.dp, if (hasMedal) linaColors.success else linaColors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag("book_unit_item_${unit.number}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Text(text = unit.icon, fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = "Unit ${unit.number}",
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = FredokaFontFamily,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "${unit.lessons.size} دروس • صفحة ${unit.startPage}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = unit.titleEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FredokaFontFamily,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
                Text(
                    text = unit.titleAr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LevelCheckDot("🌱", progress?.level1Completed == true)
                    LevelCheckDot("⭐", progress?.level2Completed == true)
                    LevelCheckDot("🏆", progress?.level3Completed == true)
                }
            }

            if (hasMedal) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "وحدة مكتملة",
                    tint = linaColors.success,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun LevelCheckDot(emoji: String, completed: Boolean) {
    val linaColors = LocalLinaColors.current
    Surface(
        shape = RoundedCornerShape(50),
        color = if (completed) linaColors.successContainer else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = if (completed) "$emoji ✓" else emoji,
            style = MaterialTheme.typography.labelMedium,
            color = if (completed) linaColors.onSuccessContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDetailScreen(
    unit: BookUnit,
    progress: UnitProgressEntity?,
    onStartGame: (DifficultyLevel) -> Unit,
    onStartFlashcards: () -> Unit,
    onStartUnitExam: () -> Unit,
    onSpeakEnglish: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val linaColors = LocalLinaColors.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = "Unit ${unit.number}: ${unit.titleEn}",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FredokaFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("unit_detail_back_button")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
                    .widthIn(max = 760.dp)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    TeacherLinaCompanionBar(
                        messageAr = "مرحباً بك في الوحدة ${unit.number} (${unit.titleAr})! اقرأ الدروس واستمع للنطق ثم اختر مستواك للعب وجمع النجوم!",
                        mood = LinaMood.EXPLAINING,
                        onSpeakMessage = { onSpeakEnglish("Unit ${unit.number}. ${unit.titleEn}") }
                    )
                }

                item {
                    Text(
                        text = "🎮 العب مستويات الوحدة",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DifficultyLevel.entries.forEach { level ->
                            val done = when (level) {
                                DifficultyLevel.BEGINNER -> progress?.level1Completed == true
                                DifficultyLevel.LEARNER -> progress?.level2Completed == true
                                DifficultyLevel.GENIUS -> progress?.level3Completed == true
                            }
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(
                                    1.2.dp,
                                    if (done) linaColors.success else linaColors.cardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { onStartGame(level) }
                                    .testTag("unit_play_level_${level.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(text = level.emoji, fontSize = 28.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = level.titleAr,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = level.descriptionAr,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (done) linaColors.success else MaterialTheme.colorScheme.primary
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.PlayArrow,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = if (done) "العب مجدداً" else "ابدأ",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = onStartFlashcards,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("unit_flashcards_button")
                        ) {
                            Icon(Icons.Rounded.Style, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("بطاقات الكلمات", style = MaterialTheme.typography.labelLarge)
                        }
                        Button(
                            onClick = onStartUnitExam,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("unit_exam_button")
                        ) {
                            Icon(Icons.Rounded.Assignment, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("اختبار الوحدة", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                item {
                    Text(
                        text = "📖 دروس وقواعد وقصص الوحدة (${unit.lessons.size} دروس)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(unit.lessons, key = { "${it.unitNumber}_${it.lessonNumber}" }) { lesson ->
                    LessonContentCard(
                        lesson = lesson,
                        onSpeakEnglish = onSpeakEnglish
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonContentCard(
    lesson: BookLesson,
    onSpeakEnglish: (String) -> Unit
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "الدرس ${lesson.lessonNumber} • ص ${lesson.pageNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                IconButton(
                    onClick = { onSpeakEnglish(lesson.titleEn) },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "استمع لعنوان الدرس",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = lesson.titleEn,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FredokaFontFamily,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

            Text(
                text = lesson.titleAr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = lesson.summaryAr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            lesson.grammarRule?.let { rule ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = linaColors.starContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "📌 ${rule.titleAr}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = linaColors.onStarContainer
                        )
                        Text(
                            text = rule.explanationAr,
                            style = MaterialTheme.typography.bodyMedium,
                            color = linaColors.onStarContainer
                        )
                        rule.examples.forEach { (en, ar) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSpeakEnglish(en) }
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = en,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = ar,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = linaColors.onStarContainer
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = "استمع للمثال",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (lesson.keySentences.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    lesson.keySentences.forEach { (en, ar) ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .clickable { onSpeakEnglish(en) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = en,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = ar,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = "استمع للجملة",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (lesson.readingTextEn != null) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📰 ${lesson.readingTitle ?: "نص القراءة"}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            FilledTonalButton(
                                onClick = { onSpeakEnglish(lesson.readingTextEn) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("اقرئي يا لينا", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                text = lesson.readingTextEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
