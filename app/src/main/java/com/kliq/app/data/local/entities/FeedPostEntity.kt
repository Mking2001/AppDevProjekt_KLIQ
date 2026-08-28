package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room-Entity für einen Beitrag im Home-Feed.
 * Beiträge werden lokal persistiert, damit selbst erstellte Posts und
 * Like-Zustände einen Screen- oder Prozesswechsel überleben.
 */
@Entity(
    tableName = "feed_posts",
    indices = [Index(value = ["createdAtMs"]), Index(value = ["authorUserId"])]
)
data class FeedPostEntity(
    @PrimaryKey val id: String,
    val authorUserId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val contentText: String,
    val imageUrl: String? = null,
    val clubId: String? = null,
    val clubName: String? = null,
    val createdAtMs: Long,
    val likeCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val commentCount: Int = 0
)
