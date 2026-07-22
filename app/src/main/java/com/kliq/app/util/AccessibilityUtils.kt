package com.kliq.app.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AccessibilityUtils {

    /**
     * Calculates the WCAG 2.1 contrast ratio between a foreground and background color.
     * @return Contrast ratio (1.0 to 21.0)
     */
    fun calculateContrastRatio(foreground: Color, background: Color): Double {
        val l1 = foreground.luminance()
        val l2 = background.luminance()
        
        val lightest = maxOf(l1, l2)
        val darkest = minOf(l1, l2)
        
        return (lightest + 0.05) / (darkest + 0.05)
    }

    /**
     * Checks if a given size meets the minimum 48dp touch target size recommended by Material Design.
     */
    fun meetsMinimumTouchTarget(sizeDp: Dp): Boolean {
        return sizeDp >= 48.dp
    }

    /**
     * Ensures minimum contrast ratio (WCAG AA normal text = 4.5:1).
     * If contrast is too low, it returns a modified foreground color (either black or white)
     * to guarantee readability against the given background.
     */
    fun ensureMinimumContrast(foreground: Color, background: Color, targetRatio: Double = 4.5): Color {
        val currentRatio = calculateContrastRatio(foreground, background)
        if (currentRatio >= targetRatio) return foreground

        // If contrast fails, pick either white or black depending on background luminance
        val bgLuminance = background.luminance()
        return if (bgLuminance > 0.5f) Color.Black else Color.White
    }
}
