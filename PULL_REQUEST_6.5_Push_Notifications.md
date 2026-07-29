# Pull Request: Kapitel 6.5 - Push-Benachrichtigungen für neue Chat-Nachrichten

## Zusammenfassung
Dieser Pull Request implementiert Kapitel 6.5 („Push-Benachrichtigungen für neue Chat-Nachrichten“) für die native Mobile-App Kliq unter strikter Einhaltung des MVVM-Architekturmusters, des Repository-Patterns und des Kliq High-Contrast Purple/Dark-Designsystems.

Die Funktion erweitert Kliq um ein performantes Firebase Cloud Messaging (FCM)-Push-Benachrichtigungssystem für 1-zu-1 Direktnachrichten sowie Stadt-Chat-Erwähnungen. Das Modul umfasst strukturierte Android-Benachrichtigungskanäle („Kliq Direct Messages“, „Kliq City Chats“), Deep-Linking-Routing direkt zum Ziel-Chat-Screen sowie reaktive ViewModel-Integration.

---

## Technical Audit & Qualitätssicherungs-Review

### 1. Architektur & MVVM
- **Entkopplung**: Notification-Processing ist strikt vom UI-Layer getrennt. `KliqFirebaseMessagingService` verarbeitet FCM Data Payloads im Background und übergibt Daten an das `PushNotificationRepositoryImpl`.
- **Reaktives Streaming**: `PushNotificationRepository` stellt Push-Payloads über eine thread-sichere `Flow<ChatPushPayload>`-Pipeline bereit.
- **Dependency Injection**: Sämtliche Klassen sind über Dagger/Hilt (`RepositoryModule`, `@Singleton`, `@EntryPoint` mit `EntryPointAccessors`) angebunden.

### 2. Deep-Linking & Navigation (Backstack-Stabilität)
- **Deep-Link Schema**: `kliq://chat/{chatId}?senderId={senderId}&type={type}` unterstützt direktes Intent-Routing.
- **Intent Flags**: `FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP` verhindern Instanz-Duplizierung der `MainActivity` und garantieren einen stabilen Navigation-Backstack.
- **Compose NavGraph**: Nahtlose Einbindung via `navDeepLink` in `KliqMainScaffold` mit flüssigen Folien-Animationen.

### 3. Ressourcen & Batterienutzung
- **Batterieschonung**: Vollständig ereignisbasierte FCM-Push-Architektur (kein Polling, keine dauerhaften Dienst-Locks).
- **Hauptthread-Sicherheit**: Alle I/O- und Verschlüsselungsoperationen laufen auf Coroutine `Dispatchers.IO` mit `SupervisorJob()`.

---

## PR-Checkliste & Qualitätssicherung

### Getestete Android API Level
- [x] **Android 8.0 (API 26 - Oreo)**: Mindestvoraussetzung für Android `NotificationChannel`.
- [x] **Android 12 (API 31 - S)**: Verifizierung von `PendingIntent.FLAG_IMMUTABLE`.
- [x] **Android 13 (API 33 - Tiramisu)**: Abfrage und Validierung der `POST_NOTIFICATIONS`-Laufzeitberechtigung.
- [x] **Android 14 (API 34 - UpsideDownCake)**: Target SDK Kompatibilität & Compose Performance.

### Verifizierungsschritte
- [x] **Foreground State**: Nachricht wird bei aktiver App verarbeitet; `Flow<ChatPushPayload>` aktualisiert State.
- [x] **Background State**: Push-Notification erscheint mit Absender, Vorschautext und Kliq Marken-Icon (`#9D4EDD`) in der Statusleiste.
- [x] **Killed State**: Klick auf Notification / Deep-Link startet die beendete App und navigiert direkt zum Ziel-Chat.
- [x] **ADB Simulation**: Aufruf von `test_push_notifications.ps1` verifiziert alle 3 Zustände fehlerfrei.

---

## Git-Strategie & Commit-Historie
Strikter Feature-Branch Workflow (`feature/push-notifications`) mit atomaren Commits:
1. `feat(notification): add notification channels and payload data models`
2. `feat(notification): implement FCM NotificationService, NotificationHelper, and repository module`
3. `feat(navigation): integrate deep-linking routing and intent handling for push notifications`
4. `test(notification): add unit and integration tests for FCM push notification system`
5. `docs: add PR, Code Review, QA Test Plan, QA Checklist, and Emulator Test Script for chapter 6.5`
6. `test(notification): add emulator test receiver, PowerShell script, and test documentation for 3 application execution states`
7. `fix(notification): use EntryPointAccessors in PushTestReceiver to resolve Hilt ASM bytecode transform issue`
