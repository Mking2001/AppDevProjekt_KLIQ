package com.kliq.app.ui.screens.auth

import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit

sealed class UsernameCheckStatus {
    data object Idle : UsernameCheckStatus()
    data object Checking : UsernameCheckStatus()
    data object Available : UsernameCheckStatus()
    data class Taken(val message: String = "Dieser Benutzername ist bereits vergeben.") : UsernameCheckStatus()
    data class Invalid(val message: String) : UsernameCheckStatus()
}

sealed class EmailCheckStatus {
    data object Idle : EmailCheckStatus()
    data object Checking : EmailCheckStatus()
    data object Available : EmailCheckStatus()
    data class Taken(val message: String = "Diese E-Mail-Adresse wird bereits verwendet.") : EmailCheckStatus()
    data class Invalid(val message: String) : EmailCheckStatus()
}

sealed class PhoneCheckStatus {
    data object Idle : PhoneCheckStatus()
    data object Checking : PhoneCheckStatus()
    data object Available : PhoneCheckStatus()
    data class Taken(val message: String = "Diese Telefonnummer ist bereits registriert.") : PhoneCheckStatus()
    data class Invalid(val message: String) : PhoneCheckStatus()
}

data class RegisterUiState(
    val username: String = "",
    val usernameStatus: UsernameCheckStatus = UsernameCheckStatus.Idle,
    val usernameError: String? = null,
    val email: String = "",
    val emailStatus: EmailCheckStatus = EmailCheckStatus.Idle,
    val emailError: String? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val birthDateMs: Long? = null,
    val birthDateFormatted: String = "",
    val birthDateError: String? = null,
    val gender: String = "MALE",
    val hometown: String = "",
    val hometownSuggestions: List<String> = emptyList(),
    val isHometownDropdownExpanded: Boolean = false,
    val hometownError: String? = null,
    val countryCode: String = "+43",
    val isCountryCodeDropdownExpanded: Boolean = false,
    val phoneNumber: String = "",
    val phoneStatus: PhoneCheckStatus = PhoneCheckStatus.Idle,
    val phoneNumberError: String? = null,

    val photos: List<String> = emptyList(),
    val selectedPhotoSlotIndex: Int = 0,
    val profilePictureError: String? = null,

    val isPreviewModalOpen: Boolean = false,
    val previewPhotoIndex: Int = 0,
    val searchIntent: SearchIntent = SearchIntent.BOTH,
    val smokingHabit: SmokingHabit = SmokingHabit.OCCASIONALLY,
    val drinkingHabit: DrinkingHabit = DrinkingHabit.SOCIAL,
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
) {
    val profilePictureUrl: String?
        get() = photos.firstOrNull { it.isNotBlank() }
}
