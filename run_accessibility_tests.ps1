# Powershell runner for Accessibility Feature unit tests
Write-Host "Running Accessibility Unit Tests..." -ForegroundColor Cyan

$env:JAVA_HOME = $env:JAVA_HOME

./gradlew testDebugUnitTest --tests "com.kliq.app.util.AccessibilityUtilsTest" --tests "com.kliq.app.data.repository.AccessibilityRepositoryTest" --tests "com.kliq.app.viewmodel.AccessibilityViewModelTest"

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nAll Accessibility Unit Tests Passed Successfully!" -ForegroundColor Green
} else {
    Write-Host "`nAccessibility Unit Tests Failed!" -ForegroundColor Red
    exit 1
}
