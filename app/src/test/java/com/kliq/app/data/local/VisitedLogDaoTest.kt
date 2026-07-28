package com.kliq.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.VisitedLogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class VisitedLogDaoTest {

    private lateinit var db: KliqDatabase
    private lateinit var visitedLogDao: VisitedLogDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        visitedLogDao = db.visitedLogDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertVisitedLogAndQueryByUserId() = runTest {
        val now = System.currentTimeMillis()
        val log1 = VisitedLogEntity(
            id = "log_1",
            userId = "usr_42",
            clubId = "club_1",
            clubName = "Bootshaus Köln",
            visitedAtTimestamp = now,
            isVerifiedByGps = true
        )
        val log2 = VisitedLogEntity(
            id = "log_2",
            userId = "usr_42",
            clubId = "club_2",
            clubName = "Berghain Berlin",
            visitedAtTimestamp = now - 3600_000L,
            isVerifiedByGps = false
        )

        visitedLogDao.insertVisitedLogs(listOf(log1, log2))

        val logs = visitedLogDao.getVisitedLogsForUser("usr_42").first()
        assertEquals(2, logs.size)
        assertEquals("log_1", logs[0].id)
        assertTrue(logs[0].isVerifiedByGps)
        assertEquals("Bootshaus Köln", logs[0].clubName)
    }

    @Test
    fun deleteVisitedLog_removesEntry() = runTest {
        val log = VisitedLogEntity(
            id = "log_delete",
            userId = "usr_1",
            clubId = "club_x",
            clubName = "Club X",
            visitedAtTimestamp = System.currentTimeMillis(),
            isVerifiedByGps = true
        )
        visitedLogDao.insertVisitedLog(log)

        var fetched = visitedLogDao.getVisitedLogById("log_delete")
        assertNotNull(fetched)

        visitedLogDao.deleteVisitedLog("log_delete")
        fetched = visitedLogDao.getVisitedLogById("log_delete")
        assertNull(fetched)
    }

    @Test
    fun clearVisitedLogsForUser_removesAllEntriesForUser() = runTest {
        val log1 = VisitedLogEntity(
            id = "log_u1_1",
            userId = "usr_clear",
            clubId = "club_a",
            clubName = "Club A",
            visitedAtTimestamp = 1000L,
            isVerifiedByGps = true
        )
        val log2 = VisitedLogEntity(
            id = "log_u1_2",
            userId = "usr_clear",
            clubId = "club_b",
            clubName = "Club B",
            visitedAtTimestamp = 2000L,
            isVerifiedByGps = false
        )
        val logOther = VisitedLogEntity(
            id = "log_other",
            userId = "usr_keep",
            clubId = "club_c",
            clubName = "Club C",
            visitedAtTimestamp = 3000L,
            isVerifiedByGps = true
        )

        visitedLogDao.insertVisitedLogs(listOf(log1, log2, logOther))

        visitedLogDao.clearVisitedLogsForUser("usr_clear")

        val userLogs = visitedLogDao.getVisitedLogsForUser("usr_clear").first()
        assertTrue(userLogs.isEmpty())

        val otherLogs = visitedLogDao.getVisitedLogsForUser("usr_keep").first()
        assertEquals(1, otherLogs.size)
    }
}
