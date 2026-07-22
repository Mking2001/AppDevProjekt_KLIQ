package com.kliq.app.data

import android.content.Context
import android.os.Looper
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.data.repository.ClubAndEventRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AsyncDatabaseConcurrencyAndroidTest {

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
     * 1. Asynchroner Datenabruf & Reaktives Streaming:
     * Verifiziert, dass Datenbankabfragen auf Hintergrund-Threads ablaufen
     * und reaktive Flows Ergebnisse sicher an den Consumer streamen.
     */
    @Test
    fun test1_asynchronousDataQueryAndReactiveStreaming() = runBlocking {
        val club = ClubEntity(
            id = "club_async_stream_1",
            name = "Watergate Async Stream",
            latitude = 52.50,
            longitude = 13.44,
            address = "Falckensteinstr. 49",
            geofenceRadiusMeters = 200.0,
            averageRating = 4.8,
            isFavorite = true,
            category = "Techno",
            rating = 4.8f,
            region = "Berlin"
        )
        db.clubDao().insertClubs(listOf(club))

        var executionThreadName = ""
        val clubs = withContext(Dispatchers.IO) {
            executionThreadName = Thread.currentThread().name
            clubAndEventRepository.getClubs().first()
        }

        assertNotNull("Gestreamte Club-Liste darf nicht null sein", clubs)
        assertEquals(1, clubs.size)
        assertEquals("Watergate Async Stream", clubs[0].name)
        assertNotEquals(
            "Datenbank-Query darf nicht auf dem Haupt-Thread ausgeführt werden",
            Looper.getMainLooper().thread.name,
            executionThreadName
        )
    }

    /**
     * 2. Thread-Sicherheit & Paralleles Schreiben unter hoher Last:
     * Überprüft, dass das gleichzeitige Schreiben von 100 Chat-Einträgen
     * über Coroutines auf IO-Dispatchern keinen Deadlock verursacht.
     */
    @Test
    fun test2_parallelWriteThreadSafetyAndNoDeadlocks() = runBlocking {
        val chat = ChatEntity(
            id = "chat_concurrent_100",
            name = "High Load Test Group",
            cityRegion = "Berlin",
            lastMessageText = "",
            lastMessageTimestampMs = System.currentTimeMillis(),
            lastMessageTimestampIso = "2026-07-22T15:00:00Z",
            avatarInitial = "H",
            chatType = ChatType.PUBLIC_CITY
        )
        db.chatDao().insertChat(chat)

        val totalParallelWrites = 100

        val sendResults = coroutineScope {
            (1..totalParallelWrites).map { index ->
                async(Dispatchers.IO) {
                    chatRepository.sendTextMessage(
                        chatId = "chat_concurrent_100",
                        senderUserId = "user_$index",
                        senderName = "User $index",
                        text = "Parallele Nachricht #$index"
                    )
                }
            }.awaitAll()
        }

        assertEquals(totalParallelWrites, sendResults.size)
        assertTrue(
            "Alle 100 parallelen Schreibvorgänge müssen erfolgreich sein",
            sendResults.all { it.isSuccess }
        )

        val storedMessages = chatRepository.getMessagesForChat("chat_concurrent_100").first()
        assertEquals(
            "Es müssen exakt 100 Nachrichten ohne Datenverlust in Room persistiert sein",
            totalParallelWrites,
            storedMessages.size
        )
    }

    /**
     * 3. Test-Dispatcher & Virtuelle Zeit-Mechanismen:
     * Verifiziert simulierte Latenzen und Timeouts deterministisch via currentTime
     * und virtueller Zeit in runTest.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun test3_virtualTimeAndDeterministicTimeoutValidation() = runTest {
        val chat = ChatEntity(
            id = "chat_virtual_time",
            name = "Virtual Time Room",
            cityRegion = "Berlin",
            lastMessageText = "",
            lastMessageTimestampMs = 1784600000000L,
            lastMessageTimestampIso = "2026-07-22T15:00:00Z",
            avatarInitial = "V",
            chatType = ChatType.PRIVATE
        )
        db.chatDao().insertChat(chat)

        val startTimeMs = testScheduler.currentTime
        delay(5000) // Virtuelle 5-Sekunden-Latenz

        val sendResult = chatRepository.sendTextMessage(
            chatId = "chat_virtual_time",
            senderUserId = "user_virtual",
            senderName = "Virtual User",
            text = "Verzögerte Nachricht nach 5s"
        )
        val endTimeMs = testScheduler.currentTime

        assertTrue(sendResult.isSuccess)
        assertEquals("Virtuelle Zeit muss exakt um 5000ms vorangeschritten sein", 5000L, endTimeMs - startTimeMs)

        val messages = chatRepository.getMessagesForChat("chat_virtual_time").first()
        assertEquals(1, messages.size)
        assertEquals("Verzögerte Nachricht nach 5s", messages[0].text)
    }
}
