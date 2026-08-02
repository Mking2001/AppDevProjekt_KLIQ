package com.kliq.app.viewmodel

import com.kliq.app.data.model.RegionSearchResult
import com.kliq.app.data.model.SearchFilterType
import com.kliq.app.ui.model.ClubHighContrastItemState

/**
 * UI-State für den Club- und Regionen-Such-Screen im Kliq High-Contrast Design.
 *
 * @property searchQuery Aktuelle Texteingabe in der Suchleiste.
 * @property activeFilter Ausgewählter Hauptfilter (Alle, Name, Region, Genre).
 * @property selectedRegion Optional gefilterte Region oder Stadt.
 * @property selectedGenre Optional gefiltertes Genre oder Vibe.
 * @property isLoading Flag zur Steuerung von Lade-Platzhaltern.
 * @property clubResults Liste der gefundenen Club-UI-Zustände.
 * @property regionResults Liste der gefundenen Städte und Regionen.
 * @property userLatitude Aktuelle GPS-Breite des Nutzers.
 * @property userLongitude Aktuelle GPS-Länge des Nutzers.
 * @property isGpsActive Flag, ob GPS-Standort für Distanzsuche verfügbar ist.
 * @property errorMessage Fehlerbeschreibung bei fehlgeschlagener Suche.
 * @property recentSearchQueries Verlauf der letzten Suchanfragen.
 */
data class ClubSearchUiState(
    val searchQuery: String = "",
    val activeFilter: SearchFilterType = SearchFilterType.ALL,
    val selectedRegion: String? = null,
    val selectedGenre: String? = null,
    val isLoading: Boolean = false,
    val clubResults: List<ClubHighContrastItemState> = emptyList(),
    val regionResults: List<RegionSearchResult> = emptyList(),
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val isGpsActive: Boolean = false,
    val errorMessage: String? = null,
    val recentSearchQueries: List<String> = emptyList()
)
