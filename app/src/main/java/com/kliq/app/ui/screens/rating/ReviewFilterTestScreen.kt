package com.kliq.app.ui.screens.rating

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.ui.components.ReviewCommentCard
import com.kliq.app.ui.components.ReviewFilterSection
import com.kliq.app.ui.model.ReviewFilterState
import com.kliq.app.ui.model.ReviewSortOption
import com.kliq.app.ui.model.StarFilterOption
import com.kliq.app.ui.model.toHighContrastUiState
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkOnBackground
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.KliqTheme
import com.kliq.app.ui.theme.PurplePrimary

@Composable
fun ReviewFilterTestScreen(
    modifier: Modifier = Modifier
) {
    var rawReviews by remember { mutableStateOf(generateMockReviews()) }
    var filterState by remember { mutableStateOf(ReviewFilterState()) }
    var updateLatencyMs by remember { mutableStateOf(0L) }

    val filteredReviews = remember(rawReviews, filterState) {
        val startTime = System.currentTimeMillis()
        val result = applyFilterAndSort(rawReviews, filterState)
        updateLatencyMs = System.currentTimeMillis() - startTime
        result
    }

    val darkContainer = Color(0xFF1E1B2E)
    val accentPurple = Color(0xFF7C4DFF)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "QA Club-Bewertungs-Filter Test-Bench",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )
            )

            Text(
                text = "Interaktiver Emulator-Test für Sterne-Filter, Sortierung & Verifizierungs-Status",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { rawReviews = generateMockReviews() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = accentPurple),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("10 Testdaten laden", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { rawReviews = emptyList() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null, tint = Color.LightGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Empty State testen", fontSize = 12.sp, color = Color.LightGray)
                }
            }

            ReviewFilterSection(
                filterState = filterState,
                onStarFilterSelected = { option -> filterState = filterState.copy(selectedStarFilter = option) },
                onSortOptionSelected = { option -> filterState = filterState.copy(selectedSortOption = option) },
                onVerifiedOnlyToggled = { onlyVerified -> filterState = filterState.copy(onlyVerified = onlyVerified) },
                onResetFilters = { filterState = ReviewFilterState() }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = darkContainer),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2B3D))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gefiltert: ${filteredReviews.size} von ${rawReviews.size} Bewertungen",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Aktive Filter: ${filterState.activeFilterCount} | Aktualisierung: ${updateLatencyMs}ms",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = accentPurple
                    )
                }
            }

            AnimatedVisibility(
                visible = filteredReviews.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(darkContainer)
                        .border(1.dp, accentPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Keine passenden Bewertungen gefunden",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Versuche, die Sterne-Auswahl oder den Verifizierungs-Filter zurückzusetzen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredReviews, key = { it.id }) { review ->
                    ReviewCommentCard(review = review)
                }
            }
        }
    }
}

private fun applyFilterAndSort(
    reviews: List<Review>,
    filterState: ReviewFilterState
): List<com.kliq.app.ui.model.ReviewHighContrastItemState> {
    var result = reviews

    if (filterState.onlyVerified) {
        result = result.filter { it.isVerified }
    }

    result = when (filterState.selectedStarFilter) {
        StarFilterOption.ALL -> result
        StarFilterOption.FIVE_STARS -> result.filter { it.rating == 5 }
        StarFilterOption.FOUR_PLUS_STARS -> result.filter { it.rating >= 4 }
        StarFilterOption.THREE_PLUS_STARS -> result.filter { it.rating >= 3 }
        StarFilterOption.TWO_PLUS_STARS -> result.filter { it.rating >= 2 }
        StarFilterOption.ONE_STAR -> result.filter { it.rating == 1 }
    }

    val sorted = when (filterState.selectedSortOption) {
        ReviewSortOption.NEWEST_FIRST -> result.sortedByDescending { it.timestamp }
        ReviewSortOption.OLDEST_FIRST -> result.sortedBy { it.timestamp }
        ReviewSortOption.HIGHEST_RATING -> result.sortedWith(
            compareByDescending<Review> { it.rating }.thenByDescending { it.timestamp }
        )
        ReviewSortOption.LOWEST_RATING -> result.sortedWith(
            compareBy<Review> { it.rating }.thenByDescending { it.timestamp }
        )
        ReviewSortOption.MOST_HELPFUL -> result.sortedByDescending { it.timestamp }
    }

    return sorted.map { it.toHighContrastUiState() }
}

private fun generateMockReviews(): List<Review> {
    val now = System.currentTimeMillis()
    return listOf(
        Review(
            id = "mock_1",
            reviewerUserId = "u101",
            reviewerUsername = "Sarah M.",
            rating = 5,
            text = "Absolut genialer Club! Soundanlage ist Erstklasse und Einlass ging per QR verifiziert blitzschnell.",
            timestamp = now - 1000 * 60 * 30,
            isVerified = true,
            verificationMethod = ReviewVerificationMethod.QR_CODE_SCAN
        ),
        Review(
            id = "mock_2",
            reviewerUserId = "u102",
            reviewerUsername = "Lukas B.",
            rating = 4,
            text = "Stimmung war gut, aber die Schlange an der Bar war etwas lang.",
            timestamp = now - 1000 * 60 * 60 * 2,
            isVerified = true,
            verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
        ),
        Review(
            id = "mock_3",
            reviewerUserId = "u103",
            reviewerUsername = "Anonymous Guest",
            rating = 1,
            text = "Türsteher waren unfreundlich und Musik war nicht mein Fall.",
            timestamp = now - 1000 * 60 * 60 * 5,
            isVerified = false,
            verificationMethod = ReviewVerificationMethod.UNVERIFIED
        ),
        Review(
            id = "mock_4",
            reviewerUserId = "u104",
            reviewerUsername = "Elena K.",
            rating = 5,
            text = "Bester Techno Club der Stadt! Immer wieder gerne.",
            timestamp = now - 1000 * 60 * 60 * 12,
            isVerified = true,
            verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
        ),
        Review(
            id = "mock_5",
            reviewerUserId = "u105",
            reviewerUsername = "Maximilian T.",
            rating = 3,
            text = "Durchschnittlicher Abend. Preise sind ordentlich gestiegen.",
            timestamp = now - 1000 * 60 * 60 * 24,
            isVerified = false,
            verificationMethod = ReviewVerificationMethod.UNVERIFIED
        ),
        Review(
            id = "mock_6",
            reviewerUserId = "u106",
            reviewerUsername = "Mia R.",
            rating = 2,
            text = "Sehr überfüllt, kaum Platz zum Tanzen.",
            timestamp = now - 1000 * 60 * 60 * 48,
            isVerified = false,
            verificationMethod = ReviewVerificationMethod.UNVERIFIED
        )
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun ReviewFilterTestScreenPreview() {
    KliqTheme {
        ReviewFilterTestScreen()
    }
}
