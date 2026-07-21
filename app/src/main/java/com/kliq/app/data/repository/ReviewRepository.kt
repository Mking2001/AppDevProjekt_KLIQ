package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val reviewDao: ReviewDao
) {
    fun getReviewsForClub(clubId: String): Flow<List<ReviewEntity>> = reviewDao.getReviewsForClub(clubId)

    suspend fun addReview(review: ReviewEntity) {
        reviewDao.insertReview(review)
        // Optionally sync to backend
    }
}
