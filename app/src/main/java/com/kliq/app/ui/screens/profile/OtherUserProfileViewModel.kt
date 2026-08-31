package com.kliq.app.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.model.VisitedLog
import com.kliq.app.data.repository.ReviewRepository
import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.SocialRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.repository.VisitedLogRepository
import com.kliq.app.domain.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val socialRepository: SocialRepository,
    private val feedRepository: com.kliq.app.data.repository.FeedRepository? = null,
    private val visitedLogRepository: VisitedLogRepository? = null,
    private val currentUserProvider: CurrentUserProvider? = null,
    private val sessionRepository: SessionRepository? = null,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val targetUserId: String = savedStateHandle.get<String>("userId")
        ?: savedStateHandle.get<String>("targetUserId")
        ?: ""

    private val _uiState = MutableStateFlow(OtherUserProfileUiState(userId = targetUserId))
    val uiState: StateFlow<OtherUserProfileUiState> = _uiState.asStateFlow()

    init {
        if (targetUserId.isNotBlank()) {
            loadUserProfile(targetUserId)
        }
    }

    private fun getCurrentUserId(): String {
        return currentUserProvider?.userId()
            ?: sessionRepository?.getUserId()
            ?: "current_user"
    }

    fun loadUserProfile(userId: String) {
        val currentUserId = getCurrentUserId()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, userId = userId) }

            try { userRepository.syncUserProfile(userId) } catch (ignored: Exception) { }
            try { reviewRepository.syncReviewsForTargetUser(userId) } catch (ignored: Exception) { }
            try { feedRepository?.syncFeedPosts() } catch (ignored: Exception) { }
            try { socialRepository.syncSocialConnections(userId) } catch (ignored: Exception) { }

            val userFlow = userRepository.getUserById(userId)
            val prefsFlow = userRepository.getUserPreferences(userId)
            val reviewsFlow = reviewRepository.getReviewsForTargetUser(userId)
            val isFriendFlow = socialRepository.isFriend(currentUserId, userId)
            val isBlockedFlow = userRepository.isUserBlocked(currentUserId, userId)

            combine(
                userFlow,
                prefsFlow,
                reviewsFlow,
                isFriendFlow,
                isBlockedFlow
            ) { userEntity, userPrefs, reviewsList, isFriend, isBlocked ->
                if (userEntity != null) {
                    val actualAvg = if (reviewsList.isNotEmpty()) {
                        reviewsList.map { it.rating }.average()
                    } else {
                        0.0
                    }

                    val userPhotos = if (userEntity.photos.isNotEmpty()) {
                        userEntity.photos.filter { it.isNotBlank() }
                    } else {
                        listOfNotNull(userEntity.profilePictureUrl)
                    }

                    val samePlaceVisit = try {
                        val myVisits = visitedLogRepository?.getVisitedLogsForUser(currentUserId)?.first() ?: emptyList()
                        val targetVisits = visitedLogRepository?.getVisitedLogsForUser(userId)?.first() ?: emptyList()
                        myVisits.any { myV ->
                            targetVisits.any { targetV ->
                                myV.clubId == targetV.clubId || myV.clubName.equals(targetV.clubName, ignoreCase = true)
                            }
                        }
                    } catch (e: Exception) {
                        false
                    }
                    val canReviewUser = samePlaceVisit || isFriend || userEntity.isVerified

                    val hasAlreadyReviewed = reviewsList.any { it.reviewerUserId == currentUserId }

                    _uiState.value.copy(
                        isLoading = false,
                        userId = userEntity.id,
                        username = userEntity.username.ifBlank { "User" },
                        age = userEntity.age ?: 20,
                        hometown = userEntity.hometown,
                        bio = userEntity.bio,
                        profilePictureUrl = userEntity.profilePictureUrl ?: userPhotos.firstOrNull(),
                        photos = userPhotos,
                        isVerified = userEntity.isVerified,
                        searchIntent = userPrefs?.searchIntent ?: SearchIntent.BOTH,
                        smokingHabit = userPrefs?.smokingHabit ?: SmokingHabit.NEVER,
                        drinkingHabit = userPrefs?.drinkingHabit ?: DrinkingHabit.NEVER,
                        averageRating = actualAvg,
                        reviewCount = reviewsList.size,
                        reviews = reviewsList,
                        isFriend = isFriend,
                        canReview = canReviewUser,
                        alreadyReviewed = hasAlreadyReviewed,
                        isBlocked = isBlocked,
                        isReported = false,
                        errorMessage = null
                    )
                } else {
                    _uiState.value.copy(
                        isLoading = false,
                        userId = userId,
                        username = "Kliq-Nutzer",
                        averageRating = 0.0,
                        reviewCount = 0,
                        reviews = emptyList()
                    )
                }
            }.catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Fehler beim Laden des Profils."
                    )
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }

        feedRepository?.getFeedPostsByAuthor(userId)?.let { postsFlow ->
            viewModelScope.launch {
                postsFlow.collect { postList ->
                    _uiState.update {
                        it.copy(
                            posts = postList,
                            postCount = postList.size
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            socialRepository.getFollowers(userId).collect { followers ->
                val followerUserIds = followers.map { it.userId }
                val resolvedUsers = try {
                    userRepository.getUsersByIds(followerUserIds).first()
                } catch (e: Exception) {
                    emptyList()
                }
                _uiState.update {
                    it.copy(
                        followerCount = followers.size,
                        followersList = resolvedUsers
                    )
                }
            }
        }

        viewModelScope.launch {
            socialRepository.getFollowing(userId).collect { following ->
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

    fun selectTab(tab: ProfileTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun openFollowList(type: FollowListType) {
        _uiState.update { it.copy(activeFollowListDialog = type) }
    }

    fun closeFollowList() {
        _uiState.update { it.copy(activeFollowListDialog = null) }
    }

    fun openPostDetail(post: com.kliq.app.data.model.FeedPost) {
        _uiState.update { it.copy(selectedPostForDetail = post) }
    }

    fun closePostDetail() {
        _uiState.update { it.copy(selectedPostForDetail = null) }
    }

    fun toggleLikePost(postId: String) {
        viewModelScope.launch {
            feedRepository?.toggleLike(postId)
        }
    }

    fun removeFollower(followerUserId: String) {
        val currentUserId = getCurrentUserId()
        viewModelScope.launch {
            socialRepository.removeFriend(followerUserId, currentUserId)
        }
    }

    fun unfollowUser(targetUserId: String) {
        val currentUserId = getCurrentUserId()
        viewModelScope.launch {
            socialRepository.removeFriend(currentUserId, targetUserId)
        }
    }

    fun toggleFollow() {
        val currentUserId = getCurrentUserId()
        val targetId = _uiState.value.userId
        if (targetId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingFollow = true) }
            val isCurrentlyFriend = _uiState.value.isFriend

            if (isCurrentlyFriend) {
                socialRepository.removeFriend(currentUserId, targetId)
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSubmittingFollow = false,
                                isFriend = false,
                                actionSuccessMessage = "Du bist ${it.username} entfolgt."
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isSubmittingFollow = false,
                                errorMessage = error.localizedMessage ?: "Fehler beim Entfolgen."
                            )
                        }
                    }
            } else {
                socialRepository.sendFriendRequest(currentUserId, targetId)
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSubmittingFollow = false,
                                isFriend = true,
                                canReview = true,
                                actionSuccessMessage = "Du folgst jetzt ${it.username}!"
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isSubmittingFollow = false,
                                errorMessage = error.localizedMessage ?: "Fehler beim Folgen."
                            )
                        }
                    }
            }
        }
    }

    fun openRatingSheet() {
        if (!_uiState.value.canReview) {
            _uiState.update {
                it.copy(errorMessage = "🔒 Bewertung gesperrt: Du kannst diesen Nutzer erst bewerten, wenn ihr am selben Abend am gleichen Ort oder Event wart!")
            }
            return
        }
        if (_uiState.value.alreadyReviewed) {
            _uiState.update {
                it.copy(errorMessage = "Du hast diesen Nutzer bereits bewertet. Pro Person ist nur eine Bewertung möglich.")
            }
            return
        }
        _uiState.update { it.copy(isRatingSheetVisible = true) }
    }

    fun closeRatingSheet() {
        _uiState.update { it.copy(isRatingSheetVisible = false) }
    }

    fun submitRating(rating: Int, text: String) {
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Bitte schreibe einen Text / Kommentar zu deiner Bewertung.") }
            return
        }

        val currentUserId = getCurrentUserId()
        val targetId = _uiState.value.userId

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingRating = true) }

            val result = reviewRepository.submitVerifiedUserComment(
                reviewerUserId = currentUserId,
                targetUserId = targetId,
                rating = rating,
                text = trimmedText,
                verificationMethod = ReviewVerificationMethod.GPS_GEOFENCE_MATCH
            )

            result.onSuccess { newReview ->
                val updatedReviews = listOf(newReview) + _uiState.value.reviews.filter { it.id != newReview.id }
                val newAvg = updatedReviews.map { it.rating }.average()
                _uiState.update {
                    it.copy(
                        isSubmittingRating = false,
                        isRatingSheetVisible = false,
                        reviews = updatedReviews,
                        reviewCount = updatedReviews.size,
                        averageRating = newAvg,
                        alreadyReviewed = true,
                        actionSuccessMessage = "Bewertung erfolgreich veröffentlicht!"
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isSubmittingRating = false,
                        errorMessage = ex.localizedMessage ?: "Fehler beim Abgeben der Bewertung."
                    )
                }
            }
        }
    }

    fun openProfileStoryViewer(initialIndex: Int = 0) {
        val photos = _uiState.value.photos.ifEmpty { listOfNotNull(_uiState.value.profilePictureUrl) }
        val safeIndex = initialIndex.coerceIn(0, (photos.size - 1).coerceAtLeast(0))
        _uiState.update {
            it.copy(
                isPhotoViewerVisible = true,
                activePhotoViewerIndex = safeIndex
            )
        }
    }

    fun dismissProfileStoryViewer() {
        _uiState.update { it.copy(isPhotoViewerVisible = false) }
    }

    fun onNextPhoto() {
        val photos = _uiState.value.photos.ifEmpty { listOfNotNull(_uiState.value.profilePictureUrl) }
        if (photos.isEmpty()) return
        _uiState.update {
            val nextIndex = (it.activePhotoViewerIndex + 1).coerceAtMost(photos.size - 1)
            it.copy(activePhotoViewerIndex = nextIndex)
        }
    }

    fun onPreviousPhoto() {
        _uiState.update {
            val prevIndex = (it.activePhotoViewerIndex - 1).coerceAtLeast(0)
            it.copy(activePhotoViewerIndex = prevIndex)
        }
    }

    fun openReportDialog() {
        _uiState.update { it.copy(isReportDialogVisible = true) }
    }

    fun closeReportDialog() {
        _uiState.update { it.copy(isReportDialogVisible = false) }
    }

    fun reportUser(reason: String, details: String = "") {
        val currentUserId = getCurrentUserId()
        val targetId = _uiState.value.userId
        viewModelScope.launch {
            userRepository.reportUser(currentUserId, targetId, reason, details)
            _uiState.update {
                it.copy(
                    isReported = true,
                    isReportDialogVisible = false,
                    actionSuccessMessage = "Profil wurde erfolgreich gemeldet."
                )
            }
        }
    }

    fun openBlockConfirmationDialog() {
        _uiState.update { it.copy(isBlockConfirmationDialogVisible = true) }
    }

    fun closeBlockConfirmationDialog() {
        _uiState.update { it.copy(isBlockConfirmationDialogVisible = false) }
    }

    fun toggleBlockUser() {
        if (_uiState.value.isBlocked) {
            unblockUser()
        } else {
            openBlockConfirmationDialog()
        }
    }

    fun confirmBlockUser(reason: String? = null) {
        val currentUserId = getCurrentUserId()
        val targetId = _uiState.value.userId
        viewModelScope.launch {
            userRepository.blockUser(currentUserId, targetId, reason)
            _uiState.update {
                it.copy(
                    isBlocked = true,
                    isBlockConfirmationDialogVisible = false,
                    actionSuccessMessage = "Nutzer wurde blockiert."
                )
            }
        }
    }

    fun unblockUser() {
        val currentUserId = getCurrentUserId()
        val targetId = _uiState.value.userId
        viewModelScope.launch {
            userRepository.unblockUser(currentUserId, targetId)
            _uiState.update {
                it.copy(
                    isBlocked = false,
                    isBlockConfirmationDialogVisible = false,
                    actionSuccessMessage = "Blockierung aufgehoben."
                )
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(errorMessage = null, actionSuccessMessage = null) }
    }

    fun retry() {
        if (targetUserId.isNotBlank()) {
            loadUserProfile(targetUserId)
        }
    }
}
