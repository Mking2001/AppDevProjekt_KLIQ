# PowerShell Test Runner for Kapitel 8.4: Screen Transitions
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host " Running Kliq Screen Transitions Test Suite (Kapitel 8.4)" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

$testCmd = ".\gradlew.bat testDebugUnitTest --tests `"com.kliq.app.viewmodel.NavigationViewModelTest`" --tests `"com.kliq.app.ui.navigation.KliqScreenTransitionsTest`""
Write-Host "Executing command: $testCmd" -ForegroundColor Yellow

Invoke-Expression $testCmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[SUCCESS] All Screen Transition tests passed successfully!" -ForegroundColor Green
} else {
    Write-Host "`n[FAILURE] Screen Transition tests failed with exit code $LASTEXITCODE" -ForegroundColor Red
}
