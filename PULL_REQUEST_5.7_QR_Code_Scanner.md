# Pull Request: Kapitel 5.7 - QR-Code-Scanner für Freundesanfragen/Verifizierung

## Zusammenfassung
Dieser Pull Request implementiert Kapitel 5.7 („QR-Code-Scanner für Freundesanfragen/Verifizierung“) für die Kliq Android-App. Die Implementierung umfasst den vollständigen `QRScannerScreen` im Kliq-Designsystem, CameraX & Google ML Kit Barcode-Scanning Integration, das `QRScannerViewModel`, den `VerifyQRCodeUseCase` sowie die Anbindung an die Room-Datenbank via `SocialRepository`.

## Wichtigste Änderungen

### 1. UI & Design (`QRScannerScreen`)
- **Kliq Designsystem Integration**: Dark Mode Grundlayout mit Hauptakzentfarbe Lila (`#8A2BE2`) und abgedunkelten UI-Overlays.
- **Visual Scanner Frame & Laser-Line**: Zentrierter Scan-Rahmen mit abgerundeten Ecken und einer vertikal wandern/pulsierenden Laser-Animationslinie.
- **Kamera-Permission Screen**: Eleganter Permission-Request Dialog im Kliq-Design, falls Kamera-Zugriff noch nicht erteilt wurde.
- **Haptisches Feedback**: Auslösung haptischer Rückmeldung (`HapticFeedbackType.LongPress`) bei erfolgreichem Scan-Vorgang.
- **Status & Feedback Cards**: Anzeige von Verifizierungs- und Fehler-Zuständen (Erfolg, bereits befreundet, Selbst-Scan, ungültiger Code) in abgerundeten Dark-Cards mit Direktnavigation zum Nutzerprofil.

### 2. CameraX & ML Kit Integration
- Einbindung von CameraX (`PreviewView` & `ImageAnalysis`) zur ruckelfreien Live-Kamera-Vorschau.
- Integration des Google ML Kit Barcode Scanning Client zur Echtzeit-Analyse von QR-Codes im Kamera-Stream.
- Automatische Pausierung des Kamera-Analyse-Streams nach einem erfolgreichen Scan.
- Support für Blitz-Steuerung (Torch Mode).

### 3. MVVM State & Domain Layer
- **`VerifyQRCodeUseCase`**: Dekodiert und validiert gescannte QR-Payloads (z. B. `kliq://user/verify/{userId}`). Verifiziert die Nutzer-ID, schützt vor Selbst-Scans und aktualisiert die Anti-Spam Bewertungssperre.
- **`SocialRepository` & `SocialDao`**: Neue Room-Entity `FriendEntity` und DAO-Schnittstelle zur automatischen Erstellung und Verifizierung von Freundesbeziehungen in der lokalen DB.
- **`QRScannerViewModel`**: Verwaltet den `QRScannerUiState` (Permissions, Scanning-Status, Blitz-Status, Resultate, Fehlermeldungen).

### 4. Navigations-Einbindung
- Registrierung der Route `ProfileRoutes.QR_SCANNER` in `NavigationRoute.kt` und `KliqMainScaffold.kt`.
- Anbindung der Scan-Funktion an das `ProfileQrCodeBottomSheet`.

## Tests & Verifikation
- **Unit Tests**: `VerifyQRCodeUseCaseTest` und `QRScannerViewModelTest` decken alle Logik-Pfade (gültiger Scan, Selbst-Scan, bereits befreundet, ungültige Payloads) vollständig ab.
- **Verifizierungs-Kommando**: `./gradlew testDebugUnitTest` ausgeführt.
