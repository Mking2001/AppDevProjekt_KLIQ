# Pull Request: Kapitel 4.3 - Live-Nutzer-Standort Tracking im Hintergrund

**Branch:** `feature/background-location-tracking` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/background-location-tracking)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert das **Live-Nutzer-Standort Tracking im Hintergrund** für die Kliq Mobile-App gemäß Kapitel 4.3 der technischen Spezifikation nach dem MVVM-Muster.

---

## 🛠 Umgesetzte Änderungen

### 1. Datenmodell & Room-Datenbank (Version 10)
- **`LocationData.kt`**: Domain Value Object für GPS-Koordinaten, Genauigkeit, Geschwindigkeit und Zeitstempel.
- **`LocationEntity.kt` & `LocationDao.kt`**: Room-Entität `user_locations` und DAO zur Speicherung und reaktiven Abfrage von Standort-Trackpunkten.
- **`KliqDatabase.kt` & `DatabaseMigrations.kt`**: Erhöhung der DB-Version auf 10 und Implementierung von `MIGRATION_9_10`.

### 2. Berechtigungs-Management & Service
- **`PermissionManager.kt`**: Ergänzung von `checkBackgroundLocationPermission()` zur Auswertung von `ACCESS_BACKGROUND_LOCATION` unter Android 10+ (API 29+).
- **`BackgroundLocationService.kt`**: Native Android Foreground Service Klasse (`foregroundServiceType="location"`) mit persistenter Benachrichtigung im Kliq High-Contrast Lila-Design (`#7C4DFF` / `#0F0B15`).
- **Batterieschonender Intervall-Algorithmus**: Implementierung eines adaptiven FusedLocationProviderClient-Requests (50m Distanzfilter, 1 Min aktives Intervall bis 5 Min Ruhezustand-Delay).
- **`AndroidManifest.xml`**: Deklaration aller erforderlichen Berechtigungen und des Foreground Service.

### 3. Repository Layer & Dependency Injection
- **`LocationRepository.kt` & `LocationRepositoryImpl.kt`**: Schnittstelle und Implementierung zur Steuerung des Dienstes, Bereitstellung von `StateFlow<LocationData?>` und `Flow<List<LocationEntity>>` sowie Room-Persistierung.
- **`AppModule.kt` & `RepositoryModule.kt`**: Hilt-Bindings für `LocationDao` und `LocationRepository`.

### 4. ViewModels & Compose UI
- **`LocationTrackingViewModel.kt`**: MVVM State-Management für den Tracking-Status, GPS-Telemetrie, Berechtigungsprüfungen und Verlaufsbereinigung.
- **`BackgroundLocationTrackingCard.kt`**: High-Contrast Compose UI Karte im Dark/Lila Kliq Design mit pulsierendem Live-Status-Badge, Telemetrieanzeige und Direct Deep-Link zu den Systemeinstellungen bei fehlenden Hintergrundberechtigungen.

### 5. Tests & Qualitätssicherung
- **Unit-Tests**: `LocationRepositoryTest.kt` und `LocationTrackingViewModelTest.kt` (`BUILD SUCCESSFUL in 18s`).
- **QA-Dokumentation**: [QA_Checklist_Background_Location_Tracking.md](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/QA_Checklist_Background_Location_Tracking.md).

---

## 📋 Commit-Historie

1. `feat(location): add LocationData model, Room LocationEntity, LocationDao and Migration 9 to 10`
2. `feat(location): upgrade PermissionManager for ACCESS_BACKGROUND_LOCATION check`
3. `feat(location): implement LocationRepository and Hilt bindings for background tracking`
4. `feat(location): implement BackgroundLocationService with battery-adaptive interval tracking and Kliq notification`
5. `feat(location): implement LocationTrackingViewModel and BackgroundLocationTrackingCard UI`
6. `test(location): add LocationRepositoryTest, LocationTrackingViewModelTest and QA checklist for Kapitel 4.3`

---

## 🧪 Verifizierung
- `./gradlew testDebugUnitTest --tests "com.kliq.app.data.repository.LocationRepositoryTest" --tests "com.kliq.app.viewmodel.LocationTrackingViewModelTest"` erfolgreich bestanden.
- Keinerlei KI-Hinweise in Code, Kommentaren oder Commits vorhanden (Null-Transparenz-Regel).
