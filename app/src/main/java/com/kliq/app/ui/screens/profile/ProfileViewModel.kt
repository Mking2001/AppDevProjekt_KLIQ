package com.kliq.app.ui.screens.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.formatRelativeTime
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.data.repository.FeedRepository
import com.kliq.app.data.repository.SocialRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.seed.KlagenfurtSeedData
import com.kliq.app.data.util.ImageCompressor
import com.kliq.app.domain.CurrentUserProvider
import com.kliq.app.service.QrCodeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Darstellungsmodell eines Events im Profil-Tab "Events".
 *
 * @param clubId Ziel der Detailnavigation.
 * @param dateLabel Vorformatierte Datums- und Zeitangabe.
 */
data class ProfileEventUi(
    val id: String,
    val clubId: String,
    val title: String,
    val dateLabel: String,
    val clubName: String,
    val price: String
)

/**
 * Darstellungsmodell eines eigenen Beitrags im Profil-Tab "Beiträge".
 */
data class ProfilePostUi(
    val id: String,
    val contentText: String,
    val timeAgo: String,
    val clubName: String? = null,
    val likeCount: Int = 0
)

data class ProfileUiState(
    val userId: String = KlagenfurtSeedData.CURRENT_USER_ID,
    val displayName: String = "",
    val username: String = "",
    val bio: String = "",
    val location: String = "",
    val profilePictureUrl: String? = null,
    val isProcessingImage: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val ownPosts: List<ProfilePostUi> = emptyList(),
    val upcomingEvents: List<ProfileEventUi> = emptyList(),
    val averageRating: Double = 0.0,
    val formattedAverageRating: String = "0.0",
    val totalReviewsCount: Int = 0,
    val verifiedReviewsCount: Int = 0,
    val hasRatings: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Beiträge", "Events", "Historie", "Über mich"),
    val isOwnProfile: Boolean = true,

    // Profil-Bearbeitung
    val isEditDialogVisible: Boolean = false,
    val editName: String = "",
    val editBio: String = "",
    val editLocation: String = "",
    val isSavingProfile: Boolean = false,

    // QR-Code Zustände (Kapitel 5.6)
    val isQrModalVisible: Boolean = false,
    val qrCodeBitmap: Bitmap? = null,
    val isGeneratingQrCode: Boolean = false,
    val qrPayloadText: String? = null,

    // Pinch-to-Zoom Image Viewer Zustand (Kapitel 8.3)
    val imageViewerState: ProfileImageViewerState = ProfileImageViewerState()
)

/**
 * ViewModel für den Profil-Screen.
 *
 * Profildaten, eigene Beiträge und die Freundesliste werden reaktiv aus der
 * lokalen Datenbank bezogen. Änderungen aus dem Bearbeiten-Dialog werden über
 * [UserRepository.saveProfile] persistiert und über den Room-Flow zurückgespielt.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
    private val eventRepository: EventRepository,
    private val clubRepository: ClubRepository,
    private val socialRepository: SocialRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val qrCodeService: QrCodeService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Alter des Nutzers, wird beim Speichern unverändert übernommen. */
    private var currentUserAge: Int = 0

    /** Formatierung der Event-Termine im Profil-Tab. */
    private val eventDateFormat = SimpleDateFormat("EEE, dd. MMM, HH:mm", Locale.GERMAN)

    init {
        loadProfileData(currentUserProvider.userId())
    }

    fun loadProfileData(targetUserId: String = currentUserProvider.userId()) {
        val ownUserId = currentUserProvider.userId()
        _uiState.update {
            it.copy(userId = targetUserId, isOwnProfile = targetUserId == ownUserId)
        }

        observeUserProfile(targetUserId)
        observeOwnPosts(targetUserId)
        observeFriends(targetUserId)
        observeReputation(targetUserId)
        observeUpcomingEvents()
    }

    /**
     * Lädt die kommende Event-Agenda für den Profil-Tab "Events".
     * Die Einträge sind über die Club-ID mit der Detailansicht verknüpft.
     */
    private fun observeUpcomingEvents() {
        viewModelScope.launch {
            combine(
                eventRepository.getUpcomingEvents(System.currentTimeMillis()),
                clubRepository.getAllClubs()
            ) { events, clubs ->
                val clubNamesById = clubs.associate { it.id to it.name }
                events.sortedBy { it.startTime }.map { event ->
                    ProfileEventUi(
                        id = event.id,
                        clubId = event.clubId,
                        title = event.title,
                        dateLabel = eventDateFormat.format(Date(event.startTime)),
                        clubName = clubNamesById[event.clubId].orEmpty(),
                        price = event.price
                    )
                }
            }
                .catch { }
                .collect { events ->
                    _uiState.update { it.copy(upcomingEvents = events) }
                }
        }
    }

    private fun observeUserProfile(targetUserId: String) {
        viewModelScope.launch {
            userRepository.getUserById(targetUserId)
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = "Profil konnte nicht geladen werden: ${error.localizedMessage}")
                    }
                }
                .collect { user ->
                    if (user == null) return@collect
                    currentUserAge = user.age ?: 0

                    _uiState.update { state ->
                        state.copy(
                            displayName = user.username,
                            username = "@${user.username.lowercase().replace(" ", "").replace(".", "")}",
                            bio = user.bio.orEmpty(),
                            location = user.hometown.orEmpty(),
                            profilePictureUrl = user.profilePictureUrl
                        )
                    }
                }
        }
    }

    private fun observeOwnPosts(targetUserId: String) {
        viewModelScope.launch {
            feedRepository.getFeedPosts()
                .catch { }
                .collect { posts ->
                    val ownPosts = posts.filter { it.authorUserId == targetUserId }
                    _uiState.update { state ->
                        state.copy(
                            postsCount = ownPosts.size,
                            ownPosts = ownPosts.map { post ->
                                ProfilePostUi(
                                    id = post.id,
                                    contentText = post.contentText,
                                    timeAgo = formatRelativeTime(post.createdAtMs),
                                    clubName = post.clubName,
                                    likeCount = post.likeCount
                                )
                            }
                        )
                    }
                }
        }
    }

    private fun observeFriends(targetUserId: String) {
        viewModelScope.launch {
            socialRepository.getFriendsForUser(targetUserId)
                .catch { }
                .collect { friends ->
                    _uiState.update {
                        it.copy(followersCount = friends.size, followingCount = friends.size)
                    }
                }
        }
    }

    private fun observeReputation(targetUserId: String) {
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

    // =====================================================================
    // Profil bearbeiten
    // =====================================================================

    /** Öffnet den Bearbeiten-Dialog, vorbelegt mit den aktuellen Profildaten. */
    fun onEditProfile() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                isEditDialogVisible = true,
                editName = state.displayName,
                editBio = state.bio,
                editLocation = state.location
            )
        }
    }

    /** Schließt den Bearbeiten-Dialog und verwirft die Eingaben. */
    fun onEditDialogDismissed() {
        _uiState.update { it.copy(isEditDialogVisible = false) }
    }

    fun onEditNameChanged(value: String) {
        _uiState.update { it.copy(editName = value) }
    }

    fun onEditBioChanged(value: String) {
        _uiState.update { it.copy(editBio = value) }
    }

    fun onEditLocationChanged(value: String) {
        _uiState.update { it.copy(editLocation = value) }
    }

    /**
     * Speichert die geänderten Profildaten in der lokalen Datenbank.
     * Der Anzeigename darf nicht leer sein.
     */
    fun onSaveProfile() {
        val state = _uiState.value
        val name = state.editName.trim()

        if (name.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Der Anzeigename darf nicht leer sein.") }
            return
        }

        _uiState.update { it.copy(isSavingProfile = true) }

        viewModelScope.launch {
            runCatching {
                userRepository.saveProfile(
                    userId = state.userId,
                    username = name,
                    age = currentUserAge,
                    hometown = state.editLocation.trim(),
                    bio = state.editBio.trim(),
                    profilePictureUrl = state.profilePictureUrl
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSavingProfile = false,
                        isEditDialogVisible = false,
                        infoMessage = "Profil gespeichert."
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSavingProfile = false,
                        errorMessage = "Profil konnte nicht gespeichert werden: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    // =====================================================================
    // QR-Code
    // =====================================================================

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

    // =====================================================================
    // Profilbild
    // =====================================================================

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
        _uiState.update { it.copy(errorMessage = "Kamera- oder Galerie-Zugriff wurde verweigert.") }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun onFollowToggle() {
        val targetUserId = _uiState.value.userId
        if (_uiState.value.isOwnProfile) return

        viewModelScope.launch {
            socialRepository.sendFriendRequest(
                userId = currentUserProvider.userId(),
                targetUserId = targetUserId,
                isQrVerified = false
            ).onSuccess {
                _uiState.update { it.copy(infoMessage = "Kontaktanfrage gesendet.") }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "Anfrage konnte nicht gesendet werden: ${error.localizedMessage}")
                }
            }
        }
    }

    /** Setzt Fehler- und Bestätigungsmeldungen zurück. */
    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    // =====================================================================
    // Bild-Vollbildanzeige
    // =====================================================================

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
