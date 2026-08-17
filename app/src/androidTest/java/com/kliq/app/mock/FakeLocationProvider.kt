package com.kliq.app.mock

import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Deterministischer Fake-Standortdienst für Android UI-Tests.
 * Simuliert GPS-Updates (z. B. Berlin Alexanderplatz/Mitte) ohne echte Sensor-Abhängigkeit.
 */
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

    private val historyList = mutableListOf<LocationEntity>()

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
