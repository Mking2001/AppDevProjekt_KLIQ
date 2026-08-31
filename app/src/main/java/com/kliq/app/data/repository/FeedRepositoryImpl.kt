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

/**
 * Room-basierte Implementierung des [FeedRepository].
 */
@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val feedDao: FeedDao,
    private val kliqConnector: KliqConnectorConnector? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FeedRepository {

    override fun getFeedPosts(): Flow<List<FeedPost>> {
        return feedDao.getFeedPosts()
            .map { entities -> entities.map { it.toDomain() } }
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
                    FeedPostEntity(
                        id = p.id,
                        authorUserId = p.authorUserId,
                        authorName = p.authorName,
                        authorAvatarUrl = p.authorAvatarUrl,
                        contentText = p.contentText,
                        imageUrl = p.imageUrl,
                        clubId = p.clubId,
                        clubName = p.clubName,
                        createdAtMs = p.createdAtMs,
                        likeCount = p.likeCount,
                        commentCount = p.commentCount
                    )
                }
                if (remotePosts.isNotEmpty()) {
                    feedDao.insertFeedPosts(remotePosts)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
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
        imageUrl: String?
    ): Result<FeedPost> = withContext(ioDispatcher) {
        val trimmedText = contentText.trim()
        if (trimmedText.isEmpty()) {
            return@withContext Result.failure(
                IllegalArgumentException("Ein Beitrag darf nicht leer sein.")
            )
        }

        try {
            val entity = FeedPostEntity(
                id = "post_${UUID.randomUUID()}",
                authorUserId = authorUserId,
                authorName = authorName,
                contentText = trimmedText,
                imageUrl = imageUrl,
                clubId = clubId,
                clubName = clubName,
                createdAtMs = System.currentTimeMillis()
            )
            feedDao.insertFeedPost(entity)
            Result.success(entity.toDomain())
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
        createdAtMs = createdAtMs,
        likeCount = likeCount,
        isLikedByMe = isLikedByMe,
        commentCount = commentCount
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
