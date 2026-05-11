package com.kliq.app.ui.screens.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI State für den Notifications-Screen.
 *
 * @param selectedTabIndex Index des ausgewählten Filter-Tabs.
 * @param tabs Verfügbare Filter-Tabs.
 * @param notifications Platzhalter-Benachrichtigungen.
 * @param unreadCount Anzahl ungelesener Benachrichtigungen.
 */
data class NotificationsUiState(
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = emptyList(),
    val notifications: List<NotificationItemUi> = emptyList(),
    val unreadCount: Int = 0
)

/**
 * Platzhalter-Datenklasse für eine Benachrichtigung.
 */
data class NotificationItemUi(
    val id: String,
    val text: String,
    val timeAgo: String,
    val isUnread: Boolean,
    val type: NotificationType = NotificationType.LIKE
)

/**
 * Typ einer Benachrichtigung für Filter-Zuordnung.
 */
enum class NotificationType {
    LIKE, COMMENT, FOLLOW, EVENT
}

/**
 * ViewModel für den Notifications/Aktivität-Screen.
 * Verwaltet Benachrichtigungs-Liste und Filter-Tab-State.
 *
 * Folgt strikt dem MVVM-Pattern:
 * - Kein Compose/Android-Import
 * - Immutable State via StateFlow
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    /**
     * Lädt Platzhalter-Daten für die visuelle Darstellung.
     */
    private fun loadMockData() {
        val notifications = listOf(
            NotificationItemUi("1", "Anna M. hat deinen Beitrag geliked", "Vor 5 Min.", true, NotificationType.LIKE),
            NotificationItemUi("2", "Max K. hat kommentiert: \"Mega!\"", "Vor 15 Min.", true, NotificationType.COMMENT),
            NotificationItemUi("3", "Lisa W. folgt dir jetzt", "Vor 30 Min.", true, NotificationType.FOLLOW),
            NotificationItemUi("4", "Tom S. hat dein Foto geliked", "Vor 1 Std.", false, NotificationType.LIKE),
            NotificationItemUi("5", "Sarah B. hat kommentiert", "Vor 2 Std.", false, NotificationType.COMMENT),
            NotificationItemUi("6", "Jonas M. folgt dir jetzt", "Vor 3 Std.", false, NotificationType.FOLLOW),
            NotificationItemUi("7", "Event \"Techno Night\" startet bald", "Vor 4 Std.", false, NotificationType.EVENT),
            NotificationItemUi("8", "Mia R. hat deinen Beitrag geliked", "Gestern", false, NotificationType.LIKE)
        )

        _uiState.update { state ->
            state.copy(
                tabs = listOf("Alle", "Likes", "Kommentare", "Follows"),
                notifications = notifications,
                unreadCount = notifications.count { it.isUnread }
            )
        }
    }

    /**
     * Stub für Tab-Filter-Auswahl.
     * @param index Index des ausgewählten Tabs.
     */
    fun onFilterTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    /**
     * Stub zum Markieren aller als gelesen.
     */
    fun onMarkAllRead() {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { it.copy(isUnread = false) },
                unreadCount = 0
            )
        }
    }

    /**
     * Stub für Klick auf eine Benachrichtigung.
     * @param notificationId ID der angeklickten Benachrichtigung.
     */
    fun onNotificationClicked(notificationId: String) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { item ->
                    if (item.id == notificationId) item.copy(isUnread = false) else item
                },
                unreadCount = state.notifications.count {
                    it.isUnread && it.id != notificationId
                }
            )
        }
        // TODO: Navigation zum relevanten Content
    }
}
