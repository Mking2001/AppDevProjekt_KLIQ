package com.kliq.app.ui.model

enum class StarFilterOption(val label: String, val minRating: Int, val maxRating: Int) {
    ALL("Alle Sterne", 1, 5),
    FIVE_STARS("5 Sterne", 5, 5),
    FOUR_PLUS_STARS("4+ Sterne", 4, 5),
    THREE_PLUS_STARS("3+ Sterne", 3, 5),
    TWO_PLUS_STARS("2+ Sterne", 2, 5),
    ONE_STAR("1 Stern", 1, 1)
}

enum class ReviewSortOption(val label: String) {
    NEWEST_FIRST("Neueste zuerst"),
    OLDEST_FIRST("Älteste zuerst"),
    HIGHEST_RATING("Höchste Bewertung"),
    LOWEST_RATING("Niedrigste Bewertung"),
    MOST_HELPFUL("Hilfreichste")
}

data class ReviewFilterState(
    val selectedStarFilter: StarFilterOption = StarFilterOption.ALL,
    val onlyVerified: Boolean = false,
    val selectedSortOption: ReviewSortOption = ReviewSortOption.NEWEST_FIRST
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedStarFilter != StarFilterOption.ALL) count++
            if (onlyVerified) count++
            if (selectedSortOption != ReviewSortOption.NEWEST_FIRST) count++
            return count
        }

    val isDefault: Boolean
        get() = selectedStarFilter == StarFilterOption.ALL &&
                !onlyVerified &&
                selectedSortOption == ReviewSortOption.NEWEST_FIRST
}
