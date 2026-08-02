package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.ui.model.ReviewFilterState
import com.kliq.app.ui.model.ReviewHighContrastItemState
import com.kliq.app.ui.model.ReviewSortOption
import com.kliq.app.ui.model.StarFilterOption
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
    val targetUserId: String? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccessMessage: String? = null,
    val reviews: List<ReviewHighContrastItemState> = emptyList(),
    val commentReviews: List<ReviewHighContrastItemState> = emptyList(),
    val verifiedReviewsOnly: Boolean = false,
    val averageRating: Double = 0.0,
    val errorMessage: String? = null,

    // Filter und Sortierung
    val filterState: ReviewFilterState = ReviewFilterState(),

    // Kommentarsektions-Zustände (Kapitel 5.5)
    val commentInputText: String = "",
    val maxCommentLength: Int = 280,
    val selectedRating: Int = 5,
    val isVerificationLocked: Boolean = true,
    val activeVerificationMethod: ReviewVerificationMethod = ReviewVerificationMethod.UNVERIFIED,
    val isSectionEmpty: Boolean = true,
    val activeQrToken: String? = null
) {
    val remainingCharacters: Int
        get() = maxCommentLength - commentInputText.length

    val isCommentLengthValid: Boolean
        get() = commentInputText.trim().isNotEmpty() && commentInputText.length <= maxCommentLength
}

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var rawClubReviews: List<Review> = emptyList()

    fun loadReviewsForClub(clubId: String) {
        _uiState.update { it.copy(clubId = clubId, isLoading = true) }

        viewModelScope.launch {
            combine(
                reviewRepository.getReviewsForClub(clubId),
                reviewRepository.getAverageRatingForClub(clubId)
            ) { reviewsList, avgRating ->
                rawClubReviews = reviewsList
                val uiReviews = applyFilterAndSort(reviewsList, _uiState.value.filterState)
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

    fun setStarFilter(option: StarFilterOption) {
        _uiState.update { state ->
            val updatedFilterState = state.filterState.copy(selectedStarFilter = option)
            val filteredList = applyFilterAndSort(rawClubReviews, updatedFilterState)
            state.copy(
                filterState = updatedFilterState,
                reviews = filteredList
            )
        }
    }

    fun setSortOption(option: ReviewSortOption) {
        _uiState.update { state ->
            val updatedFilterState = state.filterState.copy(selectedSortOption = option)
            val filteredList = applyFilterAndSort(rawClubReviews, updatedFilterState)
            state.copy(
                filterState = updatedFilterState,
                reviews = filteredList
            )
        }
    }

    fun setVerifiedOnly(onlyVerified: Boolean) {
        _uiState.update { state ->
            val updatedFilterState = state.filterState.copy(onlyVerified = onlyVerified)
            val filteredList = applyFilterAndSort(rawClubReviews, updatedFilterState)
            state.copy(
                filterState = updatedFilterState,
                verifiedReviewsOnly = onlyVerified,
                reviews = filteredList
            )
        }
    }

    fun resetFilters() {
        _uiState.update { state ->
            val defaultFilter = ReviewFilterState()
            val filteredList = applyFilterAndSort(rawClubReviews, defaultFilter)
            state.copy(
                filterState = defaultFilter,
                verifiedReviewsOnly = false,
                reviews = filteredList
            )
        }
    }

    private fun applyFilterAndSort(
        reviews: List<Review>,
        filterState: ReviewFilterState
    ): List<ReviewHighContrastItemState> {
        var result = reviews

        if (filterState.onlyVerified) {
            result = result.filter { it.isVerified }
        }

        result = when (filterState.selectedStarFilter) {
            StarFilterOption.ALL -> result
            StarFilterOption.FIVE_STARS -> result.filter { it.rating == 5 }
            StarFilterOption.FOUR_PLUS_STARS -> result.filter { it.rating >= 4 }
            StarFilterOption.THREE_PLUS_STARS -> result.filter { it.rating >= 3 }
            StarFilterOption.TWO_PLUS_STARS -> result.filter { it.rating >= 2 }
            StarFilterOption.ONE_STAR -> result.filter { it.rating == 1 }
        }

        val sorted = when (filterState.selectedSortOption) {
            ReviewSortOption.NEWEST_FIRST -> result.sortedByDescending { it.timestamp }
            ReviewSortOption.OLDEST_FIRST -> result.sortedBy { it.timestamp }
            ReviewSortOption.HIGHEST_RATING -> result.sortedWith(
                compareByDescending<Review> { it.rating }.thenByDescending { it.timestamp }
            )
            ReviewSortOption.LOWEST_RATING -> result.sortedWith(
                compareBy<Review> { it.rating }.thenByDescending { it.timestamp }
            )
            ReviewSortOption.MOST_HELPFUL -> result.sortedByDescending { it.timestamp }
        }

        return sorted.map { it.toHighContrastUiState() }
    }

    fun loadCommentsForUser(targetUserId: String) {
        _uiState.update { it.copy(targetUserId = targetUserId, isLoading = true) }

        viewModelScope.launch {
            reviewRepository.getReviewsForTargetUser(targetUserId)
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.localizedMessage) }
                }
                .collect { reviewsList ->
                    val uiComments = reviewsList.map { it.toHighContrastUiState() }
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            commentReviews = uiComments,
                            isSectionEmpty = uiComments.isEmpty()
                        )
                    }
                }
        }
    }

    fun onCommentInputChanged(text: String) {
        val trimmedToMax = text.take(_uiState.value.maxCommentLength)
        _uiState.update { it.copy(commentInputText = trimmedToMax) }
    }

    fun onRatingSelected(rating: Int) {
        if (rating in 1..5) {
            _uiState.update { it.copy(selectedRating = rating) }
        }
    }

    fun updateVerificationLockStatus(isLocked: Boolean, method: ReviewVerificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH, qrToken: String? = null) {
        _uiState.update {
            it.copy(
                isVerificationLocked = isLocked,
                activeVerificationMethod = if (isLocked) ReviewVerificationMethod.UNVERIFIED else method,
                activeQrToken = qrToken
            )
        }
    }

    fun submitUserComment(reviewerUserId: String, targetUserId: String) {
        val currentState = _uiState.value

        if (currentState.isVerificationLocked) {
            _uiState.update {
                it.copy(errorMessage = "Sicherheits-Sperre aktiv: Kommentare erfordern physische Nähe (GPS) oder QR-Scan!")
            }
            return
        }

        if (!currentState.isCommentLengthValid) {
            _uiState.update {
                it.copy(errorMessage = "Kommentar muss zwischen 1 und 280 Zeichen lang sein.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = reviewRepository.submitVerifiedUserComment(
                reviewerUserId = reviewerUserId,
                targetUserId = targetUserId,
                rating = currentState.selectedRating,
                text = currentState.commentInputText,
                verificationMethod = currentState.activeVerificationMethod,
                qrToken = currentState.activeQrToken
            )

            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isSubmitting = false,
                        commentInputText = "",
                        submitSuccessMessage = "Verifizierter Kommentar erfolgreich veröffentlicht!"
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isSubmitting = false,
                        errorMessage = error.localizedMessage ?: "Fehler beim Veröffentlichen des Kommentars."
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
