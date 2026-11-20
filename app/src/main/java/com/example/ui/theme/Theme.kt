package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = RoyalViolet,
    onPrimary = Color.White,
    primaryContainer = SoftVioletContainer,
    onPrimaryContainer = OnVioletContainer,
    secondary = DeepAmber,
    onSecondary = Color.White,
    secondaryContainer = WarmHoney,
    onSecondaryContainer = Color(0xFF4A2C00),
    tertiary = BlossomRose,
    onTertiary = Color.White,
    tertiaryContainer = BlossomTint,
    onTertiaryContainer = Color(0xFF6A0A2D),
    background = CreamCanvas,
    onBackground = InkPlum,
    surface = WarmPaperSurface,
    onSurface = InkPlum,
    surfaceVariant = SoftCreamContainer,
    onSurfaceVariant = MutedPlum,
    outline = SoftBorder,
    error = WrongCoral,
    onError = Color.White,
    errorContainer = WrongBlushSurface,
    onErrorContainer = WrongDarkText
)

private val DarkColorScheme = darkColorScheme(
    primary = LightLavenderPrimary,
    onPrimary = OnLightLavender,
    primaryContainer = DeepPlum,
    onPrimaryContainer = Color(0xFFEDE7F6),
    secondary = SunnyGold,
    onSecondary = Color(0xFF3E2900),
    secondaryContainer = DarkGoldContainer,
    onSecondaryContainer = DarkGoldText,
    tertiary = Color(0xFFF48FB1),
    onTertiary = Color(0xFF560A25),
    tertiaryContainer = Color(0xFF4A192D),
    onTertiaryContainer = Color(0xFFFFD9E3),
    background = DarkCanvas,
    onBackground = DarkTextPrimary,
    surface = DarkCardSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = Color(0xFFF87171),
    onError = Color(0xFF4A0E0E),
    errorContainer = Color(0xFF471B1E),
    onErrorContainer = Color(0xFFFFD6D6)
)

private val TeacherLinaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun TeacherLinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkLinaExtendedColors else LightLinaExtendedColors

    CompositionLocalProvider(LocalLinaColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TeacherLinaTypography,
            shapes = TeacherLinaShapes,
            content = content
        )
    }
}
