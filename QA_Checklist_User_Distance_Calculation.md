# QA Checklist: Feature 4.7 – Distanz-Berechnungen zwischen Nutzern

## Testumgebung
- **App Version**: Kliq Android v1.4.7
- **Test-Suites**: JUnit4 / Kotlin Coroutines Test
- **Branch**: `feature/user-distance-calculation`

---

## QA Testfälle & Abnahmekriterien

| ID | Testfall | Erwartetes Ergebnis | Status |
|:---|:---|:---|:---:|
| **QA-4.7.1** | Haversine Berechnung bei bekannten Geokoordinaten | Präzise Entfernung in Metern (z. B. Brandenburger Tor bis Alex ~ 2480m) | PASSED |
| **QA-4.7.2** | Identische Standorte | Distanz genau 0.0 Meter | PASSED |
| **QA-4.7.3** | Ungültige Koordinaten (NaN / Out-of-Bounds) | Rückgabe von `null` ohne App-Absturz | PASSED |
| **QA-4.7.4** | Distanz-Formatierung unter 1000m | Anzeige in Metern ohne Nachkommastellen (z. B. `"150 m"`) | PASSED |
| **QA-4.7.5** | Distanz-Formatierung ab 1000m | Anzeige in Kilometern mit 1 Nachkommastelle (z. B. `"1.2 km"`) | PASSED |
| **QA-4.7.6** | Fehlende GPS-Koordinaten | Fallback-Text `"Entfernung unbekannt"` | PASSED |
| **QA-4.7.7** | Reaktivität bei Standort-Updates | UI State aktualisiert Distanzwerte automatisch bei neuen Location-Events | PASSED |
| **QA-4.7.8** | UI Thread Performance | Berechnungen laufen auf `Dispatchers.Default` ohne Stutter | PASSED |
| **QA-4.7.9** | User QuickView Card Integration | Formatierte Distanz wird im QuickView Popup korrekt dargestellt | PASSED |

---

## Ausgeführte Testbefehle
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
.\gradlew testDebugUnitTest --tests "com.kliq.app.domain.usecase.CalculateUserDistanceUseCaseTest"
.\gradlew testDebugUnitTest --tests "com.kliq.app.util.UserDistanceFormatterTest"
.\gradlew testDebugUnitTest --tests "com.kliq.app.ui.screens.map.UserDistanceIntegrationTest"
```
