package com.kliq.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.model.ReviewVerificationMethod
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
class ReviewDaoTest {

    private lateinit var db: KliqDatabase
    private lateinit var userDao: UserDao
    private lateinit var clubDao: ClubDao
    private lateinit var reviewDao: ReviewDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
        clubDao = db.clubDao()
        reviewDao = db.reviewDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertReviewAndQueryVerifiedFilter() = runTest {
        userDao.insertUser(UserEntity("usr_100", "alex", "alex@kliq.de", null, null))
        userDao.insertUser(UserEntity("usr_101", "sarah", "sarah@kliq.de", null, null))
        clubDao.insertClub(ClubEntity(id = "club_matrix", name = "Matrix Berlin"))

        val reviewVerified = ReviewEntity(
            id = "rev_1",
            reviewerUserId = "usr_100",
            clubId = "club_matrix",
            rating = 5,
            text = "Awesome party & sound system!",
            timestamp = System.currentTimeMillis(),
            verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
            isVerified = true,
            reviewerUsername = "alex"
        )
        val reviewUnverified = ReviewEntity(
            id = "rev_2",
            reviewerUserId = "usr_101",
            clubId = "club_matrix",
            rating = 1,
            text = "Spam review",
            timestamp = System.currentTimeMillis() - 1000L,
            verificationMethod = ReviewVerificationMethod.UNVERIFIED,
            isVerified = false,
            reviewerUsername = "sarah"
        )

        reviewDao.insertReviews(listOf(reviewVerified, reviewUnverified))

        val allReviews = reviewDao.getReviewsForClub("club_matrix").first()
        assertEquals(2, allReviews.size)

        val verifiedReviews = reviewDao.getVerifiedReviewsForClub("club_matrix").first()
        assertEquals(1, verifiedReviews.size)
        assertEquals("rev_1", verifiedReviews[0].id)
        assertTrue(verifiedReviews[0].isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, verifiedReviews[0].verificationMethod)

        val avgRating = reviewDao.getAverageRatingForClub("club_matrix").first()
        assertNotNull(avgRating)
        assertEquals(3.0, avgRating!!, 0.01)
    }
}
