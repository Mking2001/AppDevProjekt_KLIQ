package com.kliq.app.ui.screens.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.formatRelativeTime
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.FeedRepository
import com.kliq.app.data.repository.SocialRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.util.ImageCompressor
import com.kliq.app.domain.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Immutable UI State für den Home-Screen.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val feedItems: List<FeedItemUi> = emptyList(),
    val storyItems: List<StoryItemUi> = emptyList(),
    val myStory: StoryItemUi? = null,
    val isComposerVisible: Boolean = false,
    val composerText: String = "",
    val composerImageUri: String? = null,
    val composerLocation: String = "",
    val composerIsEventPinned: Boolean = false,
    val isPublishing: Boolean = false,
    val activeStory: StoryItemUi? = null,
    val isPostingStory: Boolean = false,
    val activeCommentPostId: String? = null,
    val comments: List<CommentItemUi> = emptyList(),
    val commentInput: String = "",
    val activeSharePost: FeedItemUi? = null,
    val shareSearchQuery: String = "",
    val shareContacts: List<ShareContactUi> = emptyList(),
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val myProfilePictureUrl: String? = null,
    val myUserName: String = "Du",
    val userSearchQuery: String = "",
    val userSearchResults: List<SearchedUserUi> = emptyList(),
    val isSearchingUsers: Boolean = false,
    val isUserSearchActive: Boolean = false
)

/**
 * Darstellungsmodell eines gesuchten Nutzers.
 */
data class SearchedUserUi(
    val id: String,
    val username: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
    val hometown: String? = null,
    val isVerified: Boolean = false
)

/**
 * Darstellungsmodell eines Feed-Beitrags.
 */
data class FeedItemUi(
    val id: String,
    val authorUserId: String = "",
    val userName: String,
    val timeAgo: String,
    val contentText: String,
    val clubName: String? = null,
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val commentCount: Int = 0,
    val isPinnedToMap: Boolean = false,
    val isOwnPost: Boolean = false
)

/**
 * Darstellungsmodell einer Story-Kachel.
 */
data class StoryItemUi(
    val id: String,
    val authorUserId: String = "",
    val userName: String,
    val headline: String = "",
    val createdAtFormatted: String = "",
    val clubName: String? = null,
    val imageUrl: String? = null,
    val hasUnseenStory: Boolean = true,
    val isOwnStory: Boolean = false
)

/**
 * Darstellungsmodell eines Kommentars im Kommentar-Sheet.
 */
data class CommentItemUi(
    val id: String,
    val authorName: String,
    val text: String,
    val timeAgo: String
)

/**
 * Darstellungsmodell eines Kontakts im Teilen-Dialog.
 */
data class ShareContactUi(
    val id: String,
    val name: String,
    val avatarInitial: String,
    val lastMessage: String = ""
)

/**
 * ViewModel für den Home-Feed.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: UserRepository? = null,
    private val chatRepository: ChatRepository? = null,
    private val socialRepository: SocialRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var commentObserverJob: Job? = null

    init {
        observeFeed()
        observeStories()
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            val currentUserId = currentUserProvider.userId()
            userRepository?.getUserById(currentUserId)?.collect { user ->
                _uiState.update {
                    it.copy(
                        myProfilePictureUrl = user?.profilePictureUrl,
                        myUserName = user?.username ?: "Du"
                    )
                }
            }
        }
    }

    private fun observeFeed() {
        viewModelScope.launch {
            val currentUserId = currentUserProvider.userId()
            feedRepository.getFeedPosts()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Feed konnte nicht geladen werden: ${error.localizedMessage}"
                        )
                    }
                }
                .collect { posts ->
                    val items = posts.map { post ->
                        FeedItemUi(
                            id = post.id,
                            authorUserId = post.authorUserId,
                            userName = post.authorName,
                            timeAgo = formatRelativeTime(post.createdAtMs),
                            contentText = post.contentText,
                            clubName = post.clubName,
                            imageUrl = post.imageUrl,
                            likeCount = post.likeCount,
                            isLiked = post.isLikedByMe,
                            commentCount = post.commentCount,
                            isOwnPost = (post.authorUserId == currentUserId)
                        )
                    }
                    _uiState.update { it.copy(feedItems = items, isLoading = false) }
                }
        }
    }

    fun onDeletePost(postId: String) {
        viewModelScope.launch {
            feedRepository.deletePost(postId)
            _uiState.update { state ->
                state.copy(
                    feedItems = state.feedItems.filter { it.id != postId },
                    infoMessage = "Beitrag wurde gelöscht."
                )
            }
        }
    }

    private fun observeStories() {
        viewModelScope.launch {
            val currentUserId = currentUserProvider.userId()
            val timeFormat = SimpleDateFormat("HH:mm 'Uhr'", Locale.GERMANY)

            val friendsFlow: Flow<Set<String>> = socialRepository?.getFriendsForUser(currentUserId)
                ?.map { friends ->
                    friends.filter { it.status == "ACCEPTED" }.map { it.friendUserId }.toSet()
                } ?: flowOf(emptySet())

            combine(feedRepository.getStories(), friendsFlow) { stories, friendIds ->
                Pair(stories, friendIds)
            }
            .catch { error ->
                _uiState.update {
                    it.copy(errorMessage = "Storys konnten nicht geladen werden: ${error.localizedMessage}")
                }
            }
            .collect { (stories, friendIds) ->
                val myStoryEntity = stories.find { it.authorUserId == currentUserId }
                val myStoryUi = myStoryEntity?.let {
                    StoryItemUi(
                        id = it.id,
                        authorUserId = it.authorUserId,
                        userName = "Deine Story",
                        headline = it.headline,
                        createdAtFormatted = timeFormat.format(Date(it.createdAtMs)),
                        clubName = it.clubName ?: "Klagenfurt",
                        imageUrl = it.imageUrl,
                        hasUnseenStory = false,
                        isOwnStory = true
                    )
                }

                // Nur Storys von bestätigten Freunden oder Clubs anzeigen
                val otherStories = stories
                    .filter { it.authorUserId != currentUserId && (friendIds.contains(it.authorUserId) || it.authorUserId.startsWith("club_")) }
                    .map { story ->
                        StoryItemUi(
                            id = story.id,
                            authorUserId = story.authorUserId,
                            userName = story.authorName,
                            headline = story.headline,
                            createdAtFormatted = timeFormat.format(Date(story.createdAtMs)),
                            clubName = story.clubName ?: "Klagenfurt",
                            imageUrl = story.imageUrl,
                            hasUnseenStory = !story.isSeen,
                            isOwnStory = false
                        )
                    }

                _uiState.update { state ->
                    state.copy(
                        myStory = myStoryUi,
                        storyItems = otherStories,
                        activeStory = state.activeStory?.let { active ->
                            if (active.id == myStoryUi?.id) myStoryUi
                            else otherStories.find { it.id == active.id } ?: active
                        }
                    )
                }
            }
        }
    }

    fun onPostStory(context: Context, uri: Uri, location: String = "Klagenfurt") {
        viewModelScope.launch {
            _uiState.update { it.copy(isPostingStory = true) }
            val compressor = ImageCompressor(context)
            val compressResult = compressor.compressAndSaveImage(uri)

            val currentUserId = currentUserProvider.userId()
            val currentUserName = currentUserProvider.displayName()
            val avatarUrl = _uiState.value.myProfilePictureUrl
            val timeString = SimpleDateFormat("HH:mm 'Uhr'", Locale.GERMANY).format(Date())

            compressResult.onSuccess { imagePath ->
                feedRepository.createStory(
                    authorUserId = currentUserId,
                    authorName = currentUserName,
                    imageUrl = imagePath,
                    avatarUrl = avatarUrl,
                    headline = timeString,
                    clubName = location
                ).onSuccess {
                    _uiState.update { it.copy(isPostingStory = false, infoMessage = "Story veröffentlicht!") }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isPostingStory = false,
                            errorMessage = "Story konnte nicht gespeichert werden: ${error.localizedMessage}"
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isPostingStory = false,
                        errorMessage = "Bild konnte nicht verarbeitet werden: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    fun onDeleteStory(storyId: String) {
        viewModelScope.launch {
            feedRepository.deleteStory(storyId).onSuccess {
                _uiState.update { state ->
                    state.copy(
                        activeStory = null,
                        myStory = if (state.myStory?.id == storyId) null else state.myStory,
                        storyItems = state.storyItems.filter { it.id != storyId },
                        infoMessage = "Story gelöscht."
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "Story konnte nicht gelöscht werden: ${error.localizedMessage}")
                }
            }
        }
    }

    fun refreshFeed() {
        _uiState.update { it.copy(isRefreshing = true) }
        _uiState.update { it.copy(isRefreshing = false) }
    }

    // =====================================================================
    // Storys
    // =====================================================================

    fun onStoryOpened(storyId: String) {
        val state = _uiState.value
        val story = if (state.myStory?.id == storyId) {
            state.myStory
        } else {
            state.storyItems.find { it.id == storyId }
        } ?: return

        _uiState.update { it.copy(activeStory = story) }

        viewModelScope.launch {
            runCatching { feedRepository.markStoryAsSeen(storyId) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = "Story konnte nicht als gesehen markiert werden: ${error.localizedMessage}")
                    }
                }
        }
    }

    fun onStoryDismissed() {
        _uiState.update { it.copy(activeStory = null) }
    }

    // =====================================================================
    // Beitrags-Editor
    // =====================================================================

    fun onCreatePost() {
        _uiState.update {
            it.copy(
                isComposerVisible = true,
                composerText = "",
                composerImageUri = null,
                composerLocation = "",
                composerIsEventPinned = false
            )
        }
    }

    fun onComposerDismissed() {
        _uiState.update {
            it.copy(
                isComposerVisible = false,
                composerText = "",
                composerImageUri = null,
                composerLocation = "",
                composerIsEventPinned = false
            )
        }
    }

    fun onComposerTextChanged(text: String) {
        _uiState.update { it.copy(composerText = text) }
    }

    fun onComposerImageSelected(context: Context, uri: Uri) {
        viewModelScope.launch {
            val compressor = ImageCompressor(context)
            compressor.compressAndSaveImage(uri).onSuccess { path ->
                _uiState.update { it.copy(composerImageUri = path) }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = "Bild konnte nicht geladen werden: ${error.localizedMessage}") }
            }
        }
    }

    fun onComposerImageRemoved() {
        _uiState.update { it.copy(composerImageUri = null) }
    }

    fun onComposerLocationChanged(location: String) {
        _uiState.update { it.copy(composerLocation = location) }
    }

    fun onToggleComposerEventPinned() {
        _uiState.update { it.copy(composerIsEventPinned = !it.composerIsEventPinned) }
    }

    fun onPublishPost() {
        val text = _uiState.value.composerText.trim()
        val image = _uiState.value.composerImageUri
        val location = _uiState.value.composerLocation.trim().ifBlank { null }
        if (text.isEmpty() && image == null) {
            _uiState.update { it.copy(errorMessage = "Bitte gib einen Text oder ein Bild für den Beitrag ein.") }
            return
        }

        _uiState.update { it.copy(isPublishing = true) }

        viewModelScope.launch {
            val userId = currentUserProvider.userId()
            val userName = currentUserProvider.displayName()

            feedRepository.createPost(
                authorUserId = userId,
                authorName = userName,
                contentText = text,
                imageUrl = image,
                clubName = location
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isPublishing = false,
                        isComposerVisible = false,
                        composerText = "",
                        composerImageUri = null,
                        composerLocation = "",
                        composerIsEventPinned = false,
                        infoMessage = "Beitrag veröffentlicht!"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isPublishing = false,
                        errorMessage = "Beitrag konnte nicht gespeichert werden: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    // =====================================================================
    // Likes und Kommentare
    // =====================================================================

    fun onLikePost(postId: String) {
        viewModelScope.launch {
            feedRepository.toggleLike(postId).onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "Like konnte nicht gespeichert werden: ${error.localizedMessage}")
                }
            }
        }
    }

    fun onCommentsOpened(postId: String) {
        _uiState.update { it.copy(activeCommentPostId = postId, commentInput = "", comments = emptyList()) }

        commentObserverJob?.cancel()
        commentObserverJob = viewModelScope.launch {
            feedRepository.getCommentsForPost(postId)
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = "Kommentare konnten nicht geladen werden: ${error.localizedMessage}")
                    }
                }
                .collect { comments ->
                    val items = comments.map { comment ->
                        CommentItemUi(
                            id = comment.id,
                            authorName = comment.authorName,
                            text = comment.text,
                            timeAgo = formatRelativeTime(comment.createdAtMs)
                        )
                    }
                    _uiState.update { it.copy(comments = items) }
                }
        }
    }

    fun onCommentsDismissed() {
        commentObserverJob?.cancel()
        commentObserverJob = null
        _uiState.update { it.copy(activeCommentPostId = null, comments = emptyList(), commentInput = "") }
    }

    fun onCommentInputChanged(text: String) {
        _uiState.update { it.copy(commentInput = text) }
    }

    fun onSubmitComment() {
        val postId = _uiState.value.activeCommentPostId ?: return
        val text = _uiState.value.commentInput.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            val userId = currentUserProvider.userId()
            val userName = currentUserProvider.displayName()

            feedRepository.addComment(
                postId = postId,
                authorUserId = userId,
                authorName = userName,
                text = text
            ).onSuccess {
                _uiState.update { it.copy(commentInput = "") }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "Kommentar konnte nicht gespeichert werden: ${error.localizedMessage}")
                }
            }
        }
    }

    // =====================================================================
    // Teilen von Beiträgen
    // =====================================================================

    fun onSharePostOpened(post: FeedItemUi) {
        _uiState.update {
            it.copy(
                activeSharePost = post,
                shareSearchQuery = ""
            )
        }
        viewModelScope.launch {
            chatRepository?.getActiveChats()?.collect { chats ->
                val contacts = chats.map { chat ->
                    ShareContactUi(
                        id = chat.id,
                        name = chat.name,
                        avatarInitial = chat.avatarInitial ?: chat.name.take(1).uppercase(),
                        lastMessage = chat.lastMessageText ?: ""
                    )
                }
                _uiState.update { it.copy(shareContacts = contacts) }
            }
        }
    }

    fun onShareSearchQueryChanged(query: String) {
        _uiState.update { it.copy(shareSearchQuery = query) }
    }

    fun onSharePostToChat(chatId: String) {
        val post = _uiState.value.activeSharePost ?: return
        viewModelScope.launch {
            val userId = currentUserProvider.userId()
            val userName = currentUserProvider.displayName()
            val shareText = "📰 ${post.userName} hat geteilt:\n\"${post.contentText}\""

            chatRepository?.sendTextMessage(
                chatId = chatId,
                senderUserId = userId,
                senderName = userName,
                text = shareText
            )?.onSuccess {
                _uiState.update { it.copy(activeSharePost = null, infoMessage = "Beitrag im Chat geteilt!") }
            }?.onFailure { error ->
                _uiState.update { it.copy(errorMessage = "Konnte nicht geteilt werden: ${error.localizedMessage}") }
            }
        }
    }

    fun onSharePostDismissed() {
        _uiState.update { it.copy(activeSharePost = null, shareSearchQuery = "") }
    }

    fun onUserSearchQueryChanged(query: String) {
        _uiState.update { it.copy(userSearchQuery = query, isUserSearchActive = query.isNotBlank()) }
        if (query.isBlank()) {
            _uiState.update { it.copy(userSearchResults = emptyList(), isSearchingUsers = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearchingUsers = true) }
            val currentUserId = currentUserProvider.userId()
            val results = userRepository?.searchUsers(query.trim()) ?: emptyList()
            val mapped = results
                .filter { it.id != currentUserId }
                .map { user ->
                    SearchedUserUi(
                        id = user.id,
                        username = user.username,
                        displayName = user.hometown?.takeIf { it.isNotBlank() } ?: user.username,
                        profilePictureUrl = user.profilePictureUrl,
                        hometown = user.hometown,
                        isVerified = user.isVerified
                    )
                }
            _uiState.update { it.copy(userSearchResults = mapped, isSearchingUsers = false) }
        }
    }

    fun onClearUserSearch() {
        _uiState.update {
            it.copy(
                userSearchQuery = "",
                userSearchResults = emptyList(),
                isSearchingUsers = false,
                isUserSearchActive = false
            )
        }
    }

    fun onToggleUserSearch() {
        _uiState.update { it.copy(isUserSearchActive = !it.isUserSearchActive) }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }
}
