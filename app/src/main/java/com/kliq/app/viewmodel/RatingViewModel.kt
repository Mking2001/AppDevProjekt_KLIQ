package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.RatingRepository
import com.kliq.app.service.VerificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RatingUiState(
    val reviewerUserId: String = "",
    val targetUserId: String = "",
    val rating: Int = 0,
    val text: String = "",
    val isRatingLocked: Boolean = true,
    val verificationMethod: ReviewVerificationMethod = ReviewVerificationMethod.UNVERIFIED,
    val verificationDetails: String = "Bewertung gesperrt: Bitte QR-Code scannen oder am selben Standort befinden.",
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val submittedReview: Review? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingRepository: RatingRepository,
    private val verificationService: VerificationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()

    fun initTargetUser(reviewerUserId: String, targetUserId: String) {
        _uiState.update {
            it.copy(
                reviewerUserId = reviewerUserId,
                targetUserId = targetUserId,
                isRatingLocked = true
            )
        }

        viewModelScope.launch {
            verificationService.observeVerificationStatus(reviewerUserId, targetUserId)
                .catch { error ->
                    _uiState.update { it.copy(errorMessage = error.localizedMessage) }
                }
                .collect { verificationResult ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isRatingLocked = !verificationResult.isVerified,
                            verificationMethod = verificationResult.method,
                            verificationDetails = verificationResult.verificationDetails
                        )
                    }
                }
        }
    }

    fun onRatingChanged(newRating: Int) {
        if (_uiState.value.isRatingLocked) return
        _uiState.update { it.copy(rating = newRating.coerceIn(1, 5)) }
    }

    fun onCommentChanged(newText: String) {
        if (_uiState.value.isRatingLocked) return
        _uiState.update { it.copy(text = newText) }
    }

    fun onQrCodeScanned(qrToken: String) {
        viewModelScope.launch {
            val result = verificationService.verifyQrScanToken(
                reviewerUserId = _uiState.value.reviewerUserId,
                targetUserId = _uiState.value.targetUserId,
                qrToken = qrToken
            )

            if (result.isVerified) {
                _uiState.update {
                    it.copy(
                        isRatingLocked = false,
                        verificationMethod = result.method,
                        verificationDetails = result.verificationDetails,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Ungültiger QR-Code für diesen Nutzer."
                    )
                }
            }
        }
    }

    fun submitRating() {
        val currentState = _uiState.value
        if (currentState.isRatingLocked) {
            _uiState.update {
                it.copy(errorMessage = "Bewertung gesperrt: Weder physische Nähe noch QR-Scan vorhanden.")
            }
            return
        }

        if (currentState.rating !in 1..5) {
            _uiState.update {
                it.copy(errorMessage = "Bitte wähle eine Sternebewertung zwischen 1 und 5 Sternen.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val result = ratingRepository.submitUserRating(
                reviewerUserId = currentState.reviewerUserId,
                targetUserId = currentState.targetUserId,
                rating = currentState.rating,
                text = currentState.text
            )

            result.onSuccess { review ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitSuccess = true,
                        submittedReview = review
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.localizedMessage ?: "Fehler beim Einreichen der Bewertung."
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
