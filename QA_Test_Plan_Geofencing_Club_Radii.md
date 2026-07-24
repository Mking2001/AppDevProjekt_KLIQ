# QA-Test-Plan & Emulator-Anleitung: Schritt 4.6 – Geofencing-Logik für Club-Radien

**Projekt:** Kliq Mobile App  
**Modul:** Geofencing & Location Verification (`GeofenceManager`, `GeofenceBroadcastReceiver`, `GeofenceRepository`, `GeofenceViewModel`)  
**Spezifikation:** Kapitel 4.6 (Geofencing-Logik für Club-Radien)  
**Dokument-Typ:** Qualitätssicherungs-Spezifikation & Emulator-Test-Anleitung  
**Datum:** 24. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Diese Qualitätssicherungs-Spezifikation beschreibt das Test-Szenario und die automatisierte Emulator-Test-Simulation für **Kapitel 4.6 ("Geofencing-Logik für Club-Radien")** der Kliq Mobile-App.

Ziel ist die automatisierte und manuelle Validierung von:
1. **Transition ENTER**: Korrektes Erkennen des Eintreffens in den 50 Meter Club-Radius und Freischalten des verifizierten Bewertungssystems (`isReviewEnabled = true`, `ReviewVerificationMethod.GPS_GEOFENCE_MATCH`).
2. **Aufenthalt & GPS-Jitter**: Stabilität des verifizierten Zustands bei schwankenden GPS-Signalen ohne doppelte Transition-Events.
3. **Transition EXIT**: Automatisches Zurücksetzen des Status und Sperren der geschützten UI-Features beim Verlassen des Radius sowie Eintragung des `exitTimestamp` in die Besucht-Historie.

---

## 💻 2. Test-Umgebung & Emulator-Vorbereitung

### Hardware & Emulator Setup
- **Android Studio Emulator**: Pixel 7 Pro (API 34 / Android 14) oder Pixel 6 (API 33).
- **Mock-Club Konfiguration**:
  - Name: `Club Havana`
  - ID: `club_havana_50m`
  - Breitengrad (Latitude): `46.6240`
  - Längengrad (Longitude): `14.3060`
  - Radius (`geofenceRadiusMeters`): `50.0 m`

### Test-Befehle
```powershell
# Set Environment & Executing Unit / Integration Tests
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"

# Ausführung der simulierten Integrationstest-Suite im Emulator
./gradlew connectedAndroidTest --tests "com.kliq.app.service.GeofenceClubRadiiIntegrationTest"
```

---

## 🧪 3. Schritt-für-Schritt Test-Szenario für den Emulator

### 🔹 Schritt 1: Annäherung & Eintritt in den 50m Club-Radius

1. **Aktion via Emulator Extended Controls (Location Tab)**:
   - Setze die GPS-Koordinaten des Emulators auf:  
     - Latitude: `46.62401`  
     - Longitude: `14.30601`  
     (Entfernung zum Club-Zentrum: ca. 1.5 Meter $\rightarrow$ innerhalb des 50m-Radius).
   - Klicke im Emulator auf **"Send"**.

2. **System-Verhalten & Assertions**:
   - The native OS API / BroadcastReceiver detects `GEOFENCE_TRANSITION_ENTER`.
   - `GeofenceRepositoryImpl` processes transition for `club_havana_50m`.
   - `activeClubState.isInsideGeofence` changes to `true`.
   - `GeofenceViewModel.uiState.value.isReviewEnabled` changes to `true`.
   - The UI "Bewerten" (Review) button switches from disabled/unverified to **active & verified** with purple glow indicator (`#7C3AED`).
   - A new record is added to `VisitedClubHistory` with `entryTimestamp = now` and `exitTimestamp = null`.

---

### 🔹 Schritt 2: Aufenthalt im Radius (GPS-Jitter)

1. **Aktion via Emulator Extended Controls**:
   - Simuliere typische GPS-Signal-Schwankungen durch sequenzielles Senden folgender Koordinaten:
     - Jitter-Point A: `46.62403, 14.30604` (Distanz ~4 Meter)
     - Jitter-Point B: `46.62398, 14.30596` (Distanz ~5 Meter)
     - Jitter-Point C: `46.62405, 14.30608` (Distanz ~8 Meter)

2. **System-Verhalten & Assertions**:
   - Der Status bleibt ununterbrochen auf `isInsideGeofence = true` und `isReviewEnabled = true`.
   - Der "Bewerten"-Button bleibt durchgehend aktiviert.
   - Es werden **keine** doppelten `ENTER`-Events gefeuert.
   - Die `VisitedClubHistory` enthält weiterhin genau 1 aktiven Eintrag.

---

### 🔹 Schritt 3: Verlassen des Club-Radius

1. **Aktion via Emulator Extended Controls**:
   - Setze die GPS-Koordinaten auf eine Position weit außerhalb des Club-Radius:  
     - Latitude: `46.6350`  
     - Longitude: `14.3200`  
     (Entfernung zum Club-Zentrum: > 1.5 Kilometer).
   - Klicke im Emulator auf **"Send"**.

2. **System-Verhalten & Assertions**:
   - The native OS API / BroadcastReceiver detects `GEOFENCE_TRANSITION_EXIT`.
   - `GeofenceRepositoryImpl` resets active state: `isInsideGeofence = false`, `activeClubId = null`.
   - `GeofenceViewModel.uiState.value.isReviewEnabled` changes to `false`.
   - The UI "Bewerten" button is immediately locked/disabled for location-based reviews.
   - The active record in `VisitedClubHistory` receives a valid `exitTimestamp`.

---

## 📊 4. Automatisierte Test-Protokollierung & Execution Matrix

| Test-Klasse | Test-Methode | Geprüfte Kriterien | Status |
|:---|:---|:---|:---:|
| `GeofenceClubRadiiIntegrationTest` | `testCompleteGeofenceWorkflow_ApproachJitterExit` | 50m Radius Enter, GPS Jitter Stability, Exit Reset, UI State Assertions | **PASSED** |
| `GeofenceRepositoryTest` | `handleGeofenceTransition_ENTER_updatesActiveClubAndVisitedHistory` | Enter State, VisitedHistory Creation | **PASSED** |
| `GeofenceRepositoryTest` | `handleGeofenceTransition_EXIT_resetsActiveStateAndUpdateHistoryExitTimestamp` | Exit State, Exit Timestamp Completion | **PASSED** |
| `GeofenceViewModelTest` | `observeGeofenceState_updatesUiStateWhenRepositoryEmitsActiveGeofence` | Reactive UI State `isReviewEnabled` Toggle | **PASSED** |

---

## 🏆 5. Abnahme-Kriterien & QS-Freigabe

- [x] Test-Szenario Schritt 1 (Annäherung & Eintritt), Schritt 2 (Aufenthalt & GPS-Jitter) und Schritt 3 (Verlassen) vollständig abgedeckt.
- [x] Ausführbarer automatisierter Integrationstest `GeofenceClubRadiiIntegrationTest.kt` vorhanden.
- [x] Eindeutige Assertions auf `isInsideGeofence`, `isReviewEnabled`, `activeClubId` und `exitTimestamp`.
- [x] Striktes Befolgen der Null-Transparenz-Regel (keinerlei KI-Hinweise in Skripten oder Dokumentation).
