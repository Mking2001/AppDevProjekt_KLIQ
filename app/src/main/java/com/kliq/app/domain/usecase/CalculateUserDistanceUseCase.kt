package com.kliq.app.domain.usecase

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.UserDistanceResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Domain UseCase executing high-precision physical distance calculations between geographic coordinates
 * and user locations using the Haversine formula, as well as geofence radius verifications.
 *
 * Enforces strict separation of calculation logic from UI presentation and handles all edge cases
 * such as missing GPS fixes, identical positions, invalid coordinate ranges, and tolerance thresholds.
 */
@Singleton
class CalculateUserDistanceUseCase @Inject constructor() {

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
     * @return Distance in meters as [Double], or `null` if any coordinate is invalid or non-finite.
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
     * Calculates the physical distance in meters between a [LocationData] and a [GpsLocation].
     *
     * @param startLocation Location snapshot of user.
     * @param endLocation Location of target point or club.
     * @return Distance in meters, or `null` if any snapshot is null or invalid.
     */
    fun calculateDistanceMeters(
        startLocation: LocationData?,
        endLocation: GpsLocation?
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
     * Checks whether a user coordinate is within the designated geofence radius of a club,
     * with an optional tolerance margin to account for GPS jitter and measurement inaccuracy.
     *
     * @param userLat User latitude.
     * @param userLng User longitude.
     * @param clubLat Club latitude.
     * @param clubLng Club longitude.
     * @param radiusMeters Club geofence radius in meters.
     * @param toleranceMeters Optional GPS jitter tolerance in meters (default is 0.0).
     * @return `true` if the calculated distance is within radius + tolerance, `false` otherwise.
     */
    fun isWithinClubRadius(
        userLat: Double?,
        userLng: Double?,
        clubLat: Double?,
        clubLng: Double?,
        radiusMeters: Double,
        toleranceMeters: Double = 0.0
    ): Boolean {
        if (userLat == null || userLng == null || clubLat == null || clubLng == null) {
            return false
        }
        if (radiusMeters < 0.0 || toleranceMeters < 0.0) {
            return false
        }

        val distance = calculateDistanceMeters(
            startLat = userLat,
            startLng = userLng,
            endLat = clubLat,
            endLng = clubLng
        ) ?: return false

        val maxAllowedDistance = radiusMeters + toleranceMeters
        return distance <= maxAllowedDistance
    }

    /**
     * Checks whether a user's [LocationData] is within the geofence radius of a [Club].
     *
     * @param userLocation User's current location snapshot.
     * @param club Target club entity containing coordinates and geofence radius.
     * @param toleranceMeters Optional GPS jitter tolerance in meters.
     * @return `true` if within geofence radius, `false` otherwise.
     */
    fun isWithinClubRadius(
        userLocation: LocationData?,
        club: Club?,
        toleranceMeters: Double = 0.0
    ): Boolean {
        if (userLocation == null || club == null) {
            return false
        }
        return isWithinClubRadius(
            userLat = userLocation.latitude,
            userLng = userLocation.longitude,
            clubLat = club.location.latitude,
            clubLng = club.location.longitude,
            radiusMeters = club.geofenceRadiusMeters,
            toleranceMeters = toleranceMeters
        )
    }

    /**
     * Checks whether a user's [LocationData] is within a designated radius of a [GpsLocation].
     *
     * @param userLocation User's current location snapshot.
     * @param clubLocation Location of club.
     * @param radiusMeters Geofence radius in meters.
     * @param toleranceMeters Optional GPS jitter tolerance in meters.
     * @return `true` if within geofence radius, `false` otherwise.
     */
    fun isWithinClubRadius(
        userLocation: LocationData?,
        clubLocation: GpsLocation?,
        radiusMeters: Double,
        toleranceMeters: Double = 0.0
    ): Boolean {
        if (userLocation == null || clubLocation == null) {
            return false
        }
        return isWithinClubRadius(
            userLat = userLocation.latitude,
            userLng = userLocation.longitude,
            clubLat = clubLocation.latitude,
            clubLng = clubLocation.longitude,
            radiusMeters = radiusMeters,
            toleranceMeters = toleranceMeters
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

    /**
     * Validates that latitude and longitude coordinates are finite numbers within valid geographic ranges:
     * - Latitude: [-90.0, 90.0]
     * - Longitude: [-180.0, 180.0]
     */
    fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        if (latitude.isNaN() || longitude.isNaN() || latitude.isInfinite() || longitude.isInfinite()) {
            return false
        }
        return latitude in MIN_LATITUDE..MAX_LATITUDE && longitude in MIN_LONGITUDE..MAX_LONGITUDE
    }
}
