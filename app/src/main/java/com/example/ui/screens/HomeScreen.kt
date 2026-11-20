package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BookUnit
import com.example.data.CurriculumRepository
import com.example.data.DifficultyLevel
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.AppDestination
import com.example.viewmodel.LinaUiState

private data class HubMenuCardItem(
    val tag: String,
    val titleAr: String,
    val subtitleAr: String,
    val emoji: String,
    val icon: ImageVector,
    val accentIndex: Int,
    val destination: AppDestination
)

@Composable
fun HomeScreen(
    uiState: LinaUiState,
    onNavigate: (AppDestination) -> Unit,
    onLevelSelected: (DifficultyLevel) -> Unit,
    onQuickPlayUnit: (Int) -> Unit,
    onSpeakWelcome: (String) -> Unit
) {
    val linaColors = LocalLinaColors.current
    val activeCompanion = CurriculumRepository.companions.firstOrNull {
        it.id == uiState.profile.selectedCompanionId
    } ?: CurriculumRepository.companions.first()

    val menuItems = listOf(
        HubMenuCardItem(
            tag = "hub_my_book",
            titleAr = "كتابي",
            subtitleAr = "جميع الوحدات الـ 8 والدروس",
            emoji = "📚",
            icon = Icons.Rounded.AutoStories,
            accentIndex = 0,
            destination = AppDestination.BookUnits
        ),
        HubMenuCardItem(
            tag = "hub_play_learn",
            titleAr = "العب وتعلم",
            subtitleAr = "ألعاب تفاعلية بـ 3 مستويات",
            emoji = "🎮",
            icon = Icons.Rounded.SportsEsports,
            accentIndex = 2,
            destination = AppDestination.UnitDetail(1)
        ),
        HubMenuCardItem(
            tag = "hub_listen_speak",
            titleAr = "استمع وتعلم",
            subtitleAr = "نصوص الاستماع وتدريب النطق",
            emoji = "🔊",
            icon = Icons.Rounded.Hearing,
            accentIndex = 6,
            destination = AppDestination.ListenAndSpeak
        ),
        HubMenuCardItem(
            tag = "hub_dictionary",
            titleAr = "قاموسي",
            subtitleAr = "${uiState.dictionaryWords.size} كلمة بالصور والنطق",
            emoji = "📖",
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            accentIndex = 1,
            destination = AppDestination.Dictionary
        ),
        HubMenuCardItem(
            tag = "hub_achievements",
            titleAr = "إنجازاتي",
            subtitleAr = "خريطة التقدم والميداليات والشهادات",
            emoji = "🏆",
            icon = Icons.Rounded.EmojiEvents,
            accentIndex = 3,
            destination = AppDestination.Achievements
        ),
        HubMenuCardItem(
            tag = "hub_exams",
            titleAr = "اختبر نفسك",
            subtitleAr = "اختبارات الوحدات والاختبار الشامل",
            emoji = "📝",
            icon = Icons.Rounded.Assignment,
            accentIndex = 4,
            destination = AppDestination.Exams
        ),
        HubMenuCardItem(
            tag = "hub_parents",
            titleAr = "متابعة ولي الأمر",
            subtitleAr = "تقارير المستوى والنتائج المحلية",
            emoji = "👨‍👩‍👧",
            icon = Icons.Rounded.FamilyRestroom,
            accentIndex = 5,
            destination = AppDestination.ParentDashboard
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            val columns = if (maxWidth >= 600.dp) 3 else 2

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "hero_lina") {
                    TeacherLinaHeroHeader(
                        childName = uiState.profile.childName,
                        totalStars = uiState.profile.totalStars,
                        medalsCount = uiState.completedUnitsCount,
                        companionEmoji = activeCompanion.emoji,
                        onSpeakGreeting = {
                            onSpeakWelcome("Hello ${uiState.profile.childName}! Welcome to Teacher Lina! Let's learn English for Iraq together!")
                        }
                    )
                }

                item(key = "level_picker") {
                    HomeLevelSelector(
                        selectedLevel = uiState.selectedLevel,
                        onLevelSelected = onLevelSelected
                    )
                }

                item(key = "hub_grid_title") {
                    Text(
                        text = "🌟 أقسام رحلتك التعليمية",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                val rows = menuItems.chunked(columns)
                items(rows.size, key = { "hub_row_$it" }) { rowIdx ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rows[rowIdx].forEach { item ->
                            val bgTint = linaColors.unitAccents[item.accentIndex % linaColors.unitAccents.size]
                            HubSectionCard(
                                item = item,
                                iconBg = bgTint,
                                onClick = {
                                    if (item.tag == "hub_play_learn") {
                                        val nextUnit = (1..8).firstOrNull {
                                            uiState.unitProgressMap[it]?.hasMedal != true
                                        } ?: 1
                                        onQuickPlayUnit(nextUnit)
                                    } else {
                                        onNavigate(item.destination)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(columns - rows[rowIdx].size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item(key = "quick_units_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📚 وحدات كتاب English for Iraq",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onNavigate(AppDestination.BookUnits) }
                                .testTag("see_all_units_chip")
                        ) {
                            Text(
                                text = "عرض الكل",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                item(key = "quick_units_row") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.units, key = { it.number }) { unit ->
                            val hasMedal = uiState.unitProgressMap[unit.number]?.hasMedal == true
                            val accent = linaColors.unitAccents[(unit.number - 1) % linaColors.unitAccents.size]
                            QuickUnitPillCard(
                                unit = unit,
                                hasMedal = hasMedal,
                                accentColor = accent,
                                onClick = { onNavigate(AppDestination.UnitDetail(unit.number)) }
                            )
                        }
                    }
                }

                item(key = "home_bottom_space") {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun TeacherLinaHeroHeader(
    childName: String,
    totalStars: Int,
    medalsCount: Int,
    companionEmoji: String,
    onSpeakGreeting: () -> Unit
) {
    val linaColors = LocalLinaColors.current
    val infinite = rememberInfiniteTransition(label = "hero_float")
    val bobY by infinite.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lina_hero_bob"
    )

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_teacher_lina_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            linaColors.heroGradientStart,
                            linaColors.heroGradientEnd
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .offset(y = bobY.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            border = BorderStroke(3.dp, linaColors.starContainer),
                            shadowElevation = 6.dp,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_lina_3d_avatar_1795209306681),
                                contentDescription = stringResource(R.string.cd_teacher_lina),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = linaColors.starContainer,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = companionEmoji, fontSize = 16.sp)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "المعلمة لينا 🌸",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            text = "أهلاً يا $childName! هيا نتعلم كتاب الخامس الابتدائي باللعب والمرح!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.94f)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.22f)
                    ) {
                        IconButton(
                            onClick = onSpeakGreeting,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("hero_speak_welcome_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = "استمع لترحيب المعلمة لينا",
                                tint = Color.White
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = linaColors.starContainer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("total_stars_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = linaColors.starAccent,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "مجموع النجوم",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = linaColors.onStarContainer
                                )
                                Text(
                                    text = "$totalStars",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = FredokaFontFamily,
                                    color = linaColors.onStarContainer
                                )
                            }
                        }
                    }

                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White.copy(alpha = 0.18f),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("completed_units_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = null,
                                tint = linaColors.starContainer,
                                modifier = Modifier.size(26.dp)
                            )
                            Column {
                                Text(
                                    text = "ميداليات الوحدات",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "$medalsCount / 8",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = FredokaFontFamily,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeLevelSelector(
    selectedLevel: DifficultyLevel,
    onLevelSelected: (DifficultyLevel) -> Unit
) {
    val linaColors = LocalLinaColors.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "اختر مستواك التعليمي",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DifficultyLevel.entries.forEach { level ->
                val selected = selectedLevel == level
                val bg by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    label = "home_lvl_bg_${level.id}"
                )
                val fg by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    label = "home_lvl_fg_${level.id}"
                )

                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = bg),
                    border = if (selected) null else BorderStroke(1.dp, linaColors.cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 1.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onLevelSelected(level) }
                        .testTag("level_button_${level.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = level.emoji, fontSize = 24.sp)
                        Text(
                            text = level.shortNameAr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = fg,
                            maxLines = 1
                        )
                        Text(
                            text = "${level.questionCount} أسئلة",
                            style = MaterialTheme.typography.labelMedium,
                            color = fg.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HubSectionCard(
    item: HubMenuCardItem,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val linaColors = LocalLinaColors.current

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, linaColors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag(item.tag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.emoji, fontSize = 24.sp)
                }
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = item.titleAr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.subtitleAr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                minLines = 2
            )
        }
    }
}

@Composable
private fun QuickUnitPillCard(
    unit: BookUnit,
    hasMedal: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val linaColors = LocalLinaColors.current

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (hasMedal) 1.5.dp else 1.dp,
            color = if (hasMedal) linaColors.success else linaColors.cardBorder
        ),
        modifier = Modifier
            .width(168.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag("quick_unit_${unit.number}")
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = unit.icon, fontSize = 22.sp)
                }
                Text(
                    text = if (hasMedal) "🏅" else "ص ${unit.startPage}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = "Unit ${unit.number}",
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FredokaFontFamily,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = unit.titleEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = FredokaFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = unit.titleAr,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
