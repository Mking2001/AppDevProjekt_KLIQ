# QA Checklist: Kapitel 6.3 - Stadt-basierter öffentlicher Chat

## 1. UI & Visual Requirements
- [x] High-Contrast Kliq Dark-Mode Design
- [x] `CityChatHeaderBanner` mit Anzeige von GPS-Stadt, Distanz in km und aktiver Feiernde-Anzahl
- [x] Farbverlauf-Rahmen (`Brush.linearGradient`) um öffentliche Stadt-Chat-Items
- [x] `CityChatSwitcherSheet` Modal Bottom Sheet zur manuellen Auswahl der Metropol-Chats
- [x] Sender-Name im Lila-Farbton bei empfangenen Nachrichten im Gruppen-Chat

## 2. Standort- & Logik-Anforderungen
- [x] Automatische GPS-Standortauflösung via `CityChatLocationMapper`
- [x] Korrekte Distanzberechnung in Kilometern
- [x] Fallback-Auflösung (Default: Berlin) bei fehlendem GPS-Signal
- [x] Raum für lokales Room DB Caching
