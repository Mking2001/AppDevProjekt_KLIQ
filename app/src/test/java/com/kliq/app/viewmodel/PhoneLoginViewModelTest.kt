package com.kliq.app.viewmodel

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.screens.auth.PhoneLoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
    fun testInitialState() {
        val state = viewModel.uiState.value
        assertEquals("", state.identifier)
        assertEquals("", state.password)
        assertFalse(state.isFormValid)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
    }

    @Test
    fun testValidationWithEmptyAndInvalidInputs() {
        viewModel.onIdentifierChanged("")
        assertTrue(viewModel.uiState.value.identifierError != null)
        assertFalse(viewModel.uiState.value.isFormValid)

        viewModel.onPasswordChanged("123")
        assertTrue(viewModel.uiState.value.passwordError != null)
        assertFalse(viewModel.uiState.value.isFormValid)

        viewModel.onIdentifierChanged("alex_mustermann")
        viewModel.onPasswordChanged("geheimesPasswort123")
        assertNull(viewModel.uiState.value.identifierError)
        assertNull(viewModel.uiState.value.passwordError)
        assertTrue(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun testSuccessfulLogin() = runTest(testDispatcher) {
        val dummyUser = UserEntity(
            id = "usr_123",
            username = "alex",
            email = "alex@kliq.app"
        )
        `when`(userRepository.loginUser("alex", "password123")).thenReturn(Result.success(dummyUser))

        viewModel.onIdentifierChanged("alex")
        viewModel.onPasswordChanged("password123")
        viewModel.onLogin()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertNull(state.errorMessage)
    }

    @Test
    fun testFailedLoginShowsErrorMessage() = runTest(testDispatcher) {
        `when`(userRepository.loginUser("unknown_user", "wrong_pass")).thenReturn(
            Result.failure(IllegalArgumentException("Benutzer nicht gefunden"))
        )

        viewModel.onIdentifierChanged("unknown_user")
        viewModel.onPasswordChanged("wrong_pass")
        viewModel.onLogin()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertEquals("Benutzer nicht gefunden", state.errorMessage)
    }
}
