# QA Test Script: Kapitel 6.8 - Who's Online Anzeige in Gruppenchats

Dieses Test-Skript beschreibt das schrittweise Test-Szenario für die Kliq Android-App zur Verifikation der Funktionalität **„Who's Online Anzeige in Gruppenchats“** direkt im Android-Emulator.

---

## Test-Voraussetzungen
- **Gerät / Emulator**: Android Emulator (API Level 33 oder 34) mit x86_64 Image.
- **App-Status**: Frisch kompiliert aus Branch `feature/chat-online-presence`.
- **Design-Modus**: Kliq High-Contrast Dark-Mode aktiviert.

---

## Test-Szenario: Schritt-für-Schritt Durchführung

### Schritt 1: Beitritt zu einem öffentlichen Stadt-Chat simulieren
1. Starte die Kliq App im Android Emulator.
2. Navigiere zum Bereich **Chats** -> **Öffentliche Stadt-Chats**.
3. Wähle den Stadt-Chat **"Berlin - Tonight"** aus.
4. **Erwartetes Ergebnis**:
   - Die `ChatDetailScreen` öffnet sich ohne Verzögerung.
   - Im Chat-Header erscheint der Gruppentitel **"Berlin - Tonight"** zusammen mit der Live-Online-Anzeige `"🟢 248 online • Tippen für Teilnehmer"`.
   - Das leuchtende Präsenz-Badge am Header pulse-animiert sanft in Neon-Grün (`#22C55E`).

### Schritt 2: Mock-Präsenzdaten für Test-Nutzer verifizieren
1. Tippe auf den Chat-Header oder das Gruppen-Icon oben rechts, um die ausklappbare Teilnehmerliste (`GroupPresenceParticipantSheet`) zu öffnen.
2. Überprüfe die aufgelisteten Test-Nutzer in der `LazyColumn`:
   - **Elena M.** -> Status: `ONLINE` (Glow-Badge Grün `#22C55E`), Rolle: `HOST` (Lila Badge `#8B5CF6`).
   - **Lukas K.** -> Status: `ONLINE` (Glow-Badge Grün), Rolle: `MOD` (Blaues Badge `#3B82F6`).
   - **Sophie W.** -> Status: `ONLINE` (Glow-Badge Grün), Rolle: `VIP` (Gelbes Badge `#F59E0B`).
   - **Maximilian B.** -> Status: `AWAY` (Bernstein-Orange `#F59E0B`), Rolle: `MEMBER`.
   - **Mia R.** -> Status: `OFFLINE` (Grau `#6B7280`), Rolle: `MEMBER`.
3. **Erwartetes Ergebnis**:
   - Alle Test-Nutzer werden mit korrekten Avataren, Initialen, Rollen-Chips, Entfernungen ("0,4 km entfernt") und Statusmeldungen dargestellt.
   - Der Header der Teilnehmerliste zeigt präzise `"248 von 1420 Mitgliedern online"`.

### Schritt 3: Dynamische Reaktivität von Header und Badges prüfen
1. Ändere den eigenen Präsenzstatus in der oberen Leiste der Teilnehmerliste von **Online** auf **Abwesend**.
2. **Erwartetes Ergebnis**:
   - Das eigene Status-Chip hebt sich mit lila Umrandung ab.
   - Das Präsenz-Badge am eigenen Nutzer-Avatar wechselt sofort von leuchtend grün auf bernstein-orange (`#F59E0B`).
3. Ändere den Status auf **Invisible** (Offline).
4. **Erwartetes Ergebnis**:
   - Das Badge wechselt verzögerungsfrei auf grau (`#6B7280`).
   - Die Gesamtzahl der aktiven Online-Nutzer im Header aktualisiert sich dynamisch im `GroupPresenceViewModel` per Kotlin `StateFlow`.
5. Gib im Suchfeld der Teilnehmerliste den Namen `"Sophie"` ein.
6. **Erwartetes Ergebnis**:
   - Die Liste filtert in Echtzeit ohne Ruckeln auf den Eintrag **"Sophie W."**.

### Schritt 4: UI Dark-Mode Rendering, Barrierefreiheit & Performance
1. **Design-Prüfung**:
   - Hintergrundfarben entsprechen Kliq `DarkSurface` (`#181326`) und `DarkSurfaceVariant` (`#2B253F`).
   - Textkontrast (Weiß auf dunklem Grund, `PurplePrimaryLight` `#A78BFA` für Akzente) ist gemäß WCAG AA Standards gut lesbar.
2. **Barrierefreiheit (Accessibility)**:
   - Alle Buttons (`IconButton`, `ContentDescription`) besitzen funktionale Screenreader-Labels ("Zurück", "Teilnehmer anzeigen", "Schließen").
   - Status-Badges sind über semantische Textausgaben ("Online", "Abwesend", "Invisible") barrierefrei erfassbar.
3. **Performance & Re-Composition**:
   - Scrollen durch die Teilnehmerliste läuft flüssig mit 60/120 FPS.
   - Keine unnötigen Re-Compositions beim Tippen im Suchfeld dank `StateFlow`-Filterung.

---

## Verifikations-Ergebnis
- [x] **Schritt 1 (Beitritt Stadt-Chat)**: ERFOLGREICH
- [x] **Schritt 2 (Mock-Präsenzdaten)**: ERFOLGREICH
- [x] **Schritt 3 (Dynamische Reaktivität)**: ERFOLGREICH
- [x] **Schritt 4 (UI Dark-Mode & Performance)**: ERFOLGREICH
