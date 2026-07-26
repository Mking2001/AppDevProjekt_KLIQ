package com.kliq.app.service

import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.ClubGeofenceState
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.model.VisitedClubHistory
import com.kliq.app.data.repository.GeofenceRepository
import com.kliq.app.data.repository.RatingRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
import com.kliq.app.viewmodel.RatingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RatingVerificationLockTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeReviewDao
    private lateinit var fakeGeofenceRepository: FakeGeofenceRepository
    private lateinit var antiSpamValidator: AntiSpamReviewValidator
    private lateinit var verificationService: VerificationServiceImpl
    private lateinit var ratingRepository: RatingRepositoryImpl
    private lateinit var viewModel: RatingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeReviewDao()
        fakeGeofenceRepository = FakeGeofenceRepository()
        antiSpamValidator = AntiSpamReviewValidator()
        verificationService = VerificationServiceImpl(fakeGeofenceRepository, antiSpamValidator)
        ratingRepository = RatingRepositoryImpl(fakeDao, verificationService, antiSpamValidator, testDispatcher)
        viewModel = RatingViewModel(ratingRepository, verificationService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Ablauf 1: Unverifizierter Bewertungsversuch (Sperre greift)
     */
    @Test
    fun test1_unverifiedRatingSubmission_locksUiAndThrowsSecurityExceptionOnRepositoryCall() = runTest {
        val reviewerId = "user_alpha"
        val targetId = "user_beta"

        viewModel.initTargetUser(reviewerId, targetId)
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertTrue("UI Sterne-System muss standardmäßig gesperrt sein", uiState.isRatingLocked)
        assertEquals("Verifizierungsmethode muss UNVERIFIED sein", ReviewVerificationMethod.UNVERIFIED, uiState.verificationMethod)

        val repositoryResult = ratingRepository.submitUserRating(
            reviewerUserId = reviewerId,
            targetUserId = targetId,
            rating = 5,
            text = "Unverifizierter Versuch",
            qrToken = null
        )

        assertTrue("Repository-Aufruf muss bei fehlender Verifizierung scheitern", repositoryResult.isFailure)
        val exception = repositoryResult.exceptionOrNull()
        assertNotNull("Es muss eine Sicherheits-Exception geworfen werden", exception)
        assertTrue("Exception muss vom Typ IllegalStateException sein", exception is IllegalStateException)
        assertEquals(0, fakeDao.insertedReviews.size)
    }

    /**
     * Ablauf 2: Erfolgreicher GPS-Match (Freischaltung via Nähe)
     */
    @Test
    fun test2_successfulGpsMatch_unlocksUiAndPersistsRatingToDatabase() = runTest {
        val reviewerId = "user_alpha"
        val targetId = "user_beta"

        fakeGeofenceRepository.setActiveClubState(
            ClubGeofenceState(
                activeClubId = "club_matrix_50m",
                activeClubName = "Club Matrix",
                isInsideGeofence = true
            )
        )

        viewModel.initTargetUser(reviewerId, targetId)
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse("UI Sterne-System muss bei GPS-Match freigeschaltet sein", uiState.isRatingLocked)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, uiState.verificationMethod)

        viewModel.onRatingChanged(5)
        viewModel.onCommentChanged("Super Party zusammen!")
        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Bewertung muss erfolgreich eingereicht werden", viewModel.uiState.value.submitSuccess)
        assertEquals(1, fakeDao.insertedReviews.size)
        val persisted = fakeDao.insertedReviews.first()
        assertEquals(reviewerId, persisted.reviewerUserId)
        assertEquals(targetId, persisted.targetUserId)
        assertEquals(5, persisted.rating)
        assertTrue(persisted.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, persisted.verificationMethod)
    }

    /**
     * Ablauf 3: Erfolgreicher QR-Scan (Freischaltung via Koppelung)
     */
    @Test
    fun test3_successfulQrScan_instantlyUnlocksUiAndUpdatesRecord() = runTest {
        val reviewerId = "user_alpha"
        val targetId = "user_beta"
        val qrToken = "KLIQ_PASS_$targetId"

        viewModel.initTargetUser(reviewerId, targetId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Sperre muss vor dem Scan aktiv sein", viewModel.uiState.value.isRatingLocked)

        viewModel.onQrCodeScanned(qrToken)
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse("Sperre muss nach QR-Scan aufgehoben sein", uiState.isRatingLocked)
        assertEquals(ReviewVerificationMethod.QR_CODE_SCAN, uiState.verificationMethod)

        val repositoryResult = ratingRepository.submitUserRating(
            reviewerUserId = reviewerId,
            targetUserId = targetId,
            rating = 4,
            text = "Verifiziert via QR-Scan Pass",
            qrToken = qrToken
        )

        assertTrue("Repository muss verifizierte QR-Bewertung akzeptieren", repositoryResult.isSuccess)
        val persisted = repositoryResult.getOrNull()
        assertNotNull(persisted)
        assertTrue(persisted!!.isVerified)
        assertEquals(ReviewVerificationMethod.QR_CODE_SCAN, persisted.verificationMethod)
        assertEquals(1, fakeDao.insertedReviews.size)
    }

    private class FakeReviewDao : ReviewDao {
        val insertedReviews = mutableListOf<ReviewEntity>()

        override fun getReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getVerifiedReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForEvent(eventId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForTargetUser(targetUserId: String): Flow<List<ReviewEntity>> =
            flowOf(insertedReviews.filter { it.targetUserId == targetUserId })
        override fun getReviewsByReviewer(reviewerUserId: String): Flow<List<ReviewEntity>> =
            flowOf(insertedReviews.filter { it.reviewerUserId == reviewerUserId })
        override fun getAverageRatingForClub(clubId: String): Flow<Double?> = flowOf(null)
        override fun getAverageRatingForEvent(eventId: String): Flow<Double?> = flowOf(null)
        override suspend fun incrementHelpfulVotes(reviewId: String) {}
        override suspend fun flagReview(reviewId: String) {}
        override suspend fun insertReview(review: ReviewEntity) {
            insertedReviews.add(review)
        }
        override suspend fun insertReviews(reviews: List<ReviewEntity>) {
            insertedReviews.addAll(reviews)
        }
        override suspend fun deleteReviewById(reviewId: String) {}
    }

    private class FakeGeofenceRepository : GeofenceRepository {
        private val _activeClubState = MutableStateFlow(ClubGeofenceState())
        override val activeClubState: StateFlow<ClubGeofenceState> = _activeClubState

        private val _visitedHistory = MutableStateFlow<List<VisitedClubHistory>>(emptyList())
        override val visitedHistory: StateFlow<List<VisitedClubHistory>> = _visitedHistory

        fun setActiveClubState(state: ClubGeofenceState) {
            _activeClubState.value = state
        }

        override suspend fun handleGeofenceTransition(clubId: String, transitionType: GeofenceTransitionType) {}
        override fun isClubGeofenceVerified(clubId: String): Boolean = _activeClubState.value.isInsideGeofence
        override suspend fun getVisitedHistoryForUser(): List<VisitedClubHistory> = _visitedHistory.value
        override suspend fun updateRegisteredGeofenceCount(count: Int) {}
        override suspend fun resetGeofenceState() {}
    }
}
