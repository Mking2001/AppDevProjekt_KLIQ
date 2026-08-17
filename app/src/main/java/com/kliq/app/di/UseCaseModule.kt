package com.kliq.app.di

import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.domain.usecase.CalculateUserDistanceUseCase
import com.kliq.app.domain.usecase.GetClubsWithDistanceUseCase
import com.kliq.app.domain.usecase.VerifyQRCodeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Dependency Injection module providing Domain UseCases for clean architecture.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideCalculateUserDistanceUseCase(): CalculateUserDistanceUseCase {
        return CalculateUserDistanceUseCase()
    }

    @Provides
    @Singleton
    fun provideVerifyQRCodeUseCase(): VerifyQRCodeUseCase {
        return VerifyQRCodeUseCase()
    }

    @Provides
    @Singleton
    fun provideGetClubsWithDistanceUseCase(
        clubRepository: ClubRepository
    ): GetClubsWithDistanceUseCase {
        return GetClubsWithDistanceUseCase(clubRepository)
    }
}
