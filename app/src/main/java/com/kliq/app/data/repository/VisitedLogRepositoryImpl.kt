package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.VisitedLogEntity
import com.kliq.app.data.local.entities.toDomain
import com.kliq.app.data.model.VisitedLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitedLogRepositoryImpl @Inject constructor(
    private val visitedLogDao: VisitedLogDao
) : VisitedLogRepository {

    override fun getVisitedLogsForUser(userId: String): Flow<List<VisitedLog>> {
        return visitedLogDao.getVisitedLogsForUser(userId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun addVisitedLog(
        userId: String,
        clubId: String,
        clubName: String,
        visitedAtTimestamp: Long,
        isVerifiedByGps: Boolean
    ): Result<VisitedLog> = withContext(Dispatchers.IO) {
        try {
            val entity = VisitedLogEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                clubId = clubId,
                clubName = clubName,
                visitedAtTimestamp = visitedAtTimestamp,
                isVerifiedByGps = isVerifiedByGps
            )
            visitedLogDao.insertVisitedLog(entity)
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteVisitedLog(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            visitedLogDao.deleteVisitedLog(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearVisitedLogs(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            visitedLogDao.clearVisitedLogsForUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
