package com.kliq.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.data.model.ClubEvent
import com.kliq.app.data.model.ClubOffer
import com.kliq.app.data.model.EventCategory
import com.kliq.app.data.model.OfferType
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.viewmodel.ClubEventOfferUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClubEventOfferInfoBlock(
    state: ClubEventOfferUiState,
    onOfferSelected: (ClubOffer) -> Unit,
    onEventSelected: (ClubEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PurplePrimary.copy(alpha = 0.7f),
                        FuchsiaTertiary.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PurplePrimary, FuchsiaTertiary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Events & Specials",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Aktuelle Partys & VIP-Aktionen",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = DarkSurfaceVariant,
                contentColor = PurplePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = PurplePrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "Specials & Deals (${state.offers.size})",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 0) PurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "Partys & Events (${state.events.size})",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 1) PurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurplePrimary, modifier = Modifier.size(32.dp))
                }
            } else if (selectedTabIndex == 0) {
                if (state.offers.isEmpty()) {
                    EmptySectionText("Derzeit keine aktiven Specials für diesen Club.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.offers.forEach { offer ->
                            ClubOfferCard(
                                offer = offer,
                                onClick = { onOfferSelected(offer) }
                            )
                        }
                    }
                }
            } else {
                if (state.events.isEmpty()) {
                    EmptySectionText("Derzeit keine bevorstehenden Events angekündigt.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.events.forEach { event ->
                            ClubEventCard(
                                event = event,
                                onClick = { onEventSelected(event) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubOfferCard(
    offer: ClubOffer,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OfferBadgeChip(offerType = offer.offerType)
                    if (offer.isExclusive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(FuchsiaTertiary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VIP",
                                color = FuchsiaTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                offer.discountPercentage?.let { discount ->
                    Text(
                        text = "-$discount%",
                        color = FuchsiaTertiary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = offer.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = offer.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                offer.discountCode?.let { code ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Code: $code",
                            color = PurplePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } ?: Spacer(modifier = Modifier.width(1.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Details & Einlösen",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = PurplePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClubEventCard(
    event: ClubEvent,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy - HH:mm 'Uhr'", Locale.GERMANY)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .animateContentSize()
            .clickable {
                expanded = !expanded
                onClick()
            }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PurplePrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = event.category.label,
                            color = PurplePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (event.isVipEvent) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(FuchsiaTertiary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "HIGHLIGHT",
                                color = FuchsiaTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = event.price,
                    style = MaterialTheme.typography.labelMedium,
                    color = FuchsiaTertiary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dateFormatter.format(Date(event.startTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Weniger anzeigen" else "Mehr Infos",
                    color = PurplePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun OfferBadgeChip(offerType: OfferType) {
    val (bgColor, textColor) = when (offerType) {
        OfferType.SPECIAL_DEAL -> Pair(PurplePrimary.copy(alpha = 0.2f), PurplePrimary)
        OfferType.VIP_ACTION -> Pair(FuchsiaTertiary.copy(alpha = 0.2f), FuchsiaTertiary)
        OfferType.DRINK_SPECIAL -> Pair(Color(0xFF14B8A6).copy(alpha = 0.2f), Color(0xFF14B8A6))
        OfferType.ENTRY_DISCOUNT -> Pair(Color(0xFFF59E0B).copy(alpha = 0.2f), Color(0xFFF59E0B))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = offerType.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptySectionText(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
