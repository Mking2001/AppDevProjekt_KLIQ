/**
 * AI-GENERATED CODE
 * Dieses Layout-Scaffolding wurde vollständig durch KI generiert.
 * Erstellt im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 */
package com.kliq.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.KliqFeedCard
import com.kliq.app.ui.components.KliqScreenScaffold
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

// ============================================================
// AI-generiert: Home-Feed-Screen – Hauptbildschirm der Kliq-App.
// Zeigt eine Story-Row und einen scrollbaren Social-Feed.
// Architektur: MVVM mit Hilt-injiziertem ViewModel.
// ============================================================

/**
 * AI-generiert: Home-Feed-Screen mit Story-Row, scrollbarem Feed
 * und Floating Action Button zum Erstellen neuer Posts.
 *
 * Konsumiert den [HomeUiState] über collectAsStateWithLifecycle()
 * für lifecycle-bewusstes State-Management.
 *
 * @param viewModel Hilt-injiziertes [HomeViewModel].
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KliqScreenScaffold(
        title = "Kliq",
        actions = {
            // AI-generiert: Action-Icons in der Top-Bar (Filter & Nachrichten)
            IconButton(onClick = { /* TODO: Filter öffnen */ }) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { /* TODO: Chat öffnen */ }) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Nachrichten",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        floatingActionButton = {
            // AI-generiert: FAB zum Erstellen neuer Posts im Primary-Lila
            FloatingActionButton(
                onClick = { viewModel.onCreatePost() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Neuer Beitrag"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // AI-generiert: Story-Row mit horizontal scrollbaren Avataren
            item {
                StoryRow(stories = uiState.storyItems)
            }

            // AI-generiert: Feed-Karten mit Platzhalter-Daten
            items(uiState.feedItems, key = { it.id }) { feedItem ->
                KliqFeedCard(
                    userName = feedItem.userName,
                    timeAgo = feedItem.timeAgo,
                    contentText = feedItem.contentText,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * AI-generiert: Horizontale Story-Row mit kreisrunden Avataren.
 * Jeder Avatar hat einen Gradient-Rahmen, der auf ungesehene
 * Stories hinweist (Lila → Fuchsia Gradient).
 *
 * @param stories Liste der Story-Platzhalter-Items.
 */
@Composable
private fun StoryRow(stories: List<StoryItemUi>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stories, key = { it.id }) { story ->
            // AI-generiert: Einzelner Story-Avatar mit Gradient-Border
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(68.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .then(
                            if (story.hasUnseenStory) {
                                Modifier.border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            PurplePrimaryLight,
                                            FuchsiaTertiary,
                                            PurplePrimary
                                        )
                                    ),
                                    shape = CircleShape
                                )
                            } else {
                                Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                            }
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = story.userName.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.userName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
