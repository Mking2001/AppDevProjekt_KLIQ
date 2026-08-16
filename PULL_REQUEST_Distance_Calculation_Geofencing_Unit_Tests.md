# Pull Request: Core-Logik & Unit-Tests für Distanzberechnung und Geofencing-Überprüfung

**Branch:** `feature/unit-tests-distance-logic` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/unit-tests-distance-logic)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die Kern-Logik sowie eine umfassende Unit-Test-Suite für die **Distanzberechnung (Haversine-Formel)** und die **Geofencing-Überprüfung (`isWithinClubRadius`)** der Kliq Mobile-App gemäß MVVM- und Clean-Architecture-Prinzipien inklusive automatisiertem Test-Runner-Setup für CI- und lokale Entwicklungs-Umgebungen.

Die Funktionalität dient als mathematisches und geodätisches Fundament für:
1. Das **Kliq-Bewertungs- und Anti-Spam-System** (Verifizierung der physischen Anwesenheit am Club-Standort vor Bewertungsabgabe).
2. Die **Map- und Proximity-Features** (Berechnung von Entfernungen zwischen Nutzern und Club-Locations).

---

## 🛠 Umgesetzte Änderungen

### 1. Domain-Modelle & Datenstrukturen
- **`UserDistanceModels.kt`**:
  - `UserDistanceResult`: Kapselt berechnete Rohdistanzen (`rawDistanceMeters`) und Validitätsstatus (`isValid`).
  - `UserLocationSnapshot`: Leichtgewichtiger Snapshot von Benutzerkoordinaten und Zeitstempel.
  - `NearbyUserDistance`: UI-fähiges Datenmodell für Benutzeranzeigen und Entfernungs-Badges.

### 2. Domain UseCase: `CalculateUserDistanceUseCase`
- **Haversine-Distanzberechnung**:
  - `calculateDistanceMeters(startLat, startLng, endLat, endLng)`: Hochpräzise sphärische Berechnung mit Erdmittelradius $R = 6.371.000\text{ m}$.
  - Overloads für `LocationData` und `GpsLocation`.
- **Geofence-Radiusprüfung**:
  - `isWithinClubRadius(...)`: Verifiziert, ob sich ein Nutzer innerhalb eines parametrisierbaren Club-Radius befindet.
  - Unterstützt `toleranceMeters` zum Ausgleich von natürlichem GPS-Jitter und Signalrauschen im Nightlife-Umfeld.
  - Overloads für direkte `Club`- und `GpsLocation`-Entitäten.
- **Koordinaten- & Fehlerbehandlung**:
  - `isValidCoordinate`: Validierung endlicher Zahlen im Bereich $[-90^\circ, +90^\circ]$ für Breitengrade und $[-180^\circ, +180^\circ]$ für Längengrade.
  - Abfangen von `NaN`, `Infinity`, negativen Radien und Nullwerten ohne unkontrollierte Laufzeitfehler.

### 3. Unit-Test-Suite & Performance-Benchmarks
- **`CalculateUserDistanceUseCaseTest.kt`**:
  - *Distanzberechnung*: Bekannte Koordinatenpaare (Brandenburger Tor <-> Alexanderplatz), identische Punkte (0m), Mikrobereich (< 10m: 1.1m, 3.3m, 5.5m, 8.9m), Nahbereich (50m, 200m, 500m), Fernbereich (München, New York, Tokio), Antipoden (Nord-/Südpol, Äquator-Gegenpunkte) und Datumsgrenzen-Überquerung (180. Meridian).
  - *Geofencing*: Zentrum (0m), Punkte innerhalb des Radius, Punkte exakt an der Grenze, Punkte außerhalb des Radius sowie Toleranz-Margen (positiv und negativ).
  - *Edge-Cases*: Ungültige/Extremwerte (Lat/Lon Out-of-Bounds, `NaN`, `±Infinity`), Null-Objektbehandlung, negativer Radius, negativer Toleranzbereich, Radius 0.
  - *Performance*: 1.000 Haversine-Iterationen und 1.000 Geofence-Prüfungen unter 50ms (gemessen: ~3ms).
- **`AntiSpamReviewValidatorTest.kt`**:
  - Erweiterte Tests für Standortabgleiche mit unterschiedlichen Geofence-Radien und Konfidenzwert-Berechnungen.

### 4. Test-Runner Setup & Automatisierung
- **`scripts/run_distance_geofence_tests.bat`**: Windows Batch Runner für lokale Ausführung.
- **`scripts/run_distance_geofence_tests.ps1`**: PowerShell Runner mit Farbhervorhebung und direkten Report-Links.
- **`scripts/run_distance_geofence_tests.sh`**: Unix/Linux/macOS Bash-Skript für CI/CD-Pipelines.
- **`QA_Checklist_Distance_Calculation_Geofencing.md`**: Strukturierte QA-Dokumentation aller Testfälle.

---

## 📋 Commit-Historie

1. `feat(domain): add user distance models and CalculateUserDistanceUseCase with isWithinClubRadius logic`
2. `test(domain): implement comprehensive distance calculation and geofencing unit tests for CalculateUserDistanceUseCase`
3. `test(rating): expand AntiSpamReviewValidator tests with geofence center and custom radius verification`
4. `docs(qa): add Pull Request description and QA checklist for distance calculation and geofencing core`
5. `test(domain): add performance benchmark (< 50ms / 1000 runs) and precision diagnostic assertions`
6. `ci(scripts): add automated test runner scripts for Windows and Unix environments`
7. `docs(qa): update QA checklist and PR documentation with performance metrics and runner guide`

---

## 🧪 Verifizierung

- **Unit-Tests**: Erfolgreiche Ausführung über `scripts\run_distance_geofence_tests.bat` (`BUILD SUCCESSFUL`).
- **Performance**: 1.000 Durchläufe in unter 5ms ausgeführt (Budget: 50ms).
- Vollständige Einhaltung der MVVM-Architektur und Code-Style-Richtlinien.
