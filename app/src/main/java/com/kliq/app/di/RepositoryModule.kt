package com.kliq.app.di

import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.ClubRepositoryImpl
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.data.repository.EventRepositoryImpl
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
}
