package com.kliq.app.viewmodel

import com.kliq.app.data.model.AntiSpamVerificationResult
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.RatingRepository
import com.kliq.app.service.VerificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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

@OptIn(ExperimentalCoroutinesApi::class)
class RatingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRatingRepository: FakeRatingRepository
    private lateinit var fakeVerificationService: FakeVerificationService
    private lateinit var viewModel: RatingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRatingRepository = FakeRatingRepository()
        fakeVerificationService = FakeVerificationService()
        viewModel = RatingViewModel(fakeRatingRepository, fakeVerificationService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isLockedByDefault() {
        val state = viewModel.uiState.value
        assertTrue(state.isRatingLocked)
        assertEquals(ReviewVerificationMethod.UNVERIFIED, state.verificationMethod)
    }

    @Test
    fun initTargetUser_whenUnverified_remainsLocked() = runTest {
        fakeVerificationService.statusFlow.value = AntiSpamVerificationResult(
            isVerified = false,
            method = ReviewVerificationMethod.UNVERIFIED
        )

        viewModel.initTargetUser("user1", "user2")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isRatingLocked)
    }

    @Test
    fun initTargetUser_whenGpsVerified_unlocksDynamically() = runTest {
        fakeVerificationService.statusFlow.value = AntiSpamVerificationResult(
            isVerified = true,
            method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
            verificationDetails = "GPS Verified"
        )

        viewModel.initTargetUser("user1", "user2")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRatingLocked)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, viewModel.uiState.value.verificationMethod)
    }

    @Test
    fun onQrCodeScanned_whenValidToken_unlocksRating() = runTest {
        viewModel.initTargetUser("user1", "user2")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onQrCodeScanned("KLIQ_PASS_USER2")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRatingLocked)
        assertEquals(ReviewVerificationMethod.QR_CODE_SCAN, viewModel.uiState.value.verificationMethod)
    }

    @Test
    fun submitRating_whenLocked_showsErrorMessage() = runTest {
        viewModel.initTargetUser("user1", "user2")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errorMessage != null)
        assertFalse(viewModel.uiState.value.submitSuccess)
    }

    @Test
    fun submitRating_whenUnlocked_submitsSuccessfully() = runTest {
        fakeVerificationService.statusFlow.value = AntiSpamVerificationResult(
            isVerified = true,
            method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
        )

        viewModel.initTargetUser("user1", "user2")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onRatingChanged(5)
        viewModel.onCommentChanged("Erstklassige Erfahrung!")
        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.submitSuccess)
        assertEquals(5, viewModel.uiState.value.submittedReview?.rating)
    }

    private class FakeRatingRepository : RatingRepository {
        var shouldSucceed = true

        override fun getReviewsForTargetUser(targetUserId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getAverageRatingForUser(targetUserId: String): Flow<Double?> = flowOf(5.0)
        override suspend fun checkRatingVerification(
            reviewerUserId: String,
            targetUserId: String,
            qrToken: String?
        ): AntiSpamVerificationResult = AntiSpamVerificationResult(isVerified = true, method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH)

        override suspend fun submitUserRating(
            reviewerUserId: String,
            targetUserId: String,
            rating: Int,
            text: String,
            qrToken: String?
        ): Result<Review> {
            return if (shouldSucceed) {
                Result.success(
                    Review(
                        id = "rev123",
                        reviewerUserId = reviewerUserId,
                        targetUserId = targetUserId,
                        rating = rating,
                        text = text,
                        isVerified = true,
                        verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
                    )
                )
            } else {
                Result.failure(IllegalStateException("Bewertung gesperrt"))
            }
        }
    }

    private class FakeVerificationService : VerificationService {
        val statusFlow = MutableStateFlow(AntiSpamVerificationResult(isVerified = false, method = ReviewVerificationMethod.UNVERIFIED))

        override suspend fun verifyUserProximityOrQr(
            reviewerUserId: String,
            targetUserId: String,
            qrScanToken: String?
        ): AntiSpamVerificationResult = statusFlow.value

        override suspend fun verifyGpsLocationMatch(
            reviewerUserId: String,
            targetUserId: String
        ): AntiSpamVerificationResult = statusFlow.value

        override suspend fun verifyQrScanToken(
            reviewerUserId: String,
            targetUserId: String,
            qrToken: String
        ): AntiSpamVerificationResult {
            return if (qrToken.contains("KLIQ_PASS_")) {
                AntiSpamVerificationResult(isVerified = true, method = ReviewVerificationMethod.QR_CODE_SCAN)
            } else {
                AntiSpamVerificationResult(isVerified = false, method = ReviewVerificationMethod.UNVERIFIED)
            }
        }

        override fun observeVerificationStatus(
            reviewerUserId: String,
            targetUserId: String
        ): Flow<AntiSpamVerificationResult> = statusFlow
    }
}
