# PowerShell Test Runner for Kapitel 8.4: Screen Transitions
param (
    [switch]$Emulator
)

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host " Running Kliq Screen Transitions Test Suite (Kapitel 8.4)" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

if ($Emulator) {
    $testCmd = ".\gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.navigation.ScreenTransitionsEmulatorTest"
    Write-Host "Executing Instrumented Emulator UI Test: $testCmd" -ForegroundColor Yellow
} else {
    $testCmd = ".\gradlew.bat testDebugUnitTest --tests `"com.kliq.app.viewmodel.NavigationViewModelTest`" --tests `"com.kliq.app.ui.navigation.KliqScreenTransitionsTest`""
    Write-Host "Executing Unit Tests: $testCmd" -ForegroundColor Yellow
}

Invoke-Expression $testCmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[SUCCESS] Screen Transition test execution completed successfully!" -ForegroundColor Green
} else {
    Write-Host "`n[FAILURE] Screen Transition tests failed with exit code $LASTEXITCODE" -ForegroundColor Red
}
