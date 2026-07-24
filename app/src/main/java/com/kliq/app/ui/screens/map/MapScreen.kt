package com.kliq.app.ui.screens.map

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.ui.components.KliqCategoryChip
import com.kliq.app.ui.components.LocationPermanentlyDeniedDialog
import com.kliq.app.ui.components.LocationRationaleDialog
import com.kliq.app.ui.components.MapQuickViewCard
import com.kliq.app.ui.components.UserQuickViewCard
import com.kliq.app.ui.navigation.TopBarMenuAction
import com.kliq.app.ui.navigation.TopBarUiState
import com.kliq.app.viewmodel.PermissionViewModel

/**
 * Native Map Screen integrating Google Maps Compose SDK with custom dark-purple JSON styling,
 * custom Kliq purple club pins, circular user profile markers, performance marker clustering,
 * and interactive quick view cards.
 *
 * @param topBarState Top bar UI state.
 * @param onToggleMenu Callback for menu toggle.
 * @param onDismissMenu Callback for menu dismiss.
 * @param onMenuAction Callback for menu actions.
 * @param viewModel Hilt-injected [MapViewModel].
 * @param permissionViewModel Hilt-injected [PermissionViewModel].
 */
@Composable
fun MapScreen(
    topBarState: TopBarUiState,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
    permissionViewModel: PermissionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionUiState by permissionViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(uiState.cameraPosition.latitude, uiState.cameraPosition.longitude),
            uiState.cameraPosition.zoom
        )
    }

    LaunchedEffect(uiState.cameraPosition) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(uiState.cameraPosition.latitude, uiState.cameraPosition.longitude),
            uiState.cameraPosition.zoom
        )
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val target = cameraPositionState.position.target
            val zoom = cameraPositionState.position.zoom
            viewModel.onCameraMoved(target.latitude, target.longitude, zoom)
        }
    }

    val mapProperties = remember(uiState.styleConfig) {
        val styleOptions = try {
            MapStyleOptions.loadRawResourceStyle(context, uiState.styleConfig.styleRawResId)
        } catch (e: Exception) {
            null
        }
        MapProperties(
            mapStyleOptions = styleOptions,
            isBuildingEnabled = uiState.styleConfig.isBuildingEnabled,
            isIndoorEnabled = uiState.styleConfig.isIndoorEnabled,
            isMyLocationEnabled = uiState.isLocationEnabled
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
            // Render Custom Kliq User Profile Markers
            uiState.userMarkers.forEach { userMarker ->
                val userIcon = remember(userMarker.username, userMarker.isOnline) {
                    MarkerBitmapHelper.getUserMarkerBitmap(
                        username = userMarker.username,
                        isOnline = userMarker.isOnline
                    )
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

            // Render Clustered & Single Kliq Club Markers
            uiState.clusteredMarkers.forEach { markerItem ->
                when (markerItem) {
                    is ClusterMarkerUiState.SingleNode -> {
                        val venue = markerItem.venue
                        val clubIcon = remember(venue.category, venue.activeEventTitle) {
                            MarkerBitmapHelper.getClubMarkerBitmap(
                                category = venue.category,
                                hasActiveEvent = venue.activeEventTitle != null
                            )
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
                            }
                        )
                    }
                    is ClusterMarkerUiState.ClusterNode -> {
                        val clusterIcon = remember(markerItem.count, markerItem.primaryCategory) {
                            MarkerBitmapHelper.getClusterMarkerBitmap(
                                count = markerItem.count,
                                primaryCategory = markerItem.primaryCategory
                            )
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

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            itemsIndexed(uiState.filters) { index, filter ->
                KliqCategoryChip(
                    label = filter,
                    selected = uiState.selectedFilter == index,
                    onClick = { viewModel.onFilterSelected(index) }
                )
            }
        }

        // Location FAB
        FloatingActionButton(
            onClick = {
                if (permissionUiState.permissionState is LocationPermissionState.Granted) {
                    viewModel.onLocationRequested()
                } else {
                    permissionViewModel.onRequestPermissionClicked(context)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 240.dp),
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
                    contentDescription = "Mein Standort"
                )
            }
        }

        // Bottom Sheet Peek for nearby venues
        VenueBottomSheet(
            venues = uiState.nearbyVenues,
            onVenueClick = { viewModel.onMarkerClicked(it) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Overlay Quick View Card for selected club venue
        MapQuickViewCard(
            venue = uiState.selectedVenue,
            isVisible = uiState.selectedVenue != null,
            onDismiss = { viewModel.onQuickViewDismissed() },
            onNavigateDetails = { /* Navigate to Venue Detail */ },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )

        // Overlay Quick View Card for selected user marker
        UserQuickViewCard(
            user = uiState.selectedUser,
            isVisible = uiState.selectedUser != null,
            onDismiss = { viewModel.onUserQuickViewDismissed() },
            onSendMessage = { /* Trigger chat navigation */ },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
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
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(160.dp)
            ) {
                items(venues, key = { it.id }) { venue ->
                    VenueCard(venue = venue, onClick = { onVenueClick(venue) })
                }
            }
        }
    }
}

/**
 * Individual Venue Card item.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VenueCard(
    venue: VenueItemUi,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
