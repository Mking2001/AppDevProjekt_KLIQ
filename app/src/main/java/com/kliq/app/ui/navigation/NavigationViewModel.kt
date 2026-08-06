package com.kliq.app.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel responsible for managing the main bottom navigation state and screen transitions.
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
     * Updates current/previous routes and configures tab switch transition.
     *
     * @param route The route string of the selected tab.
     */
    fun onTabSelected(route: String) {
        _navigationState.update { currentState ->
            if (currentState.currentRoute == route) {
                currentState
            } else {
                currentState.copy(
                    previousRoute = currentState.currentRoute,
                    currentRoute = route,
                    transitionType = ScreenTransitionType.TabSwitch,
                    isTransitioning = true
                )
            }
        }
    }

    /**
     * Called when navigating to a new destination.
     * Automatically classifies the transition type based on origin and target routes.
     *
     * @param targetRoute The target navigation route.
     * @param explicitType Optional explicit override for transition type.
     */
    fun onNavigateToRoute(targetRoute: String, explicitType: ScreenTransitionType? = null) {
        _navigationState.update { currentState ->
            val computedType = explicitType ?: determineTransitionType(currentState.currentRoute, targetRoute)
            currentState.copy(
                previousRoute = currentState.currentRoute,
                currentRoute = targetRoute,
                transitionType = computedType,
                isTransitioning = true
            )
        }
    }

    /**
     * Explicitly sets the active screen transition type.
     */
    fun setTransitionType(type: ScreenTransitionType) {
        _navigationState.update { it.copy(transitionType = type) }
    }

    /**
     * Signals that a screen transition animation has started.
     */
    fun onTransitionStart() {
        _navigationState.update { it.copy(isTransitioning = true) }
    }

    /**
     * Signals that a screen transition animation has completed.
     */
    fun onTransitionEnd() {
        _navigationState.update { it.copy(isTransitioning = false) }
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

    /**
     * Determines the optimal transition type based on origin and target route patterns.
     */
    fun determineTransitionType(fromRoute: String, toRoute: String): ScreenTransitionType {
        return when {
            toRoute == CoreRoutes.SPLASH || toRoute == CoreRoutes.PHONE_LOGIN || fromRoute == CoreRoutes.SPLASH -> {
                ScreenTransitionType.DefaultFade
            }
            toRoute == ProfileRoutes.QR_SCANNER -> {
                ScreenTransitionType.ModalSlideUp
            }
            (fromRoute == NavigationRoute.Map.route || fromRoute == NavigationRoute.Explore.route) &&
                    toRoute.startsWith("club_detail") -> {
                ScreenTransitionType.SharedElementExpand
            }
            fromRoute.startsWith("chat_detail") || fromRoute.startsWith("profile/other") || fromRoute.startsWith("club_detail") -> {
                ScreenTransitionType.DetailPop
            }
            toRoute.startsWith("chat_detail") || toRoute.startsWith("profile/other") || toRoute == ChatRoutes.CHAT_LIST -> {
                ScreenTransitionType.DetailPush
            }
            NavigationRoute.bottomBarItems.any { it.route == toRoute } -> {
                ScreenTransitionType.TabSwitch
            }
            else -> ScreenTransitionType.TabSwitch
        }
    }

}

