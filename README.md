# Kliq - Native Mobile Socializing Platform

Native Mobile-App (Android Kotlin & Jetpack Compose) für Nightlife, Event-Discovery, Geofencing, Live-Club-Besucherstatistiken und Socializing.

---

## Technical Stack & Architecture

- **Sprache & Framework:** Kotlin 1.9+, Android SDK (compileSdk 34), Jetpack Compose (Material3)
- **Architektur:** MVVM + Clean Architecture mit Unidirectional Data Flow (`StateFlow` & `collectAsStateWithLifecycle`)
- **Domain Layer:** Modularisierte UseCases (`GetClubsWithDistanceUseCase`, `CalculateUserDistanceUseCase`, `VerifyQRCodeUseCase`)
- **Design System & Components:** Reusable High-Contrast UI Components (`KliqPrimaryButton`, `KliqSecondaryButton`, `KliqSurfaceCard`)
- **Dependency Injection:** Hilt (Dagger Hilt 2.50 + `UseCaseModule`)
- **Persistenz:** Room Database 2.6.1 + EncryptedSharedPreferences (Crypto Security)
- **Netzwerk & Messaging:** Retrofit 2.9, Firebase Cloud Messaging (FCM), Gson
- **Memory Profiling & Leak Detection:** LeakCanary 2.13 (Debug), Coil MemoryCache (25% RAM Obergrenze)
- **Crash Reporting & Logging:** Firebase Crashlytics KTX 18.6.2, Timber 5.0.1, PiiSanitizer (Datenschutz/DSGVO)
- **Testing Stack:**
  - **Unit-Tests:** JUnit4, Mockito, Kotlin Coroutines Test, Robolectric
  - **UI & Integrationstests:** Compose UI Testing (`ui-test-junit4`), Hilt Android Testing (`hilt-android-testing`), Espresso

---

## Ausführung der Haupt-Workflow UI-Tests (Kapitel 9.2)

```powershell
powershell -ExecutionPolicy Bypass -File .\test_main_workflow_9.2.ps1
```

---

## Ausführung der Speicher-Leck Tests (Kapitel 9.3)

```powershell
powershell -ExecutionPolicy Bypass -File .\test_memory_leak_optimization_9.3.ps1
```

---

## Ausführung der Crashlytics & PII Datenschutz Tests (Kapitel 9.4)

```powershell
powershell -ExecutionPolicy Bypass -File .\test_crashlytics_integration_9.4.ps1
```

---

## Ausführung der Code-Refactoring & Architektur Tests (Kapitel 9.5)

### PowerShell Skript (Windows)
```powershell
powershell -ExecutionPolicy Bypass -File .\test_code_refactoring_9.5.ps1
```

### Gradle Command Line
```powershell
./gradlew testDebugUnitTest --tests "*ArchitectureRefactoringUnitTest*"
```

---

## Repository Dokumentation (Kapitel 9.2, 9.3, 9.4 & 9.5)

- [Walkthrough & Zusammenfassung](file:///C:/Users/kremidas/.gemini/antigravity-ide/brain/2eea5e9e-3b61-4afc-b991-e1188d549783/walkthrough.md)
- [Kapitel 9.2 - Pull Request Dokumentation](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/PULL_REQUEST_9.2_UI_Tests_Main_Workflow.md)
- [Kapitel 9.2 - Code Review & Technical Audit](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/CODE_REVIEW_9.2_UI_Tests_Main_Workflow.md)
- [Kapitel 9.3 - Pull Request Dokumentation](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/PULL_REQUEST_9.3_Memory_Leak_Optimization.md)
- [Kapitel 9.3 - Code Review & Technical Audit](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/CODE_REVIEW_9.3_Memory_Leak_Optimization.md)
- [Kapitel 9.4 - Pull Request Dokumentation](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/PULL_REQUEST_9.4_Crashlytics_Integration.md)
- [Kapitel 9.4 - Code Review & Technical Audit](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/CODE_REVIEW_9.4_Crashlytics_Integration.md)
- [Kapitel 9.5 - Pull Request Dokumentation](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/PULL_REQUEST_9.5_Code_Refactoring_Architecture.md)
- [Kapitel 9.5 - Code Review & Technical Audit](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/CODE_REVIEW_9.5_Code_Refactoring_Architecture.md)
- [Kapitel 9.5 - QA Checkliste](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/QA_Checklist_9.5_Code_Refactoring_Architecture.md)
