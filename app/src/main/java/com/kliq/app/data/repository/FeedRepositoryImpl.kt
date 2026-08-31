package com.kliq.app.data.repository

import com.kliq.app.data.generated.*
import com.kliq.app.data.local.dao.FeedDao
import com.kliq.app.data.local.entities.FeedCommentEntity
import com.kliq.app.data.local.entities.FeedPostEntity
import com.kliq.app.data.local.entities.StoryEntity
import com.kliq.app.data.model.FeedComment
import com.kliq.app.data.model.FeedPost
import com.kliq.app.data.model.Story
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val feedDao: FeedDao,
    private val clubDao: com.kliq.app.data.local.dao.ClubDao? = null,
    private val socialDao: com.kliq.app.data.local.dao.SocialDao? = null,
    private val kliqConnector: KliqConnectorConnector? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FeedRepository {

    private fun getTodayDateString(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
    }

    override fun getFeedPosts(currentUserId: String): Flow<List<FeedPost>> {
        return feedDao.getFeedPosts()
            .map { entities ->
                val today = getTodayDateString()
                val mapped = entities.map { entity ->
                    val domain = entity.toDomain()
                    val effectiveFlames = if (entity.flameDate == today) entity.flameCount else 0
                    domain.copy(flameCount = effectiveFlames)
                }

                if (currentUserId.isBlank()) {
                    mapped
                } else {
                    mapped.filter { post ->
                        if (!post.isFollowersOnly) {
                            true
                        } else {
                            post.authorUserId == currentUserId || (socialDao?.isFriendFlow(currentUserId, post.authorUserId) != null)
                        }
                    }
                }
            }
            .flowOn(ioDispatcher)
    }

    override fun getPinnedEvents(): Flow<List<FeedPost>> {
        val today = getTodayDateString()
        return feedDao.getPinnedEvents()
            .map { entities ->
                entities.map { entity ->
                    val domain = entity.toDomain()
                    val effectiveFlames = if (entity.flameDate == today) entity.flameCount else 0
                    domain.copy(flameCount = effectiveFlames)
                }
            }
            .flowOn(ioDispatcher)
    }

    override fun getFeedPostsByAuthor(authorUserId: String): Flow<List<FeedPost>> {
        return feedDao.getFeedPostsByAuthor(authorUserId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun syncFeedPosts(): Result<Unit> = withContext(ioDispatcher) {
        try {
            kliqConnector?.let { connector ->
                val response = connector.getFeedPosts.execute()
                val remotePosts = response.data.feedPosts.map { p ->
                    val existing = feedDao.getFeedPostById(p.id)
                    FeedPostEntity(
                        id = p.id,
                        authorUserId = p.authorUserId,
                        authorName = p.authorName,
                        authorAvatarUrl = p.authorAvatarUrl,
                        contentText = p.contentText,
                        imageUrl = p.imageUrl,
                        clubId = p.clubId,
                        clubName = p.clubName,
                        locationAddress = p.locationName,
                        latitude = null,
                        longitude = null,
                        isEventPinned = p.isEventPinned,
                        isFollowersOnly = false,
                        createdAtMs = p.createdAtMs,
                        likeCount = p.likeCount,
                        isLikedByMe = existing?.isLikedByMe ?: false,
                        commentCount = p.commentCount,
                        flameCount = 0,
                        flameDate = ""
                    )
                }
                if (remotePosts.isNotEmpty()) {
                    feedDao.insertFeedPosts(remotePosts)
                }
                timber.log.Timber.d("DataConnect: Synced %d feed posts from Cloud SQL", remotePosts.size)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "DataConnect: syncFeedPosts error: %s", e.message)
            Result.failure(e)
        }
    }

    override fun getStories(): Flow<List<Story>> {
        val threeHoursAgoMs = System.currentTimeMillis() - 3 * 3600 * 1000L
        return feedDao.getStories()
            .map { entities ->
                entities
                    .filter { it.createdAtMs >= threeHoursAgoMs }
                    .map { it.toDomain() }
            }
            .flowOn(ioDispatcher)
    }

    override fun getCommentsForPost(postId: String): Flow<List<FeedComment>> {
        return feedDao.getCommentsForPost(postId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun createPost(
        authorUserId: String,
        authorName: String,
        contentText: String,
        clubId: String?,
        clubName: String?,
        imageUrl: String?,
        locationAddress: String?,
        latitude: Double?,
        longitude: Double?,
        isEventPinned: Boolean,
        isFollowersOnly: Boolean
    ): Result<FeedPost> = withContext(ioDispatcher) {
        val trimmedText = contentText.trim()
        if (trimmedText.isEmpty() && imageUrl.isNullOrBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("Ein Beitrag darf nicht leer sein.")
            )
        }

        try {
            val postId = "post_${UUID.randomUUID()}"
            val entity = FeedPostEntity(
                id = postId,
                authorUserId = authorUserId,
                authorName = authorName,
                contentText = trimmedText,
                imageUrl = imageUrl,
                clubId = clubId,
                clubName = clubName,
                locationAddress = locationAddress,
                latitude = latitude,
                longitude = longitude,
                isEventPinned = isEventPinned,
                isFollowersOnly = isFollowersOnly,
                createdAtMs = System.currentTimeMillis()
            )
            feedDao.insertFeedPost(entity)

            if (isEventPinned && latitude != null && longitude != null && clubDao != null) {
                val eventClubEntity = com.kliq.app.data.local.entities.ClubEntity(
                    id = postId,
                    name = clubName ?: trimmedText.take(40),
                    latitude = latitude,
                    longitude = longitude,
                    address = locationAddress ?: "Klagenfurt",
                    category = "Event",
                    averageRating = 5.0,
                    rating = 5.0f,
                    imageUrl = imageUrl ?: "",
                    region = "Klagenfurt",
                    city = "Klagenfurt",
                    externalSearchTags = trimmedText,
                    flameCount = 0
                )
                clubDao.insertClub(eventClubEntity)
            }

            kliqConnector?.let { connector ->
                try {
                    connector.createFeedPost.execute(
                        id = postId,
                        authorUserId = authorUserId,
                        authorName = authorName,
                        contentText = trimmedText,
                        createdAtMs = entity.createdAtMs
                    ) {
                        this.imageUrl = imageUrl
                        this.clubId = clubId
                        this.clubName = clubName
                        this.locationName = locationAddress ?: clubName
                        this.isEventPinned = isEventPinned
                    }
                } catch (e: Exception) {
                    timber.log.Timber.d("DataConnect createFeedPost error: %s", e.message)
                }
            }

            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun togglePostHype(postId: String, userId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            val today = getTodayDateString()
            val current = feedDao.getFeedPostById(postId) ?: return@withContext Result.failure(NoSuchElementException("Post not found"))
            val currentFlames = if (current.flameDate == today) current.flameCount else 0
            val newFlames = currentFlames + 1
            feedDao.updateFlameCount(postId, newFlames, today)
            clubDao?.updateFlameCount(postId, newFlames, today)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleLike(postId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            val current = feedDao.getFeedPostById(postId)
                ?: return@withContext Result.failure(
                    NoSuchElementException("Beitrag $postId existiert nicht.")
                )

            val nextLiked = !current.isLikedByMe
            val nextCount = if (nextLiked) {
                current.likeCount + 1
            } else {
                (current.likeCount - 1).coerceAtLeast(0)
            }

            feedDao.updateLikeState(postId = postId, likeCount = nextCount, isLiked = nextLiked)
            Result.success(nextLiked)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addComment(
        postId: String,
        authorUserId: String,
        authorName: String,
        text: String
    ): Result<FeedComment> = withContext(ioDispatcher) {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) {
            return@withContext Result.failure(
                IllegalArgumentException("Ein Kommentar darf nicht leer sein.")
            )
        }

        try {
            val entity = FeedCommentEntity(
                id = "cmt_${UUID.randomUUID()}",
                postId = postId,
                authorUserId = authorUserId,
                authorName = authorName,
                text = trimmedText,
                createdAtMs = System.currentTimeMillis()
            )
            feedDao.insertComment(entity)

            val commentCount = feedDao.getCommentsForPost(postId).first().size
            feedDao.updateCommentCount(postId = postId, commentCount = commentCount)

            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createStory(
        authorUserId: String,
        authorName: String,
        imageUrl: String,
        avatarUrl: String?,
        headline: String,
        clubName: String?
    ): Result<Story> = withContext(ioDispatcher) {
        try {
            val entity = StoryEntity(
                id = "story_${UUID.randomUUID()}",
                authorUserId = authorUserId,
                authorName = authorName,
                avatarUrl = avatarUrl,
                imageUrl = imageUrl,
                headline = headline,
                clubName = clubName,
                createdAtMs = System.currentTimeMillis(),
                isSeen = true
            )
            feedDao.insertStory(entity)
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markStoryAsSeen(storyId: String) = withContext(ioDispatcher) {
        feedDao.markStoryAsSeen(storyId)
    }

    override suspend fun deleteStory(storyId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            feedDao.deleteStory(storyId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: String) = withContext(ioDispatcher) {
        feedDao.deleteFeedPost(postId)
    }

    private fun FeedPostEntity.toDomain(): FeedPost = FeedPost(
        id = id,
        authorUserId = authorUserId,
        authorName = authorName,
        authorAvatarUrl = authorAvatarUrl,
        contentText = contentText,
        imageUrl = imageUrl,
        clubId = clubId,
        clubName = clubName,
        locationAddress = locationAddress,
        latitude = latitude,
        longitude = longitude,
        isEventPinned = isEventPinned,
        isFollowersOnly = isFollowersOnly,
        createdAtMs = createdAtMs,
        likeCount = likeCount,
        isLikedByMe = isLikedByMe,
        commentCount = commentCount,
        flameCount = flameCount,
        flameDate = flameDate
    )

    private fun FeedCommentEntity.toDomain(): FeedComment = FeedComment(
        id = id,
        postId = postId,
        authorUserId = authorUserId,
        authorName = authorName,
        text = text,
        createdAtMs = createdAtMs
    )

    private fun StoryEntity.toDomain(): Story = Story(
        id = id,
        authorUserId = authorUserId,
        authorName = authorName,
        avatarUrl = avatarUrl,
        imageUrl = imageUrl,
        headline = headline,
        clubName = clubName,
        createdAtMs = createdAtMs,
        isSeen = isSeen
    )
}
