package com.kliq.app.data.local

import android.content.Context
import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KliqDatabaseMigrationAndroidTest {

    private const val TEST_TAG = "MigrationAndroidTest"
    private const val EMULATOR_TEST_DB = "kliq_emulator_migration_test.db"
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(EMULATOR_TEST_DB)
        Log.i(TEST_TAG, "Database reset completed for emulator test run.")
    }

    @After
    fun tearDown() {
        context.deleteDatabase(EMULATOR_TEST_DB)
        Log.i(TEST_TAG, "Database cleanup completed.")
    }

    @Test
    fun verifyMigrationFromVersion1ToLatestVersion7_onEmulator() {
        Log.i(TEST_TAG, "Step 1: Creating Schema Version 1 database instance...")

        // Step 1: Create Version 1 SQLite Database
        val configV1 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(EMULATOR_TEST_DB)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `users` (" +
                        "`id` TEXT NOT NULL, `username` TEXT NOT NULL, `email` TEXT NOT NULL, " +
                        "`profilePictureUrl` TEXT, `bio` TEXT, PRIMARY KEY(`id`))"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `clubs` (" +
                        "`id` TEXT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, " +
                        "`rating` REAL NOT NULL, `imageUrl` TEXT NOT NULL, `region` TEXT NOT NULL, " +
                        "`isFavorite` INTEGER NOT NULL, `currentCapacityPercent` INTEGER NOT NULL, " +
                        "`malePercentage` INTEGER NOT NULL, `femalePercentage` INTEGER NOT NULL, " +
                        "`totalLiveVisitors` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `chats` (" +
                        "`id` TEXT NOT NULL, `name` TEXT NOT NULL, `lastMessageText` TEXT NOT NULL, " +
                        "`lastMessageTimestamp` INTEGER NOT NULL, `avatarInitial` TEXT NOT NULL, " +
                        "`unreadCount` INTEGER NOT NULL, `chatType` TEXT NOT NULL, `isOnline` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()

        val helperV1 = FrameworkSQLiteOpenHelperFactory().create(configV1)
        val dbV1 = helperV1.writableDatabase

        Log.i(TEST_TAG, "Inserting Schema V1 sample records...")

        // Insert V1 User
        dbV1.execSQL(
            "INSERT INTO `users` (`id`, `username`, `email`, `profilePictureUrl`, `bio`) " +
            "VALUES ('user_v1_001', 'night_owl', 'owl@kliq.app', 'https://kliq.app/avatars/owl.jpg', 'Party lover')"
        )

        // Insert V1 Club
        dbV1.execSQL(
            "INSERT INTO `clubs` (`id`, `name`, `category`, `rating`, `imageUrl`, `region`, " +
            "`isFavorite`, `currentCapacityPercent`, `malePercentage`, `femalePercentage`, `totalLiveVisitors`) " +
            "VALUES ('club_v1_101', 'Pacha Club', 'EDM', 4.5, 'https://kliq.app/pacha.jpg', 'Munich', 1, 80, 50, 50, 400)"
        )

        // Insert V1 Chat
        dbV1.execSQL(
            "INSERT INTO `chats` (`id`, `name`, `lastMessageText`, `lastMessageTimestamp`, `avatarInitial`, " +
            "`unreadCount`, `chatType`, `isOnline`) " +
            "VALUES ('chat_v1_201', 'VIP Crew', 'See you at 11', 1699990000000, 'V', 3, 'PRIVATE', 1)"
        )

        helperV1.close()
        Log.i(TEST_TAG, "Schema V1 initialization complete.")

        // Step 2: Run sequential migrations V1 -> V7
        Log.i(TEST_TAG, "Step 2: Executing migration chain from V1 to V7...")

        val configV7 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(EMULATOR_TEST_DB)
            .callback(object : SupportSQLiteOpenHelper.Callback(7) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    var currentVer = oldVersion
                    Log.i(TEST_TAG, "Migrating database from version $oldVersion to $newVersion")

                    if (currentVer == 1 && newVersion >= 2) {
                        DatabaseMigrations.MIGRATION_1_2.migrate(db)
                        currentVer = 2
                    }
                    if (currentVer == 2 && newVersion >= 3) {
                        DatabaseMigrations.MIGRATION_2_3.migrate(db)
                        currentVer = 3
                    }
                    if (currentVer == 3 && newVersion >= 4) {
                        DatabaseMigrations.MIGRATION_3_4.migrate(db)
                        currentVer = 4
                    }
                    if (currentVer == 4 && newVersion >= 5) {
                        DatabaseMigrations.MIGRATION_4_5.migrate(db)
                        currentVer = 5
                    }
                    if (currentVer == 5 && newVersion >= 6) {
                        DatabaseMigrations.MIGRATION_5_6.migrate(db)
                        currentVer = 6
                    }
                    if (currentVer == 6 && newVersion >= 7) {
                        DatabaseMigrations.MIGRATION_6_7.migrate(db)
                        currentVer = 7
                    }

                    assertEquals(7, currentVer)
                }
            })
            .build()

        val helperV7 = FrameworkSQLiteOpenHelperFactory().create(configV7)
        val dbV7 = helperV7.writableDatabase

        Log.i(TEST_TAG, "Step 3: Verifying data preservation and schema integrity post-migration...")

        // Validate Users Data Preservation
        val userCursor = dbV7.query("SELECT * FROM `users` WHERE `id` = 'user_v1_001'")
        assertTrue("Migrated user record must exist", userCursor.moveToFirst())
        assertEquals("night_owl", userCursor.getString(userCursor.getColumnIndexOrThrow("username")))
        assertEquals("owl@kliq.app", userCursor.getString(userCursor.getColumnIndexOrThrow("email")))
        assertEquals("Party lover", userCursor.getString(userCursor.getColumnIndexOrThrow("bio")))
        assertTrue(userCursor.isNull(userCursor.getColumnIndexOrThrow("phoneNumber")))
        assertEquals(0, userCursor.getInt(userCursor.getColumnIndexOrThrow("isVerified")))
        assertEquals(0L, userCursor.getLong(userCursor.getColumnIndexOrThrow("updatedAtTimestampMs")))
        userCursor.close()

        // Validate Clubs Data Preservation & New Columns
        val clubCursor = dbV7.query("SELECT * FROM `clubs` WHERE `id` = 'club_v1_101'")
        assertTrue("Migrated club record must exist", clubCursor.moveToFirst())
        assertEquals("Pacha Club", clubCursor.getString(clubCursor.getColumnIndexOrThrow("name")))
        assertEquals("EDM", clubCursor.getString(clubCursor.getColumnIndexOrThrow("category")))
        assertEquals(1, clubCursor.getInt(clubCursor.getColumnIndexOrThrow("isFavorite")))
        assertEquals(0.0, clubCursor.getDouble(clubCursor.getColumnIndexOrThrow("latitude")), 0.001)
        assertEquals(0.0, clubCursor.getDouble(clubCursor.getColumnIndexOrThrow("longitude")), 0.001)
        assertEquals("", clubCursor.getString(clubCursor.getColumnIndexOrThrow("city")))
        assertEquals(0, clubCursor.getInt(clubCursor.getColumnIndexOrThrow("isPromoted")))
        clubCursor.close()

        // Validate Chats Data Preservation & New Columns
        val chatCursor = dbV7.query("SELECT * FROM `chats` WHERE `id` = 'chat_v1_201'")
        assertTrue("Migrated chat record must exist", chatCursor.moveToFirst())
        assertEquals("VIP Crew", chatCursor.getString(chatCursor.getColumnIndexOrThrow("name")))
        assertEquals("See you at 11", chatCursor.getString(chatCursor.getColumnIndexOrThrow("lastMessageText")))
        assertEquals(1699990000000L, chatCursor.getLong(chatCursor.getColumnIndexOrThrow("lastMessageTimestampMs")))
        assertEquals(0, chatCursor.getInt(chatCursor.getColumnIndexOrThrow("isPinned")))
        assertEquals(0, chatCursor.getInt(chatCursor.getColumnIndexOrThrow("isMuted")))
        assertTrue(chatCursor.isNull(chatCursor.getColumnIndexOrThrow("lastReadMessageId")))
        chatCursor.close()

        // Validate New Tables Existence
        val prefCursor = dbV7.query("SELECT COUNT(*) FROM `user_preferences`")
        assertNotNull(prefCursor)
        prefCursor.close()

        val reviewCursor = dbV7.query("SELECT COUNT(*) FROM `reviews`")
        assertNotNull(reviewCursor)
        reviewCursor.close()

        val eventCursor = dbV7.query("SELECT COUNT(*) FROM `events`")
        assertNotNull(eventCursor)
        eventCursor.close()

        val messageCursor = dbV7.query("SELECT COUNT(*) FROM `messages`")
        assertNotNull(messageCursor)
        messageCursor.close()

        // Validate PRAGMA Quick Check Integrity
        val checkCursor = dbV7.query("PRAGMA quick_check")
        assertTrue(checkCursor.moveToFirst())
        val quickCheckResult = checkCursor.getString(0)
        assertEquals("ok", quickCheckResult.lowercase())
        checkCursor.close()

        helperV7.close()
        Log.i(TEST_TAG, "MIGRATION TEST PASSED SUCCESSFULLY: All data intact and Schema V7 verified.")
    }
}
