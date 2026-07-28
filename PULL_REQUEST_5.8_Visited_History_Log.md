# Pull Request: Kapitel 5.8 - "Besucht am"-Log für die Historie

## Zusammenfassung
Dieser Pull Request implementiert Kapitel 5.8 („"Besucht am"-Log für die Historie“) für die Kliq Android-App. Die Implementierung umfasst den vollständigen `VisitedHistoryScreen` und die `VisitedLogCard`-Komponenten im Kliq-Designsystem, das `HistoryViewModel`, das `VisitedLogRepository` für die asynchrone Datenverarbeitung via Kotlin Flow & Coroutines sowie das lokale Room-Schema (`VisitedLogEntity`, `VisitedLogDao`, DB Migration 12->13).

## Wichtigste Änderungen

### 1. Architektur (MVVM & Data Centricity)
- **Domain & Room Modell**: `VisitedLog` Domain-Modell und `VisitedLogEntity` (`@Entity(tableName = "visited_logs")`) mit Feldern: `id`, `userId`, `clubId`, `clubName`, `visitedAtTimestamp`, `isVerifiedByGps`.
- **Database Update & Migration**: DB Version 13 in `KliqDatabase.kt` registriert inkl. `MIGRATION_12_13` zur Erstellung der Tabelle und Indizes.
- **DAO & Repository**: `VisitedLogDao` für Datenbankzugriffe und `VisitedLogRepository` / `VisitedLogRepositoryImpl` für die asynchrone Datenabstraktion auf `Dispatchers.IO`.
- **Dependency Injection**: Bereitstellung von `VisitedLogDao` in `AppModule.kt` und Binding von `VisitedLogRepository` in `RepositoryModule.kt`.
- **ViewModel & Clean UI State**: `HistoryViewModel` verwaltet den `HistoryUiState` mit vier sauberen Zuständen: `Loading`, `Success`, `Empty` und `Error`.

### 2. UI & Design (`VisitedHistoryScreen` & `VisitedLogCard`)
- **Kliq Designsystem**: Dunkles Farbschema mit Lila-Akzenten (`DarkBackground`, `PurplePrimary`, `DarkSurfaceContainer`) und abgerundeten Cards (`16.dp`).
- **Formatierter Zeitstempel**: Korrekte deutsche Datums- und Uhrzeitdarstellung („Besucht am DD.MM.YYYY um HH:mm Uhr“).
- **GPS-Verifizierungs-Badge**: Visualisierung eines „GPS Verifiziert“-Badges in Teal mit Checkmark-Icon, wenn ein Besuch per Geofence/GPS bestätigt wurde (`isVerifiedByGps == true`).
- **Statistik-Header**: Übersicht mit Gesamtanzahl der Clubbesuche und Anzahl der GPS-verifizierten Besuche.
- **Profil-Integration**: Einbindung als eigener „Historie“-Tab im Nutzerprofil (`ProfileScreen.kt`).

### 3. Git-Strategie & Commit-Historie
Atomare, logische Commits auf dem Feature-Branch `feature/visited-history-log`:
1. `feat(model)`: VisitedLog Domain Model, Room Entity und DB v13 Migration
2. `feat(repository)`: VisitedLogDao, VisitedLogRepository und Hilt DI Bindings
3. `feat(viewmodel)`: HistoryViewModel für UI State Management (Loading, Success, Empty, Error)
4. `feat(ui)`: VisitedLogCard Komponente, VisitedHistoryScreen und ProfileScreen Tab Integration
5. `test(history)`: Unit-Tests für VisitedLogDao, VisitedLogRepository und HistoryViewModel

## Tests & Verifikation
- **DAO Tests**: `VisitedLogDaoTest` (CRUD & Query-Filter per Robolectric)
- **Repository Tests**: `VisitedLogRepositoryTest` (Mapping & Flow Datenfluss)
- **ViewModel Tests**: `HistoryViewModelTest` (Zustandsübergänge Loading, Success, Empty, Error)
- **Erfolgreiche Ausführung**: `$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat testDebugUnitTest`
