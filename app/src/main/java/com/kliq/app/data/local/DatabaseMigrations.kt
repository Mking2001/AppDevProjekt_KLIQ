package com.kliq.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // User Preferences
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `user_preferences` (`userId` TEXT NOT NULL, `isDarkMode` INTEGER NOT NULL, `searchRadiusKm` INTEGER NOT NULL, `pushNotificationsEnabled` INTEGER NOT NULL, PRIMARY KEY(`userId`), FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            )
            // Clubs
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `clubs` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `rating` REAL NOT NULL, `imageUrl` TEXT NOT NULL, `region` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `currentCapacityPercent` INTEGER NOT NULL, `malePercentage` INTEGER NOT NULL, `femalePercentage` INTEGER NOT NULL, `totalLiveVisitors` INTEGER NOT NULL, PRIMARY KEY(`id`))"
            )
            // Events
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `events` (`id` TEXT NOT NULL, `clubId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `price` TEXT NOT NULL, `time` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`clubId`) REFERENCES `clubs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            )
            // Reviews
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `reviews` (`id` TEXT NOT NULL, `clubId` TEXT NOT NULL, `userId` TEXT NOT NULL, `rating` INTEGER NOT NULL, `text` TEXT NOT NULL, `status` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`clubId`) REFERENCES `clubs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            )
            // Chats
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `chats` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `lastMessageText` TEXT NOT NULL, `lastMessageTimestamp` INTEGER NOT NULL, `avatarInitial` TEXT NOT NULL, `unreadCount` INTEGER NOT NULL, `chatType` TEXT NOT NULL, `isOnline` INTEGER NOT NULL, PRIMARY KEY(`id`))"
            )
            // Messages
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `chatId` TEXT NOT NULL, `senderName` TEXT NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `isMine` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`chatId`) REFERENCES `chats`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add search fields to clubs
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `externalSearchTags` TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `websiteUrl` TEXT DEFAULT NULL")
            // Add search fields to events
            db.execSQL("ALTER TABLE `events` ADD COLUMN `searchKeywords` TEXT NOT NULL DEFAULT ''")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add GPS coordinates, geofence, rating, and opening hours to clubs table
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `latitude` REAL NOT NULL DEFAULT 0.0")
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `longitude` REAL NOT NULL DEFAULT 0.0")
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `address` TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `geofenceRadiusMeters` REAL NOT NULL DEFAULT 200.0")
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `averageRating` REAL NOT NULL DEFAULT 0.0")
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `openingHoursJson` TEXT NOT NULL DEFAULT ''")

            // Add timestamps, special offers, and image URL to events table
            db.execSQL("ALTER TABLE `events` ADD COLUMN `startTime` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `events` ADD COLUMN `endTime` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `events` ADD COLUMN `specialOffersJson` TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE `events` ADD COLUMN `imageUrl` TEXT DEFAULT NULL")
        }
    }

    // Array of all migrations. Scalable strategy for providing them to the builder.
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4
    )
}
