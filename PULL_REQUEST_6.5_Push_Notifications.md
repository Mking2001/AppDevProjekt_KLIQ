# Pull Request: Kapitel 6.5 - Push-Benachrichtigungen für neue Chat-Nachrichten

## Zusammenfassung
Dieser Pull Request implementiert Kapitel 6.5 („Push-Benachrichtigungen für neue Chat-Nachrichten“) für die native Mobile-App Kliq unter strikter Einhaltung des MVVM-Architekturmusters, des Repository-Patterns und des Kliq High-Contrast Purple/Dark-Designsystems. 

Die Funktion erweitert Kliq um ein performantes Firebase Cloud Messaging (FCM)-Push-Benachrichtigungssystem für 1-zu-1 Direktnachrichten sowie Stadt-Chat-Erwähnungen. Das Modul umfasst strukturierte Android-Benachrichtigungskanäle („Kliq Direct Messages“, „Kliq City Chats“), Deep-Linking-Routing direkt zum Ziel-Chat-Screen sowie reaktive Viewmodel-Integration.

## Wichtigste Änderungen

### 1. Benachrichtigungskanäle & Datenmodell (`PushNotificationModels.kt` & `NotificationChannelManager.kt`)
- **`PushNotificationModels`**:
  - `PushNotificationType`: Unterteilung in `DIRECT_MESSAGE` (1-zu-1) und `CITY_CHAT_MENTION` (Stadt-Chat-Erwähnungen).
  - `ChatPushPayload`: Robustes Parsing von FCM Data-Payloads (`chat_id`, `sender_id`, `sender_name`, `preview_text`, `notification_type`).
- **`NotificationChannelManager`**:
  - Registrierung von strukturieren Kanälen ab Android O (API 26+).
  - High-Contrast Purple Akzentfarben (`#9D4EDD`, `#7B2CBF`), Vibrations- und LED-Muster für wichtige Nachrichten.

### 2. FCM Notification Service & Helper (`KliqFirebaseMessagingService.kt` & `NotificationHelper.kt`)
- **`KliqFirebaseMessagingService`**:
  - Injektion von `NotificationHelper` und `PushNotificationRepository` via Hilt.
  - Automatische FCM-Token-Erfassung (`onNewToken`) und Nachrichten-Verarbeitung (`onMessageReceived`).
- **`NotificationHelper`**:
  - Systembenachrichtigungen mit `NotificationCompat.Builder`, Marken-Icon (`ic_launcher`), Lila-Farbschema und `BigTextStyle`.
  - Aufbau von `PendingIntent` mit Deep-Link URIs (`kliq://chat/{chatId}`).

### 3. Deep-Linking & Navigation Integration (`NavigationRoute.kt`, `MainActivity.kt` & `KliqMainScaffold.kt`)
- **`ChatRoutes`**:
  - Erweitert um Deep-Link URI-Muster (`kliq://chat/{chatId}?senderId={senderId}&type={type}`).
- **`MainActivity`**:
  - Verarbeitet Intent-Datensätze aus `onCreate` und `onNewIntent`.
  - Prüft und fordert Benachrichtigungsberechtigungen (`POST_NOTIFICATIONS`) unter Android 13+ (API 33+) an.
- **`KliqMainScaffold`**:
  - Einbindung von `navDeepLink` im `NavHost` für nahtloses Routing zum Ziel-Chat beim Klick auf Push-Benachrichtigungen.

### 4. Repository & Dependency Injection (`PushNotificationRepositoryImpl.kt` & `RepositoryModule.kt`)
- **`PushNotificationRepositoryImpl`**:
  - Sichere Speicherung des FCM-Tokens via `EncryptedSharedPreferences`.
  - Reaktives Streaming eingehender Push-Nachrichten via `Flow<ChatPushPayload>`.

---

## Git-Strategie & Commit-Historie
Strikter Feature-Branch Workflow (`feature/push-notifications`) mit atomaren Commits:
1. `feat(notification): add notification channels and payload data models`
2. `feat(notification): implement FCM NotificationService, NotificationHelper, and repository module`
3. `feat(navigation): integrate deep-linking routing and intent handling for push notifications`
4. `test(notification): add unit and integration tests for FCM push notification system`
5. `docs: add PR, Code Review, QA Test Plan, QA Checklist, and Emulator Test Script for chapter 6.5`
