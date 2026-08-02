# Test-Szenario 7.7: Favoriten-System für Clubs

## Übersicht
Dieses Dokument beschreibt das automatisierte Test-Szenario für Kapitel 7.7 ("Favoriten-System für Clubs").
Es umfasst sowohl **Local Unit Tests** für die Repository- und ViewModel-Schicht als auch **Instrumented UI Tests** (Espresso / Compose Test Rule) für interaktive Favoriten-Toggles und Persistenz-Prüfungen.

---

## 1. Unit Tests (`ClubRepositoryTest` & `ClubFavoriteViewModelTest`)

### Ziel
Verifizierung der asynchronen Raum-Datenbankabfragen, Flow-Reaktivität und StateFlow-Updates beim Umschalten des Favoriten-Status (`isFavorite`).

### Abgedeckte Testfälle
1. `testAddAndRemoveClubFromFavorites_updatesStateAndFlowInstantly`:
   - Prüft das Hinzufügen und Entfernen von Clubs in der Room-Datenbank.
   - Stellt sicher, dass `getFavoriteClubs()` sofort einen aktualisierten Flow mit den favorisierten Clubs ausgibt.
2. `testToggleFavorite_flipsIsFavoriteState`:
   - Verifiziert, dass `toggleFavorite(clubId, currentState)` den `isFavorite`-Wert in Room invertiert (`false` -> `true` und `true` -> `false`).
3. `clubViewModel_toggleFavorite_updatesStateAndCallsRepository`:
   - Verifiziert, dass `ClubViewModel` seinen `ClubUiState` reaktiv anpasst und das Repository aufruft.
4. `mapViewModel_toggleFavorite_updatesSelectedVenueAndRepository`:
   - Prüft die reaktive Aktualisierung von Quick-View-Karten und Map-Markern in `MapViewModel`.

---

## 2. Instrumented UI Test (`FavoriteClubFlowTest`)

### Ziel
Verifizierung der visuellen State-Transition des Heart-Icons (`AnimatedFavoriteButton`), der Farbgestaltung im Kliq Lila-Akzent (`#8A2BE2`) und der dauerhaften Speicherung nach Neustart.

### Abgedeckte Testfälle
1. `testFavoriteToggleVisualStateChangeAndPersistenceFlow`:
   - Rendert das Favoriten-Icon auf dem Detail-Screen im inaktiven Zustand (`isFavorite = false`).
   - Simuliert einen Benutzer-Klick.
   - Prüft, dass die Content Description sich von `"Zu Favoriten hinzufügen"` zu `"Aus Favoriten entfernen"` ändert und der Lila-Akzent aktiviert wird.
2. `testProcessRestartPersistenceAndFavoriteListRendering`:
   - Simuliert einen Prozess-Neustart (Prozess-Kill & Re-hydration aus Room).
   - Prüft, ob der gespeicherte Club in der Favoriten-/Ergebnisliste mit aktivem Herz-Icon korrekt gerendert wird.

---

## 3. Ausführung der Tests

Die Tests können über PowerShell oder Terminal mit den folgenden Befehlen ausgeführt werden:

### Local Unit Tests ausführen:
```powershell
./gradlew testDebugUnitTest --tests "com.kliq.app.data.repository.ClubRepositoryTest" --tests "com.kliq.app.viewmodel.ClubFavoriteViewModelTest"
```

### Instrumented UI Tests auf dem Emulator ausführen:
```powershell
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.FavoriteClubFlowTest
```

### Gesamtes Test-Skript ausführen:
```powershell
.\run_favorites_tests.ps1
```
