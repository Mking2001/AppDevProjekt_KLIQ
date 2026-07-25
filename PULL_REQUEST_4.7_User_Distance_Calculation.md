# Pull Request: Feature 4.7 – Distanz-Berechnungen zwischen Nutzern

## Zusammenfassung
Dieses Pull Request führt das Modul zur Berechnung und dynamischen Formatierung der physischen Entfernung zwischen Nutzern im Kliq-Nachtleben-Netzwerk ein (Kapitel 4.7). Die Implementierung folgt strikt dem MVVM-Muster und Clean Architecture, trennt Berechnungs- und Formatierungslogik vollständig von der UI-Darstellung und aktualisiert die Werte performant bei Standortänderungen auf Hintergrund-Threads.

## Key Changes
1. **Domain UseCase (`CalculateUserDistanceUseCase`)**:
   - Haversine-Formel zur präzisen Berechnung der geografischen Distanz in Metern.
   - Vollständige Absicherung gegen Randfälle: $0\text{ m}$ bei identischen Standorten, `null`-Return bei fehlenden oder ungültigen Geokoordinaten (NaN / out-of-bounds).
   - Batch- und Overload-Methoden für Koordinaten-Paare, `LocationData`-Objekte und `UserDistanceResult`.

2. **Formatting Utility (`UserDistanceFormatter`)**:
   - Dynamischer Wechsel der Maßeinheiten:
     - Distanz $< 1000\text{ m}$: Rundung auf ganze Meter (z. B. `"150 m"`, `"0 m"`).
     - Distanz $\ge 1000\text{ m}$: Umrechnung in Kilometer mit einer gerundeten Nachkommastelle (z. B. `"1.2 km"`, `"15.4 km"`).
   - Unterstützung für Custom-Fallbacks bei fehlendem GPS-Signal (`"Entfernung unbekannt"`) sowie Badge- und Suffix-Formatierungen.

3. **Data Model Extensions & ViewModel Integration**:
   - `UserDistanceModels`: `UserDistanceResult`, `UserLocationSnapshot`, `NearbyUserDistance`.
   - `UserMarkerUiState`: Erweiterung um `distanceMeters: Double?` und `formattedDistance: String`.
   - `MapViewModel`: Anbindung an reactive `locationRepository.locationUpdates` Flow mit asynchroner Neuberechnung auf `Dispatchers.Default`.
   - `UserQuickViewCard`: Anzeige der aufbereiteten Entfernung im Profil-Overlay auf der Karte.

4. **Dependency Injection**:
   - Bereitstellung von UseCase und Formatter in `AppModule`.

5. **Testabdeckung**:
   - `CalculateUserDistanceUseCaseTest`: Präzisionstests, Identität, Koordinaten-Grenzen.
   - `UserDistanceFormatterTest`: Meter/Kilometer Umschaltung, Rundung, Fallback-Verhalten.
   - `UserDistanceIntegrationTest`: End-to-End Verifizierung der Reaktivität bei Standort-Updates im ViewModel.

## Verification Checklist
- [x] Branch `feature/user-distance-calculation` basiert auf aktuellem Stand.
- [x] Haversine Distanzberechnung mathematisch verifiziert.
- [x] Meter/Kilometer Formatter-Einheitenwechsel getestet.
- [x] Asynchrone Neuberechnung ohne Main-Thread-Blockade verifiziert.
- [x] Null-Transparenz-Regel eingehalten (keine KI/AI Referenzen).
