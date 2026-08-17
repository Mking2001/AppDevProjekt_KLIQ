# Pull Request: Kapitel 9.3 - Speicher-Leck Analyse und Optimierung

## Zusammenfassung
Dieses Pull Request behebt potenzielle Speicherlecks und optimiert das gesamte Speicher-Management der **Kliq** Mobile-Applikation. Es integriert **LeakCanary** für automatisches Memory-Leak Profiling im Debug-Modus, beschränkt Coil Image-Caching auf max. 25% des verfügbaren Arbeitsspeichers, führt automatisches Bitmap-Trimming bei `onTrimMemory`/`onLowMemory` ein, leert MapView Marker Bitmaps beim Verlassen des Map-Screens in `onCleared()` und stellt die ungebundene Lifecycle-Freigabe in allen ViewModels sicher.

---

## Durchgeführte Optimierungen & Behebungen

### 1. LeakCanary Detection Tooling (`app/build.gradle.kts`)
- Integration von `com.squareup.leakcanary:leakcanary-android:2.13` unter `debugImplementation`.
- Automatische Überwachung von Activity-, Fragment- und ViewModel-Referenzen bei jedem Debug-App-Start.

### 2. Image Caching & Memory Release (`KliqApplication.kt`)
- **Coil `ImageLoaderFactory` Integration**: Explizite Obergrenzen für den Arbeitsspeicher-Cache (max 25% RAM) und Disk-Cache (50 MB).
- **`ComponentCallbacks2` Listener**: Automatische Entleerung des Memory-Caches bei System-Events (`TRIM_MEMORY_RUNNING_LOW`, `TRIM_MEMORY_BACKGROUND`, `onLowMemory`).

### 3. Map Marker Bitmap Lifecycle Management (`MarkerBitmapHelper.kt` & `MapViewModel.kt`)
- Ergänzung der `clearCache()` und `getCacheSize()` Hilfsmethoden in `MarkerBitmapHelper`.
- Überschreiben der `onCleared()` Lebenszyklus-Methode im `MapViewModel`:
  - Evakuierung aller generierten `BitmapDescriptor` Objekte aus dem RAM beim Screen-Exit.
  - Entkoppeln aller gehaltenen Datenlisten (`allVenues`, `allUsers`, `blockedUserIds`).

### 4. ViewModel & Context Leak Cleanup (`LocationTrackingViewModel.kt`, `PermissionViewModel.kt`)
- Sicherstellung der ausschließlichen Verwendung von `@ApplicationContext` zur Vermeidung von Activity-Context-Leaks.
- Sauberes Zurücksetzen des UI-Zustands in `onCleared()` zur Freigabe von Coroutine-Jobs und Observer-Subscriptions.

---

## Automated Test Verification

- **Automatisierte Unit-Tests (`MemoryLeakUnitTest.kt`)**:
  - `testMarkerBitmapHelper_clearCache_evictsAllDescriptors`: Verifiziert die vollständige Bitmap-Cache-Freigabe.
  - `testMapViewModel_onCleared_triggersCacheEviction`: Verifiziert den automatischen Cache-Drop bei ViewModel-Destruction.
  - `testLocationTrackingUiState_initialState_clean`: Verifiziert saubere Initialzustände.

- **Ausführung via PowerShell Test-Runner**:
  ```powershell
  powershell -ExecutionPolicy Bypass -File .\test_memory_leak_optimization_9.3.ps1
  ```

---

## Changed Files & Commit-Historie

- `app/build.gradle.kts`
- `app/src/main/java/com/kliq/app/KliqApplication.kt`
- `app/src/main/java/com/kliq/app/ui/screens/map/MarkerBitmapHelper.kt`
- `app/src/main/java/com/kliq/app/ui/screens/map/MapViewModel.kt`
- `app/src/main/java/com/kliq/app/viewmodel/LocationTrackingViewModel.kt`
- `app/src/test/java/com/kliq/app/util/MemoryLeakUnitTest.kt`
- `test_memory_leak_optimization_9.3.ps1`
- `scripts/run_memory_leak_tests.sh`
- `PULL_REQUEST_9.3_Memory_Leak_Optimization.md`
- `QA_Checklist_9.3_Memory_Leak_Optimization.md`
- `CODE_REVIEW_9.3_Memory_Leak_Optimization.md`

### Commits
1. `fix: integrate LeakCanary for debug memory leak detection`
2. `fix: optimize Coil image memory cache and lifecycle release in KliqApplication`
3. `fix: resolve MapView marker bitmap memory retention in MapViewModel`
4. `refactor: optimize Context handling and lifecycle cleanup in ViewModels`
5. `test: add unit tests for memory leak prevention and cache evacuation`
6. `docs(test): add pull request and code review documentation for memory leak optimization`
