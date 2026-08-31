package com.kliq.app.data.model

enum class SearchFilterType(val label: String) {
    ALL("Alle"),
    NAME("Nach Name"),
    REGION("Nach Region/Stadt"),
    GENRE("Nach Genre/Vibe")
}

data class RegionSearchResult(
    val regionName: String,
    val clubCount: Int,
    val isCity: Boolean = true
)

data class ClubSearchResult(
    val club: Club,
    val distanceKm: Double? = null,
    val isMatchedByRegion: Boolean = false
)
