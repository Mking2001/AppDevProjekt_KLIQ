package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserPreferencesEntity(
    @PrimaryKey val userId: String,
    val isDarkMode: Boolean = false,
    val searchRadiusKm: Int = 10,
    val pushNotificationsEnabled: Boolean = true
)
