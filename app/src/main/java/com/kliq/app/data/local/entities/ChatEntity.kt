package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kliq.app.data.model.ChatType

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lastMessageText: String,
    val lastMessageTimestamp: Long,
    val avatarInitial: String,
    val unreadCount: Int = 0,
    val chatType: ChatType,
    val isOnline: Boolean = false
)
