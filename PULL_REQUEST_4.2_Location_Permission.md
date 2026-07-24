# Pull Request: Kapitel 4.2 - Standort-Berechtigungs-Workflow ("Standort aktivieren")

**Branch:** `feature/location-permission-workflow-mvvm` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/location-permission-workflow-mvvm)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert den **Standort-Berechtigungs-Workflow ("Standort aktivieren")** für die Kliq Mobile-App gemäß Kapitel 4.2 der technischen Spezifikation nach dem MVVM-Muster.

---

## 🛠 Umgesetzte Änderungen

### 1. Berechtigungs-Management & Domain-Model
- **`LocationPermissionState.kt`**: Sealed Interface zur reaktiven Typisierung aller Systemberechtigungszustände (`Granted`, `Denied`, `PermanentlyDenied`, `NotRequested`).
- **`AndroidManifest.xml`**: Vollständige Deklaration von `ACCESS_FINE_LOCATION` und `ACCESS_COARSE_LOCATION`.

### 2. Service & Hilt Dependency Injection
- **`PermissionManager.kt`**: Abstrahiert den Zugriff auf native Android system permissions (`ContextCompat.checkSelfPermission`) und stellt Deep-Linking-Funktionen bereit.
- **`AppModule.kt`**: Hilt Provider-Binding für `PermissionManager`.

### 3. MVVM State-Management
- **`PermissionViewModel.kt` & `PermissionUiState`**: Reaktive Verwaltung von Berechtigungsprüfungen, Rationale-Dialog-Anzeigen und Deep-Link-Events.

### 4. Custom Rationale UI & Deep-Linking
- **`LocationRationaleDialog.kt`**: Kliq-spezifischer Rationale-Dialog im High-Contrast Lila/Dark-Mode (`PurplePrimary`, `#0F0B15`). Erklärt die Notwendigkeit für Nightlife-Kernfeatures (Geofencing, Standorts-Verifizierung bei Reviews, Live-Karten).
- **`LocationPermanentlyDeniedDialog`**: Dialog für dauerhaft abgelehnte Berechtigungen mit Direktlink (**"In Einstellungen öffnen"**) via `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)`.
- **`MapScreen.kt` Integration**: Anbindung des `PermissionViewModel` und `ActivityResultContracts.RequestMultiplePermissions` beim Klick auf den Location FAB.

### 5. Tests & QA-Dokumentation
- **Unit-Tests**: `PermissionViewModelTest.kt` zur Verifizierung aller Zustandstransformationen (`BUILD SUCCESSFUL in 55s`).
- **Instrumentierte UI-Tests**: `LocationPermissionIntegrationTest.kt` im Emulator.
- **QA-Checkliste**: [QA_Checklist_Location_Permission_Workflow.md](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/QA_Checklist_Location_Permission_Workflow.md).

---

## 📋 Commit-Historie

1. `feat(permission): add LocationPermissionState domain sealed interface`
2. `feat(permission): implement PermissionManager service for system checks and settings deep-linking`
3. `feat(permission): implement PermissionViewModel managing reactive permission state and Rationale dialog events`
4. `feat(permission): add LocationRationaleDialog UI components and integrate permission workflow into MapScreen`
5. `test(permission): add PermissionViewModelTest, LocationPermissionIntegrationTest and QA Checklist for Kapitel 4.2`

---

## 🧪 Verifizierung
- `./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.PermissionViewModelTest"` erfolgreich bestanden.
- Keinerlei KI-Hinweise in Code oder Commits vorhanden.
