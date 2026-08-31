package com.kliq.app.ui.screens.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.model.formatRelativeTime
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.data.repository.FeedRepository
import com.kliq.app.data.repository.ReviewRepository
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ProfileEventUi(
    val id: String,
    val clubId: String,
    val title: String,
    val dateLabel: String,
    val clubName: String,
    val price: String
)

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
    val photos: List<String> = emptyList(),
    val age: Int = 0,
    val email: String = "",
    val phoneNumber: String = "",
    val countryCode: String = "+49",
    val searchIntent: SearchIntent = SearchIntent.BOTH,
    val smokingHabit: SmokingHabit = SmokingHabit.NEVER,
    val drinkingHabit: DrinkingHabit = DrinkingHabit.NEVER,
    val isProcessingImage: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val followersList: List<UserEntity> = emptyList(),
    val followingList: List<UserEntity> = emptyList(),
    val activeFollowListDialog: FollowListType? = null,
    val ownPosts: List<ProfilePostUi> = emptyList(),
    val upcomingEvents: List<ProfileEventUi> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val averageRating: Double = 0.0,
    val formattedAverageRating: String = "0.0",
    val totalReviewsCount: Int = 0,
    val verifiedReviewsCount: Int = 0,
    val hasRatings: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Beiträge", "Events", "Historie", "Bewertungen"),
    val isOwnProfile: Boolean = true,

    val isEditDialogVisible: Boolean = false,
    val editName: String = "",
    val editBio: String = "",
    val editLocation: String = "",
    val editAge: Int = 0,
    val editEmail: String = "",
    val editPhoneNumber: String = "",
    val editCountryCode: String = "+49",
    val editSearchIntent: SearchIntent = SearchIntent.BOTH,
    val editSmokingHabit: SmokingHabit = SmokingHabit.NEVER,
    val editDrinkingHabit: DrinkingHabit = DrinkingHabit.NEVER,
    val editPhotos: List<String> = emptyList(),
    val selectedPhotoSlotIndex: Int = 0,
    val isSavingProfile: Boolean = false,
    val isProfileSavedSuccessfully: Boolean = false,

    val isQrModalVisible: Boolean = false,
    val qrCodeBitmap: Bitmap? = null,
    val isGeneratingQrCode: Boolean = false,
    val qrPayloadText: String? = null,

    val isMultiPhotoViewerVisible: Boolean = false,
    val activePhotoViewerIndex: Int = 0,

    val imageViewerState: ProfileImageViewerState = ProfileImageViewerState()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
    private val eventRepository: EventRepository,
    private val clubRepository: ClubRepository,
    private val socialRepository: SocialRepository,
    private val reviewRepository: ReviewRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val qrCodeService: QrCodeService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var currentUserAge: Int = 0

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
        observeUserPreferences(targetUserId)
        observeOwnPosts(targetUserId)
        observeFriends(targetUserId)
        observeReputation(targetUserId)
        observeReviews(targetUserId)
        observeUpcomingEvents()
    }

    private fun observeUpcomingEvents() {
        viewModelScope.launch {
            combine(
                eventRepository.getUpcomingEvents(System.currentTimeMillis()),
                clubRepository.getAllClubs()
            ) { events, clubs ->
                val favoriteClubIds = clubs.filter { it.isFavorite }.map { it.id }.toSet()
                val clubNamesById = clubs.associate { it.id to it.name }
                events.filter { favoriteClubIds.contains(it.clubId) }
                    .sortedBy { it.startTime }
                    .map { event ->
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

                    val userPhotos = if (user.photos.isNotEmpty()) {
                        user.photos
                    } else {
                        listOfNotNull(user.profilePictureUrl)
                    }

                    _uiState.update { state ->
                        state.copy(
                            displayName = user.username,
                            username = if (user.username.startsWith("@")) user.username else "@${user.username.lowercase().replace(" ", "").replace(".", "")}",
                            bio = user.bio.orEmpty(),
                            location = user.hometown.orEmpty(),
                            profilePictureUrl = user.profilePictureUrl ?: userPhotos.firstOrNull(),
                            photos = userPhotos,
                            age = user.age ?: 0,
                            email = user.email,
                            phoneNumber = user.phoneNumber.orEmpty(),
                            editName = user.username,
                            editBio = user.bio.orEmpty(),
                            editLocation = user.hometown.orEmpty(),
                            editAge = user.age ?: 0,
                            editEmail = user.email,
                            editPhoneNumber = user.phoneNumber.orEmpty(),
                            editPhotos = userPhotos
                        )
                    }
                }
        }
    }

    private fun observeUserPreferences(targetUserId: String) {
        viewModelScope.launch {
            userRepository.getUserPreferences(targetUserId)
                .catch { }
                .collect { pref ->
                    if (pref == null) return@collect
                    _uiState.update { state ->
                        state.copy(
                            searchIntent = pref.searchIntent,
                            smokingHabit = pref.smokingHabit,
                            drinkingHabit = pref.drinkingHabit,
                            editSearchIntent = pref.searchIntent,
                            editSmokingHabit = pref.smokingHabit,
                            editDrinkingHabit = pref.drinkingHabit
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
            try { socialRepository.syncSocialConnections(targetUserId) } catch (ignored: Exception) { }

            launch {
                socialRepository.getFollowers(targetUserId)
                    .catch { }
                    .collect { followers ->
                        val followerUserIds = followers.map { it.userId }
                        val resolvedUsers = try {
                            userRepository.getUsersByIds(followerUserIds).first()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        _uiState.update {
                            it.copy(
                                followersCount = followers.size,
                                followersList = resolvedUsers
                            )
                        }
                    }
            }

            launch {
                socialRepository.getFollowing(targetUserId)
                    .catch { }
                    .collect { following ->
                        val followingUserIds = following.map { it.friendUserId }
                        val resolvedUsers = try {
                            userRepository.getUsersByIds(followingUserIds).first()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        _uiState.update {
                            it.copy(
                                followingCount = following.size,
                                followingList = resolvedUsers
                            )
                        }
                    }
            }
        }
    }

    fun openFollowList(type: FollowListType) {
        _uiState.update { it.copy(activeFollowListDialog = type) }
    }

    fun closeFollowList() {
        _uiState.update { it.copy(activeFollowListDialog = null) }
    }

    fun removeFollower(followerUserId: String) {
        val currentUserId = currentUserProvider.userId()
        viewModelScope.launch {
            socialRepository.removeFriend(followerUserId, currentUserId)
        }
    }

    fun unfollowUser(targetUserId: String) {
        val currentUserId = currentUserProvider.userId()
        viewModelScope.launch {
            socialRepository.removeFriend(currentUserId, targetUserId)
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

    private fun observeReviews(targetUserId: String) {
        viewModelScope.launch {
            reviewRepository.getReviewsForTargetUser(targetUserId)
                .catch { }
                .collect { reviews ->
                    _uiState.update { it.copy(reviews = reviews) }
                }
        }
    }

    fun openMultiPhotoViewer(initialIndex: Int = 0) {
        val photos = _uiState.value.photos.ifEmpty { listOfNotNull(_uiState.value.profilePictureUrl) }
        val safeIndex = initialIndex.coerceIn(0, (photos.size - 1).coerceAtLeast(0))
        _uiState.update {
            it.copy(
                isMultiPhotoViewerVisible = true,
                activePhotoViewerIndex = safeIndex
            )
        }
    }

    fun dismissMultiPhotoViewer() {
        _uiState.update { it.copy(isMultiPhotoViewerVisible = false) }
    }

    fun nextPhoto() {
        val photos = _uiState.value.photos.ifEmpty { listOfNotNull(_uiState.value.profilePictureUrl) }
        if (photos.isEmpty()) return
        _uiState.update {
            val nextIndex = (it.activePhotoViewerIndex + 1).coerceAtMost(photos.size - 1)
            it.copy(activePhotoViewerIndex = nextIndex)
        }
    }

    fun previousPhoto() {
        _uiState.update {
            val prevIndex = (it.activePhotoViewerIndex - 1).coerceAtLeast(0)
            it.copy(activePhotoViewerIndex = prevIndex)
        }
    }

    fun onEditProfile() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                isEditDialogVisible = true,
                editName = state.displayName,
                editBio = state.bio,
                editLocation = state.location,
                editAge = state.age,
                editEmail = state.email,
                editPhoneNumber = state.phoneNumber,
                editCountryCode = state.countryCode,
                editSearchIntent = state.searchIntent,
                editSmokingHabit = state.smokingHabit,
                editDrinkingHabit = state.drinkingHabit,
                editPhotos = if (state.photos.isNotEmpty()) state.photos else listOfNotNull(state.profilePictureUrl),
                isProfileSavedSuccessfully = false
            )
        }
    }

    fun onEditDialogDismissed() {
        _uiState.update { it.copy(isEditDialogVisible = false) }
    }

    fun onPhotoSlotClicked(slotIndex: Int) {
        _uiState.update { it.copy(selectedPhotoSlotIndex = slotIndex) }
    }

    fun onPhotoSelected(context: Context, uri: Uri, slotIndex: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true, errorMessage = null) }
            val compressor = ImageCompressor(context)
            val result = compressor.compressAndSaveImage(uri)

            result.onSuccess { savedPath ->
                val currentPhotos = _uiState.value.editPhotos.toMutableList()
                if (slotIndex < currentPhotos.size) {
                    currentPhotos[slotIndex] = savedPath
                } else {
                    while (currentPhotos.size < slotIndex) {
                        currentPhotos.add("")
                    }
                    currentPhotos.add(savedPath)
                }
                val cleanedPhotos = currentPhotos.filter { it.isNotBlank() }
                _uiState.update {
                    it.copy(
                        editPhotos = cleanedPhotos,
                        isProcessingImage = false
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isProcessingImage = false,
                        errorMessage = exception.localizedMessage ?: "Fehler beim Laden des Bildes."
                    )
                }
            }
        }
    }

    fun onRemovePhoto(slotIndex: Int) {
        val currentPhotos = _uiState.value.editPhotos.toMutableList()
        if (slotIndex in currentPhotos.indices) {
            currentPhotos.removeAt(slotIndex)
        }
        _uiState.update { it.copy(editPhotos = currentPhotos) }
    }

    fun onEditNameChanged(value: String) {
        _uiState.update { it.copy(editName = value) }
    }

    fun onEditAgeChanged(value: Int) {
        _uiState.update { it.copy(editAge = value) }
    }

    fun onEditBioChanged(value: String) {
        _uiState.update { it.copy(editBio = value) }
    }

    fun onEditLocationChanged(value: String) {
        _uiState.update { it.copy(editLocation = value) }
    }

    fun onEditEmailChanged(value: String) {
        _uiState.update { it.copy(editEmail = value) }
    }

    fun onEditPhoneNumberChanged(value: String) {
        _uiState.update { it.copy(editPhoneNumber = value) }
    }

    fun onEditCountryCodeChanged(value: String) {
        _uiState.update { it.copy(editCountryCode = value) }
    }

    fun onEditSearchIntentChanged(intent: SearchIntent) {
        _uiState.update { it.copy(editSearchIntent = intent) }
    }

    fun onEditSmokingHabitChanged(habit: SmokingHabit) {
        _uiState.update { it.copy(editSmokingHabit = habit) }
    }

    fun onEditDrinkingHabitChanged(habit: DrinkingHabit) {
        _uiState.update { it.copy(editDrinkingHabit = habit) }
    }

    fun onSaveProfile(onSuccess: () -> Unit = {}) {
        val state = _uiState.value
        val name = state.editName.trim()

        if (name.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Der Anzeigename darf nicht leer sein.") }
            return
        }

        _uiState.update { it.copy(isSavingProfile = true) }

        viewModelScope.launch {
            runCatching {
                val primaryPhoto = state.editPhotos.firstOrNull { it.isNotBlank() } ?: state.profilePictureUrl
                val finalAge = if (state.editAge > 0) state.editAge else currentUserAge
                userRepository.saveProfile(
                    userId = state.userId,
                    username = name,
                    age = finalAge,
                    hometown = state.editLocation.trim(),
                    bio = state.editBio.trim(),
                    profilePictureUrl = primaryPhoto,
                    photos = state.editPhotos,
                    email = state.editEmail.trim(),
                    phoneNumber = state.editPhoneNumber.trim(),
                    searchIntent = state.editSearchIntent,
                    smokingHabit = state.editSmokingHabit,
                    drinkingHabit = state.editDrinkingHabit
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSavingProfile = false,
                        isEditDialogVisible = false,
                        isProfileSavedSuccessfully = true,
                        infoMessage = "Profil gespeichert."
                    )
                }
                onSuccess()
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

    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
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
