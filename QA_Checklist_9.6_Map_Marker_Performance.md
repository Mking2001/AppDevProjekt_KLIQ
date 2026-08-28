# QA-Checkliste: Kapitel 9.6 Map-Marker Performance & Stress-Testing

Diese Checkliste dient der manuellen und automatisierten Qualitätssicherung für das **Performance-Tuning der Map-Marker** der nativen Android-App **Kliq**.

---

## 1. Architektur & Code-Qualität

- [x] **MVVM-Kapselung**: Keine Business- oder Geodaten-Berechnungen in `MapScreen.kt` (Composable).
- [x] **Separation of Concerns**: Rohdaten-Transformation (`Club`, `User`) erfolgt asynchron im `MapViewModel`.
- [x] **Recomposition-Sicherheit**: Marker-Listen sind mit `androidx.compose.runtime.key(...)` umschlossen.
- [x] **Lifecycle-Cleanup**: `MapViewModel.onCleared()` leert alle Bitmap- und Cluster-Caches (`MarkerBitmapHelper.clearCache()`).

---

## 2. Performance & Thread-Sicherheit

- [x] **Background Dispatching**: Distanz- und Clusterberechnungen laufen strikt auf `Dispatchers.Default`.
- [x] **250ms Debouncing**: Wisch- und Zoomgesten werden über `cameraMoveStream.debounce(250)` gedrosselt.
- [x] **LRU Bitmap Caching**: 256 Slots Kapazität im `MarkerBitmapHelper` verhindern Frame-Allokationen.
- [x] **Spatial Bounding-Box**: $O(N)$ Vorfilterung vor Haversine-Distanzberechnungen im `MapClusterManager`.
- [x] **Cluster Memoization**: 64 Slots LRU-Cache für berechnete Cluster pro gerundeter Zoomstufe.
- [x] **60 FPS Budget**: Frame-Renderings bleiben stabil unter $16.6\,\text{ms}$.
- [x] **ANR-Freiheit**: 0% Main-Thread Blockierung bei Gesten.

---

## 3. High-Contrast Dark Mode & Design

- [x] **Signature Purple (`0xFF7C3AED`)**: Hauptmarkerkörper in leuchtendem Kliq-Lila.
- [x] **Ultra-Dark Canvas (`0xFF1E1035`)**: Dunkler Innenkreis für maximalen Kontrast.
- [x] **Active Event Badge (`0xFFEC4899`)**: Neon-Pinker Indikator für Live-Events.
- [x] **Online Status (`0xFF10B981`)**: Neon-Grüner Punkt für verifizierte Online-User.
- [x] **Translucent Cluster Halo (`0x667C3AED`)**: Transparenter Lila Glow bei aggregierten Knoten.

---

## 4. Test- & Automatisierungs-Freigabe

- [x] `MapMarkerStressTest.kt`: 500+ Pins Stress-Test, Gesten-Debounce & 20x Screen-Switch.
- [x] `MapMarkerPerformanceUnitTest.kt`: MVVM-Entkopplung, 250ms Debounce, Filtermodi.
- [x] `MarkerBitmapHelperTest.kt`: Cache-Trefferquote, Pre-Warming, Eviction, Concurrency.
- [x] `MapClusterManagerTest.kt`: Zoomstufen-Verhalten, Memoization, 500-Pins-Benchmark.
- [x] `MemoryLeakUnitTest.kt`: Evakuierung bei ViewModel-Destruction.
- [x] `test_map_marker_performance_9.6.ps1`: 100% PASS aller Testfälle.

---

**Freigabestatus**: ✅ **BESTANDEN & FREIGEGEBEN**
