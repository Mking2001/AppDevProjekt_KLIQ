package com.kliq.app.util

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRequestManager @Inject constructor(
    private val adaptiveController: AdaptiveLocationController
) {

    private var isSubscriptionActive: Boolean = false
    private var isPaused: Boolean = false

    fun buildLocationRequest(
        mode: LocationTrackingMode = adaptiveController.effectiveMode.value,
        inForeground: Boolean = adaptiveController.isForeground.value
    ): LocationRequest {
        val policy = when {
            !inForeground -> LocationPowerPolicy.IdlePassive
            else -> LocationPowerPolicy.forMode(mode)
        }

        return LocationRequest.Builder(policy.priority, policy.intervalMillis).apply {
            setMinUpdateIntervalMillis(policy.minUpdateIntervalMillis)
            setMinUpdateDistanceMeters(policy.minDistanceDisplacementMeters)
            setMaxUpdateDelayMillis(policy.maxUpdateDelayMillis)
        }.build()
    }

    fun initiateVerificationBurst(durationMs: Long = 30_000L): LocationRequest {
        adaptiveController.requestHighAccuracyBurst(durationMs)
        return buildLocationRequest(LocationTrackingMode.HIGH_ACCURACY, inForeground = true)
    }

    fun onResume() {
        isPaused = false
        adaptiveController.setForegroundState(true)
    }

    fun onPause() {
        isPaused = true
        adaptiveController.setForegroundState(false)
    }

    fun startSubscription() {
        isSubscriptionActive = true
        isPaused = false
    }

    fun stopAllSubscriptions() {
        isSubscriptionActive = false
        isPaused = true
        adaptiveController.cancelBurstSession()
        adaptiveController.setForegroundState(false)
    }

    fun isGpsActivelySubscribed(): Boolean {
        return isSubscriptionActive && !isPaused
    }
}
