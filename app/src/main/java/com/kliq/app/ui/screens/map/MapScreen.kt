package com.kliq.app.ui.screens.map

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.data.model.MapCameraAnimationEvent
import com.kliq.app.ui.components.LocationPermanentlyDeniedDialog
import com.kliq.app.ui.components.LocationRationaleDialog
import com.kliq.app.ui.components.MapFilterSegmentedControl
import com.kliq.app.ui.components.MapQuickViewCard
import com.kliq.app.ui.components.UserQuickViewCard
import com.kliq.app.ui.navigation.TopBarMenuAction
import com.kliq.app.ui.navigation.TopBarUiState
import com.kliq.app.util.accessibilityHeading
import com.kliq.app.util.ensureMinTouchTarget
import com.kliq.app.util.talkBackDescription
import com.kliq.app.viewmodel.PermissionViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * Native Map Screen integrating Google Maps Compose SDK with custom dark-purple JSON styling,
 * custom Kliq purple club pins, circular user profile markers, performance marker clustering,
 * interactive quick view cards, and location filter mode switching (Public Events vs Private Locations).
 *
 * Behebt Abstürze auf physischen Geräten durch defensive MapsInitializer-Initialisierung,
 * Lifecycle-Management, SecurityException-geschützte Standortermittlung und overflow-sichere Filter.
 *
 * @param topBarState Top bar UI state.
 * @param onToggleMenu Callback for menu toggle.
 * @param onDismissMenu Callback for menu dismiss.
 * @param onMenuAction Callback for menu actions.
 * @param onNavigateToClub Navigation zur Club-Detailansicht.
 * @param onNavigateToChat Navigation in einen Chat mit der angetippten Person.
 * @param viewModel Hilt-injected [MapViewModel].
 * @param permissionViewModel Hilt-injected [PermissionViewModel].
 */
@Composable
fun MapScreen(
    topBarState: TopBarUiState,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    onNavigateToClub: (String) -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    viewModel: MapViewModel = hiltViewModel(),
    permissionViewModel: PermissionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionUiState by permissionViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. Defensive Initialisierung des Maps SDK Renderers vor dem Rendern
    var isMapsRendererReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            MapsInitializer.initialize(context.applicationContext, MapsInitializer.Renderer.LATEST, object : OnMapsSdkInitializedCallback {
                override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
                    Timber.d("Google Maps SDK Renderer erfolgreich initialisiert: %s", renderer)
                    isMapsRendererReady = true
                }
            })
        } catch (e: Exception) {
            Timber.e(e, "MapsInitializer.initialize mit Renderer.LATEST fehlgeschlagen, versuche Standard-Renderer")
            try {
                MapsInitializer.initialize(context.applicationContext)
                isMapsRendererReady = true
            } catch (eFallback: Exception) {
                Timber.e(eFallback, "Kritischer Fehler bei Fallback MapsInitializer.initialize")
                isMapsRendererReady = true
            }
        }
    }

    // 2. Saubere Anbindung an den Android-Lifecycle zur Vermeidung von Renderer-Deadlocks
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> Timber.d("MapScreen Lifecycle ON_START")
                Lifecycle.Event.ON_RESUME -> {
                    Timber.d("MapScreen Lifecycle ON_RESUME")
                    try {
                        permissionViewModel.checkPermissionStatus(context)
                    } catch (e: Exception) {
                        Timber.w(e, "Fehler beim Prüfen der Permissions im ON_RESUME")
                    }
                }
                Lifecycle.Event.ON_PAUSE -> Timber.d("MapScreen Lifecycle ON_PAUSE")
                Lifecycle.Event.ON_STOP -> Timber.d("MapScreen Lifecycle ON_STOP")
                Lifecycle.Event.ON_DESTROY -> Timber.d("MapScreen Lifecycle ON_DESTROY")
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 3. Defensive Initialisierung von CameraPositionState (Klagenfurt 46.6247, 14.3053, Zoom 13.5f)
    val cameraPositionState = rememberCameraPositionState {
        val initLat = if (uiState.cameraPosition.latitude != 0.0) uiState.cameraPosition.latitude else 46.6247
        val initLng = if (uiState.cameraPosition.longitude != 0.0) uiState.cameraPosition.longitude else 14.3053
        val initZoom = if (uiState.cameraPosition.zoom > 0f) uiState.cameraPosition.zoom else 13.5f
        position = CameraPosition.fromLatLngZoom(LatLng(initLat, initLng), initZoom)
    }

    // Kamera-Animation Events aus dem ViewModel verarbeiten
    LaunchedEffect(Unit) {
        viewModel.cameraEventFlow.collectLatest { event ->
            try {
                when (event) {
                    is MapCameraAnimationEvent.AnimateToLocation -> {
                        val cameraPosition = CameraPosition.Builder()
                            .target(LatLng(event.latitude, event.longitude))
                            .zoom(event.zoom)
                            .tilt(event.tilt)
                            .bearing(event.bearing)
                            .build()
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(cameraPosition),
                            durationMs = event.durationMs
                        )
                    }
                    is MapCameraAnimationEvent.AnimateToBounds -> {
                        val bounds = LatLngBounds(
                            LatLng(event.bounds.southWestLat, event.bounds.southWestLng),
                            LatLng(event.bounds.northEastLat, event.bounds.northEastLng)
                        )
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngBounds(bounds, event.paddingPx),
                            durationMs = event.durationMs
                        )
                    }
                    is MapCameraAnimationEvent.AnimateTiltRotation -> {
                        val currentPos = cameraPositionState.position
                        val cameraPosition = CameraPosition.Builder()
                            .target(currentPos.target)
                            .zoom(currentPos.zoom)
                            .tilt(event.tilt)
                            .bearing(event.bearing)
                            .build()
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(cameraPosition),
                            durationMs = event.durationMs
                        )
                    }
                    is MapCameraAnimationEvent.SnapToPosition -> {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(event.latitude, event.longitude),
                            event.zoom
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Fehler bei MapCameraAnimationEvent Ausführung")
            }
        }
    }

    // Debounced Viewport-Updates bei Kamerabewegung
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val target = cameraPositionState.position.target
            val zoom = cameraPositionState.position.zoom
            viewModel.onCameraMoved(target.latitude, target.longitude, zoom)
        }
    }

    // 4. Runtime Permission Check vor Aktivierung der Standortermittlung (SecurityException-Schutz)
    val hasLocationPermission = remember(permissionUiState.permissionState) {
        try {
            val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            fineGranted || coarseGranted
        } catch (e: Exception) {
            Timber.e(e, "Fehler bei Berechtigungsprüfung")
            false
        }
    }

    val mapProperties = remember(uiState.styleConfig, uiState.isLocationEnabled, hasLocationPermission) {
        val styleOptions = try {
            MapStyleOptions.loadRawResourceStyle(context, uiState.styleConfig.styleRawResId)
        } catch (e: Exception) {
            Timber.w(e, "MapStyle konnte nicht aus Raw-Resource geladen werden")
            null
        }
        MapProperties(
            mapStyleOptions = styleOptions,
            isBuildingEnabled = uiState.styleConfig.isBuildingEnabled,
            isIndoorEnabled = uiState.styleConfig.isIndoorEnabled,
            // isMyLocationEnabled darf NIEMALS true sein, wenn keine Permission vorliegt
            isMyLocationEnabled = uiState.isLocationEnabled && hasLocationPermission
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            tiltGesturesEnabled = true,
            zoomGesturesEnabled = true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.any { it }
        val activity = context as? Activity
        val shouldShowRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)
        } ?: false

        permissionViewModel.onPermissionResult(isGranted, shouldShowRationale)
        if (isGranted) {
            viewModel.onLocationRequested()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Native Google Map SDK Component
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapLoaded = { viewModel.onMapLoaded() },
            onMapClick = { viewModel.onQuickViewDismissed() }
        ) {
            // Rendere Custom Kliq User Profile Markers (nur wenn showPrivateLocations aktiv ist)
            if (uiState.showPrivateLocations) {
                uiState.userMarkers.forEach { userMarker ->
                    androidx.compose.runtime.key(userMarker.userId) {
                        val userIcon = remember(userMarker.username, userMarker.isOnline) {
                            try {
                                MarkerBitmapHelper.getUserMarkerBitmap(
                                    username = userMarker.username,
                                    isOnline = userMarker.isOnline
                                )
                            } catch (e: Exception) {
                                Timber.e(e, "Fehler beim Erstellen des User-Marker-Bitmaps")
                                null
                            }
                        }
                        Marker(
                            state = MarkerState(position = LatLng(userMarker.latitude, userMarker.longitude)),
                            title = userMarker.username,
                            snippet = userMarker.statusMessage ?: if (userMarker.isOnline) "Online" else "Zuletzt aktiv",
                            icon = userIcon,
                            onClick = {
                                viewModel.onUserMarkerClicked(userMarker)
                                true
                            }
                        )
                    }
                }
            }

            // Rendere Clustered & Single Kliq Club Markers (nur wenn showPublicEvents aktiv ist)
            if (uiState.showPublicEvents) {
                uiState.clusteredMarkers.forEach { markerItem ->
                    androidx.compose.runtime.key(markerItem.id) {
                        when (markerItem) {
                            is ClusterMarkerUiState.SingleNode -> {
                                val venue = markerItem.venue
                                val clubIcon = remember(venue.category, venue.activeEventTitle) {
                                    try {
                                        MarkerBitmapHelper.getClubMarkerBitmap(
                                            category = venue.category,
                                            hasActiveEvent = venue.activeEventTitle != null
                                        )
                                    } catch (e: Exception) {
                                        Timber.e(e, "Fehler beim Erstellen des Club-Marker-Bitmaps")
                                        null
                                    }
                                }
                                Marker(
                                    state = MarkerState(position = markerItem.position),
                                    title = venue.name,
                                    snippet = "${venue.category} · ${venue.distance} · ★ ${venue.rating}" +
                                            (venue.activeEventTitle?.let { " · 🎉 $it" } ?: ""),
                                    icon = clubIcon,
                                    onClick = {
                                        viewModel.onMarkerClicked(venue)
                                        true
                                    },
                                    onInfoWindowLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.onMarkerLongPressed(venue)
                                    }
                                )
                            }
                            is ClusterMarkerUiState.ClusterNode -> {
                                val clusterIcon = remember(markerItem.count, markerItem.primaryCategory) {
                                    try {
                                        MarkerBitmapHelper.getClusterMarkerBitmap(
                                            count = markerItem.count,
                                            primaryCategory = markerItem.primaryCategory
                                        )
                                    } catch (e: Exception) {
                                        Timber.e(e, "Fehler beim Erstellen des Cluster-Marker-Bitmaps")
                                        null
                                    }
                                }
                                Marker(
                                    state = MarkerState(position = markerItem.position),
                                    title = "${markerItem.count} Standorte in der Nähe",
                                    snippet = "${markerItem.primaryCategory}-Gruppe · Tippen zum Heranzoomen",
                                    icon = clusterIcon,
                                    onClick = {
                                        viewModel.onClusterClicked(markerItem)
                                        true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Top Filter Controls (MapLocationFilterMode & Category Sub-Chips)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            MapFilterSegmentedControl(
                selectedMode = uiState.locationFilterMode,
                onModeSelected = { viewModel.onLocationFilterModeSelected(it) }
            )

            if (uiState.showPublicEvents && uiState.filters.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(uiState.filters) { index, filter ->
                        FilterChip(
                            selected = uiState.selectedFilter == index,
                            onClick = { viewModel.onFilterSelected(index) },
                            label = {
                                Text(
                                    text = filter,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (uiState.selectedFilter == index) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = uiState.selectedFilter == index,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                selectedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Location FAB
        FloatingActionButton(
            onClick = {
                if (hasLocationPermission) {
                    viewModel.onLocationRequested()
                } else {
                    permissionViewModel.onRequestPermissionClicked(context)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 240.dp)
                .ensureMinTouchTarget(48.dp)
                .talkBackDescription("Aktueller Standort: Karte auf eigene Position zentrieren"),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            if (uiState.isLoadingLocation) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.5.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = null
                )
            }
        }

        // Bottom Sheet Peek for nearby venues
        VenueBottomSheet(
            venues = uiState.nearbyVenues,
            onVenueClick = { viewModel.onMarkerClicked(it) },
            onVenueLongClick = { venue ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onMarkerLongPressed(venue)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Overlay Quick View Card for selected club venue
        MapQuickViewCard(
            venue = uiState.selectedVenue,
            isVisible = uiState.selectedVenue != null,
            onDismiss = { viewModel.onQuickViewDismissed() },
            onNavigateDetails = { clubId -> onNavigateToClub(clubId) },
            onOpenRoute = { venue -> launchExternalRoute(context, venue) },
            onToggleFavorite = { clubId, isFav -> viewModel.toggleFavorite(clubId, isFav) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 135.dp)
        )

        // Overlay Quick View Card for selected user marker
        UserQuickViewCard(
            user = uiState.selectedUser,
            isVisible = uiState.selectedUser != null,
            onDismiss = { viewModel.onUserQuickViewDismissed() },
            onSendMessage = { userId -> onNavigateToChat("chat_$userId") },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 135.dp)
        )

        // Custom Kliq Location Rationale Dialog
        LocationRationaleDialog(
            isVisible = permissionUiState.showRationaleDialog,
            onConfirmActivate = {
                permissionViewModel.onRationaleDismissed()
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onDismiss = { permissionViewModel.onRationaleDismissed() }
        )

        // Custom Permanently Denied Settings Deep-Link Dialog
        LocationPermanentlyDeniedDialog(
            isVisible = permissionUiState.showPermanentlyDeniedDialog,
            onOpenSettings = { permissionViewModel.onOpenSettingsClicked(context) },
            onDismiss = { permissionViewModel.onPermanentlyDeniedDismissed() }
        )
    }
}

/**
 * Bottom-Sheet-Peek with scrollable list of nearby venues.
 */
@Composable
private fun VenueBottomSheet(
    venues: List<VenueItemUi>,
    onVenueClick: (VenueItemUi) -> Unit,
    onVenueLongClick: (VenueItemUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "In deiner Nähe (${venues.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .accessibilityHeading()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(160.dp)
            ) {
                items(venues, key = { it.id }) { venue ->
                    VenueCard(
                        venue = venue,
                        onClick = { onVenueClick(venue) },
                        onLongClick = { onVenueLongClick(venue) }
                    )
                }
            }
        }
    }
}

/**
 * Individual Venue Card item supporting long-press quick-view gesture.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VenueCard(
    venue: VenueItemUi,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val venueAccessibilityDesc = "${venue.name}, ${venue.category}, Entfernung ${venue.distance}, Bewertung ${venue.rating} von 5 Sternen" +
            (venue.activeEventTitle?.let { ", Event: $it" } ?: "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .ensureMinTouchTarget(48.dp)
            .talkBackDescription(venueAccessibilityDesc)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (venue.activeEventTitle != null) Icons.Filled.Event else Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = venue.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${venue.category} · ${venue.distance}" + (venue.activeEventTitle?.let { " · 🎉 $it" } ?: ""),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Bewertung",
                    tint = Color(0xFFFFB800),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = venue.rating.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Startet die Routenführung in einer externen Karten-App (Google Maps Navigation).
 *
 * Es wird zunächst der Google-Maps-Navigationsintent versucht (`google.navigation:q=Lat,Lng`).
 * Ist die Google Maps App nicht installiert oder schlägt fehl, wird auf einen generischen `geo:`-Intent
 * bzw. auf den Webbrowser ausgewichen.
 *
 * Alle Intent-Aufrufe sind defensiv in Try-Catch-Blöcken gekapselt.
 *
 * @param context Kontext zum Starten der Activity.
 * @param venue Ziel-Venue mit Koordinaten und Namen.
 */
private fun launchExternalRoute(context: Context, venue: VenueItemUi) {
    try {
        val label = Uri.encode(venue.name)
        val navigationUri = Uri.parse("google.navigation:q=${venue.latitude},${venue.longitude}")
        val fallbackGeoUri = Uri.parse("geo:${venue.latitude},${venue.longitude}?q=${venue.latitude},${venue.longitude}($label)")
        val webMapsUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${venue.latitude},${venue.longitude}")

        val navigationIntent = Intent(Intent.ACTION_VIEW, navigationUri).apply {
            setPackage("com.google.android.apps.maps")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(navigationIntent)
        } catch (eMapsApp: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackGeoUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            } catch (eGeo: Exception) {
                try {
                    val webIntent = Intent(Intent.ACTION_VIEW, webMapsUri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(webIntent)
                } catch (eWeb: Exception) {
                    Toast.makeText(
                        context,
                        "Es ist keine Karten-App für die Routenführung installiert.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    } catch (eAll: Exception) {
        Timber.e(eAll, "Fehler beim Vorbereiten der Routenführung")
        Toast.makeText(
            context,
            "Routenführung konnte nicht gestartet werden.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
