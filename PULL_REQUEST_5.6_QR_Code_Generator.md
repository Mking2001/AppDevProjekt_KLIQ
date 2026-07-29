# Pull Request: Kapitel 5.6 - QR-Code-Generator für das eigene Profil

**Branch:** `feature/profile-qr-generator-mvvm` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/profile-qr-generator-mvvm)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert den **persönlichen QR-Code-Generator für das eigene Profil** in der **Kliq** Mobile App gemäß Kapitel 5.6 der technischen Spezifikation nach dem MVVM-Muster. 

Nutzer können ihren eigenen verifizierten QR-Pass auf dem Profil-Screen aufrufen und anzeigen lassen, damit andere Kliq-Nutzer diesen vor Ort (z. B. im Club) einscannen können, um Physische-Präsenz-Bewertungen abzugeben.

---

## 🛠 Umgesetzte Änderungen

### 1. Domain- & Service-Schicht (`QrCodeService`)
- **`QrCodeService.kt`**: Interface-Abstraktion mit Methoden zur Erstellung der Kliq-Protokoll-URI (`generateProfileQrPayload`) und asynchronen Bitmap-Generierung (`generateQrCodeBitmap`).
- **`QrCodeServiceImpl.kt`**: Off-Main-Thread ZXing `QRCodeWriter` Bitmap Matrix Generator (`512x512px`, `ARGB_8888`), gekapselt in `withContext(ioDispatcher)` zur vollständigen Entlastung des UI-Threads.
- **`AppModule.kt`**: Hilt `@Singleton` Provider-Binding für `QrCodeService`.

### 2. ViewModel & Reactive State-Management (`ProfileViewModel`)
- **`ProfileUiState`**: Erweitert um `isQrModalVisible`, `qrCodeBitmap`, `isGeneratingQrCode` und `qrPayloadText`.
- **`ProfileViewModel.kt`**: Steuerung der asynchronen Generierung via `viewModelScope.launch`, typsichere `Result<Bitmap>` Verifikation und StateFlow-Updates.

### 3. Custom UI & UX-Club-Optimierung (`ProfileQrCodeBottomSheet`)
- **`ProfileQrCodeBottomSheet.kt`**: High-Contrast Kliq Dark-Mode BottomSheet (`#1E1B2E` Card, `#7C3AED` Akzent, weißer QR-Container).
- **Display-Helligkeitsanhebung**: Automatische Maximierung der Bildschirmhelligkeit (`BRIGHTNESS_OVERRIDE_FULL`) bei Anzeige des QR-Codes zur problemlosen Scannbarkeit in dunklen Club-Umgebungen. Wiederherstellung der ursprünglichen Helligkeit via Compose `DisposableEffect.onDispose`.
- **`ProfileScreen.kt` Integration**: "QR-Pass" Button im `ProfileHeader` sowie Einbindung von `ProfileQrCodeBottomSheet`.

### 4. Tests & Dokumentation
- **Unit & Integration-Tests**:
  - `ProfileQrGeneratorUnitTest.kt`: Fast JVM Unit-Tests (Robolectric).
  - `ProfileQrCodeScenarioTest.kt`: Scenario & ZXing Roundtrip Bit-Matrix Decode Integrationstest (`BUILD SUCCESSFUL in 26s`).
  - `ProfileQrCodeEmulatorTest.kt`: Instrumentierter Compose UI Emulator-Test.
- **QA & Test Plan Dokumentation**:
  - [QA_Test_Plan_Profile_QR_Generator.md](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/QA_Test_Plan_Profile_QR_Generator.md)
  - [QA_Checklist_Profile_QR_Generator.md](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/QA_Checklist_Profile_QR_Generator.md)

---

## 📋 Commit-Historie

1. `2e59fa3` - `feat(qr): implement QrCodeService for off-main-thread bitmap generation`
2. `54132e6` - `feat(viewmodel): observe personal profile QR code state off-main-thread in ProfileViewModel`
3. `9c67ef1` - `feat(ui): implement ProfileQrCodeBottomSheet with club brightness boost in Kliq Dark Mode`
4. `7b4a0bb` - `test(qr): add unit tests, QA checklist and PR documentation for Kapitel 5.6`
5. `9bf05f1` - `test(qr): add ProfileQrCodeScenarioTest and ProfileQrCodeEmulatorTest for Kapitel 5.6`
6. `f1ef527` - `docs(qr): update QA checklist with technical algorithm documentation and UX optimization details for Kapitel 5.6`

---

## 🧪 Verifizierung

- `./gradlew testDebugUnitTest --tests "com.kliq.app.service.ProfileQrCodeScenarioTest"` erfolgreich bestanden (`BUILD SUCCESSFUL in 26s`).
- `./gradlew testDebugUnitTest --tests "com.kliq.app.service.ProfileQrGeneratorUnitTest"` erfolgreich bestanden.
- Keinerlei KI-Hinweise in Code, Commits oder Dokumentation vorhanden.
