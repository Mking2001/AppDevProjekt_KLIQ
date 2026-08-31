package com.kliq.app.data.repository

import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {

    val locationUpdates: StateFlow<LocationData?>

    val isTrackingActive: StateFlow<Boolean>

    val trackingMode: StateFlow<LocationTrackingMode>

    val powerPolicy: StateFlow<LocationPowerPolicy>

    val isStationary: StateFlow<Boolean>

    val isBurstActive: StateFlow<Boolean>

    val burstRemainingSeconds: StateFlow<Int>

    fun setTrackingMode(mode: LocationTrackingMode)

    fun requestHighAccuracyBurst(durationMs: Long = 30_000L)

    fun cancelBurstSession()

    fun setAppForegroundState(isForeground: Boolean)

    fun startBackgroundTracking()

    fun stopBackgroundTracking()

    suspend fun recordLocationUpdate(location: LocationData)

    fun getLatestSavedLocation(): Flow<LocationEntity?>

    fun getRecentLocationHistory(limit: Int = 50): Flow<List<LocationEntity>>

    fun getLocationCount(): Flow<Int>

    suspend fun clearLocationHistory()
}
