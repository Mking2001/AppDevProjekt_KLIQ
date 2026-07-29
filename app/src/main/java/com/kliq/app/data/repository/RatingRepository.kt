package com.kliq.app.data.repository

import com.kliq.app.data.model.AntiSpamVerificationResult
import com.kliq.app.data.model.Review
import kotlinx.coroutines.flow.Flow

interface RatingRepository {
    fun getReviewsForTargetUser(targetUserId: String): Flow<List<Review>>
    fun getAverageRatingForUser(targetUserId: String): Flow<Double?>
    suspend fun checkRatingVerification(
        reviewerUserId: String,
        targetUserId: String,
        qrToken: String? = null
    ): AntiSpamVerificationResult

    suspend fun submitUserRating(
        reviewerUserId: String,
        targetUserId: String,
        rating: Int,
        text: String,
        qrToken: String? = null
    ): Result<Review>
}
