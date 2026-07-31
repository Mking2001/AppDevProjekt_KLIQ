package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.GenderCountResult
import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.VisitedLogEntity
import com.kliq.app.data.model.Gender
import com.kliq.app.data.model.GenderRatio
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class GenderAggregationUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubDao: ClubDao = mock(ClubDao::class.java)
    private val apiService: KliqApiService = mock(KliqApiService::class.java)
    private val fakeVisitedLogDao = FakeVisitedLogDao()

    private lateinit var repository: ClubRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = ClubRepositoryImpl(clubDao, fakeVisitedLogDao, apiService, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGenderEnumFromString() {
        assertEquals(Gender.MALE, Gender.fromString("male"))
        assertEquals(Gender.FEMALE, Gender.fromString("FEMALE"))
        assertEquals(Gender.DIVERSE, Gender.fromString("diverse"))
        assertEquals(Gender.OTHER, Gender.fromString("other"))
        assertEquals(Gender.UNSPECIFIED, Gender.fromString(null))
        assertEquals(Gender.UNSPECIFIED, Gender.fromString("unknown_val"))
    }

    @Test
    fun testGenderRatioPrivacyThresholdUnderMinimum() {
        val ratio = GenderRatio.calculate(maleCount = 2, femaleCount = 1, diverseCount = 0)
        assertFalse(ratio.hasSufficientData)
        assertEquals(0f, ratio.malePercentage, 0.01f)
        assertEquals(0f, ratio.femalePercentage, 0.01f)
        assertEquals(3, ratio.totalVisitorsCount)
    }

    @Test
    fun testGenderRatioCalculationSufficientData() {
        val ratio = GenderRatio.calculate(maleCount = 5, femaleCount = 3, diverseCount = 2)
        assertTrue(ratio.hasSufficientData)
        assertEquals(10, ratio.totalVisitorsCount)
        assertEquals(50f, ratio.malePercentage, 0.01f)
        assertEquals(30f, ratio.femalePercentage, 0.01f)
        assertEquals(20f, ratio.diversePercentage, 0.01f)

        assertEquals("50%", ratio.formattedMale)
        assertEquals("30%", ratio.formattedFemale)
        assertEquals("20%", ratio.formattedDiverse)
    }

    @Test
    fun testRepositoryGetClubGenderRatioAggregation() = runTest {
        fakeVisitedLogDao.genderCounts = listOf(
            GenderCountResult("MALE", 6),
            GenderCountResult("FEMALE", 4)
        )

        val ratio = repository.getClubGenderRatio("club_berghain").first()

        assertTrue(ratio.hasSufficientData)
        assertEquals(10, ratio.totalVisitorsCount)
        assertEquals(60f, ratio.malePercentage, 0.01f)
        assertEquals(40f, ratio.femalePercentage, 0.01f)
        assertEquals(0f, ratio.diversePercentage, 0.01f)
    }

    private class FakeVisitedLogDao : VisitedLogDao {
        var genderCounts: List<GenderCountResult> = emptyList()

        override suspend fun insertVisitedLog(log: VisitedLogEntity): Long = 1L
        override suspend fun insertVisitedLogs(logs: List<VisitedLogEntity>) {}
        override fun getVisitedLogsForUser(userId: String): Flow<List<VisitedLogEntity>> = flowOf(emptyList())
        override suspend fun getVisitedLogById(id: String): VisitedLogEntity? = null
        override suspend fun deleteVisitedLog(id: String) {}
        override suspend fun clearVisitedLogsForUser(userId: String) {}

        override fun getGenderCountsForClub(clubId: String, sinceTimestamp: Long): Flow<List<GenderCountResult>> {
            return flowOf(genderCounts)
        }

        override fun getVerifiedLogsForClub(clubId: String, sinceTimestamp: Long): Flow<List<VisitedLogEntity>> {
            return flowOf(emptyList())
        }
    }
}
