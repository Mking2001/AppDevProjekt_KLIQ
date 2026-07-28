package com.kliq.app.di

import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.data.repository.ClubAndEventRepository
import com.kliq.app.data.repository.ClubAndEventRepositoryImpl
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.ClubRepositoryImpl
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.data.repository.EventRepositoryImpl
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
}
