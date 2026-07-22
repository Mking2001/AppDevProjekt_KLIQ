package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kliq.app.data.model.ChatType

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val cityRegion: String? = null,
    val lastMessageText: String,
    val lastMessageTimestampMs: Long,
    val lastMessageTimestampIso: String = "",
    val avatarInitial: String,
    val avatarUrl: String? = null,
    val unreadCount: Int = 0,
    val chatType: ChatType,
    val isOnline: Boolean = false,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val lastReadMessageId: String? = null
)
