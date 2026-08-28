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

    /** Zentrum Klagenfurt (Neuer Platz) als Standard-Kameraposition und Referenzpunkt. */
    const val CITY_LATITUDE = 46.6236
    const val CITY_LONGITUDE = 14.3084

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
    // Clubs, Bars und Event-Locations
    // =====================================================================

    /**
     * Liefert den vollständigen Venue-Datensatz für Klagenfurt.
     *
     * @param nowMs Referenzzeit zur Bestimmung des Geöffnet-Status.
     */
    fun clubs(nowMs: Long = System.currentTimeMillis()): List<ClubEntity> = listOf(
        buildClub(
            id = "club_volksgarten",
            name = "Volksgarten Klagenfurt",
            latitude = 46.6108,
            longitude = 14.3126,
            address = "Südring, 9020 Klagenfurt",
            category = "Club",
            averageRating = 4.6,
            geofenceRadiusMeters = 250.0,
            weeklySchedule = mapOf(
                "Montag" to "Geschlossen",
                "Dienstag" to "Geschlossen",
                "Mittwoch" to "Geschlossen",
                "Donnerstag" to "22:00 - 04:00",
                "Freitag" to "22:00 - 05:00",
                "Samstag" to "22:00 - 05:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 78,
            liveVisitors = 420,
            malePercentage = 51,
            femalePercentage = 49,
            searchTags = "Techno House Electro Grossraumdiskothek Wochenende",
            websiteUrl = "https://www.kliq-demo.at/volksgarten",
            phoneNumber = "+43 463 000001",
            isPromoted = true,
            nowMs = nowMs
        ),
        buildClub(
            id = "club_bollwerk",
            name = "Bollwerk",
            latitude = 46.6251,
            longitude = 14.3121,
            address = "Kardinalschütt, 9020 Klagenfurt",
            category = "Club",
            averageRating = 4.4,
            geofenceRadiusMeters = 150.0,
            weeklySchedule = mapOf(
                "Montag" to "Geschlossen",
                "Dienstag" to "Geschlossen",
                "Mittwoch" to "20:00 - 02:00",
                "Donnerstag" to "20:00 - 02:00",
                "Freitag" to "21:00 - 04:00",
                "Samstag" to "21:00 - 04:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 64,
            liveVisitors = 180,
            malePercentage = 55,
            femalePercentage = 45,
            searchTags = "Live Konzert Rock Indie Alternative Bühne",
            websiteUrl = "https://www.kliq-demo.at/bollwerk",
            phoneNumber = "+43 463 000002",
            nowMs = nowMs
        ),
        buildClub(
            id = "club_duchamp",
            name = "Duchamp Bar",
            latitude = 46.6247,
            longitude = 14.3096,
            address = "Pfarrhofgasse, 9020 Klagenfurt",
            category = "Bar",
            averageRating = 4.7,
            geofenceRadiusMeters = 80.0,
            weeklySchedule = mapOf(
                "Montag" to "18:00 - 01:00",
                "Dienstag" to "18:00 - 01:00",
                "Mittwoch" to "18:00 - 01:00",
                "Donnerstag" to "18:00 - 02:00",
                "Freitag" to "18:00 - 03:00",
                "Samstag" to "18:00 - 03:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 45,
            liveVisitors = 62,
            malePercentage = 47,
            femalePercentage = 53,
            searchTags = "Cocktails Bar Innenstadt Pfarrplatz Afterwork",
            phoneNumber = "+43 463 000003",
            nowMs = nowMs
        ),
        buildClub(
            id = "club_molly_malone",
            name = "Molly Malone",
            latitude = 46.6240,
            longitude = 14.3110,
            address = "Kardinalsplatz, 9020 Klagenfurt",
            category = "Pub",
            averageRating = 4.5,
            geofenceRadiusMeters = 80.0,
            weeklySchedule = mapOf(
                "Montag" to "16:00 - 01:00",
                "Dienstag" to "16:00 - 01:00",
                "Mittwoch" to "16:00 - 01:00",
                "Donnerstag" to "16:00 - 02:00",
                "Freitag" to "16:00 - 03:00",
                "Samstag" to "16:00 - 03:00",
                "Sonntag" to "16:00 - 00:00"
            ),
            capacityPercent = 52,
            liveVisitors = 74,
            malePercentage = 58,
            femalePercentage = 42,
            searchTags = "Irish Pub Bier Sport Live Musik Guinness",
            phoneNumber = "+43 463 000004",
            nowMs = nowMs
        ),
        buildClub(
            id = "club_kamot",
            name = "Cafe Bar Kamot",
            latitude = 46.6229,
            longitude = 14.3067,
            address = "Herrengasse, 9020 Klagenfurt",
            category = "Bar",
            averageRating = 4.3,
            geofenceRadiusMeters = 70.0,
            weeklySchedule = mapOf(
                "Montag" to "17:00 - 00:00",
                "Dienstag" to "17:00 - 00:00",
                "Mittwoch" to "17:00 - 00:00",
                "Donnerstag" to "17:00 - 01:00",
                "Freitag" to "17:00 - 02:00",
                "Samstag" to "17:00 - 02:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 38,
            liveVisitors = 41,
            malePercentage = 49,
            femalePercentage = 51,
            searchTags = "Cafe Bar Studenten Altstadt Wein",
            phoneNumber = "+43 463 000005",
            nowMs = nowMs
        ),
        buildClub(
            id = "club_scotch",
            name = "Scotch Club",
            latitude = 46.6222,
            longitude = 14.3103,
            address = "Bahnhofstraße, 9020 Klagenfurt",
            category = "Club",
            averageRating = 4.1,
            geofenceRadiusMeters = 120.0,
            weeklySchedule = mapOf(
                "Montag" to "Geschlossen",
                "Dienstag" to "Geschlossen",
                "Mittwoch" to "Geschlossen",
                "Donnerstag" to "22:00 - 04:00",
                "Freitag" to "22:00 - 05:00",
                "Samstag" to "22:00 - 05:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 57,
            liveVisitors = 135,
            malePercentage = 53,
            femalePercentage = 47,
            searchTags = "Charts Party Discofox Club Bahnhofstrasse",
            phoneNumber = "+43 463 000006",
            nowMs = nowMs
        ),
        buildClub(
            id = "club_loretto",
            name = "Strandbar Loretto",
            latitude = 46.6162,
            longitude = 14.2696,
            address = "Lorettoweg, 9020 Klagenfurt",
            category = "Bar",
            averageRating = 4.8,
            geofenceRadiusMeters = 200.0,
            weeklySchedule = mapOf(
                "Montag" to "14:00 - 23:00",
                "Dienstag" to "14:00 - 23:00",
                "Mittwoch" to "14:00 - 23:00",
                "Donnerstag" to "14:00 - 00:00",
                "Freitag" to "14:00 - 02:00",
                "Samstag" to "12:00 - 02:00",
                "Sonntag" to "12:00 - 23:00"
            ),
            capacityPercent = 61,
            liveVisitors = 190,
            malePercentage = 46,
            femalePercentage = 54,
            searchTags = "Strandbar Woerthersee Sundowner Sommer Loretto",
            websiteUrl = "https://www.kliq-demo.at/loretto",
            phoneNumber = "+43 463 000007",
            isPromoted = true,
            nowMs = nowMs
        ),
        buildClub(
            id = "club_villa_lido",
            name = "Villa Lido",
            latitude = 46.6155,
            longitude = 14.2733,
            address = "Friedelstrand, 9020 Klagenfurt",
            category = "Restaurant",
            averageRating = 4.5,
            geofenceRadiusMeters = 150.0,
            weeklySchedule = mapOf(
                "Montag" to "11:30 - 23:00",
                "Dienstag" to "11:30 - 23:00",
                "Mittwoch" to "11:30 - 23:00",
                "Donnerstag" to "11:30 - 23:00",
                "Freitag" to "11:30 - 00:00",
                "Samstag" to "11:30 - 00:00",
                "Sonntag" to "11:30 - 22:00"
            ),
            capacityPercent = 43,
            liveVisitors = 88,
            malePercentage = 48,
            femalePercentage = 52,
            searchTags = "Restaurant Seeblick Dinner Woerthersee Terrasse",
            phoneNumber = "+43 463 000008",
            nowMs = nowMs
        ),
        buildClub(
            id = "club_pumpe",
            name = "Kulturhaus Pumpe",
            latitude = 46.6205,
            longitude = 14.3151,
            address = "Radetzkystraße, 9020 Klagenfurt",
            category = "Event",
            averageRating = 4.2,
            geofenceRadiusMeters = 130.0,
            weeklySchedule = mapOf(
                "Montag" to "Geschlossen",
                "Dienstag" to "19:00 - 23:00",
                "Mittwoch" to "19:00 - 23:00",
                "Donnerstag" to "19:00 - 00:00",
                "Freitag" to "19:00 - 02:00",
                "Samstag" to "19:00 - 02:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 34,
            liveVisitors = 56,
            malePercentage = 50,
            femalePercentage = 50,
            searchTags = "Kultur Lesung Konzert DJ Workshop Verein",
            phoneNumber = "+43 463 000009",
            nowMs = nowMs
        ),
        buildClub(
            id = "club_augustin",
            name = "Bierhaus zum Augustin",
            latitude = 46.6249,
            longitude = 14.3089,
            address = "Pfarrhofgasse, 9020 Klagenfurt",
            category = "Pub",
            averageRating = 4.4,
            geofenceRadiusMeters = 70.0,
            weeklySchedule = mapOf(
                "Montag" to "11:00 - 00:00",
                "Dienstag" to "11:00 - 00:00",
                "Mittwoch" to "11:00 - 00:00",
                "Donnerstag" to "11:00 - 01:00",
                "Freitag" to "11:00 - 02:00",
                "Samstag" to "11:00 - 02:00",
                "Sonntag" to "Geschlossen"
            ),
            capacityPercent = 47,
            liveVisitors = 69,
            malePercentage = 56,
            femalePercentage = 44,
            searchTags = "Bier Craft Beer Wirtshaus Altstadt Schnitzel",
            phoneNumber = "+43 463 000010",
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
            id = "evt_volksgarten_technight",
            clubId = "club_volksgarten",
            title = "Wörthersee Techno Night",
            description = "Drei Floors, internationale Gäste und ein Resident-Set bis in die Morgenstunden.",
            startTime = nowMs + 6L * HOUR_MS,
            endTime = nowMs + 13L * HOUR_MS,
            price = "18 EUR",
            time = "22:00 - 05:00",
            searchKeywords = "Techno Rave Floor DJ Klagenfurt",
            capacityLimit = 1200,
            category = "Techno"
        ),
        EventEntity(
            id = "evt_bollwerk_indie",
            clubId = "club_bollwerk",
            title = "Indie Live Session",
            description = "Vier Bands aus Kärnten und Slowenien, danach DJ-Set im Untergeschoss.",
            startTime = nowMs + 1L * DAY_MS + 4L * HOUR_MS,
            endTime = nowMs + 1L * DAY_MS + 10L * HOUR_MS,
            price = "12 EUR",
            time = "21:00 - 03:00",
            searchKeywords = "Indie Live Band Konzert Alternative",
            capacityLimit = 400,
            category = "Live"
        ),
        EventEntity(
            id = "evt_loretto_sundowner",
            clubId = "club_loretto",
            title = "Sundowner am Ostufer",
            description = "Deep House zum Sonnenuntergang, Aperitif-Aktion bis 20:00 Uhr.",
            startTime = nowMs + 2L * DAY_MS + 8L * HOUR_MS,
            endTime = nowMs + 2L * DAY_MS + 14L * HOUR_MS,
            price = "Eintritt frei",
            time = "18:00 - 00:00",
            searchKeywords = "Sundowner Deep House Strandbar Sommer",
            capacityLimit = 600,
            category = "House"
        ),
        EventEntity(
            id = "evt_scotch_charts",
            clubId = "club_scotch",
            title = "Studentennacht",
            description = "Charts und Partyklassiker, ermäßigter Eintritt mit Studierendenausweis.",
            startTime = nowMs + 3L * DAY_MS + 5L * HOUR_MS,
            endTime = nowMs + 3L * DAY_MS + 12L * HOUR_MS,
            price = "8 EUR",
            time = "22:00 - 05:00",
            searchKeywords = "Studenten Charts Party Uni Klagenfurt",
            capacityLimit = 500,
            category = "Charts"
        ),
        EventEntity(
            id = "evt_pumpe_slam",
            clubId = "club_pumpe",
            title = "Poetry Slam Kärnten",
            description = "Offene Bühne mit Publikumsjury, im Anschluss Bar im Foyer.",
            startTime = nowMs + 4L * DAY_MS + 3L * HOUR_MS,
            endTime = nowMs + 4L * DAY_MS + 8L * HOUR_MS,
            price = "9 EUR",
            time = "19:00 - 00:00",
            searchKeywords = "Poetry Slam Kultur Buehne Lesung",
            capacityLimit = 200,
            category = "Kultur"
        )
    )

    /** Liefert Club-Aktionen, die im Event-Info-Block angezeigt werden. */
    fun clubOffers(nowMs: Long = System.currentTimeMillis()): List<ClubOfferEntity> = listOf(
        ClubOfferEntity(
            id = "offer_volksgarten_early",
            clubId = "club_volksgarten",
            title = "Early Bird bis 23:00",
            description = "Reduzierter Eintritt für alle Gäste vor 23:00 Uhr.",
            offerType = "EINTRITT",
            discountPercentage = 30,
            validUntil = nowMs + 7L * DAY_MS,
            isExclusive = true
        ),
        ClubOfferEntity(
            id = "offer_loretto_aperitif",
            clubId = "club_loretto",
            title = "Aperitif zum halben Preis",
            description = "Gilt auf alle Aperitifs von 18:00 bis 20:00 Uhr.",
            offerType = "GETRAENK",
            discountPercentage = 50,
            validUntil = nowMs + 14L * DAY_MS
        ),
        ClubOfferEntity(
            id = "offer_augustin_beer",
            clubId = "club_augustin",
            title = "Craft-Beer-Verkostung",
            description = "Drei regionale Sorten im Probierbrett zum Aktionspreis.",
            offerType = "GETRAENK",
            discountCode = "KLIQ-BEER",
            discountPercentage = 20,
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
    fun feedPosts(nowMs: Long = System.currentTimeMillis()): List<FeedPostEntity> = listOf(
        FeedPostEntity(
            id = "post_kf_1",
            authorUserId = "usr_lena",
            authorName = "Lena P.",
            contentText = "Sonnenuntergang an der Strandbar Loretto. Besser wird der Abend nicht mehr.",
            clubId = "club_loretto",
            clubName = "Strandbar Loretto",
            createdAtMs = nowMs - 25L * MINUTE_MS,
            likeCount = 48,
            commentCount = 2
        ),
        FeedPostEntity(
            id = "post_kf_2",
            authorUserId = "usr_david",
            authorName = "David M.",
            contentText = "Floor 2 im Volksgarten war heute ein Statement. Wer war dabei?",
            clubId = "club_volksgarten",
            clubName = "Volksgarten Klagenfurt",
            createdAtMs = nowMs - 2L * HOUR_MS,
            likeCount = 132,
            commentCount = 1
        ),
        FeedPostEntity(
            id = "post_kf_3",
            authorUserId = "usr_tobias",
            authorName = "Tobias R.",
            contentText = "Vier Bands, ein Abend, kein einziger Leerlauf. Bollwerk bleibt Pflichtprogramm.",
            clubId = "club_bollwerk",
            clubName = "Bollwerk",
            createdAtMs = nowMs - 5L * HOUR_MS,
            likeCount = 61,
            commentCount = 0
        ),
        FeedPostEntity(
            id = "post_kf_4",
            authorUserId = "usr_sarah",
            authorName = "Sarah H.",
            contentText = "Neuer Cocktail in der Duchamp Bar. Absolute Empfehlung für alle Gin-Fans.",
            clubId = "club_duchamp",
            clubName = "Duchamp Bar",
            createdAtMs = nowMs - 9L * HOUR_MS,
            likeCount = 37,
            commentCount = 1
        ),
        FeedPostEntity(
            id = "post_kf_5",
            authorUserId = "usr_nina",
            authorName = "Nina S.",
            contentText = "Poetry Slam im Kulturhaus Pumpe war überraschend gut. Nächsten Monat wieder.",
            clubId = "club_pumpe",
            clubName = "Kulturhaus Pumpe",
            createdAtMs = nowMs - 1L * DAY_MS,
            likeCount = 24,
            commentCount = 0
        )
    )

    /** Liefert die Kommentare zu den Beiträgen aus [feedPosts]. */
    fun feedComments(nowMs: Long = System.currentTimeMillis()): List<FeedCommentEntity> = listOf(
        FeedCommentEntity(
            id = "cmt_kf_1",
            postId = "post_kf_1",
            authorUserId = "usr_sarah",
            authorName = "Sarah H.",
            text = "Sieht traumhaft aus. Bin nächste Woche auch dort.",
            createdAtMs = nowMs - 20L * MINUTE_MS
        ),
        FeedCommentEntity(
            id = "cmt_kf_2",
            postId = "post_kf_1",
            authorUserId = "usr_david",
            authorName = "David M.",
            text = "Ostufer bleibt ungeschlagen.",
            createdAtMs = nowMs - 15L * MINUTE_MS
        ),
        FeedCommentEntity(
            id = "cmt_kf_3",
            postId = "post_kf_2",
            authorUserId = "usr_nina",
            authorName = "Nina S.",
            text = "War da, kann das nur bestätigen.",
            createdAtMs = nowMs - 100L * MINUTE_MS
        ),
        FeedCommentEntity(
            id = "cmt_kf_4",
            postId = "post_kf_4",
            authorUserId = "usr_lena",
            authorName = "Lena P.",
            text = "Wie heißt der Drink genau?",
            createdAtMs = nowMs - 8L * HOUR_MS
        )
    )

    /** Liefert die Storys der Home-Story-Leiste. */
    fun stories(nowMs: Long = System.currentTimeMillis()): List<StoryEntity> = listOf(
        StoryEntity(
            id = "story_kf_1",
            authorUserId = "usr_lena",
            authorName = "Lena",
            headline = "Sundowner am Ostufer",
            clubName = "Strandbar Loretto",
            createdAtMs = nowMs - 18L * MINUTE_MS
        ),
        StoryEntity(
            id = "story_kf_2",
            authorUserId = "usr_david",
            authorName = "David",
            headline = "Floor 2 ist offen",
            clubName = "Volksgarten Klagenfurt",
            createdAtMs = nowMs - 45L * MINUTE_MS
        ),
        StoryEntity(
            id = "story_kf_3",
            authorUserId = "usr_tobias",
            authorName = "Tobias",
            headline = "Soundcheck läuft",
            clubName = "Bollwerk",
            createdAtMs = nowMs - 90L * MINUTE_MS
        ),
        StoryEntity(
            id = "story_kf_4",
            authorUserId = "usr_sarah",
            authorName = "Sarah",
            headline = "Gin Tasting",
            clubName = "Duchamp Bar",
            createdAtMs = nowMs - 3L * HOUR_MS
        ),
        StoryEntity(
            id = "story_kf_5",
            authorUserId = "usr_nina",
            authorName = "Nina",
            headline = "Altstadt-Runde",
            clubName = "Cafe Bar Kamot",
            createdAtMs = nowMs - 6L * HOUR_MS
        ),
        StoryEntity(
            id = "story_kf_6",
            authorUserId = CURRENT_USER_ID,
            authorName = "Du",
            headline = "Letztes Wochenende",
            clubName = "Molly Malone",
            createdAtMs = nowMs - 2L * DAY_MS,
            isSeen = true
        )
    )

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
        websiteUrl: String? = null,
        phoneNumber: String? = null,
        isPromoted: Boolean = false
    ): ClubEntity {
        val calendar = Calendar.getInstance().apply { timeInMillis = nowMs }
        val today = OpeningHoursHelper.getCurrentDayGermanName(calendar)
        val todayHours = weeklySchedule[today] ?: "Geschlossen"
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
            phoneNumber = phoneNumber
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
