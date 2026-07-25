package com.kliq.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.CameraEasing
import com.kliq.app.data.model.CameraPositionStateData
import com.kliq.app.data.model.LatLngBoundsData
import com.kliq.app.data.model.MapCameraAnimationEvent
import com.kliq.app.data.model.MapStyleConfig
import com.kliq.app.data.repository.ClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.domain.usecase.CalculateUserDistanceUseCase
import com.kliq.app.util.UserDistanceFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * Enum defining map location filtering modes (Öffentliche Events vs. Private Standorte vs. Alle).
 */
enum class MapLocationFilterMode {
    ALL,
    PUBLIC_ONLY,
    PRIVATE_ONLY
}

/**
 * UI State representation of a club/event map marker.
 */
data class ClubMarkerUiState(
    val id: String,
    val name: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val hasActiveEvent: Boolean = false,
    val activeEventTitle: String? = null,
    val rating: Float = 0f,
    val distance: String = "",
    val isOpenNow: Boolean = true,
    val capacityPercent: Int = 0,
    val venue: VenueItemUi
)

/**
 * UI State representation of a Kliq user map marker.
 */
data class UserMarkerUiState(
    val userId: String,
    val username: String,
    val avatarUrl: String? = null,
    val latitude: Double,
    val longitude: Double,
    val isOnline: Boolean = true,
    val statusMessage: String? = null,
    val searchIntent: String? = null,
    val distanceMeters: Double? = null,
    val formattedDistance: String = "",
    val isLocationSharingEnabled: Boolean = true
)

/**
 * Immutable UI State for the MapScreen.
 *
 * @param cameraPosition Current map camera center, zoom, tilt and bearing.
 * @param styleConfig Configuration for custom dark-purple map styling.
 * @param selectedFilter Index of selected filter category chip.
 * @param filters List of available venue filter labels.
 * @param locationFilterMode Selected location filter mode (ALL, PUBLIC_ONLY, PRIVATE_ONLY).
 * @param showPublicEvents Whether public club & event markers are displayed.
 * @param showPrivateLocations Whether private user location markers are displayed.
 * @param nearbyVenues List of nearby club/bar venues with map pin coordinates.
 * @param clubMarkers Structured club map marker UI states.
 * @param userMarkers Structured user map marker UI states.
 * @param clusteredMarkers Computed cluster and single markers for performance map rendering.
 * @param isLocationEnabled State of GPS location permission.
 * @param isLoadingLocation State of location centering operation.
 * @param selectedVenue Currently selected venue for overlay quick view card.
 * @param selectedUser Currently selected user profile marker for overlay quick view.
 * @param isMapLoaded State of map render completion.
 */
data class MapUiState(
    val cameraPosition: CameraPositionStateData = CameraPositionStateData(),
    val styleConfig: MapStyleConfig = MapStyleConfig(),
    val selectedFilter: Int? = null,
    val filters: List<String> = emptyList(),
    val locationFilterMode: MapLocationFilterMode = MapLocationFilterMode.ALL,
    val showPublicEvents: Boolean = true,
    val showPrivateLocations: Boolean = true,
    val nearbyVenues: List<VenueItemUi> = emptyList(),
    val clubMarkers: List<ClubMarkerUiState> = emptyList(),
    val userMarkers: List<UserMarkerUiState> = emptyList(),
    val clusteredMarkers: List<ClusterMarkerUiState> = emptyList(),
    val isLocationEnabled: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val selectedVenue: VenueItemUi? = null,
    val selectedUser: UserMarkerUiState? = null,
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
 * ClubRepository flow observation, separate club/user marker UI states,
 * privacy-aware user location filtering, and performance marker clustering.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val clubRepository: ClubRepository,
    private val calculateUserDistanceUseCase: CalculateUserDistanceUseCase = CalculateUserDistanceUseCase(),
    private val userDistanceFormatter: UserDistanceFormatter = UserDistanceFormatter.default,
    private val locationRepository: LocationRepository? = null,
    private val defaultDispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _cameraEventFlow = MutableSharedFlow<MapCameraAnimationEvent>(
        extraBufferCapacity = 10,
        replay = 0
    )
    val cameraEventFlow: SharedFlow<MapCameraAnimationEvent> = _cameraEventFlow.asSharedFlow()

    private var allVenues: List<VenueItemUi> = emptyList()
    private var allUsers: List<UserMarkerUiState> = emptyList()

    init {
        setupFilters()
        loadUserMarkers()
        observeClubRepository()
        observeLocationUpdates()
    }

    private fun setupFilters() {
        _uiState.update { state ->
            state.copy(
                filters = listOf("Alle", "Clubs", "Bars", "Events", "Restaurants")
            )
        }
    }

    private fun loadUserMarkers() {
        allUsers = getFallbackUsers()
        updateUserDistances(_uiState.value.cameraPosition.latitude, _uiState.value.cameraPosition.longitude)
    }

    fun updateUserDistances(currentLat: Double, currentLng: Double) {
        viewModelScope.launch(defaultDispatcher) {
            val updatedUsers = allUsers.map { user ->
                val rawDist = calculateUserDistanceUseCase.calculateDistanceMeters(
                    startLat = currentLat,
                    startLng = currentLng,
                    endLat = user.latitude,
                    endLng = user.longitude
                )
                val formatted = userDistanceFormatter.formatDistance(rawDist)
                user.copy(
                    distanceMeters = rawDist,
                    formattedDistance = formatted
                )
            }
            allUsers = updatedUsers
            val showPrivate = _uiState.value.showPrivateLocations
            val visibleUsers = if (showPrivate) {
                updatedUsers.filter { it.isLocationSharingEnabled }
            } else {
                emptyList()
            }
            _uiState.update { state ->
                val updatedSelected = state.selectedUser?.let { selected ->
                    visibleUsers.find { it.userId == selected.userId }
                }
                state.copy(
                    userMarkers = visibleUsers,
                    selectedUser = updatedSelected
                )
            }
        }
    }

    private fun observeLocationUpdates() {
        locationRepository?.let { repo ->
            viewModelScope.launch {
                repo.locationUpdates.collect { locationData ->
                    locationData?.let { loc ->
                        updateUserDistances(loc.latitude, loc.longitude)
                    }
                }
            }
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
        val showPublic = _uiState.value.showPublicEvents
        val filtered = if (showPublic) {
            val filterIndex = _uiState.value.selectedFilter
            val filterName = filterIndex?.let { _uiState.value.filters.getOrNull(it) }

            when {
                filterName == null || filterName == "Alle" -> allVenues
                filterName == "Events" -> allVenues.filter { it.activeEventTitle != null }
                filterName == "Clubs" -> allVenues.filter { it.category.contains("Club", ignoreCase = true) }
                filterName == "Bars" -> allVenues.filter { it.category.contains("Bar", ignoreCase = true) }
                filterName == "Restaurants" -> allVenues.filter { it.category.contains("Restaurant", ignoreCase = true) }
                else -> allVenues.filter { it.category.equals(filterName, ignoreCase = true) }
            }
        } else {
            emptyList()
        }

        val clubMarkerStates = filtered.map { venue ->
            ClubMarkerUiState(
                id = venue.id,
                name = venue.name,
                category = venue.category,
                latitude = venue.latitude,
                longitude = venue.longitude,
                hasActiveEvent = venue.activeEventTitle != null,
                activeEventTitle = venue.activeEventTitle,
                rating = venue.rating,
                distance = venue.distance,
                isOpenNow = venue.isOpenNow,
                capacityPercent = venue.currentCapacityPercent,
                venue = venue
            )
        }

        val zoom = _uiState.value.cameraPosition.zoom
        val clusters = if (showPublic) MapClusterManager.clusterVenues(filtered, zoom) else emptyList()

        _uiState.update { state ->
            state.copy(
                nearbyVenues = filtered,
                clubMarkers = clubMarkerStates,
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

    private fun getFallbackUsers(): List<UserMarkerUiState> {
        return listOf(
            UserMarkerUiState(
                userId = "u1",
                username = "Alex",
                latitude = 52.5130,
                longitude = 13.4410,
                isOnline = true,
                statusMessage = "Looking for Techno party",
                searchIntent = "Party",
                isLocationSharingEnabled = true
            ),
            UserMarkerUiState(
                userId = "u2",
                username = "Sophie",
                latitude = 52.5050,
                longitude = 13.4480,
                isOnline = true,
                statusMessage = "Drinks at Watergate?",
                searchIntent = "Bar & Lounge",
                isLocationSharingEnabled = true
            ),
            UserMarkerUiState(
                userId = "u3",
                username = "Leon",
                latitude = 52.5180,
                longitude = 13.4120,
                isOnline = false,
                statusMessage = "Chilling",
                searchIntent = "Chill",
                isLocationSharingEnabled = true
            ),
            UserMarkerUiState(
                userId = "u4",
                username = "Private User",
                latitude = 52.5200,
                longitude = 13.4000,
                isOnline = false,
                statusMessage = "Invisible",
                searchIntent = null,
                isLocationSharingEnabled = false
            )
        )
    }

    fun onLocationFilterModeSelected(mode: MapLocationFilterMode) {
        val showPublic = mode == MapLocationFilterMode.ALL || mode == MapLocationFilterMode.PUBLIC_ONLY
        val showPrivate = mode == MapLocationFilterMode.ALL || mode == MapLocationFilterMode.PRIVATE_ONLY

        _uiState.update { state ->
            state.copy(
                locationFilterMode = mode,
                showPublicEvents = showPublic,
                showPrivateLocations = showPrivate
            )
        }
        updateFilteredAndClusteredVenues()
        updateUserDistances(_uiState.value.cameraPosition.latitude, _uiState.value.cameraPosition.longitude)
        triggerAutoFitCameraAnimation()
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
        triggerAutoFitCameraAnimation()
    }

    fun onLocationRequested() {
        val targetLat = 52.5112
        val targetLng = 13.4430
        _uiState.update { state ->
            state.copy(
                isLocationEnabled = true,
                isLoadingLocation = true,
                cameraPosition = CameraPositionStateData(
                    latitude = targetLat,
                    longitude = targetLng,
                    zoom = 15.0f,
                    tilt = 0.0f,
                    bearing = 0.0f
                )
            )
        }
        updateFilteredAndClusteredVenues()
        updateUserDistances(targetLat, targetLng)
        _uiState.update { it.copy(isLoadingLocation = false) }

        viewModelScope.launch {
            _cameraEventFlow.emit(
                MapCameraAnimationEvent.AnimateToLocation(
                    latitude = targetLat,
                    longitude = targetLng,
                    zoom = 15.0f,
                    tilt = 0.0f,
                    bearing = 0.0f,
                    durationMs = 1000,
                    easing = CameraEasing.EASE_IN_OUT
                )
            )
        }
    }

    fun onClubMarkerClicked(clubMarker: ClubMarkerUiState) {
        onMarkerClicked(clubMarker.venue)
    }

    fun onMarkerClicked(venue: VenueItemUi) {
        _uiState.update { state ->
            state.copy(
                selectedVenue = venue,
                selectedUser = null,
                cameraPosition = CameraPositionStateData(
                    latitude = venue.latitude,
                    longitude = venue.longitude,
                    zoom = 16.0f,
                    tilt = 35.0f,
                    bearing = 15.0f
                )
            )
        }
        viewModelScope.launch {
            _cameraEventFlow.emit(
                MapCameraAnimationEvent.AnimateToLocation(
                    latitude = venue.latitude,
                    longitude = venue.longitude,
                    zoom = 16.0f,
                    tilt = 35.0f,
                    bearing = 15.0f,
                    durationMs = 1000,
                    easing = CameraEasing.EASE_IN_OUT
                )
            )
        }
    }

    fun onUserMarkerClicked(userMarker: UserMarkerUiState) {
        _uiState.update { state ->
            state.copy(
                selectedUser = userMarker,
                selectedVenue = null,
                cameraPosition = CameraPositionStateData(
                    latitude = userMarker.latitude,
                    longitude = userMarker.longitude,
                    zoom = 16.0f,
                    tilt = 35.0f,
                    bearing = 15.0f
                )
            )
        }
        viewModelScope.launch {
            _cameraEventFlow.emit(
                MapCameraAnimationEvent.AnimateToLocation(
                    latitude = userMarker.latitude,
                    longitude = userMarker.longitude,
                    zoom = 16.0f,
                    tilt = 35.0f,
                    bearing = 15.0f,
                    durationMs = 1000,
                    easing = CameraEasing.EASE_IN_OUT
                )
            )
        }
    }

    fun onClusterClicked(cluster: ClusterMarkerUiState.ClusterNode) {
        val targetZoom = (_uiState.value.cameraPosition.zoom + 2.0f).coerceAtMost(18.0f)
        onCameraMoved(cluster.centerLat, cluster.centerLng, targetZoom)
        viewModelScope.launch {
            _cameraEventFlow.emit(
                MapCameraAnimationEvent.AnimateToLocation(
                    latitude = cluster.centerLat,
                    longitude = cluster.centerLng,
                    zoom = targetZoom,
                    tilt = 20.0f,
                    durationMs = 800
                )
            )
        }
    }

    fun animateNightPerspective(tilt: Float = 40.0f, bearing: Float = 25.0f) {
        viewModelScope.launch {
            _cameraEventFlow.emit(
                MapCameraAnimationEvent.AnimateTiltRotation(
                    tilt = tilt,
                    bearing = bearing,
                    durationMs = 800
                )
            )
        }
    }

    private fun triggerAutoFitCameraAnimation() {
        viewModelScope.launch(defaultDispatcher) {
            val visibleCoordinates = mutableListOf<Pair<Double, Double>>()
            val state = _uiState.value
            if (state.showPublicEvents) {
                visibleCoordinates.addAll(state.nearbyVenues.map { Pair(it.latitude, it.longitude) })
            }
            if (state.showPrivateLocations) {
                visibleCoordinates.addAll(state.userMarkers.map { Pair(it.latitude, it.longitude) })
            }

            if (visibleCoordinates.size >= 2) {
                LatLngBoundsData.fromCoordinates(visibleCoordinates)?.let { bounds ->
                    _cameraEventFlow.emit(
                        MapCameraAnimationEvent.AnimateToBounds(
                            bounds = bounds,
                            paddingPx = 120,
                            durationMs = 1000
                        )
                    )
                }
            } else if (visibleCoordinates.size == 1) {
                val (lat, lng) = visibleCoordinates.first()
                _cameraEventFlow.emit(
                    MapCameraAnimationEvent.AnimateToLocation(
                        latitude = lat,
                        longitude = lng,
                        zoom = 15.5f,
                        durationMs = 1000
                    )
                )
            }
        }
    }

    fun onMarkerLongPressed(venue: VenueItemUi) {
        onMarkerClicked(venue)
    }

    fun onQuickViewDismissed() {
        _uiState.update { it.copy(selectedVenue = null, selectedUser = null) }
    }

    fun onUserQuickViewDismissed() {
        _uiState.update { it.copy(selectedUser = null) }
    }

    fun onCameraMoved(latitude: Double, longitude: Double, zoom: Float) {
        _uiState.update { state ->
            state.copy(
                cameraPosition = CameraPositionStateData(latitude, longitude, zoom)
            )
        }
        updateFilteredAndClusteredVenues()
        updateUserDistances(latitude, longitude)
    }
}
