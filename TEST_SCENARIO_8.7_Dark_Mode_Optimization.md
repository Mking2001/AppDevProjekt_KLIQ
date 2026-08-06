# Test-Szenario: Dark-Mode-Optimierung für die Nacht-Nutzung (Kapitel 8.7)

## Übersicht
Dieses Dokument beschreibt das manuelle Test-Szenario sowie die automatisierte Validierung der **Dark-Mode-Optimierung für die Nacht-Nutzung (Kapitel 8.7)** in der Kliq Android Application. Ziel ist der Nachweis, dass das High-Contrast Lila/Dark-Design auf allen Screens (Party-Map, Stadt-Chat, Profilansicht) nahtlos angewendet wird, der Zustand der ViewModels bei Theme-Wechseln erhalten bleibt und keine hellen Lichtblitze oder flackernden Flächen während Screen-Transitionen auftreten.

---

## 1. Voraussetzungen & Test-Setup

- **Android Emulator**: Pixel 7 / Pixel 8 Pro (API Level 33+)
- **System-Einstellungen**: Entwickleroptionen aktiviert
- **App Build**: `feature/dark-mode-optimization`
- **Gradle Command für Tests**:
  ```bash
  ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.theme.DarkModeOptimizationEmulatorTest
  ```

---

## 2. Manuelles Test-Szenario (Schritt-für-Schritt-Anleitung)

### Schritt 1: Initialer App-Start im Light-Mode / System-Standard
1. Android Emulator starten.
2. In den System-Einstellungen des Emulators das Theme auf **Light Mode** einstellen.
3. Kliq-App starten.
4. **Erwartetes Verhalten**:
   - Die App startet sauber im Standard-Lila-Design.
   - Statusbar und Navigationbar passen sich nahtlos an den App-Hintergrund an.

### Schritt 2: Umschalten auf den High-Contrast Dark-Mode
1. System-Schnelleinstellungen des Emulators herunterziehen.
2. Auf **Dunkles Design (Dark Theme)** tippen, um das System-Theme umzuschalten (oder in den Kliq-Einstellungen den In-App-Night-Toggle aktivieren).
3. Zur Kliq-App zurückkehren / App im Vordergrund beobachten.
4. **Erwartetes Verhalten**:
   - Die Benutzeroberfläche wechselt ohne Neustart der Activity oder Datenverlust in den High-Contrast Lila/Dark-Mode.
   - Der Hintergrund wechselt zu Deep Violett-Schwarz (`#0C0914`).
   - Texte und Icons weisen eine hohe Kontrastwirkung (`#F5F3FF` / `#EDE9FE`) auf.
   - Keine weißen Lichtblitze oder ungepufferte helle Flächen sind sichtbar.

### Schritt 3: Navigation durch die Haupt-Screens
1. **Party-Map (Karte)**:
   - In der Bottom-Bar auf **Karte** tippen.
   - Karten-Overlays, Filter-Chips und Quick-View-Cards prüfen.
   - *Kriterium*: Alle Floating Cards und Overlays verwenden dunkle Surface-Farben (`#161124` / `#241C38`).
2. **Stadt-Chat (Nachrichten & Private Chat)**:
   - In der Bottom-Bar auf **Nachrichten** tippen.
   - Einen Gruppen-Chat (z. B. "Afterwork Köln") und einen privaten Chat öffen.
   - *Kriterium*: Eigene Chat-Sprechblasen zeigen das Kliq-Marken-Lila (`#9333EA` / `#7C3AED`), fremde Sprechblasen ein dunkles Surface-Violett (`#241C38`). Kein weißer Hintergrund im Chat-Verlauf.
3. **Profilansicht (Profil)**:
   - In der Bottom-Bar auf **Profil** tippen.
   - Profil-Header, Einstellungskarten und Bottom Sheets prüfen.
   - *Kriterium*: Einheitlicher dunkler Hintergrund, lesbare Kontraste, keine hellen Umrandungen.

### Schritt 4: Validierung der Screen-Transitionen (Flash-Test)
1. Mehrfach zügig zwischen **Home**, **Karte**, **Nachrichten** und **Profil** wechseln.
2. App in den Hintergrund schieben und wieder in den Vordergrund holen.
3. **Erwartetes Verhalten**:
   - Die Status- und Navigationsleisten bleiben durchgehend in der dunklen Hintergrundfarbe (`#0C0914`) gerendert.
   - Es treten keine kurzzeitigen weißen Lichtblitze (White Flashes) bei Container-Swaps oder Screen-Übergängen auf.
   - Eingabefelder und Scroll-Positionen in ViewModels bleiben vollständig erhalten.

---

## 3. Akzeptanzkriterien & Matrix

| Prüfpunkt | Erwartetes Ergebnis | Status |
| :--- | :--- | :--- |
| **High-Contrast Lila/Dark Palette** | Hintergrund `#0C0914`, Surfaces `#161124`, Texte `#F5F3FF` mit hoher Lesbarkeit | OK |
| **Anti-Flash System Bars** | Status- & Navigation-Bars bleiben während Screen-Wechseln dunkel | OK |
| **State Preservation** | ViewModel-Daten und UI-Eingaben bleiben beim Theme-Switch unverändert | OK |
| **Chat-Sprechblasen** | Eigen: Purple Container; Fremd: Surface Variant; 0% White Background Leaks | OK |
| **Party-Map Overlays** | Map-Controls und Search-Overlays nutzen konsistent Dark Surface Tokens | OK |
