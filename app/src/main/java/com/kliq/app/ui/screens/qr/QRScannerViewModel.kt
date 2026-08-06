package com.kliq.app.ui.screens.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.domain.usecase.QRScanResult
import com.kliq.app.domain.usecase.VerifyQRCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QRScannerUiState(
    val hasCameraPermission: Boolean = false,
    val isScanning: Boolean = true,
    val isProcessingScan: Boolean = false,
    val isFlashEnabled: Boolean = false,
    val scanResult: QRScanResult? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class QRScannerViewModel @Inject constructor(
    private val verifyQRCodeUseCase: VerifyQRCodeUseCase,
    private val sessionRepository: SessionRepository,
    private val hapticFeedbackManager: com.kliq.app.util.HapticFeedbackManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRScannerUiState())
    val uiState: StateFlow<QRScannerUiState> = _uiState.asStateFlow()

    private var lastScannedCode: String? = null
    private var lastScanTimeMs: Long = 0L

    fun onCameraPermissionGranted() {
        _uiState.update { it.copy(hasCameraPermission = true, errorMessage = null) }
    }

    fun onCameraPermissionDenied() {
        _uiState.update {
            it.copy(
                hasCameraPermission = false,
                errorMessage = "Kamera-Berechtigung erforderlich, um QR-Codes zu scannen."
            )
        }
    }

    fun toggleFlash() {
        _uiState.update { it.copy(isFlashEnabled = !it.isFlashEnabled) }
    }

    fun onQRCodeScanned(rawContent: String, triggerHaptics: () -> Unit = {}) {
        val currentState = _uiState.value
        if (!currentState.isScanning || currentState.isProcessingScan) return

        val now = System.currentTimeMillis()
        if (rawContent == lastScannedCode && (now - lastScanTimeMs) < 2000L) {
            return
        }

        lastScannedCode = rawContent
        lastScanTimeMs = now

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingScan = true, isScanning = false) }

            val currentUserId = sessionRepository.getUserId() ?: "user_me"
            val result = verifyQRCodeUseCase(currentUserId, rawContent)

            triggerHaptics()

            when (result) {
                is QRScanResult.Success -> {
                    hapticFeedbackManager?.performConfirm("QR Scan / Friend verification")
                    _uiState.update {
                        it.copy(
                            isProcessingScan = false,
                            scanResult = result,
                            successMessage = result.message,
                            errorMessage = null
                        )
                    }
                }
                is QRScanResult.AlreadyFriends -> {
                    hapticFeedbackManager?.performConfirm("QR Scan / Friend verification (Already Friends)")
                    _uiState.update {
                        it.copy(
                            isProcessingScan = false,
                            scanResult = result,
                            successMessage = result.message,
                            errorMessage = null
                        )
                    }
                }
                is QRScanResult.SelfScan -> {
                    hapticFeedbackManager?.performReject("QR Scan Self-Scan error")
                    _uiState.update {
                        it.copy(
                            isProcessingScan = false,
                            scanResult = result,
                            errorMessage = result.message
                        )
                    }
                }
                is QRScanResult.InvalidCode -> {
                    hapticFeedbackManager?.performReject("Invalid QR Code")
                    _uiState.update {
                        it.copy(
                            isProcessingScan = false,
                            scanResult = result,
                            errorMessage = result.message
                        )
                    }
                }
                is QRScanResult.Error -> {
                    hapticFeedbackManager?.performReject("QR Scan processing error")
                    _uiState.update {
                        it.copy(
                            isProcessingScan = false,
                            scanResult = result,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun resumeScanning() {
        _uiState.update {
            it.copy(
                isScanning = true,
                isProcessingScan = false,
                scanResult = null,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
