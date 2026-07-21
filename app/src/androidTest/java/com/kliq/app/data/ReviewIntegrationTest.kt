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
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.ReviewRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
import com.kliq.app.ui.model.ReviewHighContrastPalette
import com.kliq.app.ui.model.toHighContrastUiState
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
    fun testReviewInstantiationAndLocalDatabasePersistence() = runBlocking {
        // 1. Instanziieren von User und Club in der Datenbank
        val reviewerId = "user_rev_1"
        val clubId = "club_berghain_1"
        userDao.insertUser(UserEntity(reviewerId, "techno_fan", "techno@kliq.de", null, null))
        clubDao.insertClub(ClubEntity(id = clubId, name = "Berghain / Panorama Bar"))

        // 2. Instanziieren und lokales Speichern eines Review-Objekts (5 Sterne)
        val reviewId = "rev_persisted_101"
        val reviewEntity = ReviewEntity(
            id = reviewId,
            reviewerUserId = reviewerId,
            clubId = clubId,
            rating = 5,
            text = "Phänomenale Sound-Anlage und einmalige Atmosphäre!",
            timestamp = System.currentTimeMillis(),
            verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
            isVerified = true,
            reviewerUsername = "techno_fan"
        )
        reviewDao.insertReview(reviewEntity)

        // 3. Auslesen aus der Room-Datenbank und Verifikation der Werte
        val dbReviews = reviewDao.getReviewsForClub(clubId).first()
        assertEquals(1, dbReviews.size)

        val retrievedReview = dbReviews[0]
        assertEquals(reviewId, retrievedReview.id)
        assertEquals(reviewerId, retrievedReview.reviewerUserId)
        assertEquals(clubId, retrievedReview.clubId)
        assertEquals(5, retrievedReview.rating)
        assertEquals("Phänomenale Sound-Anlage und einmalige Atmosphäre!", retrievedReview.text)
        assertTrue(retrievedReview.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, retrievedReview.verificationMethod)
    }

    @Test
    fun testGpsVerificationStatusMatchAndMismatch() = runBlocking {
        val reviewerId = "user_gps_tester"
        val clubId = "club_tresor_berlin"
        userDao.insertUser(UserEntity(reviewerId, "gps_tester", "gps@kliq.de", null, null))
        
        // Club mit GPS-Koordinaten in der Datenbank anlegen
        clubDao.insertClub(
            ClubEntity(
                id = clubId,
                name = "Tresor Berlin",
                latitude = 52.5111,
                longitude = 13.4194,
                geofenceRadiusMeters = 300.0
            )
        )

        // Testfall A: Erfolgreicher GPS-Match (Benutzer ~100m entfernt -> Verifiziert = true)
        val resultMatch = reviewRepository.submitReviewWithGpsCheck(
            reviewerUserId = reviewerId,
            clubId = clubId,
            rating = 5,
            text = "Live im Club verfasst - bombastischer Bass!",
            userLat = 52.5118,
            userLon = 13.4198
        )
        assertTrue(resultMatch.isSuccess)
        val verifiedReview = resultMatch.getOrNull()
        assertNotNull(verifiedReview)
        assertTrue("Review muss als verifiziert markiert sein", verifiedReview!!.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, verifiedReview.verificationMethod)

        val uiStateVerified = verifiedReview.toHighContrastUiState()
        assertTrue(uiStateVerified.verificationBadgeText.contains("VERIFIZIERT"))
        assertEquals(ReviewHighContrastPalette.VerifiedNeonGreen, uiStateVerified.verificationBadgeColorHex)

        // Testfall B: Fehlgeschlagene Standorterkennung (Benutzer ~5km entfernt -> Verifiziert = false)
        val resultMismatch = reviewRepository.submitReviewWithGpsCheck(
            reviewerUserId = reviewerId,
            clubId = clubId,
            rating = 2,
            text = "Von zuhause aus geschrieben",
            userLat = 52.5500,
            userLon = 13.3500
        )
        assertTrue(resultMismatch.isSuccess)
        val unverifiedReview = resultMismatch.getOrNull()
        assertNotNull(unverifiedReview)
        assertFalse("Review darf nicht verifiziert sein", unverifiedReview!!.isVerified)
        assertEquals(ReviewVerificationMethod.UNVERIFIED, unverifiedReview.verificationMethod)

        val uiStateUnverified = unverifiedReview.toHighContrastUiState()
        assertEquals("UNVERIFIZIERT", uiStateUnverified.verificationBadgeText)
        assertEquals(ReviewHighContrastPalette.UnverifiedGray, uiStateUnverified.verificationBadgeColorHex)
    }

    @Test
    fun testReviewTargetMappingToUserAndClub() = runBlocking {
        val reviewerId = "user_reviewer_alpha"
        val targetUserId = "user_target_bouncer"
        val clubId = "club_watergate"

        userDao.insertUser(UserEntity(reviewerId, "alpha", "alpha@kliq.de", null, null))
        userDao.insertUser(UserEntity(targetUserId, "bouncer_steve", "steve@kliq.de", null, null))
        clubDao.insertClub(ClubEntity(id = clubId, name = "Watergate"))

        // Unverifiziertes Peer-Review für ein Target-User (z.B. Türsteher/Host) in einem Club
        val resultPeerReview = reviewRepository.submitUnverifiedReview(
            reviewerUserId = reviewerId,
            clubId = clubId,
            targetUserId = targetUserId,
            rating = 4,
            text = "Sehr freundlicher Einlass am Haupteingang."
        )

        assertTrue(resultPeerReview.isSuccess)
        val peerReview = resultPeerReview.getOrNull()
        assertNotNull(peerReview)
        assertEquals(reviewerId, peerReview!!.reviewerUserId)
        assertEquals(targetUserId, peerReview.targetUserId)
        assertEquals(clubId, peerReview.clubId)
        assertEquals(4, peerReview.rating)

        // Verifikation der Zuordnung über DAO-Queries
        val reviewsForTargetUser = reviewDao.getReviewsForTargetUser(targetUserId).first()
        assertEquals(1, reviewsForTargetUser.size)
        assertEquals(reviewerId, reviewsForTargetUser[0].reviewerUserId)
        assertEquals(targetUserId, reviewsForTargetUser[0].targetUserId)

        val reviewsByReviewer = reviewDao.getReviewsByReviewer(reviewerId).first()
        assertEquals(1, reviewsByReviewer.size)
        assertEquals("Sehr freundlicher Einlass am Haupteingang.", reviewsByReviewer[0].text)
    }
}
