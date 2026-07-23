package com.kliq.app.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "Max Mustermann",
    val username: String = "@maxmuster",
    val bio: String = "Nightlife-Enthusiast 🌙 | Immer unterwegs | München 📍",
    val location: String = "München, Deutschland",
    val profilePictureUrl: String? = null,
    val isProcessingImage: Boolean = false,
    val errorMessage: String? = null,
    val postsCount: Int = 127,
    val followersCount: Int = 1842,
    val followingCount: Int = 394,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Beiträge", "Events", "Über mich"),
    val isOwnProfile: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            userRepository.getUserById("current_user")
                .catch {
                    loadMockFallbackData()
                }
                .collect { user ->
                    if (user != null) {
                        _uiState.update { state ->
                            state.copy(
                                displayName = user.username.ifBlank { "Max Mustermann" },
                                username = "@${user.username.ifBlank { "maxmuster" }}",
                                bio = user.bio ?: "Nightlife-Enthusiast 🌙 | Immer unterwegs",
                                location = user.hometown ?: "München, Deutschland",
                                profilePictureUrl = user.profilePictureUrl,
                                postsCount = 127,
                                followersCount = 1842,
                                followingCount = 394,
                                tabs = listOf("Beiträge", "Events", "Über mich"),
                                isOwnProfile = true
                            )
                        }
                    } else {
                        loadMockFallbackData()
                    }
                }
        }
    }

    private fun loadMockFallbackData() {
        _uiState.update { state ->
            state.copy(
                displayName = "Max Mustermann",
                username = "@maxmuster",
                bio = "Nightlife-Enthusiast 🌙 | Immer unterwegs | München 📍",
                location = "München, Deutschland",
                postsCount = 127,
                followersCount = 1842,
                followingCount = 394,
                tabs = listOf("Beiträge", "Events", "Über mich"),
                isOwnProfile = true
            )
        }
    }

    fun onImageSelected(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true, errorMessage = null) }
            val compressor = ImageCompressor(context)
            val result = compressor.compressAndSaveImage(uri)

            result.onSuccess { savedPath ->
                userRepository.updateProfilePicture("current_user", savedPath)
                _uiState.update {
                    it.copy(
                        profilePictureUrl = savedPath,
                        isProcessingImage = false
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isProcessingImage = false,
                        errorMessage = exception.localizedMessage ?: "Fehler beim Aktualisieren des Profilbilds."
                    )
                }
            }
        }
    }

    fun onPermissionDenied(permission: String) {
        val message = "Kamera- oder Galerie-Zugriff wurde verweigert."
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun onEditProfile() {
    }

    fun onFollowToggle() {
    }
}
