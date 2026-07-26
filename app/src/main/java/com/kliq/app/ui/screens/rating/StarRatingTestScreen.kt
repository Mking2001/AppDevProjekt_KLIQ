package com.kliq.app.ui.screens.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.ui.components.InteractiveStarRating
import com.kliq.app.ui.components.RatingBottomSheet
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkOnBackground
import com.kliq.app.ui.theme.DarkOnSurface
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.ErrorRed
import com.kliq.app.ui.theme.KliqTheme
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.TealSecondary
import com.kliq.app.viewmodel.RatingSubmitStatus
import com.kliq.app.viewmodel.RatingUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StarRatingTestScreen(
    modifier: Modifier = Modifier
) {
    var isSheetVisible by remember { mutableStateOf(false) }
    var shouldSimulateError by remember { mutableStateOf(false) }
    var ratingUiState by remember { mutableStateOf(RatingUiState()) }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "QA Sterne-Rating Test-Bench",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )
            )
            Text(
                text = "Manuelle Testumgebung für Feature 5.2 (Sterne-Rating-System)",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Inline Gesture Component Playground Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Live InteractiveStarRating Component",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkOnBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    InteractiveStarRating(
                        rating = ratingUiState.rating,
                        onRatingChanged = { newRating ->
                            ratingUiState = ratingUiState.copy(rating = newRating)
                        },
                        starSize = 44.dp,
                        starSpacing = 12.dp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aktuell ausgewählt: ${ratingUiState.rating} Sterne",
                        color = if (ratingUiState.rating > 0) TealSecondary else DarkOnSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Controls Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Test-Konfiguration",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkOnBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = !shouldSimulateError,
                            onClick = { shouldSimulateError = false },
                            colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                        )
                        Text("Erfolgreiches Absenden simulieren", color = DarkOnBackground)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = shouldSimulateError,
                            onClick = { shouldSimulateError = true },
                            colors = RadioButtonDefaults.colors(selectedColor = ErrorRed)
                        )
                        Text("Repository-Fehler simulieren", color = DarkOnBackground)
                    }
                }
            }

            // Action Buttons
            Button(
                onClick = { isSheetVisible = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rating Bottom Sheet öffnen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = {
                    ratingUiState = RatingUiState()
                    shouldSimulateError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = DarkOnBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Zustand zurücksetzen", color = DarkOnBackground)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // State Inspector Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("State Inspector:", fontWeight = FontWeight.Bold, color = PurplePrimary)
                    Text("Rating: ${ratingUiState.rating} Sterne", color = DarkOnBackground)
                    Text("Review-Text: \"${ratingUiState.reviewText}\"", color = DarkOnBackground)
                    Text("Status: ${ratingUiState.status::class.simpleName}", color = DarkOnBackground)
                    Text("Submit-Enabled: ${ratingUiState.isSubmitEnabled}", color = DarkOnBackground)
                }
            }
        }

        // Render Rating Bottom Sheet
        RatingBottomSheet(
            isVisible = isSheetVisible,
            uiState = ratingUiState,
            onRatingChanged = { newRating ->
                ratingUiState = ratingUiState.copy(rating = newRating)
            },
            onReviewTextChanged = { newText ->
                if (newText.length <= ratingUiState.maxTextLength) {
                    ratingUiState = ratingUiState.copy(reviewText = newText)
                }
            },
            onSubmit = {
                scope.launch {
                    ratingUiState = ratingUiState.copy(status = RatingSubmitStatus.Submitting)
                    delay(1200) // Simulate network call

                    if (shouldSimulateError) {
                        ratingUiState = ratingUiState.copy(
                            status = RatingSubmitStatus.Error("Netzwerkfehler: Server antwortet nicht.")
                        )
                    } else {
                        val mockReview = Review(
                            id = "rev_test_123",
                            reviewerUserId = "user_qa",
                            targetUserId = "target_qa",
                            rating = ratingUiState.rating,
                            text = ratingUiState.reviewText,
                            timestamp = System.currentTimeMillis(),
                            verificationMethod = ReviewVerificationMethod.UNVERIFIED,
                            isVerified = false
                        )
                        ratingUiState = ratingUiState.copy(status = RatingSubmitStatus.Success(mockReview))
                    }
                }
            },
            onDismissRequest = {
                isSheetVisible = false
                if (ratingUiState.status is RatingSubmitStatus.Success) {
                    ratingUiState = RatingUiState()
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun StarRatingTestScreenPreview() {
    KliqTheme {
        StarRatingTestScreen()
    }
}
