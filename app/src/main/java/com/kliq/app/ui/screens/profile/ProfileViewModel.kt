package com.kliq.app.ui.screens.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.util.ImageCompressor
import com.kliq.app.service.QrCodeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userId: String = "current_user",
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
    val averageRating: Double = 0.0,
    val formattedAverageRating: String = "0.0",
    val totalReviewsCount: Int = 0,
    val verifiedReviewsCount: Int = 0,
    val hasRatings: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Beiträge", "Events", "Historie", "Über mich"),
    val isOwnProfile: Boolean = true,

    // QR-Code Zustände (Kapitel 5.6)
    val isQrModalVisible: Boolean = false,
    val qrCodeBitmap: Bitmap? = null,
    val isGeneratingQrCode: Boolean = false,
    val qrPayloadText: String? = null,

    // Pinch-to-Zoom Image Viewer Zustand (Kapitel 8.3)
    val imageViewerState: ProfileImageViewerState = ProfileImageViewerState()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val qrCodeService: QrCodeService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData(targetUserId: String = "current_user") {
        _uiState.update { it.copy(userId = targetUserId, isOwnProfile = (targetUserId == "current_user")) }

        viewModelScope.launch {
            userRepository.getUserById(targetUserId)
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
                                tabs = listOf("Beiträge", "Events", "Historie", "Über mich")
                            )
                        }
                    } else {
                        loadMockFallbackData()
                    }
                }
        }

        viewModelScope.launch {
            userRepository.getUserReputationSummary(targetUserId)
                .catch { error ->
                    _uiState.update { it.copy(errorMessage = error.localizedMessage) }
                }
                .collect { summary ->
                    _uiState.update { state ->
                        state.copy(
                            averageRating = summary.averageRating,
                            formattedAverageRating = summary.formattedAverageRating,
                            totalReviewsCount = summary.totalReviewsCount,
                            verifiedReviewsCount = summary.verifiedReviewsCount,
                            hasRatings = summary.hasRatings
                        )
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
                tabs = listOf("Beiträge", "Events", "Historie", "Über mich")
            )
        }
    }

    fun showQrCodeModal() {
        _uiState.update { it.copy(isQrModalVisible = true, isGeneratingQrCode = true) }
        val targetId = _uiState.value.userId

        viewModelScope.launch {
            val payload = qrCodeService.generateProfileQrPayload(targetId)
            val result = qrCodeService.generateQrCodeBitmap(targetId)

            result.onSuccess { bitmap ->
                _uiState.update {
                    it.copy(
                        qrCodeBitmap = bitmap,
                        qrPayloadText = payload,
                        isGeneratingQrCode = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isGeneratingQrCode = false,
                        errorMessage = error.localizedMessage ?: "Fehler beim Generieren des QR-Codes."
                    )
                }
            }
        }
    }

    fun dismissQrCodeModal() {
        _uiState.update { it.copy(isQrModalVisible = false) }
    }

    fun onImageSelected(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true, errorMessage = null) }
            val compressor = ImageCompressor(context)
            val result = compressor.compressAndSaveImage(uri)

            result.onSuccess { savedPath ->
                userRepository.updateProfilePicture(_uiState.value.userId, savedPath)
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

    fun openProfileImageViewer(targetUrl: String? = null) {
        val urlToDisplay = targetUrl ?: _uiState.value.profilePictureUrl
        _uiState.update { state ->
            state.copy(
                imageViewerState = ProfileImageViewerState(
                    isFullscreenVisible = true,
                    currentScale = 1.0f,
                    translationOffsetX = 0.0f,
                    translationOffsetY = 0.0f,
                    targetImageUrl = urlToDisplay
                )
            )
        }
    }

    fun dismissProfileImageViewer() {
        _uiState.update { state ->
            state.copy(
                imageViewerState = state.imageViewerState.copy(
                    isFullscreenVisible = false,
                    currentScale = 1.0f,
                    translationOffsetX = 0.0f,
                    translationOffsetY = 0.0f
                )
            )
        }
    }

    fun updateZoomState(scale: Float, offsetX: Float, offsetY: Float) {
        _uiState.update { state ->
            state.copy(
                imageViewerState = state.imageViewerState.copy(
                    currentScale = scale,
                    translationOffsetX = offsetX,
                    translationOffsetY = offsetY
                )
            )
        }
    }

    fun resetZoomState() {
        _uiState.update { state ->
            state.copy(
                imageViewerState = state.imageViewerState.copy(
                    currentScale = 1.0f,
                    translationOffsetX = 0.0f,
                    translationOffsetY = 0.0f
                )
            )
        }
    }
}
