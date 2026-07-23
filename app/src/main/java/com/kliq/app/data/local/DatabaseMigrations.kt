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

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `reviews` RENAME TO `reviews_old`")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `reviews` (`id` TEXT NOT NULL, `reviewerUserId` TEXT NOT NULL, `targetUserId` TEXT DEFAULT NULL, `clubId` TEXT DEFAULT NULL, `eventId` TEXT DEFAULT NULL, `rating` INTEGER NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `verificationMethod` TEXT NOT NULL, `isVerified` INTEGER NOT NULL, `reviewerUsername` TEXT NOT NULL DEFAULT '', `reviewerAvatarUrl` TEXT DEFAULT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`clubId`) REFERENCES `clubs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`reviewerUserId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`eventId`) REFERENCES `events`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            )
            db.execSQL(
                "INSERT INTO `reviews` (`id`, `reviewerUserId`, `clubId`, `rating`, `text`, `timestamp`, `verificationMethod`, `isVerified`) SELECT `id`, `userId`, `clubId`, `rating`, `text`, `timestamp`, `status`, CASE WHEN `status` = 'VERIFIED' THEN 1 ELSE 0 END FROM `reviews_old`"
            )
            db.execSQL("DROP TABLE `reviews_old`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_clubId` ON `reviews` (`clubId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_reviewerUserId` ON `reviews` (`reviewerUserId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_eventId` ON `reviews` (`eventId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_targetUserId` ON `reviews` (`targetUserId`)")
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Migrate chats table
            db.execSQL("ALTER TABLE `chats` RENAME TO `chats_old`")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `chats` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `cityRegion` TEXT DEFAULT NULL, `lastMessageText` TEXT NOT NULL, `lastMessageTimestampMs` INTEGER NOT NULL, `lastMessageTimestampIso` TEXT NOT NULL DEFAULT '', `avatarInitial` TEXT NOT NULL, `avatarUrl` TEXT DEFAULT NULL, `unreadCount` INTEGER NOT NULL DEFAULT 0, `chatType` TEXT NOT NULL, `isOnline` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`))"
            )
            db.execSQL(
                "INSERT INTO `chats` (`id`, `name`, `lastMessageText`, `lastMessageTimestampMs`, `avatarInitial`, `unreadCount`, `chatType`, `isOnline`) SELECT `id`, `name`, `lastMessageText`, `lastMessageTimestamp`, `avatarInitial`, `unreadCount`, `chatType`, `isOnline` FROM `chats_old`"
            )
            db.execSQL("DROP TABLE `chats_old`")

            // Migrate messages table
            db.execSQL("ALTER TABLE `messages` RENAME TO `messages_old`")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `chatId` TEXT NOT NULL, `senderUserId` TEXT NOT NULL DEFAULT '', `senderName` TEXT NOT NULL, `text` TEXT NOT NULL, `timestampMs` INTEGER NOT NULL, `timestampIso` TEXT NOT NULL DEFAULT '', `mediaUrl` TEXT DEFAULT NULL, `status` TEXT NOT NULL DEFAULT 'SENT', `isMine` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`chatId`) REFERENCES `chats`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            )
            db.execSQL(
                "INSERT INTO `messages` (`id`, `chatId`, `senderName`, `text`, `timestampMs`, `isMine`) SELECT `id`, `chatId`, `senderName`, `text`, `timestamp`, `isMine` FROM `messages_old`"
            )
            db.execSQL("DROP TABLE `messages_old`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_chatId` ON `messages` (`chatId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_senderUserId` ON `messages` (`senderUserId`)")
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // User entity upgrades
            db.execSQL("ALTER TABLE `users` ADD COLUMN `phoneNumber` TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE `users` ADD COLUMN `isVerified` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `users` ADD COLUMN `updatedAtTimestampMs` INTEGER NOT NULL DEFAULT 0")

            // Club entity upgrades
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `isPromoted` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `city` TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE `clubs` ADD COLUMN `postalCode` TEXT NOT NULL DEFAULT ''")

            // Event entity upgrades
            db.execSQL("ALTER TABLE `events` ADD COLUMN `capacityLimit` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `events` ADD COLUMN `isCancelled` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `events` ADD COLUMN `category` TEXT NOT NULL DEFAULT ''")

            // Review entity upgrades
            db.execSQL("ALTER TABLE `reviews` ADD COLUMN `helpfulVotesCount` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `reviews` ADD COLUMN `flaggedCount` INTEGER NOT NULL DEFAULT 0")

            // Chat entity upgrades
            db.execSQL("ALTER TABLE `chats` ADD COLUMN `isPinned` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `chats` ADD COLUMN `isMuted` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `chats` ADD COLUMN `lastReadMessageId` TEXT DEFAULT NULL")

            // Message entity upgrades
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `replyToMessageId` TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `isEdited` INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `user_preferences` ADD COLUMN `searchIntent` TEXT NOT NULL DEFAULT 'BOTH'")
        }
    }

    // Array of all migrations. Scalable strategy for providing them to the builder.
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7,
        MIGRATION_7_8
    )
}
