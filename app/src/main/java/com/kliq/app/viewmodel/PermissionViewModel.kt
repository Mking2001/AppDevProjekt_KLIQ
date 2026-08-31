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

data class PermissionUiState(
    val permissionState: LocationPermissionState = LocationPermissionState.NotRequested,
    val showRationaleDialog: Boolean = false,
    val showPermanentlyDeniedDialog: Boolean = false
)

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

    fun checkPermissionStatus(context: Context) {
        val status = permissionManager.checkLocationPermission(context)
        _uiState.update { state ->
            state.copy(permissionState = status)
        }
    }

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

                _uiState.update {
                    it.copy(
                        showRationaleDialog = true,
                        showPermanentlyDeniedDialog = false
                    )
                }
            }
        }
    }

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

    fun onRationaleDismissed() {
        _uiState.update { it.copy(showRationaleDialog = false) }
    }

    fun onPermanentlyDeniedDismissed() {
        _uiState.update { it.copy(showPermanentlyDeniedDialog = false) }
    }

    fun onOpenSettingsClicked(context: Context) {
        _uiState.update { it.copy(showPermanentlyDeniedDialog = false) }
        permissionManager.openAppSettings(context)
    }
}
