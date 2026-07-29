package com.kliq.app.di

import com.kliq.app.data.datasource.GroupPresenceDataSource
import com.kliq.app.data.datasource.GroupPresenceDataSourceImpl
import com.kliq.app.data.repository.GroupPresenceRepository
import com.kliq.app.data.repository.GroupPresenceRepositoryImpl
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
    abstract fun bindGroupPresenceDataSource(impl: GroupPresenceDataSourceImpl): GroupPresenceDataSource

    @Binds
    @Singleton
    abstract fun bindGroupPresenceRepository(impl: GroupPresenceRepositoryImpl): GroupPresenceRepository
}
