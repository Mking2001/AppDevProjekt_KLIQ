package com.kliq.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
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

data class LocationTrackingUiState(
    val isTrackingActive: Boolean = false,
    val currentLocation: LocationData? = null,
    val backgroundPermissionState: LocationPermissionState = LocationPermissionState.NotRequested,
    val isBatterySaverEnabled: Boolean = true,
    val trackingMode: LocationTrackingMode = LocationTrackingMode.BALANCED_AMBIENT,
    val isStationary: Boolean = false,
    val isBurstActive: Boolean = false,
    val burstRemainingSeconds: Int = 0,
    val totalSavedPoints: Int = 0,
    val samplingIntervalMs: Long = 60_000L,
    val minDisplacementMeters: Float = 50.0f
)

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
        val trackingStatusFlow = combine(
            locationRepository.isTrackingActive,
            locationRepository.locationUpdates,
            locationRepository.getLocationCount()
        ) { isTracking, location, count ->
            Triple(isTracking, location, count)
        }

        val adaptivePolicyFlow = combine(
            locationRepository.trackingMode,
            locationRepository.powerPolicy,
            locationRepository.isStationary,
            locationRepository.isBurstActive,
            locationRepository.burstRemainingSeconds
        ) { mode, policy, stationary, burst, burstSecs ->
            AdaptiveState(mode, policy, stationary, burst, burstSecs)
        }

        combine(
            trackingStatusFlow,
            adaptivePolicyFlow
        ) { trackingStatus, adaptive ->
            LocationTrackingUiState(
                isTrackingActive = trackingStatus.first,
                currentLocation = trackingStatus.second,
                backgroundPermissionState = permissionManager.checkBackgroundLocationPermission(context),
                isBatterySaverEnabled = adaptive.mode != LocationTrackingMode.HIGH_ACCURACY,
                trackingMode = adaptive.mode,
                isStationary = adaptive.isStationary,
                isBurstActive = adaptive.isBurstActive,
                burstRemainingSeconds = adaptive.burstRemainingSeconds,
                totalSavedPoints = trackingStatus.third,
                samplingIntervalMs = adaptive.policy.intervalMillis,
                minDisplacementMeters = adaptive.policy.minDistanceDisplacementMeters
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    private data class AdaptiveState(
        val mode: LocationTrackingMode,
        val policy: LocationPowerPolicy,
        val isStationary: Boolean,
        val isBurstActive: Boolean,
        val burstRemainingSeconds: Int
    )

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

    fun setTrackingMode(mode: LocationTrackingMode) {
        locationRepository.setTrackingMode(mode)
    }

    fun triggerHighAccuracyBurst(durationMs: Long = 30_000L) {
        locationRepository.requestHighAccuracyBurst(durationMs)
    }

    fun cancelBurstSession() {
        locationRepository.cancelBurstSession()
    }

    fun setLifecycleForeground(isForeground: Boolean) {
        locationRepository.setAppForegroundState(isForeground)
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
