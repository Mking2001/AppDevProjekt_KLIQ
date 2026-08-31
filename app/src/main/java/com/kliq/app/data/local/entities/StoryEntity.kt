package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stories",
    indices = [Index(value = ["createdAtMs"])]
)
data class StoryEntity(
    @PrimaryKey val id: String,
    val authorUserId: String,
    val authorName: String,
    val avatarUrl: String? = null,
    val imageUrl: String? = null,
    val headline: String = "",
    val clubName: String? = null,
    val createdAtMs: Long,
    val isSeen: Boolean = false
)
