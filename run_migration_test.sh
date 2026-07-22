#!/usr/bin/env bash
# Kliq Database Migration Test Automation Script (Bash)

set -e

echo "=========================================================="
echo "  KLIQ DATABASE MIGRATION TEST RUNNER (V1 -> V7)"
echo "=========================================================="

echo -e "\n[STEP 1] Running Local JVM Migration Unit Tests..."
./gradlew testDebugUnitTest --tests "com.kliq.app.data.local.DatabaseMigrationTest"
echo "[SUCCESS] Local JVM Migration Tests Passed!"

echo -e "\n[STEP 2] Checking for Connected Emulator / Android Device..."
if adb devices | grep -q -w "device"; then
    echo "[INFO] Active emulator/device detected."
    echo -e "\n[STEP 3] Running Instrumented Migration Test on Emulator..."
    ./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.data.local.KliqDatabaseMigrationAndroidTest
    echo "[SUCCESS] Emulator Database Migration Test Passed Completely!"
else
    echo "[NOTICE] No active emulator/device connected. Skipping instrumented device test."
    echo "[TIP] Start an Android Emulator in Android Studio and re-run this script to test on device."
fi

echo "=========================================================="
echo "  ALL MIGRATION TEST VERIFICATIONS COMPLETED SUCCESSFULLY"
echo "=========================================================="
