# QA Checkliste & Qualitätsprüfung: Kliq Standort-Berechtigungs-Workflow (Kapitel 4.2)

Diese Dokumentation dient der qualitativen Überprüfung und Abnahme des **Standort-Berechtigungs-Workflows ("Standort aktivieren")** nach den offiziellen Bewertungskoeffizienten der Kliq Mobile-App (MVVM, State-Management, Rationale-UI & Deep-Linking).

---

## 📍 1. Berechtigungs-Management & System-Konfiguration

- [x] **Manifest & Native Berechtigungen:**
  - In `AndroidManifest.xml` sind `ACCESS_FINE_LOCATION` (präziser GPS-Standort) und `ACCESS_COARSE_LOCATION` (ungefährer Netzwerk-Standort) ordnungsgemäß deklariert.
- [x] **Zustandsmodellierung (`LocationPermissionState`):**
  - Strikte Typisierung der Berechtigungszustände über eine sealed interface (`Granted`, `Denied`, `PermanentlyDenied`, `NotRequested`).

---

## 🏗 2. MVVM-Architektur & Reaktives State-Handling

- [x] **Kapselung im `PermissionViewModel`:**
  - Reaktiv verwalteter `PermissionUiState` mit `StateFlow`.
  - Keine direkten View-Referenzen im ViewModel; volle Testbarkeit über Mocks.
- [x] **Service-Abstraktion (`PermissionManager`):**
  - `PermissionManager.kt` entkoppelt den Zugriff auf Android `ContextCompat` und `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` Intents.
  - Hilt Dependency Injection via `AppModule` sichert Saubere Entkopplung für Unit- & Integrationstests.

---

## 🎨 3. UI/UX-Workflow, Rationale-Dialog & Deep-Linking

- [x] **Custom Kliq Rationale-Dialog (`LocationRationaleDialog.kt`):**
  - Wird vor dem Aufruf des nativen System-Dialogs angezeigt.
  - Verständliche Erklärung der Notwendigkeit für Nightlife-Kernfeatures (Geofencing, Standorts-Verifizierung bei Reviews, Live-Karten).
  - High-Contrast Lila/Dark-Mode-Design (`PurplePrimary`, `#0F0B15`, abgerundete Karte, 64dp Icon-Header).
- [x] **Permanently Denied & Deep-Linking (`LocationPermanentlyDeniedDialog.kt`):**
  - Bei dauerhafter Ablehnung ("Nicht erneut fragen") bietet die UI den Button **"In Einstellungen öffnen"**.
  - Löst ein direktes Deep-Link-Intent (`Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)`) in die Android-Systemeinstellungen der App aus.

---

## 🧪 4. Automatisiertes Test-Coverage & Verifizierung

- [x] **Unit-Tests (`PermissionViewModelTest.kt`):**
  - Prüft alle Zustandstransformationen (`Granted`, `Denied`, `PermanentlyDenied`), Rationale-Dialog-Triggers und Deep-Link Intent Auslösung.
  - **Ergebnis:** `BUILD SUCCESSFUL in 55s` (100% Pass).
- [x] **Instrumentierte UI- & Integrationstests (`LocationPermissionIntegrationTest.kt`):**
  - Validiert den Rationale-Dialog-Workflow im Emulator beim Klick auf den Standort-FAB.

---

## 📊 Zusammenfassende Bewertung

| Kriterium | Status | Befund |
| :--- | :---: | :--- |
| **Permission-Management** | ✅ Bestanden | Deklaration von `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION` |
| **MVVM State-Handling** | ✅ Bestanden | Sealed Interface `LocationPermissionState` & reaktiver `PermissionViewModel` State |
| **Rationale UI & Design** | ✅ Bestanden | Custom Dark-Purple Dialog für Geofencing-Erklärung |
| **Deep-Linking** | ✅ Bestanden | Direkter Intent in App System-Einstellungen bei dauerhafter Ablehnung |
| **Testabdeckung** | ✅ Bestanden | Unit-Tests (`PermissionViewModelTest.kt`) & UI-Emulator-Tests erfolgreich |

> **Fazit:** Die Implementierung von Kapitel 4.2 erfüllt sämtliche technischen, optischen und sicherheitsbezogenen Anforderungen vollumfänglich und ist produktionsreif.
