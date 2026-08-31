package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.FeedCommentEntity
import com.kliq.app.data.local.entities.FeedPostEntity
import com.kliq.app.data.local.entities.StoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Query("SELECT * FROM feed_posts ORDER BY createdAtMs DESC")
    fun getFeedPosts(): Flow<List<FeedPostEntity>>

    @Query("SELECT * FROM feed_posts WHERE authorUserId = :userId ORDER BY createdAtMs DESC")
    fun getFeedPostsByAuthor(userId: String): Flow<List<FeedPostEntity>>

    @Query("SELECT COUNT(*) FROM feed_posts WHERE authorUserId = :userId")
    fun getFeedPostCountByAuthor(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM feed_posts")
    suspend fun countFeedPosts(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedPost(post: FeedPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedPosts(posts: List<FeedPostEntity>)

    @Query("UPDATE feed_posts SET likeCount = :likeCount, isLikedByMe = :isLiked WHERE id = :postId")
    suspend fun updateLikeState(postId: String, likeCount: Int, isLiked: Boolean)

    @Query("SELECT * FROM feed_posts WHERE id = :postId LIMIT 1")
    suspend fun getFeedPostById(postId: String): FeedPostEntity?

    @Query("DELETE FROM feed_posts WHERE id = :postId")
    suspend fun deleteFeedPost(postId: String)

    @Query("SELECT * FROM feed_comments WHERE postId = :postId ORDER BY createdAtMs ASC")
    fun getCommentsForPost(postId: String): Flow<List<FeedCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: FeedCommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<FeedCommentEntity>)

    @Query("UPDATE feed_posts SET commentCount = :commentCount WHERE id = :postId")
    suspend fun updateCommentCount(postId: String, commentCount: Int)

    @Query("SELECT * FROM stories ORDER BY isSeen ASC, createdAtMs DESC")
    fun getStories(): Flow<List<StoryEntity>>

    @Query("SELECT COUNT(*) FROM stories")
    suspend fun countStories(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET isSeen = 1 WHERE id = :storyId")
    suspend fun markStoryAsSeen(storyId: String)

    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStory(storyId: String)

    @Query("DELETE FROM stories WHERE createdAtMs < :minCreatedAtMs")
    suspend fun deleteExpiredStories(minCreatedAtMs: Long)

    @Query("DELETE FROM stories WHERE id LIKE 'story_kf_%'")
    suspend fun deleteMockStories()

    @Query("DELETE FROM feed_posts WHERE id LIKE 'post_kf_%'")
    suspend fun deleteMockPosts()

    @Query("DELETE FROM feed_comments WHERE id LIKE 'cmt_kf_%'")
    suspend fun deleteMockComments()

    @Query("DELETE FROM feed_posts WHERE authorUserId = :userId")
    suspend fun deletePostsByAuthor(userId: String)

    @Query("DELETE FROM feed_comments WHERE authorUserId = :userId")
    suspend fun deleteCommentsByAuthor(userId: String)

    @Query("DELETE FROM stories WHERE authorUserId = :userId")
    suspend fun deleteStoriesByAuthor(userId: String)

    @Query("DELETE FROM feed_posts")
    suspend fun deleteAllPosts()

    @Query("DELETE FROM feed_comments")
    suspend fun deleteAllComments()

    @Query("DELETE FROM stories")
    suspend fun deleteAllStories()
}
