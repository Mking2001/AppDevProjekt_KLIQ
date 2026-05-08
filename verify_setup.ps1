# Kliq Setup Verification Script
# This script compiles the project and runs the unit tests.

Write-Host "--- Starting Kliq Setup Verification ---" -ForegroundColor Cyan

# 1. Check if Gradle is available (or try to use gradlew)
if (Test-Path "./gradlew.bat") {
    $gradle = "./gradlew.bat"
} else {
    Write-Host "Warning: gradlew.bat not found. Trying global 'gradle'..." -ForegroundColor Yellow
    $gradle = "gradle"
}

# 2. Clean and Assemble
Write-Host "Step 1: Compiling project..." -ForegroundColor Yellow
& $gradle clean assembleDebug

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

# 3. Run Unit Tests
Write-Host "Step 2: Running Unit Tests..." -ForegroundColor Yellow
& $gradle testDebugUnitTest

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Unit tests failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

# 4. Instrumented Tests Note
Write-Host "--- Success! ---" -ForegroundColor Green
Write-Host "The project compiles and unit tests pass."
Write-Host "To run UI tests on an emulator, use: ./gradlew connectedAndroidTest" -ForegroundColor Cyan
