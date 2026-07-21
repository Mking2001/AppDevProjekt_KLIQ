package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClubDao {
    @Query("SELECT * FROM clubs")
    fun getAllClubs(): Flow<List<ClubEntity>>

    @Query("SELECT * FROM clubs WHERE id = :clubId")
    fun getClubById(clubId: String): Flow<ClubEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubs(clubs: List<ClubEntity>)

    @Query("SELECT * FROM events WHERE clubId = :clubId")
    fun getEventsForClub(clubId: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)
    
    @Query("UPDATE clubs SET isFavorite = :isFavorite WHERE id = :clubId")
    suspend fun updateFavoriteStatus(clubId: String, isFavorite: Boolean)
}
