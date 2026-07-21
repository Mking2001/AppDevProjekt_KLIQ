package com.kliq.app.data.local.entities

import androidx.annotation.IntRange
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kliq.app.data.model.ReviewVerificationMethod

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = ClubEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("clubId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("reviewerUserId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("eventId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["clubId"]),
        Index(value = ["reviewerUserId"]),
        Index(value = ["eventId"]),
        Index(value = ["targetUserId"])
    ]
)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val reviewerUserId: String,
    val targetUserId: String? = null,
    val clubId: String? = null,
    val eventId: String? = null,
    @IntRange(from = 1, to = 5) val rating: Int, // 1-5 stars
    val text: String,
    val timestamp: Long,
    val verificationMethod: ReviewVerificationMethod = ReviewVerificationMethod.UNVERIFIED,
    val isVerified: Boolean = false,
    val reviewerUsername: String = "",
    val reviewerAvatarUrl: String? = null
)
