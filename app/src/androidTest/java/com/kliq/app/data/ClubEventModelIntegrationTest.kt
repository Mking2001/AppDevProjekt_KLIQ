package com.kliq.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.SpecialOffer
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toDomain
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toEntity
import com.kliq.app.data.remote.model.ExternalClubSearchResultDto
import com.kliq.app.data.remote.model.ExternalEventSearchResultDto
import com.kliq.app.data.remote.model.ExternalSearchResponseDto
import com.kliq.app.data.remote.model.ExternalSpecialOfferDto
import com.kliq.app.ui.model.HighContrastVioletPalette
import com.kliq.app.ui.model.toHighContrastUiState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ClubEventModelIntegrationTest {

    private lateinit var db: KliqDatabase
    private lateinit var clubDao: ClubDao
    private lateinit var eventDao: EventDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        clubDao = db.clubDao()
        eventDao = db.eventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testClubAndEventLocalPersistence() = runBlocking {
        // 1. Instanziieren und lokales Speichern eines Club-Objekts
        val clubId = "club_berghain_001"
        val clubEntity = ClubEntity(
            id = clubId,
            name = "Berghain / Panorama Bar",
            latitude = 52.5112,
            longitude = 13.4432,
            address = "Am Wriezener Bahnhof, 10243 Berlin",
            geofenceRadiusMeters = 300.0,
            averageRating = 4.8,
            openingHoursJson = "{\"isOpenNow\":true,\"todayHours\":\"23:59 - 12:00\"}",
            isFavorite = true,
            category = "Techno",
            imageUrl = "https://kliq-app.de/images/berghain.jpg",
            region = "Berlin",
            currentCapacityPercent = 85,
            totalLiveVisitors = 1420
        )
        clubDao.insertClub(clubEntity)

        val retrievedClub = clubDao.getClubById(clubId).first()
        assertNotNull(retrievedClub)
        assertEquals("Berghain / Panorama Bar", retrievedClub?.name)
        assertEquals(52.5112, retrievedClub?.latitude ?: 0.0, 0.0001)
        assertEquals(13.4432, retrievedClub?.longitude ?: 0.0, 0.0001)
        assertEquals(300.0, retrievedClub?.geofenceRadiusMeters ?: 0.0, 0.001)
        assertTrue(retrievedClub?.isFavorite == true)

        // 2. Instanziieren und Speichern des zugehörigen Event-Objekts
        val eventId = "evt_klubnacht_2026"
        val eventEntity = EventEntity(
            id = eventId,
            clubId = clubId,
            title = "Klubnacht Summer Special",
            description = "Melodic techno & industrial electronic sets",
            startTime = 1784592000000L,
            endTime = 1784635200000L,
            price = "25 €",
            specialOffersJson = "[{\"id\":\"so_1\",\"title\":\"Early Bird Shot\",\"discountDescription\":\"Freier Welcome-Shot vor 00:00 Uhr\"}]",
            searchKeywords = "techno, berghain, night",
            imageUrl = "https://kliq-app.de/events/klubnacht.jpg"
        )
        eventDao.insertEvent(eventEntity)

        val retrievedEvents = eventDao.getEventsByClubId(clubId).first()
        assertEquals(1, retrievedEvents.size)
        val event = retrievedEvents[0]
        assertEquals("Klubnacht Summer Special", event.title)
        assertEquals(clubId, event.clubId)
        assertTrue(event.specialOffersJson.contains("Early Bird Shot"))
    }

    @Test
    fun testExternalApiSearchMappingAndDatabaseSync() = runBlocking {
        // Simulieren externer API-Suchergebnisse (Internet-Suche Integration)
        val offerDto = ExternalSpecialOfferDto(
            offerId = "ext_off_10",
            title = "Students 2-for-1",
            discountDescription = "2 Tickets zum Preis von 1 mit Studentenausweis"
        )
        val externalClubDto = ExternalClubSearchResultDto(
            placeId = "ext_club_watergate",
            name = "Watergate Club",
            address = "Falckensteinstraße 49, 10997 Berlin",
            latitude = 52.5008,
            longitude = 13.4437,
            rating = 4.6,
            geofenceRadius = 250.0,
            isOpenNow = true,
            openingHoursText = "23:00 - 06:00",
            category = "Deep House / Minimal",
            imageUrl = "https://kliq-app.de/images/watergate.jpg",
            websiteUrl = "https://water-gate.de",
            searchTags = listOf("watergate", "spree", "berlin", "house")
        )
        val externalEventDto = ExternalEventSearchResultDto(
            eventId = "ext_evt_watergate_01",
            clubPlaceId = "ext_club_watergate",
            title = "Watergate Night",
            description = "Deep house on the riverfront floor",
            startTimestamp = 1784600000000L,
            endTimestamp = 1784630000000L,
            ticketPrice = "15 €",
            specialOffers = listOf(offerDto),
            keywords = listOf("deep house", "watergate", "oberbaumbrücke")
        )
        val apiResponse = ExternalSearchResponseDto(
            clubs = listOf(externalClubDto),
            events = listOf(externalEventDto),
            query = "Watergate Berlin",
            totalResults = 1
        )

        // Mapping externer DTOs in lokale Entitäten & Speichern in Room
        val mappedClubEntity = apiResponse.clubs[0].toEntity(isFavorite = false)
        val mappedEventEntity = apiResponse.events[0].toEntity()

        clubDao.insertClub(mappedClubEntity)
        eventDao.insertEvent(mappedEventEntity)

        // Verifizieren der Datenbank-Integration
        val dbClub = clubDao.getClubById("ext_club_watergate").first()
        assertNotNull(dbClub)
        assertEquals("Watergate Club", dbClub?.name)
        assertEquals("watergate, spree, berlin, house", dbClub?.externalSearchTags)

        val dbEvents = eventDao.getEventsByClubId("ext_club_watergate").first()
        assertEquals(1, dbEvents.size)
        assertEquals("Watergate Night", dbEvents[0].title)

        // Verifizieren des Mappings in das High-Contrast UI-Modell
        val domainClub = apiResponse.clubs[0].toDomain(isFavorite = true)
        val uiState = domainClub.toHighContrastUiState(userLat = 52.5010, userLon = 13.4440)
        assertEquals("Watergate Club", uiState.name)
        assertTrue(uiState.isOpenNow)
        assertEquals(HighContrastVioletPalette.BadgeOpenGreen, uiState.openStatusBadgeColorHex)
        assertTrue(uiState.distanceFormatted.contains("km"))
    }

    @Test
    fun testGpsCoordinatesAndGeofenceValidation() {
        val clubLocation = GpsLocation(
            latitude = 52.5112,
            longitude = 13.4432,
            address = "Am Wriezener Bahnhof, 10243 Berlin"
        )
        val geofenceRadiusMeters = 300.0

        // Benutzer innerhalb des Radius (~100m Entfernung)
        val userLatInside = 52.5118
        val userLonInside = 13.4435
        val distanceInsideMeters = calculateDistanceMeters(
            userLatInside, userLonInside, clubLocation.latitude, clubLocation.longitude
        )
        assertTrue("Benutzer sollte innerhalb des Geofencing-Radius liegen", distanceInsideMeters <= geofenceRadiusMeters)

        // Benutzer außerhalb des Radius (~5 km Entfernung)
        val userLatOutside = 52.5400
        val userLonOutside = 13.4000
        val distanceOutsideMeters = calculateDistanceMeters(
            userLatOutside, userLonOutside, clubLocation.latitude, clubLocation.longitude
        )
        assertTrue("Benutzer sollte außerhalb des Geofencing-Radius liegen", distanceOutsideMeters > geofenceRadiusMeters)
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
