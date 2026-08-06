package com.kliq.app.di

import com.kliq.app.data.datasource.GroupPresenceDataSource
import com.kliq.app.data.datasource.GroupPresenceDataSourceImpl
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.data.repository.ClubAndEventRepository
import com.kliq.app.data.repository.ClubAndEventRepositoryImpl
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.ClubRepositoryImpl
import com.kliq.app.data.repository.ClubEventOfferRepository
import com.kliq.app.data.repository.ClubEventOfferRepositoryImpl
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.data.repository.EventRepositoryImpl
import com.kliq.app.data.repository.GroupPresenceRepository
import com.kliq.app.data.repository.GroupPresenceRepositoryImpl
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.data.repository.ReviewRepositoryImpl
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.repository.UserRepositoryImpl
import com.kliq.app.service.QrCodeService
import com.kliq.app.service.QrCodeServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindClubAndEventRepository(impl: ClubAndEventRepositoryImpl): ClubAndEventRepository

    @Binds
    @Singleton
    abstract fun bindClubRepository(impl: ClubRepositoryImpl): ClubRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindClubEventOfferRepository(impl: ClubEventOfferRepositoryImpl): ClubEventOfferRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: com.kliq.app.data.repository.SessionRepositoryImpl): com.kliq.app.data.repository.SessionRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: com.kliq.app.data.repository.LocationRepositoryImpl): com.kliq.app.data.repository.LocationRepository

    @Binds
    @Singleton
    abstract fun bindGeofenceRepository(impl: com.kliq.app.data.repository.GeofenceRepositoryImpl): com.kliq.app.data.repository.GeofenceRepository

    @Binds
    @Singleton
    abstract fun bindRatingRepository(impl: com.kliq.app.data.repository.RatingRepositoryImpl): com.kliq.app.data.repository.RatingRepository

    @Binds
    @Singleton
    abstract fun bindQrCodeService(impl: QrCodeServiceImpl): QrCodeService

    @Binds
    @Singleton
    abstract fun bindSocialRepository(impl: com.kliq.app.data.repository.SocialRepositoryImpl): com.kliq.app.data.repository.SocialRepository

    @Binds
    @Singleton
    abstract fun bindVisitedLogRepository(impl: com.kliq.app.data.repository.VisitedLogRepositoryImpl): com.kliq.app.data.repository.VisitedLogRepository

    @Binds
    @Singleton
    abstract fun bindPushNotificationRepository(impl: com.kliq.app.data.repository.PushNotificationRepositoryImpl): com.kliq.app.data.repository.PushNotificationRepository

    @Binds
    @Singleton
    abstract fun bindGroupPresenceDataSource(impl: GroupPresenceDataSourceImpl): GroupPresenceDataSource

    @Binds
    @Singleton
    abstract fun bindGroupPresenceRepository(impl: GroupPresenceRepositoryImpl): GroupPresenceRepository

    @Binds
    @Singleton
    abstract fun bindAccessibilityRepository(impl: com.kliq.app.data.repository.AccessibilityRepositoryImpl): com.kliq.app.data.repository.AccessibilityRepository
}

