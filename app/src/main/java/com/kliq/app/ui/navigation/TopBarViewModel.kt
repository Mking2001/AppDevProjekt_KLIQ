package com.kliq.app.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TopBarUiState(
    val screenTitle: String = "Kliq",
    val isMenuExpanded: Boolean = false
)

@HiltViewModel
class TopBarViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TopBarUiState())
    val uiState: StateFlow<TopBarUiState> = _uiState.asStateFlow()

    fun toggleMenu() {
        _uiState.update { it.copy(isMenuExpanded = !it.isMenuExpanded) }
    }

    fun dismissMenu() {
        _uiState.update { it.copy(isMenuExpanded = false) }
    }

    fun updateTitleForRoute(route: String) {
        val title = when (route) {
            NavigationRoute.Home.route -> "Kliq"
            NavigationRoute.Explore.route -> "Entdecken"
            NavigationRoute.Map.route -> "Karte"
            NavigationRoute.Messages.route -> "Nachrichten"
            NavigationRoute.Notifications.route -> "Aktivität"
            NavigationRoute.Profile.route -> "Profil"
            else -> "Kliq"
        }
        _uiState.update { it.copy(screenTitle = title) }
    }
}
