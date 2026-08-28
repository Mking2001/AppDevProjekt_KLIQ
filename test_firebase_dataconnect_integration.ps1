# Set JAVA_HOME if not already present
if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
}
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host " Running Firebase Data Connect Integration Verification " -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

# Step 1: Run Gradle compileDebugSources
Write-Host "[1/2] Compiling Kotlin sources (compileDebugSources)..." -ForegroundColor Yellow
$compileResult = cmd /c "gradlew.bat compileDebugSources --console=plain"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed:" -ForegroundColor Red
    $compileResult
    exit 1
} else {
    Write-Host "Compilation Successful!" -ForegroundColor Green
}

# Step 2: Run Unit Tests
Write-Host "[2/2] Running Unit Tests (testDebugUnitTest)..." -ForegroundColor Yellow
$testResult = cmd /c "gradlew.bat testDebugUnitTest --console=plain"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Unit tests failed:" -ForegroundColor Red
    $testResult
    exit 1
} else {
    Write-Host "Unit Tests Passed!" -ForegroundColor Green
}

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host " All Verification Checks PASSED Successfully! " -ForegroundColor Green
Write-Host "====================================================" -ForegroundColor Cyan
