package com.kliq.app.data.repository

import com.kliq.app.data.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviewsForClub(clubId: String): Flow<List<Review>>
    fun getVerifiedReviewsForClub(clubId: String): Flow<List<Review>>
    fun getReviewsForEvent(eventId: String): Flow<List<Review>>
    fun getReviewsForTargetUser(targetUserId: String): Flow<List<Review>>
    fun getAverageRatingForClub(clubId: String): Flow<Double?>
    fun getAverageRatingForTargetUser(targetUserId: String): Flow<Double?>
    fun getReviewCountForTargetUser(targetUserId: String): Flow<Int>
    suspend fun syncReviewsForClub(clubId: String): Result<Unit>
    suspend fun syncReviewsForTargetUser(targetUserId: String): Result<Unit>
    suspend fun submitReviewWithGpsCheck(
        reviewerUserId: String,
        clubId: String,
        rating: Int,
        text: String,
        userLat: Double,
        userLon: Double
    ): Result<Review>

    suspend fun submitReviewWithQrCheck(
        reviewerUserId: String,
        targetId: String,
        rating: Int,
        text: String,
        qrToken: String
    ): Result<Review>

    suspend fun submitVerifiedUserComment(
        reviewerUserId: String,
        targetUserId: String,
        rating: Int,
        text: String,
        verificationMethod: com.kliq.app.data.model.ReviewVerificationMethod = com.kliq.app.data.model.ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
        qrToken: String? = null
    ): Result<Review>

    suspend fun submitUnverifiedReview(
        reviewerUserId: String,
        clubId: String? = null,
        eventId: String? = null,
        targetUserId: String? = null,
        rating: Int,
        text: String
    ): Result<Review>
}
