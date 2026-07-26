package com.kliq.app.viewmodel

import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.ReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class RatingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var viewModel: RatingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        reviewRepository = mock(ReviewRepository::class.java)
        viewModel = RatingViewModel(reviewRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasZeroRating_andSubmitDisabled() {
        val state = viewModel.uiState.value
        assertEquals(0, state.rating)
        assertEquals("", state.reviewText)
        assertEquals(300, state.maxTextLength)
        assertEquals(300, state.remainingCharacters)
        assertFalse(state.isSubmitEnabled)
        assertTrue(state.status is RatingSubmitStatus.Idle)
    }

    @Test
    fun onRatingChanged_updatesRating_andEnablesSubmitWhenAtLeastOneStar() {
        viewModel.onRatingChanged(4)
        val state = viewModel.uiState.value
        assertEquals(4, state.rating)
        assertTrue(state.isSubmitEnabled)
    }

    @Test
    fun onRatingChanged_clampsValueBetweenZeroAndFive() {
        viewModel.onRatingChanged(7)
        assertEquals(5, viewModel.uiState.value.rating)

        viewModel.onRatingChanged(-3)
        assertEquals(0, viewModel.uiState.value.rating)
    }

    @Test
    fun onReviewTextChanged_updatesText_andLimitsLengthToMaxTextLength() {
        val testText = "Großartige Location und super Stimmung!"
        viewModel.onReviewTextChanged(testText)
        assertEquals(testText, viewModel.uiState.value.reviewText)
        assertEquals(300 - testText.length, viewModel.uiState.value.remainingCharacters)

        val longText = "a".repeat(350)
        viewModel.onReviewTextChanged(longText)
        assertEquals(300, viewModel.uiState.value.reviewText.length)
        assertEquals(0, viewModel.uiState.value.remainingCharacters)
    }

    @Test
    fun submitRating_successfulSubmission_updatesStateToSuccess() = runTest {
        val authorId = "user_123"
        val targetUserId = "target_456"
        val mockReview = Review(
            id = "rev_999",
            reviewerUserId = authorId,
            targetUserId = targetUserId,
            rating = 5,
            text = "Super Abend!",
            timestamp = System.currentTimeMillis(),
            verificationMethod = ReviewVerificationMethod.UNVERIFIED,
            isVerified = false
        )

        `when`(
            reviewRepository.submitUnverifiedReview(
                reviewerUserId = authorId,
                clubId = null,
                eventId = null,
                targetUserId = targetUserId,
                rating = 5,
                text = "Super Abend!"
            )
        ).thenReturn(Result.success(mockReview))

        viewModel.setTarget(authorId = authorId, targetUserId = targetUserId)
        viewModel.onRatingChanged(5)
        viewModel.onReviewTextChanged("Super Abend!")

        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is RatingSubmitStatus.Success)
        assertEquals(mockReview, (state.status as RatingSubmitStatus.Success).review)
    }

    @Test
    fun submitRating_failedSubmission_updatesStateToError() = runTest {
        val authorId = "user_123"
        val targetUserId = "target_456"
        val errorMessage = "Netzwerkfehler beim Senden"

        `when`(
            reviewRepository.submitUnverifiedReview(
                reviewerUserId = authorId,
                clubId = null,
                eventId = null,
                targetUserId = targetUserId,
                rating = 4,
                text = ""
            )
        ).thenReturn(Result.failure(RuntimeException(errorMessage)))

        viewModel.setTarget(authorId = authorId, targetUserId = targetUserId)
        viewModel.onRatingChanged(4)

        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is RatingSubmitStatus.Error)
        assertEquals(errorMessage, (state.status as RatingSubmitStatus.Error).message)
    }

    @Test
    fun resetState_resetsRatingAndTextAndStatus() {
        viewModel.onRatingChanged(3)
        viewModel.onReviewTextChanged("Test Feedback")
        viewModel.resetState()

        val state = viewModel.uiState.value
        assertEquals(0, state.rating)
        assertEquals("", state.reviewText)
        assertTrue(state.status is RatingSubmitStatus.Idle)
    }
}
