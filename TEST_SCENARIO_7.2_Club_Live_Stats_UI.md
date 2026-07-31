# QA Test Script & Szenario: Kapitel 7.2 - Live-Besucherstatistik pro Club

Dieses Dokument beschreibt das schrittweise Test-Szenario für die Kliq Android-App zur Verifikation der Funktionalität **„UI-Anzeige der Live-Besucherstatistik pro Club“** (Kapitel 7.2) direkt im Android-Emulator sowie durch automatisierte Tests.

---

## 🛠️ Test-Voraussetzungen
- **Gerät / Emulator**: Android Emulator (API Level 33 oder 34, x86_64 Image).
- **App-Status**: Kompiliert aus Branch `feature/club-live-stats-ui`.
- **Design-System**: Kliq High-Contrast Lila Dark-Mode (`DarkSurface` `#1A1523`).

---

## 🧪 Test-Szenario: Schritt-für-Schritt Durchführung

### Schritt 1: Start & Navigation zur Club-Detailansicht
1. Starte die Kliq App im Android Emulator (`.\gradlew.bat installDebug`).
2. Navigiere auf der Karte oder der Club-Übersichtsliste zu einem bestimmten Club (z. B. **"Berghain / Panorama Bar"** oder **"Watergate"**).
3. Tippe auf die Club-Karte, um die `ClubDetailScreen` zu öffnen.
4. **Erwartetes Ergebnis**:
   - Die `ClubDetailScreen` lädt verzögerungsfrei.
   - Die UI-Komponente `LiveVisitorStatsCard` erscheint prominent im oberen Inhaltsbereich.
   - Der rote **`• LIVE`** Indicator-Badge oben rechts am Card-Header pulsiert in einer flüssigen 900ms Alpha-Animation (`0.3f` bis `1.0f`).

---

### Schritt 2: Visuelle Prüfung im Kliq Lila / Dark-Mode Design
1. Überprüfe die visuelle Gestaltung der `LiveVisitorStatsCard` auf Einhaltung der Kliq High-Contrast Design-Richtlinien:
   - **Karten-Container**: Abgerundete Ecken (20.dp), dunkler Hintergrund (`#1A1523`) mit dezentem lila-fuchsia Farbverlaufs-Rahmen.
   - **Auslastungs-Prozentwert**: Große fette Typografie (z. B. **85%**) in der Farbe der aktuellen Kategorie.
   - **Gästezähler**: Klare Textanzeige im Format `"1.420 / 1.500 Gäste vor Ort"`.
   - **Kategorie-Badge Pill**: Highlight-Statusfeld in den definierten Kliq Akzentfarben:
     - `SCHWACH` (<40% Auslastung) -> Cyan/Teal (`#14B8A6`)
     - `MITTEL` (40%-75% Auslastung) -> Kliq Purple (`#7C3AED`)
     - `VOLL` (>75% Auslastung) -> Neon Fuchsia (`#D946EF`)
   - **Progress Gauge / Fortschrittsbalken**: Abgerundeter Fortschrittsbalken mit flüssigem Farbverlauf.
   - **Hinweis-Text**: Erläuternde Statuszeile (z. B. *"Hohe Auslastung, Einlassverzögerungen möglich"*).
   - **Trend-Anzeige**: Trend-Chip mit Rich Text (*"Trend: Steigend ▲"*).
   - **Geschlechter-Aufteilung**: Integrierter Balken zur Darstellung des Geschlechterverhältnisses (*"55% M", "45% W"*).
2. **Erwartetes Ergebnis**:
   - Das Design ist visuell ansprechend, bietet hervorragende Lesbarkeit im Dark Mode (WCAG AA konform) und zeigt keinerlei UI-Artefakte oder Textabschneidungen.

---

### Schritt 3: Dynamische Daten-Updates & Reaktivität prüfen
1. Simuliere im `ClubAnalyticsViewModel` / Mock-Repository dynamische Auslastungsänderungen über Zeitstempel:
   - **Test 3.1**: Besucherzahl von 120 auf 280 ändern (Auslastung steigt von 8% auf 19% -> Kategorie bleibt `SCHWACH`).
   - **Test 3.2**: Besucherzahl auf 975 erhöhen (Auslastung 65% -> Kategorie wechselt auf `MITTEL`).
   - **Test 3.3**: Besucherzahl auf 1.350 erhöhen (Auslastung 89% -> Kategorie wechselt auf `VOLL`).
2. **Erwartetes Ergebnis**:
   - Die Auslastungsleiste animiert ohne UI-Flackern flüssig (`animateFloatAsState`) auf die neuen Prozentwerte.
   - Die Status-Pill wechselt nahtlos Farbe und Bezeichnung (`SCHWACH` Teal -> `MITTEL` Purple -> `VOLL` Fuchsia).
   - Alle Metriken aktualisieren sich sofort über den Kotlin `StateFlow`-Datenstrom.

---

### Schritt 4: Edge Cases (Grenzfälle & Fehlerzustände)
1. **Grenzfall 0 Besucher (0% Auslastung)**:
   - Setze `totalLiveVisitors = 0` und `currentCapacityPercent = 0`.
   - **Erwartetes Ergebnis**: Fortschrittsbalken bleibt auf 0%, Kategorie `SCHWACH` (Teal), Text anzeige `"0 / 1.500 Gäste"`. Keine `ArithmeticException` (Division durch 0).
2. **Grenzfall 100% Voll ausgelastet**:
   - Setze `totalLiveVisitors = 1500` und `currentCapacityPercent = 100`.
   - **Erwartetes Ergebnis**: Fortschrittsbalken zu 100% gefüllt, Kategorie `VOLL` in leuchtendem Fuchsia (`#D946EF`), Textanzeige `"1.500 / 1.500 Gäste"`.
3. **Ladezustand (Loading State)**:
   - Triggere Abruf mit `isLoading = true`.
   - **Erwartetes Ergebnis**: Lade-Indikator (`CircularProgressIndicator`) wird zentriert in Kliq Lila angezeigt.
4. **Fehlerzustand der Datenverbindung**:
   - Simuliere einen Verbindungsfehler (`errorMessage = "Fehler beim Laden der Live-Statistiken"`).
   - **Erwartetes Ergebnis**: Snackbar wird am unteren Bildschirmrand eingeblendet und verschwindet nach Ablauf des Timeouts selbstständig.

---

## 📋 Verifikations-Ergebnis
- [x] **Schritt 1 (Start & Navigation)**: PASSED
- [x] **Schritt 2 (Visuelle Prüfung Dark Mode)**: PASSED
- [x] **Schritt 3 (Dynamische Reaktivität & Updates)**: PASSED
- [x] **Schritt 4 (Edge Cases & Fehlerbehandlung)**: PASSED
