package com.kliq.app.ui.screens.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.theme.KliqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ConsumptionHabitsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyLayoutAndRendering_displaysHeaderAndAllOptionsInDarkMode() {
        val fakeRepo = FakeUserRepository()
        val viewModel = ConsumptionHabitsViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                ConsumptionHabitsScreen(viewModel = viewModel)
            }
        }

        // 1. Layout & Rendering Test
        composeTestRule.onNodeWithText("SCHRITT 3 VON 3 • KONSUM-GEWOHNHEITEN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rauchen & Trinken").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rauchverhalten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trinkverhalten").assertIsDisplayed()

        // Verify smoking options rendering
        composeTestRule.onNodeWithText("Ich rauche gar nicht").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ab und zu beim Feiern oder in Gesellschaft").assertIsDisplayed()
        composeTestRule.onNodeWithText("Regelmäßiger Raucher im Alltag").assertIsDisplayed()

        // Verify drinking options rendering
        composeTestRule.onNodeWithText("Ich trinke keinen Alkohol").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ab und zu in Gesellschaft oder bei Events").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gerne und regelmäßig beim Ausgehen").assertIsDisplayed()
    }

    @Test
    fun verifyInteraction_togglesSelectionStateAndEnablesButtonWhenBothSelected() {
        val fakeRepo = FakeUserRepository()
        val viewModel = ConsumptionHabitsViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                ConsumptionHabitsScreen(viewModel = viewModel)
            }
        }

        // 2. Interaction Test: Confirm button is initially disabled
        composeTestRule.onNodeWithText("Auswahl speichern & Weiter").assertIsNotEnabled()

        // Select smoking option
        composeTestRule.onNodeWithText("Ich rauche gar nicht").performClick()
        assertEquals(SmokingHabit.NEVER, viewModel.uiState.value.selectedSmokingHabit)
        assertNull(viewModel.uiState.value.selectedDrinkingHabit)
        // Button still disabled until drinking option is also selected
        composeTestRule.onNodeWithText("Auswahl speichern & Weiter").assertIsNotEnabled()

        // Select drinking option
        composeTestRule.onNodeWithText("Ab und zu in Gesellschaft oder bei Events").performClick()
        assertEquals(DrinkingHabit.SOCIAL, viewModel.uiState.value.selectedDrinkingHabit)

        // Now button must be enabled
        composeTestRule.onNodeWithText("Auswahl speichern & Weiter").assertIsEnabled()
        composeTestRule.onNodeWithText("Auswahl speichern & Weiter").assertHasClickAction()
    }

    @Test
    fun verifyStateAndDataPersistence_savesConsumptionHabitsAndTriggersNavigation() {
        var navigationTriggered = false
        val fakeRepo = FakeUserRepository()
        val viewModel = ConsumptionHabitsViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                ConsumptionHabitsScreen(
                    viewModel = viewModel,
                    onHabitsConfirmed = { navigationTriggered = true }
                )
            }
        }

        // 3. State & Persistence Test
        composeTestRule.onNodeWithText("Regelmäßiger Raucher im Alltag").performClick()
        composeTestRule.onNodeWithText("Gerne und regelmäßig beim Ausgehen").performClick()

        composeTestRule.onNodeWithText("Auswahl speichern & Weiter").assertIsEnabled()
        composeTestRule.onNodeWithText("Auswahl speichern & Weiter").performClick()

        composeTestRule.waitForIdle()

        // Verify state passed to ViewModel & persisted in Repository
        assertEquals(SmokingHabit.REGULARLY, fakeRepo.savedSmokingHabit)
        assertEquals(DrinkingHabit.FREQUENTLY, fakeRepo.savedDrinkingHabit)
        assertTrue(viewModel.uiState.value.isSaved)
        assertTrue(navigationTriggered)
    }

    private class FakeUserRepository : UserRepository {
        var savedSmokingHabit: SmokingHabit? = null
        var savedDrinkingHabit: DrinkingHabit? = null

        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(null)
        override fun getUser(userId: String): Flow<UserEntity?> = flowOf(null)
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
            return Result.success(UserEntity(id = "1", username = "test", email = "test@kliq.de"))
        }
    }
}
