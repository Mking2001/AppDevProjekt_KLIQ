# Pull Request: Kapitel 4.4 - Anzeige von Club-Markern auf der Karte

**Branch:** `feature/club-map-markers` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/club-map-markers)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die **dynamische Anzeige von Club- und Event-Markern auf der Karte** inkl. Anbindung des `ClubRepository`, performantem Marker-Clustering, interaktiven Quick-View Overlays und umfassender Testabdeckung gemäß Kapitel 4.4 der technischen Spezifikation nach dem MVVM-Muster.

---

## 🛠 Umgesetzte Änderungen

### 1. Performance-Clustering & Caching (`MapClusterManager.kt`)
- **`MapClusterManager`**: Implementierung eines zoomabhängigen Clustering-Algorithmus (Bounding-Box / Grid-Clustering), der naheliegende Club-Marker bei niedriger Zoomstufe zu Cluster-Nodes zusammenfasst.
- **Flackerfreies Nachladen**: Interne Memory-Caching-Logik basierend auf Hash-Keys zur Vermeidung unnötiger Heap-Allokationen und UI-Re-Draws beim Verschieben des Kartenausschnitts.
- **`ClusterMarkerUiState`**: Sealed Class für typsichere Unterscheidung zwischen `SingleNode` (Einzel-Marker) und `ClusterNode` (Gruppierte Marker).

### 2. MVVM-Architektur & State Management (`MapViewModel.kt`)
- **`ClubRepository` Anbindung**: Reaktives Sammeln der verifizierten Club- und Event-Daten aus der lokalen Room-Datenbank via Kotlin Coroutines `Flow`.
- **Kategorie-Filterung & Dynamic Updates**: Reaktiver Zustand für Kategorien ("Alle", "Clubs", "Bars", "Events", "Restaurants") mit automatischer Neuberechnung der sichtbaren und gruppierten Marker.
- **Interaktions-Handling**:
  - `onMarkerClicked`: Zentriert die Kamera auf den ausgewählten Club und öffnet das Quick-View Overlay.
  - `onClusterClicked`: Erhöht die Zoomstufe um +2.0f und zentriert die Kamera auf den Cluster-Mittelpunkt, um naheliegende Marker aufzufächern.
  - `onCameraMoved`: Synchronisiert Kamera-Bewegungen mit dem ViewModel für stufenloses Re-Clustering.

### 3. Native Map UI & Quick View Overlay (`MapScreen.kt` & `MapQuickViewCard.kt`)
- **High-Contrast Marker-Styling**: Farblich differenzierte Marker für Clubs (Lila `#7C3AED`), Bars (Orange/Gold `#FFB800`), Events (Magenta `#FF2A85`) und Cluster-Nodes (Cyan/Teal `#00F5D4`).
- **Enhanced `MapQuickViewCard`**: High-Contrast Compose Overlay zur Anzeige von Club-Name, Kategorie, Distanz, Auslastungsanzeige (`%`), Live-Events und Direkt-Aktionen ("Details", "Route").

### 4. Unit-Tests & Qualitätssicherung
- **`MapClusterManagerTest.kt`**: Unit-Tests zur Verifizierung von Zoom-Schwellenwerten, Distanzberechnungen und Leermengen-Handling.
- **`MapViewModelTest.kt`**: Erweiterte ViewModel Unit-Tests mit gemocktem `ClubRepository`, StateFlow-Prüfung, Filter-Toggling und Cluster-Klicks.
- **QA-Checkliste**: [QA_Checklist_Club_Map_Markers.md](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/QA_Checklist_Club_Map_Markers.md).

---

## 📋 Commit-Historie

1. `feat(map): implement MapClusterManager for zoom-dependent marker clustering and caching`
2. `feat(map): bind ClubRepository flow in MapViewModel with dynamic filtering and cluster states`
3. `feat(map): enhance MapQuickViewCard UI with live event badges and capacity indicator`
4. `feat(map): render custom high-contrast single and cluster markers on GoogleMap layout`
5. `test(map): add MapClusterManagerTest, update MapViewModelTest and QA checklist for Kapitel 4.4`

---

## 🧪 Verifizierung

- `./gradlew testDebugUnitTest --tests "com.kliq.app.ui.screens.map.*"` erfolgreich ausgeführt.
- Keinerlei KI-Hinweise in Code, Kommentaren oder Commits vorhanden (Null-Transparenz-Regel).
