# Pull Request: Feature - Vollständige Barrierefreiheits-Optimierung (Accessibility Refactoring) (Kapitel 8.8)

## Summary of Changes
- **WCAG AA Farbkontraste & Dark Theme**:
  - Überarbeitung der Farbpalette in `Color.kt` und `Theme.kt` zur Einhaltung der WCAG AA-Normen.
  - Kontrastverhältnisse betragen durchgehend mindestens **4.5:1** für Fließtext/Titel und **3:1** für UI-Borders, Icons und Akzent-Elemente in Dark-Mode und High-Contrast-Mode.
- **Dynamic Font Scaling & Layout-Stabilität**:
  - Erweiterung von `AccessibilityUtils.kt` um Hilfsfunktionen zur Erkennung und adaptiven Skalierung von System-Schriftgrößen (1.5x bis 2.0x).
  - Vermeidung von Text-Clipping, Overlaps und festen Höhenbeschränkungen across all central UI components.
- **TalkBack Semantik, Hints & Content Descriptions**:
  - **Custom Map Marker**: Anreicherung aller User-Marker, Club-Pins und Cluster-Nodes in `MapScreen.kt` mit barrierefreien Sprechtexten und Zustandsbeschreibungen.
  - **QR Code Steuerelemente**: Vollständige Auszeichnung von QR-Pass-Ansichten (`ProfileQrCodeBottomSheet.kt`), Kamera-Scanner-Overlay (`QRScannerScreen.kt`), Blitz-Schalter (`stateDescription`) und Ergebnis-Karten.
  - **Chat-Komponenten & Sprechblasen**: Zusammenfassung von Nachrichtentext, Absender, Verschlüsselungsstatus, Audio-Dauer und Abspielzustand via `semantics(mergeDescendants = true)` in `ChatComponents.kt` und `PrivateChatScreen.kt`.
  - **Top- & Bottom-Bar**: Auszeichnung von Screen-Titeln als Headings (`accessibilityHeading()`) und Bottom-Bar-Items mit `Role.Tab` sowie `stateDescription` ("Ausgewählt" / "Nicht ausgewählt").
- **Touch-Target-Mindestgrößen (>= 48dp)**:
  - Durchsetzung von `ensureMinTouchTarget(48.dp)` an allen IconButtons, Aktions-Buttons, Quick-View-Cards und Navigationselementen.

---

## 📋 Git Branch & Commit-Historie
- **Feature Branch**: `feature/accessibility-refactoring`
- **Atomare Commits**:
  1. `refactor(ui): update color contrast ratios for dark theme`
  2. `feat(accessibility): add dynamic font scaling support`
  3. `feat(accessibility): add talkback labels and semantics to main screens`
  4. `refactor(ui): enforce minimum 48dp touch targets`
  5. `test(accessibility): update accessibility checks and test coverage`

---

## 📋 Qualitäts- & Barrierefreiheits-Checkliste

### Accessibility & Screenreader (TalkBack)
- [x] Vollständige `contentDescription` und `stateDescription` an allen interaktiven Steuerelementen.
- [x] Strukturelle Überschriften mit `accessibilityHeading()` gekennzeichnet.
- [x] Custom Map-Marker besitzen strukturierte Sprechtexte für Screenreader.
- [x] QR-Code Generator & Scanner Buttons besitzen präzise Barrierefreiheits-Labels.
- [x] Chat-Sprechblasen fassen Metadaten barrierefrei zusammen (`mergeDescendants = true`).

### Dynamische Schriftgrößen & Farbkontraste
- [x] Dynamic Font Scaling bis 2.0x ohne Clipping oder UI-Brechungen verifiziert.
- [x] WCAG AA Farbkontrastwerte (>= 4.5:1 Text, >= 3:1 UI-Elemente) garantiert.
- [x] Touch-Target Mindestgröße von 48×48 dp flächendeckend eingehalten.

### MVVM Architecture & Clean Code
- [x] Strikte MVVM-Entkopplung und reaktiver Datenfluss.
- [x] Keine KI-Hinweise, Tags oder Kommentare im gesamten Codebase.

---

## Verifikation & Testergebnisse
- Automated Unit Tests: **BUILD SUCCESSFUL** (`AccessibilityUtilsTest`, `AccessibilityRepositoryTest`, `AccessibilityViewModelTest`).
- Scenario Test Runner Skripte (`run_accessibility_tests.ps1`, `test_accessibility_checks.ps1`): **ERFOLGREICH BESTANDEN**.
