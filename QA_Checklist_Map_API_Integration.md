# QA Checkliste & Qualitätsprüfung: Kliq Map-API Integration (Kapitel 4.1)

Diese Dokumentation dient der qualitativen Überprüfung und Abnahme der nativen Google Maps SDK Integration sowie der MVVM-Architektur für den zentralen Karten-Screen der Kliq Mobile-App.

---

## 🗺 1. Map-Einbindung & Native SDK Integration

- [x] **Google Maps SDK for Compose Integration:**
  - Die Karte wird über das native Jetpack Compose SDK (`com.google.maps.android:maps-compose:4.3.3`) in Verbindung mit `play-services-maps:18.2.0` gerendert.
  - Vollständiges Lifecycle-Handling durch Deklaration von `GoogleMap`, `MarkerState` und `rememberCameraPositionState`.
- [x] **Interaktive Marker & Steuerelemente:**
  - Rendering von Venue-Markern mit individuellen Positionen (`LatLng`), Titeln, Kategorien und Bewertungen.
  - Interaktives Auswählen von Venues via Marker-Klick und Synchronsierung mit dem `MapQuickViewCard` Overlay.
  - Location FAB zentriert die Karte auf den GPS-Standort und zeigt bei Anfragen den Lade-Zustand an.

---

## 🔐 2. API-Sicherheit (Secret Protection)

- [x] **Kein Hardcoding von API-Keys:**
  - Der Google Maps API Key wird über `local.properties` (oder Umgebungsvariable `MAPS_API_KEY`) geladen.
  - Gradle injiziert den Schlüssel über `manifestPlaceholders["MAPS_API_KEY"]` sicher in das `<meta-data android:name="com.google.android.geo.API_KEY">` Element im `AndroidManifest.xml`.
  - Der Key steht zusätzlich via `BuildConfig.MAPS_API_KEY` zur Verfügung und befindet sich zu keinem Zeitpunkt im versionierten Quellcode.

---

## 🎨 3. Custom Dark-Purple Styling

- [x] **High-Contrast Lila/Dark-Mode Schema:**
  - **`map_style_dark_purple.json`**: Ein maßgeschneiderter JSON Map Style für Google Maps.
  - **Hintergrund & Geometrie**: Dunkle Basisfarbe (`DarkBackground` `#0F0B15` / `#120E1A`).
  - **Straßen & Highways**: Gekennzeichnet mit lila Akzentfarben (`#4C1D95`, `#5B21B6`, `#7C3AED`).
  - **Beschriftungen & Wasser**: Kontrastreiche Violett-Töne (`#EDE9FE`, `#7C3AED`) bei reduzierten, nicht störenden POIs für optimale Lesbarkeit der Club-Marker.

---

## 🏗 4. Architektur & MVVM State Management

- [x] **Saubere Trennung von UI und Karten-Logik:**
  - **`MapViewModel.kt`**: Verwaltet den gesamten Kamera-Status (`CameraPositionStateData`), Karten-Konfigurationen (`MapStyleConfig`), Filter-Auswahl und Venue-Interaktionen.
  - **`MapUiState`**: Immutable Data-Class mit reaktivem `StateFlow`.
  - **View (`MapScreen.kt`)**: Reiner Compose UI Renderer, injiziert über Hilt (`@HiltViewModel`).

---

## 🧪 5. Qualitätssicherung & Test-Abdeckung

- [x] **Unit-Tests (`MapViewModelTest.kt`):**
  - Prüft Initialisierung der Venues & Filter, Filter-Toggle-Logik, Standortzentrierung, Marker-Klicks und Quick-View-Dismissal.
  - **Ergebnis:** `BUILD SUCCESSFUL in 1m 6s` (100% Pass).

---

## 📊 Zusammenfassende Bewertung

| Kriterium | Status | Befund |
| :--- | :---: | :--- |
| **Map-SDK Einbindung** | ✅ Bestanden | Native Google Maps Compose SDK Integration |
| **API-Sicherheit** | ✅ Bestanden | Sicheres Auslesen via local.properties & Gradle ManifestPlaceholders |
| **Custom Styling** | ✅ Bestanden | Maßgeschneiderter Dark-Purple JSON Map Style (`map_style_dark_purple.json`) |
| **MVVM Architektur** | ✅ Bestanden | Entkoppeltes Kamera & UI-State Handling in `MapViewModel` |
| **Testabdeckung** | ✅ Bestanden | Unit-Tests in `MapViewModelTest.kt` erfolgreich bestanden |

> **Fazit:** Die Map-API Integration für Kapitel 4.1 erfüllt alle technischen, optischen und sicherheitsbezogenen Anforderungen vollstens und ist produktionsreif.
