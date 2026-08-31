package com.kliq.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sessionRepository: SessionRepository? = null
) : ViewModel() {

    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    init {
        observeUnreadMessages()
        startPeriodicMessageSync()
    }

    private fun observeUnreadMessages() {
        viewModelScope.launch {
            chatRepository.getTotalUnreadCount().collect { count ->
                _navigationState.update { it.copy(notificationBadgeCount = count) }
            }
        }
    }

    private fun startPeriodicMessageSync() {
        viewModelScope.launch {
            while (isActive) {
                val userId = sessionRepository?.getUserId() ?: ""
                if (userId.isNotBlank()) {
                    try {
                        chatRepository.syncAllChatsAndMessages(userId)
                    } catch (ignored: Exception) { }
                }
                delay(3000L)
            }
        }
    }

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

    fun setTransitionType(type: ScreenTransitionType) {
        _navigationState.update { it.copy(transitionType = type) }
    }

    fun onTransitionStart() {
        _navigationState.update { it.copy(isTransitioning = true) }
    }

    fun onTransitionEnd() {
        _navigationState.update { it.copy(isTransitioning = false) }
    }

    fun updateNotificationBadge(count: Int) {
        _navigationState.update { it.copy(notificationBadgeCount = count.coerceAtLeast(0)) }
    }

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
