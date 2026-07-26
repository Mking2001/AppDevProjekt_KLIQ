# QA Test-Szenario & Dokumentation: Kapitel 5.6 - QR-Code-Generator für das eigene Profil

**App:** Kliq Mobile App  
**Modul:** Profile QR Generator (`QrCodeService`, `ProfileViewModel`, `ProfileQrCodeBottomSheet`)  
**Architektur:** MVVM Pattern (StateFlow, Jetpack Compose, ZXing Native Core Matrix)

---

## 🎯 Test-Szenarien & Simulationsabläufe

### Szenario 1: Initiales Laden & Asynchrone QR-Generierung
* **Ausgangssituation**: Der Nutzer tippt im eigenen Profil auf den Button **"QR-Pass"**.
* **Simulierter Ablauf**:
  1. `ProfileViewModel` ruft `showQrCodeModal()` auf und setzt den UI-State `isQrModalVisible = true` sowie `isGeneratingQrCode = true`.
  2. `QrCodeService.generateQrCodeBitmap(userId)` berechnet die QR-Matrix asynchron auf `Dispatchers.IO` (off-main-thread).
* **Erwartetes Ergebnis**:
  - `ProfileViewModel` erhält das generierte `Bitmap` ohne UI-Lag oder Main-Thread-Blockierung.
  - Das Modal [`ProfileQrCodeBottomSheet`](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/components/ProfileQrCodeBottomSheet.kt) rendert den QR-Code im Kliq Dark-Mode (`#1E1B2E` Card Container, `#7C3AED` Purple Accent).

---

### Szenario 2: Payload-Integritätsprüfung (Decodierung & Protokoll-Match)
* **Ausgangssituation**: Ein QR-Code Bitmap wurde von `QrCodeServiceImpl` generiert.
* **Simulierter Ablauf**:
  1. Extraktion der Pixelfelder aus dem generierten `Bitmap`.
  2. Programmgesteuerte Decodierung via ZXing `QRCodeReader` und `RGBLuminanceSource`.
* **Erwartetes Ergebnis**:
  - Der enthaltene String entspricht exakt der Kliq-Protokollstruktur:
    `kliq://user/verify/{userId}?tag=kliq_profile_v1&ts={timestamp}`.
  - Die `userId` stimmt exakt mit der ID des aktiven Nutzerprofils überein.

---

### Szenario 3: Dynamisches Lifecycle-Handling & Helligkeits-Boost
* **Ausgangssituation**: Der Nutzer betritt eine dunkle Club-Umgebung und öffnet den QR-Pass.
* **Simulierter Ablauf**:
  1. Beim Öffnen des Modals greift die `DisposableEffect`-Logik in Compose:
     `Window.attributes.screenBrightness = BRIGHTNESS_OVERRIDE_FULL (1.0f)`.
  2. Das Banner `"Display-Helligkeit für Club-Scan maximiert"` wird angezeigt.
  3. Der Nutzer schließt die Ansicht via "Fertig" oder Swipen.
* **Erwartetes Ergebnis**:
  - `onDispose` stellt die ursprüngliche Bildschirmhelligkeit des Nutzers wieder her (`BRIGHTNESS_OVERRIDE_NONE`).
  - Der Modal-Zustand `isQrModalVisible` wird auf `false` zurückgesetzt.

---

## 💻 Ausführbare Test-Skripte

### 1. ViewModel, Service & Matrix-Decode Unit-Test-Skript
Das Test-Skript befindet sich in [`ProfileQrCodeScenarioTest.kt`](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/test/java/com/kliq/app/service/ProfileQrCodeScenarioTest.kt).

**Ausführung via Terminal**:
```bash
cmd /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& set PATH=%JAVA_HOME%\bin;%PATH%&& gradlew.bat testDebugUnitTest --tests com.kliq.app.service.ProfileQrCodeScenarioTest"
```

### 2. Emulator UI-Instrumentierungstest-Skript (Compose)
Das UI-Test-Skript befindet sich in [`ProfileQrCodeEmulatorTest.kt`](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/ProfileQrCodeEmulatorTest.kt).

**Ausführung auf dem Emulator**:
```bash
cmd /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& set PATH=%JAVA_HOME%\bin;%PATH%&& gradlew.bat connectedDebugAndroidTest --tests com.kliq.app.ui.ProfileQrCodeEmulatorTest"
```
