package com.kliq.app.ui.screens.explore

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI State für den Explore-Screen.
 *
 * @param searchQuery Aktuelle Sucheingabe.
 * @param selectedCategory Index der ausgewählten Kategorie (null = keine).
 * @param categories Verfügbare Filter-Kategorien.
 * @param discoverItems Platzhalter-Einträge für das Discovery-Grid.
 * @param isLoading Ob Daten geladen werden.
 */
data class ExploreUiState(
    val searchQuery: String = "",
    val selectedCategory: Int? = null,
    val categories: List<String> = emptyList(),
    val discoverItems: List<DiscoverItemUi> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * Platzhalter-Datenklasse für ein Discovery-Element.
 */
data class DiscoverItemUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String
)

/**
 * ViewModel für den Explore/Entdecken-Screen.
 * Verwaltet Such-, Filter- und Discovery-State.
 *
 * Folgt strikt dem MVVM-Pattern:
 * - Kein Compose/Android-Import
 * - Immutable State via StateFlow
 * - Intent-basierte Actions
 */
@HiltViewModel
class ExploreViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    /**
     * Lädt Platzhalter-Daten für die visuelle Darstellung.
     */
    private fun loadMockData() {
        _uiState.update { state ->
            state.copy(
                categories = listOf("Trending", "Events", "Leute", "Orte", "Clubs"),
                discoverItems = listOf(
                    DiscoverItemUi("1", "Techno Night", "Club Berghain", "Events"),
                    DiscoverItemUi("2", "Rooftop Party", "Skybar München", "Events"),
                    DiscoverItemUi("3", "DJ Max", "12.5k Follower", "Leute"),
                    DiscoverItemUi("4", "Club Luna", "Top Location", "Clubs"),
                    DiscoverItemUi("5", "After Work", "Bar Central", "Events"),
                    DiscoverItemUi("6", "Sarah K.", "8.2k Follower", "Leute"),
                    DiscoverItemUi("7", "Warehouse Rave", "Secret Location", "Events"),
                    DiscoverItemUi("8", "Sunset Lounge", "Beach Club", "Orte"),
                    DiscoverItemUi("9", "Festival 2026", "Stadtpark", "Events")
                )
            )
        }
    }

    /**
     * Stub für Suchfunktion.
     * @param query Der eingegebene Suchtext.
     */
    fun onSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // TODO: Repository-Aufruf für Suche implementieren
    }

    /**
     * Stub für Kategorie-Auswahl.
     * @param index Index der ausgewählten Kategorie.
     */
    fun onCategorySelected(index: Int) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = if (state.selectedCategory == index) null else index
            )
        }
    }
}
