package com.kliq.app.data.util

import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.VisitedLogEntity
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock Seeder utility for populating local Room database with sample club visit entries.
 */
@Singleton
class VisitedLogMockSeeder @Inject constructor(
    private val visitedLogDao: VisitedLogDao
) {
    suspend fun seedSampleVisitedLogs(userId: String = "current_user"): List<VisitedLogEntity> {
        val now = System.currentTimeMillis()
        val logs = listOf(
            VisitedLogEntity(
                id = "mock_log_1",
                userId = userId,
                clubId = "club_bootshaus",
                clubName = "Bootshaus Köln",
                visitedAtTimestamp = now,
                isVerifiedByGps = true
            ),
            VisitedLogEntity(
                id = "mock_log_2",
                userId = userId,
                clubId = "club_pacha",
                clubName = "Pacha München",
                visitedAtTimestamp = now - TimeUnit.DAYS.toMillis(2),
                isVerifiedByGps = false
            ),
            VisitedLogEntity(
                id = "mock_log_3",
                userId = userId,
                clubId = "club_berghain",
                clubName = "Berghain Berlin",
                visitedAtTimestamp = now - TimeUnit.DAYS.toMillis(7),
                isVerifiedByGps = true
            )
        )
        visitedLogDao.insertVisitedLogs(logs)
        return logs
    }

    suspend fun clearSeededLogs(userId: String = "current_user") {
        visitedLogDao.clearVisitedLogsForUser(userId)
    }
}
