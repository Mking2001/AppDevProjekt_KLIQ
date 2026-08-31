package com.kliq.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.model.FeedPost
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.ui.components.InteractiveStarRating
import com.kliq.app.ui.components.ProfileAvatarImage
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkOnBackground
import com.kliq.app.ui.theme.DarkOnSurface
import com.kliq.app.ui.theme.DarkOnSurfaceVariant
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.ErrorRed
import com.kliq.app.ui.theme.PurpleContainer
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.ui.theme.TealSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileScreen(
    userId: String = "",
    viewModel: OtherUserProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToOtherProfile: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showOverflowMenu by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotBlank() && userId != viewModel.targetUserId) {
            viewModel.loadUserProfile(userId)
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.actionSuccessMessage) {
        val errorMsg = uiState.errorMessage
        val successMsg = uiState.actionSuccessMessage
        if (errorMsg != null) {
            snackbarHostState.showSnackbar(errorMsg)
            viewModel.dismissMessage()
        } else if (successMsg != null) {
            snackbarHostState.showSnackbar(successMsg)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.username.isNotBlank()) uiState.username else "Profil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = DarkOnBackground,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = DarkOnBackground
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Optionen",
                                tint = DarkOnBackground
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Report,
                                            contentDescription = null,
                                            tint = ErrorRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Profil melden", color = DarkOnSurface)
                                    }
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.openReportDialog()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Block,
                                            contentDescription = null,
                                            tint = ErrorRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            if (uiState.isBlocked) "Entblocken" else "Nutzer blockieren",
                                            color = DarkOnSurface
                                        )
                                    }
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.toggleBlockUser()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = DarkOnBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PurplePrimary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.isBlocked) {
                        item {
                            BlockedBannerCard(onUnblock = { viewModel.toggleBlockUser() })
                        }
                    }

                    item {
                        OtherUserProfileHeaderSection(
                            uiState = uiState,
                            onAvatarClick = { viewModel.openProfileStoryViewer(0) },
                            onSendMessageClick = { onNavigateToChat(uiState.userId) },
                            onRateUserClick = { viewModel.openRatingSheet() },
                            onFollowToggleClick = { viewModel.toggleFollow() }
                        )
                    }

                    item {
                        InstagramStatsBar(
                            postCount = uiState.postCount,
                            followerCount = uiState.followerCount,
                            followingCount = uiState.followingCount,
                            onPostsClick = { viewModel.selectTab(ProfileTab.POSTS) },
                            onFollowersClick = { viewModel.openFollowList(FollowListType.FOLLOWERS) },
                            onFollowingClick = { viewModel.openFollowList(FollowListType.FOLLOWING) }
                        )
                    }

                    item {
                        BiografieAndBadgesSection(uiState = uiState)
                    }

                    item {
                        ProfileTabSection(
                            selectedTab = uiState.selectedTab,
                            postCount = uiState.postCount,
                            reviewCount = uiState.reviewCount,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }

                    when (uiState.selectedTab) {
                        ProfileTab.POSTS -> {
                            if (uiState.posts.isEmpty()) {
                                item {
                                    EmptyPostsPlaceholder(username = uiState.username)
                                }
                            } else {

                                val postChunks = uiState.posts.chunked(3)
                                items(postChunks) { rowPosts ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        for (post in rowPosts) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                PostGridItem(
                                                    post = post,
                                                    onClick = { viewModel.openPostDetail(post) }
                                                )
                                            }
                                        }

                                        for (i in 0 until (3 - rowPosts.size)) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                        ProfileTab.REVIEWS -> {
                            if (uiState.reviews.isEmpty()) {
                                item {
                                    EmptyReviewsPlaceholder(username = uiState.username)
                                }
                            } else {
                                items(uiState.reviews, key = { it.id }) { review ->
                                    ReviewItemCard(review = review)
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.isRatingSheetVisible) {
                UserRatingBottomSheet(
                    targetUsername = uiState.username,
                    isSubmitting = uiState.isSubmittingRating,
                    onDismiss = { viewModel.closeRatingSheet() },
                    onSubmitRating = { rating, comment ->
                        viewModel.submitRating(rating, comment)
                    }
                )
            }

            if (uiState.isReportDialogVisible) {
                ReportUserModalDialog(
                    targetUsername = uiState.username,
                    onDismiss = { viewModel.closeReportDialog() },
                    onConfirmReport = { reason ->
                        viewModel.reportUser(reason)
                    }
                )
            }

            if (uiState.activeFollowListDialog != null) {
                FollowListBottomSheet(
                    type = uiState.activeFollowListDialog!!,
                    users = if (uiState.activeFollowListDialog == FollowListType.FOLLOWERS) uiState.followersList else uiState.followingList,
                    onDismiss = { viewModel.closeFollowList() },
                    onUserClick = { clickedUserId ->
                        viewModel.closeFollowList()
                        onNavigateToOtherProfile(clickedUserId)
                    }
                )
            }

            if (uiState.selectedPostForDetail != null) {
                PostDetailDialog(
                    post = uiState.selectedPostForDetail!!,
                    onDismiss = { viewModel.closePostDetail() },
                    onLikeToggle = { viewModel.toggleLikePost(uiState.selectedPostForDetail!!.id) }
                )
            }

            MultiPhotoStoryViewerDialog(
                isVisible = uiState.isPhotoViewerVisible,
                photos = uiState.photos.ifEmpty { listOfNotNull(uiState.profilePictureUrl) },
                initials = uiState.username,
                displayName = uiState.username,
                age = uiState.age ?: 20,
                currentIndex = uiState.activePhotoViewerIndex,
                onDismiss = { viewModel.dismissProfileStoryViewer() },
                onPrevious = { viewModel.onPreviousPhoto() },
                onNext = { viewModel.onNextPhoto() }
            )
        }
    }
}

@Composable
private fun InstagramStatsBar(
    postCount: Int,
    followerCount: Int,
    followingCount: Int,
    onPostsClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(count = postCount, label = "Beiträge", onClick = onPostsClick)
            Box(modifier = Modifier.height(30.dp).width(1.dp).background(DarkOutline))
            StatItem(count = followerCount, label = "Follower", onClick = onFollowersClick)
            Box(modifier = Modifier.height(30.dp).width(1.dp).background(DarkOutline))
            StatItem(count = followingCount, label = "Gefolgt", onClick = onFollowingClick)
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge.copy(
                color = DarkOnBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DarkOnSurfaceVariant,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun ProfileTabSection(
    selectedTab: ProfileTab,
    postCount: Int,
    reviewCount: Int,
    onTabSelected: (ProfileTab) -> Unit
) {
    TabRow(
        selectedTabIndex = if (selectedTab == ProfileTab.POSTS) 0 else 1,
        containerColor = Color.Transparent,
        contentColor = PurplePrimaryLight,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    tabPositions[if (selectedTab == ProfileTab.POSTS) 0 else 1]
                ),
                color = PurplePrimaryLight,
                height = 3.dp
            )
        },
        divider = {
            HorizontalDivider(color = DarkOutline)
        }
    ) {
        Tab(
            selected = selectedTab == ProfileTab.POSTS,
            onClick = { onTabSelected(ProfileTab.POSTS) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (selectedTab == ProfileTab.POSTS) PurplePrimaryLight else DarkOnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Beiträge ($postCount)",
                        fontWeight = if (selectedTab == ProfileTab.POSTS) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == ProfileTab.POSTS) DarkOnBackground else DarkOnSurfaceVariant
                    )
                }
            }
        )
        Tab(
            selected = selectedTab == ProfileTab.REVIEWS,
            onClick = { onTabSelected(ProfileTab.REVIEWS) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (selectedTab == ProfileTab.REVIEWS) PurplePrimaryLight else DarkOnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Bewertungen ($reviewCount)",
                        fontWeight = if (selectedTab == ProfileTab.REVIEWS) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == ProfileTab.REVIEWS) DarkOnBackground else DarkOnSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
private fun PostGridItem(
    post: FeedPost,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkOutline, RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        if (!post.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = "Beitrag von ${post.authorName}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(DarkSurfaceVariant, DarkSurface)
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.contentText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DarkOnSurface,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    ),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (post.likeCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${post.likeCount}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPostsPlaceholder(username: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = null,
                tint = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Noch keine Beiträge geteilt",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DarkOnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$username hat bisher noch keine Beiträge oder Fotos im Feed veröffentlicht.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DarkOnSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun EmptyReviewsPlaceholder(username: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StarRatingBar(rating = 0.0, starSize = 24.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Noch keine Bewertungen vorhanden",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DarkOnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sobald ihr am selben Abend am gleichen Ort oder Event wart, kannst du die erste Bewertung abgeben!",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DarkOnSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun PostDetailDialog(
    post: FeedPost,
    onDismiss: () -> Unit,
    onLikeToggle: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PurpleContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = post.authorName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = PurplePrimaryLight,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = DarkOnBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (!post.clubName.isNullOrBlank()) {
                                Text(
                                    text = "@ ${post.clubName}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TealSecondary
                                    )
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen",
                            tint = DarkOnSurfaceVariant
                        )
                    }
                }

                if (!post.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = post.imageUrl,
                        contentDescription = "Beitragsbild",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = post.contentText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DarkOnSurface,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onLikeToggle() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (post.isLikedByMe) ErrorRed else DarkOnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${post.likeCount} Gefällt mir",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (post.isLikedByMe) ErrorRed else DarkOnSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Text(
                            text = "${post.commentCount} Kommentare",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DarkOnSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FollowListBottomSheet(
    type: FollowListType,
    users: List<UserEntity>,
    onDismiss: () -> Unit,
    onUserClick: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val title = if (type == FollowListType.FOLLOWERS) "Follower" else "Gefolgt"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "$title (${users.size})",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider(color = DarkOutline)

            if (users.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keine Personen in dieser Liste vorhanden.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DarkOnSurfaceVariant
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(users, key = { it.id }) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onUserClick(user.id) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(PurpleContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!user.profilePictureUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = user.profilePictureUrl,
                                            contentDescription = user.username,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = user.username.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = PurplePrimaryLight,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = user.username,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = DarkOnBackground,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    if (!user.hometown.isNullOrBlank()) {
                                        Text(
                                            text = user.hometown,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DarkOnSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = { onUserClick(user.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PurpleContainer,
                                    contentColor = PurplePrimaryLight
                                ),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Profil", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OtherUserProfileHeaderSection(
    uiState: OtherUserProfileUiState,
    onAvatarClick: () -> Unit,
    onSendMessageClick: () -> Unit,
    onRateUserClick: () -> Unit,
    onFollowToggleClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clickable { onAvatarClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(PurplePrimary, TealSecondary)
                                ),
                                shape = CircleShape
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ProfileAvatarImage(
                            imageUri = uiState.profilePictureUrl,
                            onAvatarClick = onAvatarClick,
                            initials = uiState.username,
                            showCameraBadge = false,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    val intentIcon = when (uiState.searchIntent) {
                        SearchIntent.DATING -> Icons.Default.Favorite
                        SearchIntent.FRIENDS -> Icons.Default.Group
                        SearchIntent.BOTH -> Icons.Default.Favorite
                    }
                    val badgeColor = when (uiState.searchIntent) {
                        SearchIntent.DATING -> PurplePrimaryLight
                        SearchIntent.FRIENDS -> TealSecondary
                        SearchIntent.BOTH -> PurplePrimaryLight
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .background(DarkSurface, CircleShape)
                            .padding(2.dp)
                            .background(badgeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = intentIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val displayNameAge = if (uiState.age != null) {
                    "${uiState.username} ${uiState.age}"
                } else {
                    uiState.username
                }
                Text(
                    text = displayNameAge,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                StarRatingBar(rating = uiState.averageRating, starSize = 18.dp)

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (uiState.reviewCount == 0) "0 Bewertungen" else String.format("%.1f (%d)", uiState.averageRating, uiState.reviewCount),
                    style = MaterialTheme.typography.labelSmall.copy(color = DarkOnSurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End
            ) {

                Button(
                    onClick = onSendMessageClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nachricht", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (uiState.alreadyReviewed) {
                    OutlinedButton(
                        onClick = onRateUserClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = DarkOnSurfaceVariant
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = TealSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bewertet ✓", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else if (!uiState.canReview) {
                    Button(
                        onClick = onRateUserClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = DarkOnSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Bewerten", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Gesperrt",
                            modifier = Modifier.size(15.dp),
                            tint = DarkOnSurfaceVariant
                        )
                    }
                } else {
                    Button(
                        onClick = onRateUserClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleContainer,
                            contentColor = PurplePrimaryLight
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bewerten", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (uiState.isFriend) {
                    OutlinedButton(
                        onClick = onFollowToggleClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PurplePrimaryLight
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Entfolgen", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = onFollowToggleClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleContainer,
                            contentColor = PurplePrimaryLight
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Folgen", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BiografieAndBadgesSection(uiState: OtherUserProfileUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Biografie:",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (!uiState.bio.isNullOrBlank()) uiState.bio else "Keine Biografie angegeben.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DarkOnSurface,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HabitPill(text = uiState.drinkingHabit.title)
                HabitPill(text = uiState.smokingHabit.title)
            }
        }
    }
}

@Composable
private fun HabitPill(text: String) {
    Surface(
        color = Color.Black,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun StarRatingBar(rating: Double, starSize: androidx.compose.ui.unit.Dp = 18.dp) {
    Row {
        for (i in 1..5) {
            val starIcon = when {
                rating >= i -> Icons.Default.Star
                rating >= i - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Default.StarOutline
            }
            Icon(
                imageVector = starIcon,
                contentDescription = null,
                tint = if (rating > 0 && rating >= i - 0.7) Color(0xFFFFC107) else DarkOnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Composable
private fun ReviewItemCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(PurpleContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = review.reviewerUsername.take(1).uppercase().ifBlank { "U" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = PurplePrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.reviewerUsername.ifBlank { "Kliq-User" },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.Bold
                    )
                )

                if (review.text.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = review.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DarkOnSurface,
                            lineHeight = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                StarRatingBar(rating = review.rating.toDouble(), starSize = 16.dp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserRatingBottomSheet(
    targetUsername: String,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmitRating: (Int, String) -> Unit
) {
    var selectedRating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    val maxTextLength = 300
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$targetUsername bewerten",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Wie war euer gemeinsames Erlebnis? Schreibe ein ehrliches Feedback.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DarkOnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            InteractiveStarRating(
                rating = selectedRating,
                onRatingChanged = { selectedRating = it },
                starSize = 40.dp,
                starSpacing = 12.dp,
                isReadOnly = isSubmitting
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = {
                    if (it.length <= maxTextLength) {
                        reviewText = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Pflichtfeld: Beschreibe euer Treffen...", color = DarkOnSurfaceVariant) },
                supportingText = {
                    Text(
                        text = "${reviewText.length} / $maxTextLength Zeichen",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = DarkOnSurfaceVariant
                    )
                },
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = DarkOutline,
                    focusedTextColor = DarkOnBackground,
                    unfocusedTextColor = DarkOnSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSubmitRating(selectedRating, reviewText) },
                enabled = !isSubmitting && selectedRating in 1..5 && reviewText.trim().isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary,
                    disabledContainerColor = PurpleContainer.copy(alpha = 0.4f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (reviewText.trim().isBlank()) "Bitte Text schreiben" else "Bewertung absenden",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BlockedBannerCard(onUnblock: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Du hast diesen Nutzer blockiert",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            TextButton(onClick = onUnblock) {
                Text("Entblocken", color = PurplePrimaryLight)
            }
        }
    }
}

@Composable
private fun ReportUserModalDialog(
    targetUsername: String,
    onDismiss: () -> Unit,
    onConfirmReport: (String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("Unangemessenes Verhalten") }
    val reportReasons = listOf(
        "Unangemessenes Verhalten",
        "Fake-Profil / Identitätsdiebstahl",
        "Spam oder Werbung",
        "Belästigung / Unbehagen"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "$targetUsername melden",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Bitte wähle den Grund für die Meldung:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkOnSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(12.dp))
                reportReasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(18.dp),
                            shape = CircleShape,
                            color = if (selectedReason == reason) PurplePrimary else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(2.dp, PurplePrimary)
                        ) {}
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (selectedReason == reason) DarkOnBackground else DarkOnSurface,
                                fontWeight = if (selectedReason == reason) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmReport(selectedReason) },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text("Meldung absenden", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = DarkOnSurfaceVariant)
            }
        }
    )
}
