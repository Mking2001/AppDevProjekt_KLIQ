#!/bin/bash
# ==============================================================================
# Kliq Mobile App - Kapitel 6.7 Emulator Test-Skript
# Validierung von Nachrichten-Status (Gesendet -> Empfangen -> Gelesen)
# ==============================================================================

echo "======================================================================"
echo "KLIQ PUSH & MESSAGE STATUS EMULATOR TEST SUITE (KAPITEL 6.7)"
echo "======================================================================"

PACKAGE_NAME="com.kliq.app"
MAIN_ACTIVITY="com.kliq.app.MainActivity"

# 1. Prüfen, ob Emulator via ADB erreichbar ist
echo "[1/4] Prüfe ADB-Verbindung zum Android Emulator..."
ADB_DEVICE=$(adb devices | grep "emulator" | head -n 1 | cut -f1)

if [ -z "$ADB_DEVICE" ]; then
    echo "[!] WARNUNG: Kein laufender Emulator gefunden. Starte stattdessen Robolectric Scenario Tests..."
    ./gradlew testDebugUnitTest --tests "com.kliq.app.data.MessageStatusValidationScenarioTest"
    exit $?
fi

echo "[+] Emulator gefunden: $ADB_DEVICE"

# 2. App im Emulator starten
echo "[2/4] Starte Kliq App auf Emulator..."
adb shell am start -n "$PACKAGE_NAME/$MAIN_ACTIVITY"
sleep 2

# 3. Simuliere eingehendes Status-Update Event via ADB Broadcast
echo "[3/4] Simuliere Status-Updates über ADB Broadcast Events..."

# Broadcast 1: Message Delivered
echo "--> Sende Broadcast: MESSAGE_DELIVERED"
adb shell am broadcast -a com.kliq.app.MESSAGE_STATUS_UPDATE \
    --es message_id "msg_test_001" \
    --es status "DELIVERED" \
    --el timestamp $(date +%s%3N)

sleep 1.5

# Broadcast 2: Message Read
echo "--> Sende Broadcast: MESSAGE_READ"
adb shell am broadcast -a com.kliq.app.MESSAGE_STATUS_UPDATE \
    --es message_id "msg_test_001" \
    --es status "READ" \
    --el timestamp $(date +%s%3N)

# 4. Führe automatisierte UI- & Integrationstests aus
echo "[4/4] Führe Integrationstests aus..."
./gradlew testDebugUnitTest --tests "com.kliq.app.data.MessageStatusValidationScenarioTest"

echo "======================================================================"
echo "TESTPROTOKOLL KAPITEL 6.7 ABGESCHLOSSEN"
echo "======================================================================"
