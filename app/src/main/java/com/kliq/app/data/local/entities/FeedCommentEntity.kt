package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "feed_comments",
    foreignKeys = [
        ForeignKey(
            entity = FeedPostEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("postId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["postId"])]
)
data class FeedCommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorUserId: String,
    val authorName: String,
    val text: String,
    val createdAtMs: Long
)
