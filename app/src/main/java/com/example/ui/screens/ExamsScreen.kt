package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.WorkspacePremium
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookUnit
import com.example.data.local.ExamResultEntity
import com.example.data.local.UnitProgressEntity
import com.example.ui.components.TeacherLinaCompanionBar
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.LinaMood
import com.example.viewmodel.LinaUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    uiState: LinaUiState,
    onStartUnitExam: (Int) -> Unit,
    onStartBookExam: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val linaColors = LocalLinaColors.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("📝 اختبر نفسك", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("exams_back_button")) {
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    TeacherLinaCompanionBar(
                        messageAr = "اختر اختباراً نهائياً لأي وحدة أو خُض الاختبار الشامل لكتاب English for Iraq بالكامل! الأسئلة تتنوع في كل محاولة.",
                        mood = LinaMood.EXPLAINING
                    )
                }

                // Comprehensive Full-Book Exam Card
                item {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("comprehensive_book_exam_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.WorkspacePremium,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(34.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "🏆 الاختبار الشامل للكتاب كاملاً",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "16 سؤالاً متنوعاً من جميع الوحدات الـ 8 (مفردات، قواعد، حروف، جمل، محادثة، واستماع)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Button(
                                onClick = onStartBookExam,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("start_comprehensive_exam_button")
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "ابدأ الاختبار الشامل الآن",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "📚 الاختبارات النهائية للوحدات (10 أسئلة لكل وحدة)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isWide) {
                    val rows = uiState.units.chunked(2)
                    items(rows.size) { rowIndex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rows[rowIndex].forEach { unit ->
                                UnitExamCard(
                                    unit = unit,
                                    progress = uiState.unitProgressMap[unit.number],
                                    accent = linaColors.unitAccents[(unit.number - 1) % linaColors.unitAccents.size],
                                    onStart = { onStartUnitExam(unit.number) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rows[rowIndex].size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                } else {
                    items(uiState.units, key = { it.number }) { unit ->
                        UnitExamCard(
                            unit = unit,
                            progress = uiState.unitProgressMap[unit.number],
                            accent = linaColors.unitAccents[(unit.number - 1) % linaColors.unitAccents.size],
                            onStart = { onStartUnitExam(unit.number) }
                        )
                    }
                }

                if (uiState.examResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "📊 سجل نتائج اختباراتك الأخيرة",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.examResults.take(8), key = { it.id }) { exam ->
                        ExamHistoryItemCard(exam = exam)
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitExamCard(
    unit: BookUnit,
    progress: UnitProgressEntity?,
    accent: androidx.compose.ui.graphics.Color,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val linaColors = LocalLinaColors.current
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, linaColors.cardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("unit_exam_card_${unit.number}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = accent,
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = unit.icon, fontSize = 28.sp)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "اختبار الوحدة ${unit.number}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = unit.titleEn,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FredokaFontFamily,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if ((progress?.bestScorePercent ?: 0) > 0) {
                    Text(
                        text = "أفضل نتيجة: ${progress?.bestScorePercent}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = linaColors.success
                    )
                }
            }

            FilledTonalButton(
                onClick = onStart,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("start_unit_exam_${unit.number}")
            ) {
                Text("ابدأ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun ExamHistoryItemCard(exam: ExamResultEntity) {
    val linaColors = LocalLinaColors.current
    val pct = if (exam.totalQuestions > 0) (exam.correctAnswers * 100) / exam.totalQuestions else 0
    val passed = pct >= 60

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, linaColors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AssignmentTurnedIn,
                contentDescription = null,
                tint = if (passed) linaColors.success else linaColors.starAccent,
                modifier = Modifier.size(26.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.examTitleAr,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "الإجابات الصحيحة: ${exam.correctAnswers} من ${exam.totalQuestions} • +${exam.starsEarned} نجمة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = if (passed) linaColors.successContainer else linaColors.starContainer
            ) {
                Text(
                    text = "$pct%",
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (passed) linaColors.onSuccessContainer else linaColors.onStarContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}
