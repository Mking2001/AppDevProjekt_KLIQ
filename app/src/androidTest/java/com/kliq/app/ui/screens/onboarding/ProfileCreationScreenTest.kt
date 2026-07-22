package com.kliq.app.ui.screens.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.theme.KliqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProfileCreationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyValidation_invalidInputs_buttonDisabledAndErrorDisplayed() {
        val fakeRepo = FakeUserRepository()
        val viewModel = ProfileCreationViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                ProfileCreationScreen(viewModel = viewModel)
            }
        }

        // Initially button is disabled
        composeTestRule.onNodeWithText("Profil erstellen").assertIsNotEnabled()

        // Input invalid age (16)
        composeTestRule.onNodeWithText("Alter *").performTextInput("16")
        composeTestRule.onNodeWithText("Du musst mindestens 18 Jahre alt sein.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profil erstellen").assertIsNotEnabled()
    }

    @Test
    fun verifySuccessWorkflow_validInputs_enablesButtonAndTriggersNavigation() {
        var navigationTriggered = false
        val fakeRepo = FakeUserRepository()
        val viewModel = ProfileCreationViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                ProfileCreationScreen(
                    viewModel = viewModel,
                    onProfileCreated = { navigationTriggered = true }
                )
            }
        }

        // Fill form with valid inputs
        composeTestRule.onNodeWithText("Benutzername *").performTextInput("test_hero")
        composeTestRule.onNodeWithText("Alter *").performTextInput("25")
        composeTestRule.onNodeWithText("Heimatstadt *").performTextInput("Hamburg")
        composeTestRule.onNodeWithText("Bio / Über mich").performTextInput("Loves EDM festival summer!")

        // Button should now be enabled
        composeTestRule.onNodeWithText("Profil erstellen").assertIsEnabled()
        composeTestRule.onNodeWithText("Profil erstellen").assertHasClickAction()
    }

    private class FakeUserRepository : UserRepository(
        apiService = fakeApiService(),
        userDao = fakeUserDao()
    ) {
        var savedUser: UserEntity? = null

        override suspend fun saveProfile(
            userId: String,
            username: String,
            age: Int,
            hometown: String,
            bio: String
        ) {
            savedUser = UserEntity(userId, username, "test@kliq.de", age, hometown, null, bio)
        }
    }

    companion object {
        private fun fakeApiService(): com.kliq.app.data.remote.KliqApiService {
            return object : com.kliq.app.data.remote.KliqApiService {
                override suspend fun getUserProfile(userId: String): UserEntity {
                    return UserEntity("1", "test", "test@kliq.de", 20, "Berlin", null, null)
                }
            }
        }

        private fun fakeUserDao(): UserDao {
            return object : UserDao {
                override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(null)
                override suspend fun getUserByIdOneShot(userId: String): UserEntity? = null
                override suspend fun insertUser(user: UserEntity) {}
                override suspend fun clearUsers() {}
            }
        }
    }
}
