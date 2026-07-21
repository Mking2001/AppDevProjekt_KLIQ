package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubRepository @Inject constructor(
    private val clubDao: ClubDao
) {
    fun getAllClubs(): Flow<List<ClubEntity>> = clubDao.getAllClubs()
    
    fun getClubById(clubId: String): Flow<ClubEntity?> = clubDao.getClubById(clubId)

    fun getEventsForClub(clubId: String): Flow<List<EventEntity>> = clubDao.getEventsForClub(clubId)

    suspend fun refreshClubs() {
        // Implement API call and update local DB
    }

    suspend fun toggleFavoriteStatus(clubId: String, isFavorite: Boolean) {
        clubDao.updateFavoriteStatus(clubId, isFavorite)
    }
}
