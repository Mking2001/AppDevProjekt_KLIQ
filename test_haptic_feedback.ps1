# PowerShell Test Runner for Kapitel 8.5: Haptic Feedback Management
param (
    [switch]$Emulator
)

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host " Running Kliq Haptic Feedback Test Suite (Kapitel 8.5)" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

if ($Emulator) {
    Write-Host "Monitoring Emulator Logcat for Haptic Feedback Trigger Events..." -ForegroundColor Yellow
    Write-Host "Filtering Logcat tag: HapticFeedbackManager (Press Ctrl+C to stop)`n" -ForegroundColor Yellow
    $logcatCmd = "adb logcat -s HapticFeedbackManager"
    Invoke-Expression $logcatCmd
} else {
    $testCmd = ".\gradlew.bat testDebugUnitTest --tests `"com.kliq.app.util.HapticFeedbackManagerTest`""
    Write-Host "Executing HapticFeedbackManager Unit Tests: $testCmd" -ForegroundColor Yellow
    Invoke-Expression $testCmd

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n[SUCCESS] Haptic Feedback Unit Tests completed successfully!" -ForegroundColor Green
    } else {
        Write-Host "`n[FAILURE] Haptic Feedback Unit Tests failed with exit code $LASTEXITCODE" -ForegroundColor Red
    }
}
