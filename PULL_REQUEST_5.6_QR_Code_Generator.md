# Pull Request: Kapitel 5.6 - QR-Code-Generator für das eigene Profil

## 📌 Beschreibung der Änderungen
Dieser Pull Request implementiert den persönlichen **QR-Code-Generator für das eigene Profil** in der **Kliq** Mobile App gemäß Kapitel 5.6 der technischen Spezifikation nach dem MVVM-Muster.

### Key Features:
1. **Off-Main-Thread QR-Code-Generierung**:
   - `QrCodeService` / `QrCodeServiceImpl` kapselt die Payload-Erstellung (`kliq://user/verify/{userId}?tag=kliq_profile_v1&ts={timestamp}`) und ZXing `QRCodeWriter` Matrix-Generierung.
   - Konvertierung in Android `Bitmap` Instanzen erfolgt strikt asynchron über `Dispatchers.IO` ohne Main-Thread-Blockierung.

2. **Reaktive State-Anbindung im ProfileViewModel**:
   - `ProfileUiState` erweitert um `isQrModalVisible`, `qrCodeBitmap`, `isGeneratingQrCode` und `qrPayloadText`.
   - Reaktiv gesteuert via `StateFlow`.

3. **High-Contrast Dark-Mode UI & Automatische Helligkeitsanhebung**:
   - `ProfileQrCodeBottomSheet` im Kliq Dark-Mode (`#1E1B2E` Card, `#7C3AED` Purple Accent).
   - Automatische Anhebung der Display-Helligkeit auf 100% (`BRIGHTNESS_OVERRIDE_FULL`) bei Modal-Anzeige zur Gewährleistung der Scannbarkeit in dunklen Club-Umgebungen.
   - Wiederherstellung der ursprünglichen System-Helligkeit beim Schließen des Modals.

---

## 🗂️ Geänderte und neue Dateien
- `app/build.gradle.kts` (ZXing `com.google.zxing:core:3.5.3` Dependency)
- `app/src/main/java/com/kliq/app/service/QrCodeService.kt` (Service-Schnittstelle)
- `app/src/main/java/com/kliq/app/service/QrCodeServiceImpl.kt` (ZXing Bitmap Matrix Generator)
- `app/src/main/java/com/kliq/app/di/AppModule.kt` (Hilt Dependency Injection Binding)
- `app/src/main/java/com/kliq/app/ui/screens/profile/ProfileViewModel.kt` (StateFlow & Off-Main-Thread Dispatching)
- `app/src/main/java/com/kliq/app/ui/components/ProfileQrCodeBottomSheet.kt` (Compose UI Component & Helligkeitssteuerung)
- `app/src/main/java/com/kliq/app/ui/screens/profile/ProfileScreen.kt` (QR-Pass Button in ProfileHeader & BottomSheet Binding)
- `app/src/test/java/com/kliq/app/service/ProfileQrGeneratorUnitTest.kt` (Unit Test Suite)
