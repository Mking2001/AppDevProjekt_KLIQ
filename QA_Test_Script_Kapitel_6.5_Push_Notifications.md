# QA Test Script: Kapitel 6.5 - Push-Benachrichtigungen & Deep-Linking (3 Ausführungszustände)

Dieses Test-Skript beschreibt die Emulator-Testroutinen und ADB-Shell-Befehle zur vollständigen Überprüfung von Push-Benachrichtigungen in allen drei Ausführungszuständen (Foreground, Background, Killed State).

---

## 1. Testumgebung & Vorbereitung

1. **Android Emulator starten**: Emulator mit API Level 26+ (empfohlen API 33+ / Android 13) ausführen.
2. **App installieren & starten**:
   ```bash
   ./gradlew installDebug
   ```
3. **Benachrichtigungsberechtigung erteilen** (ab Android 13):
   Beim ersten App-Start den Dialog zur Benachrichtigungsberechtigung akzeptieren.

---

## 2. Test-Szenario 1: Foreground State (App aktiv im Vordergrund)

### Ziel
Überprüfung, dass eingehende Nachrichten bei aktiver App flüssig im Chat-State / ViewModel verarbeitet werden und Benachrichtigungen umgehend gerendert werden.

### Test-Schritte
1. App auf dem Emulator öffnen und auf den Home-Screen oder die Chat-Übersicht navigieren.
2. Folgenden ADB-Broadcast ausführen:
   ```bash
   adb shell am broadcast -a com.kliq.app.ACTION_SIMULATE_PUSH \
     --es "chat_id" "chat_usr_123" \
     --es "sender_id" "usr_123" \
     --es "sender_name" "Alice Miller" \
     --es "preview_text" "Hey! Bist du schon im Club?" \
     --es "notification_type" "direct_message"
   ```

### Erwartetes Ergebnis
- Die Benachrichtigung wird umgehend im System gerendert.
- `PushNotificationRepository.incomingPushPayloads` emittiert das Payload an abonnierte ViewModels.
- Keine Ruckler oder UI-Blockaden.

---

## 3. Test-Szenario 2: Background State (App im Hintergrund)

### Ziel
Überprüfung, dass Push-Benachrichtigungen bei minimierter App korrekt in der System-Statusleiste erscheinen und Absender sowie Vorschautext anzeigen.

### Test-Schritte
1. Kliq-App starten.
2. Home-Taste auf dem Emulator drücken (App wechselt in den Hintergrund).
3. Folgenden ADB-Broadcast ausführen:
   ```bash
   adb shell am broadcast -a com.kliq.app.ACTION_SIMULATE_PUSH \
     --es "chat_id" "city_berlin" \
     --es "sender_id" "usr_456" \
     --es "sender_name" "Berlin Party Radar" \
     --es "preview_text" "@everyone Treffpunkt 23:00 Uhr am Watergate!" \
     --es "notification_type" "city_chat_mention"
   ```
4. Statusleiste des Emulators nach unten ziehen.

### Erwartetes Ergebnis
- Eine Push-Benachrichtigung erscheint im Kanal „Kliq City Chats“.
- Das Kliq Marken-Icon (`ic_launcher`) und die Lila-Akzentfarbe (`#9D4EDD`) werden dargestellt.
- Absender („Berlin Party Radar“) und Vorschautext werden vollständig angezeigt.

---

## 4. Test-Szenario 3: Killed State (App vollständig geschlossen)

### Ziel
Überprüfung, dass bei beendeter App ein Klick auf die Benachrichtigung oder das Auslösen eines Deep-Links die Kliq-App startet und direkt in den entsprechenden Chat navigiert.

### Test-Schritte (Teil A: Process Force-Stop & Push Notification Click)
1. App-Prozess im Emulator vollständig beenden:
   ```bash
   adb shell am force-stop com.kliq.app
   ```
2. Push-Notification via ADB-Broadcast auslösen:
   ```bash
   adb shell am broadcast -a com.kliq.app.ACTION_SIMULATE_PUSH \
     --es "chat_id" "chat_usr_789" \
     --es "sender_id" "usr_789" \
     --es "sender_name" "Max Power" \
     --es "preview_text" "Hast du die Tickets reserviert?" \
     --es "notification_type" "direct_message"
   ```
3. Auf die erschienene Benachrichtigung in der Statusleiste tippen.

### Test-Schritte (Teil B: Direkter Deep-Link Intent Test)
Alternativ direktes Auslösen des Deep-Links über den Paketmanager:
```bash
adb shell am start -W -a android.intent.action.VIEW \
  -d "kliq://chat/chat_usr_789?senderId=usr_789&type=direct_message" com.kliq.app
```

### Erwartetes Ergebnis
- Die Kliq-App startet sauber ohne Absturz.
- Das Deep-Link-Routing leitet ohne Zwischenschritte direkt in den 1-zu-1 Chat-Screen von `chat_usr_789`.
- Alle Intent-Parameter (`chatId`, `senderId`, `type`) stehen dem `ChatDetailViewModel` zur Verfügung.

---

## 5. Automatisches Test-Skript (PowerShell & Bash)

### Windows PowerShell (`test_push_notifications.ps1`)
```powershell
Write-Host "=== Kliq Push Notification Test Routine ===" -ForegroundColor Purple

Write-Host "[1/3] Testing Foreground State..." -ForegroundColor Yellow
adb shell am broadcast -a com.kliq.app.ACTION_SIMULATE_PUSH --es "chat_id" "chat_usr_123" --es "sender_id" "usr_123" --es "sender_name" "Alice Miller" --es "preview_text" "Foreground Test Message" --es "notification_type" "direct_message"

Start-Sleep -Seconds 3

Write-Host "[2/3] Minimizing app for Background State Test..." -ForegroundColor Yellow
adb shell input keyevent 3
Start-Sleep -Seconds 1
adb shell am broadcast -a com.kliq.app.ACTION_SIMULATE_PUSH --es "chat_id" "city_berlin" --es "sender_id" "usr_456" --es "sender_name" "Berlin Chat" --es "preview_text" "Background City Mention Test" --es "notification_type" "city_chat_mention"

Start-Sleep -Seconds 3

Write-Host "[3/3] Killing app process for Killed State Deep-Link Test..." -ForegroundColor Yellow
adb shell am force-stop com.kliq.app
Start-Sleep -Seconds 1
adb shell am start -W -a android.intent.action.VIEW -d "kliq://chat/chat_usr_789?senderId=usr_789&type=direct_message" com.kliq.app

Write-Host "=== Push Notification Test Complete ===" -ForegroundColor Green
```
