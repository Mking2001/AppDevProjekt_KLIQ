# QA Test Plan: Kapitel 5.7 - QR-Code-Scanner für Freundesanfragen/Verifizierung

## 1. Übersicht & Test-Ziel
Dieser QA Test-Plan definiert die Test-Szenarien und Verifikationsschritte für Kapitel 5.7 („QR-Code-Scanner für Freundesanfragen/Verifizierung“).
Ziel ist die Validierung der Kamera-Einbindung via CameraX, des Google ML Kit Barcode-Scanners, des Kliq-Designsystems (Dark-Mode, `#8A2BE2`), des Permission-Handlings sowie der Datenbank-Verarbeitung für Freundesanfragen.

---

## 2. Test-Szenarien & Emulator-Schritt-für-Schritt-Protokoll

### Szenario 1: Kamera-Permission Workflow Test
- **Ziel**: Überprüfen, ob ohne erteilte Kamera-Berechtigung der Kliq-Permission-Screen angezeigt wird und nach Bestätigung die Kamera-Preview startet.
- **Schritte im Android Studio Emulator**:
  1. Starte den Android Studio Emulator (z. B. Pixel 6 / API 34).
  2. Stelle sicher, dass die App-Berechtigung für Kamera widerrufen ist (`Settings > Apps > Kliq > Permissions > Camera > Don't allow`).
  3. Starte die Kliq App und navigiere zum Profil.
  4. Tippe auf den QR-Pass bzw. öffne den QR-Code-Scanner (`profile/qr_scanner`).
  5. **Erwartetes Ergebnis**: 
     - Es erscheint der Kliq Kamera-Permission-Screen mit Lila Accent-Icon (`#8A2BE2`) und dem Text *"Kamera-Zugriff erforderlich"*.
  6. Klicke auf den Button **"Kamera erlauben"**.
  7. Bestätige den System-Berechtigungsdialog mit **"While using the app"**.
  8. **Erwartetes Ergebnis**:
     - Die Kamera-Vorschau wird gestartet, der zentrierte abgerundete Scan-Rahmen mit der wandern/pulsierenden Laser-Animationslinie wird gerendert.

---

### Szenario 2: QR-Code Scanning Test (Gültig vs. Ungültig)

#### Test 2a: Valid Kliq Profile QR-Code
- **Vorbereitung**:
  - Nutze ein virtuelles Kamera-Bild oder erstelle einen QR-Code mit der Payload:
    `kliq://user/verify/user_friend_99?tag=kliq_profile_v1&ts=1700000000`
- **Schritte im Emulator**:
  1. Richte die Emulator-Kamera (Virtual Scene / Static Image / Extended Controls Camera) auf den QR-Code aus.
  2. **Erwartetes Ergebnis**:
     - Haptisches Feedback (Vibration / LongPress Event) wird ausgelöst.
     - Das Kamera-Signal pausiert kurzzeitig (Loading-Indikator während der UseCase-Auswertung).
     - Die Verifizierungs-Card erscheint unten im Dark-Look (`#1E1E2E` Container, `#8A2BE2` Akzent) mit der Meldung: *"Verifiziert! Verifizierung erfolgreich! Freundesanfrage gesendet..."*.
     - In der Room DB (Tabelle `friends`) wurde der Eintrag mit `status = ACCEPTED` und `isQrVerified = 1` angelegt.
     - Beim Klick auf **"Zum Profil"** navigiert die App zum Profil des gescannten Nutzers (`profile/other/user_friend_99`).

#### Test 2b: Invalid / Non-Kliq QR-Code
- **Vorbereitung**:
  - Nutze einen normalen URL QR-Code (z. B. `https://www.google.com`).
- **Schritte im Emulator**:
  1. Richte den Scanner im Emulator auf den fremden QR-Code aus.
  2. **Erwartetes Ergebnis**:
     - Die Fehler-Card erscheint mit orangem/rotem Icon und der Meldung: *"Ungültiger Code - Der gescannte QR-Code enthält keinen gültigen Kliq-Profil-Schlüssel."*.
     - Der Scanner bleibt einsatzbereit und bietet den Button **"Erneut versuchen"** an.

#### Test 2c: Self-Scan QR-Code
- **Vorbereitung**:
  - Nutze den eigenen QR-Code aus Kapitel 5.6 (`kliq://user/verify/current_user`).
- **Schritte im Emulator**:
  1. Scanne den eigenen Code.
  2. **Erwartetes Ergebnis**:
     - Warn-Card mit der Meldung: *"Eigener QR-Code - Du kannst deinen eigenen Profil-Code nicht scannen."*.

---

### Szenario 3: UI State, Flashlight & Lifecycle Safety
- **Schritte im Emulator**:
  1. Tippe in der Header-Leiste des Scanners auf das **Flashlight-Icon**.
     - **Erwartung**: Das Torch-Signal schaltet um (Icon wechselt zu FlashOn / Lila Akzent).
  2. Tippe auf den Zurück-Pfeil oben links.
     - **Erwartung**: Die App kehrt nahtlos zum vorherigen Screen zurück, die CameraX `ImageAnalysis` & `Preview` Ressourcen werden im Lifecycle `onStop` / `onDispose` ordnungsgemäß geschlossen.

---

## 3. Automatisiertes Emulator-Test-Skript
Das instrumentierte UI-Testskript liegt in:
[QRScannerEmulatorTest.kt](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/QRScannerEmulatorTest.kt)

Ausführung via Gradle:
```bash
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.QRScannerEmulatorTest
```
