package com.kliq.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.ui.theme.FuchsiaTertiary

/**
 * Runder Avatar-Platzhalter mit Gradient-Rahmen.
 * Wird in Feed-Karten, Story-Row und Profil verwendet.
 *
 * @param size Durchmesser des Avatars.
 * @param showGradientBorder Ob ein Lila-Gradient-Rahmen angezeigt wird.
 * @param modifier Optionaler Modifier.
 */
@Composable
fun KliqAvatarCircle(
    size: Dp = 48.dp,
    showGradientBorder: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showGradientBorder) {
                    Modifier.border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(PurplePrimaryLight, FuchsiaTertiary, PurplePrimary)
                        ),
                        shape = CircleShape
                    )
                } else Modifier
            )
            .padding(if (showGradientBorder) 3.dp else 0.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        // Initialen-Platzhalter innerhalb des Avatars
        Text(
            text = "K",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Feed-Karte im Glassmorphism-Stil.
 * Platzhalter-Karte für Social-Feed-Einträge mit Profilbild,
 * Name, Zeitstempel, Content-Bereich und Interaktions-Buttons.
 *
 * @param userName Anzeigename des Benutzers.
 * @param timeAgo Zeitangabe (z.B. "Vor 2 Std.").
 * @param contentText Textinhalt des Beitrags.
 * @param modifier Optionaler Modifier.
 */
@Composable
fun KliqFeedCard(
    userName: String,
    timeAgo: String,
    contentText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Profil-Header mit Avatar, Name und Zeitstempel
            Row(verticalAlignment = Alignment.CenterVertically) {
                KliqAvatarCircle(size = 40.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content-Platzhalter (Bild-Bereich)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Beitragstext
            Text(
                text = contentText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Interaktions-Buttons (Like, Kommentar, Teilen)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    IconButton(onClick = { /* Stub */ }) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "Gefällt mir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { /* Stub */ }) {
                        Icon(
                            imageVector = Icons.Filled.ChatBubbleOutline,
                            contentDescription = "Kommentieren",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { /* Stub */ }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Teilen",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "42 Likes",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

/**
 * Selektierbarer Kategorie-Chip im Lila-Design.
 * Wird in Explore- und Map-Screen für Filteroptionen verwendet.
 *
 * @param label Beschriftung des Chips.
 * @param selected Ob der Chip aktuell ausgewählt ist.
 * @param onClick Callback bei Klick.
 * @param modifier Optionaler Modifier.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun KliqCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

/**
 * Einzelne Benachrichtigungszeile.
 * Zeigt Avatar, Beschreibungstext, Zeitstempel und visuellen Ungelesen-Indikator.
 *
 * @param text Benachrichtigungstext.
 * @param timeAgo Zeitangabe.
 * @param isUnread Ob die Benachrichtigung ungelesen ist.
 * @param modifier Optionaler Modifier.
 */
@Composable
fun KliqNotificationItem(
    text: String,
    timeAgo: String,
    isUnread: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isUnread) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ungelesen-Indikator (kleiner Punkt)
        if (isUnread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        KliqAvatarCircle(size = 44.dp)
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeAgo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
