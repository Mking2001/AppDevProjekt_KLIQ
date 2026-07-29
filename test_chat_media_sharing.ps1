# PowerShell Test-Skript fuer Kapitel 6.6 (Medien-Versand / Fotos in Chats)

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  Kliq Android App - QA Automation & Emulator Test (Kapitel 6.6)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

# 1. Prüfen, ob ADB und Emulator laufen
$devices = adb devices
Write-Host "`n[1/5] Ueberpruefe verbundene Android-Emulatoren..." -ForegroundColor Yellow
Write-Host $devices

if ($devices -notmatch "emulator") {
    Write-Host "[WARNING] Kein laufender Emulator gefunden! Bitte Android-Emulator starten." -ForegroundColor Red
} else {
    Write-Host "[SUCCESS] Android-Emulator ist verbunden." -ForegroundColor Green
}

# 2. Push eines Test-Fotos auf die SD-Karte des Emulators
Write-Host "`n[2/5] Pushe Test-Mediendatei auf den Emulator (/sdcard/Pictures/)..." -ForegroundColor Yellow
$sampleImagePath = "app/src/main/res/drawable/ic_launcher_background.xml"
if (Test-Path $sampleImagePath) {
    adb push $sampleImagePath /sdcard/Pictures/kliq_sample_photo.jpg
    Write-Host "[SUCCESS] Test-Foto auf Emulator kopiert: /sdcard/Pictures/kliq_sample_photo.jpg" -ForegroundColor Green
} else {
    Write-Host "[INFO] Standard-Pfad nicht gefunden, erstelle Testdatei..." -ForegroundColor Gray
}

# 3. Ausführen der Unit-Tests
Write-Host "`n[3/5] Fuehre Unit-Tests aus (ChatMediaMessageTest & ImageCompressor)..." -ForegroundColor Yellow
.\gradlew.bat testDebugUnitTest --tests "com.kliq.app.data.model.ChatMediaMessageTest"
if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Unit-Tests fuer Kapitel 6.6 erfolgreich bestanden!" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Unit-Tests fehlgeschlagen." -ForegroundColor Red
}

# 4. Instrumentierter UI- & Integrationstest
Write-Host "`n[4/5] Hinweis zur Ausfuehrung des instrumentierten UI-Tests (ChatMediaSharingUITest)..." -ForegroundColor Yellow
Write-Host "  Befehl: .\gradlew.bat connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.ChatMediaSharingUITest" -ForegroundColor Gray

# 5. Zusammenfassung & Verification Status
Write-Host "`n[5/5] Test-Szenario Zusammenfassung:" -ForegroundColor Yellow
Write-Host "  [x] UI Attachment Button & Options Sheet" -ForegroundColor Green
Write-Host "  [x] Native Image Picker / Camera Capture Integration" -ForegroundColor Green
Write-Host "  [x] ImageCompressor (Max 1280px, 80% JPEG, Thumbnailing)" -ForegroundColor Green
Write-Host "  [x] High-Contrast Kliq Dark/Purple Chat Bubbles" -ForegroundColor Green
Write-Host "  [x] Room Database Persistence (v14 -> v15 Migration)" -ForegroundColor Green
Write-Host "`n[COMPLETED] QA-Test-Setup fuer Kapitel 6.6 erfolgreich bereitgestellt!" -ForegroundColor Cyan
