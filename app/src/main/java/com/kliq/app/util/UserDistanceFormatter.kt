package com.kliq.app.util

import java.util.Locale
import kotlin.math.roundToInt

class UserDistanceFormatter(
    private val locale: Locale = Locale.GERMANY
) {
    companion object {
        const val DEFAULT_UNKNOWN_DISTANCE = "Entfernung unbekannt"
        private const val METERS_PER_KILOMETER = 1000.0

        val default = UserDistanceFormatter()
    }

    fun formatDistance(
        distanceMeters: Double?,
        fallbackLabel: String = DEFAULT_UNKNOWN_DISTANCE
    ): String {
        if (distanceMeters == null || distanceMeters.isNaN() || distanceMeters.isInfinite() || distanceMeters < 0.0) {
            return fallbackLabel
        }

        return when {
            distanceMeters < METERS_PER_KILOMETER -> {
                val meters = distanceMeters.roundToInt()
                "$meters m"
            }
            else -> {
                val km = distanceMeters / METERS_PER_KILOMETER
                String.format(locale, "%.1f km", km)
            }
        }
    }

    fun formatDistanceBadge(
        distanceMeters: Double?,
        withPrefix: Boolean = true
    ): String {
        val text = formatDistance(distanceMeters)
        if (distanceMeters == null || distanceMeters.isNaN() || distanceMeters.isInfinite() || distanceMeters < 0.0) {
            return text
        }
        return if (withPrefix) "📍 $text" else text
    }

    fun formatDistanceWithSuffix(
        distanceMeters: Double?,
        suffix: String = "entfernt"
    ): String {
        val text = formatDistance(distanceMeters)
        if (distanceMeters == null || distanceMeters.isNaN() || distanceMeters.isInfinite() || distanceMeters < 0.0) {
            return text
        }
        return "$text $suffix"
    }
}
