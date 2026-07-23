package com.kliq.app.ui.screens.onboarding

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
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
class ConsumptionHabitsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: ConsumptionHabitsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        viewModel = ConsumptionHabitsViewModel(fakeUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is unselected and selection is invalid`() {
        val state = viewModel.uiState.value
        assertNull(state.selectedSmokingHabit)
        assertNull(state.selectedDrinkingHabit)
        assertFalse(state.isSelectionValid)
        assertFalse(state.isLoading)
        assertFalse(state.isSaved)
        assertNull(state.errorMessage)
    }

    @Test
    fun `selecting only one habit keeps selection invalid`() {
        viewModel.selectSmokingHabit(SmokingHabit.NEVER)
        val state1 = viewModel.uiState.value
        assertEquals(SmokingHabit.NEVER, state1.selectedSmokingHabit)
        assertNull(state1.selectedDrinkingHabit)
        assertFalse(state1.isSelectionValid)

        viewModel.selectDrinkingHabit(DrinkingHabit.SOCIAL)
        val state2 = viewModel.uiState.value
        assertEquals(SmokingHabit.NEVER, state2.selectedSmokingHabit)
        assertEquals(DrinkingHabit.SOCIAL, state2.selectedDrinkingHabit)
        assertTrue(state2.isSelectionValid)
    }

    @Test
    fun `toggling a habit off invalidates selection state`() {
        viewModel.selectSmokingHabit(SmokingHabit.OCCASIONALLY)
        viewModel.selectDrinkingHabit(DrinkingHabit.FREQUENTLY)
        assertTrue(viewModel.uiState.value.isSelectionValid)

        // Toggle smoking off by selecting same habit again
        viewModel.selectSmokingHabit(SmokingHabit.OCCASIONALLY)
        val state = viewModel.uiState.value
        assertNull(state.selectedSmokingHabit)
        assertEquals(DrinkingHabit.FREQUENTLY, state.selectedDrinkingHabit)
        assertFalse(state.isSelectionValid)
    }

    @Test
    fun `attempting to save without full selection shows error message`() = runTest {
        viewModel.saveConsumptionHabits("user_123")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertEquals("Bitte wähle für beide Kategorien eine Option aus.", state.errorMessage)
        assertFalse(state.isSaved)
        assertNull(fakeUserRepository.savedSmokingHabit)
        assertNull(fakeUserRepository.savedDrinkingHabit)
    }

    @Test
    fun `saving valid consumption habits calls repository and updates state to isSaved`() = runTest {
        viewModel.selectSmokingHabit(SmokingHabit.REGULARLY)
        viewModel.selectDrinkingHabit(DrinkingHabit.SOCIAL)
        assertTrue(viewModel.uiState.value.isSelectionValid)

        viewModel.saveConsumptionHabits("user_456")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals("user_456", fakeUserRepository.savedUserId)
        assertEquals(SmokingHabit.REGULARLY, fakeUserRepository.savedSmokingHabit)
        assertEquals(DrinkingHabit.SOCIAL, fakeUserRepository.savedDrinkingHabit)
    }

    private class FakeUserRepository : UserRepository {
        var savedUserId: String? = null
        var savedSmokingHabit: SmokingHabit? = null
        var savedDrinkingHabit: DrinkingHabit? = null

        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(null)
        override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> = flowOf(null)
        override suspend fun syncUserProfile(userId: String): Result<Unit> = Result.success(Unit)
        override suspend fun saveUser(user: UserEntity) {}
        override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) {}
        override suspend fun saveSearchIntent(userId: String, intent: SearchIntent) {}

        override suspend fun saveConsumptionHabits(
            userId: String,
            smokingHabit: SmokingHabit,
            drinkingHabit: DrinkingHabit
        ) {
            savedUserId = userId
            savedSmokingHabit = smokingHabit
            savedDrinkingHabit = drinkingHabit
        }

        override suspend fun saveProfile(
            userId: String,
            username: String,
            age: Int,
            hometown: String,
            bio: String
        ) {}

        override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = Result.success(true)
        override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> {
            return Result.success(UserEntity("1", "test", "test@kliq.de"))
        }
    }
}
