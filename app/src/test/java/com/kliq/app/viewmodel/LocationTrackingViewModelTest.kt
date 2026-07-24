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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    private lateinit var viewModel: LocationTrackingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        `when`(repository.isTrackingActive).thenReturn(isTrackingFlow)
        `when`(repository.locationUpdates).thenReturn(locationUpdatesFlow)
        `when`(repository.getLocationCount()).thenReturn(locationCountFlow)
        `when`(permissionManager.checkBackgroundLocationPermission(context)).thenReturn(LocationPermissionState.Granted)

        viewModel = LocationTrackingViewModel(
            context = context,
            locationRepository = repository,
            permissionManager = permissionManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun toggleTracking_whenPermissionGrantedAndInactive_startsBackgroundTracking() = runTest {
        viewModel.toggleTracking()
        verify(repository).startBackgroundTracking()
    }

    @Test
    fun toggleTracking_whenPermissionDenied_doesNotStartTracking() = runTest {
        `when`(permissionManager.checkBackgroundLocationPermission(context)).thenReturn(LocationPermissionState.Denied)

        viewModel.toggleTracking()

        verify(repository, never()).startBackgroundTracking()
        assertEquals(LocationPermissionState.Denied, viewModel.uiState.value.backgroundPermissionState)
    }

    @Test
    fun toggleTracking_whenPermissionGrantedAndActive_stopsBackgroundTracking() = runTest {
        isTrackingFlow.value = true
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleTracking()

        verify(repository).stopBackgroundTracking()
    }

    @Test
    fun clearLocationHistory_invokesRepositoryClear() = runTest {
        viewModel.clearLocationHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository).clearLocationHistory()
    }
}
