package com.kliq.app.data.repository

import com.kliq.app.data.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsForClub(clubId: String): Flow<List<Event>>
    fun getEventById(eventId: String): Flow<Event?>
    fun searchEventsLocal(query: String): Flow<List<Event>>
    fun getUpcomingEvents(minTimestamp: Long): Flow<List<Event>>
    suspend fun saveEvents(events: List<Event>)
}
