# QA Checklist: Kapitel 9.3 - Speicher-Leck Analyse und Optimierung

## Testumgebung & Vorbedingungen
- [x] Android Studio Gradle Sync erfolgreich
- [x] Compilation Check: `./gradlew assembleDebug` fehlerfrei
- [x] Feature Branch: `feature/memory-leak-optimization`

---

## QS-Prüfpunkte

### 1. Leak Detection & Tooling
- [x] **LeakCanary 2.13**: In `debugImplementation` eingebunden; erzeugt keine Konflikte in Release-Builds.
- [x] **Memory Heap Profiling**: Aktivität-, Fragment- und ViewModel-Referenzen werden beim Navigieren ordnungsgemäß vom GC abgeräumt.

### 2. Image Caching & Memory Release
- [x] **Coil Image Loader**: Max. 25% RAM Obergrenze für den Arbeitsspeicher-Cache konfiguriert.
- [x] **ComponentCallbacks2**: `onTrimMemory` und `onLowMemory` entleeren den Bitmap-Cache bei System-Speicherknappheit.

### 3. Map Marker & Bitmap Lifecycle
- [x] **MapView Marker Eviction**: `MarkerBitmapHelper.clearCache()` wird beim Verlassen von `MapScreen` in `MapViewModel.onCleared()` aufgerufen.
- [x] **Bitmap Recycler**: Bitmaps hinter `BitmapDescriptor` Objekten werden nicht unendlich akkumuliert.

### 4. ViewModel & Context Leak Cleanup
- [x] **Context Safety**: Keine harten Activity-Context Referenzen in langlebigen ViewModels (Nutzung von `@ApplicationContext`).
- [x] **Coroutine Scopes**: `viewModelScope` beendet alle aktiven Data-Flows und Jobs beim Screen-Exit automatisch.

---

## Verifizierungsergebnis
Sämtliche Unit-Tests in `MemoryLeakUnitTest.kt` laufen erfolgreich durch. Das Skript `test_memory_leak_optimization_9.3.ps1` bestätigt die fehlerfreie Ausführung.
