package com.kliq.app.ui.screens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.theme.KliqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class OtherUserProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyOtherUserProfileScreenRendering_displaysProfileDetailsAndBadges() {
        val fakeUserRepo = FakeUserRepository()
        val fakeReviewRepo = FakeReviewRepository()
        val savedStateHandle = SavedStateHandle(mapOf("userId" to "user_test"))
        val viewModel = OtherUserProfileViewModel(fakeUserRepo, fakeReviewRepo, savedStateHandle)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                OtherUserProfileScreen(
                    userId = "user_test",
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Sophie_Vibe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Suchabsicht").assertIsDisplayed()
        composeTestRule.onNodeWithText("Freunde & Dating (Beides)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lifestyle & Konsumgewohnheiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reputation & Bewertungen").assertIsDisplayed()
    }

    private class FakeUserRepository : UserRepository {
        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(
            UserEntity(
                id = userId,
                username = "Sophie_Vibe",
                email = "sophie@kliq.app",
                age = 23,
                hometown = "München",
                bio = "Techno & House Lover 🎶",
                isVerified = true
            )
        )

        override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> = flowOf(
            UserPreferencesEntity(
                userId = userId,
                searchIntent = SearchIntent.BOTH,
                smokingHabit = SmokingHabit.OCCASIONALLY,
                drinkingHabit = DrinkingHabit.SOCIAL
            )
        )

        override suspend fun syncUserProfile(userId: String): Result<Unit> = Result.success(Unit)
        override suspend fun saveUser(user: UserEntity) {}
        override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) {}
        override suspend fun saveSearchIntent(userId: String, intent: SearchIntent) {}
        override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = Result.success(true)
        override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> = Result.success(
            UserEntity(id = userId, username = "Sophie_Vibe", email = "sophie@kliq.app")
        )
    }

    private class FakeReviewRepository : ReviewRepository {
        override fun getReviewsForClub(clubId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getVerifiedReviewsForClub(clubId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getReviewsForEvent(eventId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getReviewsForTargetUser(targetUserId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getAverageRatingForClub(clubId: String): Flow<Double?> = flowOf(4.8)
        override fun getAverageRatingForTargetUser(targetUserId: String): Flow<Double?> = flowOf(4.8)
        override fun getReviewCountForTargetUser(targetUserId: String): Flow<Int> = flowOf(12)
        override suspend fun syncReviewsForClub(clubId: String): Result<Unit> = Result.success(Unit)
        override suspend fun submitReviewWithGpsCheck(reviewerUserId: String, clubId: String, rating: Int, text: String, userLat: Double, userLon: Double): Result<Review> = Result.failure(Exception())
        override suspend fun submitReviewWithQrCheck(reviewerUserId: String, targetId: String, rating: Int, text: String, qrToken: String): Result<Review> = Result.failure(Exception())
        override suspend fun submitUnverifiedReview(reviewerUserId: String, clubId: String?, eventId: String?, targetUserId: String?, rating: Int, text: String): Result<Review> = Result.failure(Exception())
    }
}
