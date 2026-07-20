package com.kliq.app.util

import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Provides centralized haptic feedback actions for consistent user experience.
 */
object HapticFeedbackUtils {
    
    fun triggerLightImpact(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun triggerMediumImpact(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
    
    fun triggerHeavyImpact(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}
