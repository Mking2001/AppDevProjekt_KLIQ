package com.kliq.app.data.local

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrationManager {

    private const val TAG = "DatabaseMigration"
    const val DATABASE_NAME = "kliq_db"

    val MIGRATION_20_21 = object : Migration(20, 21) {
        override fun migrate(db: SupportSQLiteDatabase) {
            try {
                db.execSQL("ALTER TABLE users ADD COLUMN password TEXT DEFAULT NULL")
            } catch (e: Exception) {
                Log.w(TAG, "MIGRATION_20_21: column may already exist: ${e.message}")
            }
        }
    }

    fun buildDatabase(context: Context): KliqDatabase {
        return Room.databaseBuilder(
            context,
            KliqDatabase::class.java,
            DATABASE_NAME
        )
        .addMigrations(MIGRATION_20_21)
        .fallbackToDestructiveMigration()
        .fallbackToDestructiveMigrationOnDowngrade()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                validateDatabaseSchema(db)
            }
        })
        .build()
    }

    fun validateDatabaseSchema(db: SupportSQLiteDatabase) {
        try {
            val cursor = db.query("PRAGMA quick_check")
            if (cursor.moveToFirst()) {
                val result = cursor.getString(0)
                if (result.lowercase() != "ok") {
                    Log.w(TAG, "Database PRAGMA quick_check result: $result")
                } else {
                    Log.d(TAG, "Database schema integrity check passed successfully.")
                }
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e(TAG, "Database schema validation error", e)
        }
    }
}
