package com.kliq.app.ui.screens.auth

import com.kliq.app.data.model.SearchIntent

/**
 * Status der Username-Verfügbarkeitsprüfung gegen Room und Firebase Cloud SQL.
 */
sealed class UsernameCheckStatus {
    data object Idle : UsernameCheckStatus()
    data object Checking : UsernameCheckStatus()
    data object Available : UsernameCheckStatus()
    data class Taken(val message: String = "Dieser Benutzername ist bereits vergeben.") : UsernameCheckStatus()
    data class Invalid(val message: String) : UsernameCheckStatus()
}

/**
 * UI State für den vollständigen Registrierungs-Flow.
 */
data class RegisterUiState(
    val username: String = "",
    val usernameStatus: UsernameCheckStatus = UsernameCheckStatus.Idle,
    val usernameError: String? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val birthDateMs: Long? = null,
    val birthDateFormatted: String = "",
    val birthDateError: String? = null,
    val gender: String = "MALE",
    val hometown: String = "",
    val hometownError: String? = null,
    val profilePictureUrl: String? = null,
    val profilePictureError: String? = null,
    val searchIntent: SearchIntent = SearchIntent.BOTH,
    val bio: String = "",
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isProcessingImage: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false,
    val isFormValid: Boolean = false
)
