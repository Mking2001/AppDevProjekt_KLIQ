# QA Test-Szenario & UI-Test-Skript: Kapitel 8.3 - Pinch-to-Zoom Logik für Profilbilder

Dieses Dokument beschreibt das vollständige Test-Szenario sowie die Anleitung zur manuellen und automatisierten Überprüfung der **„Pinch-to-Zoom Logik für Profilbilder“** (Kapitel 8.3) im Android-Emulator.

---

## 🛠️ 1. Test-Voraussetzungen

- **Gerät / Emulator**: Android Emulator (API Level 33 oder 34, x86_64 Image).
- **Branch**: `feature/profile-picture-pinch-zoom`.
- **Design-System**: Kliq High-Contrast Dark Mode (`DarkBackground` `#0F0B15`, `DarkSurface` `#1A1523`, `PurplePrimary` `#7C3AED`, `PurplePrimaryLight` `#BB86FC`).
- **Test-Komponenten**:
  - `ProfileScreen.kt` (`com.kliq.app.ui.screens.profile.ProfileScreen`)
  - `OtherUserProfileScreen.kt` (`com.kliq.app.ui.screens.profile.OtherUserProfileScreen`)
  - `ProfileViewModel.kt` (`com.kliq.app.ui.screens.profile.ProfileViewModel`)
  - `OtherUserProfileViewModel.kt` (`com.kliq.app.ui.screens.profile.OtherUserProfileViewModel`)
  - `ProfileImageViewerState.kt` (`com.kliq.app.ui.screens.profile.ProfileImageViewerState`)
  - `ZoomableImageOverlay.kt` (`com.kliq.app.ui.components.ZoomableImageOverlay`)
  - `ProfileAvatarImage.kt` (`com.kliq.app.ui.components.ProfileAvatarImage`)

---

## 🧪 2. Schritt-für-Schritt Test-Szenario

### Schritt 1: Profil-Navigations-Check & Vollbild-Modal-Start
1. Starte die Kliq App im Android-Emulator.
2. Navigiere zum eigenen Profil (`ProfileScreen`) oder öffne das Profil eines anderen Kliq-Nutzers (`OtherUserProfileScreen`).
3. Tippe direkt auf das kreisförmige Profilbild.
4. **Erwartetes Ergebnis**:
   - Ein abgedunkeltes, halbtransparentes Vollbild-Modal im Kliq Dark-Design (`#0F0B15` mit 95% Alpha) öffnet sich flüssig.
   - Oben rechts befindet sich der Schließen-Button mit Lila-Akzent-Rand (`PurplePrimary`).
   - Das Profilbild wird zentriert in voller Auflösung dargestellt.
   - Der Zustand in `ProfileImageViewerState` zeigt `isFullscreenVisible = true` und `currentScale = 1.0`.

---

### Schritt 2: Gesten-Simulation (Pinch-In & Pinch-Out)
1. Simuliere eine Zwei-Finger-Pinch-Zoom-Geste auf dem Bild (Spreizen der Finger von 1.0x auf 3.0x).
2. Vergrößere den Zoom weiter über 4.0x hinaus.
3. **Erwartetes Ergebnis**:
   - Das Profilbild skaliert flüssig und stufenlos ohne UI-Ruckeln.
   - Das **Scale Clamping** greift präzise: Der maximale Zoom-Faktor wird exakt bei **4.0x** begrenzt.
   - Sobald der Zoom-Faktor $1.0x$ übersteigt, erscheint oben zentriert ein transparenter Chip mit lila Rand, der den aktuellen Zoom-Faktor anzeigt (z. B. **„2.5x“** bzw. **„4.0x“**).

---

### Schritt 3: Pan / Drag mit Boundary Limits Clamping
1. Wenn das Profilbild auf z. B. **2.5x** herangezoomt ist, simuliere Wischgesten (Drag / Translate) in alle Richtungen (links, rechts, oben, unten).
2. Versuche, das gezoomte Bild stark nach außen über den Bildschirmrand hinaus zu ziehen.
3. **Erwartetes Ergebnis**:
   - Der Nutzer kann sich frei im vergrößerten Bildausschnitt bewegen.
   - Die **Boundary Limits Clamping**-Logik (`calculateClampedOffset`) verhindert strikt, dass die Bildränder ins Leere gezogen werden. Der Bildrand stoppt exakt an der Schnittkante des Bildschirms.

---

### Schritt 4: Double-Tap-to-Reset & Smooth Spring Animation
1. Führe ein schnelles Doppeltippen (Double-Tap) auf das gezoomte Bild aus.
2. Führe erneut ein Doppeltippen auf das zurückgesetzte Bild aus.
3. **Erwartetes Ergebnis**:
   - Beim ersten Double-Tap animiert der Zoom sanft mit einer Feder-/Bounce-Animation (`Spring.DampingRatioMediumBouncy`, `Spring.StiffnessLow`) zurück auf exakt **1.0x** und setzt die Verschiebungs-Offsets auf $(0, 0)$ zurück.
   - Beim zweiten Double-Tap zoomt das Bild flüssig auf den Ziel-Zoomfaktor **2.5x** heran.

---

### Schritt 5: UI-State Reset, Schließen & Stabilitäts-Test
1. Tippe auf den Schließen-Button oben rechts (`Icons.Default.Close`) oder betätige die Android-Zurück-Taste.
2. Öffne das Vollbild-Modal erneut.
3. Führe schnelle Wiederholungs-Gesten (z. B. 10x schnelles Zoomen, Panning und Schließen) durch.
4. **Erwartetes Ergebnis**:
   - Das Vollbild-Modal schließt sich ohne Verzögerung.
   - Im `ProfileViewModel` / `OtherUserProfileViewModel` wird der Zustand im `ProfileImageViewerState` zurückgesetzt (`isFullscreenVisible = false`, `currentScale = 1.0f`, `translationOffsetX = 0.0f`, `translationOffsetY = 0.0f`).
   - Bei schnellen Gesten-Wiederholungen treten weder Memory Leaks noch Frames-Drops / UI-Hänger auf.

---

## 🤖 3. Automatisierte Test-Ausführung

### A) Automated Unit- & Boundary-Tests (Local JVM)
Verifiziert die mathematische Offset-Begrenzung sowie die ViewModel-Zustands-Verwaltung:
```powershell
.\gradlew.bat testDebugUnitTest `
    --tests "com.kliq.app.ui.components.ZoomableImageBoundaryUnitTest" `
    --tests "com.kliq.app.ui.screens.profile.ProfileViewModelTest" `
    --tests "com.kliq.app.viewmodel.OtherUserProfileViewModelTest"
```

### B) Automated Instrumented Compose UI Test (Android Emulator)
Führt die Gesten-Simulationen und Interaktionen programmatisch auf dem gestarteten Emulator aus:
```powershell
$env:JAVA_HOME="C:\Users\Felix\jdk17\jdk-17.0.10+7"
$env:ANDROID_HOME="C:\Users\Felix\AppData\Local\Android\Sdk"

.\gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.ProfilePicturePinchZoomEmulatorTest
```

### C) Automatisiertes Test-Skript (PowerShell)
```powershell
.\test_profile_picture_pinch_zoom.ps1
```

---

## 📋 4. Verifikations-Checkliste

| Test-Schritt | Prüfpunkt | Status |
| :--- | :--- | :---: |
| **Schritt 1** | Tap auf Profilbild öffnet dunkles High-Contrast Zoom-Overlay | **PASSED** |
| **Schritt 2** | Pinch-to-Zoom Skalierung stufenlos & Clamping fest auf Max 4.0x | **PASSED** |
| **Schritt 3** | Pan/Drag im gezoomten Zustand mit Boundary Limits Clamping | **PASSED** |
| **Schritt 4** | Double-Tap Reset mit flüssiger Feder-/Bounce-Animation | **PASSED** |
| **Schritt 5** | Modal-Schließen setzt ViewModel-Zustand auf 1.0x zurück | **PASSED** |
| **Performance** | Keine Memory Leaks oder Frame Drops bei schnellen Gesten | **PASSED** |
