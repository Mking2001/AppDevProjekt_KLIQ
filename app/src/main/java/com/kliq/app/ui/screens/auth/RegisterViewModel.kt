package com.kliq.app.ui.screens.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private var usernameCheckJob: Job? = null
    private var emailCheckJob: Job? = null
    private var phoneCheckJob: Job? = null

    fun onUsernameChanged(input: String) {
        val trimmed = input.trim()
        val error = when {
            input.isBlank() -> "Benutzername darf nicht leer sein."
            trimmed.length < 3 -> "Mindestens 3 Zeichen erforderlich."
            trimmed.length > 20 -> "Maximal 20 Zeichen erlaubt."
            !trimmed.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Nur Buchstaben, Zahlen und _ erlaubt."
            else -> null
        }

        _uiState.update { current ->
            val status = if (error != null) {
                UsernameCheckStatus.Invalid(error)
            } else {
                UsernameCheckStatus.Checking
            }
            val updated = current.copy(
                username = input,
                usernameError = error,
                usernameStatus = status
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }

        if (error == null && trimmed.length >= 3) {
            usernameCheckJob?.cancel()
            usernameCheckJob = viewModelScope.launch {
                delay(350)
                val isAvailable = userRepository.checkUsernameAvailability(trimmed)
                _uiState.update { current ->
                    val newStatus = if (isAvailable) {
                        UsernameCheckStatus.Available
                    } else {
                        UsernameCheckStatus.Taken("Dieser Benutzername ist bereits vergeben.")
                    }
                    val updated = current.copy(
                        usernameStatus = newStatus,
                        usernameError = if (isAvailable) null else "Dieser Benutzername ist bereits vergeben."
                    )
                    updated.copy(isFormValid = calculateIsFormValid(updated))
                }
            }
        }
    }

    fun onEmailChanged(input: String) {
        val trimmed = input.trim()
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val error = when {
            trimmed.isBlank() -> "E-Mail darf nicht leer sein."
            !emailRegex.matches(trimmed) -> "Bitte gib eine gültige E-Mail-Adresse ein."
            else -> null
        }

        _uiState.update { current ->
            val status = if (error != null) {
                EmailCheckStatus.Invalid(error)
            } else {
                EmailCheckStatus.Checking
            }
            val updated = current.copy(
                email = input,
                emailError = error,
                emailStatus = status
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }

        if (error == null && emailRegex.matches(trimmed)) {
            emailCheckJob?.cancel()
            emailCheckJob = viewModelScope.launch {
                delay(350)
                val isAvailable = userRepository.checkEmailAvailability(trimmed)
                _uiState.update { current ->
                    val newStatus = if (isAvailable) {
                        EmailCheckStatus.Available
                    } else {
                        EmailCheckStatus.Taken("Diese E-Mail-Adresse wird bereits verwendet.")
                    }
                    val updated = current.copy(
                        emailStatus = newStatus,
                        emailError = if (isAvailable) null else "Diese E-Mail-Adresse wird bereits verwendet."
                    )
                    updated.copy(isFormValid = calculateIsFormValid(updated))
                }
            }
        }
    }

    fun onFirstNameChanged(input: String) {
        val error = if (input.isBlank()) "Vorname darf nicht leer sein." else null
        _uiState.update { current ->
            val updated = current.copy(firstName = input, firstNameError = error)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onLastNameChanged(input: String) {
        val error = if (input.isBlank()) "Nachname darf nicht leer sein." else null
        _uiState.update { current ->
            val updated = current.copy(lastName = input, lastNameError = error)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onBirthDateSelected(timestampMs: Long) {
        val now = System.currentTimeMillis()
        val ageYears = ((now - timestampMs) / (365.25 * 24 * 60 * 60 * 1000L)).toInt()
        val error = when {
            timestampMs > now -> "Ungültiges Datum."
            ageYears < 18 -> "Du musst mindestens 18 Jahre alt sein (Mindestalter für Clubs)."
            ageYears > 120 -> "Bitte gib ein realistisches Geburtsdatum an."
            else -> null
        }

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        val formattedDate = formatter.format(Date(timestampMs))

        _uiState.update { current ->
            val updated = current.copy(
                birthDateMs = timestampMs,
                birthDateFormatted = formattedDate,
                birthDateError = error
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onPhotoSlotClicked(slotIndex: Int) {
        _uiState.update { it.copy(selectedPhotoSlotIndex = slotIndex) }
    }

    fun onPhotoSelected(context: Context, uri: Uri, slotIndex: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true, errorMessage = null) }
            val compressor = ImageCompressor(context)
            val result = compressor.compressAndSaveImage(uri)

            result.onSuccess { savedPath ->
                _uiState.update { current ->
                    val mutablePhotos = current.photos.toMutableList()
                    if (slotIndex < mutablePhotos.size) {
                        mutablePhotos[slotIndex] = savedPath
                    } else {
                        mutablePhotos.add(savedPath)
                    }
                    val updated = current.copy(
                        photos = mutablePhotos,
                        profilePictureError = null,
                        isProcessingImage = false
                    )
                    updated.copy(isFormValid = calculateIsFormValid(updated))
                }
            }.onFailure { exception ->
                _uiState.update { current ->
                    current.copy(
                        isProcessingImage = false,
                        profilePictureError = "Bild konnte nicht verarbeitet werden: ${exception.localizedMessage}"
                    )
                }
            }
        }
    }

    fun onRemovePhoto(slotIndex: Int) {
        _uiState.update { current ->
            val mutablePhotos = current.photos.toMutableList()
            if (slotIndex in mutablePhotos.indices) {
                mutablePhotos.removeAt(slotIndex)
            }
            val updated = current.copy(photos = mutablePhotos)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun openPreviewModal(initialIndex: Int = 0) {
        if (_uiState.value.photos.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    isPreviewModalOpen = true,
                    previewPhotoIndex = initialIndex.coerceIn(0, it.photos.size - 1)
                )
            }
        }
    }

    fun closePreviewModal() {
        _uiState.update { it.copy(isPreviewModalOpen = false) }
    }

    fun nextPreviewPhoto() {
        _uiState.update { current ->
            if (current.previewPhotoIndex < current.photos.size - 1) {
                current.copy(previewPhotoIndex = current.previewPhotoIndex + 1)
            } else current
        }
    }

    fun previousPreviewPhoto() {
        _uiState.update { current ->
            if (current.previewPhotoIndex > 0) {
                current.copy(previewPhotoIndex = current.previewPhotoIndex - 1)
            } else current
        }
    }

    fun onGenderSelected(gender: String) {
        _uiState.update { current ->
            val updated = current.copy(gender = gender)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onHometownChanged(input: String) {
        val suggestions = com.kliq.app.data.model.AustrianCities.filter(input)
        val isValid = com.kliq.app.data.model.AustrianCities.isValidCity(input)
        val error = when {
            input.isBlank() -> "Heimatstadt darf nicht leer sein."
            !isValid && input.length >= 2 -> "Bitte wähle eine Stadt aus der Liste aus."
            else -> null
        }
        _uiState.update { current ->
            val updated = current.copy(
                hometown = input,
                hometownSuggestions = suggestions,
                isHometownDropdownExpanded = suggestions.isNotEmpty() && !isValid,
                hometownError = error
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onHometownSuggestionSelected(city: String) {
        _uiState.update { current ->
            val updated = current.copy(
                hometown = city,
                hometownSuggestions = emptyList(),
                isHometownDropdownExpanded = false,
                hometownError = null
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onHometownDropdownDismiss() {
        _uiState.update { it.copy(isHometownDropdownExpanded = false) }
    }

    fun onCountryCodeChanged(code: String) {
        _uiState.update { current ->
            val updated = current.copy(countryCode = code, isCountryCodeDropdownExpanded = false)
            // Re-trigger phone availability check with new prefix
            checkPhoneAvailabilityAsync(updated.countryCode, updated.phoneNumber)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun toggleCountryCodeDropdown() {
        _uiState.update { it.copy(isCountryCodeDropdownExpanded = !it.isCountryCodeDropdownExpanded) }
    }

    fun dismissCountryCodeDropdown() {
        _uiState.update { it.copy(isCountryCodeDropdownExpanded = false) }
    }

    fun onPhoneNumberChanged(input: String) {
        val clean = input.filter { it.isDigit() || it.isWhitespace() }
        val digits = clean.filter { it.isDigit() }
        val error = when {
            digits.isBlank() -> "Telefonnummer darf nicht leer sein."
            digits.length < 4 -> "Mindestens 4 Ziffern erforderlich."
            digits.length > 15 -> "Maximal 15 Ziffern erlaubt."
            else -> null
        }
        _uiState.update { current ->
            val status = if (error != null) {
                PhoneCheckStatus.Invalid(error)
            } else {
                PhoneCheckStatus.Checking
            }
            val updated = current.copy(
                phoneNumber = clean,
                phoneNumberError = error,
                phoneStatus = status
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }

        if (error == null && digits.length >= 4) {
            checkPhoneAvailabilityAsync(_uiState.value.countryCode, clean)
        }
    }

    private fun checkPhoneAvailabilityAsync(countryCode: String, phoneInput: String) {
        val digits = phoneInput.filter { it.isDigit() }
        if (digits.length < 4) return
        val fullPhone = "$countryCode$digits"

        phoneCheckJob?.cancel()
        phoneCheckJob = viewModelScope.launch {
            delay(350)
            val isAvailable = userRepository.checkPhoneAvailability(fullPhone)
            _uiState.update { current ->
                val newStatus = if (isAvailable) {
                    PhoneCheckStatus.Available
                } else {
                    PhoneCheckStatus.Taken("Diese Telefonnummer ist bereits registriert.")
                }
                val updated = current.copy(
                    phoneStatus = newStatus,
                    phoneNumberError = if (isAvailable) null else "Diese Telefonnummer ist bereits registriert."
                )
                updated.copy(isFormValid = calculateIsFormValid(updated))
            }
        }
    }

    fun onSearchIntentSelected(intent: SearchIntent) {
        _uiState.update { current ->
            val updated = current.copy(searchIntent = intent)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onSmokingHabitSelected(habit: SmokingHabit) {
        _uiState.update { current ->
            val updated = current.copy(smokingHabit = habit)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onDrinkingHabitSelected(habit: DrinkingHabit) {
        _uiState.update { current ->
            val updated = current.copy(drinkingHabit = habit)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onBioChanged(input: String) {
        _uiState.update { it.copy(bio = input) }
    }

    fun onPasswordChanged(input: String) {
        val error = when {
            input.length < 6 -> "Passwort muss mindestens 6 Zeichen lang sein."
            else -> null
        }
        _uiState.update { current ->
            val confirmError = if (current.confirmPassword.isNotBlank() && current.confirmPassword != input) {
                "Passwörter stimmen nicht überein."
            } else null

            val updated = current.copy(
                password = input,
                passwordError = error,
                confirmPasswordError = confirmError
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onConfirmPasswordChanged(input: String) {
        val error = when {
            input != _uiState.value.password -> "Passwörter stimmen nicht überein."
            else -> null
        }
        _uiState.update { current ->
            val updated = current.copy(
                confirmPassword = input,
                confirmPasswordError = error
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onRegister() {
        val current = _uiState.value
        if (!calculateIsFormValid(current)) {
            _uiState.update { it.copy(errorMessage = "Bitte fülle alle Pflichtfelder korrekt aus.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Final check on username availability
            val isUserAvail = userRepository.checkUsernameAvailability(current.username.trim())
            if (!isUserAvail) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usernameStatus = UsernameCheckStatus.Taken("Dieser Benutzername ist leider bereits vergeben."),
                        usernameError = "Dieser Benutzername ist leider bereits vergeben.",
                        errorMessage = "Der Benutzername ist bereits vergeben. Bitte wähle einen anderen."
                    )
                }
                return@launch
            }

            // Final check on email availability
            val isEmailAvail = userRepository.checkEmailAvailability(current.email.trim())
            if (!isEmailAvail) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        emailStatus = EmailCheckStatus.Taken("Diese E-Mail-Adresse wird bereits verwendet."),
                        emailError = "Diese E-Mail-Adresse wird bereits verwendet.",
                        errorMessage = "Die E-Mail-Adresse wird bereits verwendet."
                    )
                }
                return@launch
            }

            val digitsPhone = current.phoneNumber.filter { it.isDigit() }
            val formattedFullPhone = "${current.countryCode}$digitsPhone"

            val isPhoneAvail = userRepository.checkPhoneAvailability(formattedFullPhone)
            if (!isPhoneAvail) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        phoneStatus = PhoneCheckStatus.Taken("Diese Telefonnummer ist bereits registriert."),
                        phoneNumberError = "Diese Telefonnummer ist bereits registriert.",
                        errorMessage = "Diese Telefonnummer ist bereits registriert."
                    )
                }
                return@launch
            }

            val primaryPhoto = current.photos.firstOrNull { it.isNotBlank() } ?: ""

            val result = userRepository.registerUser(
                username = current.username.trim(),
                email = current.email.trim(),
                firstName = current.firstName.trim(),
                lastName = current.lastName.trim(),
                birthDateMs = current.birthDateMs ?: 0L,
                gender = current.gender,
                hometown = current.hometown.trim(),
                countryCode = current.countryCode,
                phoneNumber = formattedFullPhone,
                profilePictureUrl = primaryPhoto,
                photos = current.photos,
                searchIntent = current.searchIntent,
                smokingHabit = current.smokingHabit,
                drinkingHabit = current.drinkingHabit,
                bio = current.bio.trim(),
                password = current.password
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isRegistrationSuccessful = true) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Registrierung fehlgeschlagen: ${error.localizedMessage ?: "Unbekannter Fehler"}"
                    )
                }
            }
        }
    }

    private fun calculateIsFormValid(state: RegisterUiState): Boolean {
        val usernameValid = state.username.isNotBlank() &&
                state.usernameError == null &&
                state.usernameStatus is UsernameCheckStatus.Available
        val emailValid = state.email.isNotBlank() &&
                state.emailError == null &&
                state.emailStatus !is EmailCheckStatus.Taken &&
                state.emailStatus !is EmailCheckStatus.Invalid
        val firstNameValid = state.firstName.isNotBlank() && state.firstNameError == null
        val lastNameValid = state.lastName.isNotBlank() && state.lastNameError == null
        val birthDateValid = state.birthDateMs != null && state.birthDateError == null
        val hometownValid = com.kliq.app.data.model.AustrianCities.isValidCity(state.hometown) && state.hometownError == null
        val phoneValid = state.phoneNumber.filter { it.isDigit() }.length in 4..15 &&
                state.phoneNumberError == null &&
                state.phoneStatus !is PhoneCheckStatus.Taken &&
                state.phoneStatus !is PhoneCheckStatus.Invalid
        val photosValid = state.photos.isNotEmpty()
        val passwordValid = state.password.length >= 6 &&
                state.passwordError == null &&
                state.confirmPassword == state.password &&
                state.confirmPasswordError == null

        return usernameValid && emailValid && firstNameValid && lastNameValid && birthDateValid && hometownValid && phoneValid && photosValid && passwordValid
    }
}
