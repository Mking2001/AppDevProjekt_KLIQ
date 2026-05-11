package com.kliq.app.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI-State der Top-App-Bar.
 * Wird vom [TopBarViewModel] verwaltet und von der UI-Schicht beobachtet.
 *
 * @param screenTitle Aktuell angezeigter Titel in der Top-Bar.
 * @param isMenuExpanded Ob das Overflow-Dropdown-Menü geöffnet ist.
 */
data class TopBarUiState(
    val screenTitle: String = "Kliq",
    val isMenuExpanded: Boolean = false
)

/**
 * ViewModel für die globale Top-App-Bar.
 * Steuert den Menü-State und den dynamischen Titel basierend
 * auf der aktuell sichtbaren Route.
 *
 * Folgt strikt dem MVVM-Pattern:
 * - Kein Compose/Android-Import
 * - Immutable State via [StateFlow]
 * - Intent-basierte Actions
 */
@HiltViewModel
class TopBarViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TopBarUiState())
    val uiState: StateFlow<TopBarUiState> = _uiState.asStateFlow()

    /**
     * Schaltet das Overflow-Menü zwischen geöffnet und geschlossen um.
     */
    fun toggleMenu() {
        _uiState.update { it.copy(isMenuExpanded = !it.isMenuExpanded) }
    }

    /**
     * Schließt das Overflow-Menü explizit.
     * Wird nach Auswahl eines Menü-Items oder bei Dismissal aufgerufen.
     */
    fun dismissMenu() {
        _uiState.update { it.copy(isMenuExpanded = false) }
    }

    /**
     * Aktualisiert den Top-Bar-Titel basierend auf der aktuellen Route.
     * Wird vom Scaffold aufgerufen, wenn sich die Navigation ändert.
     *
     * @param route Aktuelle Navigationsroute.
     */
    fun updateTitleForRoute(route: String) {
        val title = when (route) {
            NavigationRoute.Home.route -> "Kliq"
            NavigationRoute.Explore.route -> "Entdecken"
            NavigationRoute.Map.route -> "Karte"
            NavigationRoute.Notifications.route -> "Aktivität"
            NavigationRoute.Profile.route -> "Profil"
            else -> "Kliq"
        }
        _uiState.update { it.copy(screenTitle = title) }
    }
}
