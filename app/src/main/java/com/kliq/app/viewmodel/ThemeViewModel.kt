package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class ThemeMode { LIGHT, DARK, SYSTEM }

data class ThemeState(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val isNightOptimized: Boolean = true
)

@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel() {

    private val _themeState = MutableStateFlow(ThemeState())
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    fun toggleTheme() {
        _themeState.update { currentState ->
            val nextMode = when (currentState.themeMode) {
                ThemeMode.DARK -> ThemeMode.LIGHT
                ThemeMode.LIGHT -> ThemeMode.SYSTEM
                ThemeMode.SYSTEM -> ThemeMode.DARK
            }
            currentState.copy(themeMode = nextMode)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeState.update { it.copy(themeMode = mode) }
    }
}
