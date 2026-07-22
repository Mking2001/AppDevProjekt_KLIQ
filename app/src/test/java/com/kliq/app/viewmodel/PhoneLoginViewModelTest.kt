package com.kliq.app.viewmodel

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.screens.auth.CountryCodeOption
import com.kliq.app.ui.screens.auth.PhoneLoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneLoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private lateinit var viewModel: PhoneLoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PhoneLoginViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateHasDefaultCountryCodeAndDisabledSubmit() {
        val state = viewModel.uiState.value
        assertEquals("+49", state.countryCode)
        assertEquals("", state.phoneNumber)
        assertFalse(state.isValidPhoneNumber)
        assertFalse(state.isSubmitPhoneNumberEnabled)
        assertFalse(state.isLoading)
        assertFalse(state.isOtpSent)
    }

    @Test
    fun testPhoneNumberValidationShortNumber() {
        viewModel.onPhoneNumberChanged("12345")
        val state = viewModel.uiState.value

        assertFalse(state.isValidPhoneNumber)
        assertFalse(state.isSubmitPhoneNumberEnabled)
        assertEquals("Telefonnummer zu kurz (mindestens 7 Ziffern).", state.validationErrorMessage)
    }

    @Test
    fun testPhoneNumberValidationValidNumber() {
        viewModel.onPhoneNumberChanged("1512345678")
        val state = viewModel.uiState.value

        assertTrue(state.isValidPhoneNumber)
        assertTrue(state.isSubmitPhoneNumberEnabled)
        assertNull(state.validationErrorMessage)
        assertEquals("+491512345678", state.fullPhoneNumber)
    }

    @Test
    fun testPhoneNumberValidationLongNumber() {
        viewModel.onPhoneNumberChanged("1234567890123456")
        val state = viewModel.uiState.value

        assertFalse(state.isValidPhoneNumber)
        assertFalse(state.isSubmitPhoneNumberEnabled)
        assertEquals("Telefonnummer zu lang (maximal 15 Ziffern).", state.validationErrorMessage)
    }

    @Test
    fun testCountryCodeSelectionUpdatesState() {
        val austriaOption = CountryCodeOption("Österreich", "🇦🇹", "+43", "AT")
        viewModel.onCountrySelected(austriaOption)
        viewModel.onPhoneNumberChanged("6601234567")

        val state = viewModel.uiState.value
        assertEquals("+43", state.countryCode)
        assertEquals(austriaOption, state.selectedCountry)
        assertEquals("+436601234567", state.fullPhoneNumber)
        assertTrue(state.isValidPhoneNumber)
    }

    @Test
    fun testSendOtpSuccessFlow() = runTest {
        val phone = "1512345678"
        `when`(userRepository.requestOtp("+49", phone)).thenReturn(Result.success(true))

        viewModel.onPhoneNumberChanged(phone)
        viewModel.sendOtp()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isOtpSent)
        assertNull(state.errorMessage)
    }

    @Test
    fun testSendOtpFailureFlow() = runTest {
        val phone = "1512345678"
        val failureReason = "Netzwerkfehler beim SMS-Versand"
        `when`(userRepository.requestOtp("+49", phone)).thenReturn(Result.failure(Exception(failureReason)))

        viewModel.onPhoneNumberChanged(phone)
        viewModel.sendOtp()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isOtpSent)
        assertEquals(failureReason, state.errorMessage)
    }

    @Test
    fun testVerifyOtpSuccessFlow() = runTest {
        val phone = "1512345678"
        val otp = "123456"
        val dummyUser = UserEntity(
            id = "usr_999",
            username = "kliq_test",
            email = "test@kliq.app",
            profilePictureUrl = null,
            bio = "Bio",
            phoneNumber = "+491512345678",
            isVerified = true,
            updatedAtTimestampMs = 1000L
        )

        `when`(userRepository.requestOtp("+49", phone)).thenReturn(Result.success(true))
        `when`(userRepository.verifyOtp("+49", phone, otp)).thenReturn(Result.success(dummyUser))

        viewModel.onPhoneNumberChanged(phone)
        viewModel.sendOtp()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onOtpCodeChanged(otp)
        assertTrue(viewModel.uiState.value.isValidOtp)

        viewModel.verifyOtp()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertNull(state.errorMessage)
    }

    @Test
    fun testVerifyOtpFailureFlow() = runTest {
        val phone = "1512345678"
        val otp = "000000"
        val failureMessage = "Der eingegebener Code ist ungültig."

        `when`(userRepository.requestOtp("+49", phone)).thenReturn(Result.success(true))
        `when`(userRepository.verifyOtp("+49", phone, otp)).thenReturn(Result.failure(Exception(failureMessage)))

        viewModel.onPhoneNumberChanged(phone)
        viewModel.sendOtp()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onOtpCodeChanged(otp)
        viewModel.verifyOtp()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertEquals(failureMessage, state.errorMessage)
    }

    @Test
    fun testResetOtpStateResetsToPhoneInput() = runTest {
        val phone = "1512345678"
        `when`(userRepository.requestOtp("+49", phone)).thenReturn(Result.success(true))

        viewModel.onPhoneNumberChanged(phone)
        viewModel.sendOtp()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOtpSent)

        viewModel.resetOtpState()

        val state = viewModel.uiState.value
        assertFalse(state.isOtpSent)
        assertEquals("", state.otpCode)
        assertFalse(state.isValidOtp)
    }
}
