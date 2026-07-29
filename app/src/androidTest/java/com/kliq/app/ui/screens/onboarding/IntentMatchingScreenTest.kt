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
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.theme.KliqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class IntentMatchingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyOptionSelection_togglesSelectionAndUpdatesUiState() {
        val fakeRepo = FakeUserRepository()
        val viewModel = IntentMatchingViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                IntentMatchingScreen(viewModel = viewModel)
            }
        }

        // Verify initial state: No option selected
        assertNull(viewModel.uiState.value.selectedIntent)
        composeTestRule.onNodeWithText("Was suchst du bei Kliq?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Freunde").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dating / Liebe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Beides").assertIsDisplayed()

        // Click "Freunde" option card
        composeTestRule.onNodeWithText("Freunde").performClick()
        assertEquals(SearchIntent.FRIENDS, viewModel.uiState.value.selectedIntent)
        assertTrue(viewModel.uiState.value.isSelectionValid)

        // Click "Dating / Liebe" option card
        composeTestRule.onNodeWithText("Dating / Liebe").performClick()
        assertEquals(SearchIntent.DATING, viewModel.uiState.value.selectedIntent)
        assertTrue(viewModel.uiState.value.isSelectionValid)

        // Click "Beides" option card
        composeTestRule.onNodeWithText("Beides").performClick()
        assertEquals(SearchIntent.BOTH, viewModel.uiState.value.selectedIntent)
        assertTrue(viewModel.uiState.value.isSelectionValid)
    }

    @Test
    fun verifyConfirmButton_enabledOnlyWhenOptionIsSelected() {
        val fakeRepo = FakeUserRepository()
        val viewModel = IntentMatchingViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                IntentMatchingScreen(viewModel = viewModel)
            }
        }

        // Initially confirm button is disabled because no option is selected
        composeTestRule.onNodeWithText("Auswahl bestätigen").assertIsNotEnabled()

        // Select an option ("Freunde")
        composeTestRule.onNodeWithText("Freunde").performClick()

        // Confirm button should now be enabled
        composeTestRule.onNodeWithText("Auswahl bestätigen").assertIsEnabled()
        composeTestRule.onNodeWithText("Auswahl bestätigen").assertHasClickAction()

        // Toggle option off (click "Freunde" again)
        composeTestRule.onNodeWithText("Freunde").performClick()

        // Confirm button is disabled again
        composeTestRule.onNodeWithText("Auswahl bestätigen").assertIsNotEnabled()
    }

    @Test
    fun verifyNavigationTransition_savesIntentInViewModelStateAndTriggersCallback() {
        var navigationTriggered = false
        val fakeRepo = FakeUserRepository()
        val viewModel = IntentMatchingViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                IntentMatchingScreen(
                    viewModel = viewModel,
                    onIntentConfirmed = { navigationTriggered = true }
                )
            }
        }

        // Select "Beides"
        composeTestRule.onNodeWithText("Beides").performClick()
        composeTestRule.onNodeWithText("Auswahl bestätigen").assertIsEnabled()

        // Perform click on confirm button
        composeTestRule.onNodeWithText("Auswahl bestätigen").performClick()
        composeTestRule.waitForIdle()

        // Assert preference is saved in ViewModel and repository
        assertEquals(SearchIntent.BOTH, fakeRepo.savedSearchIntent)
        assertTrue(viewModel.uiState.value.isSaved)
        assertTrue(navigationTriggered)
    }

    private class FakeUserRepository : UserRepository {
        var savedSearchIntent: SearchIntent? = null

        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(null)
        override fun getUser(userId: String): Flow<UserEntity?> = flowOf(null)
        override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> = flowOf(null)
        override suspend fun syncUserProfile(userId: String): Result<Unit> = Result.success(Unit)
        override suspend fun saveUser(user: UserEntity) {}
        override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) {}

        override suspend fun saveSearchIntent(userId: String, intent: SearchIntent) {
            savedSearchIntent = intent
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
