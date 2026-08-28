package com.kliq.app.data.repository

import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface governing background location tracking state, reactive location update flows,
 * service lifecycle controls, adaptive battery-saving policies, and Room database persistence.
 */
interface LocationRepository {
    /** Reactive flow emitting the latest live location update. */
    val locationUpdates: StateFlow<LocationData?>

    /** Reactive flow emitting the current background tracking operational status. */
    val isTrackingActive: StateFlow<Boolean>

    /** Reactive flow emitting the currently active tracking mode. */
    val trackingMode: StateFlow<LocationTrackingMode>

    /** Reactive flow emitting the current active power policy. */
    val powerPolicy: StateFlow<LocationPowerPolicy>

    /** Reactive flow indicating whether the device is currently stationary (triggering throttled sampling). */
    val isStationary: StateFlow<Boolean>

    /** Reactive flow indicating whether a high-accuracy burst session is actively running. */
    val isBurstActive: StateFlow<Boolean>

    /** Reactive flow indicating the remaining seconds of an active high-accuracy burst session. */
    val burstRemainingSeconds: StateFlow<Int>

    /** Configures the base location tracking mode. */
    fun setTrackingMode(mode: LocationTrackingMode)

    /** Requests a temporary high-accuracy burst session (e.g. for QR scan, check-in, geofence validation). */
    fun requestHighAccuracyBurst(durationMs: Long = 30_000L)

    /** Cancels an active burst session immediately. */
    fun cancelBurstSession()

    /** Updates the application lifecycle foreground/background state to enable adaptive throttling. */
    fun setAppForegroundState(isForeground: Boolean)

    /** Starts the background location tracking service. */
    fun startBackgroundTracking()

    /** Stops the background location tracking service. */
    fun stopBackgroundTracking()

    /** Persists a new location update to the database and updates the active StateFlow. */
    suspend fun recordLocationUpdate(location: LocationData)

    /** Retrieves the most recently stored location entity from the database. */
    fun getLatestSavedLocation(): Flow<LocationEntity?>

    /** Retrieves a list of recent background track points. */
    fun getRecentLocationHistory(limit: Int = 50): Flow<List<LocationEntity>>

    /** Returns the total count of persisted location history points. */
    fun getLocationCount(): Flow<Int>

    /** Clears all stored background location track points. */
    suspend fun clearLocationHistory()
}
