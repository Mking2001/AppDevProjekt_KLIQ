# Test-Szenario: Kapitel 8.8 - Barrierefreiheits-Checks & Accessibility Automatisierung

Dieses Dokument beschreibt das vollständige Test-Szenario zur Verifikation der Barrierefreiheit (Accessibility) für die native Mobile App **Kliq** (Android/Kotlin) im Emulator/Simulator gemäß den Vorgaben in Kapitel 8.8 ("Barrierefreiheits-Checks: Kontrast/Größe").

---

## 1. Test-Übersicht & Ziele

| Test-Aspekt | Test-Methode | Zielvorgabe |
| :--- | :--- | :--- |
| **1. Schriftskalierung (Systemwide Font Scaling)** | Simulation von `fontScale = 1.5f` und `2.0f` | **5 Haupt-Screens** (Map, Social Discovery, Chat, Profil, Analytics) skalieren flüssig ohne Textabriss, Overlap oder Visual Clipping. |
| **2. Kontrast-Check (Dark-Mode & High-Contrast)** | Automatisiertes Audit via `AccessibilityUtils` & Compose | WCAG AA Konformität: **>= 4.5:1** für Fließtext/Titel, **>= 3:1** für UI-Borders/Icons, **>= 7:1** im High-Contrast Mode. |
| **3. Screenreader-Fokus-Traversierung** | TalkBack Focus-Sequence Verification | Logische Auslesereihenfolge von oben nach unten auf **Profil-** und **Chat-Screens** (inkl. Headings & `mergeDescendants`). |
| **4. Touch-Target & Label-Audit** | Interaktives UI-Komponenten-Screening | Alle interaktiven Buttons halten Mindestgröße **>= 48dp × 48dp** ein. Keine fehlenden `contentDescription` / `stateDescription` Eintragsfehler. |

---

## 2. Test-Szenarien & Testfälle

### Szenario 1: Systemweiter Schriftgrößen-Test (5 Haupt-Screens)
- **Map Screen**: Map Filter Segmented Control, Category Chips und Venue Bottom Sheet bei `fontScale = 2.0f`.
- **Social Discovery Screen**: Suchleiste (`ClubSearchBar`), Filter Badges und Suchergebnisliste.
- **Chat Screen**: Chat-Liste overview und 1-zu-1 `PrivateChatScreen` Sprechblasen.
- **Profil Screen**: Profil-Header, Avatar-Initialen, QR-Pass Bottom Sheet (`ProfileQrCodeBottomSheet`) und Ratings.
- **Analytics Screen**: Live Visitor Stats Card (`LiveVisitorStatsCard`) und Gender Aggregation Blocks.
- **Erwartetes Ergebnis**: Alle Texte bleiben vollständig lesbar, brechen korrekt um und werden nicht durch Container-Ränder abgeschnitten.

### Szenario 2: WCAG AA Farbkontrast-Check im Lila-Dark-Mode
- **Fließtext auf Hintergründen**: `DarkOnBackground` (`#F5F3FF`) auf `DarkBackground` (`#0C0914`) -> Kontrast ratio > 18:1 (Pass).
- **Akzent-Farben & Buttons**: `PurplePrimary` (`#8B5CF6`) und `PurplePrimaryLight` (`#C084FC`) auf `DarkSurface` (`#161124`) -> Kontrast ratio > 4.5:1 (Pass).
- **UI-Borders & Outlines**: `DarkOutline` (`#8B7BB0`) auf Dark Background -> Kontrast ratio > 3:1 (Pass).

### Szenario 3: Screenreader-Fokus-Traversierung (Profil & Chat)
- **Profil-Screen Traversierung**:
  1. Top-Bar Navigations-Icon ("Zurück")
  2. Profil-Titel ("Profil") -> Marked as `accessibilityHeading()`
  3. Profil-Avatar & Initialen ("Profilbild von Lisa W.")
  4. Display-Name ("Lisa W.") -> Marked as `accessibilityHeading()`
  5. Bio / Statusmeldung
  6. Aktions-Buttons ("Kliq QR-Pass", "Nachricht senden") -> Minimum 48dp Target Size.
- **Chat-Screen Traversierung**:
  1. Chat Top-Bar ("Gesprächspartner", E2E-Verschlüsselungs-Badge)
  2. Chat-Verlauf chronologisch (Merged Sprechblasen: Absender + Text + Uhrzeit)
  3. Chat-Eingabeleiste (Anhang-Button, Textfeld, Senden/Aufnahme-Button).

### Szenario 4: Touch-Target-Größen & Label-Audit Report
- Automatisiertes Screening der Komponenten:
  - `IconButton` in TopBar: `48dp × 48dp` (Pass)
  - Flash-Button im QRScannerScreen: `48dp × 48dp` (Pass)
  - Bottom-Bar Tabs: `48dp × 48dp` mit `Role.Tab` & `stateDescription` (Pass)
  - Chat Input Buttons: `48dp × 48dp` (Pass)
  - Map Location FAB: `56dp × 56dp` (Pass)

---

## 3. Test-Ausführung & Befehle

Die Verifikation wird über das PowerShell-Testskript ausgeführt:
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\test_accessibility_checks_8.8.ps1
```
