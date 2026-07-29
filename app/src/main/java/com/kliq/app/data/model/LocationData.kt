package com.kliq.app.data.model

/**
 * Domain model representing a user's location snapshot with metadata.
 *
 * @property latitude Geographic latitude coordinate.
 * @property longitude Geographic longitude coordinate.
 * @property accuracy Accuracy radius of the location fix in meters.
 * @property timestampMs Epoch timestamp in milliseconds when location was acquired.
 * @property speed Current speed of travel in meters/second.
 * @property isMock Indicates whether the location sample originates from a mock provider.
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val timestampMs: Long = System.currentTimeMillis(),
    val speed: Float = 0f,
    val isMock: Boolean = false
)
