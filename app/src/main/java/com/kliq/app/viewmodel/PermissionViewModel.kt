package com.kliq.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.util.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI State for Location Permission workflow.
 *
 * @param permissionState Current state of location permission (Granted, Denied, PermanentlyDenied, NotRequested).
 * @param showRationaleDialog Whether the Kliq custom Rationale explanation dialog is visible.
 * @param showPermanentlyDeniedDialog Whether the Permanently Denied settings deep-link dialog is visible.
 */
data class PermissionUiState(
    val permissionState: LocationPermissionState = LocationPermissionState.NotRequested,
    val showRationaleDialog: Boolean = false,
    val showPermanentlyDeniedDialog: Boolean = false
)

/**
 * ViewModel managing reactive location permission state evaluation, Rationale dialog display,
 * and deep-link navigation to system settings following strict MVVM pattern.
 */
@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

    /**
     * Checks current system location permission and updates UI state.
     */
    fun checkPermissionStatus(context: Context) {
        val status = permissionManager.checkLocationPermission(context)
        _uiState.update { state ->
            state.copy(permissionState = status)
        }
    }

    /**
     * Triggered when the user clicks a feature requiring location (e.g., Location FAB or Geofencing).
     */
    fun onRequestPermissionClicked(context: Context) {
        val currentStatus = permissionManager.checkLocationPermission(context)
        when (currentStatus) {
            is LocationPermissionState.Granted -> {
                _uiState.update {
                    it.copy(
                        permissionState = LocationPermissionState.Granted,
                        showRationaleDialog = false,
                        showPermanentlyDeniedDialog = false
                    )
                }
            }
            is LocationPermissionState.PermanentlyDenied -> {
                _uiState.update {
                    it.copy(
                        permissionState = LocationPermissionState.PermanentlyDenied,
                        showRationaleDialog = false,
                        showPermanentlyDeniedDialog = true
                    )
                }
            }
            else -> {
                // Show custom Kliq Rationale explanation before native prompt
                _uiState.update {
                    it.copy(
                        showRationaleDialog = true,
                        showPermanentlyDeniedDialog = false
                    )
                }
            }
        }
    }

    /**
     * Callback when system permission result returns from ActivityResultLauncher.
     */
    fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {
        if (isGranted) {
            _uiState.update {
                it.copy(
                    permissionState = LocationPermissionState.Granted,
                    showRationaleDialog = false,
                    showPermanentlyDeniedDialog = false
                )
            }
        } else {
            if (!shouldShowRationale) {
                // User ticked "Don't ask again" or permanently denied
                _uiState.update {
                    it.copy(
                        permissionState = LocationPermissionState.PermanentlyDenied,
                        showRationaleDialog = false,
                        showPermanentlyDeniedDialog = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        permissionState = LocationPermissionState.Denied,
                        showRationaleDialog = false,
                        showPermanentlyDeniedDialog = false
                    )
                }
            }
        }
    }

    /**
     * Dismisses the Rationale dialog without granting permission.
     */
    fun onRationaleDismissed() {
        _uiState.update { it.copy(showRationaleDialog = false) }
    }

    /**
     * Dismisses the Permanently Denied dialog.
     */
    fun onPermanentlyDeniedDismissed() {
        _uiState.update { it.copy(showPermanentlyDeniedDialog = false) }
    }

    /**
     * Deep-links directly to Android system settings for the application.
     */
    fun onOpenSettingsClicked(context: Context) {
        _uiState.update { it.copy(showPermanentlyDeniedDialog = false) }
        permissionManager.openAppSettings(context)
    }
}
