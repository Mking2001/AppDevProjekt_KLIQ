# QA-Checkliste: Core-Distanzberechnung & Geofencing-Überprüfung

## 🎯 Testumfang
Diese QA-Checkliste dokumentiert die Verifizierung der Kernlogik für Distanzberechnungen (Haversine-Formel) und Geofence-Radiusprüfungen (`isWithinClubRadius`) für die Kliq Mobile-App.

---

## 🧪 Testfälle & Abdeckungsmatrix

### 1. Haversine-Distanzberechnung
| ID | Testfall | Eingabedaten | Erwartetes Ergebnis | Status |
|---|---|---|---|---|
| TC-DIST-01 | Bekannte Referenzkoordinaten | Brandenburger Tor (52.516275, 13.377704) zu Alexanderplatz (52.521918, 13.413215) | Distanz ~2480 m (±50m Toleranz) | ✅ Bestanden |
| TC-DIST-02 | Identische Koordinaten | (52.5200, 13.4050) zu (52.5200, 13.4050) | Exakt 0.0 m | ✅ Bestanden |
| TC-DIST-03 | Mikrobereich (< 10 Meter) | Distanzen von 1.1m, 3.3m, 5.5m, 8.9m | Exakte Zentimeter-/Meterauflösung | ✅ Bestanden |
| TC-DIST-04 | Nahbereich (50m - 500m) | 50m, 200m, 500m Offsets | Lineare, verzerrungsfreie Distanzen | ✅ Bestanden |
| TC-DIST-05 | Fernbereich (Kontinental/Global) | Berlin nach München (504 km), New York (6385 km), Tokio (8918 km) | Präzise sphärische Distanzen | ✅ Bestanden |
| TC-DIST-06 | Antipoden | Nordpol (90, 0) zu Südpol (-90, 0) / Äquator (0, 0) zu (0, 180) | Halber Erdumfang (~20.015 km) | ✅ Bestanden |
| TC-DIST-07 | Datumsgrenze (180. Meridian) | (0.0, 179.9) zu (0.0, -179.9) | Kürzester Großkreisbogen (~22.2 km) | ✅ Bestanden |
| TC-DIST-08 | Objekt-Overloads | `LocationData` und `GpsLocation` Instanzen | Korrekte Typverarbeitung | ✅ Bestanden |

---

### 2. Geofencing-Überprüfung (`isWithinClubRadius`)
| ID | Testfall | Eingabedaten | Erwartetes Ergebnis | Status |
|---|---|---|---|---|
| TC-GEO-01 | Club-Zentrum | User an exakten Clubkoordinaten (Distanz 0m, Radius 200m) | `true` | ✅ Bestanden |
| TC-GEO-02 | Innerhalb des Radius | User 50m vom Club entfernt (Radius 200m) | `true` | ✅ Bestanden |
| TC-GEO-03 | Exakte Radiusgrenze | User exakt an der Peripherie (Distanz == Radius) | `true` | ✅ Bestanden |
| TC-GEO-04 | Außerhalb des Radius | User 300m entfernt (Radius 200m, Toleranz 0m) | `false` | ✅ Bestanden |
| TC-GEO-05 | Innerhalb Toleranzband | User 205m entfernt (Radius 200m, Toleranz 15m) | `true` | ✅ Bestanden |
| TC-GEO-06 | Außerhalb Toleranzband | User 225m entfernt (Radius 200m, Toleranz 15m) | `false` | ✅ Bestanden |
| TC-GEO-07 | Domain-Objekt Integration | `isWithinClubRadius(userLocation, club)` mit `Club`-Entität | `true` (nah) / `false` (fern) | ✅ Bestanden |

---

### 3. Edge-Cases & Fehlerbehandlung
| ID | Testfall | Eingabedaten | Erwartetes Ergebnis | Status |
|---|---|---|---|---|
| TC-EDGE-01 | Breitengrad außerhalb Bereichs | Lat = 90.0001, Lat = -95.0 | Rückgabe `null` / `false` | ✅ Bestanden |
| TC-EDGE-02 | Längengrad außerhalb Bereichs | Lon = 180.0001, Lon = -181.0 | Rückgabe `null` / `false` | ✅ Bestanden |
| TC-EDGE-03 | Nicht-finite Werte | `Double.NaN`, `Double.POSITIVE_INFINITY`, `Double.NEGATIVE_INFINITY` | Rückgabe `null` / `false` | ✅ Bestanden |
| TC-EDGE-04 | Exakte Geogrenzen | Lat ±90.0, Lon ±180.0 | Erfolgreiche Berechnung | ✅ Bestanden |
| TC-EDGE-05 | Nullable Parameter | `null` Koordinaten / `null` Entitäten | Rückgabe `false` ohne Crash | ✅ Bestanden |
| TC-EDGE-06 | Negativer Radius / Toleranz | Radius = -10m oder Toleranz = -5m | Rückgabe `false` | ✅ Bestanden |
| TC-EDGE-07 | Radius = 0 | User am Zentrum (0m) vs. 1m entfernt | `true` bei 0m, `false` bei >0m | ✅ Bestanden |
| TC-EDGE-08 | UserDistanceResult | Validierung von Statusflag und Distanzwert | `isValid = true/false` konsistent | ✅ Bestanden |

---

### 4. Performance-Benchmark & Precision-Diagnostik
| ID | Testfall | Benchmark-Kriterium | Gemessene Ausführungszeit | Status |
|---|---|---|---|---|
| TC-PERF-01 | 1.000 Haversine-Berechnungen | Ausführungszeit < 50ms | ~2.8 ms (Budget: 50.0 ms) | ✅ Bestanden |
| TC-PERF-02 | 1.000 Geofence-Prüfungen | Ausführungszeit < 50ms | ~3.4 ms (Budget: 50.0 ms) | ✅ Bestanden |
| TC-PERF-03 | Precision Assertions & Diagnostics | Klare Fehlerbeschreibungen bei Abweichung | Valide Fehlermeldungen | ✅ Bestanden |

---

## 🚀 Test-Runner Skripte & CLI-Befehle

- **Windows Batch:** `scripts\run_distance_geofence_tests.bat`
- **PowerShell:** `.\scripts\run_distance_geofence_tests.ps1`
- **Linux/macOS:** `./scripts/run_distance_geofence_tests.sh`
- **Gradle Direktaufruf:**
  ```bash
  ./gradlew testDebugUnitTest --tests "com.kliq.app.domain.usecase.CalculateUserDistanceUseCaseTest" --tests "com.kliq.app.data.util.AntiSpamReviewValidatorTest"
  ```
