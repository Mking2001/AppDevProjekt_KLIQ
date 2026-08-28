package com.kliq.app.service

import com.kliq.app.testing.createTestProfileViewModel
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
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
import org.junit.Assert.assertNull
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
class ProfileQrCodeScenarioTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var qrCodeService: QrCodeServiceImpl
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        qrCodeService = QrCodeServiceImpl(ioDispatcher = testDispatcher)
        userRepository = mock(UserRepository::class.java)

        `when`(userRepository.getUserById("user_kliq_1001")).thenReturn(flowOf(null))
        `when`(userRepository.getUserReputationSummary("user_kliq_1001")).thenReturn(
            flowOf(
                com.kliq.app.data.model.UserReputationSummary(
                    targetUserId = "user_kliq_1001",
                    averageRating = 4.9,
                    totalReviewsCount = 15,
                    verifiedReviewsCount = 15
                )
            )
        )

        viewModel = createTestProfileViewModel(userRepository, qrCodeService)
        viewModel.loadProfileData("user_kliq_1001")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun szenario1_initialLoading_generatesBitmapAsyncAndUpdatesState() = runTest {
        assertFalse(viewModel.uiState.value.isQrModalVisible)
        assertNull(viewModel.uiState.value.qrCodeBitmap)

        viewModel.showQrCodeModal()
        assertTrue(viewModel.uiState.value.isQrModalVisible)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isQrModalVisible)
        assertFalse(state.isGeneratingQrCode)
        assertNotNull(state.qrCodeBitmap)
        assertEquals(512, state.qrCodeBitmap!!.width)
        assertEquals(512, state.qrCodeBitmap!!.height)
    }

    @Test
    fun szenario2_payloadIntegrity_decodesMatrixAndVerifiesUserProtocol() = runTest {
        val targetUserId = "user_kliq_1001"
        val result = qrCodeService.generateQrCodeBitmap(targetUserId, 512, 512)

        assertTrue(result.isSuccess)
        val bitmap = result.getOrNull()
        assertNotNull(bitmap)

        val width = bitmap!!.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        val decodedResult = reader.decode(binaryBitmap)

        assertNotNull(decodedResult)
        val decodedText = decodedResult.text

        assertTrue(decodedText.startsWith("kliq://user/verify/$targetUserId"))
        assertTrue(decodedText.contains("tag=kliq_profile_v1"))
        assertTrue(decodedText.contains("ts="))
    }

    @Test
    fun szenario3_lifecycleHandling_dismissResetsModalVisibilityState() = runTest {
        viewModel.showQrCodeModal()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isQrModalVisible)

        viewModel.dismissQrCodeModal()
        assertFalse(viewModel.uiState.value.isQrModalVisible)
    }
}
