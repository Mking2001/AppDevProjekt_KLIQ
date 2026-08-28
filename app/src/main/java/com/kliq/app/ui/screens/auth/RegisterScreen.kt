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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(context, it) }
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
            // 1. Profilbild Bereich (Pflichtfeld)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(
                                colors = if (uiState.profilePictureUrl != null) {
                                    listOf(PurplePrimary, TealSecondary)
                                } else {
                                    listOf(PurplePrimaryLight, FuchsiaTertiary)
                                }
                            ),
                            shape = CircleShape
                        )
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (!uiState.profilePictureUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = uiState.profilePictureUrl,
                            contentDescription = "Profilbild",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.AddAPhoto,
                                contentDescription = "Foto hinzufügen",
                                tint = PurplePrimaryLight,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Foto wählen*",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    if (uiState.isProcessingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = PurplePrimaryLight,
                            strokeWidth = 3.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (uiState.profilePictureUrl != null) "Tippen zum Ändern" else "Mindestens 1 Bild erforderlich*",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.profilePictureUrl != null) TealSecondary else PurplePrimaryLight
                )
            }

            // 2. Benutzername, E-Mail & Name
            SectionCard(title = "Benutzerdaten*") {
                // Benutzername
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

                // E-Mail-Adresse
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("E-Mail-Adresse*") },
                    placeholder = { Text("z.B. max.mustermann@gmail.com") },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = PurplePrimaryLight)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    isError = uiState.emailError != null,
                    supportingText = {
                        uiState.emailError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Vorname & Nachname
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

                // Telefonnummer mit wählbarer Ländervorwahl
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        isError = uiState.phoneNumberError != null,
                        supportingText = {
                            uiState.phoneNumberError?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall) }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = customTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Heimatstadt mit Autocomplete für österreichische Großstädte
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

                    // Autocomplete-Vorschläge
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

            // 3. Geschlecht (Pflicht)
            SectionCard(title = "Geschlecht*") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchIntentOption(
                        title = "Männlich",
                        icon = Icons.Filled.Male,
                        isSelected = uiState.gender == "MALE",
                        onClick = { viewModel.onGenderSelected("MALE") },
                        modifier = Modifier.weight(1f)
                    )
                    SearchIntentOption(
                        title = "Weiblich",
                        icon = Icons.Filled.Female,
                        isSelected = uiState.gender == "FEMALE",
                        onClick = { viewModel.onGenderSelected("FEMALE") },
                        modifier = Modifier.weight(1f)
                    )
                    SearchIntentOption(
                        title = "Divers",
                        icon = Icons.Filled.Transgender,
                        isSelected = uiState.gender == "DIVERSE",
                        onClick = { viewModel.onGenderSelected("DIVERSE") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Geburtsdatum (18+ Validierung)
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

            // 5. Rauch- & Alkoholkonsum (Segmented Bars gemäß Screenshot)
            SectionCard(title = "Lifestyle & Gewohnheiten*") {
                // Rauchkonsum
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

                // Alkoholkonsum
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

            // 6. Such-Präferenz ("Was suchst du?")
            SectionCard(title = "Nach was suchst du?*") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchIntentOption(
                        title = "Liebe",
                        icon = Icons.Filled.Favorite,
                        isSelected = uiState.searchIntent == SearchIntent.DATING,
                        onClick = { viewModel.onSearchIntentSelected(SearchIntent.DATING) },
                        modifier = Modifier.weight(1f)
                    )
                    SearchIntentOption(
                        title = "Freunde",
                        icon = Icons.Filled.Group,
                        isSelected = uiState.searchIntent == SearchIntent.FRIENDS,
                        onClick = { viewModel.onSearchIntentSelected(SearchIntent.FRIENDS) },
                        modifier = Modifier.weight(1f)
                    )
                    SearchIntentOption(
                        title = "Beides",
                        icon = Icons.Filled.Star,
                        isSelected = uiState.searchIntent == SearchIntent.BOTH,
                        onClick = { viewModel.onSearchIntentSelected(SearchIntent.BOTH) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 7. Bio (Optional)
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

            // 8. Passwort & Passwort wiederholen
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

            // 9. Registrieren Button
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
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { (option, title) ->
                val isSelected = option == selectedOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onOptionSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (isSelected) Color.Black else Color.White,
                        maxLines = 1
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
private fun SearchIntentOption(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) PurplePrimary.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f)
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) PurplePrimaryLight else Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) FuchsiaTertiary else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                maxLines = 1
            )
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
