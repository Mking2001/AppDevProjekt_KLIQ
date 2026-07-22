package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val profilePictureUrl: String?,
    val bio: String?,
    val phoneNumber: String? = null,
    val isVerified: Boolean = false,
    val updatedAtTimestampMs: Long = 0L
)
