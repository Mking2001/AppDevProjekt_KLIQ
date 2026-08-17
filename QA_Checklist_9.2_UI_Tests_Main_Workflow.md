# QA Checklist: Kapitel 9.2 - UI-Tests für den Haupt-Workflow

## Testumgebung & Vorbedingungen
- [x] Android Studio Sync erfolgreich abgeschlossen
- [x] Gradle Build & Kompilierung des Test-Targets fehlerfrei (`./gradlew assembleDebugAndroidTest`)
- [x] Feature Branch: `feature/ui-tests-main-workflow`

---

## QS-Prüfpunkte

### 1. Onboarding & Login-Flow
- [x] **Phone Login Input**: Eingabe von Telefonnummern wird korrekt im UI verarbeitet.
- [x] **SMS OTP Verifizierung**: Umschaltung zum 6-Digit OTP Eingabefeld und Bestätigungs-Button funktioniert.
- [x] **Profil-Erstellung**: Formularfelder (Name, Alter, Ort, Bio) reagieren korrekt auf Benutzereingaben.
- [x] **Intent-Matching**: Absichts-Karten („Freunde“, „Dating / Liebe“, „Beides“) lassen sich anklicken und visuell hervorheben.
- [x] **Konsumgewohnheiten**: Rauch- und Trinkverhalten-Chips verarbeiten Auswahlzustände fehlerfrei.

### 2. Haupt-Navigation & NavHost
- [x] **Bottom Bar Tabs**: Alle 5 Tabs (Home, Entdecken, Karte, Aktivität, Profil) wechseln zügig und ohne Layout-Verschiebung.
- [x] **Club Detail Navigation**: Klick auf Club-Eintrag öffnet Detail-Screen mit Live-Besuchern, Gender-Ratio, Events und Öffnungszeiten.
- [x] **Profil-Actions**: Profil-Buttons (QR Scanner / Bearbeiten) lassen sich zuverlässig ansteuern.

### 3. Kern-Komponenten & Theme Styling
- [x] **Map Overlay Filters**: Filter-Chips (Techno, House, 4.5+ Sterne) aktualisieren den Selektionsstatus.
- [x] **Chat List & Badges**: Public- und Private-Chat Einträge zeigen Ungelesen-Badges und korrekte Nachrichtentexte an.
- [x] **High-Contrast Theme**: Lila Buttons (`PurplePrimary`) und dunkler Hintergrund (`DarkBackground`) erfüllen Kontrast- und Accessibility-Anforderungen.

---

## Verifizierungsergebnis
Sämtliche UI-Testfälle wurden lokal verifiziert und laufen deterministisch ohne Flakiness durch.
