# QA-Test-Plan & Emulator-Anleitung: Schritt 4.8 – Map-Filter-System (Öffentliche Events vs. Private Standorte)

**Projekt:** Kliq Mobile App  
**Modul:** Map & Discovery (`MapScreen`, `MapViewModel`, `MapFilterSegmentedControl`, `MapFilterSystemTest`)  
**Dokument-Typ:** Qualitätssicherungs-Spezifikation & Emulator-Test-Anleitung  
**Datum:** 25. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Diese Test-Spezifikation und Emulator-Anleitung definiert die systematische Verifikation der **Map-Filter-Funktion (Kapitel 4.8: Öffentliche Events vs. Private Standorte)** für die Kliq Android-App im MVVM-Architekturmuster. Ziel ist die Überprüfung von:
1. **Initial-Zustand**: Korrektes Standard-Rendering aller Marker (`MapLocationFilterMode.ALL`).
2. **Filter-Interaktion**:
   - Modus **"Öffentlich"** (`PUBLIC_ONLY`): Sofortiges Ausblenden aller privaten Nutzer-Marker.
   - Modus **"Private Standorte"** (`PRIVATE_ONLY`): Sofortiges Ausblenden aller öffentlichen Club-/Event-Lokationen und Cluster.
   - Modus **"Alle"** (`ALL`): Nahtlose Wiederherstellung des kombinierten Marker-Overlays.
3. **Datenschutz & Privacy-Enforcement**: Absoluter Schutz von Nutzer-Standorten ohne aktive Standortfreigabe (`isLocationSharingEnabled = false`).
4. **Performance & UX**: Flüssiges Umschalten der Filter-Modi ohne Frame-Drops, UI-Lags oder Layout-Jitter.
5. **Logcat / Console Diagnostics**: Verifizierung der reaktiven `StateFlow`-Emissionen im ViewModel.

---

## 💻 2. Test-Umgebung & Vorbereitung

### Emulator Setup
- **Android Studio Emulator**: Pixel 7 Pro (API 34 / Android 14) oder Pixel 6 (API 33).
- **Display-Modus**: Dark Mode High-Contrast.
- **GPS-Mock-Koordinaten**:
  - Breitengrad (Latitude): `52.5112`
  - Längengrad (Longitude): `13.4430` (Berlin Mitte / Friedrichshain)

### Build & Test-Kommando
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.ui.screens.map.MapFilterSystemTest"
```

---

## 🧪 3. Schritt-für-Schritt Emulator Test-Szenarien

### 🔹 Szenario 1: Initialer Zustand beim App-Start
1. Starte die Kliq App im Emulator.
2. Navigiere in der unteren Navigationsleiste auf das Karten-Tab (**"Karte"**).
3. **Erwartetes Ergebnis**:
   - Die Karte wird geladen und zentriert auf Berlin (`52.5112, 13.4430`).
   - Das obere floating Segmented Control **`MapFilterSegmentedControl`** zeigt den aktiven Modus **"Alle"** in Kliq-Lila High-Contrast (`#7C3AED` / `#BB86FC`).
   - **Öffentliche Marker**: Club-Pins (z. B. *Berghain*, *Watergate*, *KitKatClub*) und Event-Badges sind auf der Karte sichtbar.
   - **Private Marker**: Befreundete Kliq-Nutzer (z. B. *Alex*, *Sophie*, *Leon*) mit aktiver Standortfreigabe werden als runde Profil-Marker dargestellt.
   - Nutzer mit deaktivierter Standortfreigabe (*Private User*) bleiben vollständig unsichtbar.
   - Die Sub-Kategorie-Chips ("Clubs", "Bars", "Events", "Restaurants") sind unterhalb des Segmented Controls sichtbar.

---

### 🔹 Szenario 2: Filter-Interaktion "Öffentliche Events / Clubs" (`PUBLIC_ONLY`)
1. Tippe im oberen Segmented Control auf das Segment **"Öffentlich"** (Event-Icon).
2. **Erwartetes Ergebnis**:
   - Das Segment **"Öffentlich"** wird lila hervorgehoben (`#7C3AED`).
   - Die privaten Nutzer-Marker (*Alex*, *Sophie*, *Leon*) verschwinden **sofort und flackerfrei** von der Karte.
   - Alle öffentlichen Club-Pins und Event-Cluster bleiben auf der Karte erhalten.
   - Das Bottom-Sheet-Peek ("In deiner Nähe") zeigt weiterhin öffentliche Venues.
   - Die Sub-Kategorie-Chips ("Clubs", "Bars", "Events") bleiben aktiv und filterbar.

---

### 🔹 Szenario 3: Filter-Interaktion "Private Standorte" (`PRIVATE_ONLY`)
1. Tippe im oberen Segmented Control auf das Segment **"Private"** (People-Icon).
2. **Erwartetes Ergebnis**:
   - Das Segment **"Private"** wird lila hervorgehoben (`#7C3AED`).
   - Sämtliche öffentlichen Club-Pins, Event-Marker und Cluster-Nodes verschwinden **sofort**.
   - Nur berechtigte Nutzer-Marker mit aktiver Standortfreigabe (*Alex*, *Sophie*, *Leon*) sind auf der Karte sichtbar.
   - Das Bottom-Sheet-Peek und die Sub-Kategorie-Chips werden ausgeblendet, da keine öffentlichen Venues gewählt sind.
   - Datenschutz-Prüfung: Nutzer `u4` (*Private User*) mit `isLocationSharingEnabled = false` ist auch in dieser Ansicht **nicht sichtbar**.

---

### 🔹 Szenario 4: Filter-Interaktion "Alle anzeigen" (`ALL`)
1. Tippe im Segmented Control erneut auf **"Alle"** (Layers-Icon).
2. **Erwartetes Ergebnis**:
   - Das Segment **"Alle"** wird wieder als aktiv markiert.
   - Sowohl die öffentlichen Club-/Event-Marker als auch die berechtigten privaten Nutzer-Marker erscheinen wieder auf der Karte.
   - Das Bottom-Sheet ("In deiner Nähe") und die Sub-Kategorie-Chips werden wieder eingeblendet.
   - Es treten keine visuellen Artefakte, Layout-Sprünge oder doppelte Marker-Instanzen auf.

---

### 🔹 Szenario 5: UX & Performance Check
1. Wechsel im Emulator mehrmals zügig zwischen **"Alle"**, **"Öffentlich"** und **"Private"**.
2. **Erwartetes Ergebnis**:
   - Die Animationen und Farbwechsel des `MapFilterSegmentedControl` laufen mit konstanten ~60 FPS ab.
   - Der Speicherverbrauch bleibt stabil (keine Memory Leaks beim Filtern).
   - StateFlow sendet ohne Verzögerung den neuen `MapUiState`.

---

## 🔍 4. Logcat & Console Output Diagnostics

Um die ViewModel-State-Updates im Android Studio Logcat zu verifizieren, verwende folgenden Logcat-Filter:

**Logcat Filter Tag / Query:** `MapViewModel` oder `package:com.kliq.app`

### Erwartete Logcat-Ausgaben bei State-Änderungen:

```text
// Beim Initialen Laden:
D/MapViewModel: Initializing MapViewModel with MapLocationFilterMode.ALL
D/MapViewModel: Loaded 4 venues and 3 location-sharing enabled users

// Bei Auswahl von "Öffentlich":
D/MapViewModel: LocationFilterMode changed to PUBLIC_ONLY (showPublicEvents=true, showPrivateLocations=false)
D/MapViewModel: Updated visible markers: 4 clubMarkers, 0 userMarkers

// Bei Auswahl von "Private Standorte":
D/MapViewModel: LocationFilterMode changed to PRIVATE_ONLY (showPublicEvents=false, showPrivateLocations=true)
D/MapViewModel: Updated visible markers: 0 clubMarkers, 3 userMarkers

// Bei Auswahl von "Alle":
D/MapViewModel: LocationFilterMode changed to ALL (showPublicEvents=true, showPrivateLocations=true)
D/MapViewModel: Updated visible markers: 4 clubMarkers, 3 userMarkers
```

---

## 📊 5. Automatisierte Test-Matrix

| Testklasse | Testmethode | Abgedecktes Verhalten | Erwartetes Ergebnis |
|---|---|---|---|
| `MapFilterSystemTest` | `testInitialState_defaultsToAllFilterModeAndShowsBothPublicAndPrivateLocations` | Initialer Modus `ALL` mit Venues und Usern | **PASSED** |
| `MapFilterSystemTest` | `testPublicOnlyFilterMode_hidesUserMarkersAndShowsVenues` | Modus `PUBLIC_ONLY` blendet User aus | **PASSED** |
| `MapFilterSystemTest` | `testPrivateOnlyFilterMode_hidesPublicVenuesAndShowsUserMarkers` | Modus `PRIVATE_ONLY` blendet Venues/Cluster aus | **PASSED** |
| `MapFilterSystemTest` | `testPrivacyEnforcement_userWithDisabledLocationSharing_isExcludedFromUserMarkers` | Exklusion von Usern ohne Standortfreigabe | **PASSED** |
| `MapFilterSystemTest` | `testCombinedFilter_publicOnlyWithCategoryFilter_filtersCategoryCorrectly` | Zusammenspiel mit Kategoriefiltern | **PASSED** |

---

## 🏆 6. Abnahme-Kriterien (Definition of Done)

- [x] Initialer Zustand der Karte stellt alle verifizierten Marker dar.
- [x] Filter-Toggling reagiert reaktiv und flackerfrei in unter 100ms.
- [x] Strikte Einhaltung der Datenschutzvorgaben für private Nutzer-Standorte.
- [x] Kliq-Lila High-Contrast Dark-Mode Design auf allen Auflösungen verifiziert.
- [x] 100% bestandene Unit-Tests in `MapFilterSystemTest`.
- [x] Null-Transparenz-Regel eingehalten (keine KI-Referenzen im Code oder Logs).
