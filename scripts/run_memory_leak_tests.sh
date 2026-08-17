#!/usr/bin/env bash
# Kliq Mobile App - Test Execution Script: Kapitel 9.3 Speicher-Leck Analyse & Optimierung

set -e

echo "=========================================================================="
echo " Kliq Mobile App - Test-Suite 9.3: Speicher-Leck Analyse & Optimierung   "
echo "=========================================================================="

GRADLE_CMD="./gradlew"
if [ ! -f "$GRADLE_CMD" ]; then
    GRADLE_CMD="gradle"
fi

echo -e "\nRunning Memory Leak Prevention Unit Tests..."
$GRADLE_CMD testDebugUnitTest --tests "*MemoryLeakUnitTest*"

echo "=========================================================================="
echo " KAPITEL 9.3 MEMORY LEAK OPTIMIZATION TESTS PASSED SUCCESSFULLY!          "
echo "=========================================================================="
