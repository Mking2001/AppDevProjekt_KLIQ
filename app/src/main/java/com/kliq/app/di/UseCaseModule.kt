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

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideVerifyQRCodeUseCase(
        socialRepository: com.kliq.app.data.repository.SocialRepository,
        userRepository: com.kliq.app.data.repository.UserRepository,
        verificationService: com.kliq.app.service.VerificationService
    ): VerifyQRCodeUseCase {
        return VerifyQRCodeUseCase(socialRepository, userRepository, verificationService)
    }

    @Provides
    @Singleton
    fun provideGetClubsWithDistanceUseCase(
        clubRepository: ClubRepository
    ): GetClubsWithDistanceUseCase {
        return GetClubsWithDistanceUseCase(clubRepository)
    }
}
