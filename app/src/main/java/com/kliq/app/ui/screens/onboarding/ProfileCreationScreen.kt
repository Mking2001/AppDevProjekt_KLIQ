package com.kliq.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

/**
 * Screen for Step 3.3 of the Kliq Onboarding flow: Profile Creation.
 *
 * Implements high-contrast dark theme styling, real-time input field validation,
 * and MVVM architecture connected to [ProfileCreationViewModel].
 */
@Composable
fun ProfileCreationScreen(
    viewModel: ProfileCreationViewModel = hiltViewModel(),
    onProfileCreated: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isProfileSaved) {
        if (uiState.isProfileSaved) {
            onProfileCreated()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkBackground,
                            DarkSurface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header / Step indicator
                Text(
                    text = "SCHRITT 1 VON 3 • PROFIL ERSTELLEN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PurplePrimaryLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Erstelle dein Profil",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Lass die Kliq-Community wissen, wer du bist. Diese Infos werden auf deinem Profil angezeigt.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Form Card Container
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // Username Field
                        Column {
                            OutlinedTextField(
                                value = uiState.username,
                                onValueChange = viewModel::onUsernameChanged,
                                label = { Text("Benutzername *") },
                                placeholder = { Text("z.B. alex_night") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Benutzername Icon",
                                        tint = PurplePrimaryLight
                                    )
                                },
                                trailingIcon = {
                                    when {
                                        uiState.usernameError != null -> {
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = "Fehler",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        uiState.username.isNotBlank() && uiState.usernameError == null -> {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Gültig",
                                                tint = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                },
                                isError = uiState.usernameError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                colors = customTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            AnimatedVisibility(visible = uiState.usernameError != null) {
                                Text(
                                    text = uiState.usernameError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                        }

                        // Age Field
                        Column {
                            OutlinedTextField(
                                value = uiState.age,
                                onValueChange = viewModel::onAgeChanged,
                                label = { Text("Alter *") },
                                placeholder = { Text("z.B. 24") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Cake,
                                        contentDescription = "Alter Icon",
                                        tint = PurplePrimaryLight
                                    )
                                },
                                trailingIcon = {
                                    when {
                                        uiState.ageError != null -> {
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = "Fehler",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        uiState.age.isNotBlank() && uiState.ageError == null -> {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Gültig",
                                                tint = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                },
                                isError = uiState.ageError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                colors = customTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            AnimatedVisibility(visible = uiState.ageError != null) {
                                Text(
                                    text = uiState.ageError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                        }

                        // Hometown Field
                        Column {
                            OutlinedTextField(
                                value = uiState.hometown,
                                onValueChange = viewModel::onHometownChanged,
                                label = { Text("Heimatstadt *") },
                                placeholder = { Text("z.B. Berlin") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Heimatstadt Icon",
                                        tint = PurplePrimaryLight
                                    )
                                },
                                trailingIcon = {
                                    when {
                                        uiState.hometownError != null -> {
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = "Fehler",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        uiState.hometown.isNotBlank() && uiState.hometownError == null -> {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Gültig",
                                                tint = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                },
                                isError = uiState.hometownError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                colors = customTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            AnimatedVisibility(visible = uiState.hometownError != null) {
                                Text(
                                    text = uiState.hometownError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                        }

                        // Bio Field
                        Column {
                            OutlinedTextField(
                                value = uiState.bio,
                                onValueChange = viewModel::onBioChanged,
                                label = { Text("Bio / Über mich") },
                                placeholder = { Text("Erzähle etwas über deine Musik- und Club-Präferenzen...") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Bio Icon",
                                        tint = PurplePrimaryLight
                                    )
                                },
                                isError = uiState.bioError != null,
                                minLines = 3,
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (uiState.isFormValid) {
                                            viewModel.onSaveProfile()
                                        }
                                    }
                                ),
                                colors = customTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (uiState.bioError != null) {
                                    Text(
                                        text = uiState.bioError ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }
                                Text(
                                    text = "${uiState.bio.length} / 150",
                                    color = if (uiState.bio.length > 150) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onSaveProfile()
                    },
                    enabled = uiState.isFormValid && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        disabledContainerColor = DarkSurfaceVariant,
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Profil erstellen",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Weiter"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PurplePrimaryLight,
    unfocusedBorderColor = DarkSurfaceVariant,
    errorBorderColor = MaterialTheme.colorScheme.error,
    focusedLabelColor = PurplePrimaryLight,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    errorLabelColor = MaterialTheme.colorScheme.error,
    focusedContainerColor = DarkSurface,
    unfocusedContainerColor = DarkSurface,
    errorContainerColor = DarkSurface
)
