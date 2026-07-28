package com.kliq.app.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val targetUserId: String = savedStateHandle.get<String>("userId")
        ?: savedStateHandle.get<String>("targetUserId")
        ?: ""

    private val _uiState = MutableStateFlow(OtherUserProfileUiState(userId = targetUserId))
    val uiState: StateFlow<OtherUserProfileUiState> = _uiState.asStateFlow()

    init {
        if (targetUserId.isNotBlank()) {
            loadUserProfile(targetUserId)
        } else {
            loadFallbackData("user_demo")
        }
    }

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, userId = userId) }

            combine(
                userRepository.getUserById(userId),
                userRepository.getUserPreferences(userId),
                reviewRepository.getReviewsForTargetUser(userId),
                reviewRepository.getAverageRatingForTargetUser(userId),
                userRepository.isUserBlocked("current_user", userId)
            ) { userEntity, userPrefs, reviewsList, avgRating, isBlocked ->
                if (userEntity != null) {
                    val actualAvg = avgRating ?: if (reviewsList.isNotEmpty()) {
                        reviewsList.map { it.rating }.average()
                    } else {
                        4.5
                    }
                    OtherUserProfileUiState(
                        isLoading = false,
                        userId = userEntity.id,
                        username = userEntity.username.ifBlank { "User_${userId.take(6)}" },
                        age = userEntity.age ?: 24,
                        hometown = userEntity.hometown ?: "München, Deutschland",
                        bio = userEntity.bio ?: "Nightlife Enthusiast 🌙 | Kliq Member",
                        profilePictureUrl = userEntity.profilePictureUrl,
                        isVerified = userEntity.isVerified,
                        searchIntent = userPrefs?.searchIntent ?: SearchIntent.BOTH,
                        smokingHabit = userPrefs?.smokingHabit ?: SmokingHabit.NEVER,
                        drinkingHabit = userPrefs?.drinkingHabit ?: DrinkingHabit.SOCIAL,
                        averageRating = actualAvg,
                        reviewCount = reviewsList.size,
                        reviews = reviewsList,
                        isBlocked = isBlocked,
                        isReported = false,
                        errorMessage = null
                    )
                } else {
                    buildMockFallbackState(userId)
                }
            }.catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Fehler beim Laden des Profils."
                    )
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun buildMockFallbackState(userId: String): OtherUserProfileUiState {
        val mockReviews = listOf(
            Review(
                id = UUID.randomUUID().toString(),
                reviewerUserId = "rev_1",
                targetUserId = userId,
                rating = 5,
                text = "Sehr sympathische Begleitung im Club! Gerne wieder 🎉",
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                isVerified = true,
                reviewerUsername = "Elena_M"
            ),
            Review(
                id = UUID.randomUUID().toString(),
                reviewerUserId = "rev_2",
                targetUserId = userId,
                rating = 4,
                text = "Super entspannt beim Event getroffen.",
                timestamp = System.currentTimeMillis() - 86400000L * 5,
                verificationMethod = ReviewVerificationMethod.QR_CODE_SCAN,
                isVerified = true,
                reviewerUsername = "Alex_K"
            )
        )

        return OtherUserProfileUiState(
            isLoading = false,
            userId = userId,
            username = if (userId == "user_demo" || userId.isBlank()) "Sophie_Vibe" else "Nutzer_${userId.take(6)}",
            age = 23,
            hometown = "München",
            bio = "Techno & House Lover 🎶 | Immer offen für gute Kliqs & VIP-Lounges | München 📍",
            profilePictureUrl = null,
            isVerified = true,
            searchIntent = SearchIntent.BOTH,
            smokingHabit = SmokingHabit.OCCASIONALLY,
            drinkingHabit = DrinkingHabit.SOCIAL,
            averageRating = 4.8,
            reviewCount = 12,
            reviews = mockReviews,
            isBlocked = false,
            isReported = false,
            errorMessage = null
        )
    }

    private fun loadFallbackData(userId: String) {
        _uiState.value = buildMockFallbackState(userId)
    }

    fun openRatingSheet() {
        _uiState.update { it.copy(isRatingSheetVisible = true) }
    }

    fun closeRatingSheet() {
        _uiState.update { it.copy(isRatingSheetVisible = false) }
    }

    fun submitRating(rating: Int, text: String) {
        val currentUserId = _uiState.value.userId
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingRating = true) }
            val result = reviewRepository.submitUnverifiedReview(
                reviewerUserId = "current_user",
                targetUserId = currentUserId,
                rating = rating,
                text = text
            )

            result.onSuccess { newReview ->
                val updatedReviews = listOf(newReview) + _uiState.value.reviews
                val newAvg = updatedReviews.map { it.rating }.average()
                _uiState.update {
                    it.copy(
                        isSubmittingRating = false,
                        isRatingSheetVisible = false,
                        reviews = updatedReviews,
                        reviewCount = updatedReviews.size,
                        averageRating = newAvg,
                        actionSuccessMessage = "Bewertung erfolgreich abgegeben!"
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isSubmittingRating = false,
                        errorMessage = ex.localizedMessage ?: "Fehler beim Abgeben der Bewertung."
                    )
                }
            }
        }
    }

    fun openReportDialog() {
        _uiState.update { it.copy(isReportDialogVisible = true) }
    }

    fun closeReportDialog() {
        _uiState.update { it.copy(isReportDialogVisible = false) }
    }

    fun reportUser(reason: String, details: String = "") {
        val targetId = _uiState.value.userId
        viewModelScope.launch {
            userRepository.reportUser("current_user", targetId, reason, details)
            _uiState.update {
                it.copy(
                    isReported = true,
                    isReportDialogVisible = false,
                    actionSuccessMessage = "Profil wurde erfolgreich gemeldet. Das Kliq-Sicherheitsteam überprüft den Fall."
                )
            }
        }
    }

    fun openBlockConfirmationDialog() {
        _uiState.update { it.copy(isBlockConfirmationDialogVisible = true) }
    }

    fun closeBlockConfirmationDialog() {
        _uiState.update { it.copy(isBlockConfirmationDialogVisible = false) }
    }

    fun toggleBlockUser() {
        if (_uiState.value.isBlocked) {
            unblockUser()
        } else {
            openBlockConfirmationDialog()
        }
    }

    fun confirmBlockUser(reason: String? = null) {
        val targetId = _uiState.value.userId
        viewModelScope.launch {
            userRepository.blockUser("current_user", targetId, reason)
            _uiState.update {
                it.copy(
                    isBlocked = true,
                    isBlockConfirmationDialogVisible = false,
                    actionSuccessMessage = "Nutzer wurde blockiert."
                )
            }
        }
    }

    fun unblockUser() {
        val targetId = _uiState.value.userId
        viewModelScope.launch {
            userRepository.unblockUser("current_user", targetId)
            _uiState.update {
                it.copy(
                    isBlocked = false,
                    isBlockConfirmationDialogVisible = false,
                    actionSuccessMessage = "Blockierung aufgehoben."
                )
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(errorMessage = null, actionSuccessMessage = null) }
    }

    fun retry() {
        if (targetUserId.isNotBlank()) {
            loadUserProfile(targetUserId)
        } else {
            loadFallbackData("user_demo")
        }
    }
}
