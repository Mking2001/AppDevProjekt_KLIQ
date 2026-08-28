package com.kliq.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.CameraEasing
import com.kliq.app.data.model.CameraPositionStateData
import com.kliq.app.data.model.LatLngBoundsData
import com.kliq.app.data.model.MapCameraAnimationEvent
import com.kliq.app.data.model.MapStyleConfig
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.seed.KlagenfurtSeedData
import com.kliq.app.data.generated.*
import com.kliq.app.domain.usecase.CalculateUserDistanceUseCase
import com.kliq.app.util.HapticFeedbackManager
import com.kliq.app.util.UserDistanceFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val latitude: Double = KlagenfurtSeedData.CITY_LATITUDE,
    val longitude: Double = KlagenfurtSeedData.CITY_LONGITUDE,
    val address: String = "",
    val activeEventTitle: String? = null,
    val isFavorite: Boolean = false,
    val currentCapacityPercent: Int = 0,
    val isOpenNow: Boolean = true,
    val totalLiveVisitors: Int = 0,
    val malePercentage: Int = 0,
    val femalePercentage: Int = 0
)

/**
 * High-performance ViewModel managing Map state, camera viewport, filters, custom styling,
 * asynchronous club/user data transformation, Coroutine/Flow debouncing (250ms),
 * and background marker clustering (Dispatchers.Default).
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val clubRepository: ClubRepository,
    private val getClubsWithDistanceUseCase: com.kliq.app.domain.usecase.GetClubsWithDistanceUseCase = com.kliq.app.domain.usecase.GetClubsWithDistanceUseCase(clubRepository),
    private val calculateUserDistanceUseCase: CalculateUserDistanceUseCase = CalculateUserDistanceUseCase(),
    private val userDistanceFormatter: UserDistanceFormatter = UserDistanceFormatter.default,
    private val locationRepository: LocationRepository? = null,
    private val userRepository: UserRepository? = null,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val hapticFeedbackManager: HapticFeedbackManager? = null,
    private val kliqConnector: com.kliq.app.data.generated.KliqConnectorConnector? = null
) : ViewModel() {

    companion object {
        private const val CAMERA_DEBOUNCE_MS = 250L
    }

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _cameraEventFlow = MutableSharedFlow<MapCameraAnimationEvent>(
        extraBufferCapacity = 10,
        replay = 0
    )
    val cameraEventFlow: SharedFlow<MapCameraAnimationEvent> = _cameraEventFlow.asSharedFlow()

    // Camera move stream for debounced spatial calculations during active pan & zoom
    private val cameraMoveStream = MutableSharedFlow<CameraPositionStateData>(
        extraBufferCapacity = 16,
        replay = 1
    )

    // Raw Domain / Entity models (Separation of Concerns)
    private var allVenues: List<VenueItemUi> = emptyList()
    private var allUsers: List<UserMarkerUiState> = emptyList()
    private var blockedUserIds: Set<String> = emptySet()

    init {
        setupFilters()
        loadUserMarkers()
        observeClubRepository()
        observeLocationUpdates()
        observeBlockedUsers()
        setupDebouncedCameraPipeline()
    }

    private fun setupFilters() {
        _uiState.update { state ->
            state.copy(
                filters = listOf("Alle", "Clubs", "Bars", "Events")
            )
        }
    }

    private fun setupDebouncedCameraPipeline() {
        viewModelScope.launch(defaultDispatcher) {
            cameraMoveStream
                .debounce(CAMERA_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { cameraPos ->
                    performMarkerRecalculation(cameraPos.latitude, cameraPos.longitude, cameraPos.zoom)
                }
        }
    }

    private fun observeBlockedUsers() {
        userRepository?.let { repo ->
            viewModelScope.launch(defaultDispatcher) {
                repo.getBlockedUserIds("current_user")
                    .catch { }
                    .collect { blockedList ->
                        blockedUserIds = blockedList.toSet()
                        updateUserDistances(
                            _uiState.value.cameraPosition.latitude,
                            _uiState.value.cameraPosition.longitude
                        )
                    }
            }
        }
    }

    private fun loadUserMarkers() {
        allUsers = getFallbackUsers()
        updateUserDistances(
            _uiState.value.cameraPosition.latitude,
            _uiState.value.cameraPosition.longitude
        )
        viewModelScope.launch(defaultDispatcher) {
            kliqConnector?.let { connector ->
                try {
                    val response = connector.listUsers.execute()
                    val remoteUsers = response.data.users.map { u ->
                        UserMarkerUiState(
                            userId = u.id,
                            username = u.username,
                            avatarUrl = u.profilePictureUrl,
                            latitude = KlagenfurtSeedData.CITY_LATITUDE + (Math.random() - 0.5) * 0.01,
                            longitude = KlagenfurtSeedData.CITY_LONGITUDE + (Math.random() - 0.5) * 0.01,
                            isOnline = true,
                            statusMessage = "Live via Firebase",
                            isLocationSharingEnabled = true
                        )
                    }
                    if (remoteUsers.isNotEmpty()) {
                        allUsers = remoteUsers
                        updateUserDistances(
                            _uiState.value.cameraPosition.latitude,
                            _uiState.value.cameraPosition.longitude
                        )
                    }
                } catch (ignored: Exception) {
                    // Keep fallback users
                }
            }
        }
    }

    private fun observeLocationUpdates() {
        locationRepository?.let { repo ->
            viewModelScope.launch {
                repo.locationUpdates.collect { locationData ->
                    locationData?.let { loc ->
                        updateUserDistances(loc.latitude, loc.longitude)
                        kliqConnector?.let { connector ->
                            viewModelScope.launch(defaultDispatcher) {
                                try {
                                    connector.trackUserLocation.execute(
                                        id = java.util.UUID.randomUUID().toString(),
                                        latitude = loc.latitude,
                                        longitude = loc.longitude,
                                        accuracy = loc.accuracy.toDouble(),
                                        timestampMs = System.currentTimeMillis()
                                    )
                                } catch (ignored: Exception) {
                                    // Graceful fallback for offline mode
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeClubRepository() {
        viewModelScope.launch {
            val currentLat = _uiState.value.cameraPosition.latitude
            val currentLng = _uiState.value.cameraPosition.longitude
            getClubsWithDistanceUseCase(currentLat, currentLng).collect { venues ->
                allVenues = if (venues.isNotEmpty()) venues else getFallbackVenues()
                updateFilteredAndClusteredVenues()
            }
        }
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
                updatedUsers.filter { it.isLocationSharingEnabled && !blockedUserIds.contains(it.userId) }
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

    private fun updateFilteredAndClusteredVenues() {
        viewModelScope.launch(defaultDispatcher) {
            updateFilteredAndClusteredVenuesInternal()
        }
    }

    private suspend fun updateFilteredAndClusteredVenuesInternal() = withContext(defaultDispatcher) {
        val currentState = _uiState.value
        val showPublic = currentState.showPublicEvents
        val filtered = if (showPublic) {
            val filterIndex = currentState.selectedFilter
            val filterName = filterIndex?.let { currentState.filters.getOrNull(it) }

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

        val zoom = currentState.cameraPosition.zoom
        val clusters = if (showPublic) MapClusterManager.clusterVenues(filtered, zoom) else emptyList()

        _uiState.update { state ->
            state.copy(
                nearbyVenues = filtered,
                clubMarkers = clubMarkerStates,
                clusteredMarkers = clusters
            )
        }
    }

    private suspend fun performMarkerRecalculation(lat: Double, lng: Double, zoom: Float) = withContext(defaultDispatcher) {
        val showPublic = _uiState.value.showPublicEvents
        if (showPublic && allVenues.isNotEmpty()) {
            val clusters = MapClusterManager.clusterVenues(_uiState.value.nearbyVenues, zoom)
            _uiState.update { it.copy(clusteredMarkers = clusters) }
        }
    }

    /**
     * Liefert Venue-Marker aus dem Klagenfurt-Demonstrationsdatensatz.
     * Greift nur, solange die Room-Datenbank noch nicht befuellt ist, und verwendet
     * dieselben IDs wie der Seed, damit die Navigation zum Club-Detail funktioniert.
     */
    private fun getFallbackVenues(): List<VenueItemUi> {
        return KlagenfurtSeedData.clubs().map { club ->
            VenueItemUi(
                id = club.id,
                name = club.name,
                category = club.category,
                distance = "",
                rating = club.averageRating.toFloat(),
                latitude = club.latitude,
                longitude = club.longitude,
                address = club.address,
                currentCapacityPercent = club.currentCapacityPercent,
                totalLiveVisitors = club.totalLiveVisitors,
                malePercentage = club.malePercentage,
                femalePercentage = club.femalePercentage
            )
        }
    }

    /**
     * Liefert Nutzer-Marker im Stadtgebiet Klagenfurt. Die IDs entsprechen den
     * Profilen aus [KlagenfurtSeedData], damit Chat und Profilaufruf funktionieren.
     */
    private fun getFallbackUsers(): List<UserMarkerUiState> {
        return listOf(
            UserMarkerUiState(
                userId = "usr_lena",
                username = "Lena P.",
                latitude = 46.6162,
                longitude = 14.2696,
                isOnline = true,
                statusMessage = "Sundowner an der Strandbar",
                searchIntent = "Bar & Lounge",
                isLocationSharingEnabled = true
            ),
            UserMarkerUiState(
                userId = "usr_david",
                username = "David M.",
                latitude = 46.6108,
                longitude = 14.3126,
                isOnline = true,
                statusMessage = "Floor 2 im Volksgarten",
                searchIntent = "Party",
                isLocationSharingEnabled = true
            ),
            UserMarkerUiState(
                userId = "usr_tobias",
                username = "Tobias R.",
                latitude = 46.6251,
                longitude = 14.3121,
                isOnline = true,
                statusMessage = "Soundcheck im Bollwerk",
                searchIntent = "Live-Musik",
                isLocationSharingEnabled = true
            ),
            UserMarkerUiState(
                userId = "usr_sarah",
                username = "Sarah H.",
                latitude = 46.6247,
                longitude = 14.3096,
                isOnline = false,
                statusMessage = "Cocktail in der Altstadt",
                searchIntent = "Chill",
                isLocationSharingEnabled = true
            ),
            UserMarkerUiState(
                userId = "usr_nina",
                username = "Nina S.",
                latitude = 46.6229,
                longitude = 14.3067,
                isOnline = false,
                statusMessage = "Standort privat",
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

    /**
     * Zentriert die Karte auf die letzte bekannte GPS-Position.
     * Liegt noch kein Fix vor, wird auf das Stadtzentrum Klagenfurt zurueckgefallen,
     * damit der Nutzer nicht auf einer leeren Weltkarte landet.
     */
    fun onLocationRequested() {
        val lastKnown = locationRepository?.locationUpdates?.value
        val targetLat = lastKnown?.latitude ?: KlagenfurtSeedData.CITY_LATITUDE
        val targetLng = lastKnown?.longitude ?: KlagenfurtSeedData.CITY_LONGITUDE

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

    fun toggleFavorite(clubId: String, currentFavoriteState: Boolean) {
        val nextFavorite = !currentFavoriteState
        allVenues = allVenues.map { venue ->
            if (venue.id == clubId) venue.copy(isFavorite = nextFavorite) else venue
        }
        _uiState.update { state ->
            val updatedVenue = state.selectedVenue?.let {
                if (it.id == clubId) it.copy(isFavorite = nextFavorite) else it
            }
            val updatedNearby = state.nearbyVenues.map {
                if (it.id == clubId) it.copy(isFavorite = nextFavorite) else it
            }
            state.copy(selectedVenue = updatedVenue, nearbyVenues = updatedNearby)
        }
        viewModelScope.launch {
            try {
                clubRepository.toggleFavorite(clubId, currentFavoriteState)
            } catch (ignored: Exception) {
                // Keep local UI state optimistic
            }
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
        hapticFeedbackManager?.performHeavyClick("Map marker long-press quick-view")
        onMarkerClicked(venue)
    }

    fun onQuickViewDismissed() {
        _uiState.update { it.copy(selectedVenue = null, selectedUser = null) }
    }

    fun onUserQuickViewDismissed() {
        _uiState.update { it.copy(selectedUser = null) }
    }

    fun onCameraMoved(latitude: Double, longitude: Double, zoom: Float) {
        val newPos = CameraPositionStateData(latitude, longitude, zoom)
        _uiState.update { state ->
            state.copy(cameraPosition = newPos)
        }
        cameraMoveStream.tryEmit(newPos)
        updateFilteredAndClusteredVenues()
        updateUserDistances(latitude, longitude)
    }

    override fun onCleared() {
        super.onCleared()
        MarkerBitmapHelper.clearCache()
        allVenues = emptyList()
        allUsers = emptyList()
        blockedUserIds = emptySet()
    }
}
