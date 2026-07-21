package com.kliq.app.data.repository

import com.kliq.app.data.local.RoomConverters
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toDomain
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
    private val apiService: KliqApiService
) : ClubAndEventRepository {

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
            val remoteClubs = apiService.getClubs()
            val remoteEvents = apiService.getEvents()

            val clubEntities = remoteClubs.map { it.toEntity() }
            val eventEntities = remoteEvents.map { it.toEntity() }

            clubDao.insertClubs(clubEntities)
            eventDao.insertEvents(eventEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleClubFavorite(clubId: String, currentFavoriteState: Boolean) {
        clubDao.updateFavoriteStatus(clubId, !currentFavoriteState)
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

    private fun Event.toEntity(): EventEntity {
        val offersJson = roomConverters.fromSpecialOffersList(specialOffers)
        return EventEntity(
            id = id,
            clubId = clubId,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            price = price,
            specialOffersJson = offersJson,
            searchKeywords = searchKeywords,
            imageUrl = imageUrl
        )
    }
}
