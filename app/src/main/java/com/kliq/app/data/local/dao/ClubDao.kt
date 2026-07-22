package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity

@Dao
interface ClubDao {
    @Query("SELECT * FROM clubs ORDER BY name ASC")
    fun getAllClubs(): Flow<List<ClubEntity>>

    @Query("SELECT * FROM clubs WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteClubs(): Flow<List<ClubEntity>>

    @Query("SELECT * FROM clubs WHERE id = :clubId")
    fun getClubById(clubId: String): Flow<ClubEntity?>

    @Query("SELECT * FROM clubs WHERE name LIKE '%' || :query || '%' OR externalSearchTags LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchClubs(query: String): Flow<List<ClubEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubs(clubs: List<ClubEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClub(club: ClubEntity)

    @Query("UPDATE clubs SET isFavorite = :isFavorite WHERE id = :clubId")
    suspend fun updateFavoriteStatus(clubId: String, isFavorite: Boolean)

    @Query("SELECT * FROM events WHERE clubId = :clubId")
    fun getEventsForClub(clubId: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)
}
