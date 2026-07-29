package com.kliq.app.ui.screens.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.theme.KliqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProfileImageUploadUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun avatarClick_opensImagePickerBottomSheetWithOptions() {
        val fakeRepo = FakeUserRepository()
        val viewModel = ProfileCreationViewModel(fakeRepo)

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                ProfileCreationScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithContentDescription("Profilbild Platzhalter").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Profilbild Platzhalter").performClick()

        composeTestRule.onNodeWithText("Profilbild auswählen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kamera").assertIsDisplayed()
        composeTestRule.onNodeWithText("Galerie / Foto-Mediathek").assertIsDisplayed()
    }

    @Test
    fun selectionState_updatesProfilePictureUrlInViewModelState() {
        val fakeRepo = FakeUserRepository()
        val viewModel = ProfileCreationViewModel(fakeRepo)

        viewModel.onProfilePictureUrlSet("/data/user/0/com.kliq.app/files/profile_images/test.jpg")

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                ProfileCreationScreen(viewModel = viewModel)
            }
        }

        assertEquals(
            "/data/user/0/com.kliq.app/files/profile_images/test.jpg",
            viewModel.uiState.value.profilePictureUrl
        )
        composeTestRule.onNodeWithContentDescription("Profilbild").assertIsDisplayed()
    }

    @Test
    fun permissionDenied_displaysSnackbarErrorMessage() {
        val fakeRepo = FakeUserRepository()
        val viewModel = ProfileCreationViewModel(fakeRepo)

        viewModel.onPermissionDenied("android.permission.CAMERA")

        composeTestRule.setContent {
            KliqTheme(darkTheme = true) {
                ProfileCreationScreen(viewModel = viewModel)
            }
        }

        assertNotNull(viewModel.uiState.value.permissionDeniedMessage)
        assertTrue(viewModel.uiState.value.permissionDeniedMessage!!.contains("Kamerazugriff"))
    }

    private class FakeUserRepository : UserRepository {
        var savedUser: UserEntity? = null

        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(savedUser)
        override fun getUserPreferences(userId: String): Flow<com.kliq.app.data.local.entities.UserPreferencesEntity?> = flowOf(null)
        override suspend fun syncUserProfile(userId: String): Result<Unit> = Result.success(Unit)
        override suspend fun saveUser(user: UserEntity) {}
        override suspend fun saveUserPreferences(preferences: com.kliq.app.data.local.entities.UserPreferencesEntity) {}
        override suspend fun saveSearchIntent(userId: String, intent: com.kliq.app.data.model.SearchIntent) {}

        override suspend fun saveProfile(
            userId: String,
            username: String,
            age: Int,
            hometown: String,
            bio: String,
            profilePictureUrl: String?
        ) {
            savedUser = UserEntity(userId, username, "test@kliq.de", age, hometown, profilePictureUrl, bio)
        }

        override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = Result.success(true)
        override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> {
            return Result.success(UserEntity("1", "test", "test@kliq.de"))
        }
    }
}
