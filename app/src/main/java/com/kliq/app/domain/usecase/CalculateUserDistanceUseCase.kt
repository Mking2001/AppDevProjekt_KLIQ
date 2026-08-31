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

@Singleton
class CalculateUserDistanceUseCase @Inject constructor() {

    companion object {

        const val EARTH_RADIUS_METERS = 6371000.0

        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0
    }

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

    fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        if (latitude.isNaN() || longitude.isNaN() || latitude.isInfinite() || longitude.isInfinite()) {
            return false
        }
        return latitude in MIN_LATITUDE..MAX_LATITUDE && longitude in MIN_LONGITUDE..MAX_LONGITUDE
    }
}
