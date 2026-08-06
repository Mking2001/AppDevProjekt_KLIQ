# QA Test-Szenario & UI-Test-Skript: Kapitel 8.3 - Barrierefreiheits-Checks & Accessibility

Dieses Dokument beschreibt das vollständige Test-Szenario sowie die Schritt-für-Schritt Anleitung zur manuellen und automatisierten Überprüfung der **„Barrierefreiheits-Checks (Accessibility)“** (Kapitel 8.3) im Android-Emulator.

---

## 🛠️ 1. Test-Voraussetzungen

- **Gerät / Emulator**: Android Emulator (API Level 33 oder 34, x86_64 Image).
- **Branch**: `feature/accessibility-checks`.
- **Design-System**: Kliq High-Contrast Dark Mode (`HighContrastBackground` `#000000`, Kliq Lila `#C084FC`, Contrast Text `#FFFFFF`).
- **Test-Komponenten**:
  - `AccessibilityUtils.kt` (`com.kliq.app.util.AccessibilityUtils`)
  - `AccessibilityModifiers.kt` (`com.kliq.app.util.AccessibilityModifiers`)
  - `AccessibilityViewModel.kt` (`com.kliq.app.viewmodel.AccessibilityViewModel`)
  - `InteractiveStarRating.kt` (`com.kliq.app.ui.components.InteractiveStarRating`)
  - `UserRatingStarBar.kt` (`com.kliq.app.ui.components.UserRatingStarBar`)
  - `UserQuickViewCard.kt` (`com.kliq.app.ui.components.UserQuickViewCard`)
  - `ProfileAvatarImage.kt` (`com.kliq.app.ui.components.ProfileAvatarImage`)
  - `ChatComponents.kt` (`com.kliq.app.ui.components.ChatComponents`)
  - `MapFilterSegmentedControl.kt` (`com.kliq.app.ui.components.MapFilterSegmentedControl`)
  - `AccessibilityChecksEmulatorTest.kt` (`com.kliq.app.ui.AccessibilityChecksEmulatorTest`)

---

## 🧪 2. Schritt-für-Schritt Test-Szenario

### Schritt 1: Überprüfung der Accessibility-Attribute & TalkBack-Semantik
1. Aktiviere TalkBack auf dem Android-Emulator oder führe den automatisierten UI-Test aus:
   ```powershell
   .\test_accessibility_checks.ps1
   ```
2. Navigiere zu den zentralen UI-Komponenten (Rating-Sterne, Chat-Sprechblasen, Profile-Cards, Map-Controls).
3. **Erwartetes Ergebnis**:
   - Die Rating-Sterne geben ein konsolidiertes Label aus: *"Interaktive Bewertung: X von 5 Sternen. Wischen oder tippen zum Ändern."*
   - Chat-Sprechblasen fassen Absender, Nachrichtentext bzw. Sprachnachrichten-Dauer in einem einzelnen Fokus-Element zusammen.
   - Map-Controls besitzen explizite Tab-Rollen und den Status *"Ausgewählt"* / *"Nicht ausgewählt"*.
   - Alle Schaltflächen weisen eine Touch-Mindestfläche von 48dp × 48dp auf.

---

### Schritt 2: Skalierung der Schriftgrößen (Dynamic Type / Font Scale 1.5x & 2.0x)
1. Ändere die System-Schriftgröße im Emulator auf **1.5x** und **2.0x** (`Einstellungen -> Barrierefreiheit -> Textgröße`).
2. Öffne die Detailansicht und die Kartenelemente.
3. **Erwartetes Ergebnis**:
   - Texte werden skaliert dargestellt, ohne dass Layouts brechen, überlappen oder unleserlich abgeschnitten werden.
   - Container passen ihre Höhe dynamisch an oder bieten vertikales Scrollen.

---

### Schritt 3: Simulation des TalkBack-Fokus & Überschriften-Navigation
1. Simuliere das Wischen durch Elemente im TalkBack-Modus auf der Map und in der Profilansicht.
2. **Erwartetes Ergebnis**:
   - Die Fokus-Reihenfolge verläuft von oben nach unten, von links nach rechts.
   - Überschriften werden durch `accessibilityHeading()` als Navigationspunkte erkannt.

---

## 🚀 3. Ausführung der automatisierten Tests

Führe das Test-Script direkt im Terminal aus:

```powershell
powershell -ExecutionPolicy Bypass -File .\test_accessibility_checks.ps1
```
