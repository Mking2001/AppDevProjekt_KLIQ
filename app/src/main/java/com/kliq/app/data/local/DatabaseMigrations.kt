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

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `user_preferences` ADD COLUMN `smokingHabit` TEXT NOT NULL DEFAULT 'NEVER'")
            db.execSQL("ALTER TABLE `user_preferences` ADD COLUMN `drinkingHabit` TEXT NOT NULL DEFAULT 'NEVER'")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `direct_messages` (`messageId` TEXT NOT NULL, `senderId` TEXT NOT NULL, `receiverId` TEXT NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `timestampIso` TEXT NOT NULL DEFAULT '', `deliveryStatus` TEXT NOT NULL DEFAULT 'SENT', `isEncrypted` INTEGER NOT NULL DEFAULT 1, `encryptionAlgorithm` TEXT NOT NULL DEFAULT 'AES-256-GCM', `mediaUrl` TEXT DEFAULT NULL, PRIMARY KEY(`messageId`))"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_direct_messages_senderId` ON `direct_messages` (`senderId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_direct_messages_receiverId` ON `direct_messages` (`receiverId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_direct_messages_timestamp` ON `direct_messages` (`timestamp`)")
        }
    }

    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `user_locations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `accuracy` REAL NOT NULL, `timestampMs` INTEGER NOT NULL, `speed` REAL NOT NULL DEFAULT 0.0)"
            )
        }
    }

    val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `visited_logs` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `clubId` TEXT NOT NULL, `clubName` TEXT NOT NULL, `visitedAtTimestamp` INTEGER NOT NULL, `isVerifiedByGps` INTEGER NOT NULL, PRIMARY KEY(`id`))"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_visited_logs_userId` ON `visited_logs` (`userId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_visited_logs_clubId` ON `visited_logs` (`clubId`)")
        }
    }

    val MIGRATION_13_14 = object : Migration(13, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `blocked_users` (`userId` TEXT NOT NULL, `blockedUserId` TEXT NOT NULL, `reason` TEXT, `blockedAtTimestampMs` INTEGER NOT NULL, PRIMARY KEY(`userId`, `blockedUserId`))"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_blocked_users_userId` ON `blocked_users` (`userId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_blocked_users_blockedUserId` ON `blocked_users` (`blockedUserId`)")
        }
    }

    val MIGRATION_14_15 = object : Migration(14, 15) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `messageType` TEXT NOT NULL DEFAULT 'TEXT'")
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `thumbnailUrl` TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `aspectRatio` REAL NOT NULL DEFAULT 1.0")
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `mediaWidth` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `mediaHeight` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `caption` TEXT DEFAULT NULL")

            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `messageType` TEXT NOT NULL DEFAULT 'TEXT'")
            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `thumbnailUrl` TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `aspectRatio` REAL NOT NULL DEFAULT 1.0")
            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `mediaWidth` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `mediaHeight` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `caption` TEXT DEFAULT NULL")
        }
    }

    val MIGRATION_15_16 = object : Migration(15, 16) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `deliveredAtMs` INTEGER DEFAULT NULL")
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `readAtMs` INTEGER DEFAULT NULL")
            db.execSQL("ALTER TABLE `messages` ADD COLUMN `audioDurationMs` INTEGER NOT NULL DEFAULT 0")

            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `deliveredAtMs` INTEGER DEFAULT NULL")
            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `readAtMs` INTEGER DEFAULT NULL")
            db.execSQL("ALTER TABLE `direct_messages` ADD COLUMN `audioDurationMs` INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_16_17 = object : Migration(16, 17) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `users` ADD COLUMN `gender` TEXT NOT NULL DEFAULT 'UNSPECIFIED'")
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
        MIGRATION_7_8,
        MIGRATION_8_9,
        MIGRATION_9_10,
        MIGRATION_12_13,
        MIGRATION_13_14,
        MIGRATION_14_15,
        MIGRATION_15_16,
        MIGRATION_16_17
    )
}
