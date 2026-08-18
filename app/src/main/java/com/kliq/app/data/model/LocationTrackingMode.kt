package com.kliq.app.data.model

/**
 * Stepped operational modes for location tracking, balancing geographical precision
 * with battery conservation based on active user context and device state.
 */
enum class LocationTrackingMode {
    /**
     * High frequency and precision (GPS/GNSS hardware).
     * Strictly restricted to active proximity checks, geofence validation, check-in, and QR code verification.
     */
    HIGH_ACCURACY,

    /**
     * Balanced power mode (Cell/Wi-Fi assisted, coarse GPS).
     * Reduced frequency (>60-120s interval, >50-100m displacement) for ambient discovery,
     * party map overviews, and normal foreground navigation.
     */
    BALANCED_AMBIENT,

    /**
     * Lowest power consumption mode.
     * Relying on geofence transitions and passive updates; throttled continuous polling
     * when the device is stationary or running in the background without an active session.
     */
    IDLE_PASSIVE
}
