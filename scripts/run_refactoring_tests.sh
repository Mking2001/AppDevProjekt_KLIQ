#!/usr/bin/env bash
# Kliq Mobile App - Test Execution Script: Kapitel 9.5 Code-Refactoring & Architecture

set -e

echo "=========================================================================="
echo " Kliq Mobile App - Test-Suite 9.5: Code-Refactoring & Architecture       "
echo "=========================================================================="

GRADLE_CMD="./gradlew"
if [ ! -f "$GRADLE_CMD" ]; then
    GRADLE_CMD="gradle"
fi

echo -e "\nRunning Architecture Refactoring Unit Tests..."
$GRADLE_CMD testDebugUnitTest --tests "*ArchitectureRefactoringUnitTest*"

echo "=========================================================================="
echo " KAPITEL 9.5 ARCHITECTURE REFACTORING TESTS PASSED SUCCESSFULLY!         "
echo "=========================================================================="
