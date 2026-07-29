# QA Checklist: Kapitel 4.6 - Geofencing-Logik für Club-Radien

**Modul:** Geofencing & Location Verification (`com.kliq.app.service` / `com.kliq.app.viewmodel`)  
**Spezifikation:** Kapitel 4.6  
**Ziel:** Verifizierung der Geofence-Registrierung, Transition-Handling bei Eintritt/Austritt, Datensparsamkeit und UI-Zustandssteuerung.

---

## 📋 Testfälle & Akzeptanzkriterien

| ID | Testfall | Erwartetes Verhalten | Status |
|:---|:---|:---|:---:|
| **TC-GEO-01** | Geofence-Manager Registrierung | Native `GeofencingClient` Instanz erstellt Geofences mit korrekten Latitude/Longitude & Radius-Werten für Clubs aus DB. | PASS |
| **TC-GEO-02** | Systemlimit & Datensparsamkeit | Dynamische Sortierung wählt maximal N nächste Clubs (z.B. max 50) in der Umgebung aus, um das Android Limit von 100 Geofences einzuhalten. | PASS |
| **TC-GEO-03** | Transition Enter Handling | `GEOFENCE_TRANSITION_ENTER` Event aktualisiert `activeClubState.isInsideGeofence = true` und aktiviert das verifizierte Bewertungssystem. | PASS |
| **TC-GEO-04** | Transition Exit Handling | `GEOFENCE_TRANSITION_EXIT` Event setzt `isInsideGeofence = false`, schließt den aktiven Besuch ab und trägt das `exitTimestamp` ein. | PASS |
| **TC-GEO-05** | Besucht-Historie Tracking | Bei Eintritt wird automatisch ein verifizierter `VisitedClubHistory`-Eintrag erstellt und in der Historie gespeichert. | PASS |
| **TC-GEO-06** | GeofenceViewModel State Flow | `GeofenceUiState` reagiert verzögerungsfrei auf Repository-Änderungen und aktiviert UI-Komponenten (z. B. "Bewerten"-Button). | PASS |
| **TC-GEO-07** | BroadcastReceiver Integration | `GeofenceBroadcastReceiver` ist in `AndroidManifest.xml` registriert und empfängt System-Intents korrekt. | PASS |

---

## 🧪 Durchführung der verifizierenden Tests

```bash
# Unit Tests ausführen
./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.GeofenceViewModelTest"
./gradlew testDebugUnitTest --tests "com.kliq.app.data.repository.GeofenceRepositoryTest"

# Instrumentierte Integrationstests ausführen
./gradlew connectedAndroidTest --tests "com.kliq.app.service.GeofenceIntegrationTest"
```

**Ergebnis:** All unit & integration tests PASSED successfully.
