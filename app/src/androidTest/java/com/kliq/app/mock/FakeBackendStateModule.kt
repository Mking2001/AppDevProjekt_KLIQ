package com.kliq.app.mock

import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.model.OpeningHours
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.model.User
import com.kliq.app.data.model.UserStatus

object FakeBackendStateModule {

    val mockTestUser = User(
        id = "user_test_main_01",
        username = "alex_night",
        email = "alex@kliq-app.de",
        phoneNumber = "+491512345678",
        age = 24,
        hometown = "Berlin",
        bio = "Techno Liebhaber & Event-Entdecker in Berlin Mitte.",
        profilePictureUrl = null,
        searchIntent = SearchIntent.BOTH,
        smokingHabit = SmokingHabit.OCCASIONAL,
        drinkingHabit = DrinkingHabit.SOCIAL,
        averageRating = 4.8f,
        totalRatings = 19
    )

    val mockClubList = listOf(
        Club(
            id = "club_berghain",
            name = "Berghain / Panorama Bar",
            description = "Weltbekannter Techno-Club in einem ehemaligen Heizkraftwerk.",
            address = "Am Wriezener Bahnhof, 10243 Berlin",
            city = "Berlin",
            latitude = 52.5113,
            longitude = 13.4431,
            capacity = 1500,
            currentVisitors = 1275,
            femalePercentage = 45,
            malePercentage = 55,
            musicGenres = listOf("Techno", "Industrial", "House"),
            rating = 4.9f,
            reviewCount = 342,
            isFavorite = false,
            imageUrl = "https://images.unsplash.com/photo-543210987",
            openingHours = OpeningHours(
                openTime = "23:59",
                closeTime = "12:00",
                daysOpen = "Fr-So",
                isOpenNow = true
            )
        ),
        Club(
            id = "club_watergate",
            name = "Watergate Club",
            description = "Zwei Ebenen mit Spree-Blick und LED-Decke.",
            address = "Falckensteinstraße 49, 10997 Berlin",
            city = "Berlin",
            latitude = 52.5008,
            longitude = 13.4449,
            capacity = 800,
            currentVisitors = 560,
            femalePercentage = 50,
            malePercentage = 50,
            musicGenres = listOf("Deep House", "Minimal", "Tech House"),
            rating = 4.6f,
            reviewCount = 189,
            isFavorite = true,
            imageUrl = "https://images.unsplash.com/photo-543210988",
            openingHours = OpeningHours(
                openTime = "23:00",
                closeTime = "08:00",
                daysOpen = "Do-Sa",
                isOpenNow = true
            )
        )
    )

    val mockEvents = listOf(
        Event(
            id = "event_klubnacht",
            clubId = "club_berghain",
            title = "Klubnacht Weekend special",
            description = "Line-up mit internationalen Headlinern.",
            startTimeMs = System.currentTimeMillis() - 3600000,
            endTimeMs = System.currentTimeMillis() + 86400000,
            ticketPrice = 25.0,
            genre = "Techno",
            imageUrl = null
        )
    )

    val mockChatList = listOf(
        ChatListItem(
            id = "pub_berlin_mitte",
            title = "Berlin Mitte Nightlife",
            cityRegion = "Berlin",
            lastMessage = LastMessage(
                text = "Treffen wir uns am Watergate Spree-Deck? 🍻",
                timestampMs = System.currentTimeMillis() - 300000,
                senderName = "Lisa W."
            ),
            avatarInitial = "B",
            unreadCount = 3,
            chatType = ChatType.PUBLIC_CITY
        ),
        ChatListItem(
            id = "priv_lisa_w",
            title = "Lisa W.",
            cityRegion = "Berlin",
            lastMessage = LastMessage(
                text = "Hey, bist du heute auch im Berghain?",
                timestampMs = System.currentTimeMillis() - 1200000,
                senderName = "Lisa W."
            ),
            avatarInitial = "L",
            unreadCount = 1,
            chatType = ChatType.PRIVATE,
            userStatus = UserStatus.ONLINE
        )
    )
}
