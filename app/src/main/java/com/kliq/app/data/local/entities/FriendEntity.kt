package com.kliq.app.data.local.entities

import androidx.room.Entity

@Entity(tableName = "friends", primaryKeys = ["userId", "friendUserId"])
data class FriendEntity(
    val userId: String,
    val friendUserId: String,
    val status: String = "ACCEPTED", // PENDING, ACCEPTED, REJECTED
    val isQrVerified: Boolean = true,
    val createdAtTimestampMs: Long = System.currentTimeMillis()
)
