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

    val phoneNumber: String = savedStateHandle.get<String>(PHONE_NUMBER_KEY) ?: ""

    private val _verificationState = MutableStateFlow<VerificationUiState>(VerificationUiState.Idle)
    val verificationState: StateFlow<VerificationUiState> = _verificationState.asStateFlow()

    private val _enteredCode = MutableStateFlow("")
    val enteredCode: StateFlow<String> = _enteredCode.asStateFlow()

    private val _resendTimerState = MutableStateFlow(ResendTimerState())
    val resendTimerState: StateFlow<ResendTimerState> = _resendTimerState.asStateFlow()

    private var timerJob: Job? = null

    init {

        sendInitialCode()
    }

    private fun sendInitialCode() {
        viewModelScope.launch {
            verificationService.sendVerificationCode(phoneNumber)
            startResendTimer()
        }
    }

    fun onCodeChanged(code: String) {
        val filtered = code.filter { it.isDigit() }.take(CODE_LENGTH)
        _enteredCode.value = filtered

        if (_verificationState.value is VerificationUiState.Error) {
            _verificationState.value = VerificationUiState.Idle
        }

        if (filtered.length == CODE_LENGTH) {
            verifyCode()
        }
    }

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

                    _enteredCode.value = ""
                }
        }
    }

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
