package com.kliq.app.util

import android.location.Location
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Controller managing adaptive location sampling intervals, device stationary detection,
 * burst session lifecycles, and application lifecycle-aware power throttling.
 */
@Singleton
class AdaptiveLocationController @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(defaultDispatcher)

    private val _configuredMode = MutableStateFlow(LocationTrackingMode.BALANCED_AMBIENT)
    val configuredMode: StateFlow<LocationTrackingMode> = _configuredMode.asStateFlow()

    private val _effectiveMode = MutableStateFlow(LocationTrackingMode.BALANCED_AMBIENT)
    val effectiveMode: StateFlow<LocationTrackingMode> = _effectiveMode.asStateFlow()

    private val _currentPolicy = MutableStateFlow(LocationPowerPolicy.BalancedAmbient)
    val currentPolicy: StateFlow<LocationPowerPolicy> = _currentPolicy.asStateFlow()

    private val _isStationary = MutableStateFlow(false)
    val isStationary: StateFlow<Boolean> = _isStationary.asStateFlow()

    private val _isForeground = MutableStateFlow(true)
    val isForeground: StateFlow<Boolean> = _isForeground.asStateFlow()

    private val _isBurstActive = MutableStateFlow(false)
    val isBurstActive: StateFlow<Boolean> = _isBurstActive.asStateFlow()

    private val _burstRemainingSeconds = MutableStateFlow(0)
    val burstRemainingSeconds: StateFlow<Int> = _burstRemainingSeconds.asStateFlow()

    private var burstJob: Job? = null
    private var lastRecordedLocation: LocationData? = null
    private var stationaryCount = 0

    companion object {
        private const val STATIONARY_SPEED_THRESHOLD_MPS = 0.5f
        private const val STATIONARY_DISTANCE_THRESHOLD_METERS = 15.0f
        private const val CONSECUTIVE_STATIONARY_FIXES_REQUIRED = 2
        private const val DEFAULT_BURST_DURATION_MS = 30_000L
    }

    /**
     * Updates the base target tracking mode configured by the user or application.
     */
    fun setTrackingMode(mode: LocationTrackingMode) {
        _configuredMode.value = mode
        if (!_isBurstActive.value) {
            recalculateEffectiveMode()
        }
    }

    /**
     * Informs the controller whether the app is in the foreground or background.
     * When backgrounded without an active burst, tracking is throttled to conserve battery.
     */
    fun setForegroundState(inForeground: Boolean) {
        _isForeground.value = inForeground
        if (!_isBurstActive.value) {
            recalculateEffectiveMode()
        }
    }

    /**
     * Triggers a temporary high-accuracy burst session (e.g. for QR scan, check-in, geofence validation).
     * Automatically reverts to the appropriate ambient/idle mode once the timeout expires.
     */
    fun requestHighAccuracyBurst(durationMs: Long = DEFAULT_BURST_DURATION_MS) {
        burstJob?.cancel()
        _isBurstActive.value = true
        _effectiveMode.value = LocationTrackingMode.HIGH_ACCURACY
        _currentPolicy.value = LocationPowerPolicy.HighAccuracy

        val totalSeconds = (durationMs / 1000).toInt().coerceAtLeast(1)
        _burstRemainingSeconds.value = totalSeconds

        burstJob = scope.launch {
            for (sec in totalSeconds downTo 1) {
                _burstRemainingSeconds.value = sec
                delay(1000L)
            }
            _burstRemainingSeconds.value = 0
            _isBurstActive.value = false
            recalculateEffectiveMode()
        }
    }

    /**
     * Cancels an active burst session immediately and restores standard power policies.
     */
    fun cancelBurstSession() {
        burstJob?.cancel()
        burstJob = null
        _isBurstActive.value = false
        _burstRemainingSeconds.value = 0
        recalculateEffectiveMode()
    }

    /**
     * Ingests a new location fix to evaluate movement and update stationary state.
     */
    fun onLocationSampleReceived(location: LocationData) {
        val prev = lastRecordedLocation
        lastRecordedLocation = location

        if (prev != null) {
            val distance = calculateDistanceMeters(
                prev.latitude,
                prev.longitude,
                location.latitude,
                location.longitude
            )
            val isLowSpeed = location.speed < STATIONARY_SPEED_THRESHOLD_MPS
            val isLowDisplacement = distance < STATIONARY_DISTANCE_THRESHOLD_METERS

            if (isLowSpeed && isLowDisplacement) {
                stationaryCount++
                if (stationaryCount >= CONSECUTIVE_STATIONARY_FIXES_REQUIRED && !_isStationary.value) {
                    _isStationary.value = true
                    recalculateEffectiveMode()
                }
            } else {
                stationaryCount = 0
                if (_isStationary.value) {
                    _isStationary.value = false
                    recalculateEffectiveMode()
                }
            }
        }
    }

    /**
     * Manually overrides or resets the stationary status (useful for simulated movement or testing).
     */
    fun setStationaryState(stationary: Boolean) {
        _isStationary.value = stationary
        stationaryCount = if (stationary) CONSECUTIVE_STATIONARY_FIXES_REQUIRED else 0
        recalculateEffectiveMode()
    }

    /**
     * Recalculates effective mode and policy based on configured mode, burst status,
     * foreground lifecycle state, and stationary detection.
     */
    private fun recalculateEffectiveMode() {
        if (_isBurstActive.value) {
            _effectiveMode.value = LocationTrackingMode.HIGH_ACCURACY
            _currentPolicy.value = LocationPowerPolicy.HighAccuracy
            return
        }

        val baseMode = _configuredMode.value
        val inForeground = _isForeground.value
        val isStationary = _isStationary.value

        val resolvedMode = when {
            !inForeground -> LocationTrackingMode.IDLE_PASSIVE
            isStationary && baseMode == LocationTrackingMode.BALANCED_AMBIENT -> LocationTrackingMode.IDLE_PASSIVE
            else -> baseMode
        }

        _effectiveMode.value = resolvedMode
        _currentPolicy.value = LocationPowerPolicy.forMode(resolvedMode)
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}
