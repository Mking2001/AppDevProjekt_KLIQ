package com.kliq.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kliq.app.data.local.RoomConverters
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubAndEventRepositoryImpl @Inject constructor(
    private val clubDao: ClubDao,
    private val eventDao: EventDao,
    private val apiService: KliqApiService? = null
) : ClubAndEventRepository {

    private val gson = Gson()
    private val roomConverters = RoomConverters()

    override fun getClubs(): Flow<List<Club>> {
        return clubDao.getAllClubs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteClubs(): Flow<List<Club>> {
        return clubDao.getFavoriteClubs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEventsForClub(clubId: String): Flow<List<Event>> {
        return eventDao.getEventsByClubId(clubId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getClubById(clubId: String): Flow<Club?> {
        return clubDao.getClubById(clubId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getEventById(eventId: String): Flow<Event?> {
        return eventDao.getEventById(eventId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun searchClubsAndEventsLocal(query: String): Flow<Pair<List<Club>, List<Event>>> {
        return combine(
            clubDao.searchClubs(query),
            eventDao.searchEvents(query)
        ) { clubEntities, eventEntities ->
            Pair(
                clubEntities.map { it.toDomain() },
                eventEntities.map { it.toDomain() }
            )
        }
    }

    override suspend fun syncClubsAndEventsFromRemote(): Result<Unit> {
        return try {
            if (apiService != null) {
                val remoteResponse = apiService.searchExternalClubsAndEvents("")
                val clubEntities = remoteResponse.clubs.map { it.toEntity() }
                val eventEntities = remoteResponse.events.map { it.toEntity() }
                clubDao.insertClubs(clubEntities)
                eventDao.insertEvents(eventEntities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleClubFavorite(clubId: String, currentFavoriteState: Boolean) {
        clubDao.updateFavoriteStatus(clubId, !currentFavoriteState)
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

    private fun EventEntity.toDomain(): Event {
        val offers = roomConverters.toSpecialOffersList(specialOffersJson)
        return Event(
            id = id,
            clubId = clubId,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            price = price,
            specialOffers = offers,
            searchKeywords = searchKeywords,
            imageUrl = imageUrl
        )
    }
}
