package com.kliq.app.ui.screens.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showImagePickerSheet by remember { mutableStateOf(false) }
    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

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
                    selectedTabIndex = uiState.selectedTabIndex
                )
            }
        }
    }

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

        Spacer(modifier = Modifier.height(8.dp))
        val context = LocalContext.current
        Button(
            onClick = {
                try {
                    com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("FCM Token", token)
                            clipboard.setPrimaryClip(clip)
                            android.widget.Toast.makeText(context, "FCM-Token kopiert! Test-Push gesendet.", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                    val notificationHelper = com.kliq.app.service.notification.NotificationHelper(context)
                    notificationHelper.showGeneralNotification("Kliq Live-Test", "Push-Benachrichtigungen sind aktiv! 🎉")
                } catch (e: Exception) {
                    android.widget.Toast.makeText(context, "Test-Push ausgelöst!", android.widget.Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(
                text = "🔔 FCM Push testen & Token kopieren",
                fontWeight = FontWeight.SemiBold
            )
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

@Composable
private fun ProfileTabContent(selectedTabIndex: Int) {
    when (selectedTabIndex) {
        0 -> PostsGrid()
        1 -> EventsList()
        2 -> VisitedHistoryScreen(userId = "current_user")
        3 -> AboutSection()
    }
}

@Composable
private fun PostsGrid() {
    val itemCount = 9
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        for (row in 0 until (itemCount + 2) / 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    if (index < itemCount) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.primaryContainer
                                                .copy(alpha = 0.3f + (index * 0.05f).coerceAtMost(0.4f))
                                        )
                                    )
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsList() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val events = listOf(
            "Techno Night" to "Sa, 15. Mai · Club Luna",
            "Rooftop Party" to "Fr, 21. Mai · Skybar",
            "After Work" to "Do, 27. Mai · Bar Central"
        )
        events.forEach { (title, details) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = details,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutSection() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Über mich",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Hey! Ich bin Max und immer auf der Suche nach den besten Events und Locations in München. " +
                    "Egal ob Techno, House oder einfach ein gemütlicher Abend – ich bin dabei! " +
                    "Verbinde dich mit mir und lass uns zusammen feiern. 🎶",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )

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
            val interests = listOf("🎵 Musik", "🌙 Nightlife", "📸 Fotografie")
            interests.forEach { interest ->
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
