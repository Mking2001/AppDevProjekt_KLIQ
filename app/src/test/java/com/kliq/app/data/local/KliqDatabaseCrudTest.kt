package com.kliq.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.ReviewVerificationMethod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class KliqDatabaseCrudTest {
    private lateinit var db: KliqDatabase
    private lateinit var userDao: UserDao
    private lateinit var clubDao: ClubDao
    private lateinit var reviewDao: ReviewDao
    private lateinit var chatDao: ChatDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
        clubDao = db.clubDao()
        reviewDao = db.reviewDao()
        chatDao = db.chatDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testUserCrudAsync() = runTest {
        val user = UserEntity("u1", "testuser", "test@test.com", null, null)
        userDao.insertUser(user)

        val retrievedUser = userDao.getUserById("u1").first()
        assertNotNull(retrievedUser)
        assertEquals("testuser", retrievedUser?.username)

        userDao.clearUsers()
        val deletedUser = userDao.getUserById("u1").first()
        assertNull(deletedUser)
    }

    @Test
    fun testClubSearchIntegrationFields() = runTest {
        val club = ClubEntity(
            id = "c1",
            name = "Test Club",
            category = "Techno",
            rating = 4.5f,
            imageUrl = "",
            region = "Berlin",
            externalSearchTags = "underground, techno, dark",
            websiteUrl = "https://test.club"
        )
        clubDao.insertClubs(listOf(club))

        val retrievedClub = clubDao.getClubById("c1").first()
        assertNotNull(retrievedClub)
        assertEquals("underground, techno, dark", retrievedClub?.externalSearchTags)
        assertEquals("https://test.club", retrievedClub?.websiteUrl)
    }

    @Test
    fun testReviewConstraints() = runTest {
        userDao.insertUser(UserEntity("u1", "test", "test", null, null))
        clubDao.insertClubs(listOf(ClubEntity(id = "c1", name = "test", category = "test")))

        val review = ReviewEntity(
            id = "r1",
            clubId = "c1",
            reviewerUserId = "u1",
            rating = 5,
            text = "Great!",
            verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
            isVerified = true,
            timestamp = System.currentTimeMillis()
        )
        reviewDao.insertReview(review)

        val reviews = reviewDao.getReviewsForClub("c1").first()
        assertTrue(reviews.isNotEmpty())
        assertEquals(5, reviews[0].rating)
        assertTrue(reviews[0].isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, reviews[0].verificationMethod)
    }

    @Test
    fun testChatOperations() = runTest {
        val chat = ChatEntity(
            id = "ch1",
            name = "Test Chat",
            lastMessageText = "Hello",
            lastMessageTimestampMs = 0L,
            avatarInitial = "T",
            chatType = ChatType.PRIVATE,
            isOnline = true
        )
        chatDao.insertChat(chat)

        val retrievedChats = chatDao.getAllChats().first()
        assertEquals(1, retrievedChats.size)
        assertEquals("Test Chat", retrievedChats[0].name)
        assertTrue(retrievedChats[0].isOnline)
    }
}
