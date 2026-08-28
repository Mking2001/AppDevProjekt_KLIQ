# Ursachenanalyse der aktuellen Ausfälle (Stand vor Release-Härtung)

Diese Analyse fasst zusammen, warum die im Testbericht dokumentierten Funktionen ausfallen.
Grundlage ist ein vollständiger Durchlauf der Quellen sowie ein erfolgreicher `assembleDebug`-Build.
Der Build kompiliert fehlerfrei; sämtliche Ausfälle sind Laufzeit- und Integrationslücken, keine Compilerfehler.

---

## 1. Karte bleibt weiß

**Ursache:** `local.properties` enthält den Platzhalterwert `AIzaSy_DEFAULT_DEVELOPMENT_MAPS_KEY`.
`app/build.gradle.kts` liest `MAPS_API_KEY` aus `local.properties` und schreibt es via `manifestPlaceholders`
in das `com.google.android.geo.API_KEY`-Meta-Tag. Das Maps SDK lehnt den Platzhalter ab und rendert
eine leere Kachelfläche, ohne den Prozess abzubrechen.

**Folgefehler:** Die Marker-Layer werden korrekt aufgebaut, sind aber unsichtbar, weil kein Kartenmaterial lädt.
Der Fehler wirkt deshalb wie ein Marker-Problem, ist aber ein Konfigurationsproblem.

---

## 2. Karte, Explore und Club-Detail zeigen keine echten Daten

**Ursache:** Es existiert kein Schreibpfad in die `clubs`-Tabelle. `ClubDao.insertClubs()` wird
ausschließlich aus `ClubRepositoryImpl.searchExternalClubs()` und
`ClubAndEventRepositoryImpl` aufgerufen — beide setzen einen erreichbaren Backend-Endpunkt voraus.
`AppModule.provideApiService()` zeigt jedoch auf die Platzhalter-Basis-URL
`https://api.kliq-nightlife.com/`, die nicht existiert.

Die Room-Datenbank bleibt damit dauerhaft leer:

* `MapViewModel.observeClubRepository()` erhält eine leere Liste und fällt auf `getFallbackVenues()` zurück.
* `ExploreViewModel` ist gar nicht am `ClubRepository` angebunden und arbeitet mit einer hart
  codierten Liste im ViewModel.
* `ClubDetailScreen` lädt über die ID und findet keinen Datensatz.

**Zweitursache:** Alle Fallback-Datensätze sind auf Berlin, München und Hamburg ausgelegt
(`MapViewModel.getFallbackVenues()`, `ExploreViewModel.loadMockData()`,
`CityChatLocationMapper.SUPPORTED_CITIES`, `CameraPositionStateData`-Default 52.5200/13.4050).
Der Zielmarkt Klagenfurt ist in keinem Datensatz vorhanden, wodurch Entfernungsangaben und
Stadt-Chat-Zuordnung auch bei aktivem GPS unbrauchbar sind.

---

## 3. Chatnachrichten verschwinden beim Verlassen des Screens

**Ursache:** `ChatDetailViewModel` hält die Nachrichtenliste ausschließlich im `MutableStateFlow`
und erzeugt den Gesprächsinhalt in `getMockConversation(chatId)`. Das vollständig implementierte
`ChatRepository` inklusive `ChatDao`, `ChatEntity` und `MessageEntity` wird nicht injiziert.
Beim Verlassen des Screens wird das ViewModel abgeräumt, der State geht verloren und
`loadConversation()` baut beim erneuten Öffnen wieder die Mock-Liste auf.

Betroffen sind Textnachrichten, komprimierte Bildnachrichten und Sprachnachrichten gleichermaßen,
da alle drei Sendepfade nur `_uiState.update { messages + newMessage }` ausführen.

---

## 4. Ungelesen-Badges bleiben nach dem Lesen stehen

**Ursache:** `ChatRepository.markChatAsRead()` und die zugehörige DAO-Abfrage
`UPDATE chats SET unreadCount = 0 WHERE id = :chatId` existieren, werden aber von keinem
Aufrufer verwendet. Weder `ChatListScreen` beim Antippen eines Chats noch
`ChatDetailScreen` beim Öffnen lösen die Aktion aus.

**Zweitursache:** `ChatListViewModel` hält `rawPublicChats` und `rawPrivateChats` als lokale
Felder mit Mock-Inhalt und übernimmt Repository-Daten nur, wenn die Room-Abfrage eine
nicht-leere Liste liefert (`if (conversations.isNotEmpty())`). Da die `chats`-Tabelle nie
gefüllt wird, bleibt der Mock-Zustand dauerhaft aktiv und ist gegen Schreibvorgänge immun.

---

## 5. Home-Feed: leere Bildflächen, tote Storys, inaktive Interaktionen

**Ursachen im Detail:**

* `KliqFeedCard` in `PlaceholderCards.kt` nimmt weder `likeCount`, `isLiked` noch Callbacks
  als Parameter. Alle drei Interaktions-Buttons sind mit `onClick = { /* Stub */ }` belegt,
  die Like-Zahl ist als Literal `"42 Likes"` einkodiert. `HomeViewModel.onLikePost()` ist
  implementiert, wird aber von keiner Stelle aufgerufen.
* Die Bildfläche ist ein reiner `Box` mit Farbverlauf ohne Motiv oder Fallback-Symbolik,
  was als „leere Bildfläche“ wahrgenommen wird.
* `StoryRow` in `HomeScreen.kt` rendert die Avatare ohne `clickable`-Modifier. Es gibt keinen
  Story-Viewer und keine Möglichkeit, `hasUnseenStory` zurückzusetzen.
* `HomeViewModel.onCreatePost()` ist ein leerer Rumpf. Der FAB ruft die Methode auf, es passiert nichts.
* `HomeViewModel` ist ohne Konstruktorabhängigkeiten und speist den Feed aus `loadMockData()`.

---

## 6. Profil bearbeiten, Einstellungen und „Über Kliq“ reagieren nicht

**Ursache:** `ProfileViewModel.onEditProfile()` und `onFollowToggle()` sind leere Methodenrümpfe.
Der „Bearbeiten“-Button ist verdrahtet, es existiert aber kein Dialog und kein Schreibpfad,
obwohl `UserRepositoryImpl.saveProfile()` vollständig implementiert ist.

In `KliqMainScaffold` sind zwei der fünf Menüaktionen unbelegt:

```kotlin
TopBarMenuAction.Settings -> { /* Settings-Screen öffnen */ }
TopBarMenuAction.About    -> { /* About-Dialog anzeigen */ }
```

`ToggleTheme` und `Logout` funktionieren, wodurch der Eindruck entsteht, das Menü sei
teilweise defekt statt unvollständig implementiert.

---

## 7. Bedienelemente der Karte ragen in die Statusleiste

**Ursache:** `MainActivity` aktiviert `enableEdgeToEdge()`, wodurch die App unter die System-Leisten
zeichnet. `MapScreen` rendert seine Filterleiste jedoch in einer `Box` mit
`Modifier.align(Alignment.TopCenter)` ohne `statusBarsPadding()`. Anders als die übrigen Screens
nutzt `MapScreen` kein `KliqScreenScaffold` mit `TopAppBar` und erhält daher keine Insets.

---

## 8. Details- und Routen-Button der Karten-Quickview ohne Funktion

**Ursache:** `MapScreen` übergibt leere Lambdas an die Quickview-Karten:

```kotlin
onNavigateDetails = { /* Navigate to Venue Detail */ }
onSendMessage     = { /* Trigger chat navigation */ }
```

Der `MapScreen` erhält vom `KliqNavHost` keine Navigations-Callbacks, obwohl
`ClubRoutes.clubDetail(clubId)` und `ChatRoutes.chatDetail(chatId)` definiert sind.
Ein Routen-Intent auf eine externe Karten-App ist nirgends implementiert; das Manifest
enthält zudem keine `<queries>`-Deklaration, die unter Android 11+ für die Sichtbarkeit
externer Navigations-Apps erforderlich ist.

---

## Querschnittsbefunde

1. **Optionale Abhängigkeiten verdecken Fehler.** `ChatListViewModel`, `MapViewModel` und
   `ChatRepositoryImpl` deklarieren zentrale Abhängigkeiten als nullable mit Default `null`
   (`chatRepository: ChatRepository? = null`). Hilt injiziert korrekt, aber die Konstrukte
   erlauben ein stilles Weiterlaufen mit Mock-Daten und verhindern, dass fehlende
   Verdrahtung im Build auffällt.

2. **Leere `catch`-Blöcke.** An mehreren Stellen wird `.catch { }` ohne Protokollierung
   verwendet. Datenbank- und Netzwerkfehler sind dadurch weder in der UI noch im Log sichtbar.

3. **Gradle-Konfigurationsfehler.** In `app/build.gradle.kts` liegt der Block
   `configurations.all { resolutionStrategy { ... } }` innerhalb des `dependencies`-Blocks.
   Das löst zwar über den äußeren Projekt-Scope auf, ist aber irreführend und bricht bei
   künftigen Gradle-Versionen.

4. **Legacy-Theme im Manifest.** `application` und `MainActivity` verwenden
   `@android:style/Theme.Material.NoActionBar` statt eines projekteigenen Themes. Damit sind
   Statusleisten-Farbe und Splash-Verhalten nicht kontrollierbar.

---

## Priorisierung für die Behebung

| Priorität | Bereich | Maßnahme |
|-----------|---------|----------|
| 1 | Konfiguration | Maps-Key aus `google-services.json` in `local.properties` übernehmen |
| 2 | Daten | Klagenfurt-Seed-Datensatz anlegen und beim ersten Start in Room schreiben |
| 3 | Chat | `ChatDetailViewModel` und `ChatListViewModel` an `ChatRepository` binden, `markChatAsRead()` auslösen |
| 4 | Home | Story-Viewer, Beitrags-Editor und Like-Interaktion implementieren |
| 5 | Profil | Bearbeiten-Dialog mit `UserRepository.saveProfile()` verbinden |
| 6 | Navigation | Einstellungen- und Info-Dialog belegen, Karten-Callbacks verdrahten |
| 7 | Layout | `statusBarsPadding()` auf den Karten-Bedienelementen setzen |
| 8 | Konfiguration | Gradle-Block bereinigen, eigenes App-Theme, `<queries>` für Navigations-Intents |
