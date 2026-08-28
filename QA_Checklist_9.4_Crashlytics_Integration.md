# QA Checklist: Kapitel 9.4 - Crashlytics Integration für Fehlerberichte

## Testumgebung & Vorbedingungen
- [x] Android Studio Gradle Sync erfolgreich
- [x] Compilation Check: `./gradlew assembleDebug` fehlerfrei
- [x] Feature Branch: `feature/crashlytics-integration`

---

## QS-Prüfpunkte

### 1. SDK Integration & Performance
- [x] **Firebase Crashlytics KTX & Timber**: Erfolgreich eingebunden.
- [x] **Asynchrone Initialisierung**: In `KliqApplication.onCreate()` auf `Dispatchers.Default` verschoben; App-Kaltstart wird nicht blockiert.

### 2. PII Datenschutz & Security
- [x] **Telefonnummern-Maskierung**: `+49151...` / `0151...` wird zu `[REDACTED_PHONE]`.
- [x] **GPS-Koordinaten-Maskierung**: Präzise Standortdaten werden zu `[REDACTED_GPS]`.
- [x] **E-Mail & Token-Maskierung**: `[REDACTED_EMAIL]` / `[REDACTED_TOKEN]`.
- [x] **User-ID Anonymisierung**: Generierung anonymer Session- & User-Hashes (`user_anon_...`).

### 3. Error Logging & State Tracking
- [x] **Timber Tree (`KliqCrashlyticsTree`)**: Warnungen und Fehler werden automatisch gefiltert und an Crashlytics übermittelt.
- [x] **Nicht-fatale Exceptions**: `CrashReportingLogger.logNonFatalException()` zeichnet gefangene Fehler auf.
- [x] **Route Tracking**: `KliqMainScaffold` aktualisiert den Custom Key `current_route` bei jedem Screen-Wechsel.

---

## Verifizierungsergebnis
Sämtliche Unit-Tests in `PiiSanitizerTest.kt` und `CrashlyticsTreeTest.kt` laufen erfolgreich durch. Das Skript `test_crashlytics_integration_9.4.ps1` verifiziert den fehlerfreien Ablauf.
