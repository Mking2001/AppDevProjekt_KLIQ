# QA Checkliste & Test-Szenario: Kliq Bild-Upload-Logik (Kapitel 3.6)

Diese Dokumentation beschreibt die manuellen und automatisierten Test-Szenarien zur Prüfung der Profilbild-Upload-Logik (Kamera- und Galerie-Zugriff, Komprimierung, Permission-Handling und MVVM-State-Updates) in der nativen Kliq Mobile-App.

---

## 📱 Emulator-Voraussetzungen & Vorbereitung

1. **Android Emulator Setup:**
   - Emulator mit API Level 30+ (z.B. Pixel 6 / Android 13/14 AVD) in Android Studio starten.
   - Stelle sicher, dass die Emulator-Kamera aktiviert ist (`Settings` -> `Camera` -> `VirtualScene` oder `Emulated`).
   - Lege über den Emulator 1–2 Testbilder in der Fotogalerie ab (`Drag & Drop` von Bildern in das Emulator-Fenster).

2. **App-Start:**
   - App starten und zum Onboarding-Schritt **"Profil erstellen"** oder zur **Profilansicht** navigieren.

---

## 🧪 Manual QA Test-Szenarien

### 1. UI & BottomSheet Aufruf-Test
- [x] **Initialer Zustand:**
  - Der Profilbild-Container zeigt ein lila Kamera-Icon bzw. ein Benutzer-Avatar-Symbol mit einem hochkontrastigen Fuchsia/Lila-Gradientenrahmen und einem Kamera-Badge unten rechts.
- [x] **Klick auf Avatar / Kamera-Badge:**
  - Ein Klick auf den Profilbild-Platzhalter öffnet ein ModalBottomSheet im Kliq Dark-Theme (`DarkSurface`).
  - Das BottomSheet enthält die Optionen:
    - **"Kamera"** (Kamera-Icon)
    - **"Galerie / Foto-Mediathek"** (Galerie-Icon)
    - **"Schließen"** (X-Button oben rechts)

---

### 2. Testfall Kamera-Aufnahme
- [x] **Berechtigungsmeldung & Aufruf:**
  - Klick auf **"Kamera"**. Bei der ersten Ausführung erscheint der System-Berechtigungsdialog (`CAMERA`).
  - **Aktion:** Berechtigung auf *"Bei Nutzung der App"* tippen.
  - Die System-Kamera des Emulators öffnet sich im Vollbild.
- [x] **Foto aufnehmen & Rendern:**
  - Über den Auslöser-Button des Emulators ein Foto aufnehmen und bestätigen.
  - **Erwartetes Ergebnis:**
    - Das Foto wird im Hintergrund durch `ImageCompressor` auf maximal 800x800 px skaliert und als JPEG komprimiert.
    - Während der Verarbeitung wird ein Ladekreis (`CircularProgressIndicator`) im Avatar-Container angezeigt.
    - Das verarbeitete Bild wird flüssig im kreisförmigen Avatar-Container dargestellt.

---

### 3. Testfall Galerie / Foto-Mediathek
- [x] **PhotoPicker Aufruf:**
  - Klick auf den Avatar -> Auswahl **"Galerie / Foto-Mediathek"**.
  - Der Android-System-PhotoPicker öffnet sich.
- [x] **Bildauswahl & State-Update:**
  - Ein Bild aus den Emulator-Fotos auswählen.
  - **Erwartetes Ergebnis:**
    - Das ausgewählte Bild wird unverzüglich in der App eingelesen, skaliert und im internen App-Speicher abgelegt.
    - Der UI-State im `ProfileCreationViewModel` bzw. `ProfileViewModel` aktualisiert `profilePictureUrl` reaktiv.
    - Das neue Profilbild wird auf dem Bildschirm gerendert.

---

### 4. Testfall Berechtigungsverweigerung (Permission Denial & Fallback)
- [x] **Kamera-Berechtigung verweigern:**
  - App-Einstellungen im Emulator öffnen (`App Info` -> `Permissions` -> `Camera` -> `Don't allow`).
  - Zurück zur Kliq-App wechseln, Avatar anklicken und **"Kamera"** wählen.
- [x] **Erwartetes Fallback-Verhalten:**
  - Die App stürzt **nicht** ab.
  - Eine verständliche Snackbar/Fehlermeldung wird unten angezeigt (z.B. *"Kamerazugriff wurde verweigert. Erteile die Berechtigung in den Einstellungen."*).
  - Der vorherige Profilbild-Zustand bleibt stabil erhalten.

---

## ⚙️ 5. Automatisierte UI- & Integrationstests

Die automatisierten Tests zur Absicherung der UI-Komponenten und State-Updates befinden sich unter:
- `app/src/androidTest/java/com/kliq/app/ui/screens/onboarding/ProfileImageUploadUiTest.kt`
- `app/src/test/java/com/kliq/app/ui/screens/onboarding/ProfileCreationViewModelTest.kt`
- `app/src/test/java/com/kliq/app/ui/screens/profile/ProfileViewModelTest.kt`
