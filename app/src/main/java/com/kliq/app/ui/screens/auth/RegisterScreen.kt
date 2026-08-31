package com.kliq.app.ui.screens.auth

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Visibility as EyeIcon
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kliq.app.data.model.CountryCodes
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.ErrorRed
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.ui.theme.TealSecondary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var targetSlotForPicker by remember { mutableIntStateOf(0) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoSelected(context, it, targetSlotForPicker) }
    }

    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            onRegistrationSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Konto erstellen",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            SectionCard(title = "Profilbilder (Bis zu 4 Fotos)*") {
                Text(
                    text = "Foto 1 ist dein Hauptbild. Klicke auf ein Bild oder die Vorschau, um dein Profil zu testen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (index in 0 until 4) {
                        val photoUrl = uiState.photos.getOrNull(index)
                        val hasPhoto = !photoUrl.isNullOrBlank()

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(
                                    width = if (hasPhoto) 2.dp else 1.5.dp,
                                    brush = if (hasPhoto) {
                                        Brush.linearGradient(listOf(PurplePrimary, FuchsiaTertiary))
                                    } else {
                                        Brush.linearGradient(listOf(PurplePrimaryLight.copy(alpha = 0.4f), Color.White.copy(alpha = 0.1f)))
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (hasPhoto) {
                                        viewModel.openPreviewModal(index)
                                    } else {
                                        targetSlotForPicker = index
                                        imagePickerLauncher.launch("image/*")
                                    }
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
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .clickable { viewModel.onRemovePhoto(index) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Foto entfernen",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.AddAPhoto,
                                        contentDescription = "Foto hinzufügen",
                                        tint = PurplePrimaryLight,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (index == 0) "Hauptbild*" else "+",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (index == 0) PurplePrimaryLight else Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.isProcessingImage) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = PurplePrimaryLight,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bild wird komprimiert...", color = PurplePrimaryLight, style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (uiState.photos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.openPreviewModal(0) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TealSecondary),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(listOf(TealSecondary, PurplePrimaryLight))
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EyeIcon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Profil-Vorschau ansehen",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            SectionCard(title = "Benutzerdaten*") {

                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = viewModel::onUsernameChanged,
                    label = { Text("Benutzername*") },
                    placeholder = { Text("z.B. max_mustermann") },
                    leadingIcon = {
                        Icon(Icons.Filled.AlternateEmail, contentDescription = null, tint = PurplePrimaryLight)
                    },
                    trailingIcon = {
                        when (uiState.usernameStatus) {
                            is UsernameCheckStatus.Checking -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = PurplePrimaryLight,
                                    strokeWidth = 2.dp
                                )
                            }
                            is UsernameCheckStatus.Available -> {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verfügbar",
                                    tint = TealSecondary
                                )
                            }
                            is UsernameCheckStatus.Taken -> {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = "Vergeben",
                                    tint = ErrorRed
                                )
                            }
                            else -> null
                        }
                    },
                    isError = uiState.usernameError != null,
                    supportingText = {
                        when (uiState.usernameStatus) {
                            is UsernameCheckStatus.Available -> {
                                Text("✓ Benutzername ist verfügbar!", color = TealSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                            is UsernameCheckStatus.Taken -> {
                                Text(uiState.usernameError ?: "Bereits vergeben", color = ErrorRed, style = MaterialTheme.typography.labelSmall)
                            }
                            is UsernameCheckStatus.Checking -> {
                                Text("Prüfe Verfügbarkeit...", color = PurplePrimaryLight, style = MaterialTheme.typography.labelSmall)
                            }
                            else -> {
                                uiState.usernameError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("E-Mail-Adresse*") },
                    placeholder = { Text("z.B. max.mustermann@gmail.com") },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = PurplePrimaryLight)
                    },
                    trailingIcon = {
                        when (uiState.emailStatus) {
                            is EmailCheckStatus.Checking -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = PurplePrimaryLight,
                                    strokeWidth = 2.dp
                                )
                            }
                            is EmailCheckStatus.Available -> {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verfügbar",
                                    tint = TealSecondary
                                )
                            }
                            is EmailCheckStatus.Taken -> {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = "Bereits registriert",
                                    tint = ErrorRed
                                )
                            }
                            else -> null
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    isError = uiState.emailError != null,
                    supportingText = {
                        when (uiState.emailStatus) {
                            is EmailCheckStatus.Available -> {
                                Text("✓ E-Mail ist frei", color = TealSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                            is EmailCheckStatus.Taken -> {
                                Text(uiState.emailError ?: "Bereits vergeben", color = ErrorRed, style = MaterialTheme.typography.labelSmall)
                            }
                            is EmailCheckStatus.Checking -> {
                                Text("Prüfe E-Mail...", color = PurplePrimaryLight, style = MaterialTheme.typography.labelSmall)
                            }
                            else -> {
                                uiState.emailError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.firstName,
                        onValueChange = viewModel::onFirstNameChanged,
                        label = { Text("Vorname*") },
                        isError = uiState.firstNameError != null,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = customTextFieldColors()
                    )

                    OutlinedTextField(
                        value = uiState.lastName,
                        onValueChange = viewModel::onLastNameChanged,
                        label = { Text("Nachname*") },
                        isError = uiState.lastNameError != null,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = customTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, PurplePrimaryLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleCountryCodeDropdown() }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = CountryCodes.getFlag(uiState.countryCode),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.countryCode,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Vorwahl wählen",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = uiState.isCountryCodeDropdownExpanded,
                            onDismissRequest = { viewModel.dismissCountryCodeDropdown() },
                            modifier = Modifier.background(Color(0xFF232035))
                        ) {
                            CountryCodes.list.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = item.flag, fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = item.countryName, color = Color.White, fontWeight = FontWeight.Medium)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "(${item.code})", color = PurplePrimaryLight, fontSize = 13.sp)
                                        }
                                    },
                                    onClick = { viewModel.onCountryCodeChanged(item.code) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.phoneNumber,
                        onValueChange = viewModel::onPhoneNumberChanged,
                        label = { Text("Telefonnummer*") },
                        placeholder = { Text("660 1234567") },
                        leadingIcon = {
                            Icon(Icons.Filled.Phone, contentDescription = null, tint = PurplePrimaryLight)
                        },
                        trailingIcon = {
                            when (uiState.phoneStatus) {
                                is PhoneCheckStatus.Checking -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = PurplePrimaryLight,
                                        strokeWidth = 2.dp
                                    )
                                }
                                is PhoneCheckStatus.Available -> {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Frei",
                                        tint = TealSecondary
                                    )
                                }
                                is PhoneCheckStatus.Taken -> {
                                    Icon(
                                        imageVector = Icons.Filled.ErrorOutline,
                                        contentDescription = "Bereits registriert",
                                        tint = ErrorRed
                                    )
                                }
                                else -> null
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        isError = uiState.phoneNumberError != null,
                        supportingText = {
                            when (uiState.phoneStatus) {
                                is PhoneCheckStatus.Available -> {
                                    Text("✓ Nummer ist frei", color = TealSecondary, style = MaterialTheme.typography.labelSmall)
                                }
                                is PhoneCheckStatus.Taken -> {
                                    Text(uiState.phoneNumberError ?: "Bereits vergeben", color = ErrorRed, style = MaterialTheme.typography.labelSmall)
                                }
                                is PhoneCheckStatus.Checking -> {
                                    Text("Prüfe Nummer...", color = PurplePrimaryLight, style = MaterialTheme.typography.labelSmall)
                                }
                                else -> {
                                    uiState.phoneNumberError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = customTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.hometown,
                        onValueChange = viewModel::onHometownChanged,
                        label = { Text("Heimatstadt (Österreich)*") },
                        placeholder = { Text("z.B. Klagenfurt, Wien, Graz...") },
                        leadingIcon = {
                            Icon(Icons.Filled.LocationCity, contentDescription = null, tint = PurplePrimaryLight)
                        },
                        trailingIcon = {
                            if (com.kliq.app.data.model.AustrianCities.isValidCity(uiState.hometown)) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Gültige Stadt",
                                    tint = TealSecondary
                                )
                            }
                        },
                        isError = uiState.hometownError != null,
                        supportingText = {
                            if (com.kliq.app.data.model.AustrianCities.isValidCity(uiState.hometown)) {
                                Text("✓ Stadt ausgewählt: ${uiState.hometown}", color = TealSecondary, style = MaterialTheme.typography.labelSmall)
                            } else {
                                uiState.hometownError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customTextFieldColors()
                    )

                    if (uiState.isHometownDropdownExpanded && uiState.hometownSuggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF232035)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    listOf(PurplePrimaryLight.copy(alpha = 0.5f), FuchsiaTertiary.copy(alpha = 0.3f))
                                ),
                                width = 1.dp
                            )
                        ) {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                uiState.hometownSuggestions.take(5).forEach { city ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.onHometownSuggestionSelected(city) }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationCity,
                                            contentDescription = null,
                                            tint = PurplePrimaryLight,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = city,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SectionCard(title = "Geschlecht*") {
                SegmentedPillBar(
                    label = "",
                    options = listOf(
                        "MALE" to "Männlich",
                        "FEMALE" to "Weiblich",
                        "DIVERSE" to "Divers"
                    ),
                    selectedOption = uiState.gender,
                    onOptionSelected = { viewModel.onGenderSelected(it) }
                )
            }

            SectionCard(title = "Geburtsdatum (Mindestalter 18 Jahre)*") {
                val calendar = Calendar.getInstance()
                val initialYear = calendar.get(Calendar.YEAR) - 20
                val initialMonth = calendar.get(Calendar.MONTH)
                val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = remember {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedCal = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth, 0, 0, 0)
                            }
                            viewModel.onBirthDateSelected(selectedCal.timeInMillis)
                        },
                        initialYear,
                        initialMonth,
                        initialDay
                    ).apply {
                        val maxCal = Calendar.getInstance().apply {
                            add(Calendar.YEAR, -18)
                        }
                        datePicker.maxDate = maxCal.timeInMillis
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { datePickerDialog.show() }
                ) {
                    OutlinedTextField(
                        value = uiState.birthDateFormatted,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Geburtsdatum wählen*") },
                        placeholder = { Text("TT.MM.JJJJ") },
                        leadingIcon = {
                            Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = PurplePrimaryLight)
                        },
                        isError = uiState.birthDateError != null,
                        supportingText = {
                            uiState.birthDateError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                }
            }

            SectionCard(title = "Lifestyle & Gewohnheiten*") {

                SegmentedPillBar(
                    label = "Rauchkonsum:",
                    options = listOf(
                        SmokingHabit.NEVER to "Nicht Raucher",
                        SmokingHabit.OCCASIONALLY to "PartyRaucher",
                        SmokingHabit.REGULARLY to "Raucher"
                    ),
                    selectedOption = uiState.smokingHabit,
                    onOptionSelected = { viewModel.onSmokingHabitSelected(it) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                SegmentedPillBar(
                    label = "Alkoholkonsum:",
                    options = listOf(
                        DrinkingHabit.NEVER to "Nicht Trinker",
                        DrinkingHabit.SOCIAL to "Genuss Trinker",
                        DrinkingHabit.FREQUENTLY to "Säufer"
                    ),
                    selectedOption = uiState.drinkingHabit,
                    onOptionSelected = { viewModel.onDrinkingHabitSelected(it) }
                )
            }

            SectionCard(title = "Nach was suchst du?*") {
                SegmentedPillBar(
                    label = "Such-Präferenz:",
                    options = listOf(
                        SearchIntent.DATING to "Liebe",
                        SearchIntent.FRIENDS to "Freunde",
                        SearchIntent.BOTH to "Beides"
                    ),
                    selectedOption = uiState.searchIntent,
                    onOptionSelected = { viewModel.onSearchIntentSelected(it) }
                )
            }

            SectionCard(title = "Über dich (Bio - Optional)") {
                OutlinedTextField(
                    value = uiState.bio,
                    onValueChange = viewModel::onBioChanged,
                    label = { Text("Kurze Beschreibung") },
                    placeholder = { Text("z.B. Musik-Liebhaber, gehe gerne am Wochenende feiern...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = customTextFieldColors()
                )
            }

            SectionCard(title = "Sicherheit & Passwort*") {
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = { Text("Passwort (mind. 6 Zeichen)*") },
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = PurplePrimaryLight)
                    },
                    trailingIcon = {
                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Passwort anzeigen",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = uiState.passwordError != null,
                    supportingText = {
                        uiState.passwordError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChanged,
                    label = { Text("Passwort wiederholen*") },
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = PurplePrimaryLight)
                    },
                    trailingIcon = {
                        IconButton(onClick = viewModel::toggleConfirmPasswordVisibility) {
                            Icon(
                                imageVector = if (uiState.isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Passwort anzeigen",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = uiState.confirmPasswordError != null,
                    supportingText = {
                        uiState.confirmPasswordError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = customTextFieldColors()
                )
            }

            Button(
                onClick = viewModel::onRegister,
                enabled = uiState.isFormValid && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = if (uiState.isFormValid) {
                            Brush.horizontalGradient(listOf(PurplePrimary, FuchsiaTertiary))
                        } else {
                            Brush.horizontalGradient(listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.3f)))
                        }
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Konto erstellen & loslegen",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (uiState.isFormValid) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (uiState.isPreviewModalOpen && uiState.photos.isNotEmpty()) {
        val totalPhotos = uiState.photos.size
        val currentPhotoIndex = uiState.previewPhotoIndex.coerceIn(0, totalPhotos - 1)
        val currentPhotoUrl = uiState.photos[currentPhotoIndex]

        Dialog(
            onDismissRequest = { viewModel.closePreviewModal() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                color = Color.Black
            ) {
                Box(modifier = Modifier.fillMaxSize()) {

                    AsyncImage(
                        model = currentPhotoUrl,
                        contentDescription = "Profilbild ${currentPhotoIndex + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Row(modifier = Modifier.fillMaxSize()) {

                        Box(
                            modifier = Modifier
                                .weight(0.45f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    viewModel.previousPreviewPhoto()
                                }
                        )

                        Spacer(modifier = Modifier.weight(0.1f))

                        Box(
                            modifier = Modifier
                                .weight(0.45f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    viewModel.nextPreviewPhoto()
                                }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (i in 0 until totalPhotos) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (i == currentPhotoIndex) Color.White else Color.White.copy(alpha = 0.35f)
                                        )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val displayName = if (uiState.firstName.isNotBlank()) {
                                    "${uiState.firstName} ${uiState.lastName}".trim()
                                } else if (uiState.username.isNotBlank()) {
                                    uiState.username
                                } else {
                                    "Dein Profil"
                                }

                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "🇦🇹 ${uiState.hometown.ifBlank { "Österreich" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.closePreviewModal() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Schließen",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PreviewBadge(text = "⭐ ${uiState.searchIntent.title}")
                            PreviewBadge(text = "🚬 ${uiState.smokingHabit.title}")
                            PreviewBadge(text = "🍸 ${uiState.drinkingHabit.title}")
                        }

                        if (uiState.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "\"${uiState.bio}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tippe links oder rechts, um zwischen Fotos zu wechseln (${currentPhotoIndex + 1}/$totalPhotos)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

@Composable
private fun <T> SegmentedPillBar(
    label: String,
    options: List<Pair<T, String>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1E1B2E))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { (option, title) ->
                val isSelected = option == selectedOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(listOf(PurplePrimary, FuchsiaTertiary))
                            } else {
                                Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .clickable { onOptionSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f),
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.04f))
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = PurplePrimaryLight,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
private fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.8f),
    focusedContainerColor = Color.White.copy(alpha = 0.04f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
    disabledContainerColor = Color.White.copy(alpha = 0.03f),
    focusedBorderColor = PurplePrimaryLight,
    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
    disabledBorderColor = Color.White.copy(alpha = 0.15f),
    focusedLabelColor = PurplePrimaryLight,
    unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
    disabledLabelColor = Color.White.copy(alpha = 0.6f),
    cursorColor = PurplePrimaryLight
)
