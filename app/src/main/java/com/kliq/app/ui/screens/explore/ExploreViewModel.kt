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
    val minRating: Float = 0f,
    val categories: List<String> = emptyList(),
    val discoverItems: List<DiscoverItemUi> = emptyList(),
    val allDiscoverItems: List<DiscoverItemUi> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * Platzhalter-Datenklasse für ein Discovery-Element.
 */
data class DiscoverItemUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val rating: Float = 0f,
    val region: String = ""
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
        val mockData = listOf(
            DiscoverItemUi("1", "Techno Night", "Club Berghain", "Events", rating = 4.8f, region = "Berlin"),
            DiscoverItemUi("2", "Rooftop Party", "Skybar München", "Events", rating = 4.5f, region = "München"),
            DiscoverItemUi("3", "DJ Max", "12.5k Follower", "Leute", rating = 4.2f, region = "Berlin"),
            DiscoverItemUi("4", "Club Luna", "Top Location", "Clubs", rating = 3.8f, region = "Hamburg"),
            DiscoverItemUi("5", "After Work", "Bar Central", "Events", rating = 4.0f, region = "München"),
            DiscoverItemUi("club_berghain", "Berghain", "Techno Temple", "Clubs", rating = 4.9f, region = "Berlin"),
            DiscoverItemUi("7", "Warehouse Rave", "Secret Location", "Events", rating = 4.7f, region = "Leipzig"),
            DiscoverItemUi("8", "Sunset Lounge", "Beach Club", "Orte", rating = 4.1f, region = "Hamburg"),
            DiscoverItemUi("9", "Festival 2026", "Stadtpark", "Events", rating = 4.6f, region = "Köln")
        )

        _uiState.update { state ->
            state.copy(
                categories = listOf("Trending", "Events", "Leute", "Orte", "Clubs"),
                allDiscoverItems = mockData,
                discoverItems = mockData
            )
        }
    }

    /**
     * Stub für Suchfunktion.
     * @param query Der eingegebene Suchtext.
     */
    fun onSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelected(index: Int) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = if (state.selectedCategory == index) null else index
            )
        }
        applyFilters()
    }

    fun onMinRatingSelected(rating: Float) {
        _uiState.update { state ->
            state.copy(minRating = if (state.minRating == rating) 0f else rating)
        }
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val query = currentState.searchQuery.lowercase()
        val minRating = currentState.minRating
        val selectedCat = currentState.selectedCategory?.let { currentState.categories[it] }

        val filtered = currentState.allDiscoverItems.filter { item ->
            val matchesQuery = query.isEmpty() || 
                item.title.lowercase().contains(query) || 
                item.region.lowercase().contains(query)
            
            val matchesRating = item.rating >= minRating
            val matchesCat = selectedCat == null || item.category == selectedCat

            matchesQuery && matchesRating && matchesCat
        }

        _uiState.update { it.copy(discoverItems = filtered) }
    }
}
