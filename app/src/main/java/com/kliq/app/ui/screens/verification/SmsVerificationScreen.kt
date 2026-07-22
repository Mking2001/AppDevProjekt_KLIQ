package com.kliq.app.ui.screens.verification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.ui.theme.TealSecondary
import kotlinx.coroutines.delay

private const val OTP_LENGTH = 6

/**
 * SMS-Verifizierungs-Screen mit 6-stelligem OTP-Eingabefeld.
 *
 * Zeigt den Verifikationscode-Eingabebereich mit animierten Eingabefeldern,
 * einem Countdown-Timer für den erneuten Versand und visuellem Feedback
 * bei Fehleingaben (Shake-Animation) sowie Erfolgszustand.
 *
 * @param onVerificationSuccess Callback bei erfolgreicher Verifizierung.
 * @param onNavigateBack Callback für die Zurück-Navigation.
 * @param viewModel Hilt-injiziertes [SmsVerificationViewModel].
 */
@Composable
fun SmsVerificationScreen(
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SmsVerificationViewModel = hiltViewModel()
) {
    val verificationState by viewModel.verificationState.collectAsStateWithLifecycle()
    val enteredCode by viewModel.enteredCode.collectAsStateWithLifecycle()
    val resendTimerState by viewModel.resendTimerState.collectAsStateWithLifecycle()
    val phoneNumber = viewModel.phoneNumber

    val isError = verificationState is VerificationUiState.Error
    val isLoading = verificationState is VerificationUiState.Loading
    val isSuccess = verificationState is VerificationUiState.Success

    val keyboardController = LocalSoftwareKeyboardController.current

    // Navigation bei erfolgreicher Verifizierung nach kurzem visuellen Delay
    LaunchedEffect(verificationState) {
        if (verificationState is VerificationUiState.Success) {
            keyboardController?.hide()
            delay(800L)
            onVerificationSuccess()
        }
        if (verificationState is VerificationUiState.Loading) {
            keyboardController?.hide()
        }
    }

    // Shake-Offset für Fehler-Animation (horizontales Schütteln)
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(verificationState) {
        if (verificationState is VerificationUiState.Error) {
            repeat(3) {
                shakeOffset.animateTo(24f, animationSpec = tween(40))
                shakeOffset.animateTo(-24f, animationSpec = tween(40))
            }
            shakeOffset.animateTo(0f, animationSpec = tween(40))
        }
    }

    // Auto-Focus beim Screen-Eintritt
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(300L)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Zurück-Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Header
        Text(
            text = "Code eingeben",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Beschreibungstext mit hervorgehobener Telefonnummer
        Text(
            text = "Wir haben einen 6-stelligen Code an",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = phoneNumber,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "gesendet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // OTP-Eingabefeld
        OtpInputRow(
            code = enteredCode,
            onCodeChanged = viewModel::onCodeChanged,
            isError = isError,
            isLoading = isLoading,
            isSuccess = isSuccess,
            shakeOffset = shakeOffset.value,
            focusRequester = focusRequester,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ladeindikator
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(28.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.5.dp
            )
        }

        // Fehlermeldung
        AnimatedVisibility(
            visible = isError,
            enter = slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(250)
            ) + fadeIn(animationSpec = tween(250)),
            exit = slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(200)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            val errorMessage = (verificationState as? VerificationUiState.Error)?.message ?: ""
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Erfolgsmeldung
        AnimatedVisibility(
            visible = isSuccess,
            enter = scaleIn(
                animationSpec = spring(dampingRatio = 0.6f)
            ) + fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Verifiziert",
                    tint = TealSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Erfolgreich verifiziert",
                    style = MaterialTheme.typography.bodySmall,
                    color = TealSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Code erneut senden
        ResendCodeSection(
            timerState = resendTimerState,
            onResendClick = viewModel::resendCode
        )
    }
}

// ---------------------------------------------------------------------------
// OTP-Eingabebereich
// ---------------------------------------------------------------------------

/**
 * Reihe aus 6 OTP-Ziffernfeldern mit unsichtbarem [BasicTextField] für die
 * native Tastatureingabe. Die visuelle Darstellung erfolgt über einzelne
 * [OtpDigitBox]-Composables mit animierten Rahmen und Cursor.
 */
@Composable
private fun OtpInputRow(
    code: String,
    onCodeChanged: (String) -> Unit,
    isError: Boolean,
    isLoading: Boolean,
    isSuccess: Boolean,
    shakeOffset: Float,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = modifier) {
        // Visuelle Ziffern-Boxen (bestimmt die Größe des Containers)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { translationX = shakeOffset },
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
        ) {
            for (i in 0 until OTP_LENGTH) {
                OtpDigitBox(
                    digit = code.getOrNull(i),
                    isFocused = i == code.length && !isLoading && !isSuccess,
                    isError = isError,
                    isSuccess = isSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Unsichtbares TextField fängt Tastatureingaben ab
        BasicTextField(
            value = code,
            onValueChange = onCodeChanged,
            modifier = Modifier
                .matchParentSize()
                .focusRequester(focusRequester)
                .alpha(0f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            singleLine = true,
            enabled = !isLoading && !isSuccess,
            cursorBrush = SolidColor(Color.Transparent)
        )
    }
}

/**
 * Einzelne OTP-Ziffernbox mit animierter Rahmenfarbe, blinkender
 * Cursor-Animation im fokussierten Zustand, und Farbwechsel bei
 * Fehler- bzw. Erfolgszustand.
 */
@Composable
private fun OtpDigitBox(
    digit: Char?,
    isFocused: Boolean,
    isError: Boolean,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isSuccess -> TealSecondary
            isError -> MaterialTheme.colorScheme.error
            isFocused -> PurplePrimary
            digit != null -> PurplePrimaryLight.copy(alpha = 0.4f)
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "otpBorderColor"
    )

    val borderWidth = if (isFocused || isError || isSuccess) 2.dp else 1.dp

    // Blinkender Cursor im aktiven Eingabefeld
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 530),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (digit != null) {
            Text(
                text = digit.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        } else if (isFocused) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .alpha(cursorAlpha)
                    .background(
                        color = PurplePrimary,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Resend-Code Sektion
// ---------------------------------------------------------------------------

/**
 * Zeigt entweder den aktiven "Code erneut senden"-Button oder
 * den deaktivierten Countdown-Text mit verbleibenden Sekunden.
 */
@Composable
private fun ResendCodeSection(
    timerState: ResendTimerState,
    onResendClick: () -> Unit
) {
    if (timerState.canResend) {
        TextButton(onClick = onResendClick) {
            Text(
                text = "Code erneut senden",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else {
        Text(
            text = "Code erneut senden in ${timerState.secondsRemaining}s",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
