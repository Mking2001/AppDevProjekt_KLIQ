package com.kliq.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.ui.model.ReviewFilterState
import com.kliq.app.ui.model.ReviewSortOption
import com.kliq.app.ui.model.StarFilterOption

@Composable
fun ReviewFilterSection(
    filterState: ReviewFilterState,
    onStarFilterSelected: (StarFilterOption) -> Unit,
    onSortOptionSelected: (ReviewSortOption) -> Unit,
    onVerifiedOnlyToggled: (Boolean) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkCardBg = Color(0xFF1E1B2E)
    val accentPurple = Color(0xFF7C4DFF)
    val verifiedGreen = Color(0xFF00E676)
    val outlineBorder = Color(0xFF2C2B3D)

    var isSortDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(darkCardBg)
            .border(1.dp, outlineBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = accentPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bewertungen filtern",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (filterState.activeFilterCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accentPurple)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${filterState.activeFilterCount}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !filterState.isDefault,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                TextButton(
                    onClick = onResetFilters,
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Zurücksetzen",
                        tint = Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Zurücksetzen",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                val isSortActive = filterState.selectedSortOption != ReviewSortOption.NEWEST_FIRST
                val sortBg by animateColorAsState(
                    targetValue = if (isSortActive) accentPurple else Color(0xFF2C2B3D),
                    label = "SortBgColor"
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(sortBg)
                        .clickable { isSortDropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sortieren",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = filterState.selectedSortOption.label,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                DropdownMenu(
                    expanded = isSortDropdownExpanded,
                    onDismissRequest = { isSortDropdownExpanded = false },
                    modifier = Modifier.background(Color(0xFF252136))
                ) {
                    ReviewSortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.label,
                                    color = if (filterState.selectedSortOption == option) accentPurple else Color.White,
                                    fontWeight = if (filterState.selectedSortOption == option) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortOptionSelected(option)
                                isSortDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            val verifiedBg by animateColorAsState(
                targetValue = if (filterState.onlyVerified) verifiedGreen.copy(alpha = 0.25f) else Color(0xFF2C2B3D),
                label = "VerifiedBgColor"
            )
            val verifiedBorder by animateColorAsState(
                targetValue = if (filterState.onlyVerified) verifiedGreen else Color.Transparent,
                label = "VerifiedBorderColor"
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(verifiedBg)
                    .border(1.dp, verifiedBorder, RoundedCornerShape(20.dp))
                    .clickable { onVerifiedOnlyToggled(!filterState.onlyVerified) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (filterState.onlyVerified) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = verifiedGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "Nur Verifizierte",
                    color = if (filterState.onlyVerified) verifiedGreen else Color.White,
                    fontSize = 12.sp,
                    fontWeight = if (filterState.onlyVerified) FontWeight.Bold else FontWeight.Medium
                )
            }

            StarFilterOption.values().forEach { starOption ->
                val isSelected = filterState.selectedStarFilter == starOption
                val chipBg by animateColorAsState(
                    targetValue = if (isSelected) accentPurple else Color(0xFF2C2B3D),
                    label = "StarChipBg"
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(chipBg)
                        .clickable { onStarFilterSelected(starOption) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (starOption != StarFilterOption.ALL) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = starOption.label,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
