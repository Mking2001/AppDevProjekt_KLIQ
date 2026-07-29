package com.kliq.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kliq.app.data.model.GroupMemberPresence
import com.kliq.app.data.model.GroupMemberRole
import com.kliq.app.data.model.UserStatus
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

private val OnlineBadgeGreen = Color(0xFF22C55E)
private val AwayBadgeAmber = Color(0xFFF59E0B)
private val OfflineBadgeGrey = Color(0xFF6B7280)

@Composable
fun GroupPresenceBadge(
    status: UserStatus,
    modifier: Modifier = Modifier,
    showGlowAnimation: Boolean = true
) {
    val baseColor = when (status) {
        UserStatus.ONLINE -> OnlineBadgeGreen
        UserStatus.AWAY -> AwayBadgeAmber
        UserStatus.OFFLINE -> OfflineBadgeGrey
    }

    if (showGlowAnimation && status == UserStatus.ONLINE) {
        val infiniteTransition = rememberInfiniteTransition(label = "PresenceGlow")
        val glowScale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.45f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "GlowScale"
        )
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "GlowAlpha"
        )

        Box(contentAlignment = Alignment.Center, modifier = modifier) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .scale(glowScale)
                    .clip(CircleShape)
                    .background(baseColor.copy(alpha = glowAlpha))
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .padding(1.5.dp)
                    .clip(CircleShape)
                    .background(baseColor)
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .padding(1.5.dp)
                .clip(CircleShape)
                .background(baseColor)
        )
    }
}

@Composable
fun GroupPresenceHeader(
    title: String,
    onlineCount: Int,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onHeaderClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                GroupPresenceBadge(
                    status = UserStatus.ONLINE,
                    showGlowAnimation = true
                )
                Text(
                    text = "$onlineCount online • Tippen für Teilnehmer",
                    style = MaterialTheme.typography.labelSmall,
                    color = PurplePrimaryLight,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPresenceParticipantSheet(
    title: String,
    onlineCount: Int,
    totalMembersCount: Int,
    members: List<GroupMemberPresence>,
    searchQuery: String,
    currentStatus: UserStatus,
    onSearchQueryChanged: (String) -> Unit,
    onStatusSelected: (UserStatus) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = PurplePrimaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Teilnehmer in $title",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$onlineCount von $totalMembersCount Mitgliedern online",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Schließen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mein Status:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusChip(
                            label = "Online",
                            status = UserStatus.ONLINE,
                            isSelected = currentStatus == UserStatus.ONLINE,
                            onClick = { onStatusSelected(UserStatus.ONLINE) }
                        )
                        StatusChip(
                            label = "Abwesend",
                            status = UserStatus.AWAY,
                            isSelected = currentStatus == UserStatus.AWAY,
                            onClick = { onStatusSelected(UserStatus.AWAY) }
                        )
                        StatusChip(
                            label = "Invisible",
                            status = UserStatus.OFFLINE,
                            isSelected = currentStatus == UserStatus.OFFLINE,
                            onClick = { onStatusSelected(UserStatus.OFFLINE) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Mitglieder suchen…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = PurplePrimaryLight
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.4f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members, key = { it.userId }) { member ->
                    MemberPresenceItem(member = member)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    status: UserStatus,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val badgeColor = when (status) {
        UserStatus.ONLINE -> OnlineBadgeGreen
        UserStatus.AWAY -> AwayBadgeAmber
        UserStatus.OFFLINE -> OfflineBadgeGrey
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) PurplePrimary.copy(alpha = 0.25f)
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (isSelected) PurplePrimaryLight else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) PurplePrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun MemberPresenceItem(
    member: GroupMemberPresence,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.avatarInitial,
                        style = MaterialTheme.typography.titleMedium,
                        color = PurplePrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                }

                GroupPresenceBadge(
                    status = member.status,
                    showGlowAnimation = member.status == UserStatus.ONLINE,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (member.role != GroupMemberRole.MEMBER) {
                        Spacer(modifier = Modifier.width(6.dp))
                        RoleBadge(role = member.role)
                    }
                }

                if (!member.statusMessage.isNullOrBlank() || member.distanceKm != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    val subText = buildString {
                        if (!member.statusMessage.isNullOrBlank()) {
                            append(member.statusMessage)
                        }
                        if (member.distanceKm != null) {
                            if (isNotEmpty()) append(" • ")
                            append("%.1f km entfernt".format(member.distanceKm))
                        }
                    }
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(role: GroupMemberRole) {
    val (label, bg, textClr) = when (role) {
        GroupMemberRole.HOST -> Triple("HOST", PurplePrimary, Color.White)
        GroupMemberRole.MODERATOR -> Triple("MOD", Color(0xFF3B82F6), Color.White)
        GroupMemberRole.VIP -> Triple("VIP", AwayBadgeAmber, Color.Black)
        GroupMemberRole.MEMBER -> Triple("MEMBER", Color.Transparent, Color.Transparent)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textClr,
            fontWeight = FontWeight.Bold
        )
    }
}
