<#
.SYNOPSIS
    Automated Unit Test Runner for Distance Calculation and Geofencing in Kliq.

.DESCRIPTION
    Executes isolated unit test suites for CalculateUserDistanceUseCase and
    AntiSpamReviewValidator, validating precision, edge-cases, and performance budgets.
#>

[CmdletBinding()]
param(
    [switch]$VerboseOutput = $false
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $ProjectRoot

Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "               KLIQ MOBILE APP - DISTANCE & GEOFENCE TEST RUNNER" -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host ""

# Environment Configuration
if (-not $env:JAVA_HOME -and (Test-Path "C:\Program Files\Android\Android Studio\jbr")) {
    $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
}

if (-not $env:ANDROID_HOME -and (Test-Path "$env:LOCALAPPDATA\Android\Sdk")) {
    $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
}

Write-Host "[*] Configuration:" -ForegroundColor DarkGray
Write-Host "    Project Root: $ProjectRoot" -ForegroundColor DarkGray
Write-Host "    JAVA_HOME:    $env:JAVA_HOME" -ForegroundColor DarkGray
Write-Host "    ANDROID_HOME: $env:ANDROID_HOME" -ForegroundColor DarkGray
Write-Host ""

Write-Host "[*] Executing test suite via Gradle Wrapper..." -ForegroundColor Yellow
$gradleArgs = @(
    "testDebugUnitTest",
    "--tests", "com.kliq.app.domain.usecase.CalculateUserDistanceUseCaseTest",
    "--tests", "com.kliq.app.data.util.AntiSpamReviewValidatorTest"
)

if ($VerboseOutput) {
    $gradleArgs += "--info"
}

$gradleCmd = Join-Path $ProjectRoot "gradlew.bat"
$process = Start-Process -FilePath $gradleCmd -ArgumentList $gradleArgs -NoNewWindow -Wait -PassThru

Write-Host ""
Write-Host "===============================================================================" -ForegroundColor Cyan
if ($process.ExitCode -eq 0) {
    Write-Host "[SUCCESS] All Distance Calculation and Geofencing Tests PASSED (0 Failures)" -ForegroundColor Green
} else {
    Write-Host "[FAILURE] Test suite failed with exit code: $($process.ExitCode)" -ForegroundColor Red
}
Write-Host "===============================================================================" -ForegroundColor Cyan

$reportHtml = Join-Path $ProjectRoot "app\build\reports\tests\testDebugUnitTest\index.html"
$resultsXml = Join-Path $ProjectRoot "app\build\test-results\testDebugUnitTest"

Write-Host ""
Write-Host "[*] Test Artifacts:" -ForegroundColor Cyan
Write-Host "    HTML Summary: file:///$($reportHtml -replace '\\', '/')" -ForegroundColor White
Write-Host "    XML Results:  file:///$($resultsXml -replace '\\', '/')" -ForegroundColor White
Write-Host ""

exit $process.ExitCode
