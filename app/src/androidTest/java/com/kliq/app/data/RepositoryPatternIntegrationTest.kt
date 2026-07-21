package com.kliq.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.model.ExternalClubSearchResultDto
import com.kliq.app.data.remote.model.ExternalSearchResponseDto
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.data.repository.ClubAndEventRepositoryImpl
import com.kliq.app.data.repository.ReviewRepositoryImpl
import com.kliq.app.data.repository.UserRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RepositoryPatternIntegrationTest {

    private lateinit var db: KliqDatabase
    private lateinit var mockApiService: KliqApiService
    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var clubAndEventRepository: ClubAndEventRepositoryImpl
    private lateinit var reviewRepository: ReviewRepositoryImpl
    private lateinit var chatRepository: ChatRepositoryImpl

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        mockApiService = mock(KliqApiService::class.java)
        userRepository = UserRepositoryImpl(db.userDao(), mockApiService)
        clubAndEventRepository = ClubAndEventRepositoryImpl(db.clubDao(), db.eventDao(), mockApiService)
        reviewRepository = ReviewRepositoryImpl(db.reviewDao(), db.clubDao(), AntiSpamReviewValidator(), mockApiService)
        chatRepository = ChatRepositoryImpl(db.chatDao(), mockApiService)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testOfflineFirstCacheHitWhenRoomContainsData() = runBlocking {
        // 1. Vorbefüllen der lokalen Room-Datenbank (Local-Cache)
        val clubId = "club_berghain_cache"
        val cachedUser = UserEntity("usr_offline_1", "techno_fan", "fan@kliq.de", null, "Bio")
        val cachedClub = ClubEntity(id = clubId, name = "Berghain / Panorama Bar", category = "Techno", rating = 4.9f, region = "Berlin")
        val cachedEvent = EventEntity(id = "event_klubnacht", clubId = clubId, title = "Klubnacht", description = "Techno Rave", price = "25€")

        db.userDao().insertUser(cachedUser)
        db.clubDao().insertClubs(listOf(cachedClub))
        db.eventDao().insertEvents(listOf(cachedEvent))

        // 2. Abfragen über das Repository (Single Source of Truth - Offline-First Cache Hit)
        val user = userRepository.getUserById("usr_offline_1").first()
        val clubs = clubAndEventRepository.getClubs().first()
        val events = clubAndEventRepository.getEventsForClub(clubId).first()

        // 3. Verifikation der lokal geladenen Daten ohne Netzwerkaufruf
        assertNotNull("Benutzer muss aus dem Raum-Cache geladen werden", user)
        assertEquals("techno_fan", user?.username)
        assertEquals(1, clubs.size)
        assertEquals("Berghain / Panorama Bar", clubs[0].name)
        assertEquals(1, events.size)
        assertEquals("Klubnacht", events[0].title)
    }

    @Test
    fun testEmptyLocalStorageTriggersRemoteSyncAndPersistsToRoom() = runBlocking {
        // 1. Speicher ist initial leer
        val userId = "usr_remote_new"
        `when`(mockApiService.getUserProfile(userId)).thenReturn(
            UserEntity(userId, "new_user_remote", "new@kliq.de", "https://kliq.de/pic.jpg", "New Bio")
        )

        val remoteClubDto = ExternalClubSearchResultDto(
            placeId = "club_remote_kitkat",
            name = "KitKatClub",
            category = "Electro",
            rating = 4.8,
            imageUrl = "https://kliq.de/kitkat.jpg",
            address = "Köpenicker Str. 76",
            latitude = 52.51,
            longitude = 13.41
        )
        `when`(mockApiService.searchExternalClubsAndEvents("")).thenReturn(
            ExternalSearchResponseDto(clubs = listOf(remoteClubDto), events = emptyList())
        )

        // 2. Repository-Sync ausführen
        val userSyncResult = userRepository.syncUserProfile(userId)
        val clubSyncResult = clubAndEventRepository.syncClubsAndEventsFromRemote()

        assertTrue(userSyncResult.isSuccess)
        assertTrue(clubSyncResult.isSuccess)

        // 3. Verifikation: Daten sind lokal in Room persistiert und über den Stream abrufbar
        val syncedUser = userRepository.getUserById(userId).first()
        val syncedClubs = clubAndEventRepository.getClubs().first()

        assertNotNull("Persistierter Remote-User muss im Cache vorhanden sein", syncedUser)
        assertEquals("new_user_remote", syncedUser?.username)
        assertEquals(1, syncedClubs.size)
        assertEquals("KitKatClub", syncedClubs[0].name)
    }

    @Test
    fun testNetworkFailureFallbackToCachedData() = runBlocking {
        // 1. Vorbefüllen der lokalen Datenbank mit gecachten Daten
        val userId = "usr_fallback"
        val initialUser = UserEntity(userId, "cached_alex", "alex@kliq.de", null, "Gecachte Bio")
        db.userDao().insertUser(initialUser)

        // 2. Simulieren eines Netzwerkausfalls (IOException)
        `when`(mockApiService.getUserProfile(userId)).thenThrow(RuntimeException("Netzwerkverbindung fehlgeschlagen"))

        // 3. Aufruf der Sync-Funktion bei Netzwerkausfall
        val syncResult = userRepository.syncUserProfile(userId)
        assertFalse("Sync muss bei Netzwerkausfall fehlschlagen", syncResult.isSuccess)

        // 4. Verifikation: Repository greift weiterhin stabil auf die gecachten Daten zu (Fallback)
        val cachedUser = userRepository.getUserById(userId).first()
        assertNotNull("Gecachter Benutzer muss trotz Netzwerkausfall verfügbar sein", cachedUser)
        assertEquals("cached_alex", cachedUser?.username)
    }
}
