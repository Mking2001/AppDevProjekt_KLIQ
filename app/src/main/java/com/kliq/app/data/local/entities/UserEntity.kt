package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val age: Int? = null,
    val hometown: String? = null,
    val profilePictureUrl: String? = null,
    val bio: String? = null
)
