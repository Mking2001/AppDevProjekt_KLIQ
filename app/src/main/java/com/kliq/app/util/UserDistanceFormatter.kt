package com.kliq.app.util

import java.util.Locale
import kotlin.math.roundToInt

/**
 * Utility class for formatting raw distance measurements in meters into human-readable strings.
 *
 * Automatically switches units between meters ("150 m") and kilometers ("1.2 km"),
 * applies rounding logic, and handles missing or invalid distance values safely.
 */
class UserDistanceFormatter(
    private val locale: Locale = Locale.GERMANY
) {
    companion object {
        const val DEFAULT_UNKNOWN_DISTANCE = "Entfernung unbekannt"
        private const val METERS_PER_KILOMETER = 1000.0

        /** Default instance using standard configuration. */
        val default = UserDistanceFormatter()
    }

    /**
     * Formats a raw distance in meters into a human-readable string representation.
     *
     * @param distanceMeters Distance in meters as [Double], or `null` if position is unavailable.
     * @param fallbackLabel Custom text to return when distance is `null` or invalid.
     * @return Formatted string such as "150 m", "1.2 km", or fallback text.
     */
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

    /**
     * Formats a raw distance for UI badge overlays (e.g., "📍 350 m" or "📍 1.2 km").
     *
     * @param distanceMeters Distance in meters as [Double].
     * @param withPrefix If `true`, prepends location pin emoji indicator.
     * @return UI-formatted string.
     */
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

    /**
     * Formats distance with explicit suffix (e.g., "150 m entfernt" or "1.2 km entfernt").
     */
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
