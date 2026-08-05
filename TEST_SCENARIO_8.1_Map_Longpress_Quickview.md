# QA Test-Szenario & UI-Test-Skript: Kapitel 8.1 - Long-Press Geste für Map-Marker-Quick-View

Dieses Dokument beschreibt das vollständige Test-Szenario sowie die Anleitung zur Durchführung und Verifizierung der **„Long-Press Geste für Map-Marker-Quick-View“** (Kapitel 8.1) in der native Kliq Android-Anwendung im Emulator.

---

## 🛠️ 1. Test-Voraussetzungen

- **Gerät / Emulator**: Android Emulator (API Level 33 oder 34, x86_64 Image mit Google Play Services).
- **Branch**: `feature/map-longpress-quickview`.
- **Design-System**: Kliq High-Contrast Dark Mode (`DarkSurface` `#1A1523`, Purple Accent `#8A2BE2`).
- **Test-Komponenten**:
  - `MapScreen.kt` (`com.kliq.app.ui.screens.map.MapScreen`)
  - `MapQuickViewCard.kt` (`com.kliq.app.ui.components.MapQuickViewCard`)
  - `MapViewModel.kt` (`com.kliq.app.ui.screens.map.MapViewModel`)

---

## 🧪 2. Schritt-für-Schritt Test-Szenario

### Schritt 1: App-Start & Navigation zum Karten-Screen
1. Starte die Kliq App im Emulator via Studio oder PowerShell:
   ```powershell
   $env:JAVA_HOME="C:\Users\kremidas\jdk17\jdk-17.0.10+7"
   .\gradlew.bat installDebug
   ```
2. Die Anwendung öffnet sich auf dem Hauptbildschirm. Stelle sicher, dass die Karte (`MapScreen`) geladen ist.
3. **Erwartetes Ergebnis**:
   - Die Google Maps Kartenansicht ist vollständig gerendert.
   - Aktive Club-Marker (z. B. **"Berghain / Panorama Bar"**, **"Watergate"**) werden in Lila (`#8A2BE2`) auf der Karte dargestellt.

---

### Schritt 2: Marker-Fokus & Long-Press Geste (Press-and-Hold)
1. Platziere den Mauszeiger / Touch-Fokus im Emulator auf einen aktiven Club-Marker (z. B. Berghain-Marker bei Lat `52.5112`, Lng `13.4430`).
2. Führe eine Long-Press-Geste aus: Drücke und halte die linke Maustaste für mindestens **500 ms** auf dem Marker.
3. **Erwartetes Ergebnis**:
   - Das System triggert ein leichtes haptisches Feedback (`HapticFeedbackType.LongPress`).
   - Die Kartenansicht wird sanft auf die Position des Markers zentriert, ohne den Haupt-Screen zu verlassen.

---

### Schritt 3: Visuelle Prüfung des Quick-View Overlays im Kliq Dark-Mode
1. Überprüfe das unmittelbar eingeblendete Quick-View Overlay:
2. **Erwartetes Ergebnis**:
   - Das Quick-View Panel gleitet flüssig von unten ein (`AnimatedVisibility` mit `slideInUp`).
   - Die Karte bleibt im Hintergrund sichtbar und wird nicht durch einen Screen-Wechsel ersetzt.
   - Die Farbgestaltung entspricht strikt dem Kliq Design-System: High-Contrast Dark Mode mit Lila Akzentfarben (`#8A2BE2` / `PurplePrimary`).

---

### Schritt 4: Datenintegrität in der Quick-View verifizieren
1. Prüfe die dargestellten Datenwerte im Quick-View Panel gegen die ViewModel-Daten:
   - **Location-Name**: Korrekter Name der Event-Location (z. B. *"Berghain / Panorama Bar"*).
   - **GPS-Distanz**: Berechnung der Entfernung im Format `0.3 km`.
   - **Sterne-Rating**: Durchschnittliche Bewertung mit Gold-Stern `★ 4.9`.
   - **Live-Besucherstatistik**: Genaue Angabe der aktuellen Besucher (z. B. *"380 Besucher live"*).
   - **Geschlechterverhältnis**: Prozentuale Aufschlüsselung (`♂ 52% | ♀ 48%`) mit zweifarbigem Fortschrittsbalken (Blau ♂ / Pink ♀).
   - **Auslastung & Live-Event**: Optionale Badges (`"85% Auslastung"`, Event-Titel *"Klubnacht"*).
2. **Erwartetes Ergebnis**:
   - Alle 5 Kern-Datenpunkte werden präzise und ohne Formatierungsfehler dargestellt.

---

### Schritt 5: Schließen der Quick-View & Wiederherstellung der Interaktivität
1. Simuliere das Schließen des Quick-View Panels:
   - **Methode A**: Tippe auf das Schließen-Icon (**`X`** / *"Schließen"*) oben rechts in der Card.
   - **Methode B**: Tippe auf einen freien Bereich der Karte außerhalb des Overlays.
2. **Erwartetes Ergebnis**:
   - Das Quick-View Panel blendet sich flüssig aus (`slideOutDown`).
   - Der Zustand `selectedVenue` im `MapViewModel` wird auf `null` zurückgesetzt.
   - Die Karte ist sofort wieder voll interaktiv (Zoomen, Pannen, Filtern voll funktionsfähig).

---

## 🤖 3. Automatisierter UI-Test (Emulator Execution)

Der automatisierte UI-Test [`MapLongPressQuickViewEmulatorTest.kt`](file:///c:/Users/kremidas/Documents/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/MapLongPressQuickViewEmulatorTest.kt) validiert die Gestenerkennung, Datenintegrität und Visualisierung programmatisch.

### Ausführung des automatisierten UI-Tests im Emulator:

```powershell
# 1. Umweltvariablen setzen
$env:JAVA_HOME="C:\Users\kremidas\jdk17\jdk-17.0.10+7"
$env:ANDROID_HOME="C:\Users\kremidas\AppData\Local\Android\Sdk"

# 2. UI-Test auf gestartetem Android Emulator ausführen
.\gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.MapLongPressQuickViewEmulatorTest
```

---

## 📋 4. Verifikations-Checkliste

| Test-Schritt | Prüfpunkt | Status |
| :--- | :--- | :---: |
| **Schritt 1** | App-Start & Karten-Rendering mit Club-Markern | **PASSED** |
| **Schritt 2** | Long-Press Gestenerkennung (Hold >= 500ms) & Haptic Feedback | **PASSED** |
| **Schritt 3** | Flüssige Overlay-Animation im Kliq Lila Dark-Mode | **PASSED** |
| **Schritt 4** | Datenintegrität (Name, GPS-Distanz, Rating, Live-Gäste, Geschlechterverhältnis) | **PASSED** |
| **Schritt 5** | Dismiss-Schließen & Wiederherstellung der Karten-Interaktivität | **PASSED** |
| **Edge Cases** | Abfangen fehlender Event-Titel & 0-Besucher Schwellenwerte | **PASSED** |
