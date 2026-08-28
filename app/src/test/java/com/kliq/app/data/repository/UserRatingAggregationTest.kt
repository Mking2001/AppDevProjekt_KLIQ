package com.kliq.app.data.repository

import com.kliq.app.testing.createTestProfileViewModel
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.ui.screens.profile.ProfileViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRatingAggregationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var fakeReviewDao: FakeReviewDao
    private lateinit var fakeApiService: FakeKliqApiService
    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserDao = FakeUserDao()
        fakeReviewDao = FakeReviewDao()
        fakeApiService = FakeKliqApiService()
        val qrCodeService = com.kliq.app.service.QrCodeServiceImpl(testDispatcher)
        userRepository = UserRepositoryImpl(fakeUserDao, fakeApiService, fakeReviewDao, testDispatcher)
        viewModel = createTestProfileViewModel(userRepository, qrCodeService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getUserReputationSummary_calculatesMathematicallyAccurateAverageAndCounts() = runTest {
        val targetId = "user_target_123"
        fakeReviewDao.reviews.addAll(
            listOf(
                ReviewEntity(
                    id = "r1",
                    reviewerUserId = "u1",
                    targetUserId = targetId,
                    rating = 5,
                    text = "Top!",
                    timestamp = 1000L,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
                ),
                ReviewEntity(
                    id = "r2",
                    reviewerUserId = "u2",
                    targetUserId = targetId,
                    rating = 4,
                    text = "Gut",
                    timestamp = 2000L,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.QR_CODE_SCAN
                )
            )
        )

        val summary = userRepository.getUserReputationSummary(targetId).first()

        assertEquals(targetId, summary.targetUserId)
        assertEquals(4.5, summary.averageRating, 0.001)
        assertEquals("4.5", summary.formattedAverageRating)
        assertEquals(2, summary.totalReviewsCount)
        assertEquals(2, summary.verifiedReviewsCount)
        assertTrue(summary.hasRatings)
    }

    @Test
    fun getUserReputationSummary_whenNoRatings_returnsZeroAverageAndHasRatingsFalse() = runTest {
        val targetId = "user_new"

        val summary = userRepository.getUserReputationSummary(targetId).first()

        assertEquals(targetId, summary.targetUserId)
        assertEquals(0.0, summary.averageRating, 0.001)
        assertEquals("0.0", summary.formattedAverageRating)
        assertEquals(0, summary.totalReviewsCount)
        assertFalse(summary.hasRatings)
    }

    @Test
    fun profileViewModel_streamsReputationSummaryStateOffMainThread() = runTest {
        val userId = "current_user"
        fakeReviewDao.reviews.add(
            ReviewEntity(
                id = "r10",
                reviewerUserId = "u5",
                targetUserId = userId,
                rating = 5,
                text = "Sehr sympathisch",
                timestamp = 3000L,
                isVerified = true,
                verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
            )
        )

        viewModel.loadProfileData(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(5.0, uiState.averageRating, 0.001)
        assertEquals("5.0", uiState.formattedAverageRating)
        assertEquals(1, uiState.totalReviewsCount)
        assertTrue(uiState.hasRatings)
    }

    private class FakeUserDao : UserDao {
        val users = mutableMapOf<String, UserEntity>()
        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(users[userId])
        override suspend fun getUserByIdOneShot(userId: String): UserEntity? = users[userId]
        override suspend fun getUserByUsername(username: String): UserEntity? = users.values.firstOrNull { it.username.equals(username, ignoreCase = true) }
        override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> = flowOf(null)
        override suspend fun getUserPreferencesOneShot(userId: String): UserPreferencesEntity? = null
        override suspend fun insertUser(user: UserEntity) { users[user.id] = user }
        override suspend fun insertUserPreferences(preferences: UserPreferencesEntity) {}
        override fun getVerifiedUsers(): Flow<List<UserEntity>> = flowOf(emptyList())
        override suspend fun updateUserVerificationStatus(userId: String, isVerified: Boolean) {}
        override suspend fun clearUsers() { users.clear() }
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

        override fun getAverageRatingForTargetUser(targetUserId: String): Flow<Double?> {
            val userReviews = reviews.filter { it.targetUserId == targetUserId }
            return flowOf(if (userReviews.isEmpty()) null else userReviews.map { it.rating }.average())
        }

        override fun getVerifiedReviewsCountForTargetUser(targetUserId: String): Flow<Int> {
            return flowOf(reviews.count { it.targetUserId == targetUserId && it.isVerified })
        }

        override fun getReviewsCountForTargetUser(targetUserId: String): Flow<Int> {
            return flowOf(reviews.count { it.targetUserId == targetUserId })
        }

        override fun getReviewCountForTargetUser(targetUserId: String): Flow<Int> {
            return flowOf(reviews.count { it.targetUserId == targetUserId })
        }

        override suspend fun incrementHelpfulVotes(reviewId: String) {}
        override suspend fun flagReview(reviewId: String) {}
        override suspend fun insertReview(review: ReviewEntity) { reviews.add(review) }
        override suspend fun insertReviews(reviewsList: List<ReviewEntity>) { reviews.addAll(reviewsList) }
        override suspend fun deleteReviewById(reviewId: String) {}
    }

    private class FakeKliqApiService : KliqApiService {
        override suspend fun getUserProfile(userId: String): UserEntity =
            UserEntity(id = userId, username = "TestUser", email = "test@kliq.app")

        override suspend fun searchExternalClubsAndEvents(
            query: String,
            latitude: Double?,
            longitude: Double?,
            radiusKm: Int?
        ): com.kliq.app.data.remote.model.ExternalSearchResponseDto {
            return com.kliq.app.data.remote.model.ExternalSearchResponseDto(emptyList(), emptyList())
        }

        override suspend fun reportUser(request: com.kliq.app.data.remote.ReportUserRequestDto): retrofit2.Response<Unit> {
            return retrofit2.Response.success(Unit)
        }

        override suspend fun blockUser(request: com.kliq.app.data.remote.BlockUserRequestDto): retrofit2.Response<Unit> {
            return retrofit2.Response.success(Unit)
        }

        override suspend fun unblockUser(currentUserId: String, targetUserId: String): retrofit2.Response<Unit> {
            return retrofit2.Response.success(Unit)
        }
    }
}
