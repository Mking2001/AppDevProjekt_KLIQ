# Kliq Database Migration Test Automation Script
# Executes local JVM migration tests and instrumented emulator tests.

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  KLIQ DATABASE MIGRATION TEST RUNNER (V1 -> V7)" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# Set JDK 17+ environment for Android Gradle Plugin compatibility
$jdkPath = "C:\Program Files\Android\Android Studio\jbr"
if (Test-Path "$jdkPath\bin\java.exe") {
    $env:JAVA_HOME = $jdkPath
    Write-Host "[INFO] Set JAVA_HOME to Android Studio JDK: $env:JAVA_HOME" -ForegroundColor Green
} else {
    Write-Host "[WARN] Android Studio JDK not found at default location. Using default JAVA_HOME." -ForegroundColor Yellow
}

Write-Host "`n[STEP 1] Running Local JVM Migration Unit Tests..." -ForegroundColor Yellow
$unitTestCmd = "./gradlew testDebugUnitTest --tests `"com.kliq.app.data.local.DatabaseMigrationTest`""
Invoke-Expression $unitTestCmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[SUCCESS] Local JVM Migration Tests Passed!" -ForegroundColor Green
} else {
    Write-Host "`n[ERROR] Local JVM Migration Tests Failed. Check logs above." -ForegroundColor Red
    exit 1
}

# Ensure ADB path is resolved
$adbPath = "adb"
$sdkAdb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (Test-Path $sdkAdb) {
    $env:PATH += ";$env:LOCALAPPDATA\Android\Sdk\platform-tools"
    $adbPath = $sdkAdb
}

Write-Host "`n[STEP 2] Checking for Connected Emulator / Android Device..." -ForegroundColor Yellow
$adbDevices = & $adbPath devices 2>$null | Select-String -Pattern "\sdevice$"

if ($adbDevices) {
    Write-Host "[INFO] Active emulator/device detected: $adbDevices" -ForegroundColor Green
    Write-Host "`n[STEP 3] Running Instrumented Migration Test on Emulator..." -ForegroundColor Yellow
    
    $instrumentedCmd = "./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.data.local.KliqDatabaseMigrationAndroidTest"
    Invoke-Expression $instrumentedCmd

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n[SUCCESS] Emulator Database Migration Test Passed Completely!" -ForegroundColor Green
    } else {
        Write-Host "`n[ERROR] Emulator Migration Test Failed. Check logcat / console log above." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "[NOTICE] No active emulator/device connected. Skipping instrumented device test." -ForegroundColor Yellow
    Write-Host "[TIP] Start an Android Emulator in Android Studio and re-run this script to test on device." -ForegroundColor Yellow
}

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host "  ALL MIGRATION TEST VERIFICATIONS COMPLETED SUCCESSFULLY" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
