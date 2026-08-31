package com.kliq.app.mock

import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import com.kliq.app.data.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeLocationProvider : LocationRepository {

    private val _locationUpdates = MutableStateFlow<LocationData?>(
        LocationData(
            latitude = 52.520008,
            longitude = 13.404954,
            accuracy = 5.0f,
            timestampMs = System.currentTimeMillis()
        )
    )
    override val locationUpdates: StateFlow<LocationData?> = _locationUpdates.asStateFlow()

    private val _isTrackingActive = MutableStateFlow(true)
    override val isTrackingActive: StateFlow<Boolean> = _isTrackingActive.asStateFlow()

    private val _trackingMode = MutableStateFlow(LocationTrackingMode.BALANCED_AMBIENT)
    override val trackingMode: StateFlow<LocationTrackingMode> = _trackingMode.asStateFlow()

    private val _powerPolicy = MutableStateFlow(LocationPowerPolicy.BalancedAmbient)
    override val powerPolicy: StateFlow<LocationPowerPolicy> = _powerPolicy.asStateFlow()

    override val isStationary: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
    override val isBurstActive: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
    override val burstRemainingSeconds: StateFlow<Int> = MutableStateFlow(0).asStateFlow()

    private val historyList = mutableListOf<LocationEntity>()

    override fun setTrackingMode(mode: LocationTrackingMode) {
        _trackingMode.value = mode
        _powerPolicy.value = LocationPowerPolicy.forMode(mode)
    }

    override fun requestHighAccuracyBurst(durationMs: Long) {
        _trackingMode.value = LocationTrackingMode.HIGH_ACCURACY
        _powerPolicy.value = LocationPowerPolicy.HighAccuracy
    }

    override fun cancelBurstSession() {
        _trackingMode.value = LocationTrackingMode.BALANCED_AMBIENT
        _powerPolicy.value = LocationPowerPolicy.BalancedAmbient
    }

    override fun setAppForegroundState(isForeground: Boolean) {}

    override fun startBackgroundTracking() {
        _isTrackingActive.value = true
    }

    override fun stopBackgroundTracking() {
        _isTrackingActive.value = false
    }

    override suspend fun recordLocationUpdate(location: LocationData) {
        _locationUpdates.value = location
        historyList.add(
            LocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                timestamp = location.timestampMs
            )
        )
    }

    override fun getLatestSavedLocation(): Flow<LocationEntity?> {
        val latest = historyList.lastOrNull() ?: LocationEntity(
            latitude = 52.520008,
            longitude = 13.404954,
            accuracy = 5.0f,
            timestamp = System.currentTimeMillis()
        )
        return flowOf(latest)
    }

    override fun getRecentLocationHistory(limit: Int): Flow<List<LocationEntity>> {
        return flowOf(historyList.takeLast(limit))
    }

    override fun getLocationCount(): Flow<Int> {
        return flowOf(historyList.size)
    }

    override suspend fun clearLocationHistory() {
        historyList.clear()
    }

    fun setLocation(latitude: Double, longitude: Double, accuracy: Float = 4.0f) {
        _locationUpdates.value = LocationData(
            latitude = latitude,
            longitude = longitude,
            accuracy = accuracy,
            timestampMs = System.currentTimeMillis()
        )
    }
}
