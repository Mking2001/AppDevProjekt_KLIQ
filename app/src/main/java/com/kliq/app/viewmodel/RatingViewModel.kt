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

sealed interface RatingSubmitStatus {
    object Idle : RatingSubmitStatus
    object Submitting : RatingSubmitStatus
    data class Success(val review: Review) : RatingSubmitStatus
    data class Error(val message: String) : RatingSubmitStatus
}

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
    val errorMessage: String? = null,
    val maxTextLength: Int = 300,
    val status: RatingSubmitStatus = RatingSubmitStatus.Idle,
    val clubId: String? = null,
    val eventId: String? = null
) {
    val authorId: String
        get() = reviewerUserId

    val reviewText: String
        get() = text

    val isSubmitEnabled: Boolean
        get() = !isRatingLocked && rating in 1..5 && !isSubmitting && status !is RatingSubmitStatus.Submitting

    val remainingCharacters: Int
        get() = maxTextLength - text.length
}

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
                    _uiState.update { currentState ->
                        currentState.copy(
                            errorMessage = error.localizedMessage,
                            status = RatingSubmitStatus.Error(error.localizedMessage ?: "Fehler")
                        )
                    }
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

    fun setTarget(
        authorId: String,
        targetUserId: String? = null,
        clubId: String? = null,
        eventId: String? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                reviewerUserId = authorId,
                targetUserId = targetUserId ?: "",
                clubId = clubId,
                eventId = eventId
            )
        }
        if (targetUserId != null) {
            initTargetUser(authorId, targetUserId)
        }
    }

    fun onRatingChanged(newRating: Int) {
        if (_uiState.value.isRatingLocked) return
        _uiState.update { it.copy(rating = newRating.coerceIn(1, 5)) }
    }

    fun onCommentChanged(newText: String) {
        if (_uiState.value.isRatingLocked) return
        val maxLength = _uiState.value.maxTextLength
        val sanitized = if (newText.length > maxLength) newText.substring(0, maxLength) else newText
        _uiState.update { it.copy(text = sanitized) }
    }

    fun onReviewTextChanged(newText: String) {
        onCommentChanged(newText)
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
                val errorMsg = "Ungültiger QR-Code für diesen Nutzer."
                _uiState.update {
                    it.copy(
                        errorMessage = errorMsg,
                        status = RatingSubmitStatus.Error(errorMsg)
                    )
                }
            }
        }
    }

    fun submitRating() {
        val currentState = _uiState.value
        if (currentState.isRatingLocked) {
            val errorMsg = "Bewertung gesperrt: Weder physische Nähe noch QR-Scan vorhanden."
            _uiState.update {
                it.copy(
                    errorMessage = errorMsg,
                    status = RatingSubmitStatus.Error(errorMsg)
                )
            }
            return
        }

        if (currentState.rating !in 1..5) {
            val errorMsg = "Bitte wähle eine Sternebewertung zwischen 1 und 5 Sternen."
            _uiState.update {
                it.copy(
                    errorMessage = errorMsg,
                    status = RatingSubmitStatus.Error(errorMsg)
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    status = RatingSubmitStatus.Submitting,
                    errorMessage = null
                )
            }

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
                        submittedReview = review,
                        status = RatingSubmitStatus.Success(review)
                    )
                }
            }.onFailure { error ->
                val msg = error.localizedMessage ?: "Fehler beim Einreichen der Bewertung."
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = msg,
                        status = RatingSubmitStatus.Error(msg)
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { currentState ->
            currentState.copy(
                rating = 0,
                text = "",
                status = RatingSubmitStatus.Idle,
                errorMessage = null,
                submitSuccess = false,
                submittedReview = null
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
