# Pull Request: Kapitel 4.1 - Map-API Integration (Google Maps SDK / Compose)

**Branch:** `feature/map-api-integration-mvvm` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/map-api-integration-mvvm)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request integriert das **Google Maps SDK for Compose** auf dem zentralen Karten-Screen der Kliq Mobile-App gemäß Kapitel 4.1 der technischen Spezifikation.

---

## 🛠 Umgesetzte Änderungen

### 1. API-Konfiguration & Sicherheit
- **Sicheres API Key Handling**: `MAPS_API_KEY` wird aus `local.properties` (oder Umgebungsvariablen) eingelesen und über Gradle `manifestPlaceholders["MAPS_API_KEY"]` in die `AndroidManifest.xml` injiziert. Keinerlei Hardcoding im Quellcode.
- **Dependencies**: Hinzugefügt `com.google.maps.android:maps-compose:4.3.3` und `play-services-maps:18.2.0` in `build.gradle.kts`.

### 2. Custom Dark-Purple Styling
- **`map_style_dark_purple.json`**: Maßgeschneidertes Google Maps JSON Map Style, das exakt zum Lila/Dark-Mode Theme von Kliq (`#0F0B15`, `#7C3AED`, `#1A1523`) passt.

### 3. MVVM-Architektur & State Management
- **`MapConfig.kt`**: Domain-Modell für Kamera-Positionierung und Stylingeinstellungen.
- **`MapViewModel.kt` & `MapUiState`**: Reaktiv verwalteter Zustand für Kamera-Position, Filter, ausgewählte Markers und GPS-Standort-Zentrierung.

### 4. Native Map UI (`MapScreen.kt`)
- Einbindung von `GoogleMap`, `Marker` (mit `MarkerState` & Venue-Metadaten), `rememberCameraPositionState`, Filter-Chips, Location FAB und Venue Bottom Sheet.

### 5. Tests & QA-Dokumentation
- **Unit-Tests**: `MapViewModelTest.kt` zur Verifizierung aller Kamera-, Filter- und Marker-Interaktionen (`BUILD SUCCESSFUL in 1m 6s`).
- **QA-Checkliste**: [QA_Checklist_Map_API_Integration.md](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/QA_Checklist_Map_API_Integration.md).

---

## 📋 Commit-Historie

1. `feat(map): configure secure Maps API key placeholders and add Google Maps SDK dependencies`
2. `feat(map): add custom dark-purple JSON map style and MapConfig domain models`
3. `feat(map): implement MapViewModel and MapUiState for camera positioning and map event handling`
4. `feat(map): integrate Google Maps Compose SDK layout with custom JSON styling and venue markers`
5. `test(map): add MapViewModelTest and QA Checklist for Kapitel 4.1 Map-API integration`

---

## 🧪 Verifizierung
- `./gradlew testDebugUnitTest --tests "com.kliq.app.ui.screens.map.MapViewModelTest"` erfolgreich bestanden.
- Keinerlei KI-Hinweise in Code oder Commits vorhanden.
