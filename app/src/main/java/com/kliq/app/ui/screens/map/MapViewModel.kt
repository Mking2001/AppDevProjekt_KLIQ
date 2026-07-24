package com.kliq.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.CameraPositionStateData
import com.kliq.app.data.model.MapStyleConfig
import com.kliq.app.data.repository.ClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * Immutable UI State for the MapScreen.
 *
 * @param cameraPosition Current map camera center, zoom, tilt and bearing.
 * @param styleConfig Configuration for custom dark-purple map styling.
 * @param selectedFilter Index of selected filter category chip.
 * @param filters List of available venue filter labels.
 * @param nearbyVenues List of nearby club/bar venues with map pin coordinates.
 * @param clusteredMarkers Computed cluster and single markers for performance map rendering.
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
    val clusteredMarkers: List<ClusterMarkerUiState> = emptyList(),
    val isLocationEnabled: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val selectedVenue: VenueItemUi? = null,
    val isMapLoaded: Boolean = false
)

/**
 * UI representation of a club/venue item with geographic coordinates and status metadata.
 */
data class VenueItemUi(
    val id: String,
    val name: String,
    val category: String,
    val distance: String,
    val rating: Float = 0f,
    val latitude: Double = 52.5200,
    val longitude: Double = 13.4050,
    val address: String = "",
    val activeEventTitle: String? = null,
    val isFavorite: Boolean = false,
    val currentCapacityPercent: Int = 0,
    val isOpenNow: Boolean = true
)

/**
 * ViewModel managing Map state, camera viewport, filters, custom styling,
 * ClubRepository flow observation, and performance marker clustering.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var allVenues: List<VenueItemUi> = emptyList()

    init {
        setupFilters()
        observeClubRepository()
    }

    private fun setupFilters() {
        _uiState.update { state ->
            state.copy(
                filters = listOf("Alle", "Clubs", "Bars", "Events", "Restaurants")
            )
        }
    }

    private fun observeClubRepository() {
        viewModelScope.launch {
            clubRepository.getAllClubs().collect { clubList ->
                val venues = if (clubList.isNotEmpty()) {
                    clubList.map { club ->
                        val distKm = MapClusterManager.calculateDistanceMeters(
                            _uiState.value.cameraPosition.latitude,
                            _uiState.value.cameraPosition.longitude,
                            club.location.latitude,
                            club.location.longitude
                        ) / 1000.0
                        val formattedDist = String.format(Locale.US, "%.1f km", distKm)

                        VenueItemUi(
                            id = club.id,
                            name = club.name,
                            category = club.category.ifBlank { "Club" },
                            distance = formattedDist,
                            rating = club.averageRating.toFloat(),
                            latitude = club.location.latitude,
                            longitude = club.location.longitude,
                            address = club.location.address,
                            activeEventTitle = club.activeEvent?.title,
                            isFavorite = club.isFavorite,
                            currentCapacityPercent = club.analytics.currentCapacityPercent,
                            isOpenNow = club.operatingHours.isOpenNow
                        )
                    }
                } else {
                    getFallbackVenues()
                }

                allVenues = venues
                updateFilteredAndClusteredVenues()
            }
        }
    }

    private fun updateFilteredAndClusteredVenues() {
        val filterIndex = _uiState.value.selectedFilter
        val filterName = filterIndex?.let { _uiState.value.filters.getOrNull(it) }

        val filtered = when {
            filterName == null || filterName == "Alle" -> allVenues
            filterName == "Events" -> allVenues.filter { it.activeEventTitle != null }
            filterName == "Clubs" -> allVenues.filter { it.category.contains("Club", ignoreCase = true) }
            filterName == "Bars" -> allVenues.filter { it.category.contains("Bar", ignoreCase = true) }
            filterName == "Restaurants" -> allVenues.filter { it.category.contains("Restaurant", ignoreCase = true) }
            else -> allVenues.filter { it.category.equals(filterName, ignoreCase = true) }
        }

        val zoom = _uiState.value.cameraPosition.zoom
        val clusters = MapClusterManager.clusterVenues(filtered, zoom)

        _uiState.update { state ->
            state.copy(
                nearbyVenues = filtered,
                clusteredMarkers = clusters
            )
        }
    }

    private fun getFallbackVenues(): List<VenueItemUi> {
        return listOf(
            VenueItemUi(
                id = "1",
                name = "Berghain / Panorama Bar",
                category = "Club",
                distance = "0.3 km",
                rating = 4.9f,
                latitude = 52.5112,
                longitude = 13.4430,
                address = "Am Wriezener Bahnhof, 10243 Berlin",
                activeEventTitle = "Klubnacht",
                currentCapacityPercent = 85
            ),
            VenueItemUi(
                id = "2",
                name = "Watergate",
                category = "Club",
                distance = "0.7 km",
                rating = 4.7f,
                latitude = 52.5011,
                longitude = 13.4452,
                address = "Falckensteinstraße 49, 10997 Berlin",
                activeEventTitle = "Watergate Night",
                currentCapacityPercent = 60
            ),
            VenueItemUi(
                id = "3",
                name = "KitKatClub",
                category = "Club",
                distance = "1.2 km",
                rating = 4.6f,
                latitude = 52.5114,
                longitude = 13.4172,
                address = "Köpenicker Str. 76, 10179 Berlin",
                activeEventTitle = "Symbiotikka",
                currentCapacityPercent = 90
            ),
            VenueItemUi(
                id = "4",
                name = "Sunset Lounge",
                category = "Bar",
                distance = "1.5 km",
                rating = 4.8f,
                latitude = 52.5280,
                longitude = 13.4100,
                address = "Torstraße 140, 10119 Berlin",
                currentCapacityPercent = 40
            )
        )
    }

    fun onMapLoaded() {
        _uiState.update { it.copy(isMapLoaded = true) }
    }

    fun onFilterSelected(index: Int) {
        _uiState.update { state ->
            val newFilter = if (state.selectedFilter == index) null else index
            state.copy(selectedFilter = newFilter)
        }
        updateFilteredAndClusteredVenues()
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
        updateFilteredAndClusteredVenues()
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

    fun onClusterClicked(cluster: ClusterMarkerUiState.ClusterNode) {
        val targetZoom = (_uiState.value.cameraPosition.zoom + 2.0f).coerceAtMost(18.0f)
        onCameraMoved(cluster.centerLat, cluster.centerLng, targetZoom)
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
        updateFilteredAndClusteredVenues()
    }
}
