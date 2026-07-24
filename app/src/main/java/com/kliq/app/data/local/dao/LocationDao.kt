package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for persisting and querying background location history points.
 */
@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long

    @Query("SELECT * FROM user_locations ORDER BY timestampMs DESC LIMIT 1")
    fun getLatestLocation(): Flow<LocationEntity?>

    @Query("SELECT * FROM user_locations ORDER BY timestampMs DESC LIMIT :limit")
    fun getRecentLocations(limit: Int): Flow<List<LocationEntity>>

    @Query("SELECT COUNT(*) FROM user_locations")
    fun getLocationCount(): Flow<Int>

    @Query("DELETE FROM user_locations")
    suspend fun clearAllLocations()
}
