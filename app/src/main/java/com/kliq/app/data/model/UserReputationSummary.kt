package com.kliq.app.data.model

import java.util.Locale

data class UserReputationSummary(
    val targetUserId: String = "",
    val averageRating: Double = 0.0,
    val totalReviewsCount: Int = 0,
    val verifiedReviewsCount: Int = 0
) {
    val hasRatings: Boolean
        get() = totalReviewsCount > 0

    val formattedAverageRating: String
        get() = if (hasRatings) String.format(Locale.US, "%.1f", averageRating) else "0.0"
}
