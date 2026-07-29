package com.kliq.app.viewmodel

import com.kliq.app.data.model.SessionState
import com.kliq.app.data.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeSessionRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeSessionRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCheckAutoLoginWithValidSessionNavigatesToAuthenticatedState() = runTest {
        fakeRepository.setSessionValid(true, token = "valid_tok", userId = "usr_123")
        viewModel = AuthViewModel(fakeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Authenticated)
        assertEquals("usr_123", (state as AuthUiState.Authenticated).userId)
    }

    @Test
    fun testCheckAutoLoginWithNoSessionNavigatesToUnauthenticatedState() = runTest {
        fakeRepository.setSessionValid(false)
        viewModel = AuthViewModel(fakeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Unauthenticated)
    }

    @Test
    fun testLoginUpdatesSessionAndStateToAuthenticated() = runTest {
        fakeRepository.setSessionValid(false)
        viewModel = AuthViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.login("new_token", "usr_777")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Authenticated)
        assertEquals("usr_777", (state as AuthUiState.Authenticated).userId)
    }

    @Test
    fun testLogoutClearsSessionAndStateToUnauthenticated() = runTest {
        fakeRepository.setSessionValid(true, token = "valid_tok", userId = "usr_123")
        viewModel = AuthViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Unauthenticated)
    }

    private class FakeSessionRepository : SessionRepository {
        private var isValid: Boolean = false
        private var token: String? = null
        private var userId: String? = null
        private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
        override val sessionState: StateFlow<SessionState> = _sessionState

        fun setSessionValid(valid: Boolean, token: String? = null, userId: String? = null) {
            this.isValid = valid
            this.token = token
            this.userId = userId
            _sessionState.value = if (valid && token != null && userId != null) {
                SessionState.Authenticated(token, userId)
            } else {
                SessionState.Unauthenticated
            }
        }

        override suspend fun checkAndValidateSession(): Boolean {
            return isValid
        }

        override suspend fun saveSession(token: String, userId: String) {
            setSessionValid(true, token, userId)
        }

        override suspend fun clearSession() {
            setSessionValid(false)
        }

        override fun getAuthToken(): String? = token

        override fun getUserId(): String? = userId

        override fun isSessionActive(): Boolean = isValid
    }
}
