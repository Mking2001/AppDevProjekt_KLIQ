# Test Scenario & Test Plan: Kapitel 7.4 - Suchfunktion für Clubs und Regionen

## Test-Fall 1: Live-Suche & Debouncing (300ms)
- **Ziel**: Überprüfen, dass Texteingaben in `ClubSearchBar` erst nach einer Verzögerung von 300 ms eine Abfrage an die Datenbank ausführen.
- **Schritte**:
  1. Öffne den `ClubSearchScreen`.
  2. Tippe schnell den Begriff "Berlin" ein.
  3. Verifiziere im ViewModel/Log, dass erst nach 300 ms ohne weitere Tastatureingabe der Suchstream emittiert wird.
- **Erwartetes Ergebnis**: Keine Mehrfach-Queries bei schnellem Tippen; Ergebnisse werden flüssig gerendert.

## Test-Fall 2: Filter-Badges Wechsel
- **Ziel**: Überprüfen, dass das Auswählen von Filter-Badges ("Nach Name", "Nach Region/Stadt", "Nach Genre/Vibe") die Suchergebnisse entsprechend einschränkt.
- **Schritte**:
  1. Wähle das Filter-Badge "Nach Region/Stadt".
  2. Gib "München" ein.
  3. Verifiziere, dass nur Clubs in München oder dem Landkreis München gelistet werden.
- **Erwartetes Ergebnis**: Die Ergebnisliste filtert exakt nach der ausgewählten Kategorie.

## Test-Fall 3: Empty-State bei ungültiger Region
- **Ziel**: Überprüfen der visuellen Platzhalter und des Empty-States ("Keine Clubs in dieser Region gefunden").
- **Schritte**:
  1. Gib eine ungültige Region ein (z. B. "XYZ123").
  2. Beobachte die UI-Reaktion.
- **Erwartetes Ergebnis**: Die Karte schaltet auf `ClubSearchEmptyState` um mit der Meldung "Keine Clubs in dieser Region gefunden" und Empfehlungen für andere Suchbegriffe.

## Test-Fall 4: Umkreissuche & GPS-Distanzberechnung
- **Ziel**: Überprüfen, dass bei vergebenen GPS-Koordinaten die Entfernung in Kilometern (z. B. "2.4 km entfernt") auf jeder Club-Karte angezeigt wird.
- **Schritte**:
  1. Simuliere GPS-Koordinaten des Nutzers.
  2. Führe eine Suche durch.
- **Erwartetes Ergebnis**: Die Club-Karten zeigen dynamisch berechnete Entfernungen in Kilometern an.
