package com.kliq.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class KliqDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
