package com.kliq.app.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel responsible for managing the main bottom navigation state.
 * Follows MVVM by exposing an immutable [NavigationState] via [StateFlow]
 * and providing intent-based actions for the UI layer.
 *
 * The ViewModel survives configuration changes, ensuring seamless
 * navigation state preservation across screen rotations.
 */
@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    /**
     * Called by the UI when a bottom bar tab is tapped.
     * Updates the current route and tracks the previous route
     * for animation direction determination.
     *
     * @param route The route string of the selected tab.
     */
    fun onTabSelected(route: String) {
        _navigationState.update { currentState ->
            if (currentState.currentRoute == route) {
                // Already on this tab — no state change needed.
                // In a full implementation, this could trigger scroll-to-top.
                currentState
            } else {
                currentState.copy(
                    previousRoute = currentState.currentRoute,
                    currentRoute = route
                )
            }
        }
    }

    /**
     * Updates the notification badge count.
     * Called from a repository or use-case layer when unread count changes.
     *
     * @param count The number of unread notifications.
     */
    fun updateNotificationBadge(count: Int) {
        _navigationState.update { it.copy(notificationBadgeCount = count.coerceAtLeast(0)) }
    }
}
