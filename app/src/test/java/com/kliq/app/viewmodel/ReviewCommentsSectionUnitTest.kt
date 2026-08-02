package com.kliq.app.viewmodel

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.ReviewRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
class ReviewCommentsSectionUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeReviewDao: FakeReviewDao
    private lateinit var fakeClubDao: FakeClubDao
    private lateinit var antiSpamValidator: AntiSpamReviewValidator
    private lateinit var repository: ReviewRepositoryImpl
    private lateinit var viewModel: ReviewViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeReviewDao = FakeReviewDao()
        fakeClubDao = FakeClubDao()
        antiSpamValidator = AntiSpamReviewValidator()
        repository = ReviewRepositoryImpl(fakeReviewDao, fakeClubDao, antiSpamValidator)
        viewModel = ReviewViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun submitUserComment_whenVerificationLocked_blocksSubmissionAndSetErrorMessage() = runTest {
        viewModel.updateVerificationLockStatus(isLocked = true)
        viewModel.onCommentInputChanged("Toller Abend!")

        viewModel.submitUserComment("reviewer1", "target1")

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertTrue(state.errorMessage!!.contains("Sicherheits-Sperre"))
        assertEquals(0, fakeReviewDao.reviews.size)
    }

    @Test
    fun onCommentInputChanged_enforces280CharacterLimitRealtime() = runTest {
        val longText = "A".repeat(350)
        viewModel.onCommentInputChanged(longText)

        val state = viewModel.uiState.value
        assertEquals(280, state.commentInputText.length)
        assertEquals(0, state.remainingCharacters)
        assertTrue(state.isCommentLengthValid)
    }

    @Test
    fun submitUserComment_whenVerified_publishesCommentSuccessfully() = runTest {
        viewModel.updateVerificationLockStatus(
            isLocked = false,
            method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
        )
        viewModel.onCommentInputChanged("Perfekter Club-Besuch!")
        viewModel.onRatingSelected(5)

        viewModel.submitUserComment("reviewer1", "target1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.commentInputText)
        assertEquals("Verifizierter Kommentar erfolgreich veröffentlicht!", state.submitSuccessMessage)
        assertEquals(1, fakeReviewDao.reviews.size)

        val saved = fakeReviewDao.reviews.first()
        assertEquals("Perfekter Club-Besuch!", saved.text)
        assertEquals(5, saved.rating)
        assertTrue(saved.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, saved.verificationMethod)
    }

    @Test
    fun repository_submitVerifiedUserComment_whenUnverifiedMethod_returnsFailure() = runTest {
        val result = repository.submitVerifiedUserComment(
            reviewerUserId = "rev1",
            targetUserId = "target1",
            rating = 4,
            text = "Unverifizierter Versuch",
            verificationMethod = ReviewVerificationMethod.UNVERIFIED
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals(0, fakeReviewDao.reviews.size)
    }

    @Test
    fun loadCommentsForUser_streamsVerifiedCommentsToViewModelState() = runTest {
        val targetId = "target_user_123"
        fakeReviewDao.reviews.add(
            ReviewEntity(
                id = "r_comment_1",
                reviewerUserId = "rev_99",
                targetUserId = targetId,
                rating = 5,
                text = "Klasse Erfahrung im Neuraum",
                timestamp = 5000L,
                isVerified = true,
                verificationMethod = ReviewVerificationMethod.QR_CODE_SCAN,
                reviewerUsername = "Alex M."
            )
        )

        viewModel.loadCommentsForUser(targetId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSectionEmpty)
        assertEquals(1, state.commentReviews.size)
        assertEquals("Klasse Erfahrung im Neuraum", state.commentReviews.first().reviewText)
    }

    private class FakeReviewDao : ReviewDao {
        val reviews = mutableListOf<ReviewEntity>()

        override fun getReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getVerifiedReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForEvent(eventId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForTargetUser(targetUserId: String): Flow<List<ReviewEntity>> =
            flowOf(reviews.filter { it.targetUserId == targetUserId })
        override fun getReviewsByReviewer(reviewerUserId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getAverageRatingForClub(clubId: String): Flow<Double?> = flowOf(null)
        override fun getAverageRatingForEvent(eventId: String): Flow<Double?> = flowOf(null)
        override fun getAverageRatingForTargetUser(targetUserId: String): Flow<Double?> = flowOf(null)
        override fun getVerifiedReviewsCountForTargetUser(targetUserId: String): Flow<Int> = flowOf(0)
        override fun getReviewsCountForTargetUser(targetUserId: String): Flow<Int> = flowOf(0)
        override fun getReviewCountForTargetUser(targetUserId: String): Flow<Int> = flowOf(0)
        override suspend fun incrementHelpfulVotes(reviewId: String) {}
        override suspend fun flagReview(reviewId: String) {}
        override suspend fun insertReview(review: ReviewEntity) { reviews.add(review) }
        override suspend fun insertReviews(reviewsList: List<ReviewEntity>) { reviews.addAll(reviewsList) }
        override suspend fun deleteReviewById(reviewId: String) {}
    }

    private class FakeClubDao : ClubDao {
        val clubs = mutableListOf<ClubEntity>()
        override fun getAllClubs(): Flow<List<ClubEntity>> = flowOf(clubs)
        override fun getFavoriteClubs(): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun getPromotedClubs(): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun getClubsByCity(city: String): Flow<List<ClubEntity>> = flowOf(clubs.filter { it.city == city })
        override fun getClubById(clubId: String): Flow<ClubEntity?> = flowOf(clubs.find { it.id == clubId })
        override fun searchClubs(query: String): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun searchClubsFiltered(query: String, region: String, category: String): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun searchDistinctRegionsAndCities(query: String): Flow<List<String>> = flowOf(emptyList())
        override suspend fun insertClub(club: ClubEntity) { clubs.add(club) }
        override suspend fun insertClubs(clubsList: List<ClubEntity>) { clubs.addAll(clubsList) }
        override suspend fun updateFavoriteStatus(clubId: String, isFavorite: Boolean) {}
        override fun getEventsForClub(clubId: String): Flow<List<EventEntity>> = flowOf(emptyList())
        override suspend fun insertEvents(events: List<EventEntity>) {}
    }
}
