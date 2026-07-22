package com.kliq.app.di

import android.content.Context
import com.kliq.app.data.local.DatabaseMigrationManager
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.MockSmsVerificationService
import com.kliq.app.data.remote.SmsVerificationService
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
        return DatabaseMigrationManager.buildDatabase(context)
    }

    @Provides
    fun provideUserDao(database: KliqDatabase): UserDao = database.userDao()

    @Provides
    fun provideClubDao(database: KliqDatabase): ClubDao = database.clubDao()

    @Provides
    fun provideEventDao(database: KliqDatabase): EventDao = database.eventDao()

    @Provides
    fun provideReviewDao(database: KliqDatabase): ReviewDao = database.reviewDao()

    @Provides
    fun provideChatDao(database: KliqDatabase): ChatDao = database.chatDao()

    @Provides
    @Singleton
    fun provideApiService(): KliqApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.kliq-nightlife.com/") // Placeholder URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KliqApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSmsVerificationService(): SmsVerificationService {
        return MockSmsVerificationService()
    }
}
