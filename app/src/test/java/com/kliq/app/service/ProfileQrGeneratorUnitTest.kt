package com.kliq.app.service

import com.kliq.app.testing.createTestProfileViewModel
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.screens.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ProfileQrGeneratorUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var qrCodeService: QrCodeServiceImpl
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        qrCodeService = QrCodeServiceImpl(ioDispatcher = testDispatcher)
        userRepository = mock(UserRepository::class.java)

        `when`(userRepository.getUserById("current_user")).thenReturn(flowOf(null))
        `when`(userRepository.getUserReputationSummary("current_user")).thenReturn(
            flowOf(
                com.kliq.app.data.model.UserReputationSummary(
                    targetUserId = "current_user",
                    averageRating = 4.8,
                    totalReviewsCount = 10,
                    verifiedReviewsCount = 10
                )
            )
        )

        viewModel = createTestProfileViewModel(userRepository, qrCodeService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun generateProfileQrPayload_createsValidKliqProtocolUri() {
        val userId = "user_test_999"
        val payload = qrCodeService.generateProfileQrPayload(userId)

        assertTrue(payload.startsWith("kliq://user/verify/$userId"))
        assertTrue(payload.contains("tag=kliq_profile_v1"))
        assertTrue(payload.contains("ts="))
    }

    @Test
    fun generateQrCodeBitmap_offMainThread_createsNonNullBitmapMatrix() = runTest {
        val userId = "user_test_888"
        val result = qrCodeService.generateQrCodeBitmap(userId, 256, 256)

        assertTrue(result.isSuccess)
        val bitmap = result.getOrNull()
        assertNotNull(bitmap)
        assertEquals(256, bitmap!!.width)
        assertEquals(256, bitmap.height)
    }

    @Test
    fun showQrCodeModal_updatesViewModelStateFlowWithGeneratedBitmap() = runTest {
        viewModel.showQrCodeModal()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isQrModalVisible)
        assertFalse(state.isGeneratingQrCode)
        assertNotNull(state.qrCodeBitmap)
        assertNotNull(state.qrPayloadText)
        assertTrue(state.qrPayloadText!!.startsWith("kliq://user/verify/current_user"))
    }

    @Test
    fun dismissQrCodeModal_resetsModalVisibilityState() {
        viewModel.showQrCodeModal()
        viewModel.dismissQrCodeModal()

        assertFalse(viewModel.uiState.value.isQrModalVisible)
    }
}
