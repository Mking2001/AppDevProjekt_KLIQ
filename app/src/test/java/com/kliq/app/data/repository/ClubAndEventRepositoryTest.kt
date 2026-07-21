package com.kliq.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.remote.KliqApiService
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
        val clubEntity = ClubEntity("club_watergate", "Watergate", "Techno", 4.8f, "https://kliq.de/watergate.jpg", "Berlin", isFavorite = true)
        val eventEntity = EventEntity("event_night", "club_watergate", "Night Session", "Deep House & Techno", "15€", "23:00")

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
        val clubEntity = ClubEntity("c_tresor", "Tresor Berlin", "Techno", 4.9f, "", "Berlin", externalSearchTags = "techno, vault")
        val eventEntity = EventEntity("e_tresor_rave", "c_tresor", "Tresor Rave Night", "Rave", "20€", "23:59", searchKeywords = "rave, techno")

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
        val remoteClubs = listOf(
            com.kliq.app.data.model.Club(
                id = "c_remote_1",
                name = "KitKatClub",
                category = "Electro",
                rating = 4.7f,
                imageUrl = "https://kliq.de/kitkat.jpg",
                region = "Berlin",
                location = com.kliq.app.data.model.GpsLocation(52.51, 13.41)
            )
        )
        val remoteEvents = listOf(
            com.kliq.app.data.model.Event(
                id = "e_remote_1",
                clubId = "c_remote_1",
                title = "Gegen Night",
                description = "Electronic Art Party",
                price = "18€",
                time = "22:00"
            )
        )

        `when`(mockApiService.getClubs()).thenReturn(remoteClubs)
        `when`(mockApiService.getEvents()).thenReturn(remoteEvents)

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
