package com.kliq.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.kliq.app.viewmodel.ThemeMode

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
    scrim = DarkScrim
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = LightBackground,
    primaryContainer = PurpleContainerLight,
    onPrimaryContainer = OnPurpleContainerLight,
    secondary = TealSecondaryDark,
    onSecondary = LightBackground,
    secondaryContainer = TealContainerLight,
    onSecondaryContainer = OnTealContainerLight,
    tertiary = FuchsiaTertiaryDark,
    onTertiary = LightBackground,
    tertiaryContainer = FuchsiaContainerLight,
    onTertiaryContainer = OnFuchsiaContainerLight,
    error = ErrorRedDark,
    onError = LightBackground,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    scrim = LightScrim
)

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = HighContrastPurplePrimary,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF3B0764),
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = androidx.compose.ui.graphics.Color(0xFF2DD4BF),
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF134E4A),
    onSecondaryContainer = androidx.compose.ui.graphics.Color.White,
    tertiary = androidx.compose.ui.graphics.Color(0xFFF472B6),
    onTertiary = androidx.compose.ui.graphics.Color.Black,
    error = androidx.compose.ui.graphics.Color(0xFFF87171),
    onError = androidx.compose.ui.graphics.Color.Black,
    background = HighContrastBackground,
    onBackground = HighContrastOnBackground,
    surface = HighContrastSurface,
    onSurface = HighContrastOnSurface,
    surfaceVariant = HighContrastSurfaceVariant,
    onSurfaceVariant = androidx.compose.ui.graphics.Color.White,
    outline = HighContrastOutline,
    outlineVariant = androidx.compose.ui.graphics.Color(0xFF9333EA),
    scrim = androidx.compose.ui.graphics.Color.Black
)

@Composable
fun KliqTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    isHighContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        isHighContrast -> HighContrastDarkColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            activity?.window?.let { window ->
                val bgArgb = colorScheme.background.toArgb()
                window.statusBarColor = bgArgb
                window.navigationBarColor = bgArgb
                window.decorView.setBackgroundColor(bgArgb)
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme && !isHighContrast
                insetsController.isAppearanceLightNavigationBars = !darkTheme && !isHighContrast
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KliqTypography,
        shapes = KliqShapes,
        content = content
    )
}
