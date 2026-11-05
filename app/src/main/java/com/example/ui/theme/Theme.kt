package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightningDarkColorScheme = darkColorScheme(
    primary = ElectricYellow,
    onPrimary = AmoledBlack,
    primaryContainer = ElectricYellowSubtle,
    onPrimaryContainer = ElectricYellow,
    secondary = NeonGreen,
    onSecondary = AmoledBlack,
    secondaryContainer = NeonGreenSubtle,
    onSecondaryContainer = NeonGreen,
    tertiary = NeonCyan,
    onTertiary = AmoledBlack,
    background = AmoledBlack,
    onBackground = TextPrimary,
    surface = DarkCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = CardCharcoal,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderHighlight
)

@Composable
fun LightningFitsTheme(
    content: @Composable () -> Unit
) {
    // Pure AMOLED black theme as specified by brand identity
    MaterialTheme(
        colorScheme = LightningDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
