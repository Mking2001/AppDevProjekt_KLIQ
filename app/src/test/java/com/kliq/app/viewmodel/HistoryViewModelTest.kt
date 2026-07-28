package com.kliq.app.viewmodel

import com.kliq.app.data.model.VisitedLog
import com.kliq.app.data.repository.VisitedLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: VisitedLogRepository
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock(VisitedLogRepository::class.java)
        viewModel = HistoryViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadHistory_whenEmpty_emitsEmptyState() = runTest {
        `when`(mockRepository.getVisitedLogsForUser("usr_test")).thenReturn(flowOf(emptyList()))

        viewModel.loadHistory("usr_test")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HistoryUiState.Empty)
    }

    @Test
    fun loadHistory_whenPopulated_emitsSuccessStateWithCounts() = runTest {
        val now = System.currentTimeMillis()
        val logs = listOf(
            VisitedLog("1", "usr_test", "c1", "Club 1", now, isVerifiedByGps = true),
            VisitedLog("2", "usr_test", "c2", "Club 2", now - 1000L, isVerifiedByGps = false)
        )
        `when`(mockRepository.getVisitedLogsForUser("usr_test")).thenReturn(flowOf(logs))

        viewModel.loadHistory("usr_test")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HistoryUiState.Success)
        val successState = state as HistoryUiState.Success
        assertEquals(2, successState.totalVisitsCount)
        assertEquals(1, successState.verifiedVisitsCount)
        assertEquals(2, successState.logs.size)
    }

    @Test
    fun loadHistory_whenErrorOccurs_emitsErrorState() = runTest {
        val errorFlow: Flow<List<VisitedLog>> = flow {
            throw RuntimeException("Netzwerkfehler")
        }
        `when`(mockRepository.getVisitedLogsForUser("usr_test")).thenReturn(errorFlow)

        viewModel.loadHistory("usr_test")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HistoryUiState.Error)
        assertEquals("Netzwerkfehler", (state as HistoryUiState.Error).message)
    }

    @Test
    fun addVisitedLog_invokesRepository() = runTest {
        `when`(mockRepository.addVisitedLog("usr_1", "club_1", "Pacha", 1000L, true))
            .thenReturn(Result.success(VisitedLog("log_1", "usr_1", "club_1", "Pacha", 1000L, true)))

        viewModel.addVisitedLog("usr_1", "club_1", "Pacha", 1000L, true)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(mockRepository).addVisitedLog("usr_1", "club_1", "Pacha", 1000L, true)
    }

    @Test
    fun deleteVisitedLog_invokesRepository() = runTest {
        `when`(mockRepository.deleteVisitedLog("log_123")).thenReturn(Result.success(Unit))

        viewModel.deleteVisitedLog("log_123", "usr_1")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(mockRepository).deleteVisitedLog("log_123")
    }

    @Test
    fun clearHistory_invokesRepositoryAndEmitsEmptyState() = runTest {
        `when`(mockRepository.clearVisitedLogs("usr_1")).thenReturn(Result.success(Unit))

        viewModel.clearHistory("usr_1")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(mockRepository).clearVisitedLogs("usr_1")
        assertTrue(viewModel.uiState.value is HistoryUiState.Empty)
    }
}
