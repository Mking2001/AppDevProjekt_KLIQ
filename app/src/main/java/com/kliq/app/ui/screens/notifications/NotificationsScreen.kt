/**
 * AI-GENERATED CODE
 * Dieses Layout-Scaffolding wurde vollständig durch KI generiert.
 * Erstellt im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 */
package com.kliq.app.ui.screens.notifications

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.KliqNotificationItem
import com.kliq.app.ui.components.KliqScreenScaffold

// ============================================================
// AI-generiert: Notifications/Aktivität-Screen der Kliq-App.
// Zeigt Tab-Filter und eine scrollbare Benachrichtigungsliste.
// Architektur: MVVM mit Hilt-injiziertem ViewModel.
// ============================================================

/**
 * AI-generiert: Notifications-Screen mit Filter-Tabs, scrollbarer
 * Benachrichtigungsliste und Empty-State-Darstellung.
 *
 * @param viewModel Hilt-injiziertes [NotificationsViewModel].
 */
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KliqScreenScaffold(
        title = "Aktivität",
        actions = {
            // AI-generiert: "Alle gelesen" Button in der Top-Bar
            if (uiState.unreadCount > 0) {
                IconButton(onClick = { viewModel.onMarkAllRead() }) {
                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "Alle als gelesen markieren",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // AI-generiert: Filter-Tab-Row (Alle, Likes, Kommentare, Follows)
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                divider = {}
            ) {
                uiState.tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = uiState.selectedTabIndex == index,
                        onClick = { viewModel.onFilterTabSelected(index) },
                        text = {
                            Text(
                                text = tab,
                                fontWeight = if (uiState.selectedTabIndex == index)
                                    FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.5.dp
            )

            // AI-generiert: Gefilterte Benachrichtigungsliste
            val filteredNotifications = filterNotifications(
                notifications = uiState.notifications,
                selectedTabIndex = uiState.selectedTabIndex
            )

            if (filteredNotifications.isEmpty()) {
                // AI-generiert: Empty-State wenn keine Benachrichtigungen vorhanden
                EmptyNotificationsState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { notification ->
                        KliqNotificationItem(
                            text = notification.text,
                            timeAgo = notification.timeAgo,
                            isUnread = notification.isUnread,
                            modifier = Modifier.clickable {
                                viewModel.onNotificationClicked(notification.id)
                            }
                        )
                        Divider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 72.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * AI-generiert: Filtert Benachrichtigungen basierend auf dem ausgewählten Tab.
 */
private fun filterNotifications(
    notifications: List<NotificationItemUi>,
    selectedTabIndex: Int
): List<NotificationItemUi> {
    return when (selectedTabIndex) {
        0 -> notifications // Alle
        1 -> notifications.filter { it.type == NotificationType.LIKE }
        2 -> notifications.filter { it.type == NotificationType.COMMENT }
        3 -> notifications.filter { it.type == NotificationType.FOLLOW }
        else -> notifications
    }
}

/**
 * AI-generiert: Empty-State-Darstellung wenn keine Benachrichtigungen vorhanden.
 * Zeigt ein gedimmtes Icon und einen Hinweistext.
 */
@Composable
private fun EmptyNotificationsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Keine Benachrichtigungen",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hier erscheinen deine Aktivitäten\nund Interaktionen",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
