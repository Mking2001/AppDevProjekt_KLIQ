package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.GenderCountResult
import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.VisitedLogEntity
import com.kliq.app.data.model.Gender
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.viewmodel.ClubAnalyticsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Integration Test for Chapter 7.1 Gender Ratio Aggregation Logic and ClubAnalyticsViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GenderAggregationIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubDao: ClubDao = mock(ClubDao::class.java)
    private val apiService: KliqApiService = mock(KliqApiService::class.java)
    private val fakeVisitedLogDao = FakeVisitedLogDao()

    private lateinit var repository: ClubRepositoryImpl
    private lateinit var viewModel: ClubAnalyticsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = ClubRepositoryImpl(clubDao, fakeVisitedLogDao, apiService, testDispatcher)
        viewModel = ClubAnalyticsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test-Szenario: 20 verifizierte Check-ins an Club-ID "club_berghain_71"
     *   - 10x Weiblich
     *   - 8x Männlich
     *   - 2x Divers
     * Verifiziert: Korrekte Prozentwerte (50% W / 40% M / 10% D) im ClubAnalyticsViewModel.
     */
    @Test
    fun testSimulated20CheckInsGenderAggregation_returnsCorrectPercentages() = runTest {
        val targetClubId = "club_berghain_71"
        fakeVisitedLogDao.genderCounts = listOf(
            GenderCountResult(gender = Gender.FEMALE.name, count = 10),
            GenderCountResult(gender = Gender.MALE.name, count = 8),
            GenderCountResult(gender = Gender.DIVERSE.name, count = 2)
        )

        viewModel.observeClubAnalytics(targetClubId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(targetClubId, state.clubId)
        assertEquals(20, state.totalLiveVisitors)
        assertTrue(state.genderRatio.hasSufficientData)

        // Verifikation der Prozentwerte (50% W, 40% M, 10% D)
        assertEquals(50f, state.genderRatio.femalePercentage, 0.01f)
        assertEquals(40f, state.genderRatio.malePercentage, 0.01f)
        assertEquals(10f, state.genderRatio.diversePercentage, 0.01f)

        // Verifikation der UI-Bar Segmente für Kliq High-Contrast Lila Style
        assertEquals(3, state.segments.size)

        val femaleSegment = state.segments.find { it.gender == Gender.FEMALE }
        assertNotNull(femaleSegment)
        assertEquals("50%", femaleSegment?.formattedPercentage)
        assertEquals("#D946EF", femaleSegment?.colorHex)

        val maleSegment = state.segments.find { it.gender == Gender.MALE }
        assertNotNull(maleSegment)
        assertEquals("40%", maleSegment?.formattedPercentage)
        assertEquals("#7C3AED", maleSegment?.colorHex)

        val diverseSegment = state.segments.find { it.gender == Gender.DIVERSE }
        assertNotNull(diverseSegment)
        assertEquals("10%", diverseSegment?.formattedPercentage)
        assertEquals("#14B8A6", diverseSegment?.colorHex)
    }

    /**
     * Edge Case Test: 0 Check-ins (Division by Zero Abfangung & Anonymisierungs-Schwellenwert)
     */
    @Test
    fun testZeroCheckIns_preventsDivisionByZero_andMasksData() = runTest {
        val targetClubId = "club_empty"
        fakeVisitedLogDao.genderCounts = emptyList()

        viewModel.observeClubAnalytics(targetClubId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(0, state.totalLiveVisitors)
        assertFalse(state.genderRatio.hasSufficientData)
        assertEquals(0f, state.genderRatio.femalePercentage, 0.0f)
        assertEquals(0f, state.genderRatio.malePercentage, 0.0f)
        assertEquals(0f, state.genderRatio.diversePercentage, 0.0f)
        assertTrue(state.segments.isEmpty())
    }

    /**
     * Edge Case Test: Unter Minimum-Datenschutz-Schwellenwert (< 5 Check-ins)
     */
    @Test
    fun testUnderPrivacyThreshold_masksIndividualData() = runTest {
        val targetClubId = "club_low_visitors"
        fakeVisitedLogDao.genderCounts = listOf(
            GenderCountResult(gender = Gender.FEMALE.name, count = 2),
            GenderCountResult(gender = Gender.MALE.name, count = 1)
        )

        viewModel.observeClubAnalytics(targetClubId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(3, state.totalLiveVisitors)
        assertFalse(state.genderRatio.hasSufficientData)
        assertTrue(state.segments.isEmpty())
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
