package com.kliq.app.ui.screens.verification

sealed interface VerificationUiState {

    data object Idle : VerificationUiState

    data object Loading : VerificationUiState

    data object Success : VerificationUiState

    data class Error(val message: String) : VerificationUiState
}

data class ResendTimerState(
    val secondsRemaining: Int = 0,
    val canResend: Boolean = true
)
