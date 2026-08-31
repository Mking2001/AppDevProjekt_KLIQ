package com.kliq.app.ui.screens.profile

import com.kliq.app.testing.createTestProfileViewModel
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        val qrCodeService = com.kliq.app.service.QrCodeServiceImpl(testDispatcher)
        viewModel = createTestProfileViewModel(fakeUserRepository, qrCodeService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads user profile data from repository`() = runTest {
        val testUser = UserEntity(
            id = "current_user",
            username = "alex_night",
            email = "alex@kliq.app",
            hometown = "Hamburg",
            profilePictureUrl = "/path/to/profile.jpg",
            bio = "Techno Fan"
        )
        fakeUserRepository.emitUser(testUser)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("alex_night", state.displayName)
        assertEquals("@alex_night", state.username)
        assertEquals("Hamburg", state.location)
        assertEquals("/path/to/profile.jpg", state.profilePictureUrl)
        assertEquals("Techno Fan", state.bio)
    }

    @Test
    fun `tab selection updates selected tab index`() {
        viewModel.onTabSelected(1)
        assertEquals(1, viewModel.uiState.value.selectedTabIndex)

        viewModel.onTabSelected(2)
        assertEquals(2, viewModel.uiState.value.selectedTabIndex)
    }

    @Test
    fun `permission denial updates error message`() {
        viewModel.onPermissionDenied("android.permission.CAMERA")
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("verweigert"))
    }

    @Test
    fun `openProfileImageViewer sets full screen visibility and target image url`() {
        viewModel.openProfileImageViewer("https://kliq.app/images/profile.jpg")
        val state = viewModel.uiState.value.imageViewerState

        assertTrue(state.isFullscreenVisible)
        assertEquals("https://kliq.app/images/profile.jpg", state.targetImageUrl)
        assertEquals(1.0f, state.currentScale, 0.001f)
    }

    @Test
    fun `updateZoomState updates scale and translation offsets`() {
        viewModel.openProfileImageViewer("https://kliq.app/images/profile.jpg")
        viewModel.updateZoomState(2.5f, 100f, -50f)

        val state = viewModel.uiState.value.imageViewerState
        assertEquals(2.5f, state.currentScale, 0.001f)
        assertEquals(100f, state.translationOffsetX, 0.001f)
        assertEquals(-50f, state.translationOffsetY, 0.001f)
    }

    @Test
    fun `resetZoomState restores scale to one and offsets to zero`() {
        viewModel.openProfileImageViewer("https://kliq.app/images/profile.jpg")
        viewModel.updateZoomState(3.0f, 200f, 150f)
        viewModel.resetZoomState()

        val state = viewModel.uiState.value.imageViewerState
        assertEquals(1.0f, state.currentScale, 0.001f)
        assertEquals(0.0f, state.translationOffsetX, 0.001f)
        assertEquals(0.0f, state.translationOffsetY, 0.001f)
    }

    @Test
    fun `dismissProfileImageViewer hides modal and resets zoom parameters`() {
        viewModel.openProfileImageViewer("https://kliq.app/images/profile.jpg")
        viewModel.updateZoomState(2.0f, 50f, 50f)
        viewModel.dismissProfileImageViewer()

        val state = viewModel.uiState.value.imageViewerState
        assertFalse(state.isFullscreenVisible)
        assertEquals(1.0f, state.currentScale, 0.001f)
    }

    @Test
    fun `profile has 4 tabs including Bewertungen`() {
        val tabs = viewModel.uiState.value.tabs
        assertEquals(4, tabs.size)
        assertEquals(listOf("Beiträge", "Events", "Historie", "Bewertungen"), tabs)
    }

    @Test
    fun `multi photo viewer opens navigates and dismisses correctly`() = runTest {
        val testUser = UserEntity(
            id = "current_user",
            username = "alex_night",
            email = "alex@kliq.app",
            photos = listOf("photo1.jpg", "photo2.jpg", "photo3.jpg")
        )
        fakeUserRepository.emitUser(testUser)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.openMultiPhotoViewer(0)
        assertTrue(viewModel.uiState.value.isMultiPhotoViewerVisible)
        assertEquals(0, viewModel.uiState.value.activePhotoViewerIndex)

        viewModel.nextPhoto()
        assertEquals(1, viewModel.uiState.value.activePhotoViewerIndex)

        viewModel.nextPhoto()
        assertEquals(2, viewModel.uiState.value.activePhotoViewerIndex)

        viewModel.nextPhoto()
        assertEquals(2, viewModel.uiState.value.activePhotoViewerIndex)

        viewModel.previousPhoto()
        assertEquals(1, viewModel.uiState.value.activePhotoViewerIndex)

        viewModel.dismissMultiPhotoViewer()
        assertFalse(viewModel.uiState.value.isMultiPhotoViewerVisible)
    }

    @Test
    fun `photo slot click and remove updates edit photos state`() {
        viewModel.onPhotoSlotClicked(2)
        assertEquals(2, viewModel.uiState.value.selectedPhotoSlotIndex)

        viewModel.onRemovePhoto(0)
        assertTrue(viewModel.uiState.value.editPhotos.isEmpty())
    }

    @Test
    fun `saveProfile saves updated profile and preferences`() = runTest {
        viewModel.onEditNameChanged("Max Mustermann")
        viewModel.onEditBioChanged("Club lover")
        viewModel.onEditLocationChanged("Wien")
        viewModel.onEditAgeChanged(22)
        viewModel.onEditSearchIntentChanged(com.kliq.app.data.model.SearchIntent.DATING)
        viewModel.onEditSmokingHabitChanged(com.kliq.app.data.model.SmokingHabit.OCCASIONALLY)
        viewModel.onEditDrinkingHabitChanged(com.kliq.app.data.model.DrinkingHabit.SOCIAL)

        viewModel.onSaveProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isProfileSavedSuccessfully)
        assertEquals("Profil gespeichert.", viewModel.uiState.value.infoMessage)
    }

    private class FakeUserRepository : UserRepository {
        private val userFlow = MutableStateFlow<UserEntity?>(null)
        private val prefFlow = MutableStateFlow<com.kliq.app.data.local.entities.UserPreferencesEntity?>(null)

        fun emitUser(user: UserEntity?) {
            userFlow.value = user
        }

        override fun getUserById(userId: String): Flow<UserEntity?> = userFlow
        override fun getUser(userId: String): Flow<UserEntity?> = userFlow
        override fun getUserPreferences(userId: String): Flow<com.kliq.app.data.local.entities.UserPreferencesEntity?> = prefFlow
        override suspend fun syncUserProfile(userId: String): Result<Unit> = Result.success(Unit)
        override suspend fun saveUser(user: UserEntity) { userFlow.value = user }
        override suspend fun saveUserPreferences(preferences: com.kliq.app.data.local.entities.UserPreferencesEntity) { prefFlow.value = preferences }
        override suspend fun saveSearchIntent(userId: String, intent: com.kliq.app.data.model.SearchIntent) {}
        override suspend fun saveProfile(
            userId: String,
            username: String,
            age: Int,
            hometown: String,
            bio: String,
            profilePictureUrl: String?,
            photos: List<String>,
            email: String?,
            phoneNumber: String?,
            searchIntent: com.kliq.app.data.model.SearchIntent?,
            smokingHabit: com.kliq.app.data.model.SmokingHabit?,
            drinkingHabit: com.kliq.app.data.model.DrinkingHabit?
        ) {
            userFlow.value = UserEntity(
                id = userId,
                username = username,
                email = email ?: "test@kliq.app",
                age = age,
                hometown = hometown,
                bio = bio,
                profilePictureUrl = profilePictureUrl,
                photos = photos
            )
        }

        override suspend fun updateProfilePicture(userId: String, pictureUrl: String) {
            userFlow.value = userFlow.value?.copy(profilePictureUrl = pictureUrl)
        }

        override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = Result.success(true)
        override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> {
            return Result.success(UserEntity(id = "1", username = "test", email = "test@kliq.de"))
        }
    }
}
