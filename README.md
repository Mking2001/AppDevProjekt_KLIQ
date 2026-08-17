# Kliq - Native Mobile Socializing Platform

Native Mobile-App (Android Kotlin & Jetpack Compose) für Nightlife, Event-Discovery, Geofencing, Live-Club-Besucherstatistiken und Socializing.

---

## Technical Stack & Architecture

- **Sprache & Framework:** Kotlin 1.9+, Android SDK (compileSdk 34), Jetpack Compose (Material3)
- **Architektur:** MVVM + Clean Architecture mit Unidirectional Data Flow (`StateFlow` & `collectAsStateWithLifecycle`)
- **Dependency Injection:** Hilt (Dagger Hilt 2.50)
- **Persistenz:** Room Database 2.6.1 + EncryptedSharedPreferences (Crypto Security)
- **Netzwerk & Messaging:** Retrofit 2.9, Firebase Cloud Messaging (FCM), Gson
- **Memory Profiling & Leak Detection:** LeakCanary 2.13 (Debug), Coil MemoryCache (25% RAM Obergrenze)
- **Crash Reporting & Logging:** Firebase Crashlytics KTX 18.6.2, Timber 5.0.1, PiiSanitizer (Datenschutz/DSGVO)
- **Testing Stack:**
  - **Unit-Tests:** JUnit4, Mockito, Kotlin Coroutines Test, Robolectric
  - **UI & Integrationstests:** Compose UI Testing (`ui-test-junit4`), Hilt Android Testing (`hilt-android-testing`), Espresso

---

## Ausführung der Haupt-Workflow UI-Tests (Kapitel 9.2)

Die automatisierten UI-Tests für den Haupt-Workflow (Onboarding, Login, Bottom-Navigation Host, Map-Overlay & Stadt-Chat) befinden sich unter `app/src/androidTest/java/com/kliq/app/ui/workflow/`.

### 1. Ausführung über das PowerShell Skript
```powershell
powershell -ExecutionPolicy Bypass -File .\test_main_workflow_9.2.ps1
```

### 2. Ausführen via Gradle Command Line (mit aktivem Android Emulator)
```powershell
# 1. Onboarding & Login Flow Tests
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowOnboardingUITest

# 2. Bottom Navigation Host Tests (5 Haupt-Screens)
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowNavigationUITest

# 3. Core Components & Map Overlay Tests
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.workflow.MainWorkflowCoreComponentsUITest
```

---

## Ausführung der Speicher-Leck Tests (Kapitel 9.3)

```powershell
powershell -ExecutionPolicy Bypass -File .\test_memory_leak_optimization_9.3.ps1
```

---

## Ausführung der Crashlytics & PII Datenschutz Tests (Kapitel 9.4)

### PowerShell Skript (Windows)
```powershell
powershell -ExecutionPolicy Bypass -File .\test_crashlytics_integration_9.4.ps1
```

### Gradle Command Line
```powershell
./gradlew testDebugUnitTest --tests "*PiiSanitizerTest*" --tests "*CrashlyticsTreeTest*"
```

---

## Repository Dokumentation (Kapitel 9.2, 9.3 & 9.4)

- [Walkthrough & Zusammenfassung](file:///C:/Users/kremidas/.gemini/antigravity-ide/brain/2eea5e9e-3b61-4afc-b991-e1188d549783/walkthrough.md)
- [Kapitel 9.2 - Pull Request Dokumentation](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/PULL_REQUEST_9.2_UI_Tests_Main_Workflow.md)
- [Kapitel 9.2 - Code Review & Technical Audit](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/CODE_REVIEW_9.2_UI_Tests_Main_Workflow.md)
- [Kapitel 9.3 - Pull Request Dokumentation](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/PULL_REQUEST_9.3_Memory_Leak_Optimization.md)
- [Kapitel 9.3 - Code Review & Technical Audit](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/CODE_REVIEW_9.3_Memory_Leak_Optimization.md)
- [Kapitel 9.4 - Pull Request Dokumentation](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/PULL_REQUEST_9.4_Crashlytics_Integration.md)
- [Kapitel 9.4 - Code Review & Technical Audit](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/CODE_REVIEW_9.4_Crashlytics_Integration.md)
- [Kapitel 9.4 - Test Szenario Manual](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/TEST_SCENARIO_9.4_Crashlytics_Integration.md)
- [Kapitel 9.4 - QA Checkliste](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/QA_Checklist_9.4_Crashlytics_Integration.md)
