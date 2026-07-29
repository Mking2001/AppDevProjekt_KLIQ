# Pull Request: Kapitel 4.5 – Custom Marker Design & Rendering

## 📌 Übersicht

Dieser Pull Request implementiert das **Custom Marker Design** (Kapitel 4.5) für die interaktive Kliq Map-Komponente. Es ermöglicht eine klare visuelle Unterscheidung zwischen Club-/Event-Locations und aktiven Kliq-Nutzern auf der Karte.

## 🚀 Wichtigste Änderungen

1. **Custom Marker Bitmaps & Caching (`MarkerBitmapHelper`)**:
   - **Club Pin Design**: Prägnantes Teardrop-Pin-Design in Kliq Lila (`#6B46C1`) mit kategorie-spezifischem Symbol (z. B. Party/Cocktail) und Live-Event Indicator Badge.
   - **User Marker Design**: Kreisförmiges Avatar-Design mit lila Umrandung, Benutzer-Initialen und Online-Statusanzeige.
   - **Performance Caching**: Integration eines `LruCache<String, BitmapDescriptor>` für flackerfreies Map-Scrolling und Panning.

2. **MVVM State Separation (`MapViewModel`)**:
   - Bereitstellung von getrennten UI-State-Modellen: `ClubMarkerUiState` und `UserMarkerUiState`.
   - Erweiterung von `MapUiState` um `clubMarkers`, `userMarkers` und `selectedUser`.
   - Implementierung von Click-Handlern für beide Marker-Typen (`onUserMarkerClicked`, `onClubMarkerClicked`, `onUserQuickViewDismissed`).

3. **UI Components & View-Integration (`MapScreen` & `UserQuickViewCard`)**:
   - Integration der Custom Bitmap Descriptors in Google Maps Compose.
   - Neu implementiertes `UserQuickViewCard` Overlay mit Quick-Chat-Schnittstelle und Intent-Badges.

4. **Automatische Tests & QA**:
   - Neue Unit-Tests in `MarkerBitmapHelperTest` für Bitmap-Erzeugung und Caching-Verhalten.
   - Erweiterte `MapViewModelTest` Testabdeckung für User- und Club-Marker State-Management.
   - Aktualisiertes `ClubMapMarkersUiTest` für instrumentierte UI-Abnahme.

## 🧪 Test-Abnahme

All Unit Tests under `com.kliq.app.ui.screens.map.*` executed and PASSED.

```bash
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.ui.screens.map.*"
```

Refer to `QA_Checklist_Custom_Map_Markers.md` for full test details.
