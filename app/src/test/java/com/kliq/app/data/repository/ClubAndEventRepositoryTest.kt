package com.kliq.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.model.ExternalClubSearchResultDto
import com.kliq.app.data.remote.model.ExternalEventSearchResultDto
import com.kliq.app.data.remote.model.ExternalSearchResponseDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ClubAndEventRepositoryTest {

    private lateinit var db: KliqDatabase
    private lateinit var clubDao: ClubDao
    private lateinit var eventDao: EventDao
    private lateinit var mockApiService: KliqApiService
    private lateinit var repository: ClubAndEventRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        clubDao = db.clubDao()
        eventDao = db.eventDao()
        mockApiService = mock(KliqApiService::class.java)
        repository = ClubAndEventRepositoryImpl(clubDao, eventDao, mockApiService)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testUnifiedClubAndEventSingleSourceOfTruth() = runTest {
        val clubEntity = ClubEntity(
            id = "club_watergate",
            name = "Watergate",
            latitude = 52.50,
            longitude = 13.44,
            address = "Falckensteinstr. 49",
            geofenceRadiusMeters = 200.0,
            averageRating = 4.8,
            isFavorite = true,
            category = "Techno",
            rating = 4.8f,
            imageUrl = "https://kliq.de/watergate.jpg",
            region = "Berlin"
        )
        val eventEntity = EventEntity(
            id = "event_night",
            clubId = "club_watergate",
            title = "Night Session",
            description = "Deep House & Techno",
            startTime = 1784600000000L,
            endTime = 1784636000000L,
            price = "15€"
        )

        clubDao.insertClubs(listOf(clubEntity))
        eventDao.insertEvents(listOf(eventEntity))

        val clubs = repository.getClubs().first()
        val events = repository.getEventsForClub("club_watergate").first()

        assertEquals(1, clubs.size)
        assertEquals("Watergate", clubs[0].name)
        assertTrue(clubs[0].isFavorite)

        assertEquals(1, events.size)
        assertEquals("Night Session", events[0].title)
    }

    @Test
    fun testCombinedLocalSearch() = runTest {
        val clubEntity = ClubEntity(
            id = "c_tresor",
            name = "Tresor Berlin",
            latitude = 52.51,
            longitude = 13.41,
            address = "Köpenicker Str. 70",
            category = "Techno",
            rating = 4.9f,
            region = "Berlin",
            externalSearchTags = "techno, vault"
        )
        val eventEntity = EventEntity(
            id = "e_tresor_rave",
            clubId = "c_tresor",
            title = "Tresor Rave Night",
            description = "Rave",
            startTime = 1784600000000L,
            endTime = 1784636000000L,
            price = "20€",
            searchKeywords = "rave, techno"
        )

        clubDao.insertClubs(listOf(clubEntity))
        eventDao.insertEvents(listOf(eventEntity))

        val (clubs, events) = repository.searchClubsAndEventsLocal("Tresor").first()

        assertEquals(1, clubs.size)
        assertEquals("Tresor Berlin", clubs[0].name)
        assertEquals(1, events.size)
        assertEquals("Tresor Rave Night", events[0].title)
    }

    @Test
    fun testRemoteBackgroundSyncUpdatesLocalCache() = runTest {
        val remoteClubDto = ExternalClubSearchResultDto(
            placeId = "c_remote_1",
            name = "KitKatClub",
            category = "Electro",
            rating = 4.7,
            imageUrl = "https://kliq.de/kitkat.jpg",
            address = "Köpenicker Str. 76",
            latitude = 52.51,
            longitude = 13.41,
            isOpenNow = true,
            openingHoursText = "22:00 - 08:00",
            searchTags = listOf("electro", "fetish")
        )
        val remoteEventDto = ExternalEventSearchResultDto(
            eventId = "e_remote_1",
            clubPlaceId = "c_remote_1",
            title = "Gegen Night",
            description = "Electronic Art Party",
            ticketPrice = "18€",
            startTimestamp = 1784600000000L,
            endTimestamp = 1784636000000L,
            keywords = listOf("gegen", "art")
        )
        val responseDto = ExternalSearchResponseDto(
            clubs = listOf(remoteClubDto),
            events = listOf(remoteEventDto)
        )

        `when`(mockApiService.searchExternalClubsAndEvents("")).thenReturn(responseDto)

        val syncResult = repository.syncClubsAndEventsFromRemote()
        assertTrue(syncResult.isSuccess)

        val cachedClubs = repository.getClubs().first()
        val cachedEvents = repository.getEvents().first()

        assertEquals(1, cachedClubs.size)
        assertEquals("KitKatClub", cachedClubs[0].name)
        assertEquals(1, cachedEvents.size)
        assertEquals("Gegen Night", cachedEvents[0].title)
    }
}
