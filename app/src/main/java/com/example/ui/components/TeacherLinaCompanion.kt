package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalLinaColors
import com.example.viewmodel.LinaMood

@Composable
fun TeacherLinaCompanionBar(
    messageAr: String,
    mood: LinaMood = LinaMood.EXPLAINING,
    isSpeaking: Boolean = false,
    onSpeakMessage: (() -> Unit)? = null,
    onHintClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val linaColors = LocalLinaColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "lina_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lina_float_y"
    )
    val speakPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.07f else 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lina_speak_pulse"
    )

    val moodBadge = when (mood) {
        LinaMood.WELCOMING -> "🌸"
        LinaMood.EXPLAINING -> "💡"
        LinaMood.HAPPY -> "🌟"
        LinaMood.HINTING -> "🤗"
        LinaMood.CELEBRATING -> "🎉"
    }

    val bubbleContainerColor = when (mood) {
        LinaMood.HAPPY, LinaMood.CELEBRATING -> linaColors.successContainer
        LinaMood.HINTING -> linaColors.starContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val bubbleTextColor = when (mood) {
        LinaMood.HAPPY, LinaMood.CELEBRATING -> linaColors.onSuccessContainer
        LinaMood.HINTING -> linaColors.onStarContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = bubbleContainerColor),
        border = BorderStroke(1.2.dp, linaColors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("teacher_lina_companion_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .offset(y = floatOffset.dp)
                    .scale(speakPulse)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_lina_original),
                        contentDescription = stringResource(R.string.cd_teacher_lina),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = moodBadge, fontSize = 13.sp)
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = messageAr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = bubbleTextColor
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onHintClick != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = linaColors.starContainer
                    ) {
                        IconButton(
                            onClick = onHintClick,
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("lina_hint_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lightbulb,
                                contentDescription = "تلميح من المعلمة لينا",
                                tint = linaColors.onStarContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                if (onSpeakMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        IconButton(
                            onClick = onSpeakMessage,
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("lina_speak_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = stringResource(R.string.cd_speak_word),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
