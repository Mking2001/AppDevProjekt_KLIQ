package com.kliq.app.ui.navigation

/**
 * Immutable UI state for the main navigation.
 * Held by [NavigationViewModel] and observed by the UI layer.
 *
 * @param currentRoute The currently active navigation route string.
 * @param previousRoute The previously active route (for transition direction detection).
 * @param notificationBadgeCount Number of unread notifications to display as a badge.
 * @param transitionType Active screen transition type for UI animation selection.
 * @param animationDurationMs Configured transition animation duration in milliseconds.
 * @param isTransitioning True if a screen transition animation is currently running.
 */
data class NavigationState(
    val currentRoute: String = NavigationRoute.Home.route,
    val previousRoute: String? = null,
    val notificationBadgeCount: Int = 0,
    val transitionType: ScreenTransitionType = ScreenTransitionType.TabSwitch,
    val animationDurationMs: Int = 300,
    val isTransitioning: Boolean = false
)

