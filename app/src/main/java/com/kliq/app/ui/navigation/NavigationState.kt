package com.kliq.app.ui.navigation

data class NavigationState(
    val currentRoute: String = NavigationRoute.Home.route,
    val previousRoute: String? = null,
    val notificationBadgeCount: Int = 0,
    val transitionType: ScreenTransitionType = ScreenTransitionType.TabSwitch,
    val animationDurationMs: Int = 300,
    val isTransitioning: Boolean = false
)
