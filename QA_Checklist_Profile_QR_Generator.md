# QA Checklist: Kapitel 5.6 - QR-Code-Generator für das eigene Profil

## 🧪 Entwickler- & QA-Prüfprotokoll

| Test-Kategorie | Prüfschritt | Erwartetes Ergebnis | Status |
| :--- | :--- | :--- | :---: |
| **Service Layer** | `QrCodeService.generateProfileQrPayload(userId)` | Erzeugt einen validen Kliq-Protokoll-URI (`kliq://user/verify/{userId}`) mit Timestamp. | PASS |
| **Performance** | Off-Main-Thread Generierung | QR-Matrix-Berechnung via ZXing läuft auf `Dispatchers.IO`, UI bleibt 100% flüssig. | PASS |
| **StateFlow** | `showQrCodeModal()` im `ProfileViewModel` | `isQrModalVisible = true` und `qrCodeBitmap` wird reaktiv im UI-State aktualisiert. | PASS |
| **Club-UX** | Automatische Helligkeitsanhebung | Beim Öffnen des Modals wird die Display-Helligkeit auf `BRIGHTNESS_OVERRIDE_FULL` gesetzt. | PASS |
| **Club-UX** | Helligkeits-Wiederherstellung | Beim Schließen des Modals wird die ursprüngliche System-Helligkeit wiederhergestellt. | PASS |
| **Design System** | Kliq Dark-Mode Schema | `#1E1B2E` Card-Hintergrund, `#7C3AED` Purple Accent, weißer Kontrast-Rahmen für QR-Code. | PASS |
| **Unit Tests** | `ProfileQrGeneratorUnitTest.kt` | Alle Testfälle für Payload, Bitmap-Generierung und State-Wechsel verlaufen grün. | PASS |
