# QA Checklist: Kapitel 9.7 – Batterie-Verbrauchs-Optimierung (GPS)

## Testumgebung & Build-Prüfung
- [x] Gradle Build & Kompilierung: `./gradlew assembleDebug` fehlerfrei
- [x] Feature Branch: `feature/gps-battery-optimization`
- [x] Hilt Dependency Injection: Saubere Bereitstellung aller Location-Module ohne zyklische Abhängigkeiten

---

## QS-Prüfpunkte

### 1. Stepped Tracking Modes & Power-Policies
- [x] **High-Accuracy Mode**: `PRIORITY_HIGH_ACCURACY` wird ausschließlich bei aktiver Verifizierung (Check-In, QR-Scan, Geofence-Prüfung) aktiviert.
- [x] **Balanced Ambient Mode**: Normalbetrieb auf der Party-Map läuft mit 60–120s Intervall und 50m Displacement.
- [x] **Idle Passive Mode**: Ruhezustand und Hintergrundbetrieb schalten auf `PRIORITY_PASSIVE` mit Geofence-gestützten Triggern.

### 2. Adaptive Logik & Stillstandserkennung (Stationary Detection)
- [x] **Movement Sensor**: Bei Geschwindigkeit < 0,5 m/s und Distanzdelta < 15m schaltet die App nach 2 Fixes automatisch auf Drosselung (*Stationär*).
- [x] **Movement Resume**: Bei Fortbewegung (>15m Verschiebung) wird der Balanced-Modus sofort wiederhergestellt.
- [x] **Burst Countdown**: High-Accuracy-Bursts zählen 30 Sekunden herunter und fallen automatisch zurück.
- [x] **Cancel Action**: Vorzeitiges Abbrechen des Bursts setzt den Modus unmittelbar zurück.

### 3. Lifecycle- & Background-Verhalten
- [x] **App Minimization (`onPause` / `onStop`)**: Kontinuierliche GPS-Abfragen werden gestoppt; Hardware-WakeLocks werden freigegeben.
- [x] **Service Dynamic Reconfiguration**: `BackgroundLocationService` passt `LocationRequest` ohne Neustart zur Laufzeit an.
- [x] **ViewModel Cleanup (`onCleared`)**: Alle aktiven Subskriptionen und Coroutines werden terminiert.

### 4. High-Contrast Dark/Purple UI
- [x] **Design-System**: Wahrung der Kliq-Farben (`#0D0B14`, `#181224`, `#2D2240`, `#7C4DFF`, `#A855F7`, `#00E676`, `#FFAB00`, `#00E5FF`, `#FF6D00`).
- [x] **Segmented Mode Switcher**: Reaktive Umschaltung zwischen *Eco/Idle*, *Balanced* und *High-Acc*.
- [x] **Burst Banner**: Anzeige der verbleibenden Restzeit mit Schnellabbruch-Button.
- [x] **Telemetrie-Badge**: Statusanzeige für *"Stationär (Drosselung)"* vs. *"Adaptiv (>50m)"*.

---

## Verifizierungsergebnis
Alle automatisierten Unit- und Integrationstests wurden mit 100% Erfolgsquote ausgeführt (`test_gps_battery_optimization_9.7.ps1`).
