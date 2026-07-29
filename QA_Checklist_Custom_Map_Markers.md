# QA-Checkliste: Kapitel 4.5 – Custom Marker Design

**Feature-Branch:** `feature/custom-map-markers`  
**Datum:** 24. Juli 2026  
**Status:** PASSED (Automatisierte Unit-Tests & Manuelle UI-Prüfung)

---

## 🧪 Testergebnisse im Überblick

| Test-Kategorie | Testfall / Beschreibung | Erwartetes Ergebnis | Status |
|---|---|---|---|
| **MVVM State Separation** | `ClubMarkerUiState` & `UserMarkerUiState` getrennt im `MapUiState` | `MapViewModel` stellt strukturierte Datenmodelle für Clubs und Nutzer getrennt bereit. | PASSED |
| **Club Marker Rendering** | Kliq Lila Pin-Design (#6B46C1) mit Event-Badge | Club-Marker zeichnen ein teiltropfenförmiges Lila-Design mit Icon und Live-Event Indicator. | PASSED |
| **User Marker Rendering** | Kreisförmiges Avatar-Design mit lila Umrandung & Status | Nutzer-Marker werden als kreisförmiges Avatar-Icon mit Online-Dot eindeutig unterschieden. | PASSED |
| **Bitmap Caching** | Memory Caching via `LruCache` in `MarkerBitmapHelper` | Bitmaps werden einmalig generiert und wiederverwendet, um Ruckler beim Panning/Zooming zu verhindern. | PASSED |
| **User Marker Tap** | Klick auf Nutzer-Marker | Kamera zentriert sich auf Nutzer-Koordinaten und blendet die `UserQuickViewCard` ein. | PASSED |
| **Club Marker Tap** | Klick auf Club-Marker | Kamera zentriert sich auf Club-Koordinaten und öffnet die `MapQuickViewCard`. | PASSED |
| **Quick-View Selection** | Exklusive Auswahl im `MapUiState` | Das Aktivieren eines Nutzer-Markers blendet eine evtl. geöffnete Club-Karte aus und umgekehrt. | PASSED |
| **Performance & Clean-up** | `clearCache()` Schnittstelle | Ermöglicht kontrolliertes Leeren des Marker-Memory-Caches bei Speicherknappheit. | PASSED |

---

## 🔧 Durchgeführte Unit-Tests

```bash
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.ui.screens.map.*"
```

### Testergebnis:
- `MarkerBitmapHelperTest > testGetClubMarkerBitmap_returnsValidBitmapDescriptor` PASSED
- `MarkerBitmapHelperTest > testGetClubMarkerBitmap_usesCacheForSameCategoryAndEventStatus` PASSED
- `MarkerBitmapHelperTest > testGetUserMarkerBitmap_returnsValidBitmapDescriptor` PASSED
- `MarkerBitmapHelperTest > testGetUserMarkerBitmap_usesCacheForSameInitialAndOnlineStatus` PASSED
- `MarkerBitmapHelperTest > testGetClusterMarkerBitmap_returnsValidBitmapDescriptor` PASSED
- `MarkerBitmapHelperTest > testClearCache_evictsCachedDescriptors` PASSED
- `MapViewModelTest > testInitialStateLoadsClubsAndUsers` PASSED
- `MapViewModelTest > testClubMarkerUiStateMapping_populatesEventDetailsCorrectly` PASSED
- `MapViewModelTest > testUserMarkerClicked_updatesSelectedUserAndCameraPosition` PASSED
- `MapViewModelTest > testClubMarkerClicked_updatesSelectedVenueAndClearsSelectedUser` PASSED
- `MapViewModelTest > testUserQuickViewDismissed_clearsOnlySelectedUser` PASSED
- `MapViewModelTest > testQuickViewDismissed_clearsBothSelectedVenueAndUser` PASSED

---

## 📋 Fazit & Abnahme

Alle technischen und visuellen Anforderungen aus Kapitel 4.5 der Kliq Spezifikation wurden vollständig umgesetzt. Die strikte MVVM-Architektur, das performante Marker-Bitmap-Caching sowie die getrennte Benutzer- und Club-Interaktion wurden nachweislich verifiziert.
