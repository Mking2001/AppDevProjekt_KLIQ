package com.kliq.app.ui.screens.qr

import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.domain.usecase.QRScanResult
import com.kliq.app.domain.usecase.VerifyQRCodeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class QRScannerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var verifyQRCodeUseCase: VerifyQRCodeUseCase
    private lateinit var sessionRepository: SessionRepository
    private lateinit var viewModel: QRScannerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        verifyQRCodeUseCase = mock(VerifyQRCodeUseCase::class.java)
        sessionRepository = mock(SessionRepository::class.java)

        `when`(sessionRepository.getUserId()).thenReturn("user_me")

        viewModel = QRScannerViewModel(
            verifyQRCodeUseCase = verifyQRCodeUseCase,
            sessionRepository = sessionRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCameraPermissionGranted_updatesState() {
        viewModel.onCameraPermissionGranted()
        assertTrue(viewModel.uiState.value.hasCameraPermission)
    }

    @Test
    fun onCameraPermissionDenied_updatesStateWithError() {
        viewModel.onCameraPermissionDenied()
        assertFalse(viewModel.uiState.value.hasCameraPermission)
        assertTrue(viewModel.uiState.value.errorMessage != null)
    }

    @Test
    fun toggleFlash_togglesState() {
        assertFalse(viewModel.uiState.value.isFlashEnabled)
        viewModel.toggleFlash()
        assertTrue(viewModel.uiState.value.isFlashEnabled)
    }

    @Test
    fun onQRCodeScanned_successfulScan_updatesStateWithSuccess() = runTest {
        val payload = "kliq://user/verify/user_friend_123?tag=kliq_profile_v1&ts=1000"
        val expectedResult = QRScanResult.Success(
            targetUserId = "user_friend_123",
            username = "Alex",
            message = "Verifizierung erfolgreich!"
        )

        `when`(verifyQRCodeUseCase("user_me", payload)).thenReturn(expectedResult)

        viewModel.onQRCodeScanned(payload)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(expectedResult, viewModel.uiState.value.scanResult)
        assertEquals("Verifizierung erfolgreich!", viewModel.uiState.value.successMessage)
        assertFalse(viewModel.uiState.value.isScanning)
    }

    @Test
    fun resumeScanning_resetsScanState() {
        viewModel.resumeScanning()
        assertTrue(viewModel.uiState.value.isScanning)
        assertEquals(null, viewModel.uiState.value.scanResult)
    }
}
