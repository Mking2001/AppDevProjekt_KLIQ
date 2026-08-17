package com.kliq.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.util.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State data class for background location tracking features.
 */
data class LocationTrackingUiState(
    val isTrackingActive: Boolean = false,
    val currentLocation: LocationData? = null,
    val backgroundPermissionState: LocationPermissionState = LocationPermissionState.NotRequested,
    val isBatterySaverEnabled: Boolean = true,
    val totalSavedPoints: Int = 0
)

/**
 * ViewModel managing reactive background location tracking state, service control actions,
 * permission status evaluation, and history cleanup.
 */
@HiltViewModel
class LocationTrackingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationRepository: LocationRepository,
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationTrackingUiState())
    val uiState: StateFlow<LocationTrackingUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
        observeRepositoryState()
    }

    fun checkPermissions() {
        val permState = permissionManager.checkBackgroundLocationPermission(context)
        _uiState.value = _uiState.value.copy(backgroundPermissionState = permState)
    }

    private fun observeRepositoryState() {
        combine(
            locationRepository.isTrackingActive,
            locationRepository.locationUpdates,
            locationRepository.getLocationCount()
        ) { isTracking, location, count ->
            LocationTrackingUiState(
                isTrackingActive = isTracking,
                currentLocation = location,
                backgroundPermissionState = permissionManager.checkBackgroundLocationPermission(context),
                isBatterySaverEnabled = true,
                totalSavedPoints = count
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun toggleTracking() {
        val currentPermission = permissionManager.checkBackgroundLocationPermission(context)
        _uiState.value = _uiState.value.copy(backgroundPermissionState = currentPermission)

        if (currentPermission != LocationPermissionState.Granted) {
            return
        }

        if (_uiState.value.isTrackingActive) {
            locationRepository.stopBackgroundTracking()
        } else {
            locationRepository.startBackgroundTracking()
        }
    }

    fun openAppSettings() {
        permissionManager.openAppSettings(context)
    }

    fun clearLocationHistory() {
        viewModelScope.launch {
            locationRepository.clearLocationHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        _uiState.value = LocationTrackingUiState()
    }
}

