package com.kliq.app.ui.screens.auth

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
