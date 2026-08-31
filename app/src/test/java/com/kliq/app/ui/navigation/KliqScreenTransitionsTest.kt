package com.kliq.app.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class KliqScreenTransitionsTest {

    @Test
    fun testTransitionDurations_withinPerformanceBudgets() {
        assertEquals(300, KliqScreenTransitions.DURATION_TAB_SWITCH)
        assertEquals(320, KliqScreenTransitions.DURATION_DETAIL_PUSH)
        assertEquals(380, KliqScreenTransitions.DURATION_SHARED_ELEMENT)
        assertEquals(350, KliqScreenTransitions.DURATION_MODAL)
        assertEquals(250, KliqScreenTransitions.DURATION_FADE)
    }

    @Test
    fun testEasingCurves_nonNullAndDefined() {
        assertNotNull(KliqScreenTransitions.KliqDecelerationEasing)
        assertNotNull(KliqScreenTransitions.KliqAccelerateEasing)
    }
}
