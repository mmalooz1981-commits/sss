package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.BlossomRose
import com.example.ui.theme.CorrectEmerald
import com.example.ui.theme.LocalLinaColors
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.SunnyGold
import com.example.ui.theme.WarmHoney
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private enum class ConfettiShape {
    RIBBON,
    CIRCLE,
    STAR,
    DIAMOND
}

private data class ConfettiParticle(
    val startXFraction: Float,
    val startYFraction: Float,
    val velocityX: Float,
    val velocityY: Float,
    val gravity: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val swayPhase: Float,
    val sizeDp: Float,
    val initialRotation: Float,
    val rotationSpeed: Float,
    val delayFraction: Float,
    val colorIndex: Int,
    val shape: ConfettiShape
)

/**
 * Energetic radial confetti & star burst for correct answers and successful pronunciation.
 * Does not intercept pointer input so the child can keep interacting immediately.
 */
@Composable
fun ConfettiBurstOverlay(
    triggerKey: Any?,
    modifier: Modifier = Modifier
) {
    if (triggerKey == null) return

    val linaColors = LocalLinaColors.current
    val palette = listOf(
        SunnyGold,
        BlossomRose,
        CorrectEmerald,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        WarmHoney,
        linaColors.starAccent
    )

    val particles = remember(triggerKey) {
        val rng = Random(triggerKey.hashCode())
        List(54) { idx ->
            val angleDeg = -155f + rng.nextFloat() * 130f // Upward fan (-155° to -25°)
            val angleRad = angleDeg * (PI / 180.0)
            val speed = 0.55f + rng.nextFloat() * 0.85f
            ConfettiParticle(
                startXFraction = 0.35f + rng.nextFloat() * 0.30f,
                startYFraction = 0.42f + rng.nextFloat() * 0.14f,
                velocityX = (cos(angleRad) * speed).toFloat(),
                velocityY = (sin(angleRad) * speed * 1.15f).toFloat(),
                gravity = 1.65f + rng.nextFloat() * 0.65f,
                swayAmplitude = 0.015f + rng.nextFloat() * 0.025f,
                swayFrequency = 4f + rng.nextFloat() * 5f,
                swayPhase = rng.nextFloat() * (2f * PI.toFloat()),
                sizeDp = 7f + rng.nextFloat() * 8f,
                initialRotation = rng.nextFloat() * 360f,
                rotationSpeed = (-320f + rng.nextFloat() * 640f),
                delayFraction = rng.nextFloat() * 0.08f,
                colorIndex = idx % palette.size,
                shape = when (idx % 5) {
                    0, 1 -> ConfettiShape.STAR
                    2 -> ConfettiShape.RIBBON
                    3 -> ConfettiShape.CIRCLE
                    else -> ConfettiShape.DIAMOND
                }
            )
        }
    }

    val progress = remember(triggerKey) { Animatable(0f) }
    LaunchedEffect(triggerKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1650, easing = LinearEasing)
        )
    }

    val t = progress.value
    if (t <= 0f || t >= 1f) return

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("confetti_burst_overlay")
    ) {
        drawConfettiParticles(
            particles = particles,
            palette = palette,
            globalProgress = t,
            durationSec = 1.65f
        )
    }
}

/**
 * Full-screen celebratory confetti shower for completing a unit, level, or exam.
 * Combines a grand top rainfall wave with twin side celebratory cannons.
 */
@Composable
fun ConfettiShowerOverlay(
    triggerKey: Any = Unit,
    modifier: Modifier = Modifier
) {
    val linaColors = LocalLinaColors.current
    val palette = listOf(
        SunnyGold,
        BlossomRose,
        CorrectEmerald,
        RoyalViolet,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        linaColors.starAccent,
        WarmHoney
    )

    val particles = remember(triggerKey) {
        val rng = Random(triggerKey.hashCode() xor 0x51A7F)
        val topRain = List(72) { idx ->
            ConfettiParticle(
                startXFraction = rng.nextFloat(),
                startYFraction = -0.05f - rng.nextFloat() * 0.25f,
                velocityX = -0.10f + rng.nextFloat() * 0.20f,
                velocityY = 0.22f + rng.nextFloat() * 0.28f,
                gravity = 0.36f + rng.nextFloat() * 0.25f,
                swayAmplitude = 0.025f + rng.nextFloat() * 0.04f,
                swayFrequency = 3f + rng.nextFloat() * 4.5f,
                swayPhase = rng.nextFloat() * (2f * PI.toFloat()),
                sizeDp = 8f + rng.nextFloat() * 9f,
                initialRotation = rng.nextFloat() * 360f,
                rotationSpeed = -280f + rng.nextFloat() * 560f,
                delayFraction = rng.nextFloat() * 0.38f,
                colorIndex = idx % palette.size,
                shape = when (idx % 4) {
                    0 -> ConfettiShape.STAR
                    1 -> ConfettiShape.RIBBON
                    2 -> ConfettiShape.CIRCLE
                    else -> ConfettiShape.DIAMOND
                }
            )
        }
        val sideCannons = List(30) { idx ->
            val fromLeft = idx % 2 == 0
            val angleDeg = if (fromLeft) {
                -75f + rng.nextFloat() * 45f
            } else {
                -150f + rng.nextFloat() * 45f
            }
            val angleRad = angleDeg * (PI / 180.0)
            val speed = 0.45f + rng.nextFloat() * 0.55f
            ConfettiParticle(
                startXFraction = if (fromLeft) 0.04f else 0.96f,
                startYFraction = 0.55f + rng.nextFloat() * 0.15f,
                velocityX = (cos(angleRad) * speed).toFloat(),
                velocityY = (sin(angleRad) * speed).toFloat(),
                gravity = 0.72f + rng.nextFloat() * 0.30f,
                swayAmplitude = 0.015f + rng.nextFloat() * 0.025f,
                swayFrequency = 4f + rng.nextFloat() * 3f,
                swayPhase = rng.nextFloat() * (2f * PI.toFloat()),
                sizeDp = 8f + rng.nextFloat() * 7f,
                initialRotation = rng.nextFloat() * 360f,
                rotationSpeed = -360f + rng.nextFloat() * 720f,
                delayFraction = rng.nextFloat() * 0.12f,
                colorIndex = (idx + 3) % palette.size,
                shape = if (idx % 2 == 0) ConfettiShape.STAR else ConfettiShape.RIBBON
            )
        }
        topRain + sideCannons
    }

    val progress = remember(triggerKey) { Animatable(0f) }
    LaunchedEffect(triggerKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4200, easing = LinearEasing)
        )
    }

    val t = progress.value
    if (t <= 0f || t >= 1f) return

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("confetti_shower_overlay")
    ) {
        drawConfettiParticles(
            particles = particles,
            palette = palette,
            globalProgress = t,
            durationSec = 4.2f
        )
    }
}

private fun DrawScope.drawConfettiParticles(
    particles: List<ConfettiParticle>,
    palette: List<Color>,
    globalProgress: Float,
    durationSec: Float
) {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return

    particles.forEach { p ->
        val localProgress = ((globalProgress - p.delayFraction) / (1f - p.delayFraction))
            .coerceIn(0f, 1f)
        if (localProgress <= 0f) return@forEach

        val timeSec = localProgress * durationSec
        val sway = sin(timeSec * p.swayFrequency + p.swayPhase) * p.swayAmplitude
        val x = (p.startXFraction + p.velocityX * timeSec + sway) * w
        val y = (p.startYFraction + p.velocityY * timeSec + 0.5f * p.gravity * timeSec * timeSec) * h

        if (y > h + 60f || x < -60f || x > w + 60f) return@forEach

        val fadeAlpha = when {
            localProgress < 0.08f -> localProgress / 0.08f
            localProgress > 0.72f -> ((1f - localProgress) / 0.28f).coerceIn(0f, 1f)
            else -> 1f
        }
        val baseColor = palette[p.colorIndex % palette.size].copy(alpha = fadeAlpha)
        val pxSize = p.sizeDp * density
        val angle = p.initialRotation + p.rotationSpeed * timeSec
        val flipScaleY = (0.35f + 0.65f * kotlin.math.abs(cos(timeSec * 5f + p.swayPhase)))

        withTransform({
            translate(left = x, top = y)
            rotate(degrees = angle, pivot = Offset.Zero)
            scale(scaleX = 1f, scaleY = flipScaleY, pivot = Offset.Zero)
        }) {
            when (p.shape) {
                ConfettiShape.RIBBON -> {
                    drawRoundRect(
                        color = baseColor,
                        topLeft = Offset(-pxSize * 0.6f, -pxSize * 0.28f),
                        size = Size(pxSize * 1.2f, pxSize * 0.56f),
                        cornerRadius = CornerRadius(pxSize * 0.16f, pxSize * 0.16f)
                    )
                }
                ConfettiShape.CIRCLE -> {
                    drawCircle(
                        color = baseColor,
                        radius = pxSize * 0.42f,
                        center = Offset.Zero
                    )
                }
                ConfettiShape.DIAMOND -> {
                    val path = Path().apply {
                        val r = pxSize * 0.55f
                        moveTo(0f, -r)
                        lineTo(r * 0.7f, 0f)
                        lineTo(0f, r)
                        lineTo(-r * 0.7f, 0f)
                        close()
                    }
                    drawPath(path = path, color = baseColor)
                }
                ConfettiShape.STAR -> {
                    drawFivePointStar(radius = pxSize * 0.62f, color = baseColor)
                }
            }
        }
    }
}

private fun DrawScope.drawFivePointStar(radius: Float, color: Color) {
    val innerRadius = radius * 0.44f
    val path = Path()
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angleRad = (-90.0 + i * 36.0) * (PI / 180.0)
        val px = (cos(angleRad) * r).toFloat()
        val py = (sin(angleRad) * r).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path = path, color = color)
}
