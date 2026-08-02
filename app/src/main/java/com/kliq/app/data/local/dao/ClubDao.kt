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

    @Query("SELECT * FROM clubs WHERE isPromoted = 1 ORDER BY name ASC")
    fun getPromotedClubs(): Flow<List<ClubEntity>>

    @Query("SELECT * FROM clubs WHERE city = :city ORDER BY name ASC")
    fun getClubsByCity(city: String): Flow<List<ClubEntity>>

    @Query("SELECT * FROM clubs WHERE id = :clubId")
    fun getClubById(clubId: String): Flow<ClubEntity?>

    @Query("SELECT * FROM clubs WHERE name LIKE '%' || :query || '%' OR externalSearchTags LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchClubs(query: String): Flow<List<ClubEntity>>

    @Query("""
        SELECT * FROM clubs 
        WHERE (:query = '' OR LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(externalSearchTags) LIKE '%' || LOWER(:query) || '%' OR LOWER(category) LIKE '%' || LOWER(:query) || '%' OR LOWER(region) LIKE '%' || LOWER(:query) || '%' OR LOWER(city) LIKE '%' || LOWER(:query) || '%')
        AND (:region = '' OR LOWER(region) LIKE '%' || LOWER(:region) || '%' OR LOWER(city) LIKE '%' || LOWER(:region) || '%')
        AND (:category = '' OR LOWER(category) LIKE '%' || LOWER(:category) || '%' OR LOWER(externalSearchTags) LIKE '%' || LOWER(:category) || '%')
        ORDER BY name ASC
    """)
    fun searchClubsFiltered(query: String, region: String = "", category: String = ""): Flow<List<ClubEntity>>

    @Query("""
        SELECT DISTINCT city FROM clubs WHERE city IS NOT NULL AND city != '' AND LOWER(city) LIKE '%' || LOWER(:query) || '%'
        UNION
        SELECT DISTINCT region FROM clubs WHERE region IS NOT NULL AND region != '' AND LOWER(region) LIKE '%' || LOWER(:query) || '%'
    """)
    fun searchDistinctRegionsAndCities(query: String): Flow<List<String>>

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
