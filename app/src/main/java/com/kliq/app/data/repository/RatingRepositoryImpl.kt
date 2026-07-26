package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.AntiSpamVerificationResult
import com.kliq.app.data.model.Review
import com.kliq.app.data.util.AntiSpamReviewValidator
import com.kliq.app.service.VerificationService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatingRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
    private val verificationService: VerificationService,
    private val antiSpamValidator: AntiSpamReviewValidator,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RatingRepository {

    override fun getReviewsForTargetUser(targetUserId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForTargetUser(targetUserId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun getAverageRatingForUser(targetUserId: String): Flow<Double?> {
        return reviewDao.getReviewsForTargetUser(targetUserId).map { reviews ->
            if (reviews.isEmpty()) null else reviews.map { it.rating }.average()
        }.flowOn(ioDispatcher)
    }

    override suspend fun checkRatingVerification(
        reviewerUserId: String,
        targetUserId: String,
        qrToken: String?
    ): AntiSpamVerificationResult {
        return verificationService.verifyUserProximityOrQr(reviewerUserId, targetUserId, qrToken)
    }

    override suspend fun submitUserRating(
        reviewerUserId: String,
        targetUserId: String,
        rating: Int,
        text: String,
        qrToken: String?
    ): Result<Review> = withContext(ioDispatcher) {
        if (!antiSpamValidator.isRatingValid(rating)) {
            return@withContext Result.failure(
                IllegalArgumentException("Ungültige Bewertung: Die Sterne-Bewertung muss zwischen 1 und 5 liegen.")
            )
        }

        val verification = verificationService.verifyUserProximityOrQr(
            reviewerUserId = reviewerUserId,
            targetUserId = targetUserId,
            qrScanToken = qrToken
        )

        if (!verification.isVerified) {
            return@withContext Result.failure(
                IllegalStateException("Bewertung gesperrt: Es liegt weder eine GPS-Standort-Verifizierung vor, noch wurde ein gültiger QR-Code gescannt.")
            )
        }

        val entity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            targetUserId = targetUserId,
            rating = rating,
            text = text,
            timestamp = System.currentTimeMillis(),
            verificationMethod = verification.method,
            isVerified = true
        )

        reviewDao.insertReview(entity)
        Result.success(entity.toDomain())
    }

    private fun ReviewEntity.toDomain(): Review {
        return Review(
            id = id,
            reviewerUserId = reviewerUserId,
            targetUserId = targetUserId,
            clubId = clubId,
            eventId = eventId,
            rating = rating,
            text = text,
            timestamp = timestamp,
            verificationMethod = verificationMethod,
            isVerified = isVerified,
            reviewerUsername = reviewerUsername,
            reviewerAvatarUrl = reviewerAvatarUrl
        )
    }
}
