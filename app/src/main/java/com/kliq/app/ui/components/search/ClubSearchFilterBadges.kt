package com.kliq.app.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kliq.app.data.model.SearchFilterType
import com.kliq.app.ui.components.KliqCategoryChip

/**
 * Horizontal scrollbare Filter-Badge Leiste für die Club- und Regionen-Suche.
 *
 * @param activeFilter Aktuell ausgewählter Filter.
 * @param onFilterSelected Callback bei Auswahl eines Filters.
 * @param modifier Der Modifier für die Layout-Komponente.
 */
@Composable
fun ClubSearchFilterBadges(
    activeFilter: SearchFilterType,
    onFilterSelected: (SearchFilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SearchFilterType.values()) { filter ->
            KliqCategoryChip(
                label = filter.label,
                selected = activeFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}
