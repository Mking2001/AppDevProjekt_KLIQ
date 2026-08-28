package com.kliq.app.ui.screens.profile

import com.kliq.app.testing.createTestProfileViewModel
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.model.ExternalSearchResponseDto
import com.kliq.app.data.repository.UserRepositoryImpl
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
class ProfileAverageRatingUnitTest {

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
    fun szenario1_profileWithoutRatings_providesDefaultFallbackState() = runTest {
        val targetUserId = "user_no_reviews"

        viewModel.loadProfileData(targetUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("0.0", state.formattedAverageRating)
        assertEquals(0.0, state.averageRating, 0.0001)
        assertEquals(0, state.totalReviewsCount)
        assertEquals(0, state.verifiedReviewsCount)
        assertFalse(state.hasRatings)
    }

    @Test
    fun szenario2_correctAverageCalculation_threeVerifiedReviews_returnsRounded4Dot3() = runTest {
        val targetUserId = "user_three_reviews"
        fakeReviewDao.reviews.addAll(
            listOf(
                ReviewEntity(
                    id = "r1",
                    reviewerUserId = "rev1",
                    targetUserId = targetUserId,
                    rating = 5,
                    text = "Top Nightlife-Begleitung!",
                    timestamp = 1000L,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
                ),
                ReviewEntity(
                    id = "r2",
                    reviewerUserId = "rev2",
                    targetUserId = targetUserId,
                    rating = 4,
                    text = "Super Abend im Club",
                    timestamp = 2000L,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.QR_CODE_SCAN
                ),
                ReviewEntity(
                    id = "r3",
                    reviewerUserId = "rev3",
                    targetUserId = targetUserId,
                    rating = 4,
                    text = "Sehr zuverlässig",
                    timestamp = 3000L,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
                )
            )
        )

        viewModel.loadProfileData(targetUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        val expectedExactAverage = (5 + 4 + 4) / 3.0 // 4.333333333333333
        assertEquals(expectedExactAverage, state.averageRating, 0.0001)
        assertEquals("4.3", state.formattedAverageRating)
        assertEquals(3, state.totalReviewsCount)
        assertEquals(3, state.verifiedReviewsCount)
        assertTrue(state.hasRatings)
    }

    @Test
    fun szenario3_extremeValues_perfectFiveStarsAndHighVolume_formatsCorrectlyWithoutError() = runTest {
        val perfectUserId = "user_perfect_five"
        repeat(10) { index ->
            fakeReviewDao.reviews.add(
                ReviewEntity(
                    id = "p_$index",
                    reviewerUserId = "rev_$index",
                    targetUserId = perfectUserId,
                    rating = 5,
                    text = "Perfekt",
                    timestamp = 1000L + index,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
                )
            )
        }

        viewModel.loadProfileData(perfectUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        val statePerfect = viewModel.uiState.value
        assertEquals(5.0, statePerfect.averageRating, 0.0001)
        assertEquals("5.0", statePerfect.formattedAverageRating)
        assertEquals(10, statePerfect.totalReviewsCount)
        assertTrue(statePerfect.hasRatings)

        val highVolumeUserId = "user_high_volume"
        repeat(9999) { index ->
            fakeReviewDao.reviews.add(
                ReviewEntity(
                    id = "hv_$index",
                    reviewerUserId = "rev_$index",
                    targetUserId = highVolumeUserId,
                    rating = if (index % 5 == 0) 4 else 5,
                    text = "High volume review",
                    timestamp = 10000L + index,
                    isVerified = true,
                    verificationMethod = ReviewVerificationMethod.QR_CODE_SCAN
                )
            )
        }

        viewModel.loadProfileData(highVolumeUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        val stateHighVolume = viewModel.uiState.value
        assertEquals(9999, stateHighVolume.totalReviewsCount)
        assertTrue(stateHighVolume.averageRating in 4.0..5.0)
        assertTrue(stateHighVolume.formattedAverageRating.matches(Regex("\\d+\\.\\d")))
    }

    private class FakeUserDao : UserDao {
        val users = mutableMapOf<String, UserEntity>()
        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(users[userId])
        override suspend fun getUserByIdOneShot(userId: String): UserEntity? = users[userId]
        override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> = flowOf(null)
        override suspend fun getUserPreferencesOneShot(userId: String): UserPreferencesEntity? = null
        override suspend fun getUserByUsername(username: String): UserEntity? = users.values.find { it.username.equals(username, ignoreCase = true) }
        override suspend fun getUserByEmail(email: String): UserEntity? = users.values.find { it.email.equals(email, ignoreCase = true) }
        override suspend fun getUserByPhone(phoneNumber: String): UserEntity? = users.values.find { it.phoneNumber == phoneNumber }
        override suspend fun insertUser(user: UserEntity) { users[user.id] = user }
        override suspend fun insertUserPreferences(preferences: UserPreferencesEntity) {}
        override fun getVerifiedUsers(): Flow<List<UserEntity>> = flowOf(emptyList())
        override suspend fun updateUserVerificationStatus(userId: String, isVerified: Boolean) {}
        override suspend fun deleteUserById(userId: String) { users.remove(userId) }
        override suspend fun searchUsers(query: String): List<UserEntity> = emptyList()
        override suspend fun getAllUsers(): List<UserEntity> = users.values.toList()
        override suspend fun deleteUserPreferencesByUserId(userId: String) {}
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
        ): ExternalSearchResponseDto {
            return ExternalSearchResponseDto(emptyList(), emptyList())
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
