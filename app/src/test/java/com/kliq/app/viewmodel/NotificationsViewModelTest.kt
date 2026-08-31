package com.kliq.app.viewmodel

import com.kliq.app.data.model.Review
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.SocialRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.domain.CurrentUserProvider
import com.kliq.app.ui.screens.notifications.NotificationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val reviewRepository: ReviewRepository = mock(ReviewRepository::class.java)
    private val socialRepository: SocialRepository = mock(SocialRepository::class.java)
    private val sessionRepository: SessionRepository = mock(SessionRepository::class.java)
    private val userRepository: UserRepository = mock(UserRepository::class.java)

    private lateinit var currentUserProvider: CurrentUserProvider
    private lateinit var viewModel: NotificationsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(sessionRepository.getUserId()).thenReturn("user_1")
        currentUserProvider = CurrentUserProvider(sessionRepository, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_whenNoActivities_hasEmptyListAndZeroUnread() = runTest(testDispatcher) {
        `when`(reviewRepository.getReviewsForTargetUser(anyString())).thenReturn(flowOf(emptyList()))
        `when`(socialRepository.getFriendsForUser(anyString())).thenReturn(flowOf(emptyList()))

        viewModel = NotificationsViewModel(
            reviewRepository = reviewRepository,
            socialRepository = socialRepository,
            currentUserProvider = currentUserProvider
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.notifications.isEmpty())
        assertEquals(0, state.unreadCount)
    }

    @Test
    fun observeRealActivities_createsNotificationItemsFromReviews() = runTest(testDispatcher) {
        val testReview = Review(
            id = "r_1",
            reviewerUserId = "u_2",
            targetUserId = "user_1",
            rating = 5,
            text = "Super sympathisch!",
            timestamp = System.currentTimeMillis(),
            reviewerUsername = "Sarah"
        )
        `when`(reviewRepository.getReviewsForTargetUser(anyString())).thenReturn(flowOf(listOf(testReview)))
        `when`(socialRepository.getFriendsForUser(anyString())).thenReturn(flowOf(emptyList()))

        viewModel = NotificationsViewModel(
            reviewRepository = reviewRepository,
            socialRepository = socialRepository,
            currentUserProvider = currentUserProvider
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.notifications.size)
        assertTrue(state.notifications.first().text.contains("Sarah"))
        assertTrue(state.notifications.first().text.contains("5-Sterne"))
    }
}
