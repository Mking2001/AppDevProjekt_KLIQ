package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.ui.model.ReviewHighContrastItemState
import com.kliq.app.ui.model.toHighContrastUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val clubId: String? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccessMessage: String? = null,
    val reviews: List<ReviewHighContrastItemState> = emptyList(),
    val verifiedReviewsOnly: Boolean = false,
    val averageRating: Double = 0.0,
    val errorMessage: String? = null
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun loadReviewsForClub(clubId: String) {
        _uiState.update { it.copy(clubId = clubId, isLoading = true) }

        viewModelScope.launch {
            combine(
                reviewRepository.getReviewsForClub(clubId),
                reviewRepository.getAverageRatingForClub(clubId)
            ) { reviewsList, avgRating ->
                val uiReviews = reviewsList.map { it.toHighContrastUiState() }
                Pair(uiReviews, avgRating ?: 0.0)
            }
            .catch { throwable ->
                _uiState.update { it.copy(isLoading = false, errorMessage = throwable.localizedMessage) }
            }
            .collect { (uiReviews, avgRating) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        reviews = uiReviews,
                        averageRating = avgRating
                    )
                }
            }
        }
    }

    fun submitReviewWithGps(
        reviewerUserId: String,
        clubId: String,
        rating: Int,
        text: String,
        userLat: Double,
        userLon: Double
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = reviewRepository.submitReviewWithGpsCheck(
                reviewerUserId = reviewerUserId,
                clubId = clubId,
                rating = rating,
                text = text,
                userLat = userLat,
                userLon = userLon
            )
            result.onSuccess { review ->
                val msg = if (review.isVerified) "Bewertung verifiziert und veröffentlicht!" else "Bewertung unverifiziert eingereicht."
                _uiState.update { it.copy(isSubmitting = false, submitSuccessMessage = msg) }
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = error.localizedMessage) }
            }
        }
    }

    fun submitReviewWithQr(
        reviewerUserId: String,
        targetId: String,
        rating: Int,
        text: String,
        qrToken: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = reviewRepository.submitReviewWithQrCheck(
                reviewerUserId = reviewerUserId,
                targetId = targetId,
                rating = rating,
                text = text,
                qrToken = qrToken
            )
            result.onSuccess { review ->
                val msg = if (review.isVerified) "Bewertung per QR-Scan verifiziert!" else "Bewertung eingereicht."
                _uiState.update { it.copy(isSubmitting = false, submitSuccessMessage = msg) }
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = error.localizedMessage) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, submitSuccessMessage = null) }
    }
}
