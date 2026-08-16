#!/usr/bin/env bash
# ===============================================================================
#                KLIQ MOBILE APP - DISTANCE & GEOFENCE TEST RUNNER
# ===============================================================================
set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

echo "==============================================================================="
echo "               KLIQ MOBILE APP - DISTANCE & GEOFENCE TEST RUNNER"
echo "==============================================================================="
echo ""
echo "[*] Working directory: ${PROJECT_ROOT}"
echo "[*] Executing isolated distance and geofence unit tests..."
echo ""

./gradlew testDebugUnitTest \
    --tests "com.kliq.app.domain.usecase.CalculateUserDistanceUseCaseTest" \
    --tests "com.kliq.app.data.util.AntiSpamReviewValidatorTest"

TEST_EXIT_CODE=$?

echo ""
echo "==============================================================================="
if [ ${TEST_EXIT_CODE} -eq 0 ]; then
    echo "[SUCCESS] All Distance Calculation and Geofencing Unit Tests PASSED!"
else
    echo "[FAILURE] Test suite encountered errors. Return code: ${TEST_EXIT_CODE}"
fi
echo "==============================================================================="
echo ""
echo "[*] Generated Test Reports:"
echo "    HTML Report: ${PROJECT_ROOT}/app/build/reports/tests/testDebugUnitTest/index.html"
echo "    XML Results: ${PROJECT_ROOT}/app/build/test-results/testDebugUnitTest/"
echo ""

exit ${TEST_EXIT_CODE}
