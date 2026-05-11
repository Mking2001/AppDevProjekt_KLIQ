# QA Test-Szenario: Layout-Scaffolding (Schritt 4)

> **AI-GENERATED DOCUMENT**
> Dieses Test-Szenario wurde vollständig durch KI generiert.
> Datum: 2026-05-11

## Voraussetzungen

- Android Studio mit konfiguriertem Emulator (API 26+)
- Branch: `feature/screen-scaffolding`

## 🔧 Build & Start

```bash
# 1. Projekt kompilieren (ohne Tests)
./gradlew assembleDebug

# 2. Auf Emulator installieren & starten
./gradlew installDebug
adb shell am start -n com.kliq.app/.MainActivity

# 3. Instrumentierte Tests ausführen
./gradlew connectedAndroidTest

# 4. Nur den Scaffolding-Test:
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.screens.ScreenScaffoldingTest
```

---

## 📋 Manuelle Test-Checkliste

### 1. App-Start & Home-Screen
- [ ] App startet ohne Crash
- [ ] Dunkler Hintergrund (Lila/Dark-Mode) wird angezeigt
- [ ] TopAppBar zeigt "Kliq" als Titel
- [ ] Filter-Icon (rechts oben) ist sichtbar
- [ ] Chat-Icon (rechts oben) ist sichtbar
- [ ] Story-Row zeigt horizontale Avatar-Kreise (Anna, Max, Lisa, ...)
- [ ] Story-Avatare mit ungesehenen Stories haben Gradient-Border (Lila→Fuchsia)
- [ ] Feed-Karten werden angezeigt (Anna M., Max K., Lisa W., Tom S.)
- [ ] Jede Feed-Karte hat: Avatar, Name, Zeit, Bild-Platzhalter, Text, Like/Kommentar/Teilen
- [ ] FAB (lila "+"-Button) ist rechts unten sichtbar
- [ ] Fade-in-Animation beim Screen-Eintritt sichtbar
- [ ] Story-Row ist horizontal scrollbar

### 2. Entdecken-Screen
- [ ] Tab "Entdecken" in der Bottom-Bar antippen
- [ ] TopAppBar zeigt "Entdecken"
- [ ] Suchleiste mit Lupe-Icon und Platzhaltertext ist sichtbar
- [ ] Suchleiste akzeptiert Texteingabe
- [ ] Kategorie-Chips werden angezeigt: Trending, Events, Leute, Orte, Clubs
- [ ] Chip-Auswahl funktioniert (Farbe wechselt auf Primary-Lila)
- [ ] 2-Spalten Grid mit Discovery-Karten sichtbar
- [ ] Grid-Karten haben Gradient-Overlay (Lila-Ton)
- [ ] Kategorie-Badge oben rechts auf jeder Karte
- [ ] Grid ist vertikal scrollbar

### 3. Karte-Screen
- [ ] Tab "Karte" in der Bottom-Bar antippen
- [ ] Karten-Platzhalter mit dunklem Hintergrund und gestricheltem Raster
- [ ] Lila Standort-Punkte auf der Karte sichtbar
- [ ] Zentraler Standort-Marker (LocationOn-Icon in Lila)
- [ ] Filter-Chips oben: Alle, Clubs, Bars, Events, Restaurants
- [ ] Chips sind scrollbar und selektierbar
- [ ] Location-FAB rechts ("Mein Standort")
- [ ] Bottom-Sheet-Peek: "In deiner Nähe" Titel
- [ ] Venue-Karten: Club Luna, Skybar, Warehouse 23, Sunset Lounge
- [ ] Jede Venue-Karte zeigt: Name, Kategorie, Entfernung, Stern-Bewertung
- [ ] Bottom-Sheet ist scrollbar

### 4. Aktivität-Screen
- [ ] Tab "Aktivität" in der Bottom-Bar antippen
- [ ] TopAppBar zeigt "Aktivität"
- [ ] "Alle gelesen" Icon-Button (✓✓) in der TopBar sichtbar
- [ ] Filter-Tabs: Alle, Likes, Kommentare, Follows
- [ ] Tab-Wechsel funktioniert und filtert die Liste korrekt
- [ ] Benachrichtigungsliste zeigt alle Mock-Einträge
- [ ] Ungelesene Einträge: Lila Punkt-Indikator links + leicht hervorgehobener Hintergrund
- [ ] Gelesene Einträge: Kein Punkt, normaler Hintergrund
- [ ] Klick auf "Alle gelesen" entfernt den Button und alle Punkte
- [ ] Klick auf eine Benachrichtigung markiert sie als gelesen

### 5. Profil-Screen
- [ ] Tab "Profil" in der Bottom-Bar antippen
- [ ] TopAppBar zeigt "Profil" + Zahnrad-Icon (Einstellungen)
- [ ] Großer Avatar mit Lila-Fuchsia Gradient-Rahmen
- [ ] Avatar zeigt Initialen "MA" (Max Mustermann)
- [ ] Name: "Max Mustermann"
- [ ] Handle: "@maxmuster"
- [ ] Bio-Text sichtbar
- [ ] Standort mit Pin-Icon: "München, Deutschland"
- [ ] Statistiken-Row: 127 Beiträge, 1.8k Follower, 394 Following
- [ ] "Profil bearbeiten" Outlined-Button in voller Breite
- [ ] Tabs: Beiträge, Events, Über mich
- [ ] Tab "Beiträge": 3×3 Grid mit Gradient-Platzhaltern
- [ ] Tab "Events": Event-Karten (Techno Night, Rooftop Party, After Work)
- [ ] Tab "Über mich": Text-Beschreibung + Interessen-Chips (🎵 Musik, 🌙 Nightlife, 📸 Fotografie)

### 6. Navigation & Transitions
- [ ] Alle 5 Tabs sind in der Bottom-Bar sichtbar
- [ ] Bottom-Bar hat Gradient-Akzentlinie oben
- [ ] Aktiver Tab: Icon gefüllt + Lila gefärbt + Label fett
- [ ] Inaktiver Tab: Icon outlined + grau
- [ ] Spring-Animation beim Tab-Wechsel (Icon-Skalierung)
- [ ] Slide + Fade Transitions zwischen den Screens
- [ ] Kein Crash bei schnellem Tab-Wechsel

### 7. Theme-Validierung
- [ ] Hintergrund ist sehr dunkel (fast schwarz, #0F0B15)
- [ ] Oberflächen (Cards, Bottom-Bar) sind dunkel-lila (#1A1523)
- [ ] Primärfarbe ist Lila (#BB86FC in Dark Mode)
- [ ] Text ist hell auf dunklem Hintergrund (High Contrast)
- [ ] Kein weißer Flash beim App-Start (Edge-to-Edge konfiguriert)
- [ ] Status-Bar ist dunkel gefärbt

---

## 🤖 Automatisierte Tests

### ScreenScaffoldingTest (12 Tests)

| Test | Prüft |
|------|-------|
| `homeScreen_displaysScaffoldingElements` | TopBar, Feed-Karten, FAB, Action-Icons |
| `homeScreen_displaysStoryRow` | Story-Avatare (Anna, Max, Lisa) |
| `exploreScreen_displaysScaffoldingElements` | TopBar, Suche, Chips, Grid |
| `exploreScreen_categoryChipsAreClickable` | Chip-Interaktion |
| `mapScreen_displaysScaffoldingElements` | Marker, FAB, Chips, Bottom-Sheet, Venues |
| `notificationsScreen_displaysScaffoldingElements` | TopBar, Tabs, Benachrichtigungen, Alle-gelesen |
| `notificationsScreen_tabFilterIsInteractive` | Tab-Filter auf "Follows" |
| `notificationsScreen_markAllReadWorks` | "Alle gelesen" entfernt Button |
| `profileScreen_displaysScaffoldingElements` | Avatar, Name, Stats, Bearbeiten-Button |
| `profileScreen_tabNavigationWorks` | Tab-Wechsel zu Events & Über mich |
| `fullNavigationLoop_allScreensReachable` | Alle 5 Tabs navigierbar + zurück |

### NavigationFlowTest (1 Test)
| Test | Prüft |
|------|-------|
| `verifyBottomNavigationFlow` | Vollständiger Tab-Loop mit Content-Verification |

### MainActivityTest (1 Test)
| Test | Prüft |
|------|-------|
| `appStartsAndDisplaysHomeScreen` | App-Start zeigt "Kliq" |
