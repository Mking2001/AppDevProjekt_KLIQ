package com.kliq.app.ui.model

import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReviewHighContrastPalette {
    const val PrimaryVioletAccent = "#BB86FC"
    const val StarGold = "#FFD700"
    const val VerifiedNeonGreen = "#00E676"
    const val UnverifiedGray = "#757575"
    const val DarkCardBackground = "#1E1B2E"
    const val BorderVerifiedViolet = "#7C4DFF"
    const val BorderUnverified = "#2C2B3D"
}

data class ReviewHighContrastItemState(
    val id: String,
    val reviewerUsername: String,
    val reviewerAvatarUrl: String?,
    val rating: Int,
    val ratingStarsFormatted: String,
    val ratingStarsColorHex: String = ReviewHighContrastPalette.StarGold,
    val reviewText: String,
    val formattedDate: String,
    val isVerified: Boolean,
    val verificationBadgeText: String,
    val verificationBadgeColorHex: String,
    val cardBackgroundColorHex: String = ReviewHighContrastPalette.DarkCardBackground,
    val cardBorderColorHex: String
)

fun Review.toHighContrastUiState(): ReviewHighContrastItemState {
    val stars = "★".repeat(rating.coerceIn(1, 5)) + "☆".repeat(5 - rating.coerceIn(1, 5))
    val starsFormatted = "$stars ($rating.0)"

    val dateFormat = SimpleDateFormat("dd. MMM yyyy, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(timestamp))

    val badgeText = when (verificationMethod) {
        ReviewVerificationMethod.GPS_GEOFENCE_MATCH -> "✓ VERIFIZIERT (GPS)"
        ReviewVerificationMethod.QR_CODE_SCAN -> "✓ VERIFIZIERT (QR-Scan)"
        ReviewVerificationMethod.UNVERIFIED -> "UNVERIFIZIERT"
    }

    val badgeColor = if (isVerified) ReviewHighContrastPalette.VerifiedNeonGreen else ReviewHighContrastPalette.UnverifiedGray
    val borderColor = if (isVerified) ReviewHighContrastPalette.BorderVerifiedViolet else ReviewHighContrastPalette.BorderUnverified

    return ReviewHighContrastItemState(
        id = id,
        reviewerUsername = reviewerUsername.ifBlank { "User ${reviewerUserId.takeLast(4)}" },
        reviewerAvatarUrl = reviewerAvatarUrl,
        rating = rating,
        ratingStarsFormatted = starsFormatted,
        reviewText = text,
        formattedDate = dateStr,
        isVerified = isVerified,
        verificationBadgeText = badgeText,
        verificationBadgeColorHex = badgeColor,
        cardBorderColorHex = borderColor
    )
}
