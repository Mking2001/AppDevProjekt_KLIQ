# QA Checklist: Kapitel 5.7 - QR-Code-Scanner für Freundesanfragen/Verifizierung

## 1. Permission & Security Handling
- [x] Erstaufruf ohne Kamera-Berechtigung zeigt den maßgeschneiderten Kliq Permission Request Screen an.
- [x] Gewährung der Kamera-Berechtigung startet unverzüglich die CameraX-Vorschau.
- [x] Ablehnung der Kamera-Berechtigung zeigt eine verständliche Hinweis-Meldung im Kliq-Dark-Design.

## 2. Sensorik & Scanning (CameraX & ML Kit)
- [x] Live CameraX Preview ist korrekt im Layout zentriert und skaliert.
- [x] Zentrierter Scan-Rahmen besitzt abgerundete Ecken (`24.dp`) und leuchtenden Lila-Rand (`#8A2BE2`).
- [x] Vertikal wandernde Laser-Scan-Linie mit Gradienten-Animation läuft flüssig in Dauerschleife.
- [x] ML Kit Barcode Scanning analysiert QR-Codes in Echtzeit.
- [x] Bei erfolgreichem Scan wird haptisches Feedback (`HapticFeedbackType.LongPress`) ausgelöst.
- [x] Kamera-Analyse wird während der Auswertung pausiert.

## 3. Logik & State Management (`QRScannerViewModel` & UseCase)
- [x] `VerifyQRCodeUseCase` parst `kliq://user/verify/{userId}` und generische Kliq-Token korrekt.
- [x] Selbst-Scans werden abgefangen ("Du kannst deinen eigenen Profil-Code nicht scannen.").
- [x] Bei neuem verifizierten Nutzer wird automatisch eine Freundesanfrage gesendet und in der Room DB hinterlegt.
- [x] Bei bereits bestehender Freundschaft wird der Nutzer verifiziert und die Bewertungssperre im `VerificationService` gelöst.
- [x] Fehlerhafte/fremde QR-Codes lösen eine sprechende Fehlermeldung aus, der Scanner bleibt aktiv.

## 4. UI & Navigation
- [x] Flashlight-Button schaltet den Kamera-Torch-Modus um.
- [x] Navigation zum gescannten Nutzerprofil über "Zum Profil" Button funktioniert.
- [x] Re-Scan Button "Weiteres" / "Erneut versuchen" setzt den Scanner-Zustand zurück.
- [x] Kamera-Ressourcen werden beim Schließen des Scanners sauber freigegeben (Lifecycle-Safety).

## 5. Testabdeckung
- [x] `VerifyQRCodeUseCaseTest` (Unit Tests) -> PASSED
- [x] `QRScannerViewModelTest` (Unit Tests) -> PASSED
- [x] `QRScannerEmulatorTest` (Android Instrumented UI Tests) -> IMPLEMENTED
