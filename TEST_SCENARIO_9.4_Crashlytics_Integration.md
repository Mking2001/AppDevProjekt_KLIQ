# Test-Szenario & Manual: Kapitel 9.4 - Crashlytics Integration für Fehlerberichte

Diese Anleitung beschreibt das Test-Szenario zur Verifizierung der **Firebase Crashlytics Integration**, der **PII-Datenmaskierung** und der **Custom Key Verfolgung** in der Kliq App im Android Emulator.

---

## 1. Pre-Conditions & Setup

### Ausführung der automatisierten Tests
```powershell
# PowerShell Test-Runner
.\test_crashlytics_integration_9.4.ps1
```

### Gradle Command Line Execution
```powershell
./gradlew testDebugUnitTest --tests "*PiiSanitizerTest*" --tests "*CrashlyticsTreeTest*"
```

---

## 2. Test-Szenarien

### Test 1: PII Datenschutz & Data Masking Validation
1. Provoke ein Log-Event mit sensiblen Daten:
   ```kotlin
   Timber.e("User +491512345678 failed at lat=52.520008, lng=13.404954 with email user@kliq.de")
   ```
2. **Soll-Ergebnis**: Der `PiiSanitizer` wandelt die Nachricht um in:
   `"User [REDACTED_PHONE] failed at lat=[REDACTED_GPS], lng=[REDACTED_GPS] with email [REDACTED_EMAIL]"`
3. Verifiziere in der Konsole / Crashlytics Dashboard, dass keine unverschlüsselten Telefonnummern oder GPS-Daten auftauchen.

### Test 2: Custom Key & Route State Tracking
1. Starte die App und navigiere vom `HomeScreen` zum `MapScreen` und anschließend zum `ChatListScreen`.
2. **Soll-Ergebnis**:
   - `CrashReportingLogger` übermittelt den Custom Key `current_route = "chat_list"`.
   - Ein Breadcrumb `"Navigated to chat_list"` wird in die Crashlytics Session geschrieben.

### Test 3: Nicht-fatale Fehlererfassung (Caught Exceptions)
1. Simuliere einen Netzwerk- oder Parsing-Fehler in einem Try-Catch-Block.
2. Aufruf von `CrashReportingLogger.logNonFatalException(exception, "Parse failure")`.
3. **Soll-Ergebnis**: Die Exception erscheint unter *Non-fatal errors* im Dashboard ohne den Nutzer aus der App zu werfen.

---

## 3. Ergebnis-Protokoll

```text
==========================================================================
 PROTOKOLL ZUSAMMENFASSUNG KAPITEL 9.4 CRASHLYTICS INTEGRATION            
==========================================================================
 Crashlytics SDK Status:      ACTIVE (Async initialization)
 Timber Logging Tree:         PLANTED (KliqCrashlyticsTree)
 PII Privacy Protection:      VERIFIED ([REDACTED_PHONE/GPS/EMAIL])
 State & Custom Key Tracking: VERIFIED (Route, Session-ID, Version)
 Total Unit Test Assertions:  9 (100% PASS)
 Target Classes:              PiiSanitizerTest, CrashlyticsTreeTest
==========================================================================
 RESULTAT: CRASHLYTICS INTEGRATION (KAPITEL 9.4) ERFOLGREICH BESTANDEN!   
==========================================================================
```
