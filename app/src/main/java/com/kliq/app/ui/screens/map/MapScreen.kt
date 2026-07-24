package com.kliq.app.ui.screens.map

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.ui.components.KliqCategoryChip
import com.kliq.app.ui.components.LocationPermanentlyDeniedDialog
import com.kliq.app.ui.components.LocationRationaleDialog
import com.kliq.app.ui.components.MapQuickViewCard
import com.kliq.app.ui.navigation.TopBarMenuAction
import com.kliq.app.ui.navigation.TopBarUiState
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.util.HapticFeedbackUtils
import com.kliq.app.viewmodel.PermissionViewModel

/**
 * Map Screen integrating interactive map controls, category filters, bottom sheet venue peeking,
 * and reactive location permission workflow (Rationale Dialog & Settings Deep-Linking).
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

    // ActivityResultLauncher for requesting system location permissions
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
        // Map Surface Placeholder / SDK View
        MapPlaceholder(
            venues = uiState.nearbyVenues,
            onVenueLongPress = { viewModel.onMarkerLongPressed(it) },
            modifier = Modifier.fillMaxSize()
        )

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

        // Location FAB with Permission Workflow Trigger
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

        // Bottom-Sheet-Peek for nearby venues
        VenueBottomSheet(
            venues = uiState.nearbyVenues,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Overlay Quick View Card
        MapQuickViewCard(
            venue = uiState.selectedVenue,
            isVisible = uiState.selectedVenue != null,
            onDismiss = { viewModel.onQuickViewDismissed() },
            onNavigateDetails = { /* Navigate to Venue Detail */ },
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
 * Map placeholder with dark styling and grid lines.
 */
@Composable
private fun MapPlaceholder(
    venues: List<VenueItemUi>,
    onVenueLongPress: (VenueItemUi) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridColor = PurplePrimary.copy(alpha = 0.08f)
    val dotColor = PurplePrimaryLight.copy(alpha = 0.15f)
    val view = LocalView.current

    BoxWithConstraints(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                val gridSpacing = 60.dp.toPx()
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f,
                        pathEffect = dashEffect
                    )
                    x += gridSpacing
                }

                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                        pathEffect = dashEffect
                    )
                    y += gridSpacing
                }
            }
    ) {
        val w = maxWidth
        val h = maxHeight

        val relativePoints = listOf(
            Offset(0.3f, 0.25f),
            Offset(0.6f, 0.35f),
            Offset(0.45f, 0.5f),
            Offset(0.7f, 0.2f),
            Offset(0.2f, 0.45f)
        )

        venues.forEachIndexed { index, venue ->
            val relPoint = relativePoints[index % relativePoints.size]
            Box(
                modifier = Modifier
                    .offset(x = w * relPoint.x - 12.dp, y = h * relPoint.y - 12.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(dotColor)
                    .pointerInput(venue.id) {
                        detectTapGestures(
                            onLongPress = {
                                HapticFeedbackUtils.triggerHeavyImpact(view)
                                onVenueLongPress(venue)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PurplePrimaryLight.copy(alpha = 0.4f))
                )
            }
        }

        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Standort",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PurplePrimary.copy(alpha = 0.6f),
                                PurplePrimary.copy(alpha = 0.0f)
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Bottom-Sheet-Peek for nearby venues.
 */
@Composable
private fun VenueBottomSheet(
    venues: List<VenueItemUi>,
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
                text = "In deiner Nähe",
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
                    VenueCard(venue = venue)
                }
            }
        }
    }
}

/**
 * Individual Venue Card item.
 */
@Composable
private fun VenueCard(venue: VenueItemUi) {
    Card(
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
                    imageVector = Icons.Filled.LocationOn,
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
                    text = "${venue.category} · ${venue.distance}",
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
