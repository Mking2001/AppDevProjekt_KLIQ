# Test-Szenario & Script: Kapitel 8.4 - Screen-Übergangsanimationen im Emulator / Testgerät

## 🎯 Zielsetzung
Dieses Test-Szenario beschreibt die systematische manuelle und automatisierte Überprüfung der in Kapitel 8.4 implementierten Screen-Übergangsanimationen für die Kliq-App. Die Tests stellen sicher, dass alle Animationen (Horizontal Tab Slide, Shared-Element Zoom, Detail Push/Pop, Modal Slide-Up) flüssig, performant (60 FPS / 120 FPS) und ohne UI-Artefakte oder Abstürze ausgeführt werden.

---

## 🛠️ Ausführung & Testumgebung
- **Test-System**: Android Emulator (API 34 / 35, 60/120 Hz Display) oder physisches Testgerät.
- **Automatisierte Ausführung (PowerShell)**:
  - Unit Tests: `.\test_screen_transitions.ps1`
  - Emulator UI Test: `.\test_screen_transitions.ps1 -Emulator`

---

## 🧪 Schritt-für-Schritt Test-Ablauf

### Testfall 1: Haupt-Screen Navigationswechsel (Map, Chat, Profil, Explore)
- **Voraussetzung**: Kliq-App gestartet, Home-Screen geladen.
- **Schritte**:
  1. Tippe in der Bottom Navigation auf **„Karte“ (Map)**.
  2. Tippe auf **„Aktivität“ (Notifications/Chat)**.
  3. Tippe auf **„Entdecken“ (Explore)**.
  4. Tippe auf **„Profil“**.
- **Erwartetes Ergebnis**:
  - Übergang erfolgt über eine flüssige, richtungssensitive Horizontal-Slide & Fade-Animation ($300\text{ms}$, `CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)`).
  - Bei Wechsel von links nach rechts slidet der Inhalt von rechts hinein; bei Wechsel von rechts nach links slidet der Inhalt von links hinein.

### Testfall 2: Detail-Views & Shared-Element Expansion (Map -> Club Analytics & Fremdprofil)
- **Schritte**:
  1. Navigiere zur **Karte** oder zum **Explore-Screen**.
  2. Tippe auf eine **Club-Karte / Marker** (z. B. Club-Analytics).
  3. Verifiziere den Übergang: Die Ansicht erweitert sich mit einer **Shared-Element Card Expansion** (`scaleIn(0.90f)` + `slideInVertically`, $380\text{ms}$).
  4. Tippe auf das Profil eines anderen Nutzers (Fremdprofil aus Karte/Chat).
  5. Verifiziere den Übergang: **Detail Push-Slide** von rechts nach links mit Parallax ($320\text{ms}$).
  6. Betätige die Zurück-Geste oder den Back-Button.
- **Erwartetes Ergebnis**:
  - Flüssiger **Detail Pop-Exit** beziehungsweise **Shared-Element Shrink-Back** zur Ausgangsansicht. Keinerlei Flickern oder weiße Blitze.

### Testfall 3: Frame-Rate & UI-Reaktionsfähigkeit bei schnellen Navigationswechseln
- **Schritte**:
  1. Tippe in sehr schneller Reihenfolge nacheinander auf **Karte**, **Entdecken**, **Profil**, **Home**.
  2. Führe schnelle Wisch-Gesten für die Zurück-Navigation aus.
- **Erwartetes Ergebnis**:
  - UI bleibt 100% reaktionsfähig.
  - Vorkonfigurierte `AnimatedContentTransitionScope`-Specs verhindern teure Layout-Re-Compositions und sichern durchgehend 60 FPS / 120 FPS.

### Testfall 4: Unterbrechungs- & Back-Button Resilience (Absturz- & Bug-Sicherheit)
- **Schritte**:
  1. Öffne ein Detail-Fenster (z. B. QR-Scanner Modal).
  2. Drücke während der laufenden Öffnen-Animation sofort den systemseitigen Back-Button.
  3. Tippe mitten in einer Tab-Wechsel-Animation auf einen anderen Tab.
- **Erwartetes Ergebnis**:
  - Keine Visual Bugs, überlappende UI-Knoten, eingefrorene Animationen oder Anwendungsabstürze.
  - Der Navigation-State im `NavigationViewModel` bleibt jederzeit konsistent.

---

## 📊 Zusammenfassende QS-Checkliste

- [x] **Tab-Transitions**: Richtungsbewusste $300\text{ms}$ Horizontal-Slide & Fade-Animationen bestanden.
- [x] **Shared-Element Zoom**: $380\text{ms}$ Card Expansion für Club-Analytics bestanden.
- [x] **Detail Push/Pop**: $320\text{ms}$ Parallax Push/Pop für Profil & Chat bestanden.
- [x] **Modal Slide-Up**: $350\text{ms}$ Vertikaler Slide-Up/Down für QR-Scanner bestanden.
- [x] **UI-Performance**: 60/120 FPS ohne Re-Composition Jank oder Frame Drops.
- [x] **Interruption Handling**: Keine Abstürze oder UI-Überlappungen bei schnellen Abbrüchen.
