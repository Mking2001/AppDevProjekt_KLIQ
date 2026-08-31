package com.kliq.app.util

import android.view.HapticFeedbackConstants
import android.view.View

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

    fun triggerPattern(view: View, pattern: HapticFeedbackPattern) {
        when (pattern) {
            HapticFeedbackPattern.CONFIRM -> triggerMediumImpact(view)
            HapticFeedbackPattern.REJECT -> triggerHeavyImpact(view)
            HapticFeedbackPattern.LIGHT_CLICK -> triggerLightImpact(view)
            HapticFeedbackPattern.HEAVY_CLICK -> triggerHeavyImpact(view)
        }
    }
}
