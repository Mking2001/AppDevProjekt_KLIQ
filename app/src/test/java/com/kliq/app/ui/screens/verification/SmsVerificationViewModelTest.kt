package com.kliq.app.ui.screens.verification

import androidx.lifecycle.SavedStateHandle
import com.kliq.app.data.remote.MockSmsVerificationService
import com.kliq.app.data.remote.SmsVerificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SmsVerificationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var service: SmsVerificationService
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: SmsVerificationViewModel

    private val testPhoneNumber = "+4917612345678"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        service = MockSmsVerificationService()
        savedStateHandle = SavedStateHandle(mapOf(SmsVerificationViewModel.PHONE_NUMBER_KEY to testPhoneNumber))
        viewModel = SmsVerificationViewModel(service, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasCorrectPhoneNumberAndTimerStarted() = runTest {
        assertEquals(testPhoneNumber, viewModel.phoneNumber)
        assertEquals("", viewModel.enteredCode.value)
        
        // Initial code sending starts timer
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.resendTimerState.value.canResend)
        assertEquals(30, viewModel.resendTimerState.value.secondsRemaining)
    }

    @Test
    fun onCodeChanged_filtersNonDigitsAndLimitsToSixChars() = runTest {
        viewModel.onCodeChanged("12a3b45678")
        assertEquals("123456", viewModel.enteredCode.value)
    }

    @Test
    fun onCodeChanged_whenSixDigits_triggersAutoVerification_success() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onCodeChanged("123456")
        
        // State should transition to Loading then Success
        assertEquals(VerificationUiState.Loading, viewModel.verificationState.value)
        
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(VerificationUiState.Success, viewModel.verificationState.value)
    }

    @Test
    fun onCodeChanged_whenSixDigits_triggersAutoVerification_failure() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onCodeChanged("000000")
        
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.verificationState.value is VerificationUiState.Error)
        assertEquals("", viewModel.enteredCode.value)
    }

    @Test
    fun timer_countsDownToZero_andEnablesResendButton() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Advance 30 seconds
        advanceTimeBy(30_000L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.resendTimerState.value.canResend)
        assertEquals(0, viewModel.resendTimerState.value.secondsRemaining)
    }

    @Test
    fun resendCode_resetsStateAndRestartsTimer() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        advanceTimeBy(30_000L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.resendTimerState.value.canResend)

        viewModel.resendCode()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.resendTimerState.value.canResend)
        assertEquals(30, viewModel.resendTimerState.value.secondsRemaining)
    }
}
