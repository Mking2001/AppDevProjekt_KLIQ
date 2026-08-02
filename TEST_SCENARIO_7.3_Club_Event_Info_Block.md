# QA Test Script & Szenario: Kapitel 7.3 - Info-Block für spezielle Events und Angebote

Dieses Dokument beschreibt das schrittweise Test-Szenario für die Kliq Android-App zur Verifikation der Funktionalität **„Info-Block für spezielle Events und Angebote in der Club-Detailansicht“** (Kapitel 7.3) im Android-Emulator sowie durch automatisierte Testläufe.

---

## 🛠️ Test-Voraussetzungen
- **Gerät / Emulator**: Android Emulator (API Level 33 oder 34, x86_64 Image).
- **App-Status**: Kompiliert aus Branch `feature/club-event-info-block`.
- **Design-System**: Kliq High-Contrast Lila Dark-Mode (`DarkSurface` `#1A1523`, `DarkSurfaceVariant` `#2D2640`, `PurplePrimary` `#7C3AED`, `FuchsiaTertiary` `#D946EF`).

---

## 🧪 Test-Szenario: Schritt-für-Schritt Durchführung

### Schritt 1: Start & Navigation zur Club-Detailansicht
1. Starte die Kliq App im Android Emulator (`.\gradlew.bat installDebug`).
2. Navigiere über die Karte oder die Suchleiste zu einem Club-Profil (z. B. **"Berghain / Panorama Bar"** oder **"Watergate"**).
3. Tippe auf die Club-Karte, um die `ClubDetailScreen` zu öffnen.
4. **Erwartetes Ergebnis**:
   - Die `ClubDetailScreen` lädt verzögerungsfrei.
   - Der neue `ClubEventOfferInfoBlock` ("Events & Specials") wird unterhalb der Live-Besucherstatistik sichtbar angezeigt.

---

### Schritt 2: Visuelle Prüfung im Kliq Lila / Dark-Mode Design
1. Überprüfe das visuelle Erscheinungsbild des `ClubEventOfferInfoBlock`:
   - **Karten-Container**: Abgerundete Ecken (20.dp), dunkler Hintergrund (`#1A1523`) mit lila-fuchsia Farbverlaufs-Rahmen (`#7C3AED` -> `#D946EF`).
   - **Header & Icon**: Kreisförmiges Icon mit Lofi-Farbverlauf und der Aufschrift *"Events & Specials"*.
   - **Tab-Navigation**: Integrierte Tabs (*"Specials & Deals"*, *"Partys & Events"*) mit lila Indikatorleiste und hoher Kontrastlesbarkeit im Dark Mode.
   - **Badges & Chips**:
     - `Special Deal` / `Getränke-Special` -> Cyan/Teal (`#14B8A6`) / Kliq Lila (`#7C3AED`).
     - `VIP Aktion` -> Neon Fuchsia (`#D946EF`) mit VIP-Marker.
     - `Eintrittsrabatt` -> Amber Gold (`#F59E0B`).
2. **Erwartetes Ergebnis**:
   - Hervorragende Lesbarkeit im Dark Mode (WCAG AA konform).
   - Keine Textüberschneidungen oder Grafikfehler.

---

### Schritt 3: Interaktions-Test & Modals
1. **Ausklappbare Event-Details**:
   - Wechsle auf den Tab *"Partys & Events"*.
   - Tippe auf ein Party-Event (z. B. *"Midnight Techno Rave & Visuals"*).
   - **Erwartetes Ergebnis**: Die Karte erweitert sich flüssig via `AnimatedVisibility` und zeigt die vollständige Beschreibung und den Einlasspreis.
2. **Offer Detail Bottom Sheet & Rabatt-Code**:
   - Wechsle auf den Tab *"Specials & Deals"*.
   - Tippe auf das Angebot *"2-for-1 Cocktail Special"* oder *"VIP Fast-Track & Welcome Shot"*.
   - **Erwartetes Ergebnis**:
     - Das `ClubOfferDetailBottomSheet` gleitet von unten sanft ein.
     - Der Rabatt-Code (z. B. `KLIQ2FOR1`) wird in großer fetter lila Typografie in einem hervorgehobenen Container dargestellt.
3. **Rabattcode Kopieren**:
   - Tippe im Modal-View auf die Schaltfläche **"Kopieren"**.
   - **Erwartetes Ergebnis**: Der Code wird in die Zwischenablage übernommen und eine Toast/Snackbar-Meldung (*"Gutscheincode 'KLIQ2FOR1' kopiert!"*) bestätigt die Aktion.

---

### Schritt 4: Edge Cases & Data State (Fehler- & Offline-Verhalten)
1. **Zustand ohne aktive Angebote (Empty State)**:
   - Wähle einen Club ohne angemeldete Events/Offers aus oder setze in der Testumgebung leere Listen ein.
   - **Erwartetes Ergebnis**: Eine saubere, strukturierte Fallback-Meldung (*"Derzeit keine aktiven Specials für diesen Club."*) wird zentriert angezeigt.
2. **Offline-Verhalten (Room Cache)**:
   - Aktiviere im Emulator den Flugmodus (Airplane Mode).
   - Navigiere erneut in die Club-Detailansicht.
   - **Erwartetes Ergebnis**: Alle zuvor geladenen Events und Specials werden zuverlässig aus dem lokalen Room-Datenbankcache (`club_offers` & `events` Tabellen) geladen und angezeigt.
3. **Fehler- & Ladezustand**:
   - Ladeanzeige (`CircularProgressIndicator`) wird während der Initialisierung zentriert in Kliq Lila eingeblendet.

---

## 📋 Verifikations-Ergebnis
- [x] **Schritt 1 (Start & Navigation)**: PASSED
- [x] **Schritt 2 (Visuelle Prüfung Dark Mode)**: PASSED
- [x] **Schritt 3 (Interaktions-Test & Modals)**: PASSED
- [x] **Schritt 4 (Edge Cases & Offline Room Cache)**: PASSED
