# Pull Request: Feature - Barrierefreiheits-Checks & Accessibility (Kapitel 8.3)

## Summary of Changes
- **Accessibility Utility & Modifier Extensions**:
  - Expanded `AccessibilityUtils.kt` with WCAG 2.1 ratio calculation, WCAG compliance verification (`AAA`, `AA`, `FAIL`), touch target validation (>= 48dp), and dynamic font scaling helper functions.
  - Implemented `AccessibilityModifiers.kt` offering Jetpack Compose extensions: `accessibilityHeading()`, `talkBackDescription()`, `ensureMinTouchTarget()`.
- **High-Contrast Design & Dynamic Font Scaling**:
  - Defined high-contrast color scheme (`HighContrastDarkColorScheme`) with WCAG AAA compliant contrast ratios (>7:1) using pure dark background and vibrant purple accents.
  - Integrated `isHighContrast` mode into `KliqTheme` along with scalable typography.
- **Central UI Components Accessibility**:
  - **Rating Stars**: Added `clearAndSetSemantics`, TalkBack state descriptions, and custom accessibility actions (increment/decrement rating) to `InteractiveStarRating` and `UserRatingStarBar`.
  - **Profile Cards**: Added `accessibilityHeading`, merged status accessibility descriptions, and minimum 48dp touch targets to `UserQuickViewCard` and `ProfileAvatarImage`.
  - **Chat Sprechblasen**: Added merged TalkBack semantics for message type, sender, playback state, and duration in `ChatBubble` and `VoiceMessageBubble`.
  - **Map Controls**: Added tab role semantics, selection state descriptions, and 48dp minimum height to `MapFilterSegmentedControl`.
- **MVVM Architecture**:
  - Created `AccessibilitySettings` model, `AccessibilityRepository` interface, `AccessibilityRepositoryImpl`, and `@HiltViewModel` `AccessibilityViewModel`.
  - Registered dependency injection binding in `RepositoryModule.kt`.

## Automated Unit Tests
- `AccessibilityUtilsTest.kt`: Validates contrast ratio math, WCAG compliance levels, touch target checking, and font scale calculations.
- `AccessibilityRepositoryTest.kt`: Validates preference updates, clamp limits, and resetting defaults.
- `AccessibilityViewModelTest.kt`: Validates UI state emission and user action handlers.

## Branch & Commits
- Branch: `feature/accessibility-checks`
- Commits:
  - `feat: add accessibility contrast and touch target utility functions`
  - `feat: implement accessibility repository data model and viewmodel`
  - `feat: add high-contrast theme color scheme and theme support`
  - `fix: talkback descriptions for rating stars`
  - `feat: add accessibility headings and content descriptions to profile cards`
  - `feat: add screen reader optimizations and semantics to chat bubbles`
  - `feat: optimize map controls for talkback and touch target standards`
  - `test: add unit tests for accessibility utilities repository and viewmodel`
  - `test: add accessibility instrumented emulator ui test and test script`

---

## 📋 GitHub PR Quality & Accessibility Checklist

### Code-Qualität & MVVM-Architektur
- [x] Strikte Trennung nach MVVM-Muster (`AccessibilitySettings`, `AccessibilityRepository`, `AccessibilityViewModel`).
- [x] Unveränderliche Zustandsübermittlung mittels `StateFlow<AccessibilityUiState>`.
- [x] Sauber konfigurierte Dependency Injection via Hilt in `RepositoryModule.kt`.

### Accessibility & Screen-Reader (TalkBack)
- [x] Vollständige `contentDescription` und `stateDescription` an allen zentralen UI-Komponenten.
- [x] Strukturelle Orientierungspunkte mit `accessibilityHeading()` gekennzeichnet.
- [x] Zusammengefasste Sprechblasen-Labels für ungestörte Sprachausgabe (`mergeDescendants = true`).
- [x] Benutzerdefinierte Accessibility Actions (`CustomAccessibilityAction`) für Schritt-Interaktionen.

### Dynamische Schriftgrößen & Layout-Flexibilität
- [x] Dynamic Text Scaling verifiziert für Font Scale 1.5x und 2.0x ohne Layout-Brechung.
- [x] Mindest-Touch-Target-Größen von 48dp × 48dp für alle Buttons und interaktiven Elemente eingehalten.
- [x] High-Contrast Farbschema (`HighContrastDarkColorScheme`) erfüllt WCAG AAA (> 7:1 Kontrastverhältnis).

### Testabdeckung & Verifikation
- [x] Unit-Tests (`AccessibilityUtilsTest`, `AccessibilityRepositoryTest`, `AccessibilityViewModelTest`) bestanden.
- [x] Emulator UI-Test (`AccessibilityChecksEmulatorTest`) und Test-Skript (`test_accessibility_checks.ps1`) erfolgreich verifiziert.

