package com.kliq.app.data.repository

import com.kliq.app.data.model.AccessibilitySettings
import kotlinx.coroutines.flow.StateFlow

interface AccessibilityRepository {
    val accessibilitySettings: StateFlow<AccessibilitySettings>

    fun updateHighContrast(enabled: Boolean)
    fun updateFontScale(scale: Float)
    fun updateTalkBackOptimized(enabled: Boolean)
    fun updateReduceMotion(enabled: Boolean)
    fun resetToDefaults()
}
