package com.kliq.app.data.model

import com.google.android.gms.location.Priority

/**
 * Configuration parameters governing adaptive location sampling intervals, displacement filters,
 * priority tier, and power conservation policies.
 *
 * @property intervalMillis Target update interval in milliseconds.
 * @property minUpdateIntervalMillis Fastest acceptable update interval in milliseconds.
 * @property minDistanceDisplacementMeters Minimum displacement required to emit a new fix.
 * @property priority Google Play Services Fused Location priority constant.
 * @property maxUpdateDelayMillis Maximum delay for batched location delivery to reduce CPU wakeups.
 * @property burstTimeoutMillis Maximum duration for temporary high-accuracy sessions before reverting.
 */
data class LocationPowerPolicy(
    val intervalMillis: Long,
    val minUpdateIntervalMillis: Long,
    val minDistanceDisplacementMeters: Float,
    val priority: Int,
    val maxUpdateDelayMillis: Long,
    val burstTimeoutMillis: Long = 30_000L
) {
    companion object {
        /**
         * Power policy for active distance verification, check-in, and QR interactions.
         * Updates every 8s (min 3s) with high GPS accuracy and small displacement threshold (5m).
         */
        val HighAccuracy = LocationPowerPolicy(
            intervalMillis = 8_000L,
            minUpdateIntervalMillis = 3_000L,
            minDistanceDisplacementMeters = 5.0f,
            priority = Priority.PRIORITY_HIGH_ACCURACY,
            maxUpdateDelayMillis = 10_000L,
            burstTimeoutMillis = 30_000L
        )

        /**
         * Power policy for ambient discovery, foreground map overview, and casual exploration.
         * Updates every 60-120s with balanced accuracy (Cell/Wi-Fi) and 50m displacement threshold.
         */
        val BalancedAmbient = LocationPowerPolicy(
            intervalMillis = 60_000L,
            minUpdateIntervalMillis = 30_000L,
            minDistanceDisplacementMeters = 50.0f,
            priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            maxUpdateDelayMillis = 120_000L,
            burstTimeoutMillis = 0L
        )

        /**
         * Power policy for background/idle state and stationary device conservation.
         * Relies on passive fixes and geofence triggers with minimal wakeups (>100m displacement).
         */
        val IdlePassive = LocationPowerPolicy(
            intervalMillis = 300_000L, // 5 minutes
            minUpdateIntervalMillis = 120_000L, // 2 minutes
            minDistanceDisplacementMeters = 100.0f,
            priority = Priority.PRIORITY_PASSIVE,
            maxUpdateDelayMillis = 600_000L, // 10 minutes
            burstTimeoutMillis = 0L
        )

        /**
         * Resolves the default [LocationPowerPolicy] corresponding to the given [LocationTrackingMode].
         */
        fun forMode(mode: LocationTrackingMode): LocationPowerPolicy = when (mode) {
            LocationTrackingMode.HIGH_ACCURACY -> HighAccuracy
            LocationTrackingMode.BALANCED_AMBIENT -> BalancedAmbient
            LocationTrackingMode.IDLE_PASSIVE -> IdlePassive
        }
    }
}
