package com.kliq.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.util.ensureMinTouchTarget
import com.kliq.app.util.talkBackDescription

/**
 * Feed-Karte für den Home-Feed.
 *
 * Zeigt Verfasser, Zeitangabe, Motiv, Text und die Interaktionsleiste.
 * Liegt keine Bild-URL vor, wird eine Fallback-Grafik aus Kliq-Farbverlauf,
 * Nachtsymbol und optionalem Location-Namen gerendert, statt eine leere
 * Fläche anzuzeigen.
 *
 * @param userName Anzeigename des Verfassers.
 * @param timeAgo Relative Zeitangabe, zum Beispiel "Vor 15 Min.".
 * @param contentText Textinhalt des Beitrags.
 * @param likeCount Anzahl der Likes.
 * @param isLiked Ob der aktuelle Nutzer den Beitrag geliked hat.
 * @param commentCount Anzahl der Kommentare.
 * @param clubName Optionaler Name der verknüpften Location.
 * @param imageUrl Optionale Bild-URL des Beitrags.
 * @param onLikeClick Callback für den Like-Button.
 * @param onCommentClick Callback für den Kommentar-Button.
 * @param onShareClick Callback für den Teilen-Button.
 */
@Composable
fun KliqFeedCard(
    userName: String,
    timeAgo: String,
    contentText: String,
    modifier: Modifier = Modifier,
    likeCount: Int = 0,
    isLiked: Boolean = false,
    commentCount: Int = 0,
    clubName: String? = null,
    imageUrl: String? = null,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    val likeScale by animateFloatAsState(
        targetValue = if (isLiked) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.4f),
        label = "likeScale"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            FeedCardMedia(imageUrl = imageUrl, clubName = clubName)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = contentText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier
                            .ensureMinTouchTarget(48.dp)
                            .talkBackDescription(
                                if (isLiked) "Gefällt mir entfernen, $likeCount Likes"
                                else "Gefällt mir, $likeCount Likes"
                            )
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isLiked) PurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.scale(likeScale)
                        )
                    }

                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier
                            .ensureMinTouchTarget(48.dp)
                            .talkBackDescription("Kommentare anzeigen, $commentCount Kommentare")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (commentCount > 0) {
                        Text(
                            text = commentCount.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .ensureMinTouchTarget(48.dp)
                            .talkBackDescription("Beitrag teilen")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = if (likeCount == 1) "1 Like" else "$likeCount Likes",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isLiked) FontWeight.Bold else FontWeight.Normal,
                    color = if (isLiked) PurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Motivbereich einer Feed-Karte.
 * Lädt ein Bild, wenn eine URL vorliegt, und rendert andernfalls
 * eine Fallback-Grafik mit Kliq-Farbverlauf und Location-Hinweis.
 */
@Composable
private fun FeedCardMedia(imageUrl: String?, clubName: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        PurplePrimary.copy(alpha = 0.55f),
                        FuchsiaTertiary.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = clubName?.let { "Beitragsbild von $it" } ?: "Beitragsbild",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            return@Box
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.NightsStay,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                modifier = Modifier.size(40.dp)
            )

            if (!clubName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = clubName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
