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
 * für den Telefonnummer-Login im Onboarding-Prozess.
 *
 * Strikte MVVM-Trennlinie: Enthält keinerlei Android-UI-Referenzen.
 */
@HiltViewModel
class PhoneLoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneLoginUiState())
    val uiState: StateFlow<PhoneLoginUiState> = _uiState.asStateFlow()

    /**
     * Aktualisiert die gewählte Ländervorwahl und führt eine erneute Validierung der Telefonnummer durch.
     */
    fun onCountrySelected(country: CountryCodeOption) {
        _uiState.update { currentState ->
            val updatedState = currentState.copy(
                selectedCountry = country,
                countryCode = country.phonePrefix
            )
            validatePhoneNumber(updatedState.phoneNumber, updatedState)
        }
    }

    /**
     * Verarbeitet Änderungen der Telefonnummer-Eingabe, bereinigt Sonderzeichen und validiert das Ziffernformat.
     */
    fun onPhoneNumberChanged(input: String) {
        // Nur Ziffern und Leerzeichen zulassen
        val cleanInput = input.filter { it.isDigit() || it.isWhitespace() }
        
        _uiState.update { currentState ->
            validatePhoneNumber(cleanInput, currentState)
        }
    }

    /**
     * Verarbeitet die Eingabe des 6-stelligen OTP-Bestätigungscodes.
     */
    fun onOtpCodeChanged(input: String) {
        val digitsOnly = input.filter { it.isDigit() }.take(6)
        val isValidOtp = digitsOnly.length == 6

        _uiState.update { currentState ->
            currentState.copy(
                otpCode = digitsOnly,
                isValidOtp = isValidOtp,
                errorMessage = null
            )
        }
    }

    /**
     * Sendet die OTP-Anforderung ab, sobald die Eingabe validiert ist.
     */
    fun sendOtp() {
        val currentState = _uiState.value
        if (!currentState.isValidPhoneNumber || currentState.isLoading) return

        val digitsOnly = currentState.phoneNumber.filter { it.isDigit() }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = userRepository.requestOtp(currentState.countryCode, digitsOnly)
            
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isOtpSent = true,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isOtpSent = false,
                        errorMessage = error.localizedMessage ?: "Fehler beim Anfordern des OTP-Codes."
                    )
                }
            }
        }
    }

    /**
     * Überprüft den eingegebenen OTP-Code und schließt die Authentifizierung ab.
     */
    fun verifyOtp() {
        val currentState = _uiState.value
        if (!currentState.isValidOtp || currentState.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val digitsOnlyPhone = currentState.phoneNumber.filter { it.isDigit() }
            val result = userRepository.verifyOtp(
                countryCode = currentState.countryCode,
                phoneNumber = digitsOnlyPhone,
                otpCode = currentState.otpCode
            )

            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = error.localizedMessage ?: "Der eingegebener Code ist ungültig."
                    )
                }
            }
        }
    }

    /**
     * Setzt den OTP-Versandstatus zurück, um die Telefonnummer erneut bearbeiten zu können.
     */
    fun resetOtpState() {
        _uiState.update { state ->
            state.copy(
                isOtpSent = false,
                otpCode = "",
                isValidOtp = false,
                errorMessage = null
            )
        }
    }

    /**
     * Löscht aktive System- oder Formular-Fehlermeldungen.
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Interne Validierungslogik für das Ziffernformat und die Länge der Telefonnummer.
     */
    private fun validatePhoneNumber(inputNumber: String, state: PhoneLoginUiState): PhoneLoginUiState {
        val digitsOnly = inputNumber.filter { it.isDigit() }
        
        return when {
            inputNumber.isEmpty() -> {
                state.copy(
                    phoneNumber = inputNumber,
                    isValidPhoneNumber = false,
                    validationErrorMessage = null
                )
            }
            digitsOnly.length < 7 -> {
                state.copy(
                    phoneNumber = inputNumber,
                    isValidPhoneNumber = false,
                    validationErrorMessage = "Telefonnummer zu kurz (mindestens 7 Ziffern)."
                )
            }
            digitsOnly.length > 15 -> {
                state.copy(
                    phoneNumber = inputNumber,
                    isValidPhoneNumber = false,
                    validationErrorMessage = "Telefonnummer zu lang (maximal 15 Ziffern)."
                )
            }
            else -> {
                state.copy(
                    phoneNumber = inputNumber,
                    isValidPhoneNumber = true,
                    validationErrorMessage = null
                )
            }
        }
    }
}
