package com.kliq.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.util.AntiSpamReviewValidator
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
class AsyncDatabaseOperationTest {

    private lateinit var db: KliqDatabase
    private lateinit var clubDao: ClubDao
    private lateinit var eventDao: EventDao
    private lateinit var chatDao: ChatDao
    private lateinit var reviewDao: ReviewDao
    private lateinit var userDao: UserDao

    private lateinit var clubAndEventRepository: ClubAndEventRepositoryImpl
    private lateinit var chatRepository: ChatRepositoryImpl
    private lateinit var reviewRepository: ReviewRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        clubDao = db.clubDao()
        eventDao = db.eventDao()
        chatDao = db.chatDao()
        reviewDao = db.reviewDao()
        userDao = db.userDao()

        val antiSpamValidator = AntiSpamReviewValidator()

        clubAndEventRepository = ClubAndEventRepositoryImpl(clubDao, eventDao, null)
        chatRepository = ChatRepositoryImpl(chatDao, null)
        reviewRepository = ReviewRepositoryImpl(reviewDao, clubDao, antiSpamValidator, null)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testAsyncClubAndEventFlowStreams() = runTest {
        val club = ClubEntity(
            id = "club_async_1",
            name = "Berghain Async",
            latitude = 52.51,
            longitude = 13.44,
            address = "Am Wriezener Bahnhof",
            geofenceRadiusMeters = 150.0,
            averageRating = 4.9,
            isFavorite = true,
            category = "Techno",
            rating = 4.9f,
            region = "Berlin"
        )
        clubDao.insertClub(club)

        val clubsFlow = clubAndEventRepository.getClubs().first()
        assertEquals(1, clubsFlow.size)
        assertEquals("Berghain Async", clubsFlow[0].name)
    }

    @Test
    fun testAsyncChatCreationAndMessageSending() = runTest {
        val chat = ChatEntity(
            id = "chat_async_1",
            name = "Berlin Tech Group",
            cityRegion = "Berlin",
            lastMessageText = "",
            lastMessageTimestampMs = System.currentTimeMillis(),
            lastMessageTimestampIso = "2026-07-22T14:00:00Z",
            avatarInitial = "B",
            chatType = ChatType.PUBLIC_CITY
        )
        chatDao.insertChat(chat)

        val sendResult = chatRepository.sendTextMessage(
            chatId = "chat_async_1",
            senderUserId = "user_123",
            senderName = "Alex",
            text = "Hallo aus dem Async Room!"
        )

        assertTrue(sendResult.isSuccess)
        val message = sendResult.getOrNull()
        assertNotNull(message)
        assertEquals("Hallo aus dem Async Room!", message?.text)

        val messages = chatRepository.getMessagesForChat("chat_async_1").first()
        assertEquals(1, messages.size)
        assertEquals("Hallo aus dem Async Room!", messages[0].text)
    }

    @Test
    fun testAsyncReviewSubmissionWithAntiSpamValidation() = runTest {
        val user = UserEntity(
            id = "user_777",
            username = "Alex",
            email = "alex@kliq.de",
            profilePictureUrl = null,
            bio = "Party Goer"
        )
        userDao.insertUser(user)

        val club = ClubEntity(
            id = "club_review_1",
            name = "Ritter Butzke",
            latitude = 52.50,
            longitude = 13.41,
            address = "Lobeckstr. 30",
            geofenceRadiusMeters = 300.0,
            averageRating = 4.5,
            isFavorite = false,
            category = "House",
            rating = 4.5f,
            region = "Berlin"
        )
        clubDao.insertClub(club)

        val reviewResult = reviewRepository.submitReviewWithGpsCheck(
            reviewerUserId = "user_777",
            clubId = "club_review_1",
            rating = 5,
            text = "Super Location & Sound!",
            userLat = 52.5001,
            userLon = 13.4101
        )

        assertTrue(reviewResult.isSuccess)
        val review = reviewResult.getOrNull()
        assertNotNull(review)
        assertTrue(review?.isVerified == true)

        val reviews = reviewRepository.getReviewsForClub("club_review_1").first()
        assertEquals(1, reviews.size)
        assertEquals("Super Location & Sound!", reviews[0].text)
    }
}
