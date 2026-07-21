package com.kliq.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity

@Database(
    entities = [
        UserEntity::class,
        UserPreferencesEntity::class,
        ClubEntity::class,
        EventEntity::class,
        ReviewEntity::class,
        ChatEntity::class,
        MessageEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class KliqDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clubDao(): ClubDao
    abstract fun reviewDao(): ReviewDao
    abstract fun chatDao(): ChatDao
}
