package com.kliq.app.data.repository

import com.kliq.app.data.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviewsForClub(clubId: String): Flow<List<Review>>
    fun getVerifiedReviewsForClub(clubId: String): Flow<List<Review>>
    fun getReviewsForEvent(eventId: String): Flow<List<Review>>
    fun getAverageRatingForClub(clubId: String): Flow<Double?>
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

    suspend fun submitUnverifiedReview(
        reviewerUserId: String,
        clubId: String? = null,
        eventId: String? = null,
        targetUserId: String? = null,
        rating: Int,
        text: String
    ): Result<Review>
}
