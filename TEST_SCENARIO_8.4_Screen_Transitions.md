# Test-Szenario: Kapitel 8.4 - Komplexe UI-Animationen für Screen-Übergänge

## 🎯 Zielsetzung
Validierung der flüssigen, ruckelfreien und optisch ansprechenden Screen-Übergangsanimationen in der Kliq-App gemäß MVVM-Architektur und Kliq Dark/Lila-Design-System.

---

## 🧪 Testfälle

### Testfall 1: Horizontaler Tab-Wechsel (Map, Chat, Profil, Explore)
- **Schritte**:
  1. App starten und vom Home-Screen auf "Karte" (Map) tippen.
  2. Von "Karte" auf "Entdecken" (Explore) und danach auf "Profil" tippen.
- **Erwartetes Verhalten**:
  - Flüssige Richtungs-sensitive Slide- & Fade-Animation ($300\text{ms}$, `CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)`).
  - Keinerlei Ruckler (60 FPS / 120 FPS auf High-Refresh-Rate-Displays).

### Testfall 2: Shared Element Card Zoom (Map / Explore -> Club Analytics / Details)
- **Schritte**:
  1. Auf der Karte oder im Explore-Screen eine Club-Karte / Marker auswählen.
  2. Auf den Club tippen, um in den Club-Analytics / Detail-Screen zu navigieren.
  3. Den Zurück-Button antippen.
- **Erwartetes Verhalten**:
  - Smooth Elevation Zoom-In Animation mit `scaleIn(0.90f)` + `slideInVertically` ($380\text{ms}$).
  - Beim Schließen sanftes Zoom-Out und vertikales Heruntergleiten zur ursprünglichen Kartenansicht.

### Testfall 3: Detail-Push & Pop (Chat Overview -> Chat Detail / Profil)
- **Schritte**:
  1. Chat-Übersicht öffnen und eine Konversation antippen.
  2. Aus dem Chat-Detail heraus zurück navigieren.
- **Erwartetes Verhalten**:
  - Rechter Parallax Push-Slide mit subtiler $0.96x$ Skalierung ($320\text{ms}$).
  - Beim Verlassen flüssige Pop-Exit-Animation nach rechts.

### Testfall 4: Modal Slide-Up (Profil -> QR-Scanner)
- **Schritte**:
  1. Im Profil den QR-Scanner öffnen.
  2. Den Scanner per Zurück-Geste oder X-Button schließen.
- **Erwartetes Verhalten**:
  - Vertikaler Slide-Up von unten ($350\text{ms}$) mit transparentem Dark-Dim Overlay.
  - Schließen lässt das Modal flüssig nach unten gleiten.

---

## 📊 Performance-Prüfung
- **GPU Rendering**: Keinerlei Re-Composition-Overhead während der Animations-Frames dank `.graphicsLayer` und vorkonfigurierter Transitions.
- **Farbtreue**: Keine weißen Blitze oder Hintergrund-Artefakte; durchgängig Kliq Dark Surface (`#0F0B15`).
