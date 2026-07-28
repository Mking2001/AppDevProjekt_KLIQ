# QA Test Plan: Kapitel 5.8 - "Besucht am"-Log für die Historie

## 1. Übersicht & Test-Ziel
Dieser QA Test-Plan definiert das Test-Skript und die Emulator-Test-Szenarien für Kapitel 5.8 („"Besucht am"-Log für die Historie“).
Ziel ist die Überprüfung der korrekten Darstellung, Sortierung (absteigend nach Datum), Formatierung („Besucht am DD.MM.YYYY um HH:mm Uhr“), des visuellen GPS-Verifizierungs-Badges im Kliq Dark/Lila-Design sowie des Empty-States und der Performance ohne UI-Glitches oder Crashes.

---

## 2. Test-Szenarien & Emulator-Schritt-für-Schritt-Protokoll

### Szenario 1: Vorbereitung & Daten-Injektion via Mock-Seeder
- **Ziel**: Vorbereitung der Test-Datenbank mit drei variierenden Besuchs-Einträgen.
- **Schritte & Seeder-Aufruf**:
  1. Starte den Android Studio Emulator (z. B. Pixel 6 / API 34).
  2. Injiziere über den `VisitedLogMockSeeder` drei Testeinträge für den Nutzer `current_user`:
     - **Eintrag 1 (Neuestes Datum / Heute)**: `clubName = "Bootshaus Köln"`, `isVerifiedByGps = true`, `visitedAtTimestamp = now`
     - **Eintrag 2 (Vor 2 Tagen)**: `clubName = "Pacha München"`, `isVerifiedByGps = false`, `visitedAtTimestamp = now - 2 Tage`
     - **Eintrag 3 (Ältestes Datum / Vor 7 Tagen)**: `clubName = "Berghain Berlin"`, `isVerifiedByGps = true`, `visitedAtTimestamp = now - 7 Tage`
  3. **Erwartetes Ergebnis**:
     - Die Einträge sind erfolgreich in der lokalen Room-Datenbank (Tabelle `visited_logs`) gespeichert.

---

### Szenario 2: Durchführung - Anzeige & Datums-Sortierung im Emulator
- **Schritte im Emulator**:
  1. Öffne die Kliq App im Emulator.
  2. Navigiere zum Profil (`profile`) und wähle den Tab **"Historie"** aus (oder rufe den `VisitedHistoryScreen` direkt auf).
  3. **Erwartete Ergebnisse & Visuelle Prüfung**:
     - **Sortierung**: Der oberste Eintrag ist "Bootshaus Köln" (neuestes Datum), gefolgt von "Pacha München" (vor 2 Tagen) und "Berghain Berlin" (vor 7 Tagen).
     - **Text-Formatierung**: Jeder Eintrag zeigt die präzise Datums- und Zeitangabe nach der Vorgabe:
       `„Besucht am DD.MM.YYYY um HH:mm Uhr“`
     - **GPS-Verifizierungs-Badge**:
       - Bei "Bootshaus Köln" und "Berghain Berlin" (`isVerifiedByGps = true`) ist das grüne/teal-farbene Badge **„GPS Verifiziert“** mit Checkmark-Icon sichtbar.
       - Bei "Pacha München" (`isVerifiedByGps = false`) ist das Badge ausgeblendet.
     - **Theme & Design**: Die Cards nutzen das Kliq Dark-Purple Theme (`DarkSurfaceContainer`, `#1A1523` / `#2D2640`) ohne visuelle Glitches.
     - **Header-Statistik**: Die Summary-Card zeigt `Gesamte Besuche: 3` und `GPS Verifiziert: 2`.

---

### Szenario 3: Durchführung - Empty State Test
- **Schritte im Emulator**:
  1. Führe die Aktion **Historie leeren** aus oder starte die App mit einem neuen Nutzer ohne Besuchs-Einträge.
  2. Navigiere zum Besuchs-Historien-Screen.
  3. **Erwartetes Ergebnis**:
     - Der maßgeschneiderte Kliq Empty-State wird gerendert.
     - Anzeige des Historien-Icons mit dem Titel **"Noch keine Besuche"** und der Beschreibung *"Deine vergangenen Club-Besuche und GPS-Bestätigungen werden hier chronologisch angezeigt."*.
     - Ein Button **"Aktualisieren"** steht bereit.

---

## 3. Erwartete Gesamtergebnisse & Performance
- **Flüssiges Rendering**: Ruckelfreies Scrollen durch die Besuchs-Liste via `LazyColumn`.
- **Konsolen/Logcat-Prüfung**: Keine Ausnahmen (`Exception`, `NullPointerException`), Warnungen oder UI-Jank im Logcat-Output (`adb logcat *:E`).

---

## 4. Automatisiertes UI-Test-Skript
Das instrumentierte UI-Testskript befindet sich in:
[VisitedHistoryEmulatorTest.kt](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/screens/profile/VisitedHistoryEmulatorTest.kt)

Ausführung via Gradle:
```bash
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.screens.profile.VisitedHistoryEmulatorTest
```
