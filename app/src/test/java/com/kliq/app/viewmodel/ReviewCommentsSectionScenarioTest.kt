package com.kliq.app.viewmodel

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.ReviewRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
class ReviewCommentsSectionScenarioTest {

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
    fun szenario1_emptyCommentsSection_showsEmptyStateInStateFlow() = runTest {
        val targetUserId = "user_no_comments"
        viewModel.loadCommentsForUser(targetUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSectionEmpty)
        assertEquals(0, state.commentReviews.size)
        assertTrue(state.isVerificationLocked)
    }

    @Test
    fun szenario2_writingAndSubmittingComment_whenVerified_persistsToDbAndUpdatesStateImmediately() = runTest {
        val reviewerId = "rev_1"
        val targetUserId = "user_target_456"

        viewModel.updateVerificationLockStatus(
            isLocked = false,
            method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
        )
        viewModel.onCommentInputChanged("Mega Club-Abend mit der Kliq Crew!")
        viewModel.onRatingSelected(5)

        viewModel.submitUserComment(reviewerId, targetUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.commentInputText)
        assertNotNull(state.submitSuccessMessage)
        assertEquals(1, fakeReviewDao.reviews.size)

        val savedEntity = fakeReviewDao.reviews.first()
        assertEquals("Mega Club-Abend mit der Kliq Crew!", savedEntity.text)
        assertEquals(5, savedEntity.rating)
        assertTrue(savedEntity.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, savedEntity.verificationMethod)
    }

    @Test
    fun szenario3_characterLimitValidation_enforces280MaxCharactersRealtime() = runTest {
        val overflowInput = "X".repeat(320)
        viewModel.onCommentInputChanged(overflowInput)

        val state = viewModel.uiState.value
        assertEquals(280, state.commentInputText.length)
        assertEquals(0, state.remainingCharacters)
        assertTrue(state.isCommentLengthValid)

        val emptyInput = "   "
        viewModel.onCommentInputChanged(emptyInput)
        assertFalse(viewModel.uiState.value.isCommentLengthValid)
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
        override suspend fun updateFlameCount(clubId: String, flameCount: Int, flameDate: String) {}
        override fun getEventsForClub(clubId: String): Flow<List<EventEntity>> = flowOf(emptyList())
        override suspend fun insertEvents(events: List<EventEntity>) {}
        override suspend fun insertClubHype(hype: com.kliq.app.data.local.entities.ClubHypeEntity) {}
        override suspend fun deleteClubHype(clubId: String, userId: String, dateString: String) {}
        override fun isClubHypedToday(clubId: String, userId: String, dateString: String): Flow<Boolean> = flowOf(false)
        override fun getHypedClubIdsToday(userId: String, dateString: String): Flow<List<String>> = flowOf(emptyList())
    }
}
