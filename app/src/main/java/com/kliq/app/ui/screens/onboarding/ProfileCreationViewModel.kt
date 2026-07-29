package com.kliq.app.ui.screens.onboarding

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileCreationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileCreationUiState())
    val uiState: StateFlow<ProfileCreationUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(input: String) {
        val trimmed = input.trim()
        val error = when {
            input.isBlank() -> "Benutzername darf nicht leer sein."
            trimmed.length < 3 -> "Benutzername muss mindestens 3 Zeichen lang sein."
            trimmed.length > 20 -> "Benutzername darf maximal 20 Zeichen lang sein."
            !trimmed.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Nur Buchstaben, Zahlen und Unterstriche erlaubt."
            else -> null
        }

        _uiState.update { currentState ->
            val updated = currentState.copy(
                username = input,
                usernameError = error
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onAgeChanged(input: String) {
        val ageInt = input.toIntOrNull()
        val error = when {
            input.isBlank() -> "Alter darf nicht leer sein."
            ageInt == null -> "Bitte gib ein gültiges Alter ein."
            ageInt < 18 -> "Du musst mindestens 18 Jahre alt sein."
            ageInt > 120 -> "Bitte gib ein gültiges Alter ein."
            else -> null
        }

        _uiState.update { currentState ->
            val updated = currentState.copy(
                age = input,
                ageError = error
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onHometownChanged(input: String) {
        val error = when {
            input.isBlank() -> "Heimatstadt darf nicht leer sein."
            input.trim().length < 2 -> "Heimatstadt muss mindestens 2 Zeichen lang sein."
            else -> null
        }

        _uiState.update { currentState ->
            val updated = currentState.copy(
                hometown = input,
                hometownError = error
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onBioChanged(input: String) {
        val error = when {
            input.length > 150 -> "Bio darf maximal 150 Zeichen lang sein."
            else -> null
        }

        _uiState.update { currentState ->
            val updated = currentState.copy(
                bio = input,
                bioError = error
            )
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onImageSelected(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true, errorMessage = null) }
            val compressor = ImageCompressor(context)
            val result = compressor.compressAndSaveImage(uri)

            result.onSuccess { savedPath ->
                _uiState.update {
                    it.copy(
                        profilePictureUrl = savedPath,
                        isProcessingImage = false
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isProcessingImage = false,
                        errorMessage = exception.localizedMessage ?: "Fehler beim Verarbeiten des Bildes."
                    )
                }
            }
        }
    }

    fun onProfilePictureUrlSet(url: String?) {
        _uiState.update { it.copy(profilePictureUrl = url) }
    }

    fun onPermissionDenied(permission: String) {
        val message = when {
            permission.contains("CAMERA") -> "Kamerazugriff wurde verweigert. Erteile die Berechtigung in den Einstellungen."
            else -> "Zugriff auf Fotos wurde verweigert. Erteile die Berechtigung in den Einstellungen."
        }
        _uiState.update { it.copy(permissionDeniedMessage = message, errorMessage = message) }
    }

    fun onPermissionMessageDismissed() {
        _uiState.update { it.copy(permissionDeniedMessage = null) }
    }

    fun onSaveProfile(userId: String = "current_user") {
        val currentState = _uiState.value
        if (!calculateIsFormValid(currentState)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                userRepository.saveProfile(
                    userId = userId,
                    username = currentState.username.trim(),
                    age = currentState.age.trim().toInt(),
                    hometown = currentState.hometown.trim(),
                    bio = currentState.bio.trim(),
                    profilePictureUrl = currentState.profilePictureUrl
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isProfileSaved = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Fehler beim Speichern des Profils."
                    )
                }
            }
        }
    }

    private fun calculateIsFormValid(state: ProfileCreationUiState): Boolean {
        val ageInt = state.age.trim().toIntOrNull()
        val usernameValid = state.username.isNotBlank() &&
                state.username.trim().length in 3..20 &&
                state.username.trim().matches(Regex("^[a-zA-Z0-9_]+$"))
        val ageValid = ageInt != null && ageInt in 18..120
        val hometownValid = state.hometown.trim().length >= 2
        val bioValid = state.bio.length <= 150

        return usernameValid && ageValid && hometownValid && bioValid
    }
}
