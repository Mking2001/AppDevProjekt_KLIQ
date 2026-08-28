package com.kliq.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel zur Verwaltung des Formularstatus und der Validierungslogik
 * für den Login mit Benutzername, E-Mail oder Telefonnummer + Passwort.
 */
@HiltViewModel
class PhoneLoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneLoginUiState())
    val uiState: StateFlow<PhoneLoginUiState> = _uiState.asStateFlow()

    fun onIdentifierChanged(input: String) {
        val error = if (input.isBlank()) "Bitte gib deinen Benutzernamen, E-Mail oder Telefonnummer ein." else null
        _uiState.update { current ->
            val updated = current.copy(identifier = input, identifierError = error, errorMessage = null)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun onPasswordChanged(input: String) {
        val error = if (input.length < 6) "Passwort muss mindestens 6 Zeichen lang sein." else null
        _uiState.update { current ->
            val updated = current.copy(password = input, passwordError = error, errorMessage = null)
            updated.copy(isFormValid = calculateIsFormValid(updated))
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onLogin() {
        val current = _uiState.value
        if (!current.isFormValid || current.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = userRepository.loginUser(
                identifier = current.identifier.trim(),
                password = current.password
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Anmeldung fehlgeschlagen. Bitte Daten prüfen."
                    )
                }
            }
        }
    }

    private fun calculateIsFormValid(state: PhoneLoginUiState): Boolean {
        return state.identifier.isNotBlank() &&
                state.identifierError == null &&
                state.password.length >= 6 &&
                state.passwordError == null
    }
}
