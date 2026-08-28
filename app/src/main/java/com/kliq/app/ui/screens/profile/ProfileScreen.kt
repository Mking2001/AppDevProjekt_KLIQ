package com.kliq.app.ui.screens.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Edit
import com.kliq.app.ui.components.KliqIcon
import com.kliq.app.ui.components.KliqIconCategory
import com.kliq.app.ui.components.KliqIconSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.KliqScreenScaffold
import com.kliq.app.ui.components.ProfileAvatarImage
import com.kliq.app.ui.components.ProfileImagePickerBottomSheet
import com.kliq.app.ui.components.ZoomableImageOverlay
import com.kliq.app.ui.components.UserRatingStarBar
import com.kliq.app.ui.components.ReviewCommentSection
import com.kliq.app.ui.components.ProfileQrCodeBottomSheet
import com.kliq.app.ui.screens.history.VisitedHistoryScreen
import androidx.compose.material.icons.filled.QrCode2
import com.kliq.app.ui.navigation.TopBarMenuAction
import com.kliq.app.ui.navigation.TopBarUiState
import java.io.File

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    topBarState: TopBarUiState,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    onNavigateToQrScanner: () -> Unit = {},
    onNavigateToClub: (String) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showImagePickerSheet by remember { mutableStateOf(false) }
    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.infoMessage) {
        val message = uiState.errorMessage ?: uiState.infoMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(context, it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraImageUri?.let { uri ->
                viewModel.onImageSelected(context, uri)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createProfileTempImageUri(context)
            tempCameraImageUri = uri
            uri?.let { cameraLauncher.launch(it) }
        } else {
            viewModel.onPermissionDenied(Manifest.permission.CAMERA)
        }
    }

    fun launchCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val uri = createProfileTempImageUri(context)
            tempCameraImageUri = uri
            uri?.let { cameraLauncher.launch(it) }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun launchGallery() {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    KliqScreenScaffold(
        title = "Profil",
        isMenuExpanded = topBarState.isMenuExpanded,
        onToggleMenu = onToggleMenu,
        onDismissMenu = onDismissMenu,
        onMenuAction = onMenuAction
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    ProfileHeader(
                        uiState = uiState,
                        onEditProfile = { viewModel.onEditProfile() },
                        onShowQrCode = { viewModel.showQrCodeModal() },
                        onAvatarClick = { viewModel.openProfileImageViewer() },
                        onCameraBadgeClick = { showImagePickerSheet = true }
                    )
                }

                item {
                    ProfileTabRow(
                        tabs = uiState.tabs,
                        selectedTabIndex = uiState.selectedTabIndex,
                        onTabSelected = { viewModel.onTabSelected(it) }
                    )
                }

                item {
                    ProfileTabContent(
                        uiState = uiState,
                        onEventClick = onNavigateToClub
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    ProfileEditDialog(
        isVisible = uiState.isEditDialogVisible,
        name = uiState.editName,
        bio = uiState.editBio,
        location = uiState.editLocation,
        isSaving = uiState.isSavingProfile,
        onNameChange = viewModel::onEditNameChanged,
        onBioChange = viewModel::onEditBioChanged,
        onLocationChange = viewModel::onEditLocationChanged,
        onSave = viewModel::onSaveProfile,
        onDismiss = viewModel::onEditDialogDismissed
    )

    ProfileImagePickerBottomSheet(
        isVisible = showImagePickerSheet,
        onDismissRequest = { showImagePickerSheet = false },
        onCameraSelect = { launchCamera() },
        onGallerySelect = { launchGallery() }
    )

    ProfileQrCodeBottomSheet(
        isVisible = uiState.isQrModalVisible,
        qrBitmap = uiState.qrCodeBitmap,
        isGenerating = uiState.isGeneratingQrCode,
        displayName = uiState.displayName,
        username = uiState.username,
        onDismissRequest = { viewModel.dismissQrCodeModal() },
        onScanQrCode = onNavigateToQrScanner
    )

    ZoomableImageOverlay(
        isVisible = uiState.imageViewerState.isFullscreenVisible,
        onDismiss = { viewModel.dismissProfileImageViewer() },
        imageUrl = uiState.imageViewerState.targetImageUrl,
        initials = uiState.displayName.ifBlank { "MM" },
        scaleState = uiState.imageViewerState.currentScale,
        offsetXState = uiState.imageViewerState.translationOffsetX,
        offsetYState = uiState.imageViewerState.translationOffsetY,
        onZoomStateChanged = { scale, offsetX, offsetY ->
            viewModel.updateZoomState(scale, offsetX, offsetY)
        }
    )
}

@Composable
private fun ProfileHeader(
    uiState: ProfileUiState,
    onEditProfile: () -> Unit,
    onShowQrCode: () -> Unit,
    onAvatarClick: () -> Unit,
    onCameraBadgeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileAvatarImage(
            imageUri = uiState.profilePictureUrl,
            isProcessing = uiState.isProcessingImage,
            initials = uiState.displayName.ifBlank { "MM" },
            onAvatarClick = onAvatarClick,
            onCameraBadgeClick = onCameraBadgeClick,
            size = 100.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = uiState.displayName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = uiState.username,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            KliqIcon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                size = KliqIconSize.SMALL,
                category = KliqIconCategory.ACTION,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = uiState.location,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        UserRatingStarBar(
            averageRating = uiState.averageRating,
            formattedAverageRating = uiState.formattedAverageRating,
            totalReviewsCount = uiState.totalReviewsCount,
            verifiedReviewsCount = uiState.verifiedReviewsCount,
            hasRatings = uiState.hasRatings
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(count = uiState.postsCount.toString(), label = "Beiträge")
            StatItem(count = formatCount(uiState.followersCount), label = "Follower")
            StatItem(count = formatCount(uiState.followingCount), label = "Following")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onEditProfile,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                KliqIcon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    size = KliqIconSize.SMALL,
                    category = KliqIconCategory.ACTION
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Bearbeiten",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onShowQrCode,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                KliqIcon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    size = KliqIconSize.SMALL,
                    category = KliqIconCategory.ACTION,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "QR-Pass",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 0.dp,
        divider = {
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.5.dp
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = tab,
                        fontWeight = if (selectedTabIndex == index)
                            FontWeight.Bold else FontWeight.Medium
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Rendert den Inhalt des aktuell gewählten Profil-Tabs.
 *
 * @param onEventClick Callback mit der Club-ID beim Antippen einer Event-Karte.
 */
@Composable
private fun ProfileTabContent(
    uiState: ProfileUiState,
    onEventClick: (String) -> Unit
) {
    when (uiState.selectedTabIndex) {
        0 -> OwnPostsList(posts = uiState.ownPosts)
        1 -> EventsList(events = uiState.upcomingEvents, onEventClick = onEventClick)
        2 -> VisitedHistoryScreen(userId = uiState.userId)
        3 -> AboutSection(bio = uiState.bio, location = uiState.location)
    }
}

/**
 * Liste der eigenen Beitraege im Tab "Beitraege".
 */
@Composable
private fun OwnPostsList(posts: List<ProfilePostUi>) {
    if (posts.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Noch keine eigenen Beitraege",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Erstelle im Home-Feed ueber das Plus-Symbol deinen ersten Beitrag.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        posts.forEach { post ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Text(
                    text = post.contentText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!post.clubName.isNullOrBlank()) {
                        Text(
                            text = " - ${post.clubName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (post.likeCount == 1) "1 Like" else "${post.likeCount} Likes",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Kommende Event-Agenda im Tab "Events".
 * Jede Karte fuehrt in die Detailansicht des zugehoerigen Clubs.
 */
@Composable
private fun EventsList(
    events: List<ProfileEventUi>,
    onEventClick: (String) -> Unit
) {
    if (events.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Keine anstehenden Events",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        events.forEach { event ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onEventClick(event.clubId) }
                    .padding(16.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listOf(event.dateLabel, event.clubName, event.price)
                        .filter { it.isNotBlank() }
                        .joinToString(" - "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Beschreibungstext, Standort und Interessen im Tab "Ueber mich".
 */
@Composable
private fun AboutSection(bio: String, location: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Ueber mich",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = bio.ifBlank { PROFILE_BIO_PLACEHOLDER },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )

        if (location.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                KliqIcon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    size = KliqIconSize.SMALL,
                    category = KliqIconCategory.ACTION,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Interessen",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PROFILE_INTERESTS.forEach { interest ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = interest,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/** Hinweistext, solange keine Profilbeschreibung hinterlegt ist. */
private const val PROFILE_BIO_PLACEHOLDER =
    "Noch keine Beschreibung hinterlegt. Ergaenze sie ueber die Schaltflaeche Bearbeiten."

/** Interessen-Schlagworte des Profil-Tabs. */
private val PROFILE_INTERESTS = listOf("Musik", "Nightlife", "Fotografie")

/**
 * Dialog zum Bearbeiten von Anzeigename, Beschreibung und Standort.
 *
 * @param isVisible Ob der Dialog angezeigt wird.
 * @param isSaving Ob gerade gespeichert wird.
 * @param onSave Callback zum Uebernehmen der Aenderungen.
 * @param onDismiss Callback zum Verwerfen der Aenderungen.
 */
@Composable
private fun ProfileEditDialog(
    isVisible: Boolean,
    name: String,
    bio: String,
    location: String,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Profil bearbeiten",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(text = "Anzeigename") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isSaving
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = onBioChange,
                    label = { Text(text = "Ueber mich") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    enabled = !isSaving
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text(text = "Standort") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isSaving
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !isSaving && name.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Speichern", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(text = "Abbrechen")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

private fun createProfileTempImageUri(context: Context): Uri? {
    return try {
        val tempFile = File.createTempFile("profile_capture_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}.${(count % 1_000_000) / 100_000}M"
        count >= 1_000 -> "${count / 1_000}.${(count % 1_000) / 100}k"
        else -> count.toString()
    }
}
