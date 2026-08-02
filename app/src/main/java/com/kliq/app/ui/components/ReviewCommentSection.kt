package com.kliq.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.model.ReviewHighContrastItemState

import com.kliq.app.ui.model.ReviewFilterState
import com.kliq.app.ui.model.ReviewSortOption
import com.kliq.app.ui.model.StarFilterOption

@Composable
fun ReviewCommentSection(
    comments: List<ReviewHighContrastItemState>,
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit,
    isVerificationLocked: Boolean,
    isSubmitting: Boolean = false,
    onSubmitClick: () -> Unit,
    filterState: ReviewFilterState? = null,
    onStarFilterSelected: ((StarFilterOption) -> Unit)? = null,
    onSortOptionSelected: ((ReviewSortOption) -> Unit)? = null,
    onVerifiedOnlyToggled: ((Boolean) -> Unit)? = null,
    onResetFilters: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val accentPurple = Color(0xFF7C3AED)
    val containerBg = Color(0xFF1E1B2E)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Verifizierte Reviews & Kommentare",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (filterState != null &&
            onStarFilterSelected != null &&
            onSortOptionSelected != null &&
            onVerifiedOnlyToggled != null &&
            onResetFilters != null
        ) {
            ReviewFilterSection(
                filterState = filterState,
                onStarFilterSelected = onStarFilterSelected,
                onSortOptionSelected = onSortOptionSelected,
                onVerifiedOnlyToggled = onVerifiedOnlyToggled,
                onResetFilters = onResetFilters
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerBg)
                    .border(1.dp, accentPurple.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Noch keine schriftlichen Kommentare vorhanden. Sei der Erste mit einer verifizierten Bewertung!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            comments.forEach { review ->
                ReviewCommentCard(review = review)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ReviewCommentInputCard(
            commentText = commentText,
            onCommentTextChange = onCommentTextChange,
            selectedRating = selectedRating,
            onRatingSelected = onRatingSelected,
            isVerificationLocked = isVerificationLocked,
            isSubmitting = isSubmitting,
            onSubmitClick = onSubmitClick
        )
    }
}
