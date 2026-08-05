# Pull Request: Kapitel 8.2 - Swipe-to-Action in Chat-Listen (Löschen/Archivieren)

## 📌 Feature-Beschreibung & Zielsetzung

Dieser Pull Request implementiert die **„Swipe-to-Action in Chat-Listen (Löschen/Archivieren)“** gemäß Kapitel 8.2 des Kliq-Entwicklungsplans. Chat-Einträge in den Listen (Öffentliche Stadt-Chats und Private Nachrichten) bieten nun interaktive Swipe-Gesten für schnelles Archivieren und sicheres Löschen mit Bestätigungsdialog.

---

## 🛠️ Implementierte Änderungen & Architektur

### 1. Daten- & Persistence-Schicht (Room / Repository)
- **`ChatEntity.kt`**: Feld `isArchived: Boolean = false` hinzugefügt zur lokalen Persistierung des Archiv-Status.
- **`ChatDao.kt`**: Methode `updateArchiveStatus(chatId, isArchived)` und `deleteChatById(chatId)` integriert.
- **`ChatRepository.kt` & `ChatRepositoryImpl.kt`**: Repository-Methoden `archiveChat(chatId, isArchived)` und `deleteChat(chatId)` zur asynchronen Datenbank-Synchronisierung bereitgestellt.

### 2. ViewModel & State-Management (MVVM)
- **`ChatListViewModel.kt` & `ChatListUiState`**:
  - Hinzugefügt: `archivedChats`, `pendingDeleteChat` und `showArchivedSection`.
  - `onRequestDeleteChat(chat)`: Setzt den Status für den Sicherheitsdialog.
  - `onConfirmDeleteChat()`: Führt die endgültige Löschung in DB & UI durch.
  - `onArchiveChat(chat)`: Verschiebt den Chat in den Archiv-Bereich und synchronisiert DB.
  - `onUnarchiveChat(chat)`: Wiederherstellen archivierter Chats.

### 3. UI & Design System (Kliq Dark Mode)
- **`SwipeableActionRow.kt`**:
  - **Swipe Links (End-to-Start)**: Auslösen der Aktion **Archivieren** mit Kliq Lila Akzentfarbe (`#8A2BE2` / `PurplePrimary`) und Archiv-Icon.
  - **Swipe Rechts (Start-to-End)**: Auslösen der Aktion **Löschen** mit Fehler-Rot (`#EF4444` / `ErrorRed`) und Mülleimer-Icon.
  - Flüssige Compose-Animationen (`animateColorAsState`, `animateFloatAsState`) und haptisches Feedback.
- **`DeleteChatConfirmationDialog.kt`**:
  - High-Contrast Sicherheitsdialog zur Vermeidung von versehentlichem Datenverlust ("Chat wirklich löschen?").

---

## 🧪 Test-Abdeckung & Verifikation

- **Unit-Tests**:
  - [`ChatSwipeActionsUnitTest.kt`](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/app/src/test/java/com/kliq/app/viewmodel/ChatSwipeActionsUnitTest.kt) validiert 100% der ViewModel-Gestenlogik, Dialog-Zustände, Archivierung und Löschung.
  - Ausführung: `.\gradlew.bat testDebugUnitTest --tests "com.kliq.app.viewmodel.ChatSwipeActionsUnitTest"` -> **BUILD SUCCESSFUL**.

---

## 📁 Betroffene Dateien

- `app/src/main/java/com/kliq/app/data/local/entities/ChatEntity.kt`
- `app/src/main/java/com/kliq/app/data/local/dao/ChatDao.kt`
- `app/src/main/java/com/kliq/app/data/repository/ChatRepository.kt`
- `app/src/main/java/com/kliq/app/data/repository/ChatRepositoryImpl.kt`
- `app/src/main/java/com/kliq/app/ui/screens/chat/ChatListViewModel.kt`
- `app/src/main/java/com/kliq/app/ui/components/DeleteChatConfirmationDialog.kt` [NEU]
- `app/src/main/java/com/kliq/app/ui/components/SwipeableActionRow.kt`
- `app/src/main/java/com/kliq/app/ui/screens/chat/ChatListScreen.kt`
- `app/src/test/java/com/kliq/app/viewmodel/ChatSwipeActionsUnitTest.kt` [NEU]
