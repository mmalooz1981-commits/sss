package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light Storybook Classroom Palette
val CreamCanvas = Color(0xFFFFF8E8)
val WarmPaperSurface = Color(0xFFFFFFFF)
val SoftCreamContainer = Color(0xFFFFF1D0)
val WarmSandVariant = Color(0xFFF6E7C8)

val RoyalViolet = Color(0xFF5E35B1)
val DeepPlum = Color(0xFF4527A0)
val LavenderMist = Color(0xFFEDE7F6)
val SoftVioletContainer = Color(0xFFE5DBFA)
val OnVioletContainer = Color(0xFF28105C)

val SunnyGold = Color(0xFFFFCA28)
val WarmHoney = Color(0xFFFFE082)
val DeepAmber = Color(0xFF8C5300)
val GoldenSurface = Color(0xFFFFF3C4)

val BlossomRose = Color(0xFFE91E63)
val BlossomTint = Color(0xFFFCE4EC)

val InkPlum = Color(0xFF241B35)
val MutedPlum = Color(0xFF5F5475)
val SoftBorder = Color(0xFFE7DBC2)

// Semantic Feedback Colors
val CorrectEmerald = Color(0xFF2E9E5B)
val CorrectMintSurface = Color(0xFFE8F5E9)
val CorrectDarkText = Color(0xFF145A32)

val WrongCoral = Color(0xFFE53935)
val WrongBlushSurface = Color(0xFFFFEBEE)
val WrongDarkText = Color(0xFF8E1B1B)

val MutedNeutralOption = Color(0xFFB5AEC2)

// Dark Cozy Night-Classroom Palette
val DarkCanvas = Color(0xFF181424)
val DarkCardSurface = Color(0xFF241E35)
val DarkSurfaceVariant = Color(0xFF312A47)
val LightLavenderPrimary = Color(0xFFCBB6FF)
val OnLightLavender = Color(0xFF2E1266)
val DarkGoldContainer = Color(0xFF4A3800)
val DarkGoldText = Color(0xFFFFE082)
val DarkTextPrimary = Color(0xFFF6F1FF)
val DarkTextSecondary = Color(0xFFC4BBD6)
val DarkBorder = Color(0xFF3E3557)

@Immutable
data class LinaExtendedColors(
    val starContainer: Color,
    val starAccent: Color,
    val onStarContainer: Color,
    val success: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val wrong: Color,
    val wrongContainer: Color,
    val onWrongContainer: Color,
    val mutedOption: Color,
    val cardBorder: Color,
    val heroGradientStart: Color,
    val heroGradientEnd: Color,
    val unitAccents: List<Color>
)

val LightLinaExtendedColors = LinaExtendedColors(
    starContainer = WarmHoney,
    starAccent = Color(0xFFF59E0B),
    onStarContainer = Color(0xFF4A2C00),
    success = CorrectEmerald,
    successContainer = CorrectMintSurface,
    onSuccessContainer = CorrectDarkText,
    wrong = WrongCoral,
    wrongContainer = WrongBlushSurface,
    onWrongContainer = WrongDarkText,
    mutedOption = MutedNeutralOption,
    cardBorder = SoftBorder,
    heroGradientStart = Color(0xFF673AB7),
    heroGradientEnd = Color(0xFF8E5AE2),
    unitAccents = listOf(
        Color(0xFFE3F2FD),
        Color(0xFFE8F5E9),
        Color(0xFFFFF3E0),
        Color(0xFFFFF8E1),
        Color(0xFFFCE4EC),
        Color(0xFFEDE7F6),
        Color(0xFFE0F7FA),
        Color(0xFFF3E5F5)
    )
)

val DarkLinaExtendedColors = LinaExtendedColors(
    starContainer = DarkGoldContainer,
    starAccent = SunnyGold,
    onStarContainer = DarkGoldText,
    success = Color(0xFF4ADE80),
    successContainer = Color(0xFF143A24),
    onSuccessContainer = Color(0xFFC8F7D6),
    wrong = Color(0xFFF87171),
    wrongContainer = Color(0xFF471B1E),
    onWrongContainer = Color(0xFFFFD6D6),
    mutedOption = Color(0xFF4A435C),
    cardBorder = DarkBorder,
    heroGradientStart = Color(0xFF4527A0),
    heroGradientEnd = Color(0xFF673AB7),
    unitAccents = listOf(
        Color(0xFF1E3246),
        Color(0xFF1B3527),
        Color(0xFF3E2D1B),
        Color(0xFF3E351A),
        Color(0xFF421F30),
        Color(0xFF2F244A),
        Color(0xFF19353B),
        Color(0xFF392344)
    )
)

val LocalLinaColors = staticCompositionLocalOf { LightLinaExtendedColors }
