package com.kliq.app.data.model

/**
 * Filter-Typen für die erweiterte Club- und Regionen-Suche.
 */
enum class SearchFilterType(val label: String) {
    ALL("Alle"),
    NAME("Nach Name"),
    REGION("Nach Region/Stadt"),
    GENRE("Nach Genre/Vibe")
}

/**
 * Datenmodell für ein Regions- / Stadt-Suchergebnis.
 *
 * @property regionName Name der Stadt oder geografischen Region.
 * @property clubCount Anzahl der verfügbaren Clubs in dieser Region.
 * @property isCity Flag, ob es sich um eine Stadt oder Region handelt.
 */
data class RegionSearchResult(
    val regionName: String,
    val clubCount: Int,
    val isCity: Boolean = true
)

/**
 * Ergebnis-Wrapper für die Club-Suche inklusive Distanzberechnung.
 *
 * @property club Das gefundene Club-Domainmodell.
 * @property distanceKm Relevante GPS-Entfernung in Kilometern (falls Standort vorhanden).
 * @property isMatchedByRegion Kennzeichnung, ob der Match über eine Region erfolgte.
 */
data class ClubSearchResult(
    val club: Club,
    val distanceKm: Double? = null,
    val isMatchedByRegion: Boolean = false
)
