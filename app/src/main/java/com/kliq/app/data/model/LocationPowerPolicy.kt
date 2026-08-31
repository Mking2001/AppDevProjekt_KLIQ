package com.kliq.app.data.model

import com.google.android.gms.location.Priority

data class LocationPowerPolicy(
    val intervalMillis: Long,
    val minUpdateIntervalMillis: Long,
    val minDistanceDisplacementMeters: Float,
    val priority: Int,
    val maxUpdateDelayMillis: Long,
    val burstTimeoutMillis: Long = 30_000L
) {
    companion object {

        val HighAccuracy = LocationPowerPolicy(
            intervalMillis = 8_000L,
            minUpdateIntervalMillis = 3_000L,
            minDistanceDisplacementMeters = 5.0f,
            priority = Priority.PRIORITY_HIGH_ACCURACY,
            maxUpdateDelayMillis = 10_000L,
            burstTimeoutMillis = 30_000L
        )

        val BalancedAmbient = LocationPowerPolicy(
            intervalMillis = 60_000L,
            minUpdateIntervalMillis = 30_000L,
            minDistanceDisplacementMeters = 50.0f,
            priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            maxUpdateDelayMillis = 120_000L,
            burstTimeoutMillis = 0L
        )

        val IdlePassive = LocationPowerPolicy(
            intervalMillis = 300_000L,
            minUpdateIntervalMillis = 120_000L,
            minDistanceDisplacementMeters = 100.0f,
            priority = Priority.PRIORITY_PASSIVE,
            maxUpdateDelayMillis = 600_000L,
            burstTimeoutMillis = 0L
        )

        fun forMode(mode: LocationTrackingMode): LocationPowerPolicy = when (mode) {
            LocationTrackingMode.HIGH_ACCURACY -> HighAccuracy
            LocationTrackingMode.BALANCED_AMBIENT -> BalancedAmbient
            LocationTrackingMode.IDLE_PASSIVE -> IdlePassive
        }
    }
}
