package com.kliq.app.data.model

/**
 * Data representation of a calculated user distance result.
 *
 * @property userId Unique identifier of the target user.
 * @property rawDistanceMeters Physical distance in meters, or `null` if location data is missing.
 * @property isValid Indicates whether distance was successfully computed.
 */
data class UserDistanceResult(
    val userId: String,
    val rawDistanceMeters: Double?,
    val isValid: Boolean = rawDistanceMeters != null
)

/**
 * Lightweight snapshot of a user's location for distance calculations.
 *
 * @property userId Unique identifier of the user.
 * @property latitude Geographic latitude coordinate.
 * @property longitude Geographic longitude coordinate.
 * @property timestampMs Time of last location fix.
 */
data class UserLocationSnapshot(
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val timestampMs: Long = System.currentTimeMillis()
)

/**
 * UI-ready domain item combining user metadata with calculated raw and formatted distances.
 *
 * @property userId Unique identifier of the user.
 * @property username Display name of the user.
 * @property avatarUrl Optional avatar image URL.
 * @property rawDistanceMeters Raw physical distance in meters.
 * @property formattedDistance Formatted human-readable distance string (e.g., "150 m", "1.2 km").
 * @property isOnline Online state indicator.
 */
data class NearbyUserDistance(
    val userId: String,
    val username: String,
    val avatarUrl: String? = null,
    val rawDistanceMeters: Double? = null,
    val formattedDistance: String = "",
    val isOnline: Boolean = true
)
