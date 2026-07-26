package com.kliq.app.domain.usecase

import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.UserDistanceResult
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Domain UseCase executing high-precision physical distance calculations between geographic coordinates
 * and user locations using the Haversine formula.
 *
 * Enforces strict separation of calculation logic from UI presentation and handles all edge cases
 * such as missing GPS fixes, identical positions, and invalid coordinate ranges.
 */
class CalculateUserDistanceUseCase {

    companion object {
        /** Mean radius of the Earth in meters. */
        const val EARTH_RADIUS_METERS = 6371000.0

        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0
    }

    /**
     * Calculates the physical distance in meters between two sets of latitude/longitude coordinates.
     *
     * @param startLat Latitude of starting location.
     * @param startLng Longitude of starting location.
     * @param endLat Latitude of destination location.
     * @param endLng Longitude of destination location.
     * @return Distance in meters as [Double], or `null` if any coordinate is invalid/NaN.
     */
    fun calculateDistanceMeters(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Double? {
        if (!isValidCoordinate(startLat, startLng) || !isValidCoordinate(endLat, endLng)) {
            return null
        }

        if (startLat == endLat && startLng == endLng) {
            return 0.0
        }

        val dLat = Math.toRadians(endLat - startLat)
        val dLng = Math.toRadians(endLng - startLng)

        val lat1Rad = Math.toRadians(startLat)
        val lat2Rad = Math.toRadians(endLat)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLng / 2) * sin(dLng / 2) * cos(lat1Rad) * cos(lat2Rad)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c
    }

    /**
     * Calculates the physical distance in meters between two [LocationData] snapshots.
     *
     * @param startLocation Location snapshot of current user.
     * @param endLocation Location snapshot of target user.
     * @return Distance in meters, or `null` if any snapshot is null or invalid.
     */
    fun calculateDistanceMeters(
        startLocation: LocationData?,
        endLocation: LocationData?
    ): Double? {
        if (startLocation == null || endLocation == null) {
            return null
        }
        return calculateDistanceMeters(
            startLat = startLocation.latitude,
            startLng = startLocation.longitude,
            endLat = endLocation.latitude,
            endLng = endLocation.longitude
        )
    }

    /**
     * Calculates user distance and packages the raw distance alongside metadata into a [UserDistanceResult].
     *
     * @param targetUserId ID of the target user.
     * @param currentUserLat Latitude of current user.
     * @param currentUserLng Longitude of current user.
     * @param targetUserLat Latitude of target user.
     * @param targetUserLng Longitude of target user.
     * @return [UserDistanceResult] containing raw distance and status flag.
     */
    fun calculateUserDistance(
        targetUserId: String,
        currentUserLat: Double?,
        currentUserLng: Double?,
        targetUserLat: Double?,
        targetUserLng: Double?
    ): UserDistanceResult {
        if (currentUserLat == null || currentUserLng == null || targetUserLat == null || targetUserLng == null) {
            return UserDistanceResult(
                userId = targetUserId,
                rawDistanceMeters = null,
                isValid = false
            )
        }

        val dist = calculateDistanceMeters(
            startLat = currentUserLat,
            startLng = currentUserLng,
            endLat = targetUserLat,
            endLng = targetUserLng
        )

        return UserDistanceResult(
            userId = targetUserId,
            rawDistanceMeters = dist,
            isValid = dist != null
        )
    }

    private fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        if (latitude.isNaN() || longitude.isNaN() || latitude.isInfinite() || longitude.isInfinite()) {
            return false
        }
        return latitude in MIN_LATITUDE..MAX_LATITUDE && longitude in MIN_LONGITUDE..MAX_LONGITUDE
    }
}
