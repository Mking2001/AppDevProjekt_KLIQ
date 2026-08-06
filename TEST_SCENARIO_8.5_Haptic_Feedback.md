# Test-Szenario & Script: Kapitel 8.5 - Haptisches Feedback Management im Emulator

## 🎯 Zielsetzung
Dieses Test-Szenario beschreibt die systematische manuelle und automatisierte Überprüfung der in **Kapitel 8.5** implementierten haptischen Feedback-Funktion für die Kliq-App. Da auf Android-Emulatoren physische Vibrationen in der Regel nicht spürbar sind, nutzt das System automatisierte Logcat-Ausgaben (`[HAPTIC] Triggered <PATTERN> pattern for <REASON>`), um das ordnungsgemäße Auslösen der Haptik bei allen Schlüsselaktionen präzise zu verifizieren.

---

## 🛠️ Ausführung & Testumgebung
- **Test-System**: Android Emulator (API 34 / 35) oder physisches Testgerät.
- **Logcat-Verifizierung**: `adb logcat -s HapticFeedbackManager`
- **Automatisierte Ausführung (PowerShell)**: `.\test_haptic_feedback.ps1`

---

## 🧪 Schritt-für-Schritt Test-Ablauf

### Testfall 1: QR-Code Scan & Freundes-Verifizierung
- **Voraussetzung**: Kliq-App gestartet, QR-Scanner-Screen geöffnet.
- **Schritte**:
  1. Öffne den **QR-Code Scanner** (z. B. via Bottom-Bar oder Profil-Button).
  2. Richte die Kamera auf einen **gültigen Kliq-Freundes-QR-Code** (oder simuliere einen erfolgreichen Scan im ViewModel).
  3. Richte die Kamera auf einen **ungültigen QR-Code**.
- **Erwartetes Ergebnis (Logcat)**:
  - Bei erfolgreichem Scan / Verifizierung erscheint im Log:  
    `D/HapticFeedbackManager: [HAPTIC] Triggered CONFIRM pattern for QR Scan / Friend verification`
  - Bei ungültigem Scan erscheint im Log:  
    `D/HapticFeedbackManager: [HAPTIC] Triggered REJECT pattern for Invalid QR Code`

### Testfall 2: Abgabe einer Sterne-Bewertung
- **Voraussetzung**: Verifizierte Location (GPS / Geofence) oder verifizierte Profilansicht.
- **Schritte**:
  1. Öffne den **Bewertungs-Dialog** (Sterne-Rating).
  2. Tippe nacheinander auf verschiedene Sterne (1 bis 5 Sterne), um die Bewertung zu ändern.
  3. Tippe auf **„Bewertung absenden“**.
- **Erwartetes Ergebnis (Logcat)**:
  - Bei jeder Sterne-Auswahl erscheint im Log:  
    `D/HapticFeedbackManager: [HAPTIC] Triggered LIGHT_CLICK pattern for Rating star selection`
  - Bei erfolgreichem Absenden erscheint im Log:  
    `D/HapticFeedbackManager: [HAPTIC] Triggered CONFIRM pattern for Successful rating submission`

### Testfall 3: Swipe-to-Delete / Swipe-to-Archive in Chat-Listen
- **Voraussetzung**: Chat-Übersichtsscreen geladen, mindestens ein Chat-Eintrag in der Liste vorhanden.
- **Schritte**:
  1. Wische einen Chat-Eintrag **nach rechts** (Swipe Right -> Löschen).
  2. Wische einen Chat-Eintrag **nach links** (Swipe Left -> Archivieren).
- **Erwartetes Ergebnis (Logcat & UI)**:
  - Beim Erreichen des Rechts-Wisch-Schwellenwerts (Löschen) wird ein `HEAVY_CLICK` Haptik-Impuls ausgelöst.
  - Beim Erreichen des Links-Wisch-Schwellenwerts (Archivieren) wird ein `LIGHT_CLICK` Haptik-Impuls ausgelöst.

### Testfall 4: Long-Press auf Map-Marker (Quick-View Modal)
- **Voraussetzung**: Karten-Screen geladen, Club-Marker sichtbar.
- **Schritte**:
  1. Führe eine **Long-Press-Geste** (Press-and-Hold >= 500ms) auf einem Karten-Marker aus.
  2. Beobachte das Erscheinen des Quick-View Modals.
- **Erwartetes Ergebnis (Logcat & UI)**:
  - Unmittelbar beim Auslösen der Geste wird ein `HEAVY_CLICK` Haptik-Impuls getriggert:  
    `D/HapticFeedbackManager: [HAPTIC] Triggered HEAVY_CLICK pattern for Map marker long-press quick-view`
  - Das Quick-View-Modal öffnet sich flüssig.

---

## 📊 Zusammenfassende QS-Checkliste

- [x] **HapticFeedbackManager**: Zentrale Hilt-gebundene Service-Klasse mit System-Settings-Prüfung (`HAPTIC_FEEDBACK_ENABLED`).
- [x] **Logcat Mock/Logger**: `[HAPTIC]` Ausgaben für Emulator-Verifizierung aktiv.
- [x] **QR-Code Scan**: `CONFIRM` (Erfolg) und `REJECT` (Fehler) verifiziert.
- [x] **Sterne-Bewertung**: `LIGHT_CLICK` (Sterne-Wahl) und `CONFIRM` (Absenden) verifiziert.
- [x] **Chat Swiping**: `HEAVY_CLICK` (Delete) und `LIGHT_CLICK` (Archive) verifiziert.
- [x] **Map Long-Press**: `HEAVY_CLICK` bei Marker Quick-View verifiziert.
- [x] **Automatisierter Unit-Test**: `HapticFeedbackManagerTest.kt` bestanden.
