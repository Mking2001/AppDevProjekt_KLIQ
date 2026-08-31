package com.kliq.app.data.util

import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.model.LocationData
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class CityChatConfig(
    val id: String,
    val title: String,
    val cityRegion: String,
    val latitude: Double,
    val longitude: Double,
    val avatarInitial: String,
    val defaultOnlineCount: Int
)

object CityChatLocationMapper {

    /**
     * Unterstuetzte Stadt-Gruppenchats. Der Zielmarkt Klagenfurt steht an erster
     * Stelle und dient gleichzeitig als Fallback, wenn keine GPS-Position vorliegt.
     */
    val SUPPORTED_CITIES = listOf(
        CityChatConfig(
            id = "pub_klagenfurt",
            title = "Klagenfurt - Tonight",
            cityRegion = "Klagenfurt",
            latitude = 46.6236,
            longitude = 14.3084,
            avatarInitial = "K",
            defaultOnlineCount = 138
        ),
        CityChatConfig(
            id = "pub_villach",
            title = "Villach - Party Radar",
            cityRegion = "Villach",
            latitude = 46.6103,
            longitude = 13.8558,
            avatarInitial = "V",
            defaultOnlineCount = 84
        ),
        CityChatConfig(
            id = "pub_graz",
            title = "Graz - Nightlife",
            cityRegion = "Graz",
            latitude = 47.0707,
            longitude = 15.4395,
            avatarInitial = "G",
            defaultOnlineCount = 212
        ),
        CityChatConfig(
            id = "pub_wien",
            title = "Wien - Tonight",
            cityRegion = "Wien",
            latitude = 48.2082,
            longitude = 16.3738,
            avatarInitial = "W",
            defaultOnlineCount = 396
        ),
        CityChatConfig(
            id = "pub_salzburg",
            title = "Salzburg - Party Scene",
            cityRegion = "Salzburg",
            latitude = 47.8095,
            longitude = 13.0550,
            avatarInitial = "S",
            defaultOnlineCount = 147
        ),
        CityChatConfig(
            id = "pub_ljubljana",
            title = "Ljubljana - Crossborder",
            cityRegion = "Ljubljana",
            latitude = 46.0569,
            longitude = 14.5058,
            avatarInitial = "L",
            defaultOnlineCount = 96
        )
    )

    fun resolveCityForLocation(location: LocationData?): CityChatConfig {
        if (location == null) return SUPPORTED_CITIES.first()

        var minDistance = Double.MAX_VALUE
        var closestCity = SUPPORTED_CITIES.first()

        for (city in SUPPORTED_CITIES) {
            val dist = calculateDistanceInKm(
                location.latitude, location.longitude,
                city.latitude, city.longitude
            )
            if (dist < minDistance) {
                minDistance = dist
                closestCity = city
            }
        }
        return closestCity
    }

    fun resolveCityByName(cityName: String?): CityChatConfig {
        if (cityName.isNullOrBlank()) return SUPPORTED_CITIES.first()
        val trimmed = cityName.trim()
        return SUPPORTED_CITIES.find {
            it.cityRegion.equals(trimmed, ignoreCase = true) ||
            trimmed.contains(it.cityRegion, ignoreCase = true) ||
            it.cityRegion.contains(trimmed, ignoreCase = true)
        } ?: CityChatConfig(
            id = "pub_${trimmed.lowercase().replace(" ", "_").replace("ä", "ae").replace("ö", "oe").replace("ü", "ue")}",
            title = "$trimmed - Tonight",
            cityRegion = trimmed,
            latitude = 46.6236,
            longitude = 14.3084,
            avatarInitial = trimmed.take(1).uppercase(),
            defaultOnlineCount = 1
        )
    }

    fun calculateDistanceInKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    fun buildCityChatListItem(
        config: CityChatConfig,
        lastMessageText: String = "Willkommen im ${config.cityRegion} Public Chat!",
        lastMessageTimestampMs: Long = System.currentTimeMillis() - 600000L,
        unreadCount: Int = 0,
        distanceKm: Double? = null,
        isGpsAssigned: Boolean = false
    ): ChatListItem {
        return ChatListItem(
            id = config.id,
            title = config.title,
            cityRegion = config.cityRegion,
            lastMessage = LastMessage(
                text = lastMessageText,
                timestampMs = lastMessageTimestampMs
            ),
            avatarInitial = config.avatarInitial,
            unreadCount = unreadCount,
            chatType = ChatType.PUBLIC_CITY,
            distanceKm = distanceKm,
            onlineMembersCount = config.defaultOnlineCount,
            isGpsAssigned = isGpsAssigned
        )
    }
}
