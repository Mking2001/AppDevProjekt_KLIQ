# QA Test Script: Kapitel 6.5 - Push-Benachrichtigungen & Deep-Linking

## Simulator & ADB Command Test Script

### 1. ADB Command: Push Notification Deep-Link Intent Simulation
Zum Testen des Deep-Link-Routings via ADB Shell im Android Emulator:

```bash
# Simuliert das Antippen einer Push-Benachrichtigung für einen 1-zu-1 Chat
adb shell am start -W -a android.intent.action.VIEW -d "kliq://chat/chat_usr_123?senderId=usr_123&type=direct_message" com.kliq.app

# Simuliert das Antippen einer Push-Benachrichtigung für einen Stadt-Chat
adb shell am start -W -a android.intent.action.VIEW -d "kliq://chat/city_berlin?senderId=usr_sys&type=city_chat_mention" com.kliq.app
```

### 2. ADB Command: Broadcast Extra Intent Data Test
```bash
adb shell am start -n com.kliq.app/.MainActivity \
  --es "chat_id" "chat_usr_123" \
  --es "sender_id" "usr_123" \
  --es "sender_name" "Max Mustermann" \
  --es "preview_text" "Hey, treffen wir uns um 22 Uhr?" \
  --es "notification_type" "direct_message"
```

### 3. Verifikations-Kriterien
- Der Chat-Screen öffnet sich direkt und lädt die Konversation.
- Es treten keine Navigation-Exceptions oder Crashes auf.
