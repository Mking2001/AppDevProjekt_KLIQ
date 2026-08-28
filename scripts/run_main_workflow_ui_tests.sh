#!/usr/bin/env bash
# Kliq Mobile App - Test Execution Script: Kapitel 9.2 Haupt-Workflow UI-Tests

set -e

echo "=========================================================================="
echo " Kliq Mobile App - Test-Suite 9.2: Haupt-Workflow Emulator UI-Tests       "
echo "=========================================================================="

# Gradle Wrapper Check
GRADLE_CMD="./gradlew"
if [ ! -f "$GRADLE_CMD" ]; then
    GRADLE_CMD="gradle"
fi

echo -e "\n[1/3] Running Onboarding & Login Flow UI Tests..."
$GRADLE_CMD connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowOnboardingUITest

echo -e "\n[2/3] Running Bottom Navigation Host UI Tests..."
$GRADLE_CMD connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowNavigationUITest

echo -e "\n[3/3] Running Core Components & Map Overlay UI Tests..."
$GRADLE_CMD connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowCoreComponentsUITest

echo "=========================================================================="
echo " ALL KAPITEL 9.2 MAIN WORKFLOW UI TESTS PASSED SUCCESSFULLY!             "
echo "=========================================================================="
