package com.kliq.app.ui.screens.onboarding

/**
 * UI State for the Profile Creation screen in the Onboarding flow.
 */
data class ProfileCreationUiState(
    val username: String = "",
    val usernameError: String? = null,
    val age: String = "",
    val ageError: String? = null,
    val hometown: String = "",
    val hometownError: String? = null,
    val bio: String = "",
    val bioError: String? = null,
    val profilePictureUrl: String? = null,
    val isProcessingImage: Boolean = false,
    val permissionDeniedMessage: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val isProfileSaved: Boolean = false,
    val errorMessage: String? = null
)
