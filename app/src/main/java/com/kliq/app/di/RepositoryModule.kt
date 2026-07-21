package com.kliq.app.di

import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.ClubRepositoryImpl
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.data.repository.EventRepositoryImpl
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.data.repository.ReviewRepositoryImpl
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
}
