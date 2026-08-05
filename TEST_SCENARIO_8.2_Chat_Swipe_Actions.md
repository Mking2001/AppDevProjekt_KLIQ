# QA Test-Szenario & UI-Test-Skript: Kapitel 8.2 - Swipe-to-Action in Chat-Listen (Löschen/Archivieren)

Dieses Dokument beschreibt das vollständige Test-Szenario sowie die Schritt-für-Schritt Anleitung zur manuellen und automatisierten Überprüfung der **„Swipe-to-Action in Chat-Listen (Löschen/Archivieren)“** (Kapitel 8.2) im Android-Emulator.

---

## 🛠️ 1. Test-Voraussetzungen

- **Gerät / Emulator**: Android Emulator (API Level 33 oder 34, x86_64 Image).
- **Branch**: `feature/chat-swipe-actions`.
- **Design-System**: Kliq High-Contrast Dark Mode (`DarkSurface` `#1A1523`, Kliq Lila `#8A2BE2`, Error Red `#EF4444`).
- **Test-Komponenten**:
  - `ChatListScreen.kt` (`com.kliq.app.ui.screens.chat.ChatListScreen`)
  - `ChatListViewModel.kt` (`com.kliq.app.ui.screens.chat.ChatListViewModel`)
  - `SwipeableActionRow.kt` (`com.kliq.app.ui.components.SwipeableActionRow`)
  - `DeleteChatConfirmationDialog.kt` (`com.kliq.app.ui.components.DeleteChatConfirmationDialog`)

---

## 🧪 2. Schritt-für-Schritt Test-Szenario

### Schritt 1: Start & Navigation zur Chat-Listen-Übersicht
1. Starte die Kliq App im Android Emulator:
   ```powershell
   $env:JAVA_HOME="C:\Users\kremidas\jdk17\jdk-17.0.10+7"
   .\gradlew.bat installDebug
   ```
2. Navigiere in der unteren Navigationsleiste zur Chat-Listen-Übersicht (`ChatListScreen`).
3. **Erwartetes Ergebnis**:
   - Die Tabs **„Öffentliche Stadt-Chats“** und **„Private Nachrichten“** werden angezeigt.
   - Die Liste der aktiven Chats wird geladen.

---

### Schritt 2: Swipe nach links (Archivieren)
1. Wähle einen Test-Chat aus der Liste (z. B. **"Lisa W."**).
2. Simuliere eine Swipe-Geste nach links (von rechts nach links / `EndToStart`).
3. **Erwartetes Ergebnis**:
   - Beim Ziehen erscheint ein violetter Hintergrund (`#8A2BE2` / Kliq Lila) mit einem weißen Archiv-Icon.
   - Beim Erreichen des Schwellenwerts wird ein leichtes haptisches Feedback ausgelöst.
   - Der Chat verschwindet mit einer flüssigen Ausblend-Animation aus der aktiven Liste.
   - Eine Snackbar bestätigt: *"Chat „Lisa W.“ archiviert"* inklusive Rückgängig-Option.
   - Der Chat-Status wird im `ChatListViewModel` und in der Room-Datenbank (`isArchived = true`) aktualisiert.

---

### Schritt 3: Swipe nach rechts (Löschen mit Sicherheitsabfrage)
1. Wähle einen weiteren Chat aus der Liste (z. B. **"Max K."**).
2. Simuliere eine Swipe-Geste nach rechts (von links nach rechts / `StartToEnd`).
3. **Erwartetes Ergebnis**:
   - Beim Ziehen erscheint ein roter Hintergrund (`#EF4444` / Error Red) mit einem Mülleimer-Icon.
   - Nach Loslassen der Geste erscheint sofort der `DeleteChatConfirmationDialog` im lila Dark-Mode Design über der Liste.
   - Titel: *"Chat löschen?"*
   - Text: *"Möchtest du den Chat mit „Max K.“ wirklich löschen?"* inkl. Hinweis auf dauerhafte Entnahme.

---

### Schritt 4: Bestätigen & Verifizieren der permanenten Löschung
1. Tippe im Bestätigungsdialog auf die rote Schaltfläche **„Chat löschen“**.
2. **Erwartetes Ergebnis**:
   - Der Dialog schließt sich.
   - Der Chat wird dauerhaft aus der aktiven Liste entfernt.
   - Der Eintrag wird in der lokalen Room-Datenbank gelöscht (`deleteChatById`).
   - Eine Bestätigungs-Snackbar *"Chat „Max K.“ gelöscht"* wird angezeigt.

---

## 🤖 3. Automatisierter UI-Test (Emulator Execution)

Der automatisierte UI-Test [`ChatSwipeActionsEmulatorTest.kt`](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/ChatSwipeActionsEmulatorTest.kt) überprüft die Swipe-Gesten und den Bestätigungsdialog programmatisch.

### Ausführung des automatisierten UI-Tests im Emulator:

```powershell
# 1. Umweltvariablen setzen
$env:JAVA_HOME="C:\Users\kremidas\jdk17\jdk-17.0.10+7"
$env:ANDROID_HOME="C:\Users\kremidas\AppData\Local\Android\Sdk"

# 2. UI-Test auf gestartetem Android Emulator ausführen
.\gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.ChatSwipeActionsEmulatorTest
```

---

## 📋 4. Verifikations-Checkliste

| Test-Schritt | Prüfpunkt | Status |
| :--- | :--- | :---: |
| **Schritt 1** | App-Start & Navigation zum ChatListScreen | **PASSED** |
| **Schritt 2** | Swipe Links (Archivieren): Lila Indikator, Animation & DB-Sync | **PASSED** |
| **Schritt 3** | Swipe Rechts (Löschen): Roter Indikator & Sicherheitsdialog-Aufruf | **PASSED** |
| **Schritt 4** | Dialog-Bestätigung & permanente Entfernung aus Room-DB | **PASSED** |
| **Edge Cases** | Abbrechen im Lösch-Dialog stellt Chat unverändert wieder her | **PASSED** |
