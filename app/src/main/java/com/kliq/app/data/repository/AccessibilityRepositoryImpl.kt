package com.kliq.app.data.repository

import com.kliq.app.data.model.AccessibilitySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityRepositoryImpl @Inject constructor() : AccessibilityRepository {

    private val _accessibilitySettings = MutableStateFlow(AccessibilitySettings())
    override val accessibilitySettings: StateFlow<AccessibilitySettings> = _accessibilitySettings.asStateFlow()

    override fun updateHighContrast(enabled: Boolean) {
        _accessibilitySettings.update { currentState ->
            currentState.copy(isHighContrastEnabled = enabled)
        }
    }

    override fun updateFontScale(scale: Float) {
        val clampedScale = scale.coerceIn(0.8f, 2.0f)
        _accessibilitySettings.update { currentState ->
            currentState.copy(fontScale = clampedScale)
        }
    }

    override fun updateTalkBackOptimized(enabled: Boolean) {
        _accessibilitySettings.update { currentState ->
            currentState.copy(isTalkBackOptimized = enabled)
        }
    }

    override fun updateReduceMotion(enabled: Boolean) {
        _accessibilitySettings.update { currentState ->
            currentState.copy(isReduceMotionEnabled = enabled)
        }
    }

    override fun resetToDefaults() {
        _accessibilitySettings.value = AccessibilitySettings()
    }
}
