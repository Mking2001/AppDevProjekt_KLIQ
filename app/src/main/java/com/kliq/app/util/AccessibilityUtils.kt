package com.kliq.app.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WcagComplianceLevel {
    AAA,
    AA,
    FAIL
}

object AccessibilityUtils {

    fun calculateContrastRatio(foreground: Color, background: Color): Double {
        val l1 = foreground.luminance()
        val l2 = background.luminance()

        val lightest = maxOf(l1, l2)
        val darkest = minOf(l1, l2)

        return (lightest + 0.05) / (darkest + 0.05)
    }

    fun verifyWcagCompliance(
        foreground: Color,
        background: Color,
        isLargeText: Boolean = false
    ): WcagComplianceLevel {
        val ratio = calculateContrastRatio(foreground, background)
        val aaaThreshold = if (isLargeText) 4.5 else 7.0
        val aaThreshold = if (isLargeText) 3.0 else 4.5

        return when {
            ratio >= aaaThreshold -> WcagComplianceLevel.AAA
            ratio >= aaThreshold -> WcagComplianceLevel.AA
            else -> WcagComplianceLevel.FAIL
        }
    }

    fun meetsMinimumTouchTarget(sizeDp: Dp): Boolean {
        return sizeDp >= 48.dp
    }

    fun ensureMinimumContrast(foreground: Color, background: Color, targetRatio: Double = 4.5): Color {
        val currentRatio = calculateContrastRatio(foreground, background)
        if (currentRatio >= targetRatio) return foreground

        val bgLuminance = background.luminance()
        return if (bgLuminance > 0.5f) Color.Black else Color.White
    }

    fun calculateScaledSp(baseSpValue: Float, fontScale: Float): Float {
        return (baseSpValue * fontScale.coerceIn(0.8f, 2.0f))
    }

    fun isAccessibilityFontScaleActive(fontScale: Float): Boolean {
        return fontScale >= 1.3f
    }

    fun getAdaptiveMinContainerHeight(baseHeightDp: Dp, fontScale: Float): Dp {
        val safeScale = fontScale.coerceIn(1.0f, 2.0f)
        return (baseHeightDp.value * (1f + (safeScale - 1f) * 0.6f)).dp
    }
}
