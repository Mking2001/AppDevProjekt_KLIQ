package com.kliq.app.data.repository

import com.kliq.app.data.model.Club
import kotlinx.coroutines.flow.Flow

interface ClubRepository {
    fun getAllClubs(): Flow<List<Club>>
    fun getFavoriteClubs(): Flow<List<Club>>
    fun getClubById(clubId: String): Flow<Club?>
    fun searchClubsLocal(query: String): Flow<List<Club>>
    suspend fun toggleFavorite(clubId: String, currentFavoriteState: Boolean)
    suspend fun searchExternalClubs(query: String, userLat: Double? = null, userLon: Double? = null, radiusKm: Int = 25): Result<List<Club>>
    suspend fun isUserWithinGeofence(clubId: String, userLat: Double, userLon: Double): Boolean
}
