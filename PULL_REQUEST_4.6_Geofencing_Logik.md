# Pull Request: Kapitel 4.6 - Geofencing-Logik für Club-Radien

**Branch:** `feature/geofencing-club-radii-mvvm` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/geofencing-club-radii-mvvm)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die **Geofencing-Logik für Club-Radien** für die Kliq Mobile-App gemäß **Kapitel 4.6** der technischen Spezifikation nach dem MVVM-Muster.

Die Geofencing-Integration ermöglicht das automatisierte Erkennen von Club-Eintritten und -Austritten über native OS-Schnittstellen (`GeofenceManager` / `GeofencingClient` API). Bei Eintritt in einen Club-Radius wird der Location-Verification-Status reaktiv freigeschaltet, um nachgelagerte Funktionen wie das verifizierte Bewertungssystem (`ReviewVerificationMethod.GPS_GEOFENCE_MATCH`) und die automatisierte "Besucht am"-Historie zu aktivieren.

---

## 🛠 Umgesetzte Änderungen

### 1. Domain-Model & Repository-Architektur
- **`GeofenceModels.kt`**: Definition von `GeofenceTransitionType`, `GeofenceTransitionEvent`, `ClubGeofenceState` und `VisitedClubHistory`.
- **`GeofenceRepository.kt` & `GeofenceRepositoryImpl.kt`**: Reative Verwaltung des Geofence-Zustands (`activeClubState: StateFlow<ClubGeofenceState>`) und der Besuchshistorie (`visitedHistory: StateFlow<List<VisitedClubHistory>>`).

### 2. Service & Native OS Geofencing Integration
- **`GeofenceManager.kt` & `GeofenceManagerImpl.kt`**: Dynamische Registrierung und Unregistrierung virtueller Radien um Clubs via Android `GeofencingClient` API.
- **Datensparsamkeit & System-Integration**: Sortiert Clubs basierend auf der aktuellen Nutzerdistanz und registriert nur die N nächsten Clubs (Standard: Max. 50, strikt unter dem Android System-Limit von 100 Geofences).
- **`GeofenceBroadcastReceiver.kt`**: Native Event-Verarbeitung von `GEOFENCE_TRANSITION_ENTER` und `GEOFENCE_TRANSITION_EXIT` inklusive System-Notification.
- **`AndroidManifest.xml`**: Registrierung des `GeofenceBroadcastReceiver`.

### 3. MVVM ViewModel & Hilt Dependency Injection
- **`GeofenceViewModel.kt`**: Verwaltet den UI-Zustand (`GeofenceUiState`) reaktiv für Composable UI-Komponenten (z. B. Aktivierung des "Bewerten"-Buttons bei verifiziertem Standort).
- **`AppModule.kt` & `RepositoryModule.kt`**: Hilt `@Provides` und `@Binds` Bindungen für `GeofenceManager` und `GeofenceRepository`.

### 4. Tests & QA-Dokumentation
- **Unit-Tests**: `GeofenceRepositoryTest.kt` und `GeofenceViewModelTest.kt` zur Verifizierung aller Zustandstransformationen und Transition-Events.
- **Instrumentierte Tests**: `GeofenceIntegrationTest.kt` zur Validierung der BroadcastReceiver-Anbindung und des End-to-End-Workflows.
- **QA-Checkliste**: [QA_Checklist_Geofencing_Logik.md](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/QA_Checklist_Geofencing_Logik.md).

---

## 📋 Commit-Historie

1. `feat(geofencing): add GeofenceState domain models and GeofenceRepository interface`
2. `feat(geofencing): implement GeofenceManager service for dynamic Android GeofencingClient API registration`
3. `feat(geofencing): implement GeofenceBroadcastReceiver for ENTER/EXIT transition handling and status verification`
4. `feat(geofencing): implement GeofenceViewModel and Hilt DI bindings for reactive UI state`
5. `test(geofencing): add GeofenceViewModelTest, GeofenceIntegrationTest and QA Checklist for Kapitel 4.6`

---

## 🧪 Verifizierung
- `./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.GeofenceViewModelTest"` erfolgreich bestanden.
- `./gradlew testDebugUnitTest --tests "com.kliq.app.data.repository.GeofenceRepositoryTest"` erfolgreich bestanden.
- Vollständige Einhaltung aller MVVM-, Clean-Architecture- und Entwicklungs-Regeln.
