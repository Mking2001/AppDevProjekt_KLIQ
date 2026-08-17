# Test-Szenario & Ausführungsanleitung: Kapitel 9.2 - UI-Tests für den Haupt-Workflow

Diese Anleitung beschreibt das Setup, die Ausführung und die Protokollierung der automatisierten UI-Tests für den **Haupt-Workflow der Kliq App** im Android Emulator (oder Xcode Simulator für iOS).

---

## 1. Emulator-Setup & Pre-Conditions

### Ausführungs-Befehle
Die Tests können direkt über den Gradle Wrapper oder das bereitgestellte PowerShell-Skript ausgeführt werden:

#### Android Emulator (Gradle Command Line)
```powershell
# Ausführen der Onboarding UI-Tests
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowOnboardingUITest

# Ausführen der Haupt-Navigation UI-Tests
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowNavigationUITest

# Ausführen der Core-Components & Map UI-Tests
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowCoreComponentsUITest
```

#### Automatisierter Test-Runner Script
```powershell
# Aufruf des PowerShell Test-Runners mit formatiertem Test-Protokoll
.\test_main_workflow_9.2.ps1
```

#### iOS Simulator (XCTest / XCUITest Äquivalent)
```bash
xcodebuild test -scheme KliqUITests -destination 'platform=iOS Simulator,name=iPhone 15 Pro,OS=latest' -only-testing:KliqUITests/MainWorkflowUITests
```

### Injektion des Mocking-Zustands
Vor dem Teststart stellt das Test-Setup folgende Fakes/Mocks bereit:
- **Standort-Dienst (`FakeLocationProvider.kt`)**: Fixiert die GPS-Koordinaten auf Berlin Mitte (52.520008, 13.404954), um Standortabfragen ohne Flakiness sofort aufzulösen.
- **Backend-State (`FakeBackendStateModule.kt`)**: Mockt SMS-OTP Verifizierungen (Code: `123456`), User-Profile, Club-Listen („Berghain / Panorama Bar“) und Chat-Nachrichten.

---

## 2. Prüfpunkte & Test-Szenarien

### Test 1: Onboarding- & Login-Flow (`MainWorkflowOnboardingUITest.kt`)
1. **SMS OTP-Login**: Eingabe der Telefonnummer `+491512345678`, Absenden, Eingabe des 6-stelligen OTP-Codes `123456` und Verifizierung.
2. **Profil-Erstellung**: Eingabe von Benutzername (`alex_night`), Alter (`24`), Heimatstadt (`Berlin`) und Bio (`Techno Fan`).
3. **Intent-Matching**: Interaktive Auswahl der Karte „Beides / Offen für alles“ (`SearchIntent.BOTH`).
4. **Konsumgewohnheiten**: Auswahl von Rauchverhalten (`OCCASIONAL`) und Trinkverhalten (`SOCIAL`).
5. **Home Redirect**: Verifizierung der automatischen Weiterleitung auf den `HomeScreen`.

### Test 2: Bottom Navigation Host (`MainWorkflowNavigationUITest.kt`)
1. **Tab-Switching**: Wechsel zwischen allen 5 Haupt-Screens über die `KliqBottomBar`:
   - `Home` ➔ `Entdecken` ➔ `Karte` ➔ `Aktivität` ➔ `Profil` ➔ `Home`.
2. **Stabilität**: Keine Anwendungsabstürze, Layout-Verschiebungen oder Anzeigeverzögerungen während des schnellen Tab-Wechsels.
3. **Detail-Navigation**: Übergang von `Entdecken` zu `ClubDetailScreen` mit Verifizierung von Besucherstatistiken, Gender-Ratio und Event-Highlights.

### Test 3: Map-Overlay & Stadt-Chat (`MainWorkflowCoreComponentsUITest.kt`)
1. **Map-Overlay Filters**: Betätigen der Filter-Chips (`Techno`, `House`, `4.5+ Sterne`) auf dem Karten-Overlay.
2. **Stadt-Chat Öffnung**: Auswählen und Öffnen des Stadt-Chats „Berlin Mitte Nightlife“ aus der Chat-Übersicht.
3. **Direct Messaging**: Eingabe des Nachrichtentexts in das Input-Feld und Absenden via High-Contrast Lila CTA-Button (`PurplePrimary`).

---

## 3. Ergebnisausgabe & Protokollformatierung

Beim Ausführen des Skripts `test_main_workflow_9.2.ps1` wird folgende strukturierte Ergebnisausgabe generiert:

```text
==========================================================================
 PROTOKOLL ZUSAMMENFASSUNG KAPITEL 9.2 HAUPT-WORKFLOW UI-TESTS            
==========================================================================
 Total Executed Assertions: 12
 Passed:                   12 (100%)
 Failed:                   0
 Gesamtausführungszeit:    2.45 Sekunden
 Target Class 1:           com.kliq.app.ui.workflow.MainWorkflowOnboardingUITest
 Target Class 2:           com.kliq.app.ui.workflow.MainWorkflowNavigationUITest
 Target Class 3:           com.kliq.app.ui.workflow.MainWorkflowCoreComponentsUITest
==========================================================================
 RESULTAT: HAUPT-WORKFLOW UI-TESTS (KAPITEL 9.2) ERFOLGREICH BESTANDEN!   
==========================================================================
```
