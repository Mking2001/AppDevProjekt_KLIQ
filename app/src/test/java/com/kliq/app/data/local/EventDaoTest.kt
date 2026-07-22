package com.kliq.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.SpecialOffer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class EventDaoTest {
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
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndQueryClubWithGpsAndGeofence() = runTest {
        val club = ClubEntity(
            id = "c_berlin_1",
            name = "Watergate Club",
            latitude = 52.5008,
            longitude = 13.4437,
            address = "Falckensteinstraße 49, 10997 Berlin",
            geofenceRadiusMeters = 250.0,
            averageRating = 4.7,
            openingHoursJson = "{\"isOpenNow\":true,\"todayHours\":\"23:00 - 08:00\"}",
            isFavorite = true,
            category = "Techno",
            rating = 4.7f,
            imageUrl = "https://example.com/watergate.jpg",
            region = "Berlin"
        )

        clubDao.insertClub(club)

        val retrieved = clubDao.getClubById("c_berlin_1").first()
        assertNotNull(retrieved)
        assertEquals(52.5008, retrieved!!.latitude, 0.0001)
        assertEquals(13.4437, retrieved.longitude, 0.0001)
        assertEquals(250.0, retrieved.geofenceRadiusMeters, 0.001)
        assertEquals(4.7, retrieved.averageRating, 0.01)
        assertTrue(retrieved.isFavorite)
    }

    @Test
    fun testEventCrudAndSpecialOffers() = runTest {
        val club = ClubEntity(
            id = "c_munich_1",
            name = "Rote Sonne",
            latitude = 48.1391,
            longitude = 11.5653,
            address = "Maximilianspl. 5, 80333 München",
            geofenceRadiusMeters = 200.0,
            averageRating = 4.5,
            isFavorite = false
        )
        clubDao.insertClub(club)

        val converters = RoomConverters()
        val offers = listOf(
            SpecialOffer("o1", "Early Bird", "Free shot until 00:00"),
            SpecialOffer("o2", "Student Discount", "50% off ticket price")
        )
        val offersJson = converters.fromSpecialOffersList(offers)

        val event = EventEntity(
            id = "ev_101",
            clubId = "c_munich_1",
            title = "Midnight Odyssey",
            description = "Deep house and melodic techno night",
            startTime = 1700000000000L,
            endTime = 1700028800000L,
            price = "15 €",
            specialOffersJson = offersJson,
            searchKeywords = "techno, deep house, munich"
        )
        eventDao.insertEvent(event)

        val retrievedEvents = eventDao.getEventsByClubId("c_munich_1").first()
        assertEquals(1, retrievedEvents.size)

        val retrievedEvent = retrievedEvents[0]
        assertEquals("Midnight Odyssey", retrievedEvent.title)
        assertEquals(1700000000000L, retrievedEvent.startTime)

        val parsedOffers = converters.toSpecialOffersList(retrievedEvent.specialOffersJson)
        assertEquals(2, parsedOffers.size)
        assertEquals("Early Bird", parsedOffers[0].title)
    }
}
