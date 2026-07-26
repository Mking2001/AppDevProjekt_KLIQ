package com.kliq.app.ui.screens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.theme.KliqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class OtherUserProfileUiIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testUserProfileNavigationAndCompleteContentRendering() {
        val targetUserId = "user_sophie_99"
        val fakeUserRepo = FakeUserRepository(
            user = UserEntity(
                id = targetUserId,
                username = "Sophie_Nightlife",
                email = "sophie@kliq.app",
                age = 24,
                hometown = "München",
                profilePictureUrl = "https://kliq.app/avatars/sophie.jpg",
                bio = "Techno Fan 🎶 | Immer unterwegs in München 📍",
                isVerified = true
            ),
            preferences = UserPreferencesEntity(
                userId = targetUserId,
                searchIntent = SearchIntent.BOTH,
                smokingHabit = SmokingHabit.OCCASIONALLY,
                drinkingHabit = DrinkingHabit.SOCIAL
            )
        )
        val fakeReviewRepo = FakeReviewRepository(
            reviews = listOf(
                Review(
                    id = UUID.randomUUID().toString(),
                    reviewerUserId = "rev_1",
                    targetUserId = targetUserId,
                    rating = 5,
                    text = "Super nette Begleitung im Club!",
                    timestamp = System.currentTimeMillis(),
                    verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                    isVerified = true,
                    reviewerUsername = "Elena_M"
                )
            ),
            avgRating = 4.9,
            count = 15
        )

        val savedStateHandle = SavedStateHandle(mapOf("userId" to targetUserId))
        val viewModel = OtherUserProfileViewModel(fakeUserRepo, fakeReviewRepo, savedStateHandle)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                OtherUserProfileScreen(
                    userId = targetUserId,
                    viewModel = viewModel
                )
            }
        }

        // 1. Basic User Header Verification
        composeTestRule.onNodeWithText("Sophie_Nightlife").assertIsDisplayed()
        composeTestRule.onNodeWithText("24 Jahre • ").assertIsDisplayed()
        composeTestRule.onNodeWithText("München").assertIsDisplayed()
        composeTestRule.onNodeWithText("Techno Fan 🎶 | Immer unterwegs in München 📍").assertIsDisplayed()

        // 2. Search Intent & Lifestyle Indicators Verification
        composeTestRule.onNodeWithText("Suchabsicht").assertIsDisplayed()
        composeTestRule.onNodeWithText("Freunde & Dating (Beides)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lifestyle & Konsumgewohnheiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gelegentlich").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gesellschaftlich").assertIsDisplayed()

        // 3. Reputation & Reviews Section Verification
        composeTestRule.onNodeWithText("Reputation & Bewertungen").assertIsDisplayed()
        composeTestRule.onNodeWithText("4,9").assertIsDisplayed()
        composeTestRule.onNodeWithText("15 Bewertungen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Elena_M").assertIsDisplayed()
        composeTestRule.onNodeWithText("Super nette Begleitung im Club!").assertIsDisplayed()
    }

    @Test
    fun testEmptyProfileFields_rendersWithoutCrashOrLayoutJitter() {
        val targetUserId = "user_minimal"
        val fakeUserRepo = FakeUserRepository(
            user = UserEntity(
                id = targetUserId,
                username = "MinimalUser",
                email = "minimal@kliq.app",
                age = null,
                hometown = null,
                profilePictureUrl = null,
                bio = null,
                isVerified = false
            ),
            preferences = UserPreferencesEntity(
                userId = targetUserId,
                searchIntent = SearchIntent.FRIENDS,
                smokingHabit = SmokingHabit.NEVER,
                drinkingHabit = DrinkingHabit.NEVER
            )
        )
        val fakeReviewRepo = FakeReviewRepository(reviews = emptyList(), avgRating = 0.0, count = 0)
        val savedStateHandle = SavedStateHandle(mapOf("userId" to targetUserId))
        val viewModel = OtherUserProfileViewModel(fakeUserRepo, fakeReviewRepo, savedStateHandle)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                OtherUserProfileScreen(
                    userId = targetUserId,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("MinimalUser").assertIsDisplayed()
        composeTestRule.onNodeWithText("Freunde finden").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nie").assertIsDisplayed()
    }

    @Test
    fun testUserRatingSheetInteraction() {
        val targetUserId = "user_interactive"
        val fakeUserRepo = FakeUserRepository(
            user = UserEntity(id = targetUserId, username = "TestUser", email = "test@kliq.app"),
            preferences = UserPreferencesEntity(userId = targetUserId)
        )
        val fakeReviewRepo = FakeReviewRepository(reviews = emptyList(), avgRating = 4.5, count = 1)
        val savedStateHandle = SavedStateHandle(mapOf("userId" to targetUserId))
        val viewModel = OtherUserProfileViewModel(fakeUserRepo, fakeReviewRepo, savedStateHandle)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                OtherUserProfileScreen(
                    userId = targetUserId,
                    viewModel = viewModel
                )
            }
        }

        // Click "Bewerten" button
        composeTestRule.onNodeWithText("Bewerten").performClick()

        // Verify Bottom Sheet displays rating controls
        composeTestRule.onNodeWithText("Nutzer bewerten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kommentar schreiben (optional)...").assertIsDisplayed()

        // Type comment and submit
        composeTestRule.onNodeWithText("Kommentar schreiben (optional)...").performTextInput("Super sympathischer Contact!")
        composeTestRule.onNodeWithText("Bewertung absenden").performClick()

        // Verify successful submission updates review count in State
        assertTrue(viewModel.uiState.value.reviews.any { it.text == "Super sympathischer Contact!" })
    }

    @Test
    fun testReportAndBlockDialogInteractions() {
        val targetUserId = "user_to_block"
        val fakeUserRepo = FakeUserRepository(
            user = UserEntity(id = targetUserId, username = "BadActor", email = "bad@kliq.app"),
            preferences = UserPreferencesEntity(userId = targetUserId)
        )
        val fakeReviewRepo = FakeReviewRepository()
        val savedStateHandle = SavedStateHandle(mapOf("userId" to targetUserId))
        val viewModel = OtherUserProfileViewModel(fakeUserRepo, fakeReviewRepo, savedStateHandle)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                OtherUserProfileScreen(
                    userId = targetUserId,
                    viewModel = viewModel
                )
            }
        }

        // Open options overflow menu
        composeTestRule.onNodeWithContentDescription("Optionen").performClick()
        composeTestRule.onNodeWithText("Profil melden").assertIsDisplayed()

        // Click "Profil melden"
        composeTestRule.onNodeWithText("Profil melden").performClick()
        composeTestRule.onNodeWithText("Bitte wähle den Grund für die Meldung:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Meldung absenden").performClick()

        assertTrue(viewModel.uiState.value.isReported)

        // Block user
        composeTestRule.onNodeWithContentDescription("Optionen").performClick()
        composeTestRule.onNodeWithText("Nutzer blockieren").performClick()

        composeTestRule.onNodeWithText("Du hast diesen Nutzer blockiert").assertIsDisplayed()
        assertTrue(viewModel.uiState.value.isBlocked)
    }

    private class FakeUserRepository(
        private val user: UserEntity,
        private val preferences: UserPreferencesEntity
    ) : UserRepository {
        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(user)
        override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> = flowOf(preferences)
        override suspend fun syncUserProfile(userId: String): Result<Unit> = Result.success(Unit)
        override suspend fun saveUser(user: UserEntity) {}
        override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) {}
        override suspend fun saveSearchIntent(userId: String, intent: SearchIntent) {}
        override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = Result.success(true)
        override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> = Result.success(user)
    }

    private class FakeReviewRepository(
        private val reviews: List<Review> = emptyList(),
        private val avgRating: Double = 4.5,
        private val count: Int = 5
    ) : ReviewRepository {
        override fun getReviewsForClub(clubId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getVerifiedReviewsForClub(clubId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getReviewsForEvent(eventId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getReviewsForTargetUser(targetUserId: String): Flow<List<Review>> = flowOf(reviews)
        override fun getAverageRatingForClub(clubId: String): Flow<Double?> = flowOf(avgRating)
        override fun getAverageRatingForTargetUser(targetUserId: String): Flow<Double?> = flowOf(avgRating)
        override fun getReviewCountForTargetUser(targetUserId: String): Flow<Int> = flowOf(count)
        override suspend fun syncReviewsForClub(clubId: String): Result<Unit> = Result.success(Unit)
        override suspend fun submitReviewWithGpsCheck(reviewerUserId: String, clubId: String, rating: Int, text: String, userLat: Double, userLon: Double): Result<Review> = Result.failure(Exception())
        override suspend fun submitReviewWithQrCheck(reviewerUserId: String, targetId: String, rating: Int, text: String, qrToken: String): Result<Review> = Result.failure(Exception())
        override suspend fun submitUnverifiedReview(reviewerUserId: String, clubId: String?, eventId: String?, targetUserId: String?, rating: Int, text: String): Result<Review> {
            val newReview = Review(
                id = UUID.randomUUID().toString(),
                reviewerUserId = reviewerUserId,
                targetUserId = targetUserId,
                rating = rating,
                text = text,
                timestamp = System.currentTimeMillis(),
                verificationMethod = ReviewVerificationMethod.UNVERIFIED,
                isVerified = false,
                reviewerUsername = "Current_User"
            )
            return Result.success(newReview)
        }
    }
}
