package com.kliq.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.AntiSpamVerificationResult
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.RatingRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
import com.kliq.app.service.VerificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RatingProximityIntegrationTest {

    private lateinit var fakeDao: FakeReviewDao
    private lateinit var fakeVerificationService: FakeVerificationService
    private lateinit var antiSpamValidator: AntiSpamReviewValidator
    private lateinit var repository: RatingRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakeReviewDao()
        fakeVerificationService = FakeVerificationService()
        antiSpamValidator = AntiSpamReviewValidator()
        repository = RatingRepositoryImpl(fakeDao, fakeVerificationService, antiSpamValidator)
    }

    @Test
    fun testEndToEnd_ratingSubmissionBlockedWhenUnverified() = runBlocking {
        fakeVerificationService.result = AntiSpamVerificationResult(
            isVerified = false,
            method = ReviewVerificationMethod.UNVERIFIED
        )

        val result = repository.submitUserRating("reviewer1", "target1", 5, "Super Club", null)

        assertTrue(result.isFailure)
        assertEquals(0, fakeDao.items.size)
    }

    @Test
    fun testEndToEnd_ratingSubmissionAllowedWhenGpsMatchOrQrScan() = runBlocking {
        fakeVerificationService.result = AntiSpamVerificationResult(
            isVerified = true,
            method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
            verificationDetails = "Match found"
        )

        val result = repository.submitUserRating("reviewer1", "target1", 5, "Unvergesslicher Abend", null)

        assertTrue(result.isSuccess)
        assertEquals(1, fakeDao.items.size)
        val saved = fakeDao.items.first()
        assertTrue(saved.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, saved.verificationMethod)
    }

    private class FakeReviewDao : ReviewDao {
        val items = mutableListOf<ReviewEntity>()

        override fun getReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getVerifiedReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForEvent(eventId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForTargetUser(targetUserId: String): Flow<List<ReviewEntity>> = flowOf(items)
        override fun getReviewsByReviewer(reviewerUserId: String): Flow<List<ReviewEntity>> = flowOf(items)
        override fun getAverageRatingForClub(clubId: String): Flow<Double?> = flowOf(null)
        override fun getAverageRatingForEvent(eventId: String): Flow<Double?> = flowOf(null)
        override suspend fun incrementHelpfulVotes(reviewId: String) {}
        override suspend fun flagReview(reviewId: String) {}
        override suspend fun insertReview(review: ReviewEntity) {
            items.add(review)
        }
        override suspend fun insertReviews(reviews: List<ReviewEntity>) {
            items.addAll(reviews)
        }
        override suspend fun deleteReviewById(reviewId: String) {}
    }

    private class FakeVerificationService : VerificationService {
        var result = AntiSpamVerificationResult(isVerified = false, method = ReviewVerificationMethod.UNVERIFIED)

        override suspend fun verifyUserProximityOrQr(
            reviewerUserId: String,
            targetUserId: String,
            qrScanToken: String?
        ): AntiSpamVerificationResult = result

        override suspend fun verifyGpsLocationMatch(
            reviewerUserId: String,
            targetUserId: String
        ): AntiSpamVerificationResult = result

        override suspend fun verifyQrScanToken(
            reviewerUserId: String,
            targetUserId: String,
            qrToken: String
        ): AntiSpamVerificationResult = result

        override fun observeVerificationStatus(
            reviewerUserId: String,
            targetUserId: String
        ): Flow<AntiSpamVerificationResult> = MutableStateFlow(result)
    }
}
