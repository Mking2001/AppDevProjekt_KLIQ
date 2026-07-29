package com.kliq.app.util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.kliq.app.data.model.Club
import com.kliq.app.service.GeofenceBroadcastReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GeofenceManager {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val registeredGeofences = ConcurrentHashMap<String, Club>()

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        PendingIntent.getBroadcast(context, GEOFENCE_PENDING_INTENT_REQUEST_CODE, intent, flags)
    }

    @SuppressLint("MissingPermission")
    override suspend fun updateGeofencesForLocation(
        userLat: Double,
        userLon: Double,
        clubs: List<Club>,
        maxGeofences: Int
    ): Result<Int> = runCatching {
        if (clubs.isEmpty()) return@runCatching 0

        val sortedClubs = clubs.map { club ->
            val distance = calculateDistanceMeters(userLat, userLon, club.location.latitude, club.location.longitude)
            Pair(club, distance)
        }.sortedBy { it.second }.take(maxGeofences.coerceAtMost(MAX_SYSTEM_GEOFENCE_LIMIT))

        val targetClubIds = sortedClubs.map { it.first.id }.toSet()
        val currentRegisteredIds = registeredGeofences.keys.toSet()

        val toRemove = currentRegisteredIds.subtract(targetClubIds)
        val toAddClubs = sortedClubs.map { it.first }.filter { it.id !in currentRegisteredIds }

        if (toRemove.isNotEmpty()) {
            geofencingClient.removeGeofences(toRemove.toList()).await()
            toRemove.forEach { registeredGeofences.remove(it) }
        }

        if (toAddClubs.isNotEmpty()) {
            val geofenceList = toAddClubs.map { club ->
                Geofence.Builder()
                    .setRequestId(club.id)
                    .setCircularRegion(
                        club.location.latitude,
                        club.location.longitude,
                        club.geofenceRadiusMeters.toFloat().coerceAtLeast(MIN_GEOFENCE_RADIUS_METERS)
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            }

            val request = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build()

            geofencingClient.addGeofences(request, geofencePendingIntent).await()
            toAddClubs.forEach { registeredGeofences[it.id] = it }
        }

        registeredGeofences.size
    }

    @SuppressLint("MissingPermission")
    override suspend fun addGeofenceForClub(club: Club): Result<Unit> = runCatching {
        val geofence = Geofence.Builder()
            .setRequestId(club.id)
            .setCircularRegion(
                club.location.latitude,
                club.location.longitude,
                club.geofenceRadiusMeters.toFloat().coerceAtLeast(MIN_GEOFENCE_RADIUS_METERS)
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent).await()
        registeredGeofences[club.id] = club
    }

    override suspend fun removeGeofenceForClub(clubId: String): Result<Unit> = runCatching {
        if (registeredGeofences.containsKey(clubId)) {
            geofencingClient.removeGeofences(listOf(clubId)).await()
            registeredGeofences.remove(clubId)
        }
    }

    override suspend fun clearAllGeofences(): Result<Unit> = runCatching {
        geofencingClient.removeGeofences(geofencePendingIntent).await()
        registeredGeofences.clear()
    }

    override fun getRegisteredGeofenceIds(): List<String> {
        return registeredGeofences.keys.toList()
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    companion object {
        private const val GEOFENCE_PENDING_INTENT_REQUEST_CODE = 4600
        private const val MAX_SYSTEM_GEOFENCE_LIMIT = 100
        private const val MIN_GEOFENCE_RADIUS_METERS = 50.0f
    }
}
