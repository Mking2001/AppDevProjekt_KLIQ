package com.kliq.app.ui.screens.onboarding

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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

@OptIn(ExperimentalCoroutinesApi::class)
class IntentMatchingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: IntentMatchingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        viewModel = IntentMatchingViewModel(fakeUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is unselected and selection is invalid`() {
        val state = viewModel.uiState.value
        assertNull(state.selectedIntent)
        assertFalse(state.isSelectionValid)
        assertFalse(state.isLoading)
        assertFalse(state.isSaved)
        assertNull(state.errorMessage)
    }

    @Test
    fun `selecting an intent option validates selection state`() {
        // Select FRIENDS option
        viewModel.selectIntent(SearchIntent.FRIENDS)
        val state1 = viewModel.uiState.value
        assertEquals(SearchIntent.FRIENDS, state1.selectedIntent)
        assertTrue(state1.isSelectionValid)
        assertNull(state1.errorMessage)

        // Select DATING option
        viewModel.selectIntent(SearchIntent.DATING)
        val state2 = viewModel.uiState.value
        assertEquals(SearchIntent.DATING, state2.selectedIntent)
        assertTrue(state2.isSelectionValid)

        // Toggle DATING off by selecting it again
        viewModel.selectIntent(SearchIntent.DATING)
        val state3 = viewModel.uiState.value
        assertNull(state3.selectedIntent)
        assertFalse(state3.isSelectionValid)
    }

    @Test
    fun `attempting to save without selection shows error message`() = runTest {
        viewModel.saveIntent("user_123")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertEquals("Bitte wähle mindestens eine Option aus, um fortzufahren.", state.errorMessage)
        assertFalse(state.isSaved)
        assertNull(fakeUserRepository.savedSearchIntent)
    }

    @Test
    fun `saving valid intent calls repository and updates state to isSaved`() = runTest {
        viewModel.selectIntent(SearchIntent.BOTH)
        assertTrue(viewModel.uiState.value.isSelectionValid)

        viewModel.saveIntent("user_123")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(SearchIntent.BOTH, fakeUserRepository.savedSearchIntent)
        assertEquals("user_123", fakeUserRepository.savedUserId)
    }

    private class FakeUserRepository : UserRepository {
        var savedUserId: String? = null
        var savedSearchIntent: SearchIntent? = null

        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(null)
        override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> = flowOf(null)
        override suspend fun syncUserProfile(userId: String): Result<Unit> = Result.success(Unit)
        override suspend fun saveUser(user: UserEntity) {}
        override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) {}
        
        override suspend fun saveSearchIntent(userId: String, intent: SearchIntent) {
            savedUserId = userId
            savedSearchIntent = intent
        }

        override suspend fun saveProfile(
            userId: String,
            username: String,
            age: Int,
            hometown: String,
            bio: String,
            profilePictureUrl: String?
        ) {}

        override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = Result.success(true)
        override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> {
            return Result.success(UserEntity("1", "test", "test@kliq.de"))
        }
    }
}
