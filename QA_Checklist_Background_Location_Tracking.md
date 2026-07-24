# QA Checkliste & Qualitätsprüfung: Kliq Live-Nutzer-Standort Tracking im Hintergrund (Kapitel 4.3)

Diese Dokumentation dient der Qualitätssicherung und Abnahme des **Live-Nutzer-Standort Trackings im Hintergrund** nach den Bewertungskoeffizienten der Kliq Mobile-App.

---

## 🏛 1. Detaillierte Analyse nach Architektur- & Qualitätskriterien

### A. Architektur & MVVM-Konformität
- **Foreground Service Entkopplung (`BackgroundLocationService.kt`):**
  - Der Standort-Tracking-Dienst läuft als dedizierter Android Foreground Service mit der Deklaration `android:foregroundServiceType="location"`.
  - Der Service wird sauber über Intents (`ACTION_START`, `ACTION_STOP`) über `LocationRepository` gesteuert.
- **LocationRepository Layer (`LocationRepository.kt` / `LocationRepositoryImpl.kt`):**
  - Kapselt die Steuerung des Hinein- und Herausfallens aus dem Tracking sowie die Aufzeichnung aller Standortpunkte in die lokale Room-Datenbank.
  - Stellt reaktive Kotlin `StateFlow<LocationData?>` und `Flow<List<LocationEntity>>` für ViewModels bereit.
- **Deklaratives UI & ViewModel (`LocationTrackingViewModel.kt` / `BackgroundLocationTrackingCard.kt`):**
  - `LocationTrackingViewModel` bindet Repository-Flows und Berechtigungszustände zusammen.
  - `BackgroundLocationTrackingCard` visualisiert den Live-Tracking-Zustand, GPS-Telemetrie und ein pulsierendes Status-Badge im Kliq High-Contrast-Design.

### B. Akku-Effizienz & Adaptiver Intervall-Algorithmus
- **Batterieschonendes Tracking:**
  - Der FusedLocationProviderClient verwendet ein adaptives Intervall mit `Priority.PRIORITY_BALANCED_POWER_ACCURACY`.
  - Mindestdistanz-Schwelle (Displacement Filter) von **50 Metern** (`setMinUpdateDistanceMeters(50f)`).
  - Dynamisches Intervall von **60 Sekunden (1 Min)** bei aktiver Bewegung bis zu **300 Sekunden (5 Min)** im Ruhezustand.

### C. OS-Konformität & Berechtigungs-Management
- **Android 10+ / 14+ Konformität (`ACCESS_BACKGROUND_LOCATION`):**
  - Deklaration aller erforderlichen Berechtigungen (`ACCESS_BACKGROUND_LOCATION`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`, `POST_NOTIFICATIONS`) in der `AndroidManifest.xml`.
  - `PermissionManager.checkBackgroundLocationPermission()` prüft auf Android 10+ explizit den "Immer zulassen"-Status. Bei Fehlen der Berechtigung zeigt die App eine Warnkarte mit Direktlink in die Android-Systemeinstellungen.
- **Kliq High-Contrast Foreground Notification:**
  - Dauerhafte Benachrichtigung mit Channel `kliq_location_channel` (Akzentfarbe `#7C4DFF`), Titel *"Kliq Live-Standort aktiv"* und Hinweistext zur aktiven Standortfreigabe für Nightlife-Features.

---

## 📋 2. Checkliste für die Abnahme

- [x] **Manifest-Deklaration:** `ACCESS_BACKGROUND_LOCATION`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`, `POST_NOTIFICATIONS` sowie `<service android:name=".service.BackgroundLocationService" android:foregroundServiceType="location" />`.
- [x] **Room-Integration:** `LocationEntity`, `LocationDao` mit `insertLocation`, `getLatestLocation`, `getRecentLocations` und Migration `MIGRATION_9_10` auf DB Version 10.
- [x] **Service & Notification:** `BackgroundLocationService` mit persistentem Notification-Handler im Kliq-Design und `PendingIntent` zur `MainActivity`.
- [x] **Batterieschonung:** Adaptiver Intervall-Algorithmus (> 50m Distanzfilter, 1–5 Min Zeitintervall).
- [x] **Repository & Hilt:** `LocationRepository` und `LocationRepositoryImpl` mit Hilt `@Binds` und `@Provides` Binding.
- [x] **MVVM State-Management:** `LocationTrackingViewModel` liefert unteilbaren `LocationTrackingUiState`.
- [x] **High-Contrast UI:** `BackgroundLocationTrackingCard.kt` Compose Komponente im Dark/Purple Theme.
- [x] **Automatisierte Abdeckung:** Unit-Tests (`LocationRepositoryTest.kt`, `LocationTrackingViewModelTest.kt`) erfolgreich bestanden (`BUILD SUCCESSFUL`).

---

## 📊 3. Evaluierungsergebnis

| Prüfkriterium | Status | Bemerkung |
| :--- | :---: | :--- |
| **Architektur & MVVM** | ✅ Bestanden | Entkopplung über Foreground Service, Repository & StateFlow. |
| **Batterieschonung** | ✅ Bestanden | Adaptiver 50m Distanzfilter + 1-5 Min FusedLocationProviderClient Intervall. |
| **OS- & Perm-Konformität** | ✅ Bestanden | Vollständiges Background-Location Check & Android 14 Foreground Service Type. |
| **UI & UX Integration** | ✅ Bestanden | Kliq High-Contrast Card mit Puls-Effekt & Deep-Link zu Systemeinstellungen. |
| **Test-Abdeckung** | ✅ Bestanden | `LocationRepositoryTest` & `LocationTrackingViewModelTest` 100% grün. |
