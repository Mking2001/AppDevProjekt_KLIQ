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
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.data.repository.ClubAndEventRepositoryImpl
import com.kliq.app.data.repository.ReviewRepositoryImpl
import com.kliq.app.data.repository.UserRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
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
class RepositoryPatternIntegrationTest {

    private lateinit var db: KliqDatabase
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

        userRepository = UserRepositoryImpl(db.userDao(), mockApiService = null)
        clubAndEventRepository = ClubAndEventRepositoryImpl(db.clubDao(), db.eventDao(), mockApiService = null)
        reviewRepository = ReviewRepositoryImpl(db.reviewDao(), db.clubDao(), AntiSpamReviewValidator())
        chatRepository = ChatRepositoryImpl(db.chatDao())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testUserRepositorySingleSourceOfTruth() = runBlocking {
        val userEntity = UserEntity("usr_repo_1", "max_mustermann", "max@kliq.de", null, "Bio Text")
        userRepository.saveUser(userEntity)

        val retrievedUser = userRepository.getUserById("usr_repo_1").first()
        assertNotNull(retrievedUser)
        assertEquals("max_mustermann", retrievedUser?.username)
        assertEquals("max@kliq.de", retrievedUser?.email)
    }

    @Test
    fun testClubAndEventRepositoryUnifiedFlows() = runBlocking {
        val clubId = "club_matrix"
        db.clubDao().insertClubs(
            listOf(
                ClubEntity(id = clubId, name = "Matrix Berlin", category = "Dance", rating = 4.5f, imageUrl = "", region = "Berlin")
            )
        )
        db.eventDao().insertEvents(
            listOf(
                EventEntity(id = "event_matrix_sat", clubId = clubId, title = "Saturday Night", description = "Chart Hits", price = "12€", time = "22:00")
            )
        )

        val clubs = clubAndEventRepository.getClubs().first()
        val events = clubAndEventRepository.getEventsForClub(clubId).first()

        assertEquals(1, clubs.size)
        assertEquals("Matrix Berlin", clubs[0].name)
        assertEquals(1, events.size)
        assertEquals("Saturday Night", events[0].title)
    }

    @Test
    fun testReviewRepositoryOfflineFirstSubmission() = runBlocking {
        val userId = "usr_rev_tester"
        val clubId = "club_subway"
        db.userDao().insertUser(UserEntity(userId, "reviewer", "rev@kliq.de", null, null))
        db.clubDao().insertClub(ClubEntity(id = clubId, name = "Subway Club"))

        val submitResult = reviewRepository.submitUnverifiedReview(
            reviewerUserId = userId,
            clubId = clubId,
            rating = 5,
            text = "Klasse Sound & Beleuchtung!"
        )

        assertTrue(submitResult.isSuccess)
        val review = submitResult.getOrNull()
        assertNotNull(review)
        assertEquals(5, review?.rating)

        val clubReviews = reviewRepository.getReviewsForClub(clubId).first()
        assertEquals(1, clubReviews.size)
        assertEquals("Klasse Sound & Beleuchtung!", clubReviews[0].text)
    }

    @Test
    fun testChatRepositoryOfflineMessagingFlow() = runBlocking {
        val chatId = "chat_offline_101"
        db.chatDao().insertChat(
            ChatEntity(
                id = chatId,
                name = "Berlin - Tonight",
                cityRegion = "Berlin",
                lastMessageText = "Willkommen",
                lastMessageTimestampMs = System.currentTimeMillis(),
                avatarInitial = "B",
                chatType = ChatType.PUBLIC_CITY
            )
        )

        val sendResult = chatRepository.sendTextMessage(
            chatId = chatId,
            senderUserId = "usr_sender_1",
            senderName = "Alex",
            text = "Single Source of Truth Test Message"
        )

        assertTrue(sendResult.isSuccess)
        val sentMsg = sendResult.getOrNull()
        assertNotNull(sentMsg)
        assertEquals(MessageStatus.SENT, sentMsg?.status)

        val messages = chatRepository.getMessagesForChat(chatId).first()
        assertEquals(1, messages.size)
        assertEquals("Single Source of Truth Test Message", messages[0].text)
    }

    private companion object {
        val mockApiService = null
    }
}
