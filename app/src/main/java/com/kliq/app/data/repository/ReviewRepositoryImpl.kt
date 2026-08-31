package com.kliq.app.data.repository

import com.kliq.app.data.generated.*
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.util.AntiSpamReviewValidator
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
    private val kliqConnector: com.kliq.app.data.generated.KliqConnectorConnector? = null,
    private val userDao: com.kliq.app.data.local.dao.UserDao? = null
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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncReviewsForTargetUser(targetUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            kliqConnector?.let { connector ->
                val response = connector.getReviewsForTargetUser.execute(targetUserId = targetUserId)
                val remoteReviews = response.data.reviews.map { r ->
                    ReviewEntity(
                        id = r.id,
                        reviewerUserId = r.reviewerUserId,
                        targetUserId = targetUserId,
                        rating = r.rating,
                        text = r.text,
                        timestamp = r.timestamp,
                        verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                        isVerified = true,
                        reviewerUsername = r.reviewerUsername,
                        reviewerAvatarUrl = null
                    )
                }
                if (remoteReviews.isNotEmpty()) {
                    for (rev in remoteReviews) {
                        reviewDao.insertReview(rev)
                    }
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
        if (!verification.isVerified) {
            return@withContext Result.failure(IllegalStateException("Ungültiger oder abgelaufener QR-Code."))
        }

        val entity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            targetUserId = targetId,
            rating = rating,
            text = text,
            timestamp = System.currentTimeMillis(),
            verificationMethod = verification.method,
            isVerified = verification.isVerified
        )

        reviewDao.insertReview(entity)
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
        if (text.trim().isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Bitte schreibe einen Kommentar zu deiner Bewertung."))
        }

        val reviewerUser = userDao?.getUserByIdOneShot(reviewerUserId)
        val reviewerName = reviewerUser?.username ?: "Kliq-User"
        val reviewerAvatar = reviewerUser?.profilePictureUrl

        val entity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            targetUserId = targetUserId,
            clubId = clubId,
            eventId = eventId,
            rating = rating,
            text = text.trim(),
            timestamp = System.currentTimeMillis(),
            verificationMethod = ReviewVerificationMethod.UNVERIFIED,
            isVerified = false,
            reviewerUsername = reviewerName,
            reviewerAvatarUrl = reviewerAvatar
        )

        reviewDao.insertReview(entity)

        kliqConnector?.let { connector ->
            try {
                connector.createReview.execute(
                    id = entity.id,
                    reviewerUserId = reviewerUserId,
                    rating = rating,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    reviewerUsername = reviewerName
                ) {
                    this.targetUserId = targetUserId
                    this.clubId = clubId
                    this.eventId = eventId
                    this.reviewerAvatarUrl = reviewerAvatar
                }
            } catch (ignored: Exception) { }
        }

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
        if (text.isBlank() || text.length > 500) {
            return@withContext Result.failure(IllegalArgumentException("Kommentar muss zwischen 1 und 500 Zeichen enthalten."))
        }
        if (!antiSpamValidator.isRatingValid(rating)) {
            return@withContext Result.failure(IllegalArgumentException("Rating muss zwischen 1 und 5 Sternen liegen."))
        }

        val reviewerUser = userDao?.getUserByIdOneShot(reviewerUserId)
        val reviewerName = reviewerUser?.username ?: "Kliq-User"
        val reviewerAvatar = reviewerUser?.profilePictureUrl

        val entity = ReviewEntity(
            id = UUID.randomUUID().toString(),
            reviewerUserId = reviewerUserId,
            targetUserId = targetUserId,
            rating = rating,
            text = text.trim(),
            timestamp = System.currentTimeMillis(),
            verificationMethod = verificationMethod,
            isVerified = true,
            reviewerUsername = reviewerName,
            reviewerAvatarUrl = reviewerAvatar
        )

        reviewDao.insertReview(entity)

        kliqConnector?.let { connector ->
            try {
                connector.createReview.execute(
                    id = entity.id,
                    reviewerUserId = reviewerUserId,
                    rating = rating,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    reviewerUsername = reviewerName
                ) {
                    this.targetUserId = targetUserId
                    this.reviewerAvatarUrl = reviewerAvatar
                }
            } catch (ignored: Exception) { }
        }

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
