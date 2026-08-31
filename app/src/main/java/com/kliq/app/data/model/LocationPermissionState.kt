package com.kliq.app.data.model

sealed interface LocationPermissionState {

    data object NotRequested : LocationPermissionState

    data object Granted : LocationPermissionState

    data object Denied : LocationPermissionState

    data object PermanentlyDenied : LocationPermissionState
}
