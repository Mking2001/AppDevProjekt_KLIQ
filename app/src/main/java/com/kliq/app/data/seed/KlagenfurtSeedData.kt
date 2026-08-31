package com.kliq.app.data.seed

import com.google.gson.Gson
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.ClubOfferEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.local.entities.FeedCommentEntity
import com.kliq.app.data.local.entities.FeedPostEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.local.entities.StoryEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.LiveOpeningStatus
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.MessageType
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.model.formatMsToIso
import com.kliq.app.util.OpeningHoursHelper
import java.time.LocalTime
import java.util.Calendar

/**
 * Demonstrationsdatensatz für den Zielmarkt Klagenfurt am Wörthersee.
 *
 * Die Einträge dienen ausschließlich der lokalen Vorbefüllung der Room-Datenbank,
 * solange kein produktives Backend angebunden ist. Öffnungszeiten, Bewertungen,
 * Auslastungswerte und Besucherzahlen sind frei gewählte Demonstrationswerte und
 * keine Angaben über tatsächliche Betriebe. Die Koordinaten liegen im Stadtgebiet
 * Klagenfurt und am Wörthersee-Ostufer, damit Entfernungsberechnung, Geofencing
 * und Kartendarstellung realistische Ergebnisse liefern.
 */
object KlagenfurtSeedData {

    const val CITY_NAME = "Klagenfurt"
    const val REGION_NAME = "Kärnten"

    /** Zentrum Klagenfurt als Standard-Kameraposition und Referenzpunkt. */
    const val CITY_LATITUDE = 46.6247
    const val CITY_LONGITUDE = 14.3053

    /** ID des lokalen Demo-Nutzers, unter der die App ohne Anmeldung arbeitet. */
    const val CURRENT_USER_ID = "current_user"
    const val CURRENT_USER_NAME = "Alexandros K."

    /** ID des Stadt-Gruppenchats für Klagenfurt. */
    const val CITY_CHAT_ID = "pub_klagenfurt"

    private val gson = Gson()

    private const val MINUTE_MS = 60_000L
    private const val HOUR_MS = 60L * MINUTE_MS
    private const val DAY_MS = 24L * HOUR_MS

    // =====================================================================
    // Clubs, Bars und Event-Locations (Klagenfurt)
    // =====================================================================

    /**
     * Liefert den vollständigen Venue-Datensatz für Klagenfurt (5 Clubs & Bars).
     *
     * @param nowMs Referenzzeit zur Bestimmung des Geöffnet-Status.
     */
    fun clubs(nowMs: Long = System.currentTimeMillis()): List<ClubEntity> = listOf(
        buildClub(
            id = "club_eventstage",
            name = "Eventstage Klagenfurt",
            latitude = 46.6358,
            longitude = 14.3160,
            address = "Gerichtsstraße 4, 9020 Klagenfurt",
            category = "Club",
            averageRating = 4.7,
            imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=1200&q=80",
            geofenceRadiusMeters = 200.0,
            weeklySchedule = mapOf(
                "Montag" to "Geschlossen",
                "Dienstag" to "Geschlossen",
                "Mittwoch" to "Geschlossen",
                "Donnerstag" to "22:00 - 04:00",
                "Freitag" to "22:00 - 05:00",
                "Samstag" to "22:00 - 05:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 0,
            liveVisitors = 0,
            malePercentage = 50,
            femalePercentage = 50,
            searchTags = "Eventstage Club Disco Techno Charts Großveranstaltung Klagenfurt",
            websiteUrl = "https://www.eventstage-klagenfurt.at",
            phoneNumber = "+43 463 000001",
            isPromoted = true,
            flameCount = 0,
            nowMs = nowMs
        ),
        buildClub(
            id = "club_teatro",
            name = "Teatro Club",
            latitude = 46.6210,
            longitude = 14.3080,
            address = "Heuplatz 2, 9020 Klagenfurt",
            category = "Club",
            averageRating = 4.5,
            imageUrl = "https://images.unsplash.com/photo-1545128485-c400e7702796?auto=format&fit=crop&w=1200&q=80",
            geofenceRadiusMeters = 150.0,
            weeklySchedule = mapOf(
                "Montag" to "Geschlossen",
                "Dienstag" to "Geschlossen",
                "Mittwoch" to "21:00 - 04:00",
                "Donnerstag" to "21:00 - 04:00",
                "Freitag" to "22:00 - 05:00",
                "Samstag" to "22:00 - 05:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 0,
            liveVisitors = 0,
            malePercentage = 50,
            femalePercentage = 50,
            searchTags = "Teatro Club Nightlife Charts House Innenstadt",
            websiteUrl = "https://www.teatro-club.at",
            phoneNumber = "+43 463 000002",
            isPromoted = true,
            flameCount = 0,
            nowMs = nowMs
        ),
        buildClub(
            id = "club_speki",
            name = "Speki",
            latitude = 46.6235,
            longitude = 14.3065,
            address = "Speckbacherstraße 1, 9020 Klagenfurt",
            category = "Bar",
            averageRating = 4.6,
            imageUrl = "https://images.unsplash.com/photo-1514933651103-005eec06c04b?auto=format&fit=crop&w=1200&q=80",
            geofenceRadiusMeters = 100.0,
            weeklySchedule = mapOf(
                "Montag" to "18:00 - 02:00",
                "Dienstag" to "18:00 - 02:00",
                "Mittwoch" to "18:00 - 02:00",
                "Donnerstag" to "18:00 - 03:00",
                "Freitag" to "18:00 - 04:00",
                "Samstag" to "18:00 - 04:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 0,
            liveVisitors = 0,
            malePercentage = 50,
            femalePercentage = 50,
            searchTags = "Speki Speckbacher Bar Cocktails Szene Treffpunkt",
            phoneNumber = "+43 463 000003",
            flameCount = 0,
            nowMs = nowMs
        ),
        buildClub(
            id = "club_stereo",
            name = "Stereo Club",
            latitude = 46.6250,
            longitude = 14.3120,
            address = "Völkermarkter Ring 25, 9020 Klagenfurt",
            category = "Club",
            averageRating = 4.4,
            imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80",
            geofenceRadiusMeters = 120.0,
            weeklySchedule = mapOf(
                "Montag" to "Geschlossen",
                "Dienstag" to "Geschlossen",
                "Mittwoch" to "20:00 - 02:00",
                "Donnerstag" to "20:00 - 03:00",
                "Freitag" to "21:00 - 04:00",
                "Samstag" to "21:00 - 04:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 0,
            liveVisitors = 0,
            malePercentage = 50,
            femalePercentage = 50,
            searchTags = "Stereo Club Live Alternative Indie Rock DJ Sets",
            phoneNumber = "+43 463 000004",
            flameCount = 0,
            nowMs = nowMs
        ),
        buildClub(
            id = "club_gig",
            name = "GIG Bar & Cafe",
            latitude = 46.6240,
            longitude = 14.3070,
            address = "Theatergasse 4, 9020 Klagenfurt",
            category = "Bar",
            averageRating = 4.8,
            imageUrl = "https://images.unsplash.com/photo-1572116469696-31de0f17cc34?auto=format&fit=crop&w=1200&q=80",
            geofenceRadiusMeters = 90.0,
            weeklySchedule = mapOf(
                "Montag" to "16:00 - 01:00",
                "Dienstag" to "16:00 - 01:00",
                "Mittwoch" to "16:00 - 01:00",
                "Donnerstag" to "16:00 - 02:00",
                "Freitag" to "16:00 - 03:00",
                "Samstag" to "16:00 - 03:00",
                "Sonntag" to "17:00 - 00:00"
            ),
            capacityPercent = 0,
            liveVisitors = 0,
            malePercentage = 50,
            femalePercentage = 50,
            searchTags = "GIG Bar Cafe Drinks Cocktails Theatergasse Afterwork",
            websiteUrl = "https://www.gig-klagenfurt.at",
            phoneNumber = "+43 463 000005",
            isPromoted = true,
            flameCount = 0,
            nowMs = nowMs
        )
    )

    // =====================================================================
    // Events und Aktionen
    // =====================================================================

    /**
     * Liefert die Event-Agenda (standardmäßig leer, keine erfundenen Dummys).
     */
    fun events(nowMs: Long = System.currentTimeMillis()): List<EventEntity> = emptyList()

    /**
     * Liefert Club-Aktionen (standardmäßig leer, keine erfundenen Gutscheincodes).
     */
    fun clubOffers(nowMs: Long = System.currentTimeMillis()): List<ClubOfferEntity> = emptyList()

    // =====================================================================
    // Nutzerprofile
    // =====================================================================

    /**
     * Liefert die Nutzerprofile des Demo-Datensatzes.
     * Der erste Eintrag ist das Profil des lokalen Nutzers.
     */
    fun users(nowMs: Long = System.currentTimeMillis()): List<UserEntity> = listOf(
        UserEntity(
            id = CURRENT_USER_ID,
            username = CURRENT_USER_NAME,
            email = "alex@kliq-demo.at",
            age = 24,
            hometown = "Klagenfurt",
            bio = "Nightlife am Wörthersee. Immer auf der Suche nach guter Musik und neuen Locations.",
            profilePictureUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80",
            photos = listOf(
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=800&q=80"
            ),
            phoneNumber = "+43 660 0000000",
            isVerified = true,
            updatedAtTimestampMs = nowMs,
            gender = "MALE"
        ),
        UserEntity(
            id = "usr_lena",
            username = "Lena P.",
            email = "lena@kliq-demo.at",
            age = 23,
            hometown = "Klagenfurt, Österreich",
            bio = "Studium an der AAU, am Wochenende meistens am Ostufer.",
            isVerified = true,
            updatedAtTimestampMs = nowMs,
            gender = "FEMALE"
        ),
        UserEntity(
            id = "usr_david",
            username = "David M.",
            email = "david@kliq-demo.at",
            age = 27,
            hometown = "Villach, Österreich",
            bio = "Techno, Bass und Filterkaffee. Pendler zwischen Villach und Klagenfurt.",
            isVerified = true,
            updatedAtTimestampMs = nowMs,
            gender = "MALE"
        ),
        UserEntity(
            id = "usr_sarah",
            username = "Sarah H.",
            email = "sarah@kliq-demo.at",
            age = 25,
            hometown = "Klagenfurt, Österreich",
            bio = "Cocktails vor Clubs. Empfehlungen jederzeit willkommen.",
            isVerified = false,
            updatedAtTimestampMs = nowMs,
            gender = "FEMALE"
        ),
        UserEntity(
            id = "usr_tobias",
            username = "Tobias R.",
            email = "tobias@kliq-demo.at",
            age = 29,
            hometown = "Klagenfurt, Österreich",
            bio = "Live-Konzerte statt Playlists.",
            isVerified = true,
            updatedAtTimestampMs = nowMs,
            gender = "MALE"
        ),
        UserEntity(
            id = "usr_nina",
            username = "Nina S.",
            email = "nina@kliq-demo.at",
            age = 22,
            hometown = "Feldkirchen, Österreich",
            bio = "Erst Strandbar, dann Innenstadt.",
            isVerified = false,
            updatedAtTimestampMs = nowMs,
            gender = "FEMALE"
        )
    )

    // =====================================================================
    // Chats und Nachrichten
    // =====================================================================

    /** Liefert den Stadt-Gruppenchat für eine bestimmte Stadt. */
    fun chatForCity(cityName: String, nowMs: Long = System.currentTimeMillis()): ChatEntity {
        val normalized = cityName.trim()
        val (id, initial) = when (normalized.lowercase()) {
            "klagenfurt", "klagenfurt am wörthersee" -> "pub_klagenfurt" to "K"
            "villach" -> "pub_villach" to "V"
            "graz" -> "pub_graz" to "G"
            "wien", "vienna" -> "pub_wien" to "W"
            "salzburg" -> "pub_salzburg" to "S"
            "innsbruck" -> "pub_innsbruck" to "I"
            "linz" -> "pub_linz" to "L"
            else -> "pub_${normalized.lowercase().replace(" ", "_").replace("ä", "ae").replace("ö", "oe").replace("ü", "ue")}" to normalized.take(1).uppercase()
        }
        return ChatEntity(
            id = id,
            name = normalized,
            cityRegion = normalized,
            lastMessageText = "",
            lastMessageTimestampMs = nowMs,
            lastMessageTimestampIso = formatMsToIso(nowMs),
            avatarInitial = initial,
            unreadCount = 0,
            chatType = ChatType.PUBLIC_CITY
        )
    }

    /** Liefert den Standard-Stadt-Gruppenchat (Klagenfurt). */
    fun chats(nowMs: Long = System.currentTimeMillis()): List<ChatEntity> = listOf(
        chatForCity(CITY_NAME, nowMs)
    )

    /** Liefert den Nachrichtenverlauf (keine Phantom-Nachrichten mehr). */
    fun messages(nowMs: Long = System.currentTimeMillis()): List<MessageEntity> = emptyList()

    // =====================================================================
    // Home-Feed
    // =====================================================================

    /** Liefert die Beiträge des Home-Feeds. */
    fun feedPosts(nowMs: Long = System.currentTimeMillis()): List<FeedPostEntity> = emptyList()

    /** Liefert die Kommentare zu den Beiträgen aus [feedPosts]. */
    fun feedComments(nowMs: Long = System.currentTimeMillis()): List<FeedCommentEntity> = emptyList()

    /** Liefert die Storys der Home-Story-Leiste. */
    fun stories(nowMs: Long = System.currentTimeMillis()): List<StoryEntity> = emptyList()

    // =====================================================================
    // Interne Hilfsfunktionen
    // =====================================================================

    /**
     * Baut eine [ClubEntity] und serialisiert den Wochenplan in das
     * von `ClubRepositoryImpl` erwartete JSON-Format.
     */
    private fun buildClub(
        id: String,
        name: String,
        latitude: Double,
        longitude: Double,
        address: String,
        category: String,
        averageRating: Double,
        geofenceRadiusMeters: Double,
        weeklySchedule: Map<String, String>,
        capacityPercent: Int,
        liveVisitors: Int,
        malePercentage: Int,
        femalePercentage: Int,
        searchTags: String,
        nowMs: Long,
        imageUrl: String = "",
        websiteUrl: String? = null,
        phoneNumber: String? = null,
        isPromoted: Boolean = false,
        flameCount: Int = 0
    ): ClubEntity {
        val calendar = Calendar.getInstance().apply { timeInMillis = nowMs }
        val today = OpeningHoursHelper.getCurrentDayGermanName(calendar)
        val todayHours = weeklySchedule[today] ?: "Geschlossen"
        val dateString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(calendar.time)
        val isOpenNow = OpeningHoursHelper.determineLiveStatus(
            operatingHours = OperatingHours(
                isOpenNow = false,
                todayHours = todayHours,
                weeklySchedule = weeklySchedule
            ),
            currentTime = LocalTime.of(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            ),
            currentDayOfWeek = today
        ) != LiveOpeningStatus.CLOSED

        val openingHoursJson = gson.toJson(
            mapOf(
                "isOpenNow" to isOpenNow,
                "todayHours" to todayHours,
                "weeklySchedule" to weeklySchedule
            )
        )

        return ClubEntity(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            address = address,
            geofenceRadiusMeters = geofenceRadiusMeters,
            averageRating = averageRating,
            openingHoursJson = openingHoursJson,
            category = category,
            rating = averageRating.toFloat(),
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1566737236500-c8ac43014a67?auto=format&fit=crop&w=1200&q=80" },
            region = REGION_NAME,
            currentCapacityPercent = capacityPercent,
            malePercentage = malePercentage,
            femalePercentage = femalePercentage,
            totalLiveVisitors = liveVisitors,
            externalSearchTags = searchTags,
            websiteUrl = websiteUrl,
            isPromoted = isPromoted,
            city = CITY_NAME,
            postalCode = "9020",
            phoneNumber = phoneNumber,
            flameCount = flameCount,
            flameDate = dateString
        )
    }

    private fun buildMessage(
        id: String,
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String,
        timestampMs: Long,
        isMine: Boolean,
        status: MessageStatus = MessageStatus.READ
    ): MessageEntity = MessageEntity(
        id = id,
        chatId = chatId,
        senderUserId = senderUserId,
        senderName = senderName,
        text = text,
        timestampMs = timestampMs,
        timestampIso = formatMsToIso(timestampMs),
        messageType = MessageType.TEXT,
        status = status,
        readAtMs = if (status == MessageStatus.READ) timestampMs else null,
        isMine = isMine
    )
}
