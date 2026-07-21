package com.kliq.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.repository.ReviewRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
import com.kliq.app.ui.model.ReviewHighContrastPalette
import com.kliq.app.ui.model.toHighContrastUiState
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
class ReviewIntegrationTest {

    private lateinit var db: KliqDatabase
    private lateinit var userDao: UserDao
    private lateinit var clubDao: ClubDao
    private lateinit var reviewDao: ReviewDao
    private lateinit var reviewRepository: ReviewRepositoryImpl

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
        clubDao = db.clubDao()
        reviewDao = db.reviewDao()

        val validator = AntiSpamReviewValidator()
        reviewRepository = ReviewRepositoryImpl(reviewDao, clubDao, validator)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testReviewSubmissionWithGpsAntiSpamValidation() = runBlocking {
        // Setup initial user & club in DB
        val userId = "user_alex_1"
        val clubId = "club_kitkat_1"
        userDao.insertUser(UserEntity(userId, "alex_reviewer", "alex@kliq.de", null, null))
        clubDao.insertClub(
            ClubEntity(
                id = clubId,
                name = "KitKatClub",
                latitude = 52.5112,
                longitude = 13.4182,
                geofenceRadiusMeters = 300.0
            )
        )

        // 1. Submit review inside GPS geofence (~100m distance)
        val resultVerified = reviewRepository.submitReviewWithGpsCheck(
            reviewerUserId = userId,
            clubId = clubId,
            rating = 5,
            text = "Legendary night & atmosphere!",
            userLat = 52.5118,
            userLon = 13.4185
        )

        assertTrue(resultVerified.isSuccess)
        val verifiedReview = resultVerified.getOrNull()
        assertNotNull(verifiedReview)
        assertTrue(verifiedReview!!.isVerified)

        // 2. Verify Room DB Persistence & Query
        val dbReviews = reviewDao.getVerifiedReviewsForClub(clubId).first()
        assertEquals(1, dbReviews.size)
        assertEquals("Legendary night & atmosphere!", dbReviews[0].text)

        // 3. Verify High-Contrast UI State Formatting
        val uiState = verifiedReview.toHighContrastUiState()
        assertEquals("★★★★★ (5.0)", uiState.ratingStarsFormatted)
        assertTrue(uiState.verificationBadgeText.contains("VERIFIZIERT"))
        assertEquals(ReviewHighContrastPalette.VerifiedNeonGreen, uiState.verificationBadgeColorHex)
    }

    @Test
    fun testReviewSubmissionWithQrCodeAntiSpamValidation() = runBlocking {
        val userId = "user_sarah_2"
        val clubId = "club_watergate_2"
        userDao.insertUser(UserEntity(userId, "sarah_reviewer", "sarah@kliq.de", null, null))
        clubDao.insertClub(ClubEntity(id = clubId, name = "Watergate"))

        val resultQr = reviewRepository.submitReviewWithQrCheck(
            reviewerUserId = userId,
            targetId = clubId,
            rating = 4,
            text = "Great sound by the river!",
            qrToken = "KLIQ_PASS_WATERGATE_2026_TOKEN"
        )

        assertTrue(resultQr.isSuccess)
        val qrReview = resultQr.getOrNull()
        assertNotNull(qrReview)
        assertTrue(qrReview!!.isVerified)
        assertTrue(qrReview.toHighContrastUiState().verificationBadgeText.contains("QR-Scan"))
    }
}
