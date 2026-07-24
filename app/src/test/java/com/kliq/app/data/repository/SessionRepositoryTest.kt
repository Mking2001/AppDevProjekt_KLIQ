package com.kliq.app.data.repository

import com.kliq.app.data.local.security.SessionStorage
import com.kliq.app.data.model.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeStorage: FakeSessionStorage
    private lateinit var repository: SessionRepositoryImpl

    @Before
    fun setUp() {
        fakeStorage = FakeSessionStorage()
        repository = SessionRepositoryImpl(
            sessionStorage = fakeStorage,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun testCheckAndValidateSessionWithActiveSession() = runTest(testDispatcher) {
        fakeStorage.saveSession("test_token_123", "usr_001")

        val isValid = repository.checkAndValidateSession()

        assertTrue(isValid)
        val state = repository.sessionState.value
        assertTrue(state is SessionState.Authenticated)
        assertEquals("usr_001", (state as SessionState.Authenticated).userId)
        assertEquals("test_token_123", state.token)
    }

    @Test
    fun testCheckAndValidateSessionWithInactiveSession() = runTest(testDispatcher) {
        fakeStorage.clearSession()

        val isValid = repository.checkAndValidateSession()

        assertFalse(isValid)
        val state = repository.sessionState.value
        assertTrue(state is SessionState.Unauthenticated)
    }

    @Test
    fun testSaveSessionPersistsCredentials() = runTest(testDispatcher) {
        repository.saveSession("new_token", "usr_999")

        assertTrue(fakeStorage.isSessionActive())
        assertEquals("new_token", fakeStorage.getAuthToken())
        assertEquals("usr_999", fakeStorage.getUserId())

        val state = repository.sessionState.value
        assertTrue(state is SessionState.Authenticated)
    }

    @Test
    fun testClearSessionWipesCredentials() = runTest(testDispatcher) {
        repository.saveSession("new_token", "usr_999")
        repository.clearSession()

        assertFalse(fakeStorage.isSessionActive())
        assertEquals(null, fakeStorage.getAuthToken())
        assertEquals(null, fakeStorage.getUserId())

        val state = repository.sessionState.value
        assertTrue(state is SessionState.Unauthenticated)
    }

    private class FakeSessionStorage : SessionStorage {
        private var token: String? = null
        private var userId: String? = null
        private var active: Boolean = false

        override fun saveSession(token: String, userId: String) {
            this.token = token
            this.userId = userId
            this.active = true
        }

        override fun getAuthToken(): String? = token

        override fun getUserId(): String? = userId

        override fun isSessionActive(): Boolean = active

        override fun clearSession() {
            token = null
            userId = null
            active = false
        }
    }
}
