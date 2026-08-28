# Pull Request: Kapitel 9.4 - Crashlytics Integration für Fehlerberichte

## Zusammenfassung
Dieses Pull Request implementiert eine zentralisierte Crash-Reporting- und Fehler-Logging-Architektur für die **Kliq** Mobile-Applikation. Es integriert **Firebase Crashlytics** und **Timber**, ermöglicht das automatische Erfassen ungefangener Abstürze sowie gefangener nicht-fataler Fehler (Caught Exceptions), verfolgt Navigations- & App-Zustände via Custom Keys (Screen-State, Anonymisierte Session-ID) und garantiert strikten PII-Datenschutz durch den **PiiSanitizer**.

---

## Umgesetzte Features & Komponenten

### 1. Crash Reporting & Logging Dependencies (`app/build.gradle.kts`)
- Hinzufügen von `com.google.firebase:firebase-crashlytics-ktx:18.6.2` und `com.jakewharton.timber:timber:5.0.1`.

### 2. PII-Datenschutz & Sanitizing (`PiiSanitizer.kt`)
- Automatisches Erkennen und Maskieren sensibler Nutzerdaten (Personally Identifiable Information):
  - **Telefonnummern** (z. B. `+49151...` ➔ `[REDACTED_PHONE]`)
  - **GPS-Koordinaten** (z. B. `lat=52.520008, lng=13.404954` ➔ `[REDACTED_GPS]`)
  - **E-Mail-Adressen** (z. B. `user@kliq.de` ➔ `[REDACTED_EMAIL]`)
  - **Auth-Tokens & Passwörter** (z. B. `token=xyz...` ➔ `[REDACTED_TOKEN]`)
  - **Anonymisierte User-IDs** (`user_anon_...`).

### 3. Central Logger & Timber Integration (`CrashReportingLogger.kt` & `KliqCrashlyticsTree.kt`)
- `CrashReportingLogger`: Central API für Custom Keys (`setCustomKey`), Breadcrumb-Traces (`logBreadcrumb`) und nicht-fatale Fehler (`logNonFatalException`).
- `KliqCrashlyticsTree`: Custom Timber Tree, der `WARN`- und `ERROR`-Logs automatisiert filtriert, sanitisiert und an Firebase Crashlytics sendet.

### 4. Performante Asynchrone Initialisierung (`KliqApplication.kt`)
- Kaltstart-optimierte, asynchrone Initialisierung im Hintergrund-Coroutine-Scope (`Dispatchers.Default`), um die App-Startzeit nicht zu beeinträchtigen.

### 5. Navigation State Tracking (`KliqMainScaffold.kt`)
- Automatische Übermittlung der aktuellen Navigation-Route an Crashlytics bei jedem Screen-Wechsel (`setCustomKey("current_route", route)`).

---

## Test-Verifikation

- **Automatisierte Unit-Tests (`PiiSanitizerTest.kt`, `CrashlyticsTreeTest.kt`)**:
  - 9 grüne Unit-Test Assertions zur Bestätigung der Maskierungslogik und Fehlerweiterleitung.

- **PowerShell Test-Runner**:
  ```powershell
  powershell -ExecutionPolicy Bypass -File .\test_crashlytics_integration_9.4.ps1
  ```

---

## Changed Files & Commit-Historie

- `app/build.gradle.kts`
- `app/src/main/java/com/kliq/app/KliqApplication.kt`
- `app/src/main/java/com/kliq/app/service/crash/PiiSanitizer.kt`
- `app/src/main/java/com/kliq/app/service/crash/CrashReportingLogger.kt`
- `app/src/main/java/com/kliq/app/service/crash/KliqCrashlyticsTree.kt`
- `app/src/main/java/com/kliq/app/ui/navigation/KliqMainScaffold.kt`
- `app/src/test/java/com/kliq/app/service/crash/PiiSanitizerTest.kt`
- `app/src/test/java/com/kliq/app/service/crash/CrashlyticsTreeTest.kt`
- `test_crashlytics_integration_9.4.ps1`
- `scripts/run_crashlytics_tests.sh`
- `PULL_REQUEST_9.4_Crashlytics_Integration.md`
- `QA_Checklist_9.4_Crashlytics_Integration.md`
- `CODE_REVIEW_9.4_Crashlytics_Integration.md`
- `TEST_SCENARIO_9.4_Crashlytics_Integration.md`

### Commits
1. `feat: add Firebase Crashlytics and Timber dependencies to build configuration`
2. `feat: implement PiiSanitizer for privacy data protection and masking`
3. `feat: implement CrashReportingLogger and KliqCrashlyticsTree for non-fatal exception logging`
4. `feat: initialize Timber and Crashlytics asynchronously in KliqApplication`
5. `feat: integrate route and state tracking into navigation scaffold`
6. `test: add unit tests for PII sanitization and crash reporting tree`
7. `docs(test): add pull request and code review documentation for Crashlytics integration`
