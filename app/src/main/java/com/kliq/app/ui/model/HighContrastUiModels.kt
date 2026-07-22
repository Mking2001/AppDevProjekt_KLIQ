package com.kliq.app.ui.model

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Color palette tokens tailored for High-Contrast Dark/Violet (Lila) UI theme.
 */
object HighContrastVioletPalette {
    const val PrimaryViolet = "#BB86FC"
    const val DeepVioletContainer = "#25123E"
    const val NeonCyanAccent = "#03DAC6"
    const val HighContrastBackground = "#121212"
    const val HighContrastSurface = "#1E1B2E"
    const val TextPrimaryHighContrast = "#FFFFFF"
    const val TextSecondaryHighContrast = "#E1BEE7"
    const val BadgeOpenGreen = "#00E676"
    const val BadgeClosedRed = "#FF5252"
    const val FavoriteActivePink = "#FF4081"
}

data class ClubHighContrastItemState(
    val id: String,
    val name: String,
    val category: String,
    val ratingFormatted: String,
    val ratingBadgeColorHex: String = HighContrastVioletPalette.PrimaryViolet,
    val addressFormatted: String,
    val distanceFormatted: String,
    val isOpenNow: Boolean,
    val openStatusBadgeText: String,
    val openStatusBadgeColorHex: String,
    val isFavorite: Boolean,
    val favoriteIconColorHex: String = HighContrastVioletPalette.FavoriteActivePink,
    val geofenceRadiusFormatted: String,
    val activeEventSummary: String?,
    val imageUrl: String,
    val capacityPercent: Int,
    val liveVisitors: Int
)

data class EventHighContrastItemState(
    val id: String,
    val clubId: String,
    val title: String,
    val description: String,
    val formattedTimeSpan: String,
    val priceFormatted: String,
    val specialOfferTags: List<String>,
    val highContrastTagColorHex: String = HighContrastVioletPalette.NeonCyanAccent,
    val imageUrl: String?
)

fun Club.toHighContrastUiState(userLat: Double? = null, userLon: Double? = null): ClubHighContrastItemState {
    val distanceFormatted = if (userLat != null && userLon != null) {
        val distKm = calculateDistanceKm(userLat, userLon, location.latitude, location.longitude)
        String.format(Locale.getDefault(), "%.1f km entfernt", distKm)
    } else {
        "Standort verfügbar"
    }

    val isOpen = operatingHours.isOpenNow
    val badgeText = if (isOpen) "OFFEN • ${operatingHours.todayHours}" else "GESCHLOSSEN"
    val badgeColor = if (isOpen) HighContrastVioletPalette.BadgeOpenGreen else HighContrastVioletPalette.BadgeClosedRed

    return ClubHighContrastItemState(
        id = id,
        name = name,
        category = category.ifBlank { "Club" },
        ratingFormatted = String.format(Locale.getDefault(), "★ %.1f", averageRating),
        addressFormatted = location.address.ifBlank { "GPS: ${location.latitude}, ${location.longitude}" },
        distanceFormatted = distanceFormatted,
        isOpenNow = isOpen,
        openStatusBadgeText = badgeText,
        openStatusBadgeColorHex = badgeColor,
        isFavorite = isFavorite,
        geofenceRadiusFormatted = "${geofenceRadiusMeters.toInt()}m Geofence",
        activeEventSummary = activeEvent?.title,
        imageUrl = imageUrl,
        capacityPercent = analytics.currentCapacityPercent,
        liveVisitors = analytics.totalLiveVisitors
    )
}

fun Event.toHighContrastUiState(): EventHighContrastItemState {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startStr = dateFormat.format(Date(startTime))
    val endStr = dateFormat.format(Date(endTime))
    val timeSpan = "$startStr - $endStr Uhr"

    val tags = specialOffers.map { "${it.title}: ${it.discountDescription}" }

    return EventHighContrastItemState(
        id = id,
        clubId = clubId,
        title = title,
        description = description,
        formattedTimeSpan = timeSpan,
        priceFormatted = price.ifBlank { "Eintritt frei" },
        specialOfferTags = tags,
        imageUrl = imageUrl
    )
}

private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}
