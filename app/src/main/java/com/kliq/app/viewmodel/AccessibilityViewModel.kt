package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.AccessibilitySettings
import com.kliq.app.data.repository.AccessibilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AccessibilityUiState(
    val settings: AccessibilitySettings = AccessibilitySettings(),
    val isWcagCompliant: Boolean = true,
    val statusMessage: String? = null
)

@HiltViewModel
class AccessibilityViewModel @Inject constructor(
    private val accessibilityRepository: AccessibilityRepository
) : ViewModel() {

    val uiState: StateFlow<AccessibilityUiState> = accessibilityRepository.accessibilitySettings
        .map { settings ->
            AccessibilityUiState(
                settings = settings,
                isWcagCompliant = true,
                statusMessage = if (settings.isHighContrastEnabled) "High Contrast Aktiv" else "Standard Theme"
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccessibilityUiState()
        )

    fun toggleHighContrast() {
        val current = uiState.value.settings.isHighContrastEnabled
        accessibilityRepository.updateHighContrast(!current)
    }

    fun setHighContrastEnabled(enabled: Boolean) {
        accessibilityRepository.updateHighContrast(enabled)
    }

    fun setFontScale(scale: Float) {
        accessibilityRepository.updateFontScale(scale)
    }

    fun toggleTalkBackOptimizations() {
        val current = uiState.value.settings.isTalkBackOptimized
        accessibilityRepository.updateTalkBackOptimized(!current)
    }

    fun toggleReduceMotion() {
        val current = uiState.value.settings.isReduceMotionEnabled
        accessibilityRepository.updateReduceMotion(!current)
    }

    fun resetAccessibilityDefaults() {
        accessibilityRepository.resetToDefaults()
    }
}
