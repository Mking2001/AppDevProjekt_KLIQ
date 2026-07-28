# QA-Test-Skript & Emulator-Anleitung: Kapitel 5.9 – Nutzer-Reporting & Blockier-Funktion

**Projekt:** Kliq Native Mobile App (Android / Kotlin)  
**Modul:** Kapitel 5.9 – Nutzer-Reporting & Blockier-Funktion  
**Architektur:** MVVM, Room DB, Retrofit Backend Sync, Jetpack Compose  
**Dokument-Typ:** Emulator Test-Skript & Manuelle QA-Verifikations-Anleitung  
**Datum:** 28. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Dieses Test-Skript führt Schritt für Schritt durch die Verifikation der **Nutzer-Reporting- und Blockier-Funktion** auf dem Android Emulator / Simulator. 
Es stellt sicher, dass:
1. **Reporting-Flow**: Das Modal Bottom Sheet zur Meldung von Nutzern mit vordefinierten Meldegründen (*Spam*, *Beleidigung*, *Unangebrachte Inhalte*, *Fake-Profil*) und Freitext-Feld flüssig funktioniert und visueller Kliq-Lila-Feedback-Toast erscheint.
2. **Blockier-Prozess**: Der Bestätigungsdialog das versehentliche Blockieren verhindert und den Nutzer sofort nach Bestätigung lokal und im Backend blockiert.
3. **Reaktive System-Filterung**: Blockierte Nutzer reaktiv in Echtzeit aus der Kartenansicht (Map-Marker), den Suchergebnissen/Feed (Explore) sowie dem Chat-Posteingang verschwinden.
4. **Room DB Persistenz**: Der Blockierungs-Status in der lokalen SQLite/Room-Datenbank (`blocked_users` Tabelle, Migration v14) auch nach einem vollkommenen App-Neustart persistent erhalten bleibt.

---

## 💻 2. Test-Umgebung & Vorbereitung

### Emulator Configuration
- **Device**: Android Studio Emulator Pixel 7 Pro / Pixel 6 (API 34 / Android 14).
- **Design System**: Kliq High-Contrast Dark-Theme (#121212 / Kliq Purple #7C3AED / Error Red #EF4444).
- **Gradle Command (Automated Compose Integration Tests)**:
  ```powershell
  $env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  ./gradlew testDebugUnitTest --tests "com.kliq.app.data.repository.UserRepositoryReportingTest" --tests "com.kliq.app.viewmodel.OtherUserProfileViewModelTest"
  ```

---

## 🧪 3. Schritt-für-Schritt Emulator Test-Skript

### 🔹 Schritt 1: Profil / Chat-Detailansicht aufrufen
1. Starte die Kliq-App im Android Studio Emulator.
2. Navigiere zu einem fremden Nutzerprofil (z. B. `Sarah_Kliq` / `Lisa W.`) oder öffne eine bestehende Einzelkonversation im Chat-Tab.
3. **Soll-Ergebnis**:
   - Die Profil- bzw. Chatansicht wird im Kliq High-Contrast Dark-Theme geladen.
   - In der Top-Aktionsleiste oben rechts befindet sich das Options-Icon mit den 3 vertikalen Punkten (`Icons.Default.MoreVert`).

---

### 🔹 Schritt 2: Optionsmenü öffnen & "Nutzer melden" wählen
1. Tippe oben rechts auf das Options-Icon (`MoreVert`).
2. **Soll-Ergebnis**: Das Kliq Dropdown-Kontextmenü öffnet sich flüssig. Es enthält die zwei Einträge:
   - 🚩 **Nutzer melden**
   - 🚫 **Nutzer blockieren**
3. Tippe auf **"Nutzer melden"**.

---

### 🔹 Schritt 3: Meldegrund auswählen, Details eingeben & Report absenden
1. **Soll-Ergebnis**: Das Kliq **UserReportBottomSheet** schiebt sich von unten in den Screen.
2. Überprüfe die vordefinierten Meldegründe:
   - `Spam`
   - `Beleidigung`
   - `Unangebrachte Inhalte`
   - `Fake-Profil`
3. Wähle den Grund **"Fake-Profil"** aus (die Auswahl wird mit lila Akzent-Farbe und Checkmark hervorgehoben).
4. Tippe in das Freitext-Feld *"Zusätzliche Details (optional)"* und gib den Text ein:  
   `"Verwendet fremde Fotos und falsche Identität."`
5. Tippe auf den Button **"Meldung absenden"**.
6. **Soll-Ergebnis**:
   - Das Bottom Sheet schließt sich umgehend.
   - Eine visuelle UI-Rückmeldung (Snackbar/Toast Banner im Kliq-Lila-Design `#7C3AED`) wird am unteren Bildschirmrand eingeblendet:  
     *„Nutzer wurde gemeldet. Das Kliq-Sicherheitsteam prüft die Meldung.“*
   - Im Netzwerk-Log / Repository wird das DTO `ReportUserRequestDto` an den Server geschickt.

---

### 🔹 Schritt 4: "Nutzer blockieren" wählen & Bestätigungsdialog prüfen
1. Tippe erneut auf das Options-Icon (`MoreVert`) in der TopBar.
2. Wähle den Menüpunkt **"Nutzer blockieren"**.
3. **Soll-Ergebnis**:
   - Der Kliq **BlockConfirmationDialog** wird zentral im Screen angezeigt.
   - Er enthält den Titel *„[Nutzername] blockieren?“* sowie den Warnhinweis:  
     *„Möchtest du [Nutzername] wirklich blockieren? Blockierte Nutzer können dir keine Nachrichten mehr senden und werden auf der Karte ausgeblendet.“*
4. Tippe im Dialog auf den lila Button **"Blockieren"**.

---

### 🔹 Schritt 5: Sofortiges Verschwinden & Reaktive System-Filterung prüfen
1. **Sofortige Ansichts-Reaktion**:
   - Im Chat: Die Nachrichteneingabeleiste wird gesperrt (Placeholder: *"Du hast diesen Nutzer blockiert"*).
   - Im Profil: Das Profil aktualisiert seinen Status und zeigt die Warnkarte *"Du hast diesen Nutzer blockiert"* inkl. „Entblocken“-Button.
2. **Kartenansicht (Map)**:
   - Wechsle zum **Karten-Tab**.
   - **Soll-Ergebnis**: Der Marker des soeben blockierten Nutzers ist **sofort** von der Karte verschwunden und wird nicht mehr gerendert.
3. **Suchergebnisse & Discovery Feed (Explore)**:
   - Wechsle zum **Explore-Tab**.
   - Gib den Namen des Nutzers in die Suchleiste ein.
   - **Soll-Ergebnis**: Der blockierte Nutzer sowie dessen erstellte Events/Beiträge tauchen **nicht mehr** in den Suchergebnissen auf.
4. **Chat-Übersicht (Chat List)**:
   - Wechsle zum **Chat-Tab**.
   - **Soll-Ergebnis**: Der Privatchat mit dem blockierten Nutzer wurde automatisch aus der Konversationsliste ausgeblendet.

---

### 🔹 Schritt 6: Persistence-Check nach App-Neustart (Room DB)
1. Schließe die Kliq-App im Emulator vollständig (Kill App Task / `adb shell am force-stop com.kliq.app`).
2. Starte die Kliq-App erneut auf dem Emulator.
3. Wechsle zur Karte, zum Chat-Tab und zum Explore-Feed.
4. **Soll-Ergebnis**:
   - Der blockierte Nutzer bleibt weiterhin **vollständig ausgeblendet**.
   - Der Eintrag in der lokalen Room-Datenbank (Tabelle `blocked_users`) mit den Feldern `userId`, `blockedUserId`, `reason` und `blockedAtTimestampMs` bleibt persistent erhalten und wird beim App-Start über `BlockedUserDao.getBlockedUserIdsFlow()` reaktiv geladen.

---

## 📊 4. Automatisierter Compose UI Integrationstest

Zusätzlich zum manuellen Skript steht das Compose UI-Integrationstest-Skript bereit:  
[UserReportingBlockingEmulatorTest.kt](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/UserReportingBlockingEmulatorTest.kt)

```kotlin
// testReportingBottomSheet_selectReasonAndSubmit_invokesCallback()
// testBlockConfirmationDialog_confirmAction_invokesCallback()
// testProfileScreen_blockedState_displaysBlockedNoticeBanner()
// testChatDetailScreen_blockedState_disablesInputAndShowsBanner()
```
