package com.kliq.app.data.model

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

enum class CameraEasing {
    EASE_IN_OUT,
    FAST_OUT_SLOW_IN,
    LINEAR
}

sealed interface MapCameraAnimationEvent {

    data class AnimateToLocation(
        val latitude: Double,
        val longitude: Double,
        val zoom: Float = 15.5f,
        val tilt: Float = 0.0f,
        val bearing: Float = 0.0f,
        val durationMs: Int = 1000,
        val easing: CameraEasing = CameraEasing.EASE_IN_OUT
    ) : MapCameraAnimationEvent

    data class AnimateToBounds(
        val bounds: LatLngBoundsData,
        val paddingPx: Int = 100,
        val durationMs: Int = 1000
    ) : MapCameraAnimationEvent

    data class AnimateTiltRotation(
        val tilt: Float,
        val bearing: Float,
        val durationMs: Int = 800
    ) : MapCameraAnimationEvent

    data class SnapToPosition(
        val latitude: Double,
        val longitude: Double,
        val zoom: Float = 14.0f
    ) : MapCameraAnimationEvent
}
