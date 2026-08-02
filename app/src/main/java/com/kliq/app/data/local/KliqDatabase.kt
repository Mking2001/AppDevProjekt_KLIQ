package com.kliq.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kliq.app.data.local.dao.BlockedUserDao
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ClubOfferDao
import com.kliq.app.data.local.dao.DirectMessageDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.dao.LocationDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.SocialDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.BlockedUserEntity
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.ClubOfferEntity
import com.kliq.app.data.local.entities.DirectMessageEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.local.entities.FriendEntity
import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.local.entities.ReviewEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.local.entities.VisitedLogEntity

@Database(
    entities = [
        UserEntity::class,
        UserPreferencesEntity::class,
        ClubEntity::class,
        EventEntity::class,
        ClubOfferEntity::class,
        ReviewEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        DirectMessageEntity::class,
        LocationEntity::class,
        FriendEntity::class,
        VisitedLogEntity::class,
        BlockedUserEntity::class
    ],
    version = 19,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class KliqDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clubDao(): ClubDao
    abstract fun eventDao(): EventDao
    abstract fun clubOfferDao(): ClubOfferDao
    abstract fun reviewDao(): ReviewDao
    abstract fun chatDao(): ChatDao
    abstract fun directMessageDao(): DirectMessageDao
    abstract fun locationDao(): LocationDao
    abstract fun socialDao(): SocialDao
    abstract fun visitedLogDao(): VisitedLogDao
    abstract fun blockedUserDao(): BlockedUserDao
}
