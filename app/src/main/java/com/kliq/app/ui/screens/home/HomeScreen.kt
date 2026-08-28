package com.kliq.app.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
    onNavigateToChat: () -> Unit = {},
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
                onClick = onNavigateToChat,
                modifier = Modifier.talkBackDescription("Nachrichten öffnen")
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = null,
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
                        onLikeClick = { viewModel.onLikePost(feedItem.id) },
                        onCommentClick = { viewModel.onCommentsOpened(feedItem.id) },
                        onShareClick = { viewModel.onCommentsOpened(feedItem.id) },
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
            isPublishing = uiState.isPublishing,
            onTextChange = viewModel::onComposerTextChanged,
            onPublish = viewModel::onPublishPost,
            onDismiss = viewModel::onComposerDismissed
        )
    }

    uiState.activeStory?.let { story ->
        StoryViewerDialog(
            story = story,
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
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                if (myStory != null) onMyStoryClick() else onAddStoryClick()
                            },
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
 * Dialog zum Erstellen eines neuen Beitrags.
 */
@Composable
private fun PostComposerDialog(
    text: String,
    isPublishing: Boolean,
    onTextChange: (String) -> Unit,
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
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text(text = "Was läuft heute Abend?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(12.dp),
                minLines = 4,
                enabled = !isPublishing
            )
        },
        confirmButton = {
            Button(
                onClick = onPublish,
                enabled = !isPublishing && text.isNotBlank(),
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
 * Vollbild-Anzeige einer Story mit Kliq-Farbverlauf als Motivfläche.
 */
@Composable
private fun StoryViewerDialog(
    story: StoryItemUi,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(540.dp)
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
                        .height(120.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
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

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .talkBackDescription("Story schließen")
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = story.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (!story.clubName.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = story.clubName,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            if (story.headline.isNotBlank() && story.headline != "Neue Story") {
                Text(
                    text = story.headline,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                )
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
