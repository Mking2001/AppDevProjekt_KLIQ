package com.kliq.app.domain.usecase

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.repository.SocialRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.service.VerificationService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class VerifyQRCodeUseCaseTest {

    private lateinit var socialRepository: SocialRepository
    private lateinit var userRepository: UserRepository
    private lateinit var verificationService: VerificationService
    private lateinit var verifyQRCodeUseCase: VerifyQRCodeUseCase

    @Before
    fun setUp() {
        socialRepository = mock(SocialRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        verificationService = mock(VerificationService::class.java)

        verifyQRCodeUseCase = VerifyQRCodeUseCase(
            socialRepository = socialRepository,
            userRepository = userRepository,
            verificationService = verificationService
        )
    }

    @Test
    fun parsePayloadToUserId_validKliqUri_returnsExtractedUserId() {
        val payload = "kliq://user/verify/user_12345?tag=kliq_profile_v1&ts=1700000000"
        val extracted = verifyQRCodeUseCase.parsePayloadToUserId(payload)
        assertEquals("user_12345", extracted)
    }

    @Test
    fun parsePayloadToUserId_invalidPayload_returnsNull() {
        val payload = "   "
        val extracted = verifyQRCodeUseCase.parsePayloadToUserId(payload)
        assertEquals(null, extracted)
    }

    @Test
    fun invoke_selfScan_returnsSelfScanResult() = runTest {
        val payload = "kliq://user/verify/user_me?tag=kliq_profile_v1&ts=1700000000"
        val result = verifyQRCodeUseCase("user_me", payload)

        assertTrue(result is QRScanResult.SelfScan)
    }

    @Test
    fun invoke_validUserScan_returnsSuccess() = runTest {
        val payload = "kliq://user/verify/user_friend_777?tag=kliq_profile_v1&ts=1700000000"

        `when`(userRepository.getUserById("user_friend_777")).thenReturn(
            flowOf(UserEntity(id = "user_friend_777", username = "Alex", email = "alex@test.de"))
        )
        `when`(socialRepository.isFriendOneShot("user_me", "user_friend_777")).thenReturn(false)
        `when`(socialRepository.verifyAndAddFriend("user_me", "user_friend_777")).thenReturn(Result.success(Unit))

        val result = verifyQRCodeUseCase("user_me", payload)

        assertTrue(result is QRScanResult.Success)
        val success = result as QRScanResult.Success
        assertEquals("user_friend_777", success.targetUserId)
        assertEquals("Alex", success.username)
    }

    @Test
    fun invoke_alreadyFriendsScan_returnsAlreadyFriendsResult() = runTest {
        val payload = "kliq://user/verify/user_friend_777?tag=kliq_profile_v1&ts=1700000000"

        `when`(userRepository.getUserById("user_friend_777")).thenReturn(
            flowOf(UserEntity(id = "user_friend_777", username = "Alex", email = "alex@test.de"))
        )
        `when`(socialRepository.isFriendOneShot("user_me", "user_friend_777")).thenReturn(true)

        val result = verifyQRCodeUseCase("user_me", payload)

        assertTrue(result is QRScanResult.AlreadyFriends)
    }
}
