package com.kliq.app.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DatabaseMigrationTest {

    private val TEST_DB_NAME = "migration_test.db"
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(TEST_DB_NAME)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DB_NAME)
    }

    @Test
    fun migrate6To7_preservesExistingDataAndAddsNewFields() {
        // Step 1: Create Version 6 Database Schema manually
        val configV6 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB_NAME)
            .callback(object : SupportSQLiteOpenHelper.Callback(6) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    createV6Schema(db)
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    // No upgrade needed during initial creation
                }
            })
            .build()

        val helperV6 = FrameworkSQLiteOpenHelperFactory().create(configV6)
        val dbV6 = helperV6.writableDatabase

        // Insert test records into V6 database
        dbV6.execSQL(
            "INSERT INTO `users` (`id`, `username`, `email`, `profilePictureUrl`, `bio`) " +
            "VALUES ('u1', 'alex_night', 'alex@example.com', 'https://avatar.com/u1.jpg', 'Party enthusiast')"
        )

        dbV6.execSQL(
            "INSERT INTO `clubs` (`id`, `name`, `latitude`, `longitude`, `address`, `geofenceRadiusMeters`, " +
            "`averageRating`, `openingHoursJson`, `isFavorite`, `category`, `rating`, `imageUrl`, `region`, " +
            "`currentCapacityPercent`, `malePercentage`, `femalePercentage`, `totalLiveVisitors`, `externalSearchTags`, `websiteUrl`) " +
            "VALUES ('c1', 'Club Velvet', 48.137, 11.575, 'Munich Center 1', 200.0, 4.8, '{}', 1, 'Techno', 4.8, " +
            "'https://img.com/c1.jpg', 'Munich', 75, 45, 55, 300, 'techno,bar', 'https://clubvelvet.de')"
        )

        dbV6.execSQL(
            "INSERT INTO `events` (`id`, `clubId`, `title`, `description`, `startTime`, `endTime`, `price`, `time`, " +
            "`specialOffersJson`, `searchKeywords`, `imageUrl`) " +
            "VALUES ('e1', 'c1', 'Midnight Beats', 'Awesome music night', 1700000000000, 1700020000000, '15 EUR', '23:00', " +
            "'{}', 'beats,techno', 'https://img.com/e1.jpg')"
        )

        dbV6.execSQL(
            "INSERT INTO `reviews` (`id`, `reviewerUserId`, `targetUserId`, `clubId`, `eventId`, `rating`, `text`, " +
            "`timestamp`, `verificationMethod`, `isVerified`, `reviewerUsername`, `reviewerAvatarUrl`) " +
            "VALUES ('r1', 'u1', NULL, 'c1', 'e1', 5, 'Best venue ever!', 1700005000000, 'TICKET_SCAN', 1, 'alex_night', 'https://avatar.com/u1.jpg')"
        )

        dbV6.execSQL(
            "INSERT INTO `chats` (`id`, `name`, `cityRegion`, `lastMessageText`, `lastMessageTimestampMs`, `lastMessageTimestampIso`, " +
            "`avatarInitial`, `avatarUrl`, `unreadCount`, `chatType`, `isOnline`) " +
            "VALUES ('ch1', 'Munich Nightlife', 'Munich', 'See you there!', 1700010000000, '2026-07-22T23:00:00Z', 'M', " +
            "'https://img.com/ch1.jpg', 2, 'PUBLIC_CITY', 1)"
        )

        dbV6.execSQL(
            "INSERT INTO `messages` (`id`, `chatId`, `senderUserId`, `senderName`, `text`, `timestampMs`, `timestampIso`, " +
            "`mediaUrl`, `status`, `isMine`) " +
            "VALUES ('m1', 'ch1', 'u1', 'alex_night', 'See you there!', 1700010000000, '2026-07-22T23:00:00Z', NULL, 'SENT', 1)"
        )

        helperV6.close()

        // Step 2: Open database with Migration from 6 to 7
        val configV7 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB_NAME)
            .callback(object : SupportSQLiteOpenHelper.Callback(7) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    if (oldVersion == 6 && newVersion == 7) {
                        DatabaseMigrations.MIGRATION_6_7.migrate(db)
                    }
                }
            })
            .build()

        val helperV7 = FrameworkSQLiteOpenHelperFactory().create(configV7)
        val dbV7 = helperV7.writableDatabase

        // Step 3: Verify User table migration
        val userCursor = dbV7.query("SELECT * FROM `users` WHERE `id` = 'u1'")
        assertTrue(userCursor.moveToFirst())
        assertEquals("alex_night", userCursor.getString(userCursor.getColumnIndexOrThrow("username")))
        assertEquals("alex@example.com", userCursor.getString(userCursor.getColumnIndexOrThrow("email")))
        assertTrue(userCursor.isNull(userCursor.getColumnIndexOrThrow("phoneNumber")))
        assertEquals(0, userCursor.getInt(userCursor.getColumnIndexOrThrow("isVerified")))
        assertEquals(0L, userCursor.getLong(userCursor.getColumnIndexOrThrow("updatedAtTimestampMs")))
        userCursor.close()

        // Step 4: Verify Club table migration
        val clubCursor = dbV7.query("SELECT * FROM `clubs` WHERE `id` = 'c1'")
        assertTrue(clubCursor.moveToFirst())
        assertEquals("Club Velvet", clubCursor.getString(clubCursor.getColumnIndexOrThrow("name")))
        assertEquals("Munich Center 1", clubCursor.getString(clubCursor.getColumnIndexOrThrow("address")))
        assertEquals(0, clubCursor.getInt(clubCursor.getColumnIndexOrThrow("isPromoted")))
        assertEquals("", clubCursor.getString(clubCursor.getColumnIndexOrThrow("city")))
        assertEquals("", clubCursor.getString(clubCursor.getColumnIndexOrThrow("postalCode")))
        clubCursor.close()

        // Step 5: Verify Event table migration
        val eventCursor = dbV7.query("SELECT * FROM `events` WHERE `id` = 'e1'")
        assertTrue(eventCursor.moveToFirst())
        assertEquals("Midnight Beats", eventCursor.getString(eventCursor.getColumnIndexOrThrow("title")))
        assertEquals(0, eventCursor.getInt(eventCursor.getColumnIndexOrThrow("capacityLimit")))
        assertEquals(0, eventCursor.getInt(eventCursor.getColumnIndexOrThrow("isCancelled")))
        assertEquals("", eventCursor.getString(eventCursor.getColumnIndexOrThrow("category")))
        eventCursor.close()

        // Step 6: Verify Review table migration
        val reviewCursor = dbV7.query("SELECT * FROM `reviews` WHERE `id` = 'r1'")
        assertTrue(reviewCursor.moveToFirst())
        assertEquals("Best venue ever!", reviewCursor.getString(reviewCursor.getColumnIndexOrThrow("text")))
        assertEquals(0, reviewCursor.getInt(reviewCursor.getColumnIndexOrThrow("helpfulVotesCount")))
        assertEquals(0, reviewCursor.getInt(reviewCursor.getColumnIndexOrThrow("flaggedCount")))
        reviewCursor.close()

        // Step 7: Verify Chat table migration
        val chatCursor = dbV7.query("SELECT * FROM `chats` WHERE `id` = 'ch1'")
        assertTrue(chatCursor.moveToFirst())
        assertEquals("Munich Nightlife", chatCursor.getString(chatCursor.getColumnIndexOrThrow("name")))
        assertEquals(0, chatCursor.getInt(chatCursor.getColumnIndexOrThrow("isPinned")))
        assertEquals(0, chatCursor.getInt(chatCursor.getColumnIndexOrThrow("isMuted")))
        assertTrue(chatCursor.isNull(chatCursor.getColumnIndexOrThrow("lastReadMessageId")))
        chatCursor.close()

        // Step 8: Verify Message table migration
        val messageCursor = dbV7.query("SELECT * FROM `messages` WHERE `id` = 'm1'")
        assertTrue(messageCursor.moveToFirst())
        assertEquals("See you there!", messageCursor.getString(messageCursor.getColumnIndexOrThrow("text")))
        assertTrue(messageCursor.isNull(messageCursor.getColumnIndexOrThrow("replyToMessageId")))
        assertEquals(0, messageCursor.getInt(messageCursor.getColumnIndexOrThrow("isEdited")))
        messageCursor.close()

        helperV7.close()
    }

    @Test
    fun databaseMigrationManager_buildDatabase_succeeds() {
        val db = DatabaseMigrationManager.buildDatabase(context)
        assertNotNull(db)
        DatabaseMigrationManager.validateDatabaseSchema(db.openHelper.writableDatabase)
        db.close()
    }

    private fun createV6Schema(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `users` (" +
            "`id` TEXT NOT NULL, `username` TEXT NOT NULL, `email` TEXT NOT NULL, " +
            "`profilePictureUrl` TEXT, `bio` TEXT, PRIMARY KEY(`id`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `user_preferences` (" +
            "`userId` TEXT NOT NULL, `isDarkMode` INTEGER NOT NULL, `searchRadiusKm` INTEGER NOT NULL, " +
            "`pushNotificationsEnabled` INTEGER NOT NULL, PRIMARY KEY(`userId`), " +
            "FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `clubs` (" +
            "`id` TEXT NOT NULL, `name` TEXT NOT NULL, `latitude` REAL NOT NULL DEFAULT 0.0, " +
            "`longitude` REAL NOT NULL DEFAULT 0.0, `address` TEXT NOT NULL DEFAULT '', " +
            "`geofenceRadiusMeters` REAL NOT NULL DEFAULT 200.0, `averageRating` REAL NOT NULL DEFAULT 0.0, " +
            "`openingHoursJson` TEXT NOT NULL DEFAULT '', `isFavorite` INTEGER NOT NULL DEFAULT 0, " +
            "`category` TEXT NOT NULL DEFAULT '', `rating` REAL NOT NULL DEFAULT 0.0, " +
            "`imageUrl` TEXT NOT NULL DEFAULT '', `region` TEXT NOT NULL DEFAULT '', " +
            "`currentCapacityPercent` INTEGER NOT NULL DEFAULT 0, `malePercentage` INTEGER NOT NULL DEFAULT 0, " +
            "`femalePercentage` INTEGER NOT NULL DEFAULT 0, `totalLiveVisitors` INTEGER NOT NULL DEFAULT 0, " +
            "`externalSearchTags` TEXT NOT NULL DEFAULT '', `websiteUrl` TEXT DEFAULT NULL, PRIMARY KEY(`id`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `events` (" +
            "`id` TEXT NOT NULL, `clubId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, " +
            "`startTime` INTEGER NOT NULL DEFAULT 0, `endTime` INTEGER NOT NULL DEFAULT 0, " +
            "`price` TEXT NOT NULL DEFAULT '', `time` TEXT NOT NULL DEFAULT '', `specialOffersJson` TEXT NOT NULL DEFAULT '', " +
            "`searchKeywords` TEXT NOT NULL DEFAULT '', `imageUrl` TEXT DEFAULT NULL, PRIMARY KEY(`id`), " +
            "FOREIGN KEY(`clubId`) REFERENCES `clubs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `reviews` (" +
            "`id` TEXT NOT NULL, `reviewerUserId` TEXT NOT NULL, `targetUserId` TEXT DEFAULT NULL, " +
            "`clubId` TEXT DEFAULT NULL, `eventId` TEXT DEFAULT NULL, `rating` INTEGER NOT NULL, " +
            "`text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `verificationMethod` TEXT NOT NULL, " +
            "`isVerified` INTEGER NOT NULL, `reviewerUsername` TEXT NOT NULL DEFAULT '', " +
            "`reviewerAvatarUrl` TEXT DEFAULT NULL, PRIMARY KEY(`id`), " +
            "FOREIGN KEY(`clubId`) REFERENCES `clubs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
            "FOREIGN KEY(`reviewerUserId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
            "FOREIGN KEY(`eventId`) REFERENCES `events`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `chats` (" +
            "`id` TEXT NOT NULL, `name` TEXT NOT NULL, `cityRegion` TEXT DEFAULT NULL, `lastMessageText` TEXT NOT NULL, " +
            "`lastMessageTimestampMs` INTEGER NOT NULL, `lastMessageTimestampIso` TEXT NOT NULL DEFAULT '', " +
            "`avatarInitial` TEXT NOT NULL, `avatarUrl` TEXT DEFAULT NULL, `unreadCount` INTEGER NOT NULL DEFAULT 0, " +
            "`chatType` TEXT NOT NULL, `isOnline` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `messages` (" +
            "`id` TEXT NOT NULL, `chatId` TEXT NOT NULL, `senderUserId` TEXT NOT NULL DEFAULT '', " +
            "`senderName` TEXT NOT NULL, `text` TEXT NOT NULL, `timestampMs` INTEGER NOT NULL, " +
            "`timestampIso` TEXT NOT NULL DEFAULT '', `mediaUrl` TEXT DEFAULT NULL, `status` TEXT NOT NULL DEFAULT 'SENT', " +
            "`isMine` INTEGER NOT NULL, PRIMARY KEY(`id`), " +
            "FOREIGN KEY(`chatId`) REFERENCES `chats`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
        )
    }
}
