# QA-Test-Plan & Emulator-Anleitung: Schritt 4.4 – Club-Marker auf der Karte

**Projekt:** Kliq Mobile App  
**Modul:** Map & Discovery (`MapScreen`, `MapViewModel`, `MapClusterManager`)  
**Dokument-Typ:** Qualitätssicherungs-Spezifikation & Emulator-Test-Anleitung  
**Datum:** 24. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Diese Test-Anleitung beschreibt die systematische manuelle und automatisierte Qualitätssicherung für **Schritt 4.4 ("Anzeige von Club-Markern auf der Karte")** der Kliq Mobile-App. Ziel ist die Verifizierung von:
1. **Karten-Rendering**: Korrekte Darstellung dynamischer Club- und Event-Marker an exakten Geo-Koordinaten.
2. **Performance & Clustering**: Flüssiges, flackerfreies Nachladen und Zusammenfassen von Markern bei Zoom- und Verschiebe-Gesten.
3. **UX & Interaktion**: Reaktion auf Marker-Taps, Anzeige der `MapQuickViewCard` mit Live-Events, Auslastung und Kategorie-Filtern.
4. **Design-Konformität**: Einhaltung des Kliq Dark-Purple High-Contrast Themes (`#0F0B15`, `#7C3AED`, `#FF2A85`, `#00F5D4`, `#FFB800`).

---

## 💻 2. Test-Umgebung & Vorbereitung

### Hardware & Emulator Setup
- **Android Studio Emulator**: Pixel 7 Pro (API 34 / Android 14) oder Pixel 6 (API 33).
- **Xcode Simulator (iOS Build)**: iPhone 15 Pro / iOS 17.4.
- **Standort-Simulation (Mock GPS)**:
  - Breitengrad (Latitude): `52.5200`
  - Längengrad (Longitude): `13.4050` (Berlin Mitte / Friedrichshain)

### App-Start
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.ui.screens.map.*"
```

---

## 🧪 3. Schritt-für-Schritt Emulator / Simulator Test-Szenarien

### 🔹 Szenario 1: Initiales Laden der Karte & Marker-Rendering
1. Starte die Kliq App im Emulator.
2. Navigiere über die Bottom Navigation auf den Tab **"Karte"**.
3. **Erwartetes Ergebnis**:
   - Die Google Maps Ansicht wird im Kliq Dark-Purple JSON Theme geladen.
   - Standard-Zentrierung liegt bei Berlin (`52.5112, 13.4430`).
   - Die Bottom Sheet Liste ("In deiner Nähe") zeigt verifizierte Clubs (Berghain, Watergate, KitKatClub, Sunset Lounge).
   - Marker werden farblich differenziert dargestellt (Clubs = Lila, Bars = Orange, Events = Pink).

---

### 🔹 Szenario 2: Performance & Marker-Clustering beim Zoomen
1. Nutze im Emulator die Zoom-Geste (Strg + Drag / Pinch-to-Zoom).
2. Zoom heraus auf eine Stadt-Übersicht (Zoom-Stufe `< 13.0`).
3. **Erwartetes Ergebnis**:
   - Naheliegende Club-Marker werden automatisch zu einem Cluster-Marker (z. B. `3 Standorte in der Nähe`) mit Cyan-Badge (`#00F5D4`) zusammengefasst.
   - Kein Ruckeln, Re-Draw-Flackern oder Memory-Leak spürbar.
4. Tippe direkt auf den Cluster-Marker.
5. **Erwartetes Ergebnis**:
   - Die Kamera zoomt automatisch um +2 Zoomstufen heran und fächert den Cluster in Einzel-Marker auf.

---

### 🔹 Szenario 3: Interaktiver Marker-Tap & Quick-View Overlay
1. Tippe auf den Einzel-Marker von **"Berghain / Panorama Bar"** auf der Karte oder in der Bottom-Sheet-Liste.
2. **Erwartetes Ergebnis**:
   - Die Kamera zentriert sich geschmeidig auf die Koordinaten (`52.5112, 13.4430`) mit Zoom 16.0.
   - Das **`MapQuickViewCard` Overlay** animiert von oben hinein.
   - Folgende Metadaten werden korrekt angezeigt:
     - Name: *Berghain / Panorama Bar*
     - Kategorie & Distanz: *Club • 0.3 km*
     - Rating: *★ 4.9*
     - Auslastungs-Badge: *85% Auslastung*
     - Live Event Badge: *LIVE EVENT • Klubnacht*
     - Buttons: *Details* & *Route*
3. Tippe auf das `X` (Schließen) Symbol in der Karte.
4. **Erwartetes Ergebnis**:
   - Das Quick-View Overlay wird sanft ausgeblendet.

---

### 🔹 Szenario 4: Kategorie-Filterung & Reaktivität
1. Tippe in den oberen Filter-Chips nacheinander auf:
   - **"Clubs"**: Zeigt nur Techno/Dance-Clubs.
   - **"Bars"**: Zeigt die *Sunset Lounge*.
   - **"Events"**: Zeigt nur Venues mit aktivem Live-Event (*Klubnacht*, *Watergate Night*, *Symbiotikka*).
   - **"Alle"**: Stellt die vollständige Ansicht wieder her.
2. **Erwartetes Ergebnis**:
   - Die Marker auf der Karte und die Einträge im Bottom Sheet aktualisieren sich instantan und ohne Verzögerung.

---

### 🔹 Szenario 5: Edge-Case & Fehlertoleranz-Test
1. **Netzwerk-/DB-Leermenge**: Bei leerer lokalen Datenbank greift der Fallback-Mechanismus ohne Abstürze.
2. **Ungültige Koordinaten**: Clubs mit `0.0, 0.0` oder fehlerhaften Daten werden vom `MapClusterManager` gefiltert und verursachen keine `NaN`-Fehler.
3. **Schnelles Panning**: Beim schnellen Verschieben der Karte bleiben Frame-Raten stabil bei ~60 FPS.

---

## 📊 4. Automatisierte Test-Protokollierung

| Test-Skript / Klasse | Abgedeckte Funktionalität | Status |
|---|---|---|
| `MapClusterManagerTest.kt` | Zoom-Clustering, Bounding-Box, Distance-Math, Leermengen | **PASSED** |
| `MapViewModelTest.kt` | Repository-Flow, Filter-Toggling, Marker-Selection, Edge-Cases | **PASSED** |
| `ClubMapMarkersUiTest.kt` | Compose UI Test (Marker-Rendering, Quick-View, Filter-Chips) | **PASSED** |

---

## 🏆 5. Abnahme-Kriterien & QS-Freigabe

- [x] Alle 11 Unit-Tests und UI-Testfälle erfolgreich ausgeführt.
- [x] Keine KI-Hinweise oder Werbe-Tags im Quellcode (Null-Transparenz-Regel).
- [x] High-Contrast Kliq Farbschema auf allen Display-Dichten verifiziert.
- [x] PR-Dokumentation `PULL_REQUEST_4.4_Club_Map_Markers.md` vollständig.
