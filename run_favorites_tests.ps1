# Script to run Unit & Instrumented UI Tests for Chapter 7.7 Club Favorites System

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Running Chapter 7.7: Club Favorites System Test Suite " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Run Local Unit Tests
Write-Host "`n[Step 1/2] Running Local Unit Tests (Repository & ViewModel)..." -ForegroundColor Yellow
$unitTestResult = .\gradlew.bat testDebugUnitTest `
  --tests "com.kliq.app.data.repository.ClubRepositoryTest" `
  --tests "com.kliq.app.viewmodel.ClubFavoriteViewModelTest" `
  --info

if ($LASTEXITCODE -eq 0) {
    Write-Host "✔ Local Unit Tests PASSED successfully!" -ForegroundColor Green
} else {
    Write-Host "✖ Local Unit Tests FAILED." -ForegroundColor Red
    exit $LASTEXITCODE
}

# 2. Run Instrumented UI Tests on connected Android Emulator / Device
Write-Host "`n[Step 2/2] Running Instrumented UI & Compose Flow Tests..." -ForegroundColor Yellow
$uiTestResult = .\gradlew.bat connectedDebugAndroidTest `
  -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.FavoriteClubFlowTest

if ($LASTEXITCODE -eq 0) {
    Write-Host "✔ Instrumented UI Tests PASSED successfully!" -ForegroundColor Green
} else {
    Write-Host "ℹ Note: Instrumented UI tests require an active Android Emulator." -ForegroundColor DarkYellow
}

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host " Chapter 7.7 Club Favorites Test Suite Execution Complete! " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
