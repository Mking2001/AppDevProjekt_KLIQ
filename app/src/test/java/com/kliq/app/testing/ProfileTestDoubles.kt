package com.kliq.app.testing

import com.kliq.app.data.local.entities.FriendEntity
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.FeedComment
import com.kliq.app.data.model.FeedPost
import com.kliq.app.data.model.GenderRatio
import com.kliq.app.data.model.RegionSearchResult
import com.kliq.app.data.model.Story
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.data.repository.FeedRepository
import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.SocialRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.domain.CurrentUserProvider
import com.kliq.app.service.QrCodeService
import com.kliq.app.ui.screens.profile.ProfileViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.mockito.Mockito.mock

class FakeFeedRepository(
    initialPosts: List<FeedPost> = emptyList(),
    initialStories: List<Story> = emptyList()
) : FeedRepository {

    private val posts = MutableStateFlow(initialPosts)
    private val stories = MutableStateFlow(initialStories)
    private val comments = MutableStateFlow<List<FeedComment>>(emptyList())

    fun emitPosts(newPosts: List<FeedPost>) {
        posts.value = newPosts
    }

    override fun getFeedPosts(): Flow<List<FeedPost>> = posts

    override fun getFeedPostsByAuthor(authorUserId: String): Flow<List<FeedPost>> {
        return posts.map { list -> list.filter { it.authorUserId == authorUserId } }
    }

    override suspend fun syncFeedPosts(): Result<Unit> = Result.success(Unit)

    override fun getStories(): Flow<List<Story>> = stories

    override fun getCommentsForPost(postId: String): Flow<List<FeedComment>> = comments

    override suspend fun createPost(
        authorUserId: String,
        authorName: String,
        contentText: String,
        clubId: String?,
        clubName: String?,
        imageUrl: String?
    ): Result<FeedPost> {
        val post = FeedPost(
            id = "fake_post_${posts.value.size}",
            authorUserId = authorUserId,
            authorName = authorName,
            contentText = contentText,
            clubId = clubId,
            clubName = clubName,
            imageUrl = imageUrl
        )
        posts.value = listOf(post) + posts.value
        return Result.success(post)
    }

    override suspend fun toggleLike(postId: String): Result<Boolean> {
        val target = posts.value.find { it.id == postId }
            ?: return Result.failure(NoSuchElementException(postId))
        val nextLiked = !target.isLikedByMe
        posts.value = posts.value.map { post ->
            if (post.id != postId) {
                post
            } else {
                post.copy(
                    isLikedByMe = nextLiked,
                    likeCount = if (nextLiked) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)
                )
            }
        }
        return Result.success(nextLiked)
    }

    override suspend fun addComment(
        postId: String,
        authorUserId: String,
        authorName: String,
        text: String
    ): Result<FeedComment> {
        val comment = FeedComment(
            id = "fake_comment_${comments.value.size}",
            postId = postId,
            authorUserId = authorUserId,
            authorName = authorName,
            text = text
        )
        comments.value = comments.value + comment
        posts.value = posts.value.map { post ->
            if (post.id == postId) post.copy(commentCount = post.commentCount + 1) else post
        }
        return Result.success(comment)
    }

    override suspend fun markStoryAsSeen(storyId: String) {
        stories.value = stories.value.map { if (it.id == storyId) it.copy(isSeen = true) else it }
    }

    override suspend fun deletePost(postId: String) {
        posts.value = posts.value.filterNot { it.id == postId }
    }
}

class EmptyEventRepository : EventRepository {
    override fun getAllEvents(): Flow<List<Event>> = flowOf(emptyList())
    override fun getEventsForClub(clubId: String): Flow<List<Event>> = flowOf(emptyList())
    override fun getEventById(eventId: String): Flow<Event?> = flowOf(null)
    override fun searchEventsLocal(query: String): Flow<List<Event>> = flowOf(emptyList())
    override fun getUpcomingEvents(minTimestamp: Long): Flow<List<Event>> = flowOf(emptyList())
    override suspend fun saveEvents(events: List<Event>) = Unit
}

class FakeClubRepository(initialClubs: List<Club> = emptyList()) : ClubRepository {

    private val clubs = MutableStateFlow(initialClubs)

    val favoriteToggles = mutableListOf<Pair<String, Boolean>>()

    override fun getAllClubs(): Flow<List<Club>> = clubs

    override fun getFavoriteClubs(): Flow<List<Club>> = clubs

    override fun getClubById(clubId: String): Flow<Club?> = flowOf(clubs.value.find { it.id == clubId })

    override fun searchClubsLocal(query: String): Flow<List<Club>> = clubs

    override fun searchClubsFiltered(
        query: String,
        regionFilter: String?,
        genreFilter: String?
    ): Flow<List<Club>> = clubs

    override fun searchRegionsAndCities(query: String): Flow<List<RegionSearchResult>> = flowOf(emptyList())

    override suspend fun toggleFavorite(clubId: String, currentFavoriteState: Boolean) {
        favoriteToggles += clubId to currentFavoriteState
        clubs.value = clubs.value.map {
            if (it.id == clubId) it.copy(isFavorite = !currentFavoriteState) else it
        }
    }

    override suspend fun searchExternalClubs(
        query: String,
        userLat: Double?,
        userLon: Double?,
        radiusKm: Int
    ): Result<List<Club>> = Result.success(emptyList())

    override suspend fun isUserWithinGeofence(clubId: String, userLat: Double, userLon: Double): Boolean = false

    override fun getClubGenderRatio(clubId: String, timeWindowMs: Long): Flow<GenderRatio> =
        flowOf(GenderRatio.calculate(0, 0, 0))

    override suspend fun calculateClubGenderRatio(clubId: String, timeWindowMs: Long): GenderRatio =
        GenderRatio.calculate(0, 0, 0)

    override suspend fun toggleClubHype(clubId: String, userId: String): Result<Boolean> = Result.success(true)
    override fun isClubHypedToday(clubId: String, userId: String): Flow<Boolean> = flowOf(false)
    override fun getHypedClubIdsToday(userId: String): Flow<List<String>> = flowOf(emptyList())
}

class EmptySocialRepository : SocialRepository {
    override fun getFriendsForUser(userId: String): Flow<List<FriendEntity>> = flowOf(emptyList())
    override fun getFollowers(userId: String): Flow<List<FriendEntity>> = flowOf(emptyList())
    override fun getFollowing(userId: String): Flow<List<FriendEntity>> = flowOf(emptyList())
    override fun isFriend(userId: String, friendUserId: String): Flow<Boolean> = flowOf(false)
    override suspend fun isFriendOneShot(userId: String, friendUserId: String): Boolean = false
    override suspend fun sendFriendRequest(
        userId: String,
        targetUserId: String,
        isQrVerified: Boolean
    ): Result<Unit> = Result.success(Unit)

    override suspend fun verifyAndAddFriend(userId: String, targetUserId: String): Result<Unit> =
        Result.success(Unit)
    override suspend fun removeFriend(userId: String, targetUserId: String): Result<Unit> =
        Result.success(Unit)
    override suspend fun syncSocialConnections(userId: String): Result<Unit> =
        Result.success(Unit)
}

class EmptyReviewRepository : com.kliq.app.data.repository.ReviewRepository {
    override fun getReviewsForClub(clubId: String): Flow<List<com.kliq.app.data.model.Review>> = flowOf(emptyList())
    override fun getVerifiedReviewsForClub(clubId: String): Flow<List<com.kliq.app.data.model.Review>> = flowOf(emptyList())
    override fun getReviewsForEvent(eventId: String): Flow<List<com.kliq.app.data.model.Review>> = flowOf(emptyList())
    override fun getReviewsForTargetUser(targetUserId: String): Flow<List<com.kliq.app.data.model.Review>> = flowOf(emptyList())
    override fun getAverageRatingForClub(clubId: String): Flow<Double?> = flowOf(null)
    override fun getAverageRatingForTargetUser(targetUserId: String): Flow<Double?> = flowOf(null)
    override fun getReviewCountForTargetUser(targetUserId: String): Flow<Int> = flowOf(0)
    override suspend fun syncReviewsForClub(clubId: String): Result<Unit> = Result.success(Unit)
    override suspend fun syncReviewsForTargetUser(targetUserId: String): Result<Unit> = Result.success(Unit)
    override suspend fun submitReviewWithGpsCheck(
        reviewerUserId: String,
        clubId: String,
        rating: Int,
        text: String,
        userLat: Double,
        userLon: Double
    ): Result<com.kliq.app.data.model.Review> = Result.failure(NotImplementedError())
    override suspend fun submitReviewWithQrCheck(
        reviewerUserId: String,
        targetId: String,
        rating: Int,
        text: String,
        qrToken: String
    ): Result<com.kliq.app.data.model.Review> = Result.failure(NotImplementedError())
    override suspend fun submitVerifiedUserComment(
        reviewerUserId: String,
        targetUserId: String,
        rating: Int,
        text: String,
        verificationMethod: com.kliq.app.data.model.ReviewVerificationMethod,
        qrToken: String?
    ): Result<com.kliq.app.data.model.Review> = Result.failure(NotImplementedError())
    override suspend fun submitUnverifiedReview(
        reviewerUserId: String,
        clubId: String?,
        eventId: String?,
        targetUserId: String?,
        rating: Int,
        text: String
    ): Result<com.kliq.app.data.model.Review> = Result.failure(NotImplementedError())
}

fun createTestProfileViewModel(
    userRepository: UserRepository,
    qrCodeService: QrCodeService,
    feedRepository: FeedRepository = FakeFeedRepository(),
    eventRepository: EventRepository = EmptyEventRepository(),
    clubRepository: ClubRepository = FakeClubRepository(),
    socialRepository: SocialRepository = EmptySocialRepository(),
    reviewRepository: com.kliq.app.data.repository.ReviewRepository = EmptyReviewRepository(),
    sessionRepository: SessionRepository = mock(SessionRepository::class.java)
): ProfileViewModel = ProfileViewModel(
    userRepository = userRepository,
    feedRepository = feedRepository,
    eventRepository = eventRepository,
    clubRepository = clubRepository,
    socialRepository = socialRepository,
    reviewRepository = reviewRepository,
    currentUserProvider = CurrentUserProvider(sessionRepository, userRepository),
    qrCodeService = qrCodeService
)
