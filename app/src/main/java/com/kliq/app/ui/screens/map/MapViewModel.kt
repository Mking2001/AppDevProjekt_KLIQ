package com.kliq.app.ui.screens.map

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI State für den Map-Screen.
 *
 * @param selectedFilter Index des aktuell ausgewählten Filters (null = keiner).
 * @param filters Verfügbare Karten-Filter.
 * @param nearbyVenues Platzhalter-Venues für das Bottom-Sheet.
 * @param isLocationEnabled Ob Standortzugriff gewährt wurde.
 */
data class MapUiState(
    val selectedFilter: Int? = null,
    val filters: List<String> = emptyList(),
    val nearbyVenues: List<VenueItemUi> = emptyList(),
    val isLocationEnabled: Boolean = false,
    val selectedVenue: VenueItemUi? = null
)

/**
 * Platzhalter-Datenklasse für einen Venue/Ort.
 */
data class VenueItemUi(
    val id: String,
    val name: String,
    val category: String,
    val distance: String,
    val rating: Float = 0f
)

/**
 * ViewModel für den Map/Karten-Screen.
 * Verwaltet Filter- und Standort-State.
 *
 * Folgt strikt dem MVVM-Pattern:
 * - Kein Compose/Android-Import
 * - Immutable State via StateFlow
 */
@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    /**
     * Lädt Platzhalter-Daten für die visuelle Darstellung.
     */
    private fun loadMockData() {
        _uiState.update { state ->
            state.copy(
                filters = listOf("Alle", "Clubs", "Bars", "Events", "Restaurants"),
                nearbyVenues = listOf(
                    VenueItemUi("1", "Club Luna", "Club", "0.3 km", 4.5f),
                    VenueItemUi("2", "Skybar", "Bar", "0.7 km", 4.8f),
                    VenueItemUi("3", "Warehouse 23", "Club", "1.2 km", 4.2f),
                    VenueItemUi("4", "Sunset Lounge", "Bar", "1.5 km", 4.6f)
                )
            )
        }
    }

    /**
     * Stub für Filter-Auswahl auf der Karte.
     * @param index Index des ausgewählten Filters.
     */
    fun onFilterSelected(index: Int) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = if (state.selectedFilter == index) null else index
            )
        }
    }

    /**
     * Stub für Standort-Anfrage.
     * Wird in der finalen Version den GPS-Standort abrufen.
     */
    fun onLocationRequested() {
        _uiState.update { it.copy(isLocationEnabled = true) }
        // TODO: LocationProvider integrieren
    }

    fun onMarkerLongPressed(venue: VenueItemUi) {
        _uiState.update { it.copy(selectedVenue = venue) }
    }

    fun onQuickViewDismissed() {
        _uiState.update { it.copy(selectedVenue = null) }
    }
}
