package com.kliq.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toDomain
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubRepositoryImpl @Inject constructor(
    private val clubDao: ClubDao,
    private val apiService: KliqApiService
) : ClubRepository {

    private val gson = Gson()

    override fun getAllClubs(): Flow<List<Club>> {
        return clubDao.getAllClubs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteClubs(): Flow<List<Club>> {
        return clubDao.getFavoriteClubs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getClubById(clubId: String): Flow<Club?> {
        return clubDao.getClubById(clubId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun searchClubsLocal(query: String): Flow<List<Club>> {
        return clubDao.searchClubs(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorite(clubId: String, currentFavoriteState: Boolean) {
        clubDao.updateFavoriteStatus(clubId, !currentFavoriteState)
    }

    override suspend fun searchExternalClubs(
        query: String,
        userLat: Double?,
        userLon: Double?,
        radiusKm: Int
    ): Result<List<Club>> {
        return try {
            val response = apiService.searchExternalClubsAndEvents(
                query = query,
                latitude = userLat,
                longitude = userLon,
                radiusKm = radiusKm
            )
            val entities = response.clubs.map { it.toEntity() }
            clubDao.insertClubs(entities)
            val domainClubs = response.clubs.map { it.toDomain() }
            Result.success(domainClubs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserWithinGeofence(
        clubId: String,
        userLat: Double,
        userLon: Double
    ): Boolean {
        val clubEntity = clubDao.getClubById(clubId).firstOrNull() ?: return false
        val distanceMeters = calculateDistanceMeters(
            userLat, userLon, clubEntity.latitude, clubEntity.longitude
        )
        return distanceMeters <= clubEntity.geofenceRadiusMeters
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    private fun ClubEntity.toDomain(): Club {
        val schedule = try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = gson.fromJson(openingHoursJson, type) ?: emptyMap()
            val isOpen = map["isOpenNow"] as? Boolean ?: false
            val hoursText = map["todayHours"] as? String ?: ""
            OperatingHours(isOpenNow = isOpen, todayHours = hoursText)
        } catch (e: Exception) {
            OperatingHours(isOpenNow = false, todayHours = "")
        }

        return Club(
            id = id,
            name = name,
            location = GpsLocation(latitude, longitude, address),
            geofenceRadiusMeters = geofenceRadiusMeters,
            averageRating = averageRating,
            operatingHours = schedule,
            isFavorite = isFavorite,
            category = category,
            imageUrl = imageUrl,
            region = region,
            externalSearchTags = externalSearchTags,
            websiteUrl = websiteUrl
        )
    }
}
