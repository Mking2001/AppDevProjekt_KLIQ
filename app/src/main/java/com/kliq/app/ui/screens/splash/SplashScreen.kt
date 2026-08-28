package com.kliq.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.viewmodel.AuthUiState
import com.kliq.app.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Splash Screen für den Kliq App Start.
 * Evaluates persistent session state via [AuthViewModel] and navigates to either Main Navigation Host or Phone Login UI.
 *
 * @param authViewModel Hilt-injected ViewModel zur Session-Prüfung.
 * @param onNavigateToHome Callback bei erfolgreicher Auto-Login Session-Prüfung.
 * @param onNavigateToPhoneLogin Callback bei fehlender/invalider Session.
 * @param onSplashFinished Optionaler Legacy-Callback für Abwärtskompatibilität.
 */
@Composable
fun SplashScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToAuthSelection: () -> Unit = {},
    onNavigateToPhoneLogin: () -> Unit = onNavigateToAuthSelection,
    onSplashFinished: (() -> Unit)? = null
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        delay(1200)
        when (authState) {
            is AuthUiState.Authenticated -> {
                onNavigateToHome()
                onSplashFinished?.invoke()
            }
            is AuthUiState.Unauthenticated, is AuthUiState.Error -> {
                onNavigateToAuthSelection()
                onSplashFinished?.invoke()
            }
            is AuthUiState.Loading -> {
                // Waiting for session state evaluation
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Nightlife,
                contentDescription = "Kliq Logo",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "KLIQ",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dein Club-Netzwerk",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (authState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = PurplePrimaryLight,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
