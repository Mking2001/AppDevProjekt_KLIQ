package com.kliq.app.ui.screens.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kliq.app.data.model.AustrianCities
import com.kliq.app.data.model.CountryCodes
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.ui.components.ProfileAvatarImage
import com.kliq.app.ui.components.ProfileImagePickerBottomSheet
import com.kliq.app.ui.components.ZoomableImageOverlay
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkOnBackground
import com.kliq.app.ui.theme.DarkOnSurface
import com.kliq.app.ui.theme.DarkOnSurfaceVariant
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.ui.theme.TealSecondary
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showImagePickerSheet by remember { mutableStateOf(false) }
    var activeSlotForPicker by remember { mutableIntStateOf(0) }
    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCityPickerModal by remember { mutableStateOf(false) }
    var showCountryCodeModal by remember { mutableStateOf(false) }

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
            viewModel.onPhotoSelected(context, it, activeSlotForPicker)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraImageUri?.let { uri ->
                viewModel.onPhotoSelected(context, uri, activeSlotForPicker)
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

    fun launchCamera(slotIndex: Int) {
        activeSlotForPicker = slotIndex
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val uri = createProfileTempImageUri(context)
            tempCameraImageUri = uri
            uri?.let { cameraLauncher.launch(it) }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun launchGallery(slotIndex: Int) {
        activeSlotForPicker = slotIndex
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "KLIQ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            brush = Brush.horizontalGradient(listOf(Color.White, PurplePrimaryLight))
                        )
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
                    IconButton(
                        onClick = {
                            viewModel.onSaveProfile(onSuccess = onNavigateBack)
                        },
                        enabled = !uiState.isSavingProfile && uiState.editName.isNotBlank()
                    ) {
                        if (uiState.isSavingProfile) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PurplePrimaryLight,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Speichern",
                                tint = if (uiState.editName.isNotBlank()) PurplePrimaryLight else DarkOutline
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val primaryPhoto = uiState.editPhotos.firstOrNull { it.isNotBlank() } ?: uiState.profilePictureUrl
                    ProfileAvatarImage(
                        imageUri = primaryPhoto,
                        isProcessing = uiState.isProcessingImage,
                        initials = uiState.editName.ifBlank { "K" },
                        onAvatarClick = { viewModel.openProfileImageViewer(primaryPhoto) },
                        onCameraBadgeClick = {
                            activeSlotForPicker = 0
                            showImagePickerSheet = true
                        },
                        size = 110.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val ageDisplay = if (uiState.editAge > 0) " ${uiState.editAge}" else if (uiState.age > 0) " ${uiState.age}" else ""
                    Text(
                        text = "${uiState.editName.ifBlank { "Dein Name" }}$ageDisplay",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkOnBackground
                        )
                    )
                }
            }

            item {
                Divider(color = DarkOutline.copy(alpha = 0.25f), thickness = 0.5.dp)
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Fotos (bis zu 4)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkOnSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (index in 0 until 4) {
                            val photoUrl = uiState.editPhotos.getOrNull(index)
                            val hasPhoto = !photoUrl.isNullOrBlank()

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSurfaceVariant.copy(alpha = 0.6f))
                                    .border(
                                        width = if (hasPhoto) 2.dp else 1.dp,
                                        brush = if (hasPhoto) {
                                            Brush.linearGradient(listOf(PurplePrimary, FuchsiaTertiary))
                                        } else {
                                            Brush.linearGradient(listOf(DarkOutline.copy(alpha = 0.4f), DarkOutline.copy(alpha = 0.1f)))
                                        },
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        activeSlotForPicker = index
                                        showImagePickerSheet = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (hasPhoto) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Foto ${index + 1}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(PurplePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = Color.White
                                            )
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(3.dp)
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .clickable { viewModel.onRemovePhoto(index) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Foto entfernen",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AddPhotoAlternate,
                                            contentDescription = "Foto hinzufügen",
                                            tint = PurplePrimaryLight.copy(alpha = 0.8f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (index == 0) "Hauptbild" else "+",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                color = DarkOnSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Divider(color = DarkOutline.copy(alpha = 0.25f), thickness = 0.5.dp)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Username:",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkOnBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.editName,
                            onValueChange = viewModel::onEditNameChanged,
                            placeholder = { Text("@Username", color = DarkOutline) },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface,
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = DarkOutline.copy(alpha = 0.4f),
                                focusedTextColor = DarkOnBackground,
                                unfocusedTextColor = DarkOnBackground
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Heimatstadt:",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkOnBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(DarkSurface)
                                .border(
                                    1.dp,
                                    DarkOutline.copy(alpha = 0.4f),
                                    RoundedCornerShape(24.dp)
                                )
                                .clickable { showCityPickerModal = true }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.editLocation.ifBlank { "Stadt wählen" },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (uiState.editLocation.isNotBlank()) DarkOnBackground else DarkOutline,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Stadt auswählen",
                                    tint = PurplePrimaryLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Auf der Suche nach:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkOnBackground
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SegmentedPillBar(
                        options = listOf(
                            PillOption("Freunden", SearchIntent.FRIENDS),
                            PillOption("Beides", SearchIntent.BOTH),
                            PillOption("Liebe", SearchIntent.DATING)
                        ),
                        selectedOption = uiState.editSearchIntent,
                        onOptionSelected = { viewModel.onEditSearchIntentChanged(it) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Email:",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkOnBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.editEmail,
                            onValueChange = viewModel::onEditEmailChanged,
                            placeholder = { Text("beispiel@kliq.app", color = DarkOutline) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface,
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = DarkOutline.copy(alpha = 0.4f),
                                focusedTextColor = DarkOnBackground,
                                unfocusedTextColor = DarkOnBackground
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1.1f)) {
                        Text(
                            text = "Telefonnummer:",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkOnBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(DarkSurface)
                                    .border(1.dp, DarkOutline.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                                    .clickable { showCountryCodeModal = true }
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.editCountryCode,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PurplePrimaryLight
                                    )
                                )
                            }

                            OutlinedTextField(
                                value = uiState.editPhoneNumber,
                                onValueChange = viewModel::onEditPhoneNumberChanged,
                                placeholder = { Text("1761234567", color = DarkOutline) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface,
                                    focusedBorderColor = PurplePrimary,
                                    unfocusedBorderColor = DarkOutline.copy(alpha = 0.4f),
                                    focusedTextColor = DarkOnBackground,
                                    unfocusedTextColor = DarkOnBackground
                                )
                            )
                        }
                    }
                }
            }

            item {
                Divider(color = DarkOutline.copy(alpha = 0.25f), thickness = 0.5.dp)
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Rauchkonsum:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkOnBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SegmentedPillBar(
                        options = listOf(
                            PillOption("Nicht Raucher", SmokingHabit.NEVER),
                            PillOption("PartyRaucher", SmokingHabit.OCCASIONALLY),
                            PillOption("Raucher", SmokingHabit.REGULARLY)
                        ),
                        selectedOption = uiState.editSmokingHabit,
                        onOptionSelected = { viewModel.onEditSmokingHabitChanged(it) }
                    )
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Alkoholkonsum:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkOnBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SegmentedPillBar(
                        options = listOf(
                            PillOption("Nicht Trinker", DrinkingHabit.NEVER),
                            PillOption("Genuss Trinker", DrinkingHabit.SOCIAL),
                            PillOption("Säufer", DrinkingHabit.FREQUENTLY)
                        ),
                        selectedOption = uiState.editDrinkingHabit,
                        onOptionSelected = { viewModel.onEditDrinkingHabitChanged(it) }
                    )
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Bio:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkOnBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.editBio,
                        onValueChange = viewModel::onEditBioChanged,
                        placeholder = { Text("Erzähle etwas über dich...", color = DarkOutline) },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = DarkOutline.copy(alpha = 0.4f),
                            focusedTextColor = DarkOnBackground,
                            unfocusedTextColor = DarkOnBackground
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.onSaveProfile(onSuccess = onNavigateBack)
                    },
                    enabled = !uiState.isSavingProfile && uiState.editName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        disabledContainerColor = DarkSurfaceVariant
                    )
                ) {
                    if (uiState.isSavingProfile) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Wird gespeichert...", fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Text(
                            text = "Änderungen speichern",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    ProfileImagePickerBottomSheet(
        isVisible = showImagePickerSheet,
        onDismissRequest = { showImagePickerSheet = false },
        onCameraSelect = {
            showImagePickerSheet = false
            launchCamera(activeSlotForPicker)
        },
        onGallerySelect = {
            showImagePickerSheet = false
            launchGallery(activeSlotForPicker)
        }
    )

    ZoomableImageOverlay(
        isVisible = uiState.imageViewerState.isFullscreenVisible,
        onDismiss = { viewModel.dismissProfileImageViewer() },
        imageUrl = uiState.imageViewerState.targetImageUrl,
        initials = uiState.editName.ifBlank { "K" },
        scaleState = uiState.imageViewerState.currentScale,
        offsetXState = uiState.imageViewerState.translationOffsetX,
        offsetYState = uiState.imageViewerState.translationOffsetY,
        onZoomStateChanged = { scale, offsetX, offsetY ->
            viewModel.updateZoomState(scale, offsetX, offsetY)
        }
    )

    if (showCityPickerModal) {
        CitySelectionDialog(
            currentCity = uiState.editLocation,
            onCitySelected = { city ->
                viewModel.onEditLocationChanged(city)
                showCityPickerModal = false
            },
            onDismiss = { showCityPickerModal = false }
        )
    }

    if (showCountryCodeModal) {
        CountryCodeSelectionDialog(
            currentCode = uiState.editCountryCode,
            onCodeSelected = { code ->
                viewModel.onEditCountryCodeChanged(code)
                showCountryCodeModal = false
            },
            onDismiss = { showCountryCodeModal = false }
        )
    }
}

data class PillOption<T>(
    val label: String,
    val value: T
)

@Composable
fun <T> SegmentedPillBar(
    options: List<PillOption<T>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(DarkSurface)
            .border(1.dp, DarkOutline.copy(alpha = 0.3f), RoundedCornerShape(30.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEach { option ->
                val isSelected = option.value == selectedOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (isSelected) Color.White else Color.Transparent
                        )
                        .clickable { onOptionSelected(option.value) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isSelected) Color.Black else DarkOnSurfaceVariant
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun CitySelectionDialog(
    currentCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCities = remember(searchQuery) {
        if (searchQuery.isBlank()) AustrianCities.CITIES
        else AustrianCities.CITIES.filter { it.contains(searchQuery.trim(), ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Heimatstadt wählen",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = DarkOnBackground
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Stadt suchen oder eingeben...", color = DarkOutline) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = PurplePrimaryLight
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = DarkOutline.copy(alpha = 0.4f),
                        focusedTextColor = DarkOnBackground,
                        unfocusedTextColor = DarkOnBackground
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    if (searchQuery.isNotBlank() && filteredCities.none { it.equals(searchQuery.trim(), ignoreCase = true) }) {
                        item {
                            TextButton(
                                onClick = { onCitySelected(searchQuery.trim()) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "„${searchQuery.trim()}“ übernehmen",
                                    color = PurplePrimaryLight,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    items(filteredCities) { city ->
                        val isSelected = city.equals(currentCity.trim(), ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PurplePrimary.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onCitySelected(city) }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = city,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) PurplePrimaryLight else DarkOnBackground,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = DarkOutline)
            }
        },
        containerColor = DarkSurface
    )
}

@Composable
private fun CountryCodeSelectionDialog(
    currentCode: String,
    onCodeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ländervorwahl wählen",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = DarkOnBackground
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                items(CountryCodes.list) { item ->
                    val isSelected = item.code == currentCode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PurplePrimary.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onCodeSelected(item.code) }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.flag, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = item.countryName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) PurplePrimaryLight else DarkOnBackground,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                        Text(
                            text = item.code,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PurplePrimaryLight else DarkOutline
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = DarkOutline)
            }
        },
        containerColor = DarkSurface
    )
}

private fun createProfileTempImageUri(context: Context): Uri? {
    return try {
        val tempFile = File.createTempFile("profile_edit_", ".jpg", context.cacheDir).apply {
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
