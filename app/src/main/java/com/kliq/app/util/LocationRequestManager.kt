package com.kliq.app.util

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager responsible for constructing adaptive [LocationRequest] configurations,
 * managing GPS subscription lifecycles, and enforcing power policies across app lifecycle states.
 */
@Singleton
class LocationRequestManager @Inject constructor(
    private val adaptiveController: AdaptiveLocationController
) {

    private var isSubscriptionActive: Boolean = false
    private var isPaused: Boolean = false

    /**
     * Constructs a [LocationRequest] tailored to the provided [LocationTrackingMode] and foreground status.
     *
     * @param mode Target tracking mode (High-Accuracy, Balanced Ambient, or Idle Passive).
     * @param inForeground Whether the UI/Screen is currently in the active foreground.
     * @return Configured [LocationRequest] instance.
     */
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

    /**
     * Initiates a high-accuracy GPS burst for geofence entry validation, check-in, or QR scanning.
     */
    fun initiateVerificationBurst(durationMs: Long = 30_000L): LocationRequest {
        adaptiveController.requestHighAccuracyBurst(durationMs)
        return buildLocationRequest(LocationTrackingMode.HIGH_ACCURACY, inForeground = true)
    }

    /**
     * Notifies the manager that the UI has resumed/foregrounded.
     */
    fun onResume() {
        isPaused = false
        adaptiveController.setForegroundState(true)
    }

    /**
     * Notifies the manager that the UI has paused/backgrounded, throttling continuous subscriptions.
     */
    fun onPause() {
        isPaused = true
        adaptiveController.setForegroundState(false)
    }

    /**
     * Starts tracking subscription.
     */
    fun startSubscription() {
        isSubscriptionActive = true
        isPaused = false
    }

    /**
     * Stops and tears down all active GPS subscriptions on UI destruction (`onCleared`).
     */
    fun stopAllSubscriptions() {
        isSubscriptionActive = false
        isPaused = true
        adaptiveController.cancelBurstSession()
        adaptiveController.setForegroundState(false)
    }

    /**
     * Checks whether active GPS hardware subscription is currently running.
     */
    fun isGpsActivelySubscribed(): Boolean {
        return isSubscriptionActive && !isPaused
    }
}
