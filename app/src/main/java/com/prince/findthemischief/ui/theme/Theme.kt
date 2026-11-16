package com.prince.findthemischief.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark-first and fixed: no dynamic (wallpaper) colour, so the mischief palette always wins.
private val MischiefColorScheme = darkColorScheme(
    primary = MischiefGreen,
    onPrimary = Parchment,
    primaryContainer = DeepEmerald,
    onPrimaryContainer = Parchment,
    secondary = OldGold,
    onSecondary = NearBlack,
    secondaryContainer = OldGoldDim,
    onSecondaryContainer = Parchment,
    tertiary = GlowGreen,
    onTertiary = NearBlack,
    background = NearBlack,
    onBackground = Parchment,
    surface = DeepEmerald,
    onSurface = Parchment,
    surfaceVariant = DeepEmeraldLight,
    onSurfaceVariant = ParchmentMuted,
    surfaceContainerLowest = NearBlack,
    surfaceContainerLow = Color(0xFF0D2620),
    surfaceContainer = DeepEmerald,
    surfaceContainerHigh = DeepEmeraldLight,
    surfaceContainerHighest = DeepEmeraldLight,
    outline = OldGold,
    outlineVariant = OldGoldDim,
    error = BloodRedLight,
    onError = NearBlack,
    errorContainer = BloodRed,
    onErrorContainer = Parchment,
    scrim = Color.Black,
)

@Composable
fun FindTheMischiefTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MischiefColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
