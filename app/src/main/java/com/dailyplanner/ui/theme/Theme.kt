package com.dailyplanner.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = OffWhite,
    primaryContainer = SageGreenLight,
    onPrimaryContainer = DarkBrown,
    secondary = ClayBrown,
    onSecondary = OffWhite,
    secondaryContainer = WarmBeige,
    onSecondaryContainer = DarkBrown,
    tertiary = WarmBeige,
    onTertiary = DarkBrown,
    background = PaperWhite,
    onBackground = DarkBrown,
    surface = OffWhite,
    onSurface = DarkBrown,
    surfaceVariant = WarmBeige,
    onSurfaceVariant = DarkBrown,
    outline = BeigeDark
)

private val DarkColorScheme = darkColorScheme(
    primary = MutedSage,
    onPrimary = DeepForest,
    primaryContainer = ForestGreen,
    onPrimaryContainer = WarmGray,
    secondary = ClayBrown,
    onSecondary = DeepForest,
    secondaryContainer = Color(0xFF5A3D22),
    onSecondaryContainer = WarmGray,
    background = DarkSurface,
    onBackground = WarmGray,
    surface = Charcoal,
    onSurface = WarmGray,
    surfaceVariant = CharcoalLight,
    onSurfaceVariant = WarmGray,
    outline = Color(0xFF5A5A5A)
)

@Composable
fun DailyPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
