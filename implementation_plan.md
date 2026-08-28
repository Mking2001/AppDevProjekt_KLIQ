# Umfassender Reparatur- & Fertigstellungsplan für KLIQ (Vorab-Release für die Abgabe)

Dieses Dokument beschreibt die konkreten Schritte, um alle von dir im Testbericht festgestellten Lücken, inaktiven Buttons und Bugs vor der Abgabe in 5 Tagen strukturiert und nachhaltig zu beheben.

---

## Übersicht der Kernprobleme & Lösungsansätze

1. **🗺️ Karte & Google Maps:**
   * **Problem:** Hintergrund weiß (Platzhalter-Key); Buttons ragen in die Android-Statusleiste; Details/Route-Buttons ohne Funktion.
   * **Lösung:** Google Cloud API-Key aus `google-services.json` in `local.properties` einbinden; `statusBarsPadding()` auf den oberen Bedienelementen anbringen; `onNavigateDetails` mit `ClubRoutes.clubDetail(clubId)` verknüpfen; Routen-Button mit Google Maps Navigation Intent verknüpfen.

2. **💬 Chat-Persistierung & Ungelesen-Zähler:**
   * **Problem:** Eingegebene Nachrichten verschwinden beim Verlassen; Ungelesen-Badges bleiben nach dem Lesen stehen.
   * **Lösung:** `ChatDetailViewModel` an das bereits existierende `ChatRepository` und die Room-Datenbank anbinden; gesendete Nachrichten (Text/Bilder/Voice) in Room speichern; beim Betreten eines Chats `markChatAsRead()` auslösen.

3. **🏠 Home-Feed, Storys & Beiträge:**
   * **Problem:** Leere Bildflächen; Klicks auf Storys bewirken nichts; `+`-Button ohne Funktion; Like/Kommentar-Buttons inaktiv.
   * **Lösung:** 
     * Story-Viewer Dialog: Klick auf Story öffnet Vollbild-Story und markiert sie als gesehen (Farbkreis verschwindet).
     * `+`-FAB: Öffnet ein "Neuen Beitrag erstellen"-Modal, wodurch neue Posts direkt in den Feed gepostet werden.
     * Likes & Kommentare: Interaktives Liken (Herz füllt sich Lila + Zähler erhöht sich) und Kommentar-Anzeige.
     * Fallback-Grafiken: Schöne Farbverläufe und Icons für Beiträge ohne geladene Web-Bilder.

4. **👤 Profil bearbeiten, Einstellungen & „Über Kliq“:**
   * **Problem:** "Profil bearbeiten", "Einstellungen" und "Über Kliq" im Menü reagieren nicht; Profil-Tabs ohne Klick-Feedback.
   * **Lösung:**
     * "Profil bearbeiten"-Modal: Name, Bio und Standort editieren und im `UserRepository` speichern.
     * "Einstellungen"-Dialog: Benachrichtigungen, Dark-Mode-Umschalter und Cache-Bereinigung.
     * "Über Kliq"-Dialog: Projekt- und Versionsinformationen für die Prüfer.
     * Profil-Tabs: Klickbare Event-Karten und Besuchshistorie-Einträge.

---

## Geplante Dateiänderungen

### 1. Karten-Modul & Navigation
#### [MODIFY] [local.properties](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/local.properties)
* Google Maps API-Key auf den Projekt-Key `AIzaSyCHkOqj5rFGtw6V7WZmXK9yxOqV4CbVVCU` aktualisieren.

#### [MODIFY] [MapScreen.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/map/MapScreen.kt)
* `statusBarsPadding()` für die Filter-Buttons oben hinzufügen.
* QuickView-Card `onNavigateDetails` mit Navigation zum ClubDetailScreen verbinden.
* Route-Intent einbinden (Öffnet Route in externer Maps-App via Geokoordinaten).

---

### 2. Chat-System & Datenbank-Persistierung
#### [MODIFY] [ChatDetailViewModel.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/chat/ChatDetailViewModel.kt)
* `ChatRepository` injizieren.
* `messages` als reaktiven Flow aus Room laden und `sendMessage()` in `ChatRepository.sendMessage()` persistieren.

#### [MODIFY] [ChatListViewModel.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/chat/ChatListViewModel.kt)
* Reaktiv an `ChatRepository.getAllChats()` anbinden, damit neue Nachrichten sofort in der Vorschau sichtbar sind.

#### [MODIFY] [ChatListScreen.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/chat/ChatListScreen.kt)
* Beim Antippen eines Chats den Ungelesen-Status auf gelesen (`markChatAsRead`) setzen.

---

### 3. Home Feed, Stories & Interaktion
#### [MODIFY] [HomeScreen.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/home/HomeScreen.kt)
* Story-Klick-Handler mit Story-Modal verbinden.
* "Neuer Beitrag"-Dialog einbinden.

#### [MODIFY] [HomeViewModel.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/home/HomeViewModel.kt)
* Methoden für Story-Anzeige, Post-Erstellung und Likes ergänzen.

#### [MODIFY] [PlaceholderCards.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/components/PlaceholderCards.kt)
* Like-Button interaktiv machen (Farbwechsel & Zähler).
* Fallback-Grafiken für Post-Karten einbauen.

---

### 4. Profil, Einstellungen & Info-Dialoge
#### [MODIFY] [ProfileScreen.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/profile/ProfileScreen.kt)
* "Profil bearbeiten"-Dialog hinzufügen (Name, Bio, Stadt ändern & speichern).
* Tab-Inhalte für Events und Historie mit Details befüllen.

#### [MODIFY] [ProfileViewModel.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/profile/ProfileViewModel.kt)
* `onEditProfile()`, `updateProfileData()` implementieren und über `UserRepository` speichern.

#### [MODIFY] [KliqMainScaffold.kt](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/navigation/KliqMainScaffold.kt)
* `TopBarMenuAction.Settings` ➔ "Einstellungen"-Dialog öffnen (Push-Toggles, Dark Mode).
* `TopBarMenuAction.About` ➔ "Über Kliq"-Dialog öffnen (App-Version 1.0, Features, Team).

---

## Verifikationsplan

### 1. Automatisierte Tests
* Kompilierung und Unit-Tests ausführen:
  ```powershell
  $env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat testDebugUnitTest
  ```
* APK neu erstellen:
  ```powershell
  .\gradlew.bat assembleDebug
  ```

### 2. Manuelle Verifikation auf Gerät / Emulator
1. **Karte:** Prüfen, dass die Top-Bar nicht überlappt und Details-Klicks zum Club führen.
2. **Chat:** Nachricht schreiben, Chat verlassen und erneut öffnen ➔ Nachricht ist noch da; Badge ist weg.
3. **Home-Feed:** Auf Story tippen (wird als gesehen markiert), auf `+` tippen (neuen Post erstellen), Like-Button drücken (Herz füllt sich lila).
4. **Profil & Menü:** Auf "Profil bearbeiten" tippen und Bio ändern; auf "Einstellungen" und "Über Kliq" im 3-Punkte-Menü tippen.
