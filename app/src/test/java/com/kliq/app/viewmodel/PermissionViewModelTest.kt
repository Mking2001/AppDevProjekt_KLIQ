package com.kliq.app.viewmodel

import android.content.Context
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.util.PermissionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class PermissionViewModelTest {

    private val permissionManager: PermissionManager = mock(PermissionManager::class.java)
    private val context: Context = mock(Context::class.java)
    private lateinit var viewModel: PermissionViewModel

    @Before
    fun setUp() {
        viewModel = PermissionViewModel(permissionManager)
    }

    @Test
    fun testCheckPermissionStatusUpdatesGrantedState() {
        `when`(permissionManager.checkLocationPermission(context)).thenReturn(LocationPermissionState.Granted)

        viewModel.checkPermissionStatus(context)

        assertEquals(LocationPermissionState.Granted, viewModel.uiState.value.permissionState)
        assertFalse(viewModel.uiState.value.showRationaleDialog)
        assertFalse(viewModel.uiState.value.showPermanentlyDeniedDialog)
    }

    @Test
    fun testOnRequestPermissionClickedWhenDeniedShowsRationaleDialog() {
        `when`(permissionManager.checkLocationPermission(context)).thenReturn(LocationPermissionState.Denied)

        viewModel.onRequestPermissionClicked(context)

        assertTrue(viewModel.uiState.value.showRationaleDialog)
        assertFalse(viewModel.uiState.value.showPermanentlyDeniedDialog)
    }

    @Test
    fun testOnRequestPermissionClickedWhenPermanentlyDeniedShowsSettingsDialog() {
        `when`(permissionManager.checkLocationPermission(context)).thenReturn(LocationPermissionState.PermanentlyDenied)

        viewModel.onRequestPermissionClicked(context)

        assertFalse(viewModel.uiState.value.showRationaleDialog)
        assertTrue(viewModel.uiState.value.showPermanentlyDeniedDialog)
    }

    @Test
    fun testOnPermissionResultGrantedUpdatesState() {
        viewModel.onPermissionResult(isGranted = true, shouldShowRationale = false)

        assertEquals(LocationPermissionState.Granted, viewModel.uiState.value.permissionState)
        assertFalse(viewModel.uiState.value.showRationaleDialog)
        assertFalse(viewModel.uiState.value.showPermanentlyDeniedDialog)
    }

    @Test
    fun testOnPermissionResultPermanentlyDeniedShowsPermanentlyDeniedDialog() {
        viewModel.onPermissionResult(isGranted = false, shouldShowRationale = false)

        assertEquals(LocationPermissionState.PermanentlyDenied, viewModel.uiState.value.permissionState)
        assertTrue(viewModel.uiState.value.showPermanentlyDeniedDialog)
        assertFalse(viewModel.uiState.value.showRationaleDialog)
    }

    @Test
    fun testOnOpenSettingsClickedTriggersPermissionManagerDeepLink() {
        viewModel.onOpenSettingsClicked(context)

        assertFalse(viewModel.uiState.value.showPermanentlyDeniedDialog)
        verify(permissionManager).openAppSettings(context)
    }
}
