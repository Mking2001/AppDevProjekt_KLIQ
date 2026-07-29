package com.kliq.app.ui.screens.onboarding

import com.kliq.app.data.local.entities.UserEntity
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
class ProfileCreationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: ProfileCreationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        viewModel = ProfileCreationViewModel(fakeUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is invalid and empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.username)
        assertEquals("", state.age)
        assertEquals("", state.hometown)
        assertEquals("", state.bio)
        assertNull(state.profilePictureUrl)
        assertFalse(state.isFormValid)
    }

    @Test
    fun `invalid inputs set appropriate error messages and keep form invalid`() {
        viewModel.onUsernameChanged("ab")
        assertNotNull(viewModel.uiState.value.usernameError)
        assertFalse(viewModel.uiState.value.isFormValid)

        viewModel.onAgeChanged("16")
        assertNotNull(viewModel.uiState.value.ageError)
        assertEquals("Du musst mindestens 18 Jahre alt sein.", viewModel.uiState.value.ageError)
        assertFalse(viewModel.uiState.value.isFormValid)

        val longBio = "a".repeat(151)
        viewModel.onBioChanged(longBio)
        assertNotNull(viewModel.uiState.value.bioError)
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun `setting profile picture url updates ui state`() {
        val imagePath = "/storage/emulated/0/Pictures/avatar.jpg"
        viewModel.onProfilePictureUrlSet(imagePath)

        assertEquals(imagePath, viewModel.uiState.value.profilePictureUrl)
    }

    @Test
    fun `permission denied updates permission error message`() {
        viewModel.onPermissionDenied("android.permission.CAMERA")

        assertNotNull(viewModel.uiState.value.permissionDeniedMessage)
        assertTrue(viewModel.uiState.value.permissionDeniedMessage!!.contains("Kamera"))

        viewModel.onPermissionMessageDismissed()
        assertNull(viewModel.uiState.value.permissionDeniedMessage)
    }

    @Test
    fun `valid inputs enable form and save user profile with picture to repository`() = runTest {
        viewModel.onUsernameChanged("kliq_user")
        viewModel.onAgeChanged("22")
        viewModel.onHometownChanged("Berlin")
        viewModel.onBioChanged("Nightlife enthusiast")
        viewModel.onProfilePictureUrlSet("/storage/profile.jpg")

        val state = viewModel.uiState.value
        assertNull(state.usernameError)
        assertNull(state.ageError)
        assertNull(state.hometownError)
        assertNull(state.bioError)
        assertTrue(state.isFormValid)

        viewModel.onSaveProfile("user_123")
        testDispatcher.scheduler.advanceUntilIdle()

        val savedUser = fakeUserRepository.savedUser
        assertNotNull(savedUser)
        assertEquals("user_123", savedUser?.id)
        assertEquals("kliq_user", savedUser?.username)
        assertEquals(22, savedUser?.age)
        assertEquals("Berlin", savedUser?.hometown)
        assertEquals("Nightlife enthusiast", savedUser?.bio)
        assertEquals("/storage/profile.jpg", savedUser?.profilePictureUrl)
        assertTrue(viewModel.uiState.value.isProfileSaved)
    }

    private class FakeUserRepository : UserRepository {
        var savedUser: UserEntity? = null

        override fun getUserById(userId: String): Flow<UserEntity?> = flowOf(savedUser)
        override fun getUser(userId: String): Flow<UserEntity?> = flowOf(savedUser)
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
            savedUser = UserEntity(
                id = userId,
                username = username,
                email = "test@kliq.de",
                age = age,
                hometown = hometown,
                profilePictureUrl = profilePictureUrl,
                bio = bio
            )
        }

        override suspend fun updateProfilePicture(userId: String, pictureUrl: String) {
            savedUser = savedUser?.copy(profilePictureUrl = pictureUrl) ?: UserEntity(
                id = userId,
                username = "user",
                email = "",
                profilePictureUrl = pictureUrl
            )
        }

        override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = Result.success(true)
        override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> {
            return Result.success(UserEntity(id = "1", username = "test", email = "test@kliq.de"))
        }
    }
}
