package com.kliq.app.data.model

enum class ReviewVerificationMethod {
    UNVERIFIED,
    GPS_GEOFENCE_MATCH,
    QR_CODE_SCAN
}

data class AntiSpamVerificationResult(
    val isVerified: Boolean,
    val method: ReviewVerificationMethod,
    val confidenceScore: Float = 1.0f,
    val verificationDetails: String = ""
)

data class Review(
    val id: String,
    val reviewerUserId: String,
    val targetUserId: String? = null,
    val clubId: String? = null,
    val eventId: String? = null,
    val rating: Int, // 1 to 5 stars
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val verificationMethod: ReviewVerificationMethod = ReviewVerificationMethod.UNVERIFIED,
    val isVerified: Boolean = false,
    val reviewerUsername: String = "",
    val reviewerAvatarUrl: String? = null
)
