package com.kliq.app.data.model

/**
 * Data representation of bounding box geographic bounds for camera framing.
 *
 * @param southWestLat Latitude of the south-west corner.
 * @param southWestLng Longitude of the south-west corner.
 * @param northEastLat Latitude of the north-east corner.
 * @param northEastLng Longitude of the north-east corner.
 */
data class LatLngBoundsData(
    val southWestLat: Double,
    val southWestLng: Double,
    val northEastLat: Double,
    val northEastLng: Double
) {
    val centerLat: Double
        get() = (southWestLat + northEastLat) / 2.0

    val centerLng: Double
        get() = (southWestLng + northEastLng) / 2.0

    companion object {
        /**
         * Computes [LatLngBoundsData] enclosing a list of geographic coordinate pairs.
         * Returns null if coordinate list is empty.
         */
        fun fromCoordinates(coordinates: List<Pair<Double, Double>>): LatLngBoundsData? {
            if (coordinates.isEmpty()) return null
            var minLat = Double.MAX_VALUE
            var maxLat = -Double.MAX_VALUE
            var minLng = Double.MAX_VALUE
            var maxLng = -Double.MAX_VALUE

            for ((lat, lng) in coordinates) {
                if (lat < minLat) minLat = lat
                if (lat > maxLat) maxLat = lat
                if (lng < minLng) minLng = lng
                if (lng > maxLng) maxLng = lng
            }

            // Ensure non-zero delta for single coordinate edge case
            if (minLat == maxLat) {
                minLat -= 0.001
                maxLat += 0.001
            }
            if (minLng == maxLng) {
                minLng -= 0.001
                maxLng += 0.001
            }

            return LatLngBoundsData(
                southWestLat = minLat,
                southWestLng = minLng,
                northEastLat = maxLat,
                northEastLng = maxLng
            )
        }
    }
}

/**
 * Easing curve definitions for camera movement curves.
 */
enum class CameraEasing {
    EASE_IN_OUT,
    FAST_OUT_SLOW_IN,
    LINEAR
}

/**
 * Reactive camera animation events emitted by MapViewModel to trigger smooth view transitions.
 */
sealed interface MapCameraAnimationEvent {
    /**
     * Smoothly animate camera to specific lat/lng, zoom, tilt, bearing over [durationMs].
     */
    data class AnimateToLocation(
        val latitude: Double,
        val longitude: Double,
        val zoom: Float = 15.5f,
        val tilt: Float = 0.0f,
        val bearing: Float = 0.0f,
        val durationMs: Int = 1000,
        val easing: CameraEasing = CameraEasing.EASE_IN_OUT
    ) : MapCameraAnimationEvent

    /**
     * Smoothly animate camera to fit [bounds] with [paddingPx] over [durationMs].
     */
    data class AnimateToBounds(
        val bounds: LatLngBoundsData,
        val paddingPx: Int = 100,
        val durationMs: Int = 1000
    ) : MapCameraAnimationEvent

    /**
     * Smoothly animate tilt and rotation transitions for night perspective adjustments.
     */
    data class AnimateTiltRotation(
        val tilt: Float,
        val bearing: Float,
        val durationMs: Int = 800
    ) : MapCameraAnimationEvent

    /**
     * Snap camera directly to position without transition.
     */
    data class SnapToPosition(
        val latitude: Double,
        val longitude: Double,
        val zoom: Float = 14.0f
    ) : MapCameraAnimationEvent
}
