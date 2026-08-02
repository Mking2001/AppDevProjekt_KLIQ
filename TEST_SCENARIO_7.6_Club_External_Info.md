# Test Scenario: Kapitel 7.6 - Externe Club-Infos & Live-Öffnungszeiten

## Testziel
Verifikation der korrekten Live-Berechnung des Öffnungsstatus, der Darstellung externer Club-Daten im Kliq High-Contrast Dark-Mode Design und der fehlerfreien Ausführung von System-Intents für Website, Telefon und Navigation.

## Testschritte
1. **Club-Detailansicht aufrufen**: Navigiere zu einem Club (z. B. "Berghain / Panorama Bar").
2. **Live-Status Badge verifizieren**:
   - Prüfe die Statusanzeige (*"Jetzt geöffnet"*, *"Schließt bald"*, *"Geschlossen"*) basierend auf der aktuellen Systemuhrzeit.
3. **Wochenplan testen**:
   - Klicke auf die Öffnungszeiten-Zeile.
   - Verifiziere das Ausklappen des Wochenplans und die visuelle Hervorhebung des heutigen Wochentags.
4. **Externe Links & Intents testen**:
   - Klicke auf den Button "Website" -> Verifiziere das Öffnen des System-Browsers.
   - Klicke auf den Button "Anrufen" -> Verifiziere das Öffnen der Telefon-App.
   - Klicke auf das Standort-Icon -> Verifiziere das Öffnen der Maps/Navigations-App.

## Ergebnis
Alle Schritte wurden erfolgreich durchgeführt und in automatisierten Unit-Tests verifiziert.
