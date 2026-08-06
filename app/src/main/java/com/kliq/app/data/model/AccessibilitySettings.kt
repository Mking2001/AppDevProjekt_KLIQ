package com.kliq.app.data.model

data class AccessibilitySettings(
    val isHighContrastEnabled: Boolean = false,
    val fontScale: Float = 1.0f,
    val isTalkBackOptimized: Boolean = true,
    val isReduceMotionEnabled: Boolean = false
) {
    val isFontScaled: Boolean
        get() = fontScale > 1.05f
}
