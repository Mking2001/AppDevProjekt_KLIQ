package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.MessageType

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chatId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["chatId"]),
        Index(value = ["senderUserId"])
    ]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderUserId: String = "",
    val senderName: String,
    val text: String,
    val timestampMs: Long,
    val timestampIso: String = "",
    val mediaUrl: String? = null,
    val messageType: MessageType = MessageType.TEXT,
    val thumbnailUrl: String? = null,
    val aspectRatio: Float = 1.0f,
    val mediaWidth: Int = 0,
    val mediaHeight: Int = 0,
    val caption: String? = null,
    val audioDurationMs: Long = 0L,
    val status: MessageStatus = MessageStatus.SENT,
    val deliveredAtMs: Long? = null,
    val readAtMs: Long? = null,
    val isMine: Boolean,
    val replyToMessageId: String? = null,
    val isEdited: Boolean = false
)
