package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kliq.app.data.model.MessageStatus

/**
 * DB-Entity fuer 1-zu-1 Direktnachrichten zwischen zwei Nutzern.
 */
@Entity(
    tableName = "direct_messages",
    indices = [
        Index(value = ["senderId"]),
        Index(value = ["receiverId"]),
        Index(value = ["timestamp"])
    ]
)
data class DirectMessageEntity(
    @PrimaryKey
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long,
    val timestampIso: String = "",
    val deliveryStatus: MessageStatus = MessageStatus.SENT,
    val isEncrypted: Boolean = true,
    val encryptionAlgorithm: String = "AES-256-GCM",
    val mediaUrl: String? = null
)
