package com.kliq.app.util

import com.kliq.app.data.model.Club

interface GeofenceManager {
    suspend fun updateGeofencesForLocation(
        userLat: Double,
        userLon: Double,
        clubs: List<Club>,
        maxGeofences: Int = 50
    ): Result<Int>

    suspend fun addGeofenceForClub(club: Club): Result<Unit>
    suspend fun removeGeofenceForClub(clubId: String): Result<Unit>
    suspend fun clearAllGeofences(): Result<Unit>
    fun getRegisteredGeofenceIds(): List<String>
}
