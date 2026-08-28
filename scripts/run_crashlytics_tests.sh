#!/usr/bin/env bash
# Kliq Mobile App - Test Execution Script: Kapitel 9.4 Crashlytics Integration

set -e

echo "=========================================================================="
echo " Kliq Mobile App - Test-Suite 9.4: Crashlytics Integration & PII Safety   "
echo "=========================================================================="

GRADLE_CMD="./gradlew"
if [ ! -f "$GRADLE_CMD" ]; then
    GRADLE_CMD="gradle"
fi

echo -e "\nRunning Crashlytics & PII Sanitization Unit Tests..."
$GRADLE_CMD testDebugUnitTest --tests "*PiiSanitizerTest*" --tests "*CrashlyticsTreeTest*"

echo "=========================================================================="
echo " KAPITEL 9.4 CRASHLYTICS INTEGRATION TESTS PASSED SUCCESSFULLY!          "
echo "=========================================================================="
