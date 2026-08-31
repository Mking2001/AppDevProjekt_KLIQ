package com.kliq.app.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.data.repository.SocialRepository
import com.kliq.app.domain.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Immutable UI State für den Notifications-Screen.
 *
 * @param selectedTabIndex Index des ausgewählten Filter-Tabs.
 * @param tabs Verfügbare Filter-Tabs.
 * @param notifications Echte Benachrichtigungen aus der Datenbank.
 * @param unreadCount Anzahl ungelesener Benachrichtigungen.
 */
data class NotificationsUiState(
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Alle", "Bewertungen", "Freunde"),
    val notifications: List<NotificationItemUi> = emptyList(),
    val unreadCount: Int = 0
)

/**
 * Datenklasse für eine Aktivität / Benachrichtigung.
 */
data class NotificationItemUi(
    val id: String,
    val text: String,
    val timeAgo: String,
    val isUnread: Boolean,
    val type: NotificationType = NotificationType.COMMENT
)

/**
 * Typ einer Benachrichtigung für Filter-Zuordnung.
 */
enum class NotificationType {
    LIKE, COMMENT, FOLLOW, EVENT
}

/**
 * ViewModel für den Notifications/Aktivität-Screen.
 * Lädt echte Aktivitäten (Bewertungen, Freundschaften) aus den Repositories.
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val socialRepository: SocialRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var allNotifications: List<NotificationItemUi> = emptyList()

    init {
        observeRealActivities()
    }

    /**
     * Lädt echte Benachrichtigungen basierend auf Bewertungen und Kontakten.
     */
    private fun observeRealActivities() {
        viewModelScope.launch {
            val currentUserId = currentUserProvider.userId()

            combine(
                reviewRepository.getReviewsForTargetUser(currentUserId),
                socialRepository.getFriendsForUser(currentUserId)
            ) { reviews, friends ->
                val list = mutableListOf<NotificationItemUi>()

                reviews.forEach { r ->
                    val reviewerName = if (r.reviewerUsername.isNotBlank()) r.reviewerUsername else "Jemand"
                    list.add(
                        NotificationItemUi(
                            id = "rev_${r.id}",
                            text = "$reviewerName hat dir eine ${r.rating}-Sterne Bewertung hinterlassen: „${r.text}“",
                            timeAgo = formatTimestamp(r.timestamp),
                            isUnread = false,
                            type = NotificationType.COMMENT
                        )
                    )
                }

                friends.forEach { f ->
                    list.add(
                        NotificationItemUi(
                            id = "fr_${f.friendUserId}",
                            text = "Du bist jetzt mit einem neuen Kliq-Kontakt vernetzt.",
                            timeAgo = formatTimestamp(f.createdAtTimestampMs),
                            isUnread = false,
                            type = NotificationType.FOLLOW
                        )
                    )
                }

                list.sortedByDescending { it.id }
            }.collect { items ->
                allNotifications = items
                applyFilter()
            }
        }
    }

    private fun formatTimestamp(timestampMs: Long): String {
        if (timestampMs <= 0L) return "Vor kurzem"
        val diff = System.currentTimeMillis() - timestampMs
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "Gerade eben"
            minutes < 60 -> "Vor $minutes Min."
            hours < 24 -> "Vor $hours Std."
            days == 1L -> "Gestern"
            else -> "Vor $days Tagen"
        }
    }

    private fun applyFilter() {
        val selectedIndex = _uiState.value.selectedTabIndex
        val filtered = when (selectedIndex) {
            1 -> allNotifications.filter { it.type == NotificationType.COMMENT }
            2 -> allNotifications.filter { it.type == NotificationType.FOLLOW }
            else -> allNotifications
        }

        _uiState.update { state ->
            state.copy(
                notifications = filtered,
                unreadCount = filtered.count { it.isUnread }
            )
        }
    }

    fun onFilterTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
        applyFilter()
    }

    fun onMarkAllRead() {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { it.copy(isUnread = false) },
                unreadCount = 0
            )
        }
    }

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
    }
}
