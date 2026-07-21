package com.kliq.app.data.repository

import com.google.gson.Gson
import com.kliq.app.data.local.RoomConverters
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.SpecialOffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {

    private val gson = Gson()
    private val roomConverters = RoomConverters()

    override fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEventsForClub(clubId: String): Flow<List<Event>> {
        return eventDao.getEventsByClubId(clubId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEventById(eventId: String): Flow<Event?> {
        return eventDao.getEventById(eventId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun searchEventsLocal(query: String): Flow<List<Event>> {
        return eventDao.searchEvents(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUpcomingEvents(minTimestamp: Long): Flow<List<Event>> {
        return eventDao.getUpcomingEvents(minTimestamp).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveEvents(events: List<Event>) {
        val entities = events.map { it.toEntity() }
        eventDao.insertEvents(entities)
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
