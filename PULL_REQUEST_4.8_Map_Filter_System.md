# Pull Request: Feature 4.8 – Map-Filter-System (Öffentliche Events vs. Private Standorte)

## Zusammenfassung
Dieses Pull Request führt das Map-Filter-System auf der nativen interaktiven Karte der Kliq-App ein (Kapitel 4.8). Die Erweiterung ermöglicht Nutzerinnen und Nutzern die nahtlose und reaktive Umschaltung zwischen öffentlichen Veranstaltungslokationen (Clubs, Bars, Events) und privaten Nutzer-Standorten im MVVM-Muster. Dabei werden strenge Datenschutz- und Standortfreigabeeinstellungen eingehalten.

## Key Changes

1. **Floating Filter-UI (`MapFilterSegmentedControl`)**:
   - Neues Segmented-Control-Bedienelement am oberen Bildschirmrand im Kliq-Lila High-Contrast Dark-Mode Design (`#7C3AED` / `#BB86FC`).
   - Drei Filter-Modi:
     - **Alle**: Simultaner Overlay-View von öffentlichen Clubs/Events und befreundeten Nutzern.
     - **Öffentlich**: Exklusive Darstellung von verifizierten Party-Locations, Club-Pins und Event-Clustern.
     - **Private Standorte**: Exklusive Ansicht aktiver Nutzer im Netzwerk.
   - Smooth Animate Transitions für Hintergründe und Icons.

2. **ViewModel State-Handling & Reactivity (`MapViewModel`)**:
   - Einführung von `MapLocationFilterMode` (`ALL`, `PUBLIC_ONLY`, `PRIVATE_ONLY`).
   - Erweiterung des `MapUiState` um `locationFilterMode`, `showPublicEvents` und `showPrivateLocations`.
   - Reaktive Update-Methoden `onLocationFilterModeSelected()` zur unmittelbaren Neuberechnung sichtbarer Marker und Cluster über `StateFlow`.

3. **Marker-Filter-Logik & Privacy Enforcement**:
   - **Öffentliche Lokationen**: Werden nur gerendert, wenn `showPublicEvents == true`. Zusätzliche Kategoriefilter ("Clubs", "Bars", "Events", "Restaurants") wirken sekundär.
   - **Private Standorte**: Werden nur gerendert, wenn `showPrivateLocations == true` **UND** der jeweilige Nutzer die Standortfreigabe aktiv erteilt hat (`isLocationSharingEnabled = true`).
   - Vollständiger Ausschluss von Nutzern ohne aktive Standortfreigabe direkt in der ViewModel-Datenfilterung zum Schutz der Privatsphäre.

4. **Integration in MapScreen (`MapScreen`)**:
   - Einbettung von `MapFilterSegmentedControl` im oberen Karten-Overlay.
   - Anpassung der QuickView-Cards (`MapQuickViewCard`, `UserQuickViewCard`), um Überlappungen mit den neuen Bedienelementen zu vermeiden.

5. **Testabdeckung (`MapFilterSystemTest`)**:
   - Unit-Tests zur Absicherung aller Filtermodi (`ALL`, `PUBLIC_ONLY`, `PRIVATE_ONLY`).
   - Testabdeckung für die Privatsphäre-Erzwingung bei deaktivierter Standortfreigabe.
   - Testabdeckung für die Kombination aus Location-Filter-Mode und Sub-Kategorie-Chips.

## Verification Checklist
- [x] Branch `feature/map-filters` basiert auf aktuellem Stand.
- [x] MVVM-Architekturmuster und StateFlow-Reaktivität eingehalten.
- [x] High-Contrast Dark-Mode Design im Kliq-Lila Farbschema umgesetzt.
- [x] Datenschutz- und Freigabeeinstellungen für private Standorte verifiziert.
- [x] Unit-Tests erfolgreich ausgeführt.
- [x] Null-Transparenz-Regel eingehalten (keine KI/AI-Referenzen).
