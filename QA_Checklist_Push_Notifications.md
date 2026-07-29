# QA Checklist: Push-Benachrichtigungen für neue Chat-Nachrichten (Kapitel 6.5)

## 1. Notification Channels
- [x] Kanal "Kliq Direct Messages" ist unter Android System-Einstellungen registriert.
- [x] Kanal "Kliq City Chats" ist unter Android System-Einstellungen registriert.
- [x] Richtige Wichtigkeitsstufen (Importance High / Default) und Sounds/Vibrationen zugewiesen.

## 2. FCM Push-Payload Handling
- [x] Direct Message Push-Payloads werden korrekt geparst.
- [x] City Chat Mention Push-Payloads werden korrekt geparst.
- [x] Fallback-Werte greifen bei fehlenden optionalen Payload-Schlüsseln.

## 3. Deep-Linking & Intent Routing
- [x] Klick auf Push-Benachrichtigung öffnet `MainActivity` via Deep-Link (`kliq://chat/{chatId}`).
- [x] App navigiert direkt zum entsprechenden 1-zu-1 oder Stadt-Chat Screen.
- [x] Parameter (`chatId`, `senderId`, `type`) werden korrekt an das ViewModel übermittelt.

## 4. UI & Visual Branding
- [x] Kliq Marken-Icon (`ic_launcher`) als Small Icon in System-Benachrichtigung angezeigt.
- [x] Farbschema entspricht Kliq High-Contrast Dark/Purple (#9D4EDD).
- [x] Vorschautext und Absendername werden im Benachrichtigungs-Banner angezeigt.

## 5. Berechtigungen & Edge Cases
- [x] Laufzeit-Berechtigungsabfrage (`POST_NOTIFICATIONS`) unter Android 13+ (API 33+).
- [x] Deaktivierte Channels in den App-Einstellungen unterdrücken Push-Anzeigen ordnungsgemäß.
