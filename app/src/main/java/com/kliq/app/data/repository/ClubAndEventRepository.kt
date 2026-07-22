package com.kliq.app.data.repository

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import kotlinx.coroutines.flow.Flow

interface ClubAndEventRepository {
    fun getClubs(): Flow<List<Club>>
    fun getFavoriteClubs(): Flow<List<Club>>
    fun getEvents(): Flow<List<Event>>
    fun getEventsForClub(clubId: String): Flow<List<Event>>
    fun getClubById(clubId: String): Flow<Club?>
    fun getEventById(eventId: String): Flow<Event?>
    fun searchClubsAndEventsLocal(query: String): Flow<Pair<List<Club>, List<Event>>>
    suspend fun syncClubsAndEventsFromRemote(): Result<Unit>
    suspend fun toggleClubFavorite(clubId: String, currentFavoriteState: Boolean)
}
