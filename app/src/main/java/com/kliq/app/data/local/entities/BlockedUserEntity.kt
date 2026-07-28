package com.kliq.app.data.local.entities

import androidx.room.Entity

/**
 * Room Database Entity für die Tabelle 'blocked_users'.
 * Speichert verhängte Nutzersperren lokal ab.
 *
 * Primary Key besteht aus der Kombination aus [userId] und [blockedUserId].
 */
@Entity(tableName = "blocked_users", primaryKeys = ["userId", "blockedUserId"])
data class BlockedUserEntity(
    val userId: String,
    val blockedUserId: String,
    val reason: String? = null,
    val blockedAtTimestampMs: Long = System.currentTimeMillis()
)
