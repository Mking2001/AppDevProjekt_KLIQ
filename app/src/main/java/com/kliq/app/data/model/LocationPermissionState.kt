package com.kliq.app.data.model

/**
 * Sealed interface representing all possible location permission states.
 */
sealed interface LocationPermissionState {
    /** Permission has not been requested yet. */
    data object NotRequested : LocationPermissionState

    /** Permission granted by the user (Fine or Coarse location). */
    data object Granted : LocationPermissionState

    /** Permission denied by the user, but rationale dialog can be shown. */
    data object Denied : LocationPermissionState

    /** Permission permanently denied ("Don't ask again"); requires deep-linking to system settings. */
    data object PermanentlyDenied : LocationPermissionState
}
