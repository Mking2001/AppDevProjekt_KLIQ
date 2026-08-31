package com.kliq.app.data.repository

import com.kliq.app.data.model.FeedComment
import com.kliq.app.data.model.FeedPost
import com.kliq.app.data.model.Story
import kotlinx.coroutines.flow.Flow

interface FeedRepository {

    fun getFeedPosts(): Flow<List<FeedPost>>

    fun getFeedPostsByAuthor(authorUserId: String): Flow<List<FeedPost>>

    suspend fun syncFeedPosts(): Result<Unit>

    fun getStories(): Flow<List<Story>>

    fun getCommentsForPost(postId: String): Flow<List<FeedComment>>

    suspend fun createPost(
        authorUserId: String,
        authorName: String,
        contentText: String,
        clubId: String? = null,
        clubName: String? = null,
        imageUrl: String? = null
    ): Result<FeedPost>

    suspend fun toggleLike(postId: String): Result<Boolean>

    suspend fun addComment(
        postId: String,
        authorUserId: String,
        authorName: String,
        text: String
    ): Result<FeedComment>

    suspend fun createStory(
        authorUserId: String,
        authorName: String,
        imageUrl: String,
        avatarUrl: String? = null,
        headline: String = "",
        clubName: String? = null
    ): Result<Story> = Result.failure(NotImplementedError())

    suspend fun markStoryAsSeen(storyId: String)

    suspend fun deleteStory(storyId: String): Result<Unit> = Result.success(Unit)

    suspend fun deletePost(postId: String)
}
