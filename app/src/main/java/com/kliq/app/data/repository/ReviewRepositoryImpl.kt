package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.util.AntiSpamReviewValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
    private val clubDao: ClubDao,
    private val antiSpamValidator: AntiSpamReviewValidator
) : ReviewRepository {

    override fun getReviewsForClub(clubId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForClub(clubId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getVerifiedReviewsForClub(clubId: String): Flow<List<Review>> {
        return reviewDao.getVerifiedReviewsForClub(clubId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getReviewsForEvent(eventId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForEvent(eventId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAverageRatingForClub(clubId: String): Flow<Double?> {
        return reviewDao.getAverageRatingForClub(clubId)
    }

    override suspend fun submitReviewWithGpsCheck(
        reviewerUserId: String,
        clubId: String,
        rating: Int,
        text: String,
        userLat: Double,
        userLon: Double
    ): Result<Review> {
        if (!antiSpamValidator.isRatingValid(rating)) {
            return Result.failure(IllegalArgumentException("Rating muss zwischen 1 und 5 Sternen liegen."))
        }

        val clubEntity = clubDao.getClubById(clubId).firstOrNull()
            ?: return Result.failure(IllegalArgumentException("Club mit ID $clubId nicht gefunden."))

        val verification = antiSpamValidator.validateGpsLocationMatch(
            userLat = userLat,
            userLon = userLon,
            targetLat = clubEntity.latitude,
            targetLon = clubEntity.longitude,
            allowedRadiusMeters = clubEntity.geofenceRadiusMeters
        )

        val reviewEntity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            clubId = clubId,
            rating = rating,
            text = text,
            timestamp = System.currentTimeMillis(),
            verificationMethod = verification.method,
            isVerified = verification.isVerified
        )

        reviewDao.insertReview(reviewEntity)
        return Result.success(reviewEntity.toDomain())
    }

    override suspend fun submitReviewWithQrCheck(
        reviewerUserId: String,
        targetId: String,
        rating: Int,
        text: String,
        qrToken: String
    ): Result<Review> {
        if (!antiSpamValidator.isRatingValid(rating)) {
            return Result.failure(IllegalArgumentException("Rating muss zwischen 1 und 5 Sternen liegen."))
        }

        val verification = antiSpamValidator.validateQrCodeScanToken(qrToken, targetId)

        val reviewEntity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            clubId = targetId,
            rating = rating,
            text = text,
            timestamp = System.currentTimeMillis(),
            verificationMethod = verification.method,
            isVerified = verification.isVerified
        )

        reviewDao.insertReview(reviewEntity)
        return Result.success(reviewEntity.toDomain())
    }

    override suspend fun submitUnverifiedReview(
        reviewerUserId: String,
        clubId: String?,
        eventId: String?,
        targetUserId: String?,
        rating: Int,
        text: String
    ): Result<Review> {
        if (!antiSpamValidator.isRatingValid(rating)) {
            return Result.failure(IllegalArgumentException("Rating muss zwischen 1 und 5 Sternen liegen."))
        }

        val reviewEntity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            clubId = clubId,
            eventId = eventId,
            targetUserId = targetUserId,
            rating = rating,
            text = text,
            timestamp = System.currentTimeMillis(),
            verificationMethod = ReviewVerificationMethod.UNVERIFIED,
            isVerified = false
        )

        reviewDao.insertReview(reviewEntity)
        return Result.success(reviewEntity.toDomain())
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
