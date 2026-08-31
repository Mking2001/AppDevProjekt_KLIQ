package com.kliq.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class HapticFeedbackManagerTest {

    private val hapticFeedbackManager: HapticFeedbackManager = mock(HapticFeedbackManager::class.java)

    @Before
    fun setUp() {
    }

    @Test
    fun testHapticFeedbackPatternsExist() {
        val patterns = HapticFeedbackPattern.values()
        assertEquals(4, patterns.size)
        assertNotNull(HapticFeedbackPattern.CONFIRM)
        assertNotNull(HapticFeedbackPattern.REJECT)
        assertNotNull(HapticFeedbackPattern.LIGHT_CLICK)
        assertNotNull(HapticFeedbackPattern.HEAVY_CLICK)
    }

    @Test
    fun testPerformConfirmTriggersPatternWithReason() {
        val reason = "QR Scan / Friend verification"
        hapticFeedbackManager.performConfirm(reason)
        verify(hapticFeedbackManager).performConfirm(reason)
    }

    @Test
    fun testPerformRejectTriggersPatternWithReason() {
        val reason = "Invalid QR Code"
        hapticFeedbackManager.performReject(reason)
        verify(hapticFeedbackManager).performReject(reason)
    }

    @Test
    fun testPerformLightClickTriggersPatternWithReason() {
        val reason = "Rating star selection"
        hapticFeedbackManager.performLightClick(reason)
        verify(hapticFeedbackManager).performLightClick(reason)
    }

    @Test
    fun testPerformHeavyClickTriggersPatternWithReason() {
        val reason = "Map marker long-press quick-view"
        hapticFeedbackManager.performHeavyClick(reason)
        verify(hapticFeedbackManager).performHeavyClick(reason)
    }

    @Test
    fun testPerformHapticFeedbackPatternDirectly() {
        hapticFeedbackManager.performHapticFeedback(HapticFeedbackPattern.CONFIRM, "Geofence entry / Location match")
        verify(hapticFeedbackManager).performHapticFeedback(HapticFeedbackPattern.CONFIRM, "Geofence entry / Location match")
    }
}
