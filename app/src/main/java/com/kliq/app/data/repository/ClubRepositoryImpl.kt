package com.kliq.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Gender
import com.kliq.app.data.model.GenderRatio
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.model.RegionSearchResult
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toDomain
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubRepositoryImpl @Inject constructor(
    private val clubDao: ClubDao,
    private val visitedLogDao: VisitedLogDao,
    private val apiService: KliqApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ClubRepository {

    private val gson = Gson()

    override fun getAllClubs(): Flow<List<Club>> {
        return clubDao.getAllClubs().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getFavoriteClubs(): Flow<List<Club>> {
        return clubDao.getFavoriteClubs().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getClubById(clubId: String): Flow<Club?> {
        return clubDao.getClubById(clubId).map { entity ->
            entity?.toDomain()
        }.flowOn(Dispatchers.IO)
    }

    override fun searchClubsLocal(query: String): Flow<List<Club>> {
        return clubDao.searchClubs(query).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun searchClubsFiltered(
        query: String,
        regionFilter: String?,
        genreFilter: String?
    ): Flow<List<Club>> {
        val q = query.trim()
        val r = regionFilter?.trim() ?: ""
        val g = genreFilter?.trim() ?: ""
        return clubDao.searchClubsFiltered(q, r, g).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun searchRegionsAndCities(query: String): Flow<List<RegionSearchResult>> {
        val q = query.trim()
        return clubDao.getAllClubs().map { entities ->
            val clubs = entities.map { it.toDomain() }
            val regionCounts = mutableMapOf<String, Int>()
            
            for (club in clubs) {
                val regionName = when {
                    club.region.isNotBlank() -> club.region
                    club.location.address.contains(",") -> club.location.address.substringAfterLast(",").trim()
                    else -> ""
                }
                if (regionName.isNotBlank() && (q.isEmpty() || regionName.contains(q, ignoreCase = true))) {
                    regionCounts[regionName] = (regionCounts[regionName] ?: 0) + 1
                }
            }
            
            regionCounts.map { (name, count) ->
                RegionSearchResult(
                    regionName = name,
                    clubCount = count,
                    isCity = true
                )
            }.sortedByDescending { it.clubCount }
        }.flowOn(ioDispatcher)
    }

    override suspend fun toggleFavorite(clubId: String, currentFavoriteState: Boolean) = withContext(Dispatchers.IO) {
        clubDao.updateFavoriteStatus(clubId, !currentFavoriteState)
    }

    override suspend fun searchExternalClubs(
        query: String,
        userLat: Double?,
        userLon: Double?,
        radiusKm: Int
    ): Result<List<Club>> = withContext(Dispatchers.IO) {
        try {
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
    ): Boolean = withContext(Dispatchers.IO) {
        val clubEntity = clubDao.getClubById(clubId).firstOrNull() ?: return@withContext false
        val distanceMeters = calculateDistanceMeters(
            userLat, userLon, clubEntity.latitude, clubEntity.longitude
        )
        distanceMeters <= clubEntity.geofenceRadiusMeters
    }

    override fun getClubGenderRatio(clubId: String, timeWindowMs: Long): Flow<GenderRatio> {
        val sinceTimestamp = System.currentTimeMillis() - timeWindowMs
        return visitedLogDao.getGenderCountsForClub(clubId, sinceTimestamp).map { counts ->
            var male = 0
            var female = 0
            var diverse = 0

            for (item in counts) {
                val genderEnum = Gender.fromString(item.gender)
                when (genderEnum) {
                    Gender.MALE -> male += item.count
                    Gender.FEMALE -> female += item.count
                    Gender.DIVERSE, Gender.OTHER -> diverse += item.count
                    else -> {}
                }
            }

            GenderRatio.calculate(
                maleCount = male,
                femaleCount = female,
                diverseCount = diverse
            )
        }.flowOn(ioDispatcher)
    }

    override suspend fun calculateClubGenderRatio(clubId: String, timeWindowMs: Long): GenderRatio = withContext(ioDispatcher) {
        val sinceTimestamp = System.currentTimeMillis() - timeWindowMs
        val counts = visitedLogDao.getGenderCountsForClub(clubId, sinceTimestamp).firstOrNull() ?: emptyList()
        var male = 0
        var female = 0
        var diverse = 0

        for (item in counts) {
            val genderEnum = Gender.fromString(item.gender)
            when (genderEnum) {
                Gender.MALE -> male += item.count
                Gender.FEMALE -> female += item.count
                Gender.DIVERSE, Gender.OTHER -> diverse += item.count
                else -> {}
            }
        }

        GenderRatio.calculate(
            maleCount = male,
            femaleCount = female,
            diverseCount = diverse
        )
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
