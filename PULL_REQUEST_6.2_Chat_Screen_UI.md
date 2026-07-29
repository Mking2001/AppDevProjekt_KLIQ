# Pull Request: Kapitel 6.2 - UI für den Chat-Screen (Sprechblasen, Lila Design)

## Zusammenfassung
Dieser Pull Request implementiert Kapitel 6.2 („UI für den Chat-Screen (Sprechblasen, Lila Design)“) für die native Mobile-App Kliq unter strikter Einhaltung der MVVM-Architektur, Jetpack Compose und dem High-Contrast Purple/Dark-Designsystem. Die Implementierung bietet visuell getrennte Sprechblasen (rechtsbündiges Lila-Highlight für eigene Nachrichten, linksbündiger Dunkelkontrast mit Sender-Namen für fremde Nachrichten), zeitliche Datums-Trennlinien (`ChatDateDivider`), Zeitstempel, Nachrichtenstatus-Indikatoren (`SENT`, `DELIVERED`, `READ`), eine Input-Eingabeleiste mit Tastatur-Animation (`imePadding()`) sowie automatisches Autoscrolling bei neuen Nachrichten.

## Wichtigste Änderungen

### 1. Visual Sprechblasen-Design & Chat Components (`ChatComponents.kt`)
- **`ChatBubble`**:
  - **Ausgehende Nachrichten (`isMine = true`)**: Rechtsbündig ausgerichtet (`Alignment.End`), Kliq Lila-Akzent-Hintergrund (`PurplePrimary`), asymmetrisch abgerundete Ecken (`topStart=16.dp`, `topEnd=16.dp`, `bottomStart=16.dp`, `bottomEnd=4.dp`), weißer Nachrichtentext für maximalen Kontrast, formatierte Uhrzeit (HH:mm) und Gelesen-Status-Checkmarks (`Icons.Default.DoneAll`).
  - **Eingehende Nachrichten (`isMine = false`)**: Linksbündig ausgerichtet (`Alignment.Start`), dunkler SurfaceVariant-Hintergrund, asymmetrische Ecken (`topStart=16.dp`, `topEnd=16.dp`, `bottomStart=4.dp`, `bottomEnd=16.dp`), Sender-Name im Kliq Lila-Farbton oberhalb der Sprechblase.
- **`ChatDateDivider`**:
  - Zariertes Trennelement mit horizontalen Linien und Text-Pill ("Heute", "Gestern") zur klaren zeitlichen Segmentierung.
- **`ChatInputBar`**:
  - Untere Eingabeleiste mit `imePadding()`, abgerundetem `OutlinedTextField` (`RoundedCornerShape(24.dp)`), Platzhalter *"Nachricht schreiben…"*, Tastatur-Send-Action (`ImeAction.Send`) und animiertem Sende-Button.

### 2. Screen-Layout & Auto-Scrolling (`ChatDetailScreen.kt`)
- **LazyColumn State Management**: `rememberLazyListState()` verwaltet den Scroll-Zustand.
- **Smooth Auto-Scrolling**: `LaunchedEffect(uiState.messages.size)` animiert sanft zum neusten Element (`listState.animateScrollToItem()`), sobald eine neue Nachricht emittiert wird oder die Tastatur eingeblendet wird.

### 3. ViewModel & Reactive State Binding (`ChatDetailViewModel.kt`)
- **Reaktives Flow Binding**: `ChatDetailUiState` stellt `messages`, `currentInput`, `isOnline`, `isBlocked` immutabel bereit.

### 4. Unit & UI Integrationstests
- **Unit Tests (`ChatDetailViewModelTest.kt`)**: Validierung von `loadConversation`, `onSendMessage` (Anfügen und Textfeld leeren) sowie Eingabesperren bei blockierten Nutzern.
- **Compose UI Tests (`ChatDetailScreenEmulatorTest.kt`)**: Validierung des Renderns von ausgehenden/eingehenden Bubbles, Status-Checkmarks, Date-Dividers und Text-Input.

---

## Git-Strategie & Commit-Historie
Strikter Feature-Branch Workflow (`feature/chat-screen-ui`) mit atomaren Commits:
1. `feat(ui): implement ChatBubble with purple accent, asymmetrical corners, and date dividers`
2. `test(chat): add unit and Compose UI integration tests for ChatDetailScreen`
3. `docs: add PR, Code Review, QA Test Plan, QA Checklist, and Emulator Test Script for chapter 6.2`
