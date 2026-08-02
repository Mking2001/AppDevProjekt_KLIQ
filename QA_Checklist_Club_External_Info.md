# QA Checklist: Kapitel 7.6 - Integration von externen Club-Infos (Öffnungszeiten)

## 1. Funktionaler Test
- [x] Öffnen der Club-Detailansicht lädt externe Club-Informationen.
- [x] Der Live-Öffnungsstatus wird korrekt berechnet:
  - [x] "Jetzt geöffnet" (Grüner Badge) wenn Systemzeit innerhalb der Öffnungszeiten liegt.
  - [x] "Schließt bald" (Amber Badge) wenn die Schließung in ≤ 60 Minuten erfolgt.
  - [x] "Geschlossen" (Grau/Roter Badge) wenn außerhalb der Öffnungszeiten.
- [x] Die Adresse des Clubs wird vollständig angezeigt.
- [x] Das Ausklappen der Wochentags-Öffnungszeiten zeigt den Wochenplan mit Hervorhebung des aktuellen Tages.

## 2. Intent-Integration
- [x] Klick auf "Website" öffnet die externe URL im System-Browser.
- [x] Klick auf "Anrufen" öffnet die Telefon-App mit der Club-Nummer im Dialer.
- [x] Klick auf das Standort-Icon / "Route" öffnet die Kartennavigation.

## 3. Performance & Design
- [x] Dark Mode im Kliq High-Contrast Design mit violetten Akzenten.
- [x] Keine UI-Ruckler beim Ausklappen der Öffnungszeiten.
- [x] Offline-Verfügbarkeit dank Room-Caching.
