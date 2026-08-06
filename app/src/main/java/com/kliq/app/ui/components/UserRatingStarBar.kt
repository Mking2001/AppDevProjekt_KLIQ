package com.kliq.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kliq.app.util.accessibilityHeading
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription

@Composable
fun UserRatingStarBar(
    averageRating: Double,
    formattedAverageRating: String,
    totalReviewsCount: Int,
    verifiedReviewsCount: Int = totalReviewsCount,
    hasRatings: Boolean,
    modifier: Modifier = Modifier
) {
    val containerBg = Color(0xFF1E1B2E)
    val accentPurple = Color(0xFF7C3AED)
    val starGold = Color(0xFFFFC107)

    val ratingSummaryText = if (hasRatings) {
        "Durchschnittsbewertung $formattedAverageRating von 5 Sternen basierend auf $totalReviewsCount Bewertungen ($verifiedReviewsCount verifiziert)"
    } else {
        "Noch keine Bewertungen vorhanden"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(containerBg)
            .border(1.dp, accentPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clearAndSetSemantics {
                contentDescription = ratingSummaryText
                stateDescription = if (hasRatings) "$formattedAverageRating / 5 Sternen" else "Keine Ratings"
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (hasRatings) formattedAverageRating else "Keine Bewertungen",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = if (hasRatings) 22.sp else 16.sp,
                        modifier = Modifier.accessibilityHeading()
                    )

                    if (hasRatings) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "/ 5.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = if (hasRatings) {
                        if (verifiedReviewsCount > 0) {
                            "$totalReviewsCount Bewertungen ($verifiedReviewsCount verifiziert)"
                        } else {
                            "$totalReviewsCount Bewertungen"
                        }
                    } else {
                        "Noch keine verifizierten Ratings"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray.copy(alpha = 0.8f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                for (starIndex in 1..5) {
                    val icon = when {
                        !hasRatings -> Icons.Outlined.Star
                        averageRating >= starIndex -> Icons.Default.Star
                        averageRating >= starIndex - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                        else -> Icons.Outlined.Star
                    }

                    val tint = when {
                        !hasRatings -> Color.Gray
                        averageRating >= starIndex - 0.5 -> starGold
                        else -> Color.Gray.copy(alpha = 0.5f)
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

