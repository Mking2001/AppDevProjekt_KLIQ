# Pull Request: Feature 4.9 - Map-Kamera-Animationen für flüssiges Look & Feel

## Beschreibung
Dieses PR implementiert Kapitel 4.9 der Kliq App und führt eine reaktive, hochperformante Kamera-Animations-Architektur für die Google Maps Jetpack Compose Integration ein. 

Durch die Entkopplung der Kamera-Event-Logik im `MapViewModel` über `SharedFlow<MapCameraAnimationEvent>` erreicht die View-Schicht flüssige 60fps Kameraübergänge bei Standort-Zentrierung, Marker-Fokussierung mit 3D-Night-Tilt und automatischer Bounding-Box-Framing bei Filter-Ergebnissen.

---

## Änderungen im Detail

### 1. Domain & Model (`MapCameraAnimationEvent.kt`)
- `MapCameraAnimationEvent` Sealed Interface mit Event-Typen:
  - `AnimateToLocation`: Re-Centering und Marker-Fokussierung mit LatLng, Zoom, Tilt, Bearing, Easing und definierter Dauer (800–1000ms).
  - `AnimateToBounds`: Bounding-Box-Framing für Marker-Gruppen inkl. Padding in PX.
  - `AnimateTiltRotation`: 3D-Night-Perspektivenwechsel (Tilt & Bearing).
  - `SnapToPosition`: Sofortige Positionierung ohne Animation.
- `LatLngBoundsData`: Hilfsmodell zur thread-sicheren Bounding-Box-Berechnung.

### 2. ViewModel (`MapViewModel.kt`)
- Bereitstellung von `cameraEventFlow: SharedFlow<MapCameraAnimationEvent>`.
- Re-Centering bei `onLocationRequested()` (1000ms Dauer, Ease-In-Out Curve).
- Marker-Fokussierung bei `onMarkerClicked()` / `onUserMarkerClicked()` (Zoom 16.0, Tilt 35°, Bearing 15°, 1000ms Dauer).
- Automatische Bounding-Box-Berechnung `triggerAutoFitCameraAnimation()` bei Filter-Wechsel oder Modus-Umschaltung (`ALL`, `PUBLIC_ONLY`, `PRIVATE_ONLY`).

### 3. View-Schicht (`MapScreen.kt`)
- `LaunchedEffect(Unit)` zur Beobachtung von `cameraEventFlow`.
- Integration von `CameraUpdateFactory.newCameraPosition(...)` und `CameraUpdateFactory.newLatLngBounds(...)` auf `cameraPositionState`.
- 60fps Performance-Optimierung ohne unnötige Re-Compositions des UI-Trees.

### 4. Unit-Tests (`MapCameraAnimationTest.kt`)
- Abdeckung aller Event-Emissionen, Easing-Parameter, Bounding-Box-Berechnungen und 3D-Perspektivenübergänge.

---

## Verfizierung & Tests
- [x] Unit-Tests erfolgreich ausgeführt (`./gradlew.bat testDebugUnitTest --tests "com.kliq.app.ui.screens.map.*"`)
- [x] Git Commit Historie geprüft und sauber strukturiert
- [x] Einhaltung aller MVVM- und Kliq-Designsystem-Vorgaben
