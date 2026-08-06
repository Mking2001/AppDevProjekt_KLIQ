package com.kliq.app.util

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccessibilityUtilsTest {

    @Test
    fun `calculateContrastRatio returns maximum 21 for black and white`() {
        val contrast = AccessibilityUtils.calculateContrastRatio(Color.White, Color.Black)
        assertEquals(21.0, contrast, 0.1)
    }

    @Test
    fun `calculateContrastRatio returns minimum 1 for identical colors`() {
        val contrast = AccessibilityUtils.calculateContrastRatio(Color.Black, Color.Black)
        assertEquals(1.0, contrast, 0.05)
    }

    @Test
    fun `verifyWcagCompliance correctly identifies AAA compliance for high contrast white on black`() {
        val result = AccessibilityUtils.verifyWcagCompliance(Color.White, Color.Black, isLargeText = false)
        assertEquals(WcagComplianceLevel.AAA, result)
    }

    @Test
    fun `verifyWcagCompliance fails for low contrast colors`() {
        val result = AccessibilityUtils.verifyWcagCompliance(Color(0xFF888888), Color(0xFF777777))
        assertEquals(WcagComplianceLevel.FAIL, result)
    }

    @Test
    fun `meetsMinimumTouchTarget returns true for 48dp or larger`() {
        assertTrue(AccessibilityUtils.meetsMinimumTouchTarget(androidx.compose.ui.unit.Dp(48f)))
        assertTrue(AccessibilityUtils.meetsMinimumTouchTarget(androidx.compose.ui.unit.Dp(60f)))
        assertFalse(AccessibilityUtils.meetsMinimumTouchTarget(androidx.compose.ui.unit.Dp(40f)))
    }

    @Test
    fun `ensureMinimumContrast replaces low contrast color with white or black`() {
        val darkBg = Color(0xFF101010)
        val lowContrastFg = Color(0xFF1E1E1E)

        val safeColor = AccessibilityUtils.ensureMinimumContrast(lowContrastFg, darkBg, targetRatio = 4.5)
        assertEquals(Color.White, safeColor)
    }

    @Test
    fun `calculateScaledSp scales font size correctly within bounds`() {
        val scaledNormal = AccessibilityUtils.calculateScaledSp(16f, 1.5f)
        assertEquals(24f, scaledNormal, 0.01f)

        val scaledClampedMax = AccessibilityUtils.calculateScaledSp(16f, 3.0f)
        assertEquals(32f, scaledClampedMax, 0.01f)
    }
}
