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
    fun testPerformConfirmTriggersPattern() {
        hapticFeedbackManager.performConfirm()
        verify(hapticFeedbackManager).performConfirm()
    }

    @Test
    fun testPerformRejectTriggersPattern() {
        hapticFeedbackManager.performReject()
        verify(hapticFeedbackManager).performReject()
    }

    @Test
    fun testPerformLightClickTriggersPattern() {
        hapticFeedbackManager.performLightClick()
        verify(hapticFeedbackManager).performLightClick()
    }

    @Test
    fun testPerformHeavyClickTriggersPattern() {
        hapticFeedbackManager.performHeavyClick()
        verify(hapticFeedbackManager).performHeavyClick()
    }
}
