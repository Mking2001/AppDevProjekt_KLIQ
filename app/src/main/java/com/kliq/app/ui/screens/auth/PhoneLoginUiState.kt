package com.kliq.app.ui.screens.auth

/**
 * UI-Zustand für den direkten Login mit Benutzername / E-Mail / Telefonnummer und Passwort.
 */
data class PhoneLoginUiState(
    val identifier: String = "",
    val identifierError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val isFormValid: Boolean = false
)
