package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.VisitedLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing historical club visit logs.
 */
@Dao
interface VisitedLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedLog(log: VisitedLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedLogs(logs: List<VisitedLogEntity>)

    @Query("SELECT * FROM visited_logs WHERE userId = :userId ORDER BY visitedAtTimestamp DESC")
    fun getVisitedLogsForUser(userId: String): Flow<List<VisitedLogEntity>>

    @Query("SELECT * FROM visited_logs WHERE id = :id")
    suspend fun getVisitedLogById(id: String): VisitedLogEntity?

    @Query("DELETE FROM visited_logs WHERE id = :id")
    suspend fun deleteVisitedLog(id: String)

    @Query("DELETE FROM visited_logs WHERE userId = :userId")
    suspend fun clearVisitedLogsForUser(userId: String)
}
