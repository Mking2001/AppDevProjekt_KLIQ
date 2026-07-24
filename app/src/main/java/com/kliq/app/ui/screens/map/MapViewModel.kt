package com.kliq.app.ui.screens.map

import androidx.lifecycle.ViewModel
import com.kliq.app.data.model.CameraPositionStateData
import com.kliq.app.data.model.MapStyleConfig
import com.kliq.app.data.repository.ClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI State for the MapScreen.
 *
 * @param cameraPosition Current map camera center, zoom, tilt and bearing.
 * @param styleConfig Configuration for custom dark-purple map styling.
 * @param selectedFilter Index of selected filter category chip.
 * @param filters List of available venue filter labels.
 * @param nearbyVenues List of nearby club/bar venues with map pin coordinates.
 * @param isLocationEnabled State of GPS location permission.
 * @param isLoadingLocation State of location centering operation.
 * @param selectedVenue Currently selected venue for overlay quick view card.
 * @param isMapLoaded State of map render completion.
 */
data class MapUiState(
    val cameraPosition: CameraPositionStateData = CameraPositionStateData(),
    val styleConfig: MapStyleConfig = MapStyleConfig(),
    val selectedFilter: Int? = null,
    val filters: List<String> = emptyList(),
    val nearbyVenues: List<VenueItemUi> = emptyList(),
    val isLocationEnabled: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val selectedVenue: VenueItemUi? = null,
    val isMapLoaded: Boolean = false
)

/**
 * UI representation of a club/venue item with geographic coordinates.
 */
data class VenueItemUi(
    val id: String,
    val name: String,
    val category: String,
    val distance: String,
    val rating: Float = 0f,
    val latitude: Double = 52.5200,
    val longitude: Double = 13.4050,
    val address: String = ""
)

/**
 * ViewModel managing Map state, camera viewport, filters, custom styling,
 * and venue marker interactions following strict MVVM principles.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMapData()
    }

    private fun loadMapData() {
        _uiState.update { state ->
            state.copy(
                filters = listOf("Alle", "Clubs", "Bars", "Events", "Restaurants"),
                nearbyVenues = listOf(
                    VenueItemUi(
                        id = "1",
                        name = "Berghain / Panorama Bar",
                        category = "Club",
                        distance = "0.3 km",
                        rating = 4.9f,
                        latitude = 52.5112,
                        longitude = 13.4430,
                        address = "Am Wriezener Bahnhof, 10243 Berlin"
                    ),
                    VenueItemUi(
                        id = "2",
                        name = "Watergate",
                        category = "Club",
                        distance = "0.7 km",
                        rating = 4.7f,
                        latitude = 52.5011,
                        longitude = 13.4452,
                        address = "Falckensteinstraße 49, 10997 Berlin"
                    ),
                    VenueItemUi(
                        id = "3",
                        name = "KitKatClub",
                        category = "Club",
                        distance = "1.2 km",
                        rating = 4.6f,
                        latitude = 52.5114,
                        longitude = 13.4172,
                        address = "Köpenicker Str. 76, 10179 Berlin"
                    ),
                    VenueItemUi(
                        id = "4",
                        name = "Sunset Lounge",
                        category = "Bar",
                        distance = "1.5 km",
                        rating = 4.8f,
                        latitude = 52.5280,
                        longitude = 13.4100,
                        address = "Torstraße 140, 10119 Berlin"
                    )
                )
            )
        }
    }

    fun onMapLoaded() {
        _uiState.update { it.copy(isMapLoaded = true) }
    }

    fun onFilterSelected(index: Int) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = if (state.selectedFilter == index) null else index
            )
        }
    }

    fun onLocationRequested() {
        _uiState.update { state ->
            state.copy(
                isLocationEnabled = true,
                isLoadingLocation = true,
                cameraPosition = CameraPositionStateData(
                    latitude = 52.5112,
                    longitude = 13.4430,
                    zoom = 15.0f
                )
            )
        }
        _uiState.update { it.copy(isLoadingLocation = false) }
    }

    fun onMarkerClicked(venue: VenueItemUi) {
        _uiState.update { state ->
            state.copy(
                selectedVenue = venue,
                cameraPosition = CameraPositionStateData(
                    latitude = venue.latitude,
                    longitude = venue.longitude,
                    zoom = 16.0f
                )
            )
        }
    }

    fun onMarkerLongPressed(venue: VenueItemUi) {
        onMarkerClicked(venue)
    }

    fun onQuickViewDismissed() {
        _uiState.update { it.copy(selectedVenue = null) }
    }

    fun onCameraMoved(latitude: Double, longitude: Double, zoom: Float) {
        _uiState.update { state ->
            state.copy(
                cameraPosition = CameraPositionStateData(latitude, longitude, zoom)
            )
        }
    }
}
