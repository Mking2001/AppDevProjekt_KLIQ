package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.VisitedLogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class VisitedLogRepositoryTest {

    private lateinit var mockDao: VisitedLogDao
    private lateinit var repository: VisitedLogRepositoryImpl

    @Before
    fun setUp() {
        mockDao = mock(VisitedLogDao::class.java)
        repository = VisitedLogRepositoryImpl(mockDao)
    }

    @Test
    fun getVisitedLogsForUser_mapsEntitiesToDomainModels() = runTest {
        val now = System.currentTimeMillis()
        val entities = listOf(
            VisitedLogEntity(
                id = "id_1",
                userId = "usr_10",
                clubId = "club_1",
                clubName = "Pacha München",
                visitedAtTimestamp = now,
                isVerifiedByGps = true
            )
        )
        `when`(mockDao.getVisitedLogsForUser("usr_10")).thenReturn(flowOf(entities))

        val domainLogs = repository.getVisitedLogsForUser("usr_10").first()

        assertEquals(1, domainLogs.size)
        assertEquals("id_1", domainLogs[0].id)
        assertEquals("Pacha München", domainLogs[0].clubName)
        assertTrue(domainLogs[0].isVerifiedByGps)
    }

    @Test
    fun addVisitedLog_insertsLogAndReturnsSuccess() = runTest {
        val result = repository.addVisitedLog(
            userId = "usr_10",
            clubId = "club_2",
            clubName = "Neuraum",
            visitedAtTimestamp = 5000L,
            isVerifiedByGps = false
        )

        assertTrue(result.isSuccess)
        val addedLog = result.getOrThrow()
        assertEquals("usr_10", addedLog.userId)
        assertEquals("Neuraum", addedLog.clubName)
        assertEquals(false, addedLog.isVerifiedByGps)
    }

    @Test
    fun deleteVisitedLog_invokesDaoDelete() = runTest {
        val result = repository.deleteVisitedLog("log_123")
        assertTrue(result.isSuccess)
        verify(mockDao).deleteVisitedLog("log_123")
    }

    @Test
    fun clearVisitedLogs_invokesDaoClear() = runTest {
        val result = repository.clearVisitedLogs("usr_99")
        assertTrue(result.isSuccess)
        verify(mockDao).clearVisitedLogsForUser("usr_99")
    }
}
