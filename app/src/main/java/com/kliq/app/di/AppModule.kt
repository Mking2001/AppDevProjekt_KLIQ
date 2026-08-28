package com.kliq.app.di

import android.content.Context
import com.kliq.app.data.local.DatabaseMigrationManager
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.DirectMessageDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.dao.FeedDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.MockSmsVerificationService
import com.kliq.app.data.remote.SmsVerificationService
import com.kliq.app.data.generated.KliqConnectorConnector
import com.kliq.app.data.generated.instance
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
    fun provideFeedDao(database: KliqDatabase): FeedDao = database.feedDao()

    @Provides
    fun provideChatDao(database: KliqDatabase): ChatDao = database.chatDao()

    @Provides
    fun provideDirectMessageDao(database: KliqDatabase): DirectMessageDao = database.directMessageDao()

    @Provides
    fun provideLocationDao(database: KliqDatabase): com.kliq.app.data.local.dao.LocationDao = database.locationDao()

    @Provides
    fun provideSocialDao(database: KliqDatabase): com.kliq.app.data.local.dao.SocialDao = database.socialDao()

    @Provides
    fun provideVisitedLogDao(database: KliqDatabase): com.kliq.app.data.local.dao.VisitedLogDao = database.visitedLogDao()

    @Provides
    fun provideBlockedUserDao(database: KliqDatabase): com.kliq.app.data.local.dao.BlockedUserDao = database.blockedUserDao()

    @Provides
    fun provideClubOfferDao(database: KliqDatabase): com.kliq.app.data.local.dao.ClubOfferDao = database.clubOfferDao()

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

    @Provides
    @Singleton
    fun provideSessionStorage(encryptedSessionStorage: com.kliq.app.data.local.security.EncryptedSessionStorage): com.kliq.app.data.local.security.SessionStorage {
        return encryptedSessionStorage
    }

    @Provides
    fun provideIoDispatcher(): kotlinx.coroutines.CoroutineDispatcher {
        return kotlinx.coroutines.Dispatchers.IO
    }

    @Provides
    @Singleton
    fun providePermissionManager(impl: com.kliq.app.util.PermissionManagerImpl): com.kliq.app.util.PermissionManager {
        return impl
    }

    @Provides
    @Singleton
    fun provideGeofenceManager(impl: com.kliq.app.util.GeofenceManagerImpl): com.kliq.app.util.GeofenceManager {
        return impl
    }

    @Provides
    @Singleton
    fun provideVerificationService(impl: com.kliq.app.service.VerificationServiceImpl): com.kliq.app.service.VerificationService {
        return impl
    }

    @Provides
    @Singleton
    fun provideCalculateUserDistanceUseCase(): com.kliq.app.domain.usecase.CalculateUserDistanceUseCase {
        return com.kliq.app.domain.usecase.CalculateUserDistanceUseCase()
    }

    @Provides
    @Singleton
    fun provideUserDistanceFormatter(): com.kliq.app.util.UserDistanceFormatter {
        return com.kliq.app.util.UserDistanceFormatter()
    }

    @Provides
    @Singleton
    fun provideImageCompressor(): com.kliq.app.util.ImageCompressor {
        return com.kliq.app.util.ImageCompressor()
    }

    @Provides
    @Singleton
    fun provideHapticFeedbackManager(impl: com.kliq.app.util.HapticFeedbackManagerImpl): com.kliq.app.util.HapticFeedbackManager {
        return impl
    }

    // =========================================================================
    // Firebase Data Connect (SQL Connect)
    // =========================================================================
    @Provides
    @Singleton
    fun provideKliqConnector(): KliqConnectorConnector {
        return KliqConnectorConnector.instance
    }
}

