package com.kliq.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.formatRelativeTime
import com.kliq.app.data.repository.FeedRepository
import com.kliq.app.domain.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Immutable UI State für den Home-Screen.
 *
 * @param isLoading Ob der Feed initial geladen wird.
 * @param isRefreshing Ob ein manuelles Neuladen aktiv ist.
 * @param feedItems Beiträge des Feeds in Darstellungsform.
 * @param storyItems Storys der horizontalen Story-Leiste.
 * @param activeStory Aktuell im Vollbild geöffnete Story, null wenn geschlossen.
 * @param isComposerVisible Ob der Editor für neue Beiträge geöffnet ist.
 * @param composerText Aktueller Text im Beitrags-Editor.
 * @param isPublishing Ob ein Beitrag gerade gespeichert wird.
 * @param activeCommentPostId ID des Beitrags, dessen Kommentare angezeigt werden.
 * @param comments Kommentare des aktiven Beitrags.
 * @param commentInput Aktueller Text im Kommentarfeld.
 * @param errorMessage Fehlermeldung für die Snackbar.
 * @param infoMessage Bestätigungsmeldung für die Snackbar.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val feedItems: List<FeedItemUi> = emptyList(),
    val storyItems: List<StoryItemUi> = emptyList(),
    val activeStory: StoryItemUi? = null,
    val isComposerVisible: Boolean = false,
    val composerText: String = "",
    val isPublishing: Boolean = false,
    val activeCommentPostId: String? = null,
    val comments: List<CommentItemUi> = emptyList(),
    val commentInput: String = "",
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

/**
 * Darstellungsmodell eines Feed-Beitrags.
 */
data class FeedItemUi(
    val id: String,
    val userName: String,
    val timeAgo: String,
    val contentText: String,
    val clubName: String? = null,
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val commentCount: Int = 0
)

/**
 * Darstellungsmodell einer Story-Kachel.
 */
data class StoryItemUi(
    val id: String,
    val userName: String,
    val headline: String = "",
    val clubName: String? = null,
    val imageUrl: String? = null,
    val hasUnseenStory: Boolean = true
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
 * ViewModel für den Home-Feed.
 *
 * Bezieht Beiträge und Storys reaktiv aus dem [FeedRepository] und schreibt
 * Likes, neue Beiträge, Kommentare und den Gesehen-Status von Storys zurück
 * in die lokale Datenbank. Der State überlebt damit einen Screen-Wechsel.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var commentObserverJob: Job? = null

    init {
        observeFeed()
        observeStories()
    }

    private fun observeFeed() {
        viewModelScope.launch {
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
                            userName = post.authorName,
                            timeAgo = formatRelativeTime(post.createdAtMs),
                            contentText = post.contentText,
                            clubName = post.clubName,
                            imageUrl = post.imageUrl,
                            likeCount = post.likeCount,
                            isLiked = post.isLikedByMe,
                            commentCount = post.commentCount
                        )
                    }
                    _uiState.update { it.copy(feedItems = items, isLoading = false) }
                }
        }
    }

    private fun observeStories() {
        viewModelScope.launch {
            feedRepository.getStories()
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = "Storys konnten nicht geladen werden: ${error.localizedMessage}")
                    }
                }
                .collect { stories ->
                    val items = stories.map { story ->
                        StoryItemUi(
                            id = story.id,
                            userName = story.authorName,
                            headline = story.headline,
                            clubName = story.clubName,
                            imageUrl = story.imageUrl,
                            hasUnseenStory = !story.isSeen
                        )
                    }
                    _uiState.update { state ->
                        state.copy(
                            storyItems = items,
                            activeStory = state.activeStory?.let { active ->
                                items.find { it.id == active.id } ?: active
                            }
                        )
                    }
                }
        }
    }

    /**
     * Lädt den Feed neu. Da Room die Daten reaktiv liefert, dient der Aufruf
     * primär der Rückmeldung an den Nutzer bei Pull-to-Refresh.
     */
    fun refreshFeed() {
        _uiState.update { it.copy(isRefreshing = true) }
        _uiState.update { it.copy(isRefreshing = false) }
    }

    // =====================================================================
    // Storys
    // =====================================================================

    /**
     * Öffnet eine Story im Vollbild und markiert sie dauerhaft als gesehen.
     */
    fun onStoryOpened(storyId: String) {
        val story = _uiState.value.storyItems.find { it.id == storyId } ?: return
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

    /** Schließt die Vollbild-Story. */
    fun onStoryDismissed() {
        _uiState.update { it.copy(activeStory = null) }
    }

    // =====================================================================
    // Beitrags-Editor
    // =====================================================================

    /** Öffnet den Editor für einen neuen Beitrag. */
    fun onCreatePost() {
        _uiState.update { it.copy(isComposerVisible = true, composerText = "") }
    }

    /** Schließt den Editor und verwirft den Entwurf. */
    fun onComposerDismissed() {
        _uiState.update { it.copy(isComposerVisible = false, composerText = "") }
    }

    /** Übernimmt Texteingaben aus dem Editor. */
    fun onComposerTextChanged(text: String) {
        _uiState.update { it.copy(composerText = text) }
    }

    /**
     * Veröffentlicht den Entwurf. Der Beitrag wird persistiert und erscheint
     * über den reaktiven Room-Flow unmittelbar an der Spitze des Feeds.
     */
    fun onPublishPost() {
        val text = _uiState.value.composerText.trim()
        if (text.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Bitte gib einen Text für den Beitrag ein.") }
            return
        }

        _uiState.update { it.copy(isPublishing = true) }

        viewModelScope.launch {
            val userId = currentUserProvider.userId()
            val userName = currentUserProvider.displayName()

            feedRepository.createPost(
                authorUserId = userId,
                authorName = userName,
                contentText = text
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isPublishing = false,
                        isComposerVisible = false,
                        composerText = "",
                        infoMessage = "Beitrag veröffentlicht."
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

    /**
     * Schaltet den Like-Zustand eines Beitrags um.
     *
     * @param postId ID des betroffenen Beitrags.
     */
    fun onLikePost(postId: String) {
        viewModelScope.launch {
            feedRepository.toggleLike(postId).onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "Like konnte nicht gespeichert werden: ${error.localizedMessage}")
                }
            }
        }
    }

    /** Öffnet das Kommentar-Sheet für einen Beitrag. */
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

    /** Schließt das Kommentar-Sheet. */
    fun onCommentsDismissed() {
        commentObserverJob?.cancel()
        commentObserverJob = null
        _uiState.update { it.copy(activeCommentPostId = null, comments = emptyList(), commentInput = "") }
    }

    /** Übernimmt Texteingaben aus dem Kommentarfeld. */
    fun onCommentInputChanged(text: String) {
        _uiState.update { it.copy(commentInput = text) }
    }

    /** Speichert den eingegebenen Kommentar am aktiven Beitrag. */
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

    /** Setzt Fehler- und Bestätigungsmeldungen zurück. */
    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }
}
