# Pull Request: Kapitel 6.3 - Stadt-basierter öffentlicher Chat

## Zusammenfassung
Dieser Pull Request implementiert Kapitel 6.3 („Stadt-basierter öffentlicher Chat“) für die native Mobile-App Kliq unter strikter Einhaltung der MVVM-Architektur, des Repository-Patterns und des Kliq High-Contrast Purple/Dark-Designsystems. Die Funktion weist Nutzern automatisch den passenden öffentlichen Stadt-Chat (z. B. „Berlin - Tonight“, „München - Party Radar“, „Hamburg - Reeperbahn“) basierend auf ihren aktuellen GPS-Koordinaten zu. Zudem bietet sie lokales Room DB Caching für Offline-Verfügbarkeit, eine stadtbezogene Banner-Header-Anzeige, ein Modal Bottom Sheet zum manuellen Stadt-Wechsel sowie Absender-Avatare und Namen in Gruppenchat-Nachrichten.

## Wichtigste Änderungen

### 1. Standort-Mapping & Datenmodelle (`CityChatLocationMapper.kt` & `ChatModels.kt`)
- **`CityChatLocationMapper`**:
  - Berechnet geodätische Distanzen (`calculateDistanceInKm()`) zwischen Nutzer-GPS-Koordinaten und unterstützten Metropolen (Berlin, München, Hamburg, Köln, Frankfurt).
  - Wandelt Standorte automatisch in den passenden Stadt-Chat (`resolveCityForLocation()`) um und ermittelt Online-Mitgliederzahlen.
- **`ChatListItem` Erweitungen**:
  - Neue Felder `distanceKm: Double?`, `onlineMembersCount: Int` und `isGpsAssigned: Boolean`.

### 2. Repository Layer & Caching (`ChatRepositoryImpl.kt`)
- Implementation von `getCityChatForLocation()`, `syncPublicCityMessages()` und `joinPublicCityChat()`.
- Nahtloses Caching von Stadt-Chats und Nachrichten in der Room-Datenbank (`ChatDao`, `ChatEntity`, `MessageEntity`).

### 3. ViewModel & Reactive Location Binding (`ChatListViewModel.kt`)
- Injektion von `LocationRepository` und reaktives Streaming von `locationUpdates`.
- Automatische Zuweisung des lokalen Stadt-Chats und Bereitstellung von Manuell-Wechsel-Funktionen (`openCitySwitcher()`, `selectCityChat()`).

### 4. UI-Komponenten & Compose Layout (`ChatComponents.kt` & `ChatListScreen.kt`)
- **`CityChatHeaderBanner`**: Prominente Header-Karte im Kliq Lila/Dark-Design mit Anzeige von Standort, Entfernung in km („⚡ 248 Feiernde online • 2.5 km entfernt“) und Button "Wechseln".
- **`CityChatSwitcherSheet`**: Modal Bottom Sheet zur manuellen Auswahl aus unterstützten Metropol-Chats.
- **Sender-Erkennung**: Visualisierung von Sender-Namen im Lila-Farbton und Sender-Initialen in Gruppenchats.

---

## Git-Strategie & Commit-Historie
Strikter Feature-Branch Workflow (`feature/city-public-chat`) mit atomaren Commits:
1. `feat(data): add CityChatLocationMapper and location-based public chat models`
2. `feat(repository): implement location-based city chat resolution and Room DB caching in ChatRepositoryImpl`
3. `feat(viewmodel): integrate GPS location flow and city chat auto-assignment in ChatListViewModel`
4. `feat(ui): add CityChatHeaderBanner, sender avatars in group bubbles, and CityChatSwitcherSheet`
5. `test(city-chat): add unit and Compose UI integration tests for city public chats`
6. `docs: add PR, Code Review, QA Test Plan, QA Checklist, and Emulator Test Script for chapter 6.3`
