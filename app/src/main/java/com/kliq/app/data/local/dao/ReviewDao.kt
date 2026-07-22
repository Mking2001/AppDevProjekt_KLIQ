package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE clubId = :clubId ORDER BY timestamp DESC")
    fun getReviewsForClub(clubId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE clubId = :clubId AND isVerified = 1 ORDER BY timestamp DESC")
    fun getVerifiedReviewsForClub(clubId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE eventId = :eventId ORDER BY timestamp DESC")
    fun getReviewsForEvent(eventId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE targetUserId = :targetUserId ORDER BY timestamp DESC")
    fun getReviewsForTargetUser(targetUserId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE reviewerUserId = :reviewerUserId ORDER BY timestamp DESC")
    fun getReviewsByReviewer(reviewerUserId: String): Flow<List<ReviewEntity>>

    @Query("SELECT AVG(rating) FROM reviews WHERE clubId = :clubId")
    fun getAverageRatingForClub(clubId: String): Flow<Double?>

    @Query("SELECT AVG(rating) FROM reviews WHERE eventId = :eventId")
    fun getAverageRatingForEvent(eventId: String): Flow<Double?>

    @Query("UPDATE reviews SET helpfulVotesCount = helpfulVotesCount + 1 WHERE id = :reviewId")
    suspend fun incrementHelpfulVotes(reviewId: String)

    @Query("UPDATE reviews SET flaggedCount = flaggedCount + 1 WHERE id = :reviewId")
    suspend fun flagReview(reviewId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: String)
}
