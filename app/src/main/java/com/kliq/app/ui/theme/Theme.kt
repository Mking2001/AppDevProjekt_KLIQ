package com.kliq.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimaryLight,
    onPrimary = DarkBackground,
    primaryContainer = PurpleContainer,
    onPrimaryContainer = OnPurpleContainer,
    secondary = TealSecondaryLight,
    onSecondary = DarkBackground,
    secondaryContainer = TealContainer,
    onSecondaryContainer = OnTealContainer,
    tertiary = FuchsiaTertiaryLight,
    onTertiary = DarkBackground,
    tertiaryContainer = FuchsiaContainer,
    onTertiaryContainer = OnFuchsiaContainer,
    error = ErrorRedLight,
    onError = DarkBackground,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    scrim = DarkScrim,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceContainer = DarkSurfaceContainer
)

@Composable
fun KliqTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KliqTypography,
        shapes = KliqShapes,
        content = content
    )
}
