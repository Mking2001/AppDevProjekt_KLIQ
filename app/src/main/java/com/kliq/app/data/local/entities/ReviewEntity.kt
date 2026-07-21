package com.kliq.app.data.local.entities

import androidx.annotation.IntRange
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class VerificationStatus {
    VERIFIED,
    UNVERIFIED
}

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
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val clubId: String,
    val userId: String,
    @IntRange(from = 1, to = 5) val rating: Int, // 1-5 stars
    val text: String,
    val status: VerificationStatus,
    val timestamp: Long
)
