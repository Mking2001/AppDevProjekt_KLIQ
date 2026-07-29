# Pull Request: Kapitel 6.1 - Chat-Listen-Übersicht (Öffentlich/Privat)

## Zusammenfassung
Dieser Pull Request implementiert Kapitel 6.1 („Chat-Listen-Übersicht (Öffentlich/Privat)“) für die native Mobile-App Kliq unter strikter Einhaltung der MVVM-Architektur, Jetpack Compose und dem High-Contrast Purple/Dark-Designsystem. Die Implementierung trennt übersichtlich zwischen öffentlichen Stadt-Chats (z. B. „Berlin - Tonight“) und privaten 1-zu-1-Nachrichten, bietet reaktives Ausfiltern blockierter Nutzer, eine integrierte Suchfunktion, Wisch-Aktionen (Löschen & Archivieren) mit Rückgängig-Snackbar sowie umfassende Unit-Tests.

## Wichtigste Änderungen

### 1. Daten-Modellierung (`ChatModels.kt`)
- **Neue Datenmodelle**:
  - `ChatListItem`: Kern-Datenstruktur mit `id`, `title`, `cityRegion`, `lastMessage`, `avatarInitial`, `avatarUrl`, `unreadCount`, `chatType` und `userStatus`.
  - `ChatType`: Enum mit `PUBLIC_CITY` und `PRIVATE`.
  - `LastMessage`: Datenklasse mit `text`, `timestampMs`, `timestampIso`, `senderName` und `isRead`.
  - `UserStatus`: Enum mit `ONLINE`, `OFFLINE` und `AWAY`.
- **Rückwärtskompatibilität**: Zuordnungs- und Erweiterungsfunktionen (`toChatListItem()` & `toChatConversation()`) garantieren nahtloses Zusammenspiel mit bestehenden Repositories und Unter-Bildschirmen.

### 2. ViewModel & Business Logic (`ChatListViewModel.kt`)
- **State Management**: `ChatListUiState` verwaltet `publicChats`, `privateChats`, `selectedTab`, `searchQuery`, `isSearchActive` und `isLoading`.
- **Reaktives Ausfiltern blockierter Nutzer**: Dynamische Filterung der 1-zu-1-Privatnachrichten über den `Flow<List<String>>` von `UserRepository.getBlockedUserIds("current_user")`.
- **Interaktive Suche**: Echzeit-Filterung von Stadt- und Privat-Chats nach Titel oder Nachrichtentext.
- **Wisch- & Wiederherstellungsaktionen**: `onChatDeleted`, `onChatArchived` und `onUndoDelete`.

### 3. UI & Design System (`ChatComponents.kt` & `ChatListScreen.kt`)
- **Visual Excellence & High-Contrast Style**:
  - Avatar-Platzhalter mit Initialen.
  - Farbverlauf-Rahmen (`Brush.linearGradient`) für öffentliche Stadt-Chats.
  - Grüner Präsenz-Indikator (`UserStatus.ONLINE`) für 1-zu-1-Chats.
  - Ungelesen-Badge (`unreadCount`) mit `99+` Überlaufbehandlung.
- **Kategorie-TabNavigation**: TabRow mit animierter Farbdifferenzierung zwischen „Öffentliche Stadt-Chats“ und „Private Nachrichten“.
- **TopAppBar & Suche**: Dynamisch umschaltbare Suchleiste mit Clear- und Back-Aktionen.
- **Empty States**: Aussagekräftiges Platzhalter-Layout mit Icon und individuellem Hinweistext bei leeren Such- oder Kategorielisten.

### 4. Unit Tests (`ChatListViewModelTest.kt`)
- **Abdeckung**:
  - Initialer State und Laden von Kategorien.
  - Tab-Umschaltung.
  - Suchfilter-Logik und Such-Toggle.
  - Reaktives Ausfiltern blockierter Nutzer.
  - Wisch-Löschen und Undo-Aktionen.

---

## Git-Strategie & Commit-Historie
Strikter Feature-Branch Workflow (`feature/chat-list-overview`) mit atomaren Commits:
1. `feat(data): add ChatListItem, LastMessage, and UserStatus data models`
2. `feat(viewmodel): update ChatListViewModel with reactive repository streaming and blocked user filtering`
3. `feat(ui): refine ChatListItem and ChatListScreen with search, category tabs, and high-contrast design`
4. `test(chat): add comprehensive unit tests for ChatListViewModel`
5. `docs: add PR, Code Review, QA Test Plan, and QA Checklist for chapter 6.1`
