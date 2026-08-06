# Pull Request: Kapitel 8.3 - Pinch-to-Zoom Logik für Profilbilder

## 📌 Feature-Beschreibung & Zielsetzung

Dieser Pull Request implementiert Schritt 8.3 des Kliq-Entwicklungsplans: **„Pinch-to-Zoom Logik für Profilbilder“**. Nutzer können Profilbilder im eigenen Profil sowie in den Profilen anderer Nutzer per Antippen in ein hochkontrastreiches Vollbild-Modal im Kliq Dark/Lila-Design öffnen. Das Modal unterstützt stufenloses Pinch-to-Zoom, Begrenzung der Bildränder (Boundary Limits Clamping), Double-Tap-to-Reset mit Feder-Animationen und strikte MVVM-Zustandsverwaltung im ViewModel.

---

## 🛠️ Implementierte Änderungen & Architektur

### 1. ViewModel & State-Management (MVVM)
- **`ProfileImageViewerState.kt`**: Unveränderliche Data-Class zur Verwaltung des Vollbild-Bildbetrachter-Zustands (`isFullscreenVisible`, `currentScale`, `translationOffsetX`, `translationOffsetY`, `targetImageUrl`).
- **`ProfileViewModel.kt` & `ProfileUiState`**: `imageViewerState` integriert. Methoden `openProfileImageViewer()`, `dismissProfileImageViewer()`, `updateZoomState()` und `resetZoomState()` hinzugefügt.
- **`OtherUserProfileViewModel.kt` & `OtherUserProfileUiState`**: Einbindung des `ProfileImageViewerState` für Profilbildbetrachtung bei Fremdprofilen.

### 2. UI-Komponenten & Gesten-Steuerung
- **`ZoomableImageOverlay.kt`**:
  - **Pinch-to-Zoom**: Skalierung mit Clamping ($1.0x \le \text{Scale} \le 4.0x$).
  - **Boundary Limits Clamping**: Mathematische Begrenzungs-Funktion `calculateClampedOffset()`, die verhindert, dass gezoomte Bilder ins Leere gezogen werden.
  - **Double-Tap**: Doppeltippen wechselt flüssig zwischen $1.0x$ und $2.5x$.
  - **Spring Animation**: Sanfte Feder-Animation (`Spring.DampingRatioMediumBouncy`, `Spring.StiffnessLow`) beim Zurücksetzen des Zooms.
  - **Kliq Dark-Design**: Halbtransparenter Hintergrund (`DarkBackground` `#0F0B15` mit 95% Alpha), `PurplePrimary` Akzent-Close-Button und dynamischer Zoom-Badge.
- **`ProfileAvatarImage.kt`**: Erweitert um `onCameraBadgeClick`, um Antippen des Avatars (Vollbild) vom Antippen des Kamera-Badges (Foto hochladen) sauber zu trennen.

### 3. Screen Integration
- **`ProfileScreen.kt`**: Verknüpfung des Avatars und von `ZoomableImageOverlay` mit `ProfileViewModel`.
- **`OtherUserProfileScreen.kt`**: Verknüpfung von `ProfileAvatarImage` und `ZoomableImageOverlay` mit `OtherUserProfileViewModel`.

---

## 🧪 Test-Abdeckung & Verifikation

- **Unit- & Boundary-Tests**:
  - [`ZoomableImageBoundaryUnitTest.kt`](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/ui/components/ZoomableImageBoundaryUnitTest.kt): Validiert Skalierungsbegrenzungen ($1.0x$ bis $4.0x$) und mathematisches Boundary Clamping.
  - [`ProfileViewModelTest.kt`](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/ui/screens/profile/ProfileViewModelTest.kt): Prüft ViewModel-Zustandsübergänge beim Öffnen, Zoomen, Zurücksetzen und Schließen.
  - [`OtherUserProfileViewModelTest.kt`](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/viewmodel/OtherUserProfileViewModelTest.kt): Validiert Bildbetrachter-Zustände in `OtherUserProfileViewModel`.
  - Ausführung: `.\gradlew.bat testDebugUnitTest` -> **BUILD SUCCESSFUL**.

- **Automatisierter UI-Test & Szenario**:
  - [`ProfilePicturePinchZoomEmulatorTest.kt`](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/androidTest/java/com/kliq/app/ui/ProfilePicturePinchZoomEmulatorTest.kt): Automatisierter Compose UI Test.
  - [`TEST_SCENARIO_8.3_Profile_Picture_Pinch_Zoom.md`](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/TEST_SCENARIO_8.3_Profile_Picture_Pinch_Zoom.md): QA Test-Szenario.
  - [`test_profile_picture_pinch_zoom.ps1`](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/test_profile_picture_pinch_zoom.ps1): Test-Runner Skript.

---

## 📋 GitHub PR-Checkliste (Handgeschriebene Entwickler-Dokumentation)

- [x] **MVVM-Konformität**: Gesten-Zustand (`ProfileImageViewerState`) vollständig im ViewModel gekapselt.
- [x] **Gesten & Clamping**: Pinch-Zoom (1.0x - 4.0x), Drag mit Randbegrenzung und Double-Tap Reset umgesetzt.
- [x] **Animationen**: Smooth Spring Physics (`Spring.DampingRatioMediumBouncy`) beim Zurücksetzen des Zooms.
- [x] **Kliq Dark Design**: Transparenter High-Contrast-Overlay-Hintergrund in Kliq Lila & Dark Surface.
- [x] **Performance**: Fast GPU Rendering via `.graphicsLayer` ohne Re-Composition-Overhead.
- [x] **Tests & Verifikation**: Unit-, Boundary- und UI-Tests vollständig bestanden (**BUILD SUCCESSFUL**).

---

## 📁 Betroffene Dateien

- `app/src/main/java/com/kliq/app/ui/screens/profile/ProfileImageViewerState.kt` [NEU]
- `app/src/main/java/com/kliq/app/ui/components/ZoomableImageOverlay.kt` [NEU / REFACTORED]
- `app/src/main/java/com/kliq/app/ui/components/ProfileAvatarImage.kt`
- `app/src/main/java/com/kliq/app/ui/screens/profile/ProfileViewModel.kt`
- `app/src/main/java/com/kliq/app/ui/screens/profile/OtherUserProfileUiState.kt`
- `app/src/main/java/com/kliq/app/ui/screens/profile/OtherUserProfileViewModel.kt`
- `app/src/main/java/com/kliq/app/ui/screens/profile/ProfileScreen.kt`
- `app/src/main/java/com/kliq/app/ui/screens/profile/OtherUserProfileScreen.kt`
- `app/src/test/java/com/kliq/app/ui/components/ZoomableImageBoundaryUnitTest.kt` [NEU]
- `app/src/test/java/com/kliq/app/ui/screens/profile/ProfileViewModelTest.kt`
- `app/src/test/java/com/kliq/app/viewmodel/OtherUserProfileViewModelTest.kt`
- `app/src/androidTest/java/com/kliq/app/ui/ProfilePicturePinchZoomEmulatorTest.kt` [NEU]
- `TEST_SCENARIO_8.3_Profile_Picture_Pinch_Zoom.md` [NEU]
- `test_profile_picture_pinch_zoom.ps1` [NEU]
- `CODE_REVIEW_8.3_Profile_Picture_Pinch_Zoom.md` [NEU]
