/**
 * AI-GENERATED CODE
 * Dieses Layout-Scaffolding wurde vollständig durch KI generiert.
 * Erstellt im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 */
package com.kliq.app.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.KliqCategoryChip
import com.kliq.app.ui.components.KliqScreenScaffold

// ============================================================
// AI-generiert: Explore/Entdecken-Screen der Kliq-App.
// Bietet Suchleiste, Kategorie-Chips und ein Discovery-Grid.
// Architektur: MVVM mit Hilt-injiziertem ViewModel.
// ============================================================

/**
 * AI-generiert: Explore-Screen mit Suchleiste, Kategorie-Filter-Chips
 * und einem 2-Spalten Discovery-Grid mit Gradient-Overlays.
 *
 * @param viewModel Hilt-injiziertes [ExploreViewModel].
 */
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KliqScreenScaffold(
        title = "Entdecken"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // AI-generiert: Suchleiste mit Lupe-Icon im Lila-Design
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearch(it) },
                placeholder = {
                    Text(
                        text = "Suche nach Leuten, Events, Orten…",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Suchen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            // AI-generiert: Horizontal scrollbare Kategorie-Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(uiState.categories) { index, category ->
                    KliqCategoryChip(
                        label = category,
                        selected = uiState.selectedCategory == index,
                        onClick = { viewModel.onCategorySelected(index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AI-generiert: 2-Spalten Discovery-Grid mit Gradient-Overlay-Karten
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.discoverItems, key = { it.id }) { item ->
                    DiscoverGridCard(item = item)
                }
            }
        }
    }
}

/**
 * AI-generiert: Einzelne Discovery-Grid-Karte mit Gradient-Overlay.
 * Zeigt Titel und Untertitel über einem Platzhalter-Hintergrund
 * mit Lila-Gradient im unteren Bereich.
 *
 * @param item Discover-Datenelement.
 */
@Composable
private fun DiscoverGridCard(item: DiscoverItemUi) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // AI-generiert: Gradient-Overlay im unteren Bereich der Karte
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.0f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // AI-generiert: Text-Overlay mit Titel und Untertitel
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // AI-generiert: Kategorie-Badge oben rechts
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = item.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
