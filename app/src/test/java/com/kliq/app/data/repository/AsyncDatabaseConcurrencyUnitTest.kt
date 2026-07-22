package com.kliq.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.model.ChatType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AsyncDatabaseConcurrencyUnitTest {

    private lateinit var db: KliqDatabase
    private lateinit var clubAndEventRepository: ClubAndEventRepositoryImpl
    private lateinit var chatRepository: ChatRepositoryImpl

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        clubAndEventRepository = ClubAndEventRepositoryImpl(db.clubDao(), db.eventDao(), null)
        chatRepository = ChatRepositoryImpl(db.chatDao(), null)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * 1. Asynchroner Datenabruf & Reaktives Streaming via Flow
     */
    @Test
    fun test1_asynchronousDataQueryAndReactiveStreaming() = runBlocking {
        val club = ClubEntity(
            id = "club_unit_async_1",
            name = "KitKat Async Stream",
            latitude = 52.51,
            longitude = 13.41,
            address = "Köpenicker Str. 76",
            geofenceRadiusMeters = 150.0,
            averageRating = 4.7,
            isFavorite = true,
            category = "Electro",
            rating = 4.7f,
            region = "Berlin"
        )
        db.clubDao().insertClubs(listOf(club))

        val clubs = withContext(Dispatchers.IO) {
            clubAndEventRepository.getClubs().first()
        }

        assertNotNull("Gestreamte Club-Liste darf nicht null sein", clubs)
        assertEquals(1, clubs.size)
        assertEquals("KitKat Async Stream", clubs[0].name)
    }

    /**
     * 2. Thread-Sicherheit & Paralleles Schreiben von 100 Datensätzen
     */
    @Test
    fun test2_parallelWriteThreadSafetyAndNoDeadlocks() = runBlocking {
        val chat = ChatEntity(
            id = "chat_unit_100",
            name = "Parallel Test Room",
            cityRegion = "Berlin",
            lastMessageText = "",
            lastMessageTimestampMs = System.currentTimeMillis(),
            lastMessageTimestampIso = "2026-07-22T15:00:00Z",
            avatarInitial = "P",
            chatType = ChatType.PUBLIC_CITY
        )
        db.chatDao().insertChat(chat)

        val totalParallelWrites = 100

        val sendResults = coroutineScope {
            (1..totalParallelWrites).map { index ->
                async(Dispatchers.IO) {
                    chatRepository.sendTextMessage(
                        chatId = "chat_unit_100",
                        senderUserId = "user_$index",
                        senderName = "User $index",
                        text = "Parallele Test-Nachricht #$index"
                    )
                }
            }.awaitAll()
        }

        assertEquals(totalParallelWrites, sendResults.size)
        assertTrue(
            "Alle 100 parallelen Schreibvorgänge müssen erfolgreich abgeschlossen werden",
            sendResults.all { it.isSuccess }
        )

        val storedMessages = chatRepository.getMessagesForChat("chat_unit_100").first()
        assertEquals(
            "Es müssen exakt 100 Nachrichten ohne Deadlock oder Verlust persistiert sein",
            totalParallelWrites,
            storedMessages.size
        )
    }

    /**
     * 3. Test-Dispatcher & Virtuelle Zeit-Mechanismen (runTest & virtual time)
     */
    @Test
    fun test3_virtualTimeAndDeterministicTimeoutValidation() = runTest {
        val chat = ChatEntity(
            id = "chat_virtual_unit",
            name = "Virtual Scheduler Room",
            cityRegion = "Berlin",
            lastMessageText = "",
            lastMessageTimestampMs = 1784600000000L,
            lastMessageTimestampIso = "2026-07-22T15:00:00Z",
            avatarInitial = "V",
            chatType = ChatType.PRIVATE
        )
        db.chatDao().insertChat(chat)

        val startTimeMs = testScheduler.currentTime
        delay(3000) // Virtuelle 3-Sekunden-Verzögerung ohne reale Wartezeit

        val sendResult = chatRepository.sendTextMessage(
            chatId = "chat_virtual_unit",
            senderUserId = "user_virtual",
            senderName = "Virtual User",
            text = "Verzögerte Nachricht nach 3s"
        )
        val endTimeMs = testScheduler.currentTime

        assertTrue(sendResult.isSuccess)
        assertEquals("Virtuelle Zeit muss exakt um 3000ms vorangeschritten sein", 3000L, endTimeMs - startTimeMs)

        val messages = chatRepository.getMessagesForChat("chat_virtual_unit").first()
        assertEquals(1, messages.size)
        assertEquals("Verzögerte Nachricht nach 3s", messages[0].text)
    }
}
