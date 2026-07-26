package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.AntiSpamVerificationResult
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.util.AntiSpamReviewValidator
import com.kliq.app.service.VerificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RatingRepositoryTest {

    private lateinit var fakeReviewDao: FakeReviewDao
    private lateinit var fakeVerificationService: FakeVerificationService
    private lateinit var antiSpamValidator: AntiSpamReviewValidator
    private lateinit var repository: RatingRepositoryImpl

    @Before
    fun setUp() {
        fakeReviewDao = FakeReviewDao()
        fakeVerificationService = FakeVerificationService()
        antiSpamValidator = AntiSpamReviewValidator()
        repository = RatingRepositoryImpl(
            reviewDao = fakeReviewDao,
            verificationService = fakeVerificationService,
            antiSpamValidator = antiSpamValidator
        )
    }

    @Test
    fun submitUserRating_whenUnverified_blocksDatabaseInsertAndReturnsFailure() = runTest {
        fakeVerificationService.verificationResult = AntiSpamVerificationResult(
            isVerified = false,
            method = ReviewVerificationMethod.UNVERIFIED,
            confidenceScore = 0.0f,
            verificationDetails = "Unverified"
        )

        val result = repository.submitUserRating("reviewer1", "target1", 5, "Toller Abend!", null)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals(0, fakeReviewDao.insertedReviews.size)
    }

    @Test
    fun submitUserRating_whenInvalidRatingValue_returnsFailure() = runTest {
        fakeVerificationService.verificationResult = AntiSpamVerificationResult(
            isVerified = true,
            method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
        )

        val result = repository.submitUserRating("reviewer1", "target1", 6, "Ungültig", null)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(0, fakeReviewDao.insertedReviews.size)
    }

    @Test
    fun submitUserRating_whenGpsVerified_insertsIntoDatabaseAndReturnsSuccess() = runTest {
        fakeVerificationService.verificationResult = AntiSpamVerificationResult(
            isVerified = true,
            method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
            confidenceScore = 1.0f,
            verificationDetails = "GPS Verified"
        )

        val result = repository.submitUserRating("reviewer1", "target1", 4, "Super Stimmung", null)

        assertTrue(result.isSuccess)
        assertEquals(1, fakeReviewDao.insertedReviews.size)
        val saved = fakeReviewDao.insertedReviews.first()
        assertEquals("reviewer1", saved.reviewerUserId)
        assertEquals("target1", saved.targetUserId)
        assertEquals(4, saved.rating)
        assertTrue(saved.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, saved.verificationMethod)
    }

    private class FakeReviewDao : ReviewDao {
        val insertedReviews = mutableListOf<ReviewEntity>()

        override fun getReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getVerifiedReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForEvent(eventId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForTargetUser(targetUserId: String): Flow<List<ReviewEntity>> =
            flowOf(insertedReviews.filter { it.targetUserId == targetUserId })
        override fun getReviewsByReviewer(reviewerUserId: String): Flow<List<ReviewEntity>> =
            flowOf(insertedReviews.filter { it.reviewerUserId == reviewerUserId })
        override fun getAverageRatingForClub(clubId: String): Flow<Double?> = flowOf(null)
        override fun getAverageRatingForEvent(eventId: String): Flow<Double?> = flowOf(null)
        override suspend fun incrementHelpfulVotes(reviewId: String) {}
        override suspend fun flagReview(reviewId: String) {}
        override suspend fun insertReview(review: ReviewEntity) {
            insertedReviews.add(review)
        }
        override suspend fun insertReviews(reviews: List<ReviewEntity>) {
            insertedReviews.addAll(reviews)
        }
        override suspend fun deleteReviewById(reviewId: String) {}
    }

    private class FakeVerificationService : VerificationService {
        var verificationResult = AntiSpamVerificationResult(isVerified = false, method = ReviewVerificationMethod.UNVERIFIED)

        override suspend fun verifyUserProximityOrQr(
            reviewerUserId: String,
            targetUserId: String,
            qrScanToken: String?
        ): AntiSpamVerificationResult = verificationResult

        override suspend fun verifyGpsLocationMatch(
            reviewerUserId: String,
            targetUserId: String
        ): AntiSpamVerificationResult = verificationResult

        override suspend fun verifyQrScanToken(
            reviewerUserId: String,
            targetUserId: String,
            qrToken: String
        ): AntiSpamVerificationResult = verificationResult

        override fun observeVerificationStatus(
            reviewerUserId: String,
            targetUserId: String
        ): Flow<AntiSpamVerificationResult> = MutableStateFlow(verificationResult)
    }
}
