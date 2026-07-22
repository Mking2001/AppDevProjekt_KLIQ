package com.kliq.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.KliqScreenScaffold
import com.kliq.app.ui.components.ZoomableImageOverlay
import com.kliq.app.ui.navigation.TopBarMenuAction
import com.kliq.app.ui.navigation.TopBarUiState
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

/**
 * Profile-Screen mit großem Avatar, Statistiken,
 * "Profil bearbeiten"-Button und Tab-basiertem Content-Bereich.
 *
 * @param topBarState Aktueller Top-Bar UI-State.
 * @param onToggleMenu Callback zum Umschalten des Overflow-Menüs.
 * @param onDismissMenu Callback zum Schließen des Overflow-Menüs.
 * @param onMenuAction Callback bei Auswahl eines Menü-Eintrags.
 * @param viewModel Hilt-injiziertes [ProfileViewModel].
 */
@Composable
fun ProfileScreen(
    topBarState: TopBarUiState,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showZoomOverlay by remember { mutableStateOf(false) }

    KliqScreenScaffold(
        title = "Profil",
        isMenuExpanded = topBarState.isMenuExpanded,
        onToggleMenu = onToggleMenu,
        onDismissMenu = onDismissMenu,
        onMenuAction = onMenuAction
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Profil-Header mit Avatar, Name, Bio und Statistiken
            item {
                ProfileHeader(
                    uiState = uiState,
                    onEditProfile = { viewModel.onEditProfile() },
                    onAvatarClick = { showZoomOverlay = true }
                )
            }

            // Tab-Row (Beiträge, Events, Über mich)
            item {
                ProfileTabRow(
                    tabs = uiState.tabs,
                    selectedTabIndex = uiState.selectedTabIndex,
                    onTabSelected = { viewModel.onTabSelected(it) }
                )
            }

            // Tab-Content je nach Auswahl
            item {
                ProfileTabContent(
                    selectedTabIndex = uiState.selectedTabIndex
                )
            }
        }
    }

    ZoomableImageOverlay(
        isVisible = showZoomOverlay,
        onDismiss = { showZoomOverlay = false }
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = uiState.displayName.take(2).uppercase(),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Profil-Header mit großem Avatar mit Gradient-Border,
 * Name, Username, Bio, Standort und Statistik-Zahlen.
 */
@Composable
private fun ProfileHeader(
    uiState: ProfileUiState,
    onEditProfile: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Großer Avatar mit Lila-Fuchsia Gradient-Border
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PurplePrimaryLight,
                            FuchsiaTertiary,
                            PurplePrimary
                        )
                    ),
                    shape = CircleShape
                )
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onAvatarClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = uiState.displayName.take(2).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Anzeigename
        Text(
            text = uiState.displayName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Username/Handle
        Text(
            text = uiState.username,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bio-Text
        Text(
            text = uiState.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Standort mit Icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = uiState.location,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistik-Row (Beiträge, Follower, Following)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(count = uiState.postsCount.toString(), label = "Beiträge")
            StatItem(count = formatCount(uiState.followersCount), label = "Follower")
            StatItem(count = formatCount(uiState.followingCount), label = "Following")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Profil bearbeiten" Button im Outlined-Stil
        OutlinedButton(
            onClick = onEditProfile,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Profil bearbeiten",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Einzelne Statistik-Anzeige (Zahl + Label).
 */
@Composable
private fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Tab-Row für Profil-Inhalte.
 */
@Composable
private fun ProfileTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 0.dp,
        divider = {
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.5.dp
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = tab,
                        fontWeight = if (selectedTabIndex == index)
                            FontWeight.Bold else FontWeight.Medium
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tab-Content basierend auf dem ausgewählten Tab.
 * Tab 0: Beitrags-Grid, Tab 1: Events-Liste, Tab 2: Über-mich-Text.
 */
@Composable
private fun ProfileTabContent(selectedTabIndex: Int) {
    when (selectedTabIndex) {
        0 -> PostsGrid()
        1 -> EventsList()
        2 -> AboutSection()
    }
}

/**
 * Platzhalter-Grid für Beiträge (3-Spalten).
 */
@Composable
private fun PostsGrid() {
    val itemCount = 9
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        for (row in 0 until (itemCount + 2) / 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    if (index < itemCount) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.primaryContainer
                                                .copy(alpha = 0.3f + (index * 0.05f).coerceAtMost(0.4f))
                                        )
                                    )
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Platzhalter-Liste für Events.
 */
@Composable
private fun EventsList() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val events = listOf(
            "Techno Night" to "Sa, 15. Mai · Club Luna",
            "Rooftop Party" to "Fr, 21. Mai · Skybar",
            "After Work" to "Do, 27. Mai · Bar Central"
        )
        events.forEach { (title, details) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = details,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * "Über mich" Abschnitt mit Platzhalter-Text.
 */
@Composable
private fun AboutSection() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Über mich",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Hey! Ich bin Max und immer auf der Suche nach den besten Events und Locations in München. " +
                    "Egal ob Techno, House oder einfach ein gemütlicher Abend – ich bin dabei! " +
                    "Verbinde dich mit mir und lass uns zusammen feiern. 🎶",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Interessen",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Interessen-Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val interests = listOf("🎵 Musik", "🌙 Nightlife", "📸 Fotografie")
            interests.forEach { interest ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = interest,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/**
 * Formatiert große Zahlen mit "k"-Suffix.
 */
private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}.${(count % 1_000_000) / 100_000}M"
        count >= 1_000 -> "${count / 1_000}.${(count % 1_000) / 100}k"
        else -> count.toString()
    }
}
