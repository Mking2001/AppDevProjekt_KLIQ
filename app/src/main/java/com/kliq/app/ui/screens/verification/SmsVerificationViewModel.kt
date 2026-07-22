package com.kliq.app.ui.screens.verification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.remote.SmsVerificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel für den SMS-Verifizierungs-Screen.
 *
 * Verwaltet die Code-Eingabe, den Verifizierungs-Flow (Idle → Loading → Success/Error)
 * und den Countdown-Timer für das erneute Senden des Codes.
 *
 * Die Telefonnummer wird als Navigation-Argument aus dem SavedStateHandle bezogen.
 */
@HiltViewModel
class SmsVerificationViewModel @Inject constructor(
    private val verificationService: SmsVerificationService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val PHONE_NUMBER_KEY = "phoneNumber"
        private const val CODE_LENGTH = 6
        private const val RESEND_COOLDOWN_SECONDS = 30
    }

    /** Telefonnummer aus dem Navigation-Argument. */
    val phoneNumber: String = savedStateHandle.get<String>(PHONE_NUMBER_KEY) ?: ""

    private val _verificationState = MutableStateFlow<VerificationUiState>(VerificationUiState.Idle)
    val verificationState: StateFlow<VerificationUiState> = _verificationState.asStateFlow()

    private val _enteredCode = MutableStateFlow("")
    val enteredCode: StateFlow<String> = _enteredCode.asStateFlow()

    private val _resendTimerState = MutableStateFlow(ResendTimerState())
    val resendTimerState: StateFlow<ResendTimerState> = _resendTimerState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Beim Öffnen des Screens direkt den ersten Code senden
        sendInitialCode()
    }

    /**
     * Sendet den ersten Verifizierungscode und startet den Resend-Timer.
     */
    private fun sendInitialCode() {
        viewModelScope.launch {
            verificationService.sendVerificationCode(phoneNumber)
            startResendTimer()
        }
    }

    /**
     * Aktualisiert die Code-Eingabe. Filtert alles außer Ziffern heraus
     * und löst bei vollständiger 6-stelliger Eingabe automatisch die
     * Verifizierung aus.
     */
    fun onCodeChanged(code: String) {
        val filtered = code.filter { it.isDigit() }.take(CODE_LENGTH)
        _enteredCode.value = filtered

        // Fehlerzustand zurücksetzen wenn der Nutzer tippt
        if (_verificationState.value is VerificationUiState.Error) {
            _verificationState.value = VerificationUiState.Idle
        }

        // Automatische Verifizierung bei 6 Ziffern
        if (filtered.length == CODE_LENGTH) {
            verifyCode()
        }
    }

    /**
     * Startet die Code-Verifizierung gegen den Service.
     * Setzt den State auf Loading und anschließend auf Success oder Error.
     */
    fun verifyCode() {
        val code = _enteredCode.value
        if (code.length != CODE_LENGTH) return

        viewModelScope.launch {
            _verificationState.value = VerificationUiState.Loading

            verificationService.verifyCode(phoneNumber, code)
                .onSuccess {
                    _verificationState.value = VerificationUiState.Success
                }
                .onFailure { error ->
                    _verificationState.value = VerificationUiState.Error(
                        error.message ?: "Verifizierung fehlgeschlagen"
                    )
                    // Code zurücksetzen damit der Nutzer es erneut versuchen kann
                    _enteredCode.value = ""
                }
        }
    }

    /**
     * Fordert einen neuen Verifizierungscode an und startet den Timer neu.
     * Nur möglich wenn der Cooldown abgelaufen ist.
     */
    fun resendCode() {
        if (!_resendTimerState.value.canResend) return

        viewModelScope.launch {
            _verificationState.value = VerificationUiState.Idle
            _enteredCode.value = ""

            verificationService.sendVerificationCode(phoneNumber)
                .onSuccess {
                    startResendTimer()
                }
                .onFailure { error ->
                    _verificationState.value = VerificationUiState.Error(
                        error.message ?: "Code konnte nicht gesendet werden"
                    )
                }
        }
    }

    /**
     * Startet den 30-Sekunden-Countdown für den Resend-Button.
     * Cancelt ggf. einen bereits laufenden Timer.
     */
    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _resendTimerState.update {
                ResendTimerState(secondsRemaining = RESEND_COOLDOWN_SECONDS, canResend = false)
            }

            for (remaining in RESEND_COOLDOWN_SECONDS downTo 1) {
                _resendTimerState.update { it.copy(secondsRemaining = remaining) }
                delay(1000L)
            }

            _resendTimerState.update {
                ResendTimerState(secondsRemaining = 0, canResend = true)
            }
        }
    }
}
