package com.kliq.app.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.kliq.app.data.local.dao.LocationDao
import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import com.kliq.app.service.BackgroundLocationService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [LocationRepository] coordinating state management, Room persistence,
 * and background service lifecycle commands.
 */
@Singleton
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationDao: LocationDao,
    private val ioDispatcher: CoroutineDispatcher
) : LocationRepository {

    private val _locationUpdates = MutableStateFlow<LocationData?>(null)
    override val locationUpdates: StateFlow<LocationData?> = _locationUpdates.asStateFlow()

    private val _isTrackingActive = MutableStateFlow(false)
    override val isTrackingActive: StateFlow<Boolean> = _isTrackingActive.asStateFlow()

    override fun startBackgroundTracking() {
        _isTrackingActive.value = true
        try {
            val intent = Intent(context, BackgroundLocationService::class.java).apply {
                action = BackgroundLocationService.ACTION_START
            }
            ContextCompat.startForegroundService(context, intent)
        } catch (e: Exception) {
            // Non-android host environment fallback
        }
    }

    override fun stopBackgroundTracking() {
        _isTrackingActive.value = false
        try {
            val intent = Intent(context, BackgroundLocationService::class.java).apply {
                action = BackgroundLocationService.ACTION_STOP
            }
            context.stopService(intent)
        } catch (e: Exception) {
            // Non-android host environment fallback
        }
    }

    override suspend fun recordLocationUpdate(location: LocationData) {
        _locationUpdates.value = location
        withContext(ioDispatcher) {
            val entity = LocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                timestampMs = location.timestampMs,
                speed = location.speed
            )
            locationDao.insertLocation(entity)
        }
    }

    override fun getLatestSavedLocation(): Flow<LocationEntity?> {
        return locationDao.getLatestLocation()
    }

    override fun getRecentLocationHistory(limit: Int): Flow<List<LocationEntity>> {
        return locationDao.getRecentLocations(limit)
    }

    override fun getLocationCount(): Flow<Int> {
        return locationDao.getLocationCount()
    }

    override suspend fun clearLocationHistory() {
        withContext(ioDispatcher) {
            locationDao.clearAllLocations()
        }
    }
}
