package com.kliq.app.data.repository

import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface governing background location tracking state, reactive location update flows,
 * service lifecycle controls, and Room database persistence.
 */
interface LocationRepository {
    /** Reactive flow emitting the latest live location update. */
    val locationUpdates: StateFlow<LocationData?>

    /** Reactive flow emitting the current background tracking operational status. */
    val isTrackingActive: StateFlow<Boolean>

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
