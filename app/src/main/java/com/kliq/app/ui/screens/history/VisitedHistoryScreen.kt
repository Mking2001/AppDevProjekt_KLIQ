package com.kliq.app.ui.screens.history

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.KliqScreenScaffold
import com.kliq.app.ui.components.VisitedLogCard
import com.kliq.app.ui.navigation.TopBarMenuAction
import com.kliq.app.ui.navigation.TopBarUiState
import com.kliq.app.viewmodel.HistoryUiState
import com.kliq.app.viewmodel.HistoryViewModel

@Composable
fun VisitedHistoryScreen(
    userId: String = "current_user",
    topBarState: TopBarUiState? = null,
    onToggleMenu: (() -> Unit)? = null,
    onDismissMenu: (() -> Unit)? = null,
    onMenuAction: ((TopBarMenuAction) -> Unit)? = null,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadHistory(userId)
    }

    val content: @Composable (PaddingValues) -> Unit = { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val state = uiState) {
                is HistoryUiState.Loading -> {
                    HistoryLoadingState()
                }

                is HistoryUiState.Empty -> {
                    HistoryEmptyState(
                        onRefresh = { viewModel.loadHistory(userId) }
                    )
                }

                is HistoryUiState.Error -> {
                    HistoryErrorState(
                        message = state.message,
                        onRetry = { viewModel.loadHistory(userId) }
                    )
                }

                is HistoryUiState.Success -> {
                    HistorySuccessState(
                        state = state,
                        onDeleteLog = { logId -> viewModel.deleteVisitedLog(logId, userId) }
                    )
                }
            }
        }
    }

    if (topBarState != null && onToggleMenu != null && onDismissMenu != null && onMenuAction != null) {
        KliqScreenScaffold(
            title = "Besuchs-Historie",
            isMenuExpanded = topBarState.isMenuExpanded,
            onToggleMenu = onToggleMenu,
            onDismissMenu = onDismissMenu,
            onMenuAction = onMenuAction
        ) { innerPadding ->
            content(innerPadding)
        }
    } else {
        content(PaddingValues(0.dp))
    }
}

@Composable
private fun HistoryLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Historie wird geladen...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryEmptyState(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Noch keine Besuche",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Deine vergangenen Club-Besuche und GPS-Bestätigungen werden hier chronologisch angezeigt.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onRefresh,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aktualisieren")
        }
    }
}

@Composable
private fun HistoryErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Fehler beim Laden",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Erneut versuchen")
        }
    }
}

@Composable
private fun HistorySuccessState(
    state: HistoryUiState.Success,
    onDeleteLog: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            HistorySummaryHeader(
                totalVisits = state.totalVisitsCount,
                verifiedVisits = state.verifiedVisitsCount
            )
        }

        items(
            items = state.logs,
            key = { it.id }
        ) { log ->
            VisitedLogCard(
                log = log,
                onDeleteClick = { onDeleteLog(log.id) }
            )
        }
    }
}

@Composable
private fun HistorySummaryHeader(
    totalVisits: Int,
    verifiedVisits: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Gesamte Besuche",
            value = totalVisits.toString(),
            icon = Icons.Default.History,
            iconColor = MaterialTheme.colorScheme.primary
        )

        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "GPS Verifiziert",
            value = verifiedVisits.toString(),
            icon = Icons.Default.VerifiedUser,
            iconColor = com.kliq.app.ui.theme.TealSecondary
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}
