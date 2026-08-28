# Pull Request: Kapitel 9.2 - UI-Tests für den Haupt-Workflow

## Zusammenfassung
Dieses Pull Request implementiert automatisierte, deterministische UI-Tests für den gesamten Haupt-Workflow der **Kliq** Mobile-App. Die Test-Suite ist unter `app/src/androidTest/java/com/kliq/app/` getrennt vom Produktionscode strukturiert und verwendet native Compose UI Testing Frameworks sowie Fake-Dienste zur Vermeidung von Test-Flakiness.

---

## Überprüfte Haupt-Workflows & Testabdeckung

### 1. Onboarding & Login-Flow (`MainWorkflowOnboardingUITest.kt`)
- **`testPhoneLoginAndOtpVerificationFlow`**: Validierung der Telefonnummer-Eingabe, Anforderung des SMS-OTP-Codes, 6-stellige Code-Eingabe und Bestätigung.
- **`testProfileCreationFormAndValidation`**: Formularvalidierung für Benutzername, Alter, Heimatstadt und Bio inkl. Visuell-Feedback und Button-Zuständen.
- **`testIntentMatchingPreferenceSelection`**: Interaktive Auswahl der Intentions-Karten („Freunde“, „Dating / Liebe“, „Beides“) mit State-Binding.
- **`testConsumptionHabitsSelectionFlow`**: Auswahl der Rauch- und Trinkgewohnheiten über Auswahl-Chips.
- **`testCompleteOnboardingNavigationChain`**: End-to-End Navigationstest durch die gesamte Onboarding-Kette.

### 2. Haupt-Navigation & NavHost (`MainWorkflowNavigationUITest.kt`)
- **`testBottomBarNavigationHostTabSwitching`**: Prüfung aller 5 Haupt-Tabs der Bottom Bar (Home, Entdecken, Karte, Aktivität, Profil) auf Selektionszustände, Sichtbarkeit und Tab-Zyklen.
- **`testExploreToClubDetailNavigation`**: Navigationsfluss von der Entdecken-Übersicht zum Club-Detail-Screen (Live-Besucherstatistik, Geschlechterverhältnis, Events, Öffnungszeiten).
- **`testProfileScreenNavigationAndActions`**: Interaktionsprüfung im Profil-Screen inkl. Trigger für den QR-Code Scanner.

### 3. Kern-Komponenten & Interaktionen (`MainWorkflowCoreComponentsUITest.kt`)
- **`testMapOverlayControlsAndFilterInteractions`**: Interaktion mit Kategorie-Filtern (Techno, House, 4.5+ Sterne) auf dem Map-Overlay.
- **`testChatListOpeningAndMessageInteractions`**: Darstellung von Public-City-Chats und 1-zu-1 Direct Messaging Chats inkl. Ungelesen-Badges, Chat-Öffnung und Text-Eingabe.
- **`testHighContrastThemeButtonsAndInteractiveComponents`**: Prüfung aller primären Call-to-Action-Buttons im High-Contrast Lila/Dark-Theme (`PurplePrimary` / `DarkBackground`).

---

## System-Architektur & Test-Isolation

- **Fake Location Provider (`FakeLocationProvider.kt`)**: Stellt feste GPS-Koordinaten bereit, um flakiness durch echte Sensorik-Abhängigkeiten auszuschließen.
- **Fake Backend State (`FakeBackendStateModule.kt`)**: Bietet deterministische Test-Daten für Clubs, Events, Chats und User-Profile.
- **Null-Transparenz-Regel**: Der Quellcode und die Testberichte sind vollständig in sauberem, konventionellem Entwickler-Stil verfasst.

---

## Ausführung der UI-Tests

Die Testsuite kann lokal im Android Studio oder über das Terminal mit verbundenem Emulator/Gerät ausgeführt werden:

```powershell
# Alle UI-Tests für den Haupt-Workflow ausführen
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowOnboardingUITest
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowNavigationUITest
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowCoreComponentsUITest
```

---

## Changed Files & Commit-Historie

- `app/src/androidTest/java/com/kliq/app/mock/FakeLocationProvider.kt`
- `app/src/androidTest/java/com/kliq/app/mock/FakeBackendStateModule.kt`
- `app/src/androidTest/java/com/kliq/app/ui/workflow/MainWorkflowOnboardingUITest.kt`
- `app/src/androidTest/java/com/kliq/app/ui/workflow/MainWorkflowNavigationUITest.kt`
- `app/src/androidTest/java/com/kliq/app/ui/workflow/MainWorkflowCoreComponentsUITest.kt`
- `PULL_REQUEST_9.2_UI_Tests_Main_Workflow.md`
- `QA_Checklist_9.2_UI_Tests_Main_Workflow.md`

### Commits
1. `test: setup test mocks and fake services for deterministic UI testing`
2. `test: add UI test for onboarding flow`
3. `test: add UI test for bottom navigation and screen switching`
4. `test: add UI test for core component interactions and map overlay`
5. `docs(test): add pull request documentation for main workflow UI tests`
