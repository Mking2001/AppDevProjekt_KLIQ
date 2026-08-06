# Pull Request: Dark-Mode-Optimierung für die Nacht-Nutzung (Kapitel 8.7)

## 📌 Beschreibung
Dieser Pull Request implementiert die **Dark-Mode-Optimierung für die Nacht-Nutzung (Kapitel 8.7)** in der Kliq Android App. Das globale Farbschema wurde auf das Kliq-Markenzeichen – ein augenschonendes, kontrastreiches High-Contrast-Lila/Dark-Design – optimiert. Zudem wurden System-Bar-Glitches und weiße Lichtblitze bei Screen-Transitionen unterbunden sowie die ViewModel-Zustandserhaltung bei Theme-Wechseln abgesichert.

---

## 🚀 Wichtigste Änderungen

### 1. Farb-Tokens & Anti-Flash System Bars (`Color.kt`, `Theme.kt`)
- **Farb-Palette**: `DarkBackground` (`#0C0914`), `DarkSurface` (`#161124`), `DarkSurfaceVariant` (`#241C38`), `DarkOnBackground` (`#F5F3FF`) für maximale Kontrastwirkung bei Nacht.
- **Flackerfreie Übergänge**: Synchronisation von `statusBarColor`, `navigationBarColor` und `window.decorView.setBackgroundColor` in `KliqTheme`.

### 2. MVVM & State Preservation (`ThemeViewModel.kt`)
- Erhaltung des `ThemeMode` und `isNightOptimized`-States via `StateFlow`.
- Dynamische Anpassung ohne Activity-Recreation oder Datenverlust.

### 3. Screen-Konsistenz & Tests
- Anpassung von `PrivateChatScreen.kt`, `ChatComponents.kt` und `MapScreen.kt` auf dynamische Theme-Tokens.
- Neue Unit-Tests in `ThemeViewModelTest.kt` und automatisiertes Emulator-UI-Testskript `DarkModeOptimizationEmulatorTest.kt`.

---

## ✅ GitHub PR Checkliste

- [x] **Branch-Konformität**: Neuer Feature-Branch `feature/dark-mode-optimization` basiert auf aktuellem `main`.
- [x] **MVVM-Architektur**: Theme-Zustand wird in `ThemeViewModel` gekapselt und via `StateFlow` reaktiv publiziert.
- [x] **Zero Hardcoded Colors**: UI-Komponenten nutzen ausschließlich zentrale Tokens aus `MaterialTheme.colorScheme`.
- [x] **Accessibility & Kontrast**: Alle Text- und Surface-Kontraste entsprechen WCAG 2.1 AA/AAA Standards (z. B. Text auf Hintergrund 19.1:1).
- [x] **Anti-Flash Verification**: Status- und Navigation-Bars bleiben während Screen-Wechseln flackerfrei dunkel.
- [x] **Automatisierte Testabdeckung**: Unit-Tests (`ThemeViewModelTest`) & Instrumentierte Emulator-Tests (`DarkModeOptimizationEmulatorTest`) erfolgreich bestanden.
- [x] **Dokumentation**: `TEST_SCENARIO_8.7_Dark_Mode_Optimization.md` und `CODE_REVIEW_8.7_Dark_Mode_Optimization.md` hinzugefügt.
