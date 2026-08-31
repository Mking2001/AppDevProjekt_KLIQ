package com.kliq.app.viewmodel

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.ReviewRepositoryImpl
import com.kliq.app.data.util.AntiSpamReviewValidator
import com.kliq.app.ui.model.ReviewSortOption
import com.kliq.app.ui.model.StarFilterOption
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewFilterViewModelTest {

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
    fun loadReviewsForClub_appliesDefaultFilterAndSort() = runTest {
        setupMockReviewsForClub("club_1")

        viewModel.loadReviewsForClub("club_1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(4, state.reviews.size)
        assertTrue(state.filterState.isDefault)
        assertEquals(0, state.filterState.activeFilterCount)
        assertEquals("r_4", state.reviews[0].id)
    }

    @Test
    fun setStarFilter_filtersReviewsByStars() = runTest {
        setupMockReviewsForClub("club_1")
        viewModel.loadReviewsForClub("club_1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setStarFilter(StarFilterOption.FIVE_STARS)
        var state = viewModel.uiState.value
        assertEquals(1, state.reviews.size)
        assertEquals(5, state.reviews[0].rating)

        viewModel.setStarFilter(StarFilterOption.FOUR_PLUS_STARS)
        state = viewModel.uiState.value
        assertEquals(3, state.reviews.size)
        assertTrue(state.reviews.all { it.rating >= 4 })

        viewModel.setStarFilter(StarFilterOption.ONE_STAR)
        state = viewModel.uiState.value
        assertEquals(1, state.reviews.size)
        assertEquals(1, state.reviews[0].rating)
    }

    @Test
    fun setVerifiedOnly_filtersNonVerifiedReviews() = runTest {
        setupMockReviewsForClub("club_1")
        viewModel.loadReviewsForClub("club_1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setVerifiedOnly(true)
        val state = viewModel.uiState.value
        assertEquals(2, state.reviews.size)
        assertTrue(state.reviews.all { it.isVerified })
        assertTrue(state.verifiedReviewsOnly)
    }

    @Test
    fun setSortOption_sortsReviewsByRatingAndDate() = runTest {
        setupMockReviewsForClub("club_1")
        viewModel.loadReviewsForClub("club_1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setSortOption(ReviewSortOption.HIGHEST_RATING)
        var state = viewModel.uiState.value
        assertEquals(5, state.reviews.first().rating)
        assertEquals(1, state.reviews.last().rating)

        viewModel.setSortOption(ReviewSortOption.LOWEST_RATING)
        state = viewModel.uiState.value
        assertEquals(1, state.reviews.first().rating)
        assertEquals(5, state.reviews.last().rating)

        viewModel.setSortOption(ReviewSortOption.OLDEST_FIRST)
        state = viewModel.uiState.value
        assertEquals("r_1", state.reviews.first().id)
    }

    @Test
    fun resetFilters_restoresDefaultState() = runTest {
        setupMockReviewsForClub("club_1")
        viewModel.loadReviewsForClub("club_1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setStarFilter(StarFilterOption.FIVE_STARS)
        viewModel.setVerifiedOnly(true)
        viewModel.setSortOption(ReviewSortOption.HIGHEST_RATING)

        assertFalse(viewModel.uiState.value.filterState.isDefault)

        viewModel.resetFilters()

        val state = viewModel.uiState.value
        assertTrue(state.filterState.isDefault)
        assertEquals(4, state.reviews.size)
    }

    private fun setupMockReviewsForClub(clubId: String) {
        fakeReviewDao.reviews.addAll(
            listOf(
                ReviewEntity(
                    id = "r_1",
                    reviewerUserId = "u1",
                    clubId = clubId,
                    rating = 4,
                    text = "Guter Sound",
                    timestamp = 1000L,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
                ),
                ReviewEntity(
                    id = "r_2",
                    reviewerUserId = "u2",
                    clubId = clubId,
                    rating = 5,
                    text = "Mega Party",
                    timestamp = 2000L,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.QR_CODE_SCAN
                ),
                ReviewEntity(
                    id = "r_3",
                    reviewerUserId = "u3",
                    clubId = clubId,
                    rating = 1,
                    text = "Zu lange Schlange",
                    timestamp = 3000L,
                    isVerified = false,
                    verificationMethod = ReviewVerificationMethod.UNVERIFIED
                ),
                ReviewEntity(
                    id = "r_4",
                    reviewerUserId = "u4",
                    clubId = clubId,
                    rating = 4,
                    text = "Coole Vibes",
                    timestamp = 4000L,
                    isVerified = false,
                    verificationMethod = ReviewVerificationMethod.UNVERIFIED
                )
            )
        )
    }

    private class FakeReviewDao : ReviewDao {
        val reviews = mutableListOf<ReviewEntity>()

        override fun getReviewsForClub(clubId: String): Flow<List<ReviewEntity>> =
            flowOf(reviews.filter { it.clubId == clubId })

        override fun getVerifiedReviewsForClub(clubId: String): Flow<List<ReviewEntity>> =
            flowOf(reviews.filter { it.clubId == clubId && it.isVerified })

        override fun getReviewsForEvent(eventId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsForTargetUser(targetUserId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getReviewsByReviewer(reviewerUserId: String): Flow<List<ReviewEntity>> = flowOf(emptyList())
        override fun getAverageRatingForClub(clubId: String): Flow<Double?> = flowOf(3.5)
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
        override fun getAllClubs(): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun getFavoriteClubs(): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun getPromotedClubs(): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun getClubsByCity(city: String): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun getClubById(clubId: String): Flow<ClubEntity?> = flowOf(null)
        override fun searchClubs(query: String): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun searchClubsFiltered(query: String, region: String, category: String): Flow<List<ClubEntity>> = flowOf(emptyList())
        override fun searchDistinctRegionsAndCities(query: String): Flow<List<String>> = flowOf(emptyList())
        override suspend fun insertClub(club: ClubEntity) {}
        override suspend fun insertClubs(clubsList: List<ClubEntity>) {}
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
