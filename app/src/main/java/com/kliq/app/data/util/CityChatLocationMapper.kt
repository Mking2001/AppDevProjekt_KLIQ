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

    val SUPPORTED_CITIES = listOf(
        CityChatConfig(
            id = "pub_1",
            title = "Berlin - Tonight",
            cityRegion = "Berlin",
            latitude = 52.5200,
            longitude = 13.4050,
            avatarInitial = "B",
            defaultOnlineCount = 248
        ),
        CityChatConfig(
            id = "pub_2",
            title = "München - Party Radar",
            cityRegion = "München",
            latitude = 48.1351,
            longitude = 11.5820,
            avatarInitial = "M",
            defaultOnlineCount = 184
        ),
        CityChatConfig(
            id = "pub_3",
            title = "Hamburg - Reeperbahn",
            cityRegion = "Hamburg",
            latitude = 53.5511,
            longitude = 9.9937,
            avatarInitial = "H",
            defaultOnlineCount = 192
        ),
        CityChatConfig(
            id = "pub_4",
            title = "Köln - Nightlife",
            cityRegion = "Köln",
            latitude = 50.9375,
            longitude = 6.9603,
            avatarInitial = "K",
            defaultOnlineCount = 145
        ),
        CityChatConfig(
            id = "pub_5",
            title = "Frankfurt - Party Scene",
            cityRegion = "Frankfurt",
            latitude = 50.1109,
            longitude = 8.6821,
            avatarInitial = "F",
            defaultOnlineCount = 126
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
