package com.kliq.app.di

import android.content.Context
import androidx.room.Room
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.remote.KliqApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KliqDatabase {
        return Room.databaseBuilder(
            context,
            KliqDatabase::class.java,
            "kliq_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: KliqDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideApiService(): KliqApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.kliq-nightlife.com/") // Placeholder URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KliqApiService::class.java)
    }
}
