@echo off
setlocal enabledelayedexpansion

echo ===============================================================================
echo                KLIQ MOBILE APP - DISTANCE ^& GEOFENCE TEST RUNNER
echo ===============================================================================
echo.

rem Navigate to repository root
cd /d "%~dp0\.."

rem Auto-detect Java environment if JAVA_HOME is not set
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Android\Android Studio\jbr" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
        set "PATH=C:\Program Files\Android\Android Studio\jbr\bin;!PATH!"
    )
)

rem Auto-detect Android SDK if ANDROID_HOME is not set
if "%ANDROID_HOME%"=="" (
    if exist "%LOCALAPPDATA%\Android\Sdk" (
        set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
    )
)

echo [*] Target Environment:
echo     JAVA_HOME:    %JAVA_HOME%
echo     ANDROID_HOME: %ANDROID_HOME%
echo.

echo [*] Executing isolated distance and geofencing unit tests...
echo.

call gradlew.bat testDebugUnitTest --tests "com.kliq.app.domain.usecase.CalculateUserDistanceUseCaseTest" --tests "com.kliq.app.data.util.AntiSpamReviewValidatorTest" --info

set "TEST_EXIT_CODE=%ERRORLEVEL%"

echo.
echo ===============================================================================
if %TEST_EXIT_CODE% equ 0 (
    echo [SUCCESS] All Distance Calculation and Geofencing Unit Tests PASSED!
) else (
    echo [FAILURE] Test suite encountered errors. Return code: %TEST_EXIT_CODE%
)
echo ===============================================================================
echo.
echo [*] Generated Test Reports:
echo     HTML Report: %CD%\app\build\reports\tests\testDebugUnitTest\index.html
echo     XML Results: %CD%\app\build\test-results\testDebugUnitTest\
echo.

exit /b %TEST_EXIT_CODE%
