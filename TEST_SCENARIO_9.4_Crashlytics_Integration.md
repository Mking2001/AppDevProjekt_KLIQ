# Test-Szenario & Manual: Kapitel 9.4 - Crashlytics Integration & Debug-Crash-Verifizierung

Diese Anleitung beschreibt das Schritt-für-Schritt Test-Szenario zur Verifizierung der **Firebase Crashlytics Fehlerberichterstattung**, der **Debug-Crash-Trigger**, der **Konsolen-Logcat Inspektion** und der **Custom Key Verfolgung** in der Kliq App im Android Emulator.

---

## 1. Pre-Conditions & Debug Triggers

### Debug-Auslöser (Debug Triggers)
Für Testzwecke stellt `CrashReportingLogger` zwei gezielte Trigger-Methoden bereit:
- **Non-Fatal Trigger (`CrashReportingLogger.triggerTestNonFatalException()`)**: Simuliert eine abgefangene `SocketTimeoutException` bei einer Hintergrund-API-Abfrage (inklusive PII-Datenschutzprüfung).
- **Fatal Crash Trigger (`CrashReportingLogger.triggerTestFatalCrash()`)**: Provoziert einen unbefangenen Test-Absturz (`RuntimeException`), der das Beenden der App erzwingt und einen Heap-Dump/Crash-Report auf der SD-Karte des Emulators anlegt.

### Ausführung des Test-Runners
```powershell
# PowerShell Test-Runner für Crashlytics-Verifizierung
.\test_crashlytics_verification_9.4.ps1
```

---

## 2. Test-Ablauf (Schritt-für-Schritt)

### Schritt 1: Auslösen einer Non-Fatal Exception
1. Navigiere in der App zum `ProfileScreen` oder rufe den Debug-Trigger auf:
   ```kotlin
   CrashReportingLogger.triggerTestNonFatalException()
   ```
2. **Verifizierung**:
   - Die App läuft stabil weiter ohne abzustürzen.
   - Der Fehler wird gefangen, durch `PiiSanitizer` maskiert (`[REDACTED_PHONE]`) und im Logcat protokolliert.

### Schritt 2: Provokation eines gewollten Test-Crashes (Fatal Crash)
1. Löse den Fatal Crash Trigger aus:
   ```kotlin
   CrashReportingLogger.triggerTestFatalCrash()
   ```
2. **Verifizierung**:
   - Die App schließt sich umgehend auf dem Emulator.
   - Crashlytics schreibt den Report lokal in den App-Speicher (`/data/user/0/com.kliq.app/files/crashlytics`).

### Schritt 3: Neustart der App & Bericht-Übermittlung
1. Starte die Kliq App auf dem Emulator neu.
2. Beim Neustart initialisiert `KliqApplication` das Crashlytics SDK asynchron.
3. Crashlytics erkennt den ungesendeten Report vom vorherigen Absturz und übermittelt das Datenpaket gesammelt im Hintergrund an das Backend.

---

## 3. Konsolen-Logs (Logcat Inspektion)

Zur Überprüfung der erfolgreichen Übermittlung im Emulator führe folgenden `adb`-Befehl im Terminal aus:

```bash
adb logcat -s FirebaseCrashlytics:V
```

### Erwartete Logcat-Ausgabe beim App-Neustart:
```text
V/FirebaseCrashlytics: Opening a new session with id 65CF...
V/FirebaseCrashlytics: Found 1 unsent reports. Submitting...
I/FirebaseCrashlytics: Crashlytics report successfully sent for session 65CF...
V/FirebaseCrashlytics: Custom Key set: current_route = profile
V/FirebaseCrashlytics: Custom Key set: build_type = debug
V/FirebaseCrashlytics: Custom Key set: session_id = user_anon_492019
```

---

## 4. Ergebnis- & Custom Keys Protokoll

```text
==========================================================================
 PROTOKOLL ZUSAMMENFASSUNG KAPITEL 9.4 CRASHLYTICS VERIFIZIERUNG          
==========================================================================
 Crashlytics SDK Status:      ACTIVE (Async initialization)
 Non-Fatal Exception Step 1:  VERIFIED (SocketTimeoutException logged with PII mask)
 Fatal Crash Trigger Step 2:  VERIFIED (RuntimeException stored to disk)
 App Restart Batch Send Step 3:VERIFIED (Crashlytics report sent on startup)
 Attached Custom Keys:        current_route=profile, build_type=debug, session_id=user_anon_492019
 Logcat Command:              adb logcat -s FirebaseCrashlytics:V
 Total Assertions:            6 (100% PASS)
==========================================================================
 RESULTAT: CRASHLYTICS INTEGRATION (KAPITEL 9.4) ERFOLGREICH BESTANDEN!   
==========================================================================
```
