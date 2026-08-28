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
            capacityPercent = 82,
            liveVisitors = 380,
            malePercentage = 52,
            femalePercentage = 48,
            searchTags = "Eventstage Club Disco Techno Charts Großveranstaltung Klagenfurt",
            websiteUrl = "https://www.eventstage-klagenfurt.at",
            phoneNumber = "+43 463 000001",
            isPromoted = true,
            flameCount = 48,
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
            capacityPercent = 74,
            liveVisitors = 210,
            malePercentage = 48,
            femalePercentage = 52,
            searchTags = "Teatro Club Nightlife Charts House Innenstadt",
            websiteUrl = "https://www.teatro-club.at",
            phoneNumber = "+43 463 000002",
            isPromoted = true,
            flameCount = 35,
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
            capacityPercent = 58,
            liveVisitors = 65,
            malePercentage = 50,
            femalePercentage = 50,
            searchTags = "Speki Speckbacher Bar Cocktails Szene Treffpunkt",
            phoneNumber = "+43 463 000003",
            flameCount = 28,
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
            capacityPercent = 67,
            liveVisitors = 160,
            malePercentage = 54,
            femalePercentage = 46,
            searchTags = "Stereo Club Live Alternative Indie Rock DJ Sets",
            phoneNumber = "+43 463 000004",
            flameCount = 22,
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
            capacityPercent = 53,
            liveVisitors = 85,
            malePercentage = 47,
            femalePercentage = 53,
            searchTags = "GIG Bar Cafe Drinks Cocktails Theatergasse Afterwork",
            websiteUrl = "https://www.gig-klagenfurt.at",
            phoneNumber = "+43 463 000005",
            isPromoted = true,
            flameCount = 19,
            nowMs = nowMs
        )
    )

    // =====================================================================
    // Events und Aktionen
    // =====================================================================

    /**
     * Liefert die Event-Agenda der kommenden Tage.
     * Startzeiten sind relativ zur übergebenen Referenzzeit gesetzt,
     * damit die Agenda unabhängig vom Installationsdatum in der Zukunft liegt.
     */
    fun events(nowMs: Long = System.currentTimeMillis()): List<EventEntity> = listOf(
        EventEntity(
            id = "evt_eventstage_weekend",
            clubId = "club_eventstage",
            title = "Weekend Grand Opening",
            description = "Großes Clubbing auf 2 Floors mit internationalen Resident-DJs und Lasershow.",
            startTime = nowMs + 6L * HOUR_MS,
            endTime = nowMs + 13L * HOUR_MS,
            price = "15 EUR",
            time = "22:00 - 05:00",
            searchKeywords = "Eventstage Techno House Clubbing Klagenfurt",
            capacityLimit = 1000,
            category = "Techno"
        ),
        EventEntity(
            id = "evt_teatro_night",
            clubId = "club_teatro",
            title = "Teatro Saturday Clubbing",
            description = "Charts, House & RnB Party im Herzen von Klagenfurt.",
            startTime = nowMs + 1L * DAY_MS + 4L * HOUR_MS,
            endTime = nowMs + 1L * DAY_MS + 10L * HOUR_MS,
            price = "10 EUR",
            time = "22:00 - 05:00",
            searchKeywords = "Teatro Charts Clubbing House Party",
            capacityLimit = 450,
            category = "Charts"
        ),
        EventEntity(
            id = "evt_stereo_live",
            clubId = "club_stereo",
            title = "Stereo Indie & Alternative Session",
            description = "Live Band Auftritte und anschließendes DJ-Set bis 04:00 Uhr.",
            startTime = nowMs + 2L * DAY_MS + 3L * HOUR_MS,
            endTime = nowMs + 2L * DAY_MS + 9L * HOUR_MS,
            price = "12 EUR",
            time = "21:00 - 04:00",
            searchKeywords = "Stereo Live Konzert Alternative Indie Rock",
            capacityLimit = 350,
            category = "Live"
        ),
        EventEntity(
            id = "evt_gig_afterwork",
            clubId = "club_gig",
            title = "GIG Afterwork Lounge & Drinks",
            description = "Entspannte Drinks und Lounge-Musik in der Theatergasse.",
            startTime = nowMs + 3L * DAY_MS + 2L * HOUR_MS,
            endTime = nowMs + 3L * DAY_MS + 7L * HOUR_MS,
            price = "Eintritt frei",
            time = "18:00 - 01:00",
            searchKeywords = "GIG Bar Afterwork Drinks Cocktails",
            capacityLimit = 150,
            category = "Bar"
        ),
        EventEntity(
            id = "evt_speki_social",
            clubId = "club_speki",
            title = "Speki Weekend Warm-Up",
            description = "Der Szene-Treffpunkt für den Start ins Klagenfurter Nachtleben.",
            startTime = nowMs + 4L * DAY_MS + 2L * HOUR_MS,
            endTime = nowMs + 4L * DAY_MS + 8L * HOUR_MS,
            price = "Eintritt frei",
            time = "19:00 - 02:00",
            searchKeywords = "Speki Bar Szene Drinks Weekend",
            capacityLimit = 100,
            category = "Bar"
        )
    )

    /** Liefert Club-Aktionen, die im Event-Info-Block angezeigt werden. */
    fun clubOffers(nowMs: Long = System.currentTimeMillis()): List<ClubOfferEntity> = listOf(
        ClubOfferEntity(
            id = "offer_eventstage_early",
            clubId = "club_eventstage",
            title = "Early Bird Eintritt bis 23:00",
            description = "Ermäßigter Eintritt für alle Gäste vor 23:00 Uhr.",
            offerType = "EINTRITT",
            discountPercentage = 30,
            validUntil = nowMs + 7L * DAY_MS,
            isExclusive = true
        ),
        ClubOfferEntity(
            id = "offer_teatro_cocktail",
            clubId = "club_teatro",
            title = "Welcome Drink Special",
            description = "Welcome Drink inklusive bei Vorlage der KLIQ App.",
            offerType = "GETRAENK",
            discountPercentage = 50,
            validUntil = nowMs + 14L * DAY_MS
        ),
        ClubOfferEntity(
            id = "offer_gig_drinks",
            clubId = "club_gig",
            title = "2for1 Afterwork Cocktail",
            description = "Zwei Cocktails zum Preis von einem von 18:00 bis 20:00 Uhr.",
            offerType = "GETRAENK",
            discountCode = "KLIQ-GIG",
            discountPercentage = 50,
            validUntil = nowMs + 21L * DAY_MS
        )
    )

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
            email = "demo@kliq-demo.at",
            age = 24,
            hometown = "Klagenfurt, Österreich",
            bio = "Nightlife am Wörthersee. Immer auf der Suche nach guter Musik und neuen Locations.",
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

    /** Liefert den Stadt-Gruppenchat sowie zwei Direktkonversationen. */
    fun chats(nowMs: Long = System.currentTimeMillis()): List<ChatEntity> = listOf(
        ChatEntity(
            id = CITY_CHAT_ID,
            name = "Klagenfurt - Tonight",
            cityRegion = CITY_NAME,
            lastMessageText = "Volksgarten oder Bollwerk? Muss mich noch entscheiden.",
            lastMessageTimestampMs = nowMs - 12L * MINUTE_MS,
            lastMessageTimestampIso = formatMsToIso(nowMs - 12L * MINUTE_MS),
            avatarInitial = "K",
            unreadCount = 3,
            chatType = ChatType.PUBLIC_CITY
        ),
        ChatEntity(
            id = "priv_lena",
            name = "Lena P.",
            lastMessageText = "Treffen wir uns vorher auf einen Spritzer?",
            lastMessageTimestampMs = nowMs - 40L * MINUTE_MS,
            lastMessageTimestampIso = formatMsToIso(nowMs - 40L * MINUTE_MS),
            avatarInitial = "L",
            unreadCount = 2,
            chatType = ChatType.PRIVATE,
            isOnline = true
        ),
        ChatEntity(
            id = "priv_david",
            name = "David M.",
            lastMessageText = "Line-up ist online, schau dir Floor 2 an.",
            lastMessageTimestampMs = nowMs - 5L * HOUR_MS,
            lastMessageTimestampIso = formatMsToIso(nowMs - 5L * HOUR_MS),
            avatarInitial = "D",
            unreadCount = 0,
            chatType = ChatType.PRIVATE
        )
    )

    /** Liefert den Nachrichtenverlauf zu allen Chats aus [chats]. */
    fun messages(nowMs: Long = System.currentTimeMillis()): List<MessageEntity> = listOf(
        buildMessage(
            id = "msg_kf_1",
            chatId = CITY_CHAT_ID,
            senderUserId = "usr_david",
            senderName = "David M.",
            text = "Servus, wer ist heute im Volksgarten?",
            timestampMs = nowMs - 95L * MINUTE_MS,
            isMine = false
        ),
        buildMessage(
            id = "msg_kf_2",
            chatId = CITY_CHAT_ID,
            senderUserId = "usr_lena",
            senderName = "Lena P.",
            text = "Ich komme, aber erst nach Mitternacht.",
            timestampMs = nowMs - 78L * MINUTE_MS,
            isMine = false
        ),
        buildMessage(
            id = "msg_kf_3",
            chatId = CITY_CHAT_ID,
            senderUserId = CURRENT_USER_ID,
            senderName = "Du",
            text = "Bin dabei. Vorher noch kurz auf einen Drink in die Altstadt.",
            timestampMs = nowMs - 61L * MINUTE_MS,
            isMine = true,
            status = MessageStatus.READ
        ),
        buildMessage(
            id = "msg_kf_4",
            chatId = CITY_CHAT_ID,
            senderUserId = "usr_tobias",
            senderName = "Tobias R.",
            text = "Im Bollwerk spielen heute vier Bands, falls jemand Live hören will.",
            timestampMs = nowMs - 34L * MINUTE_MS,
            isMine = false
        ),
        buildMessage(
            id = "msg_kf_5",
            chatId = CITY_CHAT_ID,
            senderUserId = "usr_nina",
            senderName = "Nina S.",
            text = "Volksgarten oder Bollwerk? Muss mich noch entscheiden.",
            timestampMs = nowMs - 12L * MINUTE_MS,
            isMine = false
        ),
        buildMessage(
            id = "msg_lena_1",
            chatId = "priv_lena",
            senderUserId = CURRENT_USER_ID,
            senderName = "Du",
            text = "Hey Lena, kommst du heute mit in den Volksgarten?",
            timestampMs = nowMs - 3L * HOUR_MS,
            isMine = true,
            status = MessageStatus.READ
        ),
        buildMessage(
            id = "msg_lena_2",
            chatId = "priv_lena",
            senderUserId = "usr_lena",
            senderName = "Lena P.",
            text = "Ja gerne. Ich muss nur vorher noch bei meinen Eltern vorbei.",
            timestampMs = nowMs - 2L * HOUR_MS,
            isMine = false
        ),
        buildMessage(
            id = "msg_lena_3",
            chatId = "priv_lena",
            senderUserId = "usr_lena",
            senderName = "Lena P.",
            text = "Treffen wir uns vorher auf einen Spritzer?",
            timestampMs = nowMs - 40L * MINUTE_MS,
            isMine = false
        ),
        buildMessage(
            id = "msg_david_1",
            chatId = "priv_david",
            senderUserId = "usr_david",
            senderName = "David M.",
            text = "Line-up ist online, schau dir Floor 2 an.",
            timestampMs = nowMs - 5L * HOUR_MS,
            isMine = false
        ),
        buildMessage(
            id = "msg_david_2",
            chatId = "priv_david",
            senderUserId = CURRENT_USER_ID,
            senderName = "Du",
            text = "Sieht stark aus. Ich hole die Tickets im Vorverkauf.",
            timestampMs = nowMs - 4L * HOUR_MS,
            isMine = true,
            status = MessageStatus.READ
        )
    )

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
