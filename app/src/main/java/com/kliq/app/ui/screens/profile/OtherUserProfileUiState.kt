package com.kliq.app.ui.screens.profile

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit

data class OtherUserProfileUiState(
    val isLoading: Boolean = true,
    val userId: String = "",
    val username: String = "",
    val age: Int? = null,
    val hometown: String? = null,
    val bio: String? = null,
    val profilePictureUrl: String? = null,
    val isVerified: Boolean = false,
    val searchIntent: SearchIntent = SearchIntent.BOTH,
    val smokingHabit: SmokingHabit = SmokingHabit.NEVER,
    val drinkingHabit: DrinkingHabit = DrinkingHabit.NEVER,
    val averageRating: Double = 0.0,
    val reviewCount: Int = 0,
    val reviews: List<Review> = emptyList(),
    val isBlocked: Boolean = false,
    val isReported: Boolean = false,
    val isRatingSheetVisible: Boolean = false,
    val isReportDialogVisible: Boolean = false,
    val isBlockConfirmationDialogVisible: Boolean = false,
    val isSubmittingRating: Boolean = false,
    val errorMessage: String? = null,
    val actionSuccessMessage: String? = null
)
