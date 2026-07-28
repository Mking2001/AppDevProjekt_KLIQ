package com.kliq.app.data.repository

import com.kliq.app.data.model.VisitedLog
import kotlinx.coroutines.flow.Flow

interface VisitedLogRepository {
    fun getVisitedLogsForUser(userId: String): Flow<List<VisitedLog>>
    suspend fun addVisitedLog(
        userId: String,
        clubId: String,
        clubName: String,
        visitedAtTimestamp: Long,
        isVerifiedByGps: Boolean
    ): Result<VisitedLog>
    suspend fun deleteVisitedLog(id: String): Result<Unit>
    suspend fun clearVisitedLogs(userId: String): Result<Unit>
}
