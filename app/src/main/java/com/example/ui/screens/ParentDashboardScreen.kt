package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TeacherLinaCompanionBar
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.LinaMood
import com.example.viewmodel.LinaUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    uiState: LinaUiState,
    onUpdateChildName: (String) -> Unit,
    onResetProgress: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val linaColors = LocalLinaColors.current

    var nameInput by remember { mutableStateOf(uiState.profile.childName) }
    var nameSavedBanner by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.profile.childName) {
        nameInput = uiState.profile.childName
    }

    val avgExamPercent = remember(uiState.examResults) {
        if (uiState.examResults.isEmpty()) 0
        else uiState.examResults.map {
            if (it.totalQuestions > 0) (it.correctAnswers * 100) / it.totalQuestions else 0
        }.average().toInt()
    }

    val nextRecommendedUnit = remember(uiState.units, uiState.unitProgressMap) {
        uiState.units.firstOrNull { u ->
            val p = uiState.unitProgressMap[u.number]
            p == null || !p.hasMedal || p.bestScorePercent < 70
        } ?: uiState.units.first()
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("إعادة ضبط تقدم الطالب", fontWeight = FontWeight.Bold) },
            text = {
                Text("هل أنت متأكد من تصفير جميع النجوم والميداليات وسجل الاختبارات على هذا الجهاز؟ لا يمكن التراجع عن هذه الخطوة.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = linaColors.wrong),
                    modifier = Modifier.testTag("confirm_reset_progress_button")
                ) {
                    Text("نعم، أعد الضبط")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("👨‍👩‍👧 متابعة ولي الأمر", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("parent_back_button")) {
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
                    .widthIn(max = 820.dp)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TeacherLinaCompanionBar(
                        messageAr = "مرحباً بولي الأمر الكريم! هنا تقرير تفصيلي محفوظ محلياً على الجهاز لمتابعة تقدم طفلك في منهج English for Iraq للصف الخامس.",
                        mood = LinaMood.WELCOMING
                    )
                }

                // Child Name Card
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.2.dp, linaColors.cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "اسم الطالب (يظهر في الترحيب والشهادات)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = {
                                    nameInput = it
                                    nameSavedBanner = false
                                },
                                leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                                placeholder = { Text("اكتب اسم الطفل هنا...") },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("child_name_input")
                            )
                            Button(
                                onClick = {
                                    onUpdateChildName(nameInput)
                                    nameSavedBanner = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("save_child_name_button")
                            ) {
                                Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("حفظ الاسم", fontWeight = FontWeight.Bold)
                            }
                            if (nameSavedBanner) {
                                Text(
                                    text = "تم حفظ اسم الطالب بنجاح! ✓",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = linaColors.success
                                )
                            }
                        }
                    }
                }

                // KPI Summary Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ParentStatCard(
                                emoji = "⭐",
                                value = uiState.profile.totalStars.toString(),
                                label = "مجموع النجوم",
                                modifier = Modifier.weight(1f)
                            )
                            ParentStatCard(
                                emoji = "🏅",
                                value = "${uiState.completedUnitsCount}/8",
                                label = "الوحدات المنجزة",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ParentStatCard(
                                emoji = "🎤",
                                value = uiState.profile.wordsSpokenCount.toString(),
                                label = "تمارين نطق ناجحة",
                                modifier = Modifier.weight(1f)
                            )
                            ParentStatCard(
                                emoji = "📝",
                                value = if (uiState.examResults.isEmpty()) "--" else "$avgExamPercent%",
                                label = "متوسط الاختبارات",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Next Recommendation
                item {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = linaColors.starContainer,
                        border = BorderStroke(1.dp, linaColors.starAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "💡 توصية المعلمة لينا التعليمية:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = linaColors.onStarContainer
                            )
                            Text(
                                text = "ننصح بالتركيز الآن على الوحدة ${nextRecommendedUnit.number} (${nextRecommendedUnit.titleAr}) ومراجعة مفرداتها ثم خوض اختبارها.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = linaColors.onStarContainer
                            )
                        }
                    }
                }

                // Unit-by-unit mastery breakdown
                item {
                    Text(
                        text = "📈 مستوى الإتقان في الوحدات الثماني",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.units, key = { it.number }) { unit ->
                    val p = uiState.unitProgressMap[unit.number]
                    val pct = p?.bestScorePercent ?: 0
                    val levelsDone = listOf(
                        p?.level1Completed == true,
                        p?.level2Completed == true,
                        p?.level3Completed == true
                    ).count { it }

                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, linaColors.cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${unit.icon} الوحدة ${unit.number}: ${unit.titleAr}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "$pct%",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { pct / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = if (pct >= 70) linaColors.success else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = StrokeCap.Round
                            )
                            Text(
                                text = "المستويات المنجزة: $levelsDone/3 • عدد المحاولات: ${p?.attemptsCount ?: 0} • النجوم: ${p?.starsEarned ?: 0}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (uiState.examResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "📋 سجل الاختبارات المحفوظة",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.examResults.take(10), key = { it.id }) { exam ->
                        ExamHistoryItemCard(exam = exam)
                    }
                }

                // Reset progress button
                item {
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        border = BorderStroke(1.2.dp, linaColors.wrong),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = linaColors.wrong),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("reset_progress_button")
                    ) {
                        Icon(Icons.Rounded.DeleteForever, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("إعادة ضبط التقدم والنجوم", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentStatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val linaColors = LocalLinaColors.current
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, linaColors.cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 26.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FredokaFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
