package com.kliq.app.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kliq.app.ui.components.KliqFeedCard
import com.kliq.app.ui.components.KliqScreenScaffold
import com.kliq.app.ui.navigation.TopBarMenuAction
import com.kliq.app.ui.navigation.TopBarUiState
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.util.ensureMinTouchTarget
import com.kliq.app.util.talkBackDescription

/**
 * Home-Feed-Screen mit Story-Leiste, scrollbarem Feed und Beitrags-Editor.
 *
 * Alle Interaktionen laufen über das [HomeViewModel] und werden in der lokalen
 * Datenbank persistiert: Story-Aufrufe setzen den Gesehen-Status, Likes und
 * Kommentare werden gespeichert, neue Beiträge erscheinen an der Spitze des Feeds.
 *
 * @param topBarState Aktueller Top-Bar UI-State.
 * @param onToggleMenu Callback zum Umschalten des Overflow-Menüs.
 * @param onDismissMenu Callback zum Schließen des Overflow-Menüs.
 * @param onMenuAction Callback bei Auswahl eines Menü-Eintrags.
 * @param onNavigateToChat Navigation zur Chat-Übersicht.
 * @param viewModel Hilt-injiziertes [HomeViewModel].
 */
@Composable
fun HomeScreen(
    topBarState: TopBarUiState,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    onNavigateToActivities: () -> Unit = {},
    onNavigateToUserProfile: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val storyMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPostStory(context, it) }
    }

    val postImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onComposerImageSelected(context, it) }
    }

    LaunchedEffect(uiState.errorMessage, uiState.infoMessage) {
        val message = uiState.errorMessage ?: uiState.infoMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }

    KliqScreenScaffold(
        title = "Kliq",
        isMenuExpanded = topBarState.isMenuExpanded,
        onToggleMenu = onToggleMenu,
        onDismissMenu = onDismissMenu,
        onMenuAction = onMenuAction,
        actions = {
            IconButton(
                onClick = onNavigateToActivities,
                modifier = Modifier.talkBackDescription("Aktivitäten öffnen")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Aktivitäten",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreatePost() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.talkBackDescription("Neuen Beitrag erstellen")
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    StoryRow(
                        myStory = uiState.myStory,
                        myProfilePictureUrl = uiState.myProfilePictureUrl,
                        myUserName = uiState.myUserName,
                        isPostingStory = uiState.isPostingStory,
                        stories = uiState.storyItems,
                        onMyStoryClick = {
                            uiState.myStory?.let { viewModel.onStoryOpened(it.id) }
                                ?: storyMediaLauncher.launch("image/*")
                        },
                        onAddStoryClick = {
                            storyMediaLauncher.launch("image/*")
                        },
                        onStoryClick = { storyId -> viewModel.onStoryOpened(storyId) }
                    )
                }

                item {
                    UserSearchBarSection(
                        query = uiState.userSearchQuery,
                        onQueryChanged = { viewModel.onUserSearchQueryChanged(it) },
                        onClear = { viewModel.onClearUserSearch() },
                        isSearching = uiState.isSearchingUsers,
                        searchResults = uiState.userSearchResults,
                        onUserClick = { userId -> onNavigateToUserProfile(userId) }
                    )
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else if (uiState.feedItems.isEmpty()) {
                    item { EmptyFeedHint() }
                }

                items(uiState.feedItems, key = { it.id }) { feedItem ->
                    KliqFeedCard(
                        userName = feedItem.userName,
                        timeAgo = feedItem.timeAgo,
                        contentText = feedItem.contentText,
                        likeCount = feedItem.likeCount,
                        isLiked = feedItem.isLiked,
                        commentCount = feedItem.commentCount,
                        clubName = feedItem.clubName,
                        imageUrl = feedItem.imageUrl,
                        isOwnPost = feedItem.isOwnPost,
                        onLikeClick = { viewModel.onLikePost(feedItem.id) },
                        onCommentClick = { viewModel.onCommentsOpened(feedItem.id) },
                        onShareClick = { viewModel.onSharePostOpened(feedItem) },
                        onDeletePostClick = { viewModel.onDeletePost(feedItem.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (uiState.isComposerVisible) {
        PostComposerDialog(
            text = uiState.composerText,
            imageUri = uiState.composerImageUri,
            location = uiState.composerLocation,
            isEventPinned = uiState.composerIsEventPinned,
            isPublishing = uiState.isPublishing,
            onTextChange = viewModel::onComposerTextChanged,
            onPickImage = { postImageLauncher.launch("image/*") },
            onRemoveImage = viewModel::onComposerImageRemoved,
            onLocationChange = viewModel::onComposerLocationChanged,
            onToggleEventPinned = viewModel::onToggleComposerEventPinned,
            onPublish = viewModel::onPublishPost,
            onDismiss = viewModel::onComposerDismissed
        )
    }

    uiState.activeStory?.let { story ->
        StoryViewerDialog(
            story = story,
            onDeleteClick = { viewModel.onDeleteStory(story.id) },
            onAuthorClick = {
                viewModel.onStoryDismissed()
                onNavigateToUserProfile(story.authorUserId)
            },
            onDismiss = viewModel::onStoryDismissed
        )
    }

    if (uiState.activeCommentPostId != null) {
        CommentSheet(
            comments = uiState.comments,
            commentInput = uiState.commentInput,
            onCommentInputChange = viewModel::onCommentInputChanged,
            onSubmitComment = viewModel::onSubmitComment,
            onDismiss = viewModel::onCommentsDismissed
        )
    }

    uiState.activeSharePost?.let { sharePost ->
        SharePostSheet(
            post = sharePost,
            searchQuery = uiState.shareSearchQuery,
            contacts = uiState.shareContacts,
            onSearchQueryChanged = viewModel::onShareSearchQueryChanged,
            onSendToContact = viewModel::onSharePostToChat,
            onDismiss = viewModel::onSharePostDismissed
        )
    }
}

/**
 * Horizontale Story-Leiste. Erstes Element ist die eigene Story (Instagram-Style).
 */
@Composable
private fun StoryRow(
    myStory: StoryItemUi?,
    myProfilePictureUrl: String?,
    myUserName: String,
    isPostingStory: Boolean,
    stories: List<StoryItemUi>,
    onMyStoryClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onStoryClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Deine Story (Instagram-Style)
        item(key = "my_own_story_item") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (myStory != null) onMyStoryClick() else onAddStoryClick()
                    }
                    .ensureMinTouchTarget(48.dp)
            ) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Profilbild-Kreis
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .then(
                                if (myStory != null) {
                                    Modifier.border(
                                        width = 2.5.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                PurplePrimaryLight,
                                                FuchsiaTertiary,
                                                PurplePrimary
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                } else {
                                    Modifier.border(
                                        width = 1.2.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                        shape = CircleShape
                                    )
                                }
                            )
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!myProfilePictureUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = myProfilePictureUrl,
                                contentDescription = "Deine Story",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = myUserName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Plus-Badge (+)
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary)
                            .clickable { onAddStoryClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPostingStory) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color.White,
                                strokeWidth = 1.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Story hinzufügen",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Deine Story",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (myStory != null) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 2. Storys von anderen Nutzern
        items(stories, key = { it.id }) { story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onStoryClick(story.id) }
                    .ensureMinTouchTarget(48.dp)
                    .talkBackDescription(
                        if (story.hasUnseenStory) "Neue Story von ${story.userName}"
                        else "Story von ${story.userName}, bereits gesehen"
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .then(
                            if (story.hasUnseenStory) {
                                Modifier.border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            PurplePrimaryLight,
                                            FuchsiaTertiary,
                                            PurplePrimary
                                        )
                                    ),
                                    shape = CircleShape
                                )
                            } else {
                                Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                            }
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!story.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = story.imageUrl,
                            contentDescription = story.userName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = story.userName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.userName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Hinweis, wenn noch keine Beiträge vorliegen.
 */
@Composable
private fun EmptyFeedHint() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Noch keine Beiträge",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Teile über das Plus-Symbol, was heute in Klagenfurt läuft.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Dialog zum Erstellen eines neuen Beitrags mit Bild, Ort und Event-Pin.
 */
@Composable
private fun PostComposerDialog(
    text: String,
    imageUri: String?,
    location: String,
    isEventPinned: Boolean,
    isPublishing: Boolean,
    onTextChange: (String) -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onLocationChange: (String) -> Unit,
    onToggleEventPinned: () -> Unit,
    onPublish: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Neuer Beitrag",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = { Text(text = "Was läuft heute Abend?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.dp),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    enabled = !isPublishing
                )

                // Bild-Vorschau oder Hinzufügen-Button
                if (!imageUri.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Ausgewähltes Bild",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = onRemoveImage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f))
                                .size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Bild entfernen",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onPickImage,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddPhotoAlternate,
                            contentDescription = null,
                            tint = PurplePrimaryLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Foto hinzufügen",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Standort-Eingabe (optional)
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    placeholder = { Text("Ort / Location (optional)") },
                    leadingIcon = {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = PurplePrimaryLight)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isPublishing
                )

                // Auf Karte fixieren (Event) Button / Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isEventPinned) PurplePrimary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isEventPinned) PurplePrimary else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onToggleEventPinned() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                            tint = if (isEventPinned) PurplePrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Auf Karte fixieren (Event)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isEventPinned) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEventPinned) "Wird auf der Karte markiert" else "Nur im Feed sichtbar",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isEventPinned) PurplePrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isEventPinned,
                        onCheckedChange = { onToggleEventPinned() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PurplePrimary
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onPublish,
                enabled = !isPublishing && (text.isNotBlank() || !imageUri.isNullOrBlank()),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Veröffentlichen", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isPublishing) {
                Text(text = "Abbrechen")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

/**
 * Vollbild-Anzeige einer Story mit Erstellungszeitpunkt, Standort und Löschen-Option.
 */
@Composable
private fun StoryViewerDialog(
    story: StoryItemUi,
    onDeleteClick: () -> Unit,
    onAuthorClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(560.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PurplePrimary,
                            FuchsiaTertiary.copy(alpha = 0.75f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            if (!story.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = story.imageUrl,
                    contentDescription = story.headline,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Top gradient scrim for readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                            )
                        )
                )
                // Bottom gradient scrim for readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )
            }

            // Top Action Row: Author Info & Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Klick auf Ersteller-Info öffnet dessen Profil
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onAuthorClick)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = story.userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profil ansehen",
                            tint = PurplePrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Erstellungszeitpunkt (Uhrzeit) & Standort
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (story.createdAtFormatted.isNotBlank()) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = story.createdAtFormatted,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        if (!story.clubName.isNullOrBlank()) {
                            if (story.createdAtFormatted.isNotBlank()) {
                                Text(
                                    text = " • ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = PurplePrimaryLight,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = story.clubName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Story löschen Knopf - NUR für die eigene Story sichtbar!
                    if (story.isOwnStory) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Story löschen",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Schließen Knopf
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Schließen",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom Sheet mit den Kommentaren eines Beitrags und Eingabefeld.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentSheet(
    comments: List<CommentItemUi>,
    commentInput: String,
    onCommentInputChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = if (comments.isEmpty()) "Kommentare" else "Kommentare (${comments.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (comments.isEmpty()) {
                Text(
                    text = "Noch keine Kommentare. Schreib den ersten.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = comment.authorName,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = comment.timeAgo,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = comment.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = commentInput,
                onValueChange = onCommentInputChange,
                placeholder = { Text(text = "Kommentar schreiben…") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = onSubmitComment,
                        enabled = commentInput.isNotBlank(),
                        modifier = Modifier.talkBackDescription("Kommentar senden")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = null,
                            tint = if (commentInput.isNotBlank()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            )
        }
    }
}

/**
 * Prominente Suchleiste und Live-Ergebnisliste zur Nutzersuche im Home-Screen.
 */
@Composable
fun UserSearchBarSection(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    isSearching: Boolean,
    searchResults: List<SearchedUserUi>,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Suchleiste (groß, gut lesbar und stylish)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            border = BorderStroke(
                width = 1.dp,
                color = if (query.isNotBlank()) PurplePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Suche",
                    tint = if (query.isNotBlank()) PurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Leute & Freunde suchen...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(PurplePrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (isSearching) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                        color = PurplePrimary
                    )
                } else if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Suche leeren",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Live-Suchergebnisse
        if (query.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            if (searchResults.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = 1.dp,
                        color = PurplePrimary.copy(alpha = 0.35f)
                    ),
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Gefundene Profile (${searchResults.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PurplePrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        searchResults.forEach { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onUserClick(user.id) }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!user.profilePictureUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = user.profilePictureUrl,
                                        contentDescription = user.username,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, PurplePrimary, CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(PurplePrimary, FuchsiaTertiary)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.username.take(2).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "@${user.username}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (user.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Filled.Verified,
                                                contentDescription = "Verifiziert",
                                                tint = PurplePrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    if (!user.hometown.isNullOrBlank()) {
                                        Text(
                                            text = user.hometown,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                TextButton(
                                    onClick = { onUserClick(user.id) },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = PurplePrimary
                                    )
                                ) {
                                    Text("Profil", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            } else if (!isSearching) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kein Profil für \"$query\" gefunden",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
