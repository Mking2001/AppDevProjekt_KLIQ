package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.util.AntiSpamReviewValidator
import com.kliq.app.data.generated.*
import timber.log.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
    private val clubDao: ClubDao,
    private val antiSpamValidator: AntiSpamReviewValidator,
    private val apiService: KliqApiService? = null,
    private val kliqConnector: com.kliq.app.data.generated.KliqConnectorConnector? = null
) : ReviewRepository {

    override fun getReviewsForClub(clubId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForClub(clubId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getVerifiedReviewsForClub(clubId: String): Flow<List<Review>> {
        return reviewDao.getVerifiedReviewsForClub(clubId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getReviewsForEvent(eventId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForEvent(eventId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getAverageRatingForClub(clubId: String): Flow<Double?> {
        return reviewDao.getAverageRatingForClub(clubId).flowOn(Dispatchers.IO)
    }

    override fun getReviewsForTargetUser(targetUserId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForTargetUser(targetUserId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getAverageRatingForTargetUser(targetUserId: String): Flow<Double?> {
        return reviewDao.getAverageRatingForTargetUser(targetUserId).flowOn(Dispatchers.IO)
    }

    override fun getReviewCountForTargetUser(targetUserId: String): Flow<Int> {
        return reviewDao.getReviewCountForTargetUser(targetUserId).flowOn(Dispatchers.IO)
    }

    override suspend fun syncReviewsForClub(clubId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            kliqConnector?.let { connector ->
                try {
                    val response = connector.getReviewsByClub.execute(clubId = clubId)
                    val remoteReviews = response.data.reviews.map { r ->
                        ReviewEntity(
                            id = r.id,
                            reviewerUserId = r.reviewerUserId,
                            targetUserId = null,
                            clubId = clubId,
                            eventId = null,
                            rating = r.rating,
                            text = r.text,
                            timestamp = r.timestamp,
                            verificationMethod = try { ReviewVerificationMethod.valueOf(r.verificationMethod) } catch (e: Exception) { ReviewVerificationMethod.UNVERIFIED },
                            isVerified = r.isVerified,
                            reviewerUsername = r.reviewerUsername,
                            reviewerAvatarUrl = r.reviewerAvatarUrl,
                            helpfulVotesCount = r.helpfulVotesCount,
                            flaggedCount = 0
                        )
                    }
                    if (remoteReviews.isNotEmpty()) {
                        remoteReviews.forEach { reviewDao.insertReview(it) }
                        Timber.i("Synced %d reviews for club %s from Firebase SQL Connect", remoteReviews.size, clubId)
                    }
                } catch (e: Exception) {
                    Timber.d(e, "Could not sync reviews from Firebase SQL Connect for club %s", clubId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitReviewWithGpsCheck(
        reviewerUserId: String,
        clubId: String,
        rating: Int,
        text: String,
        userLat: Double,
        userLon: Double
    ): Result<Review> = withContext(Dispatchers.IO) {
        if (!antiSpamValidator.isRatingValid(rating)) {
            return@withContext Result.failure(IllegalArgumentException("Rating muss zwischen 1 und 5 Sternen liegen."))
        }

        val clubEntity = clubDao.getClubById(clubId).firstOrNull()
            ?: return@withContext Result.failure(IllegalArgumentException("Club mit ID $clubId nicht gefunden."))

        val verification = antiSpamValidator.validateGpsLocationMatch(
            userLat = userLat,
            userLon = userLon,
            targetLat = clubEntity.latitude,
            targetLon = clubEntity.longitude,
            allowedRadiusMeters = clubEntity.geofenceRadiusMeters
        )

        val entity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            clubId = clubId,
            rating = rating,
            text = text,
            timestamp = System.currentTimeMillis(),
            verificationMethod = verification.method,
            isVerified = verification.isVerified
        )

        reviewDao.insertReview(entity)
        pushReviewToCloudSql(entity)
        Result.success(entity.toDomain())
    }

    override suspend fun submitReviewWithQrCheck(
        reviewerUserId: String,
        targetId: String,
        rating: Int,
        text: String,
        qrToken: String
    ): Result<Review> = withContext(Dispatchers.IO) {
        if (!antiSpamValidator.isRatingValid(rating)) {
            return@withContext Result.failure(IllegalArgumentException("Rating muss zwischen 1 und 5 Sternen liegen."))
        }

        val verification = antiSpamValidator.validateQrCodeScanToken(qrToken, targetId)
        val entity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            clubId = targetId,
            rating = rating,
            text = text,
            timestamp = System.currentTimeMillis(),
            verificationMethod = verification.method,
            isVerified = verification.isVerified
        )

        reviewDao.insertReview(entity)
        pushReviewToCloudSql(entity)
        Result.success(entity.toDomain())
    }

    override suspend fun submitUnverifiedReview(
        reviewerUserId: String,
        clubId: String?,
        eventId: String?,
        targetUserId: String?,
        rating: Int,
        text: String
    ): Result<Review> = withContext(Dispatchers.IO) {
        if (!antiSpamValidator.isRatingValid(rating)) {
            return@withContext Result.failure(IllegalArgumentException("Rating muss zwischen 1 und 5 Sternen liegen."))
        }

        val entity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            targetUserId = targetUserId,
            clubId = clubId,
            eventId = eventId,
            rating = rating,
            text = text,
            timestamp = System.currentTimeMillis(),
            verificationMethod = ReviewVerificationMethod.UNVERIFIED,
            isVerified = false
        )

        reviewDao.insertReview(entity)
        pushReviewToCloudSql(entity)
        Result.success(entity.toDomain())
    }

    override suspend fun submitVerifiedUserComment(
        reviewerUserId: String,
        targetUserId: String,
        rating: Int,
        text: String,
        verificationMethod: ReviewVerificationMethod,
        qrToken: String?
    ): Result<Review> = withContext(Dispatchers.IO) {
        if (text.isBlank() || text.length > 280) {
            return@withContext Result.failure(IllegalArgumentException("Kommentar muss zwischen 1 und 280 Zeichen enthalten."))
        }
        if (!antiSpamValidator.isRatingValid(rating)) {
            return@withContext Result.failure(IllegalArgumentException("Rating muss zwischen 1 und 5 Sternen liegen."))
        }
        if (verificationMethod == ReviewVerificationMethod.UNVERIFIED) {
            return@withContext Result.failure(IllegalStateException("Sicherheits-Sperre: Kommentare dürfen nur bei verifizierter physischer Nähe (GPS) oder QR-Scan abgegeben werden."))
        }

        val entity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            targetUserId = targetUserId,
            rating = rating,
            text = text.trim(),
            timestamp = System.currentTimeMillis(),
            verificationMethod = verificationMethod,
            isVerified = true,
            reviewerUsername = "Kliq-User",
            reviewerAvatarUrl = null
        )

        reviewDao.insertReview(entity)
        pushReviewToCloudSql(entity)
        Result.success(entity.toDomain())
    }

    private suspend fun pushReviewToCloudSql(entity: ReviewEntity) {
        kliqConnector?.let { connector ->
            try {
                connector.createReview.execute(
                    id = entity.id,
                    reviewerUserId = entity.reviewerUserId,
                    rating = entity.rating,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    reviewerUsername = entity.reviewerUsername
                ) {
                    this.verificationMethod = entity.verificationMethod.name
                    this.isVerified = entity.isVerified
                    this.clubId = entity.clubId
                    this.eventId = entity.eventId
                    this.targetUserId = entity.targetUserId
                    this.reviewerAvatarUrl = entity.reviewerAvatarUrl
                }
                Timber.i("Successfully submitted review to Firebase SQL Connect: %s", entity.id)
            } catch (e: Exception) {
                Timber.e(e, "Failed to submit review to Firebase SQL Connect: %s", entity.id)
            }
        }
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

    private fun Review.toEntity(): ReviewEntity {
        return ReviewEntity(
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
