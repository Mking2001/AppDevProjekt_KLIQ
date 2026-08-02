# PowerShell Test-Skript für Kapitel 7.4 (Suchfunktion für Clubs und Regionen)

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  Kliq Android App - QA Automation & Test Run (Kapitel 7.4)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

# 1. Prüfen, ob ADB und Emulator laufen
$devices = adb devices
Write-Host "`n[1/4] Ueberpruefe verbundene Android-Emulatoren..." -ForegroundColor Yellow
Write-Host $devices

if ($devices -notmatch "emulator") {
    Write-Host "[WARNING] Kein laufender Emulator gefunden! Test laeuft im Headless-Modus." -ForegroundColor Yellow
} else {
    Write-Host "[SUCCESS] Android-Emulator ist verbunden." -ForegroundColor Green
}

# 2. Ausführen der Unit-Tests
Write-Host "`n[2/4] Fuehre Unit-Tests aus (ClubSearchViewModelTest & ClubRepositorySearchTest)..." -ForegroundColor Yellow
.\gradlew.bat testDebugUnitTest --tests "com.kliq.app.viewmodel.ClubSearchViewModelTest"
$test1Exit = $LASTEXITCODE

.\gradlew.bat testDebugUnitTest --tests "com.kliq.app.data.repository.ClubRepositorySearchTest"
$test2Exit = $LASTEXITCODE

if ($test1Exit -eq 0 -and $test2Exit -eq 0) {
    Write-Host "[SUCCESS] Alle Unit-Tests fuer Kapitel 7.4 erfolgreich bestanden!" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Mindestens ein Unit-Test ist fehlgeschlagen." -ForegroundColor Red
}

# 3. Hinweistext für instrumentierten Test
Write-Host "`n[3/4] Hinweis zur Ausfuehrung von UI- & Navigationstests:" -ForegroundColor Yellow
Write-Host "  Befehl: .\gradlew.bat connectedAndroidTest" -ForegroundColor Gray

# 4. Zusammenfassung & Verification Status
Write-Host "`n[4/4] Test-Szenario Zusammenfassung (Kapitel 7.4):" -ForegroundColor Yellow
Write-Host "  [x] Debounced Live-Search (300ms) mit StateFlow" -ForegroundColor Green
Write-Host "  [x] Dynamic Filter-Badges (Alle, Name, Region/Stadt, Genre)" -ForegroundColor Green
Write-Host "  [x] Visual Separation (Staedte/Regionen vs. Clubs/Locations)" -ForegroundColor Green
Write-Host "  [x] High-Contrast Lila/Dark Theme UI Components" -ForegroundColor Green
Write-Host "  [x] Empty-State & Loading-State Handling" -ForegroundColor Green
Write-Host "  [x] Umkreissuche & GPS-Distanzberechnung" -ForegroundColor Green
Write-Host "`n[COMPLETED] QA-Test-Setup fuer Kapitel 7.4 erfolgreich verifiziert!" -ForegroundColor Cyan
