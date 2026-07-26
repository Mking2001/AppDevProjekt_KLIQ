# QA Test Plan: Map-Kamera-Animationen für flüssiges Look & Feel (Kapitel 4.9)

## 1. Übersicht & Zielsetzung
Validierung der nativen Karten-Kamera-Animationen in der Kliq App. Ziel ist eine flüssige, ruckelfreie Visualisierung mit konstanter Ziel-Bildrate von 60fps bei Marker-Fokussierung, Standort-Re-Centering, Filter-Ergebnis-Framing und 3D-Night-Modus-Übergängen.

---

## 2. Testfälle

### Testfall 4.9.1: Standort-Re-Centering Animation (User Location FAB)
- **Voraussetzung**: GPS-Berechtigung ist erteilt, Karte zeigt einen beliebigen Kartenausschnitt.
- **Schritte**:
  1. Verschiebe die Karte manuell von der aktuellen GPS-Position weg.
  2. Tippe unten rechts auf den Standort-Button (MyLocation FAB).
- **Erwartetes Ergebnis**:
  - Die Kamera re-zentriert sanft auf die eigenen Standort-Koordinaten.
  - Animationsdauer beträgt exakt 1000ms mit einer Easing-Kurve (Ease-In-Out).
  - Das Ladesymbol auf dem FAB verschwindet nach Abschluss der Bewegung.

### Testfall 4.9.2: Marker-Fokussierung & 3D-Night-Tilt
- **Voraussetzung**: Öffentliche Clubs oder private User-Marker sind auf der Karte sichtbar.
- **Schritte**:
  1. Tippe auf einen Club-Marker (z.B. Berghain) oder einen User-Marker.
- **Erwartetes Ergebnis**:
  - Die Kamera zentriert sich sanft auf die Marker-Koordinaten.
  - Zoomlevel wechselt flüssig auf 16.0.
  - Die Kamera neigt sich sanft um 35° (Tilt) und rotiert um 15° (Bearing) für den Kliq 3D-Night-Look.
  - Der Overlay QuickView-Card öffnet sich ohne Ruckeln.

### Testfall 4.9.3: Automatische Bounding-Box Ausrichtung bei Filter-Wechsel
- **Voraussetzung**: Mehrere Venues in unterschiedlichen Kategorien (Clubs, Bars, Events) sind vorhanden.
- **Schritte**:
  1. Wähle den Filter chip "Bars" oder "Clubs".
- **Erwartetes Ergebnis**:
  - `MapViewModel` berechnet dynamisch die `LatLngBounds` aller gefilterten Marker.
  - Die Kamera animiert sanft innerhalb von 1000ms zu einem Vektor-Frame mit 120px Padding, sodass alle gefilterten Pins komfortabel im Viewport sichtbar sind.

### Testfall 4.9.4: Cluster-Nodes Zoom-In Animation
- **Voraussetzung**: Zoomlevel ist so weit herausgezoomt, dass Marker geklustert werden.
- **Schritte**:
  1. Tippe auf einen Cluster-Node-Pin (z.B. "5 Standorte in der Nähe").
- **Erwartetes Ergebnis**:
  - Die Kamera zentriert sich sanft auf den Schwerpunkt des Clusters.
  - Zoom-Level erhöht sich flüssig um +2.0 Stufen (bis max. 18.0).

---

## 3. Automatische Unit-Tests

```bash
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat testDebugUnitTest --tests "com.kliq.app.ui.screens.map.MapCameraAnimationTest"
```

Resultat: `BUILD SUCCESSFUL` (100% Pass)
