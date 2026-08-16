# Pull Request: Core-Logik & Unit-Tests für Distanzberechnung und Geofencing-Überprüfung

**Branch:** `feature/unit-tests-distance-logic` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/unit-tests-distance-logic)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die Kern-Logik sowie eine umfassende Unit-Test-Suite für die **Distanzberechnung (Haversine-Formel)** und die **Geofencing-Überprüfung (`isWithinClubRadius`)** der Kliq Mobile-App gemäß MVVM- und Clean-Architecture-Prinzipien.

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

### 3. Unit-Test-Suite & Testabdeckung
- **`CalculateUserDistanceUseCaseTest.kt`**:
  - *Distanzberechnung*: Bekannte Koordinatenpaare (Brandenburger Tor <-> Alexanderplatz), identische Punkte (0m), Mikrobereich (< 10m: 1.1m, 3.3m, 5.5m, 8.9m), Nahbereich (50m, 200m, 500m), Fernbereich (München, New York, Tokio), Antipoden (Nord-/Südpol, Äquator-Gegenpunkte) und Datumsgrenzen-Überquerung (180. Meridian).
  - *Geofencing*: Zentrum (0m), Punkte innerhalb des Radius, Punkte exakt an der Grenze, Punkte außerhalb des Radius sowie Toleranz-Margen (positiv und negativ).
  - *Edge-Cases*: Ungültige/Extremwerte (Lat/Lon Out-of-Bounds, `NaN`, `±Infinity`), Null-Objektbehandlung, negativer Radius, negativer Toleranzbereich, Radius 0.
- **`AntiSpamReviewValidatorTest.kt`**:
  - Erweiterte Tests für Standortabgleiche mit unterschiedlichen Geofence-Radien und Konfidenzwert-Berechnungen.
- **`QA_Checklist_Distance_Calculation_Geofencing.md`**: Strukturierte QA-Dokumentation aller Testfälle.

---

## 📋 Commit-Historie

1. `feat(domain): add user distance models and CalculateUserDistanceUseCase with isWithinClubRadius logic`
2. `test(domain): implement comprehensive distance calculation and geofencing unit tests for CalculateUserDistanceUseCase`
3. `test(rating): expand AntiSpamReviewValidator tests with geofence center and custom radius verification`
4. `docs(qa): add Pull Request description and QA checklist for distance calculation and geofencing core`

---

## 🧪 Verifizierung

- **Unit-Tests**: Erfolgreiche Ausführung mit `./gradlew testDebugUnitTest --tests com.kliq.app.domain.usecase.CalculateUserDistanceUseCaseTest --tests com.kliq.app.data.util.AntiSpamReviewValidatorTest` (`BUILD SUCCESSFUL`).
- Vollständige Einhaltung der MVVM-Architektur und Code-Style-Richtlinien.
