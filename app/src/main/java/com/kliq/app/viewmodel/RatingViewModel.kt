package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.Review
import com.kliq.app.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val rating: Int = 0,
    val reviewText: String = "",
    val maxTextLength: Int = 300,
    val authorId: String = "",
    val targetUserId: String? = null,
    val clubId: String? = null,
    val eventId: String? = null,
    val status: RatingSubmitStatus = RatingSubmitStatus.Idle
) {
    val isSubmitEnabled: Boolean
        get() = rating in 1..5 && status !is RatingSubmitStatus.Submitting

    val remainingCharacters: Int
        get() = maxTextLength - reviewText.length
}

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()

    fun setTarget(
        authorId: String,
        targetUserId: String? = null,
        clubId: String? = null,
        eventId: String? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                authorId = authorId,
                targetUserId = targetUserId,
                clubId = clubId,
                eventId = eventId
            )
        }
    }

    fun onRatingChanged(newRating: Int) {
        val clampedRating = newRating.coerceIn(0, 5)
        _uiState.update { currentState ->
            currentState.copy(rating = clampedRating)
        }
    }

    fun onReviewTextChanged(newText: String) {
        val maxLength = _uiState.value.maxTextLength
        val sanitizedText = if (newText.length > maxLength) {
            newText.substring(0, maxLength)
        } else {
            newText
        }
        _uiState.update { currentState ->
            currentState.copy(reviewText = sanitizedText)
        }
    }

    fun submitRating() {
        val currentState = _uiState.value
        if (!currentState.isSubmitEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(status = RatingSubmitStatus.Submitting) }

            val result = reviewRepository.submitUnverifiedReview(
                reviewerUserId = currentState.authorId,
                clubId = currentState.clubId,
                eventId = currentState.eventId,
                targetUserId = currentState.targetUserId,
                rating = currentState.rating,
                text = currentState.reviewText
            )

            result.fold(
                onSuccess = { review ->
                    _uiState.update { it.copy(status = RatingSubmitStatus.Success(review)) }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            status = RatingSubmitStatus.Error(
                                throwable.localizedMessage ?: "Fehler beim Senden der Bewertung."
                            )
                        )
                    }
                }
            )
        }
    }

    fun resetState() {
        _uiState.update { currentState ->
            currentState.copy(
                rating = 0,
                reviewText = "",
                status = RatingSubmitStatus.Idle
            )
        }
    }
}
