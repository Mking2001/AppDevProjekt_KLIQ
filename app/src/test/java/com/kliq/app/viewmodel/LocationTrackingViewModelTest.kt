package com.kliq.app.viewmodel

import android.content.Context
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.util.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class LocationTrackingViewModelTest {

    private val context: Context = mock(Context::class.java)
    private val repository: LocationRepository = mock(LocationRepository::class.java)
    private val permissionManager: PermissionManager = mock(PermissionManager::class.java)

    private val isTrackingFlow = MutableStateFlow(false)
    private val locationUpdatesFlow = MutableStateFlow<LocationData?>(null)
    private val locationCountFlow = flowOf(12)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        `when`(repository.isTrackingActive).thenReturn(isTrackingFlow)
        `when`(repository.locationUpdates).thenReturn(locationUpdatesFlow)
        `when`(repository.getLocationCount()).thenReturn(locationCountFlow)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(permState: LocationPermissionState): LocationTrackingViewModel {
        `when`(permissionManager.checkBackgroundLocationPermission(context)).thenReturn(permState)
        return LocationTrackingViewModel(
            context = context,
            locationRepository = repository,
            permissionManager = permissionManager
        )
    }

    @Test
    fun toggleTracking_whenPermissionGrantedAndInactive_startsBackgroundTracking() = runTest {
        val viewModel = createViewModel(LocationPermissionState.Granted)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleTracking()

        verify(repository).startBackgroundTracking()
    }

    @Test
    fun toggleTracking_whenPermissionDenied_doesNotStartTracking() = runTest {
        val viewModel = createViewModel(LocationPermissionState.Denied)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleTracking()

        verify(repository, never()).startBackgroundTracking()
        assertEquals(LocationPermissionState.Denied, viewModel.uiState.value.backgroundPermissionState)
    }

    @Test
    fun toggleTracking_whenPermissionGrantedAndActive_stopsBackgroundTracking() = runTest {
        isTrackingFlow.value = true
        val viewModel = createViewModel(LocationPermissionState.Granted)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleTracking()

        verify(repository).stopBackgroundTracking()
    }

    @Test
    fun clearLocationHistory_invokesRepositoryClear() = runTest {
        val viewModel = createViewModel(LocationPermissionState.Granted)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearLocationHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository).clearLocationHistory()
    }
}
