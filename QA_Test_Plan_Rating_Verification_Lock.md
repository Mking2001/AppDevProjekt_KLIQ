# QA-Test-Plan & Emulator-Anleitung: Schritt 5.3 – Logik-Sperre: Bewertung nur bei physischer Nähe oder QR-Scan

**Projekt:** Kliq Mobile App  
**Modul:** Anti-Spam Bewertungssystem (`VerificationService`, `RatingRepository`, `RatingViewModel`, `RatingLockScreen`)  
**Spezifikation:** Kapitel 5.3 (Logik-Sperre: Bewertung nur bei physischer Nähe oder QR-Scan)  
**Dokument-Typ:** Qualitätssicherungs-Spezifikation & Emulator-Test-Anleitung  
**Datum:** 26. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Diese Qualitätssicherungs-Spezifikation beschreibt das Test-Szenario und die automatisierte Emulator-Test-Simulation für **Kapitel 5.3 ("Logik-Sperre: Bewertung nur bei physischer Nähe oder QR-Scan")** der Kliq Mobile-App nach dem MVVM-Muster.

Ziel ist die automatische und manuelle Validierung von:
1. **Ablauf 1 (Unverifizierter Bewertungsversuch)**: Aufrufen eines Nutzerprofils ohne QR-Scan oder Geofence-Match. Validierung, dass UI-Komponenten ausgegraut/gesperrt bleiben und ein Repository-Schreibversuch mit einer `IllegalStateException` abgebrochen wird.
2. **Ablauf 2 (Erfolgreicher GPS-Match)**: Simulation eines gleichzeitigen Aufenthalts im selben Geofence/Club. Validierung, dass das `RatingViewModel` den Verifizierungsstatus reaktiv erkennt, die Sterne-Auswahl öffnet und den Datensatz speichert.
3. **Ablauf 3 (Erfolgreicher QR-Scan)**: Simulation des erfolgreichen Scans des persönlichen User-QR-Codes. Validierung der sofortigen Freischaltung und Datensatz-Aktualisierung in der Raum-Datenbank.

---

## 💻 2. Test-Umgebung & Emulator-Vorbereitung

### Hardware & Emulator Setup
- **Android Studio Emulator**: Pixel 7 Pro (API 34 / Android 14) oder Pixel 6 (API 33).
- **Test-Nutzer Konfiguration**:
  - Reviewer User ID: `user_alpha`
  - Target User ID: `user_beta`
  - Target QR-Pass Token: `KLIQ_PASS_user_beta`
  - Location Geofence ID: `club_matrix_50m` (Latitude: `46.6240`, Longitude: `14.3060`, Radius: `50m`)

### Ausführung der automatisierten Test-Suite
```powershell
# Set Environment Variable for Java SDK
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"

# Ausführung der Emulator-Integrationstest-Suite
cmd /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& set PATH=%JAVA_HOME%\bin;%PATH%&& gradlew.bat testDebugUnitTest --tests com.kliq.app.service.RatingVerificationLockEmulatorTest"
```

---

## 🧪 3. Schritt-für-Schritt Test-Szenario für den Emulator

### 🔹 Ablauf 1: Unverifizierter Bewertungsversuch (Sperre greift)

1. **Aktion im Emulator UI / Test-Setup**:
   - Der Nutzer `user_alpha` navigiert zum Profil von `user_beta`.
   - Es existiert weder ein gescannter QR-Code noch eine gemeinsame Standort-Historie.

2. **System-Verhalten & Assertions**:
   - `RatingViewModel.uiState.value.isRatingLocked` schaltet auf `true`.
   - `RatingViewModel.uiState.value.verificationMethod` verbleibt auf `UNVERIFIED`.
   - Die UI-Komponente `RatingLockScreen` zeigt das rote Anti-Spam Sperr-Banner ("Anti-Spam Sperre aktiv").
   - Sterne-Bewertung (1-5 Sterne) und Kommentarfeld sind ausgegraut (`alpha = 0.5f`, `enabled = false`).
   - Ein programmatischer Aufruf von `RatingRepositoryImpl.submitUserRating(...)` scheitert direkt mit einer `IllegalStateException` ("Bewertung gesperrt: Es liegt weder eine GPS-Standort-Verifizierung vor...").
   - Es wird **kein** Datensatz in die `reviews` Datenbanktabelle geschrieben (Assertion: `insertedReviews.size == 0`).

---

### 🔹 Ablauf 2: Erfolgreicher GPS-Match (Freischaltung via Nähe)

1. **Aktion via Emulator Extended Controls / Geofence Simulation**:
   - Setze die GPS-Koordinaten des Emulators auf den Standort des gemeinsamen Clubs (`46.6240, 14.3060`).
   - Simuliere den Geofence-Eintritt via `GeofenceRepository.handleGeofenceTransition("club_matrix_50m", ENTER)`.

2. **System-Verhalten & Assertions**:
   - `VerificationService` erkennt die physische Nähe / Geofence-Match.
   - `RatingViewModel` schaltet reaktiv um: `isRatingLocked = false`, `verificationMethod = GPS_GEOFENCE_MATCH`.
   - Das Anti-Spam Banner wechselt auf grün ("Bewertung freigeschaltet - Verifiziert via GPS-Geofence").
   - Sterne-Auswahl (1-5 Sterne) und Textkommentar-Eingabe werden interaktiv freigeschaltet.
   - Der Nutzer wählt 5 Sterne aus, gibt ein Kommentar ein und klickt auf "Bewertung abgeben".
   - `RatingRepositoryImpl` validiert die Freischaltung positiv und schreibt den Datensatz in Room DB.
   - Assertion: Datensatz in DB enthält `isVerified = true` und `verificationMethod = GPS_GEOFENCE_MATCH`.

---

### 🔹 Ablauf 3: Erfolgreicher QR-Scan (Freischaltung via Koppelung)

1. **Aktion via Emulator UI / Scanner Simulation**:
   - Klicke im `RatingLockScreen` auf den Button **"QR-Code scannen (Einlass-Pass)"**.
   - Simuliere den Scan des persönlichen User-QR-Tokens `KLIQ_PASS_user_beta`.

2. **System-Verhalten & Assertions**:
   - `VerificationService.verifyQrScanToken(...)` validiert das Token erfolgreich.
   - `RatingViewModel` schaltet die Logik-Sperre augenblicklich auf `isRatingLocked = false`.
   - `verificationMethod` wird aktualisiert auf `QR_CODE_SCAN`.
   - Der Nutzer gibt eine 4-Sterne-Bewertung ab und sendet das Formular ab.
   - `RatingRepositoryImpl` führt den finalen DB-Schreibvorgang aus.
   - Assertion: Der Datensatz wird mit `isVerified = true` und `verificationMethod = QR_CODE_SCAN` gespeichert.

---

## 📊 4. Automatisierte Execution Matrix & Test-Ergebnisse

| Test-Klasse | Test-Methode | Geprüfte Kriterien | Status |
|:---|:---|:---|:---:|
| `RatingVerificationLockEmulatorTest` | `test1_unverifiedRatingSubmission_locksUiAndThrowsSecurityExceptionOnRepositoryCall` | Default Locked State, UI Disabled, Repository Hard Validation Security Exception | **PASSED** |
| `RatingVerificationLockEmulatorTest` | `test2_successfulGpsMatch_unlocksUiAndPersistsRatingToDatabase` | Geofence Proximity Match, Reactive UI Unlock, Room DB Persistence | **PASSED** |
| `RatingVerificationLockEmulatorTest` | `test3_successfulQrScan_instantlyUnlocksUiAndUpdatesRecord` | QR Scan Pass Verification, Instant UI Unlock, Record Update | **PASSED** |

---

## 🏆 5. Abnahme-Kriterien & QS-Freigabe

- [x] Test-Szenario für alle 3 Abläufe (Unverifiziert, GPS-Match, QR-Scan) detailliert spezifiziert.
- [x] Ausführbarer Integrationstest `RatingVerificationLockEmulatorTest.kt` vorhanden und lauffähig.
- [x] Präzise Assertions auf `isRatingLocked`, `IllegalStateException`, `isVerified` und `verificationMethod`.
- [x] Null-Transparenz-Regel strikt eingehalten (keinerlei KI-Hinweise).
