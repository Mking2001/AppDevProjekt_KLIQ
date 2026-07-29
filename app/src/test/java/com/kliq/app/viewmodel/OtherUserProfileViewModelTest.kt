package com.kliq.app.viewmodel

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
import com.kliq.app.ui.screens.profile.OtherUserProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class OtherUserProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val reviewRepository: ReviewRepository = mock(ReviewRepository::class.java)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserProfile emits success state with mapped entity and preferences`() = runTest {
        val targetUserId = "user_456"
        val mockUser = UserEntity(
            id = targetUserId,
            username = "Sarah_Kliq",
            email = "sarah@kliq.app",
            age = 25,
            hometown = "Berlin",
            profilePictureUrl = "https://example.com/sarah.jpg",
            bio = "Techno Fan & Festival Lover",
            isVerified = true
        )
        val mockPrefs = UserPreferencesEntity(
            userId = targetUserId,
            searchIntent = SearchIntent.FRIENDS,
            smokingHabit = SmokingHabit.NEVER,
            drinkingHabit = DrinkingHabit.SOCIAL
        )
        val mockReviews = listOf(
            Review(
                id = "rev_10",
                reviewerUserId = "rev_user",
                targetUserId = targetUserId,
                rating = 5,
                text = "Toller Mensch!",
                timestamp = System.currentTimeMillis(),
                verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                isVerified = true
            )
        )

        `when`(userRepository.getUserById(targetUserId)).thenReturn(flowOf(mockUser))
        `when`(userRepository.getUserPreferences(targetUserId)).thenReturn(flowOf(mockPrefs))
        `when`(reviewRepository.getReviewsForTargetUser(targetUserId)).thenReturn(flowOf(mockReviews))
        `when`(reviewRepository.getAverageRatingForTargetUser(targetUserId)).thenReturn(flowOf(5.0))
        `when`(userRepository.isUserBlocked("current_user", targetUserId)).thenReturn(flowOf(false))

        val savedStateHandle = SavedStateHandle(mapOf("userId" to targetUserId))
        val viewModel = OtherUserProfileViewModel(userRepository, reviewRepository, savedStateHandle)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(targetUserId, state.userId)
        assertEquals("Sarah_Kliq", state.username)
        assertEquals(25, state.age)
        assertEquals("Berlin", state.hometown)
        assertTrue(state.isVerified)
        assertEquals(SearchIntent.FRIENDS, state.searchIntent)
        assertEquals(SmokingHabit.NEVER, state.smokingHabit)
        assertEquals(DrinkingHabit.SOCIAL, state.drinkingHabit)
        assertEquals(5.0, state.averageRating, 0.01)
        assertEquals(1, state.reviewCount)
    }

    @Test
    fun `loadUserProfile with empty repository fallback loads demo fallback state`() = runTest {
        val targetUserId = "non_existent_user"
        `when`(userRepository.getUserById(targetUserId)).thenReturn(flowOf(null))
        `when`(userRepository.getUserPreferences(targetUserId)).thenReturn(flowOf(null))
        `when`(reviewRepository.getReviewsForTargetUser(targetUserId)).thenReturn(flowOf(emptyList()))
        `when`(reviewRepository.getAverageRatingForTargetUser(targetUserId)).thenReturn(flowOf(null))
        `when`(userRepository.isUserBlocked("current_user", targetUserId)).thenReturn(flowOf(false))

        val savedStateHandle = SavedStateHandle(mapOf("userId" to targetUserId))
        val viewModel = OtherUserProfileViewModel(userRepository, reviewRepository, savedStateHandle)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.username)
        assertTrue(state.reviewCount >= 0)
    }

    @Test
    fun `toggleBlockUser updates state to blocked and unblocked`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("userId" to "user_123"))
        `when`(userRepository.getUserById("user_123")).thenReturn(flowOf(null))
        `when`(userRepository.getUserPreferences("user_123")).thenReturn(flowOf(null))
        `when`(reviewRepository.getReviewsForTargetUser("user_123")).thenReturn(flowOf(emptyList()))
        `when`(reviewRepository.getAverageRatingForTargetUser("user_123")).thenReturn(flowOf(null))
        `when`(userRepository.isUserBlocked("current_user", "user_123")).thenReturn(flowOf(false))

        val viewModel = OtherUserProfileViewModel(userRepository, reviewRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isBlocked)

        viewModel.toggleBlockUser()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isBlockConfirmationDialogVisible)

        viewModel.confirmBlockUser()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isBlocked)

        viewModel.toggleBlockUser()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isBlocked)
    }

    @Test
    fun `reportUser updates reported flag and sets action message`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("userId" to "user_123"))
        `when`(userRepository.getUserById("user_123")).thenReturn(flowOf(null))
        `when`(userRepository.getUserPreferences("user_123")).thenReturn(flowOf(null))
        `when`(reviewRepository.getReviewsForTargetUser("user_123")).thenReturn(flowOf(emptyList()))
        `when`(reviewRepository.getAverageRatingForTargetUser("user_123")).thenReturn(flowOf(null))
        `when`(userRepository.isUserBlocked("current_user", "user_123")).thenReturn(flowOf(false))

        val viewModel = OtherUserProfileViewModel(userRepository, reviewRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.reportUser("Fake Profile")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isReported)
        assertNotNull(viewModel.uiState.value.actionSuccessMessage)
    }
}
