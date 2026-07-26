package com.kliq.app.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.Review
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
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
    onNavigateToChat: (String) -> Unit = {}
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
                            imageVector = Icons.Default.ArrowBack,
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
                            onRateUserClick = { viewModel.openRatingSheet() },
                            onSendMessageClick = { onNavigateToChat(uiState.userId) },
                            onReportUserClick = { viewModel.openReportDialog() },
                            onBlockToggleClick = { viewModel.toggleBlockUser() }
                        )
                    }

                    item {
                        SearchIntentBadgeSection(intent = uiState.searchIntent)
                    }

                    item {
                        LifestyleIndicatorsSection(
                            smokingHabit = uiState.smokingHabit,
                            drinkingHabit = uiState.drinkingHabit
                        )
                    }

                    item {
                        ReputationHeaderSection(
                            averageRating = uiState.averageRating,
                            reviewCount = uiState.reviewCount,
                            onRateUserClick = { viewModel.openRatingSheet() }
                        )
                    }

                    if (uiState.reviews.isNotEmpty()) {
                        item {
                            Text(
                                text = "Bewertungen (${uiState.reviews.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = DarkOnBackground,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }

                        items(uiState.reviews, key = { it.id }) { review ->
                            ReviewItemCard(review = review)
                        }
                    }
                }
            }

            if (uiState.isRatingSheetVisible) {
                UserRatingBottomSheet(
                    isSubmitting = uiState.isSubmittingRating,
                    onDismiss = { viewModel.closeRatingSheet() },
                    onSubmitRating = { rating, comment ->
                        viewModel.submitRating(rating, comment)
                    }
                )
            }

            if (uiState.isReportDialogVisible) {
                ReportUserModalDialog(
                    onDismiss = { viewModel.closeReportDialog() },
                    onConfirmReport = { reason ->
                        viewModel.reportUser(reason)
                    }
                )
            }
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
private fun OtherUserProfileHeaderSection(
    uiState: OtherUserProfileUiState,
    onRateUserClick: () -> Unit,
    onSendMessageClick: () -> Unit,
    onReportUserClick: () -> Unit,
    onBlockToggleClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(108.dp)
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
                    onAvatarClick = { },
                    initials = uiState.username,
                    showCameraBadge = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = uiState.username,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (uiState.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verifiziertes Profil",
                        tint = PurplePrimaryLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (uiState.age != null) {
                    Text(
                        text = "${uiState.age} Jahre",
                        style = MaterialTheme.typography.bodyMedium.copy(color = DarkOnSurfaceVariant)
                    )
                }
                if (uiState.age != null && !uiState.hometown.isNull_or_blank()) {
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = DarkOnSurfaceVariant)
                    )
                }
                if (!uiState.hometown.isNull_or_blank()) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TealSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = uiState.hometown!!,
                        style = MaterialTheme.typography.bodyMedium.copy(color = DarkOnSurfaceVariant)
                    )
                }
            }

            if (!uiState.bio.isNull_or_blank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBackground,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = uiState.bio!!,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DarkOnSurface,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSendMessageClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nachricht", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onRateUserClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PurplePrimaryLight
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bewerten", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SearchIntentBadgeSection(intent: SearchIntent) {
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
                text = "Suchabsicht",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = DarkOnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            val (icon, title, color) = when (intent) {
                SearchIntent.FRIENDS -> Triple(Icons.Default.Group, "Freunde finden", TealSecondary)
                SearchIntent.DATING -> Triple(Icons.Default.Favorite, "Dating / Liebe", PurplePrimaryLight)
                SearchIntent.BOTH -> Triple(Icons.Default.Group, "Freunde & Dating (Beides)", PurplePrimaryLight)
            }

            Surface(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DarkOnBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LifestyleIndicatorsSection(
    smokingHabit: SmokingHabit,
    drinkingHabit: DrinkingHabit
) {
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
                text = "Lifestyle & Konsumgewohnheiten",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = DarkOnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LifestyleBadge(
                    icon = Icons.Default.SmokingRooms,
                    label = "Rauchen",
                    value = smokingHabit.title,
                    modifier = Modifier.weight(1f)
                )

                LifestyleBadge(
                    icon = Icons.Default.LocalBar,
                    label = "Alkohol",
                    value = drinkingHabit.title,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LifestyleBadge(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DarkBackground,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PurpleContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PurplePrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(color = DarkOnSurfaceVariant)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun ReputationHeaderSection(
    averageRating: Double,
    reviewCount: Int,
    onRateUserClick: () -> Unit
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Reputation & Bewertungen",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = DarkOnSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format("%.1f", averageRating),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = DarkOnBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StarRatingBar(rating = averageRating)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (reviewCount == 1) "1 Bewertung" else "$reviewCount Bewertungen",
                    style = MaterialTheme.typography.bodySmall.copy(color = DarkOnSurfaceVariant)
                )
            }

            Button(
                onClick = onRateUserClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleContainer,
                    contentColor = PurplePrimaryLight
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Bewerten", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StarRatingBar(rating: Double) {
    Row {
        for (i in 1..5) {
            val starIcon = when {
                rating >= i -> Icons.Default.Star
                rating >= i - 0.5 -> Icons.Default.StarHalf
                else -> Icons.Default.StarOutline
            }
            Icon(
                imageVector = starIcon,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(18.dp)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(PurpleContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.reviewerUsername.take(1).uppercase(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PurplePrimaryLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = review.reviewerUsername.ifBlank { "Kliq User" },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DarkOnBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (review.isVerified) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TealSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verifizierter Meetup-Review",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TealSecondary)
                                )
                            }
                        }
                    }
                }

                StarRatingBar(rating = review.rating.toDouble())
            }

            if (review.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = review.text,
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkOnSurface)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserRatingBottomSheet(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmitRating: (Int, String) -> Unit
) {
    var selectedRating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nutzer bewerten",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Gib dein ehrliches Feedback zu gemeinsamen Ausgeh-Erlebnissen.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DarkOnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    val isSelected = i <= selectedRating
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = "$i Sterne",
                        tint = if (isSelected) Color(0xFFFFC107) else DarkOnSurfaceVariant,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { selectedRating = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Kommentar schreiben (optional)...", color = DarkOnSurfaceVariant) },
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
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary,
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
                    Text("Bewertung absenden", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ReportUserModalDialog(
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
                text = "Profil melden",
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

private fun String?.isNull_or_blank(): Boolean {
    return this.isNullOrBlank()
}
