# QA Checklist: Kapitel 6.8 - Who's Online Anzeige in Gruppenchats

## Testfälle & Akzeptanzkriterien

### 1. Chat Header Online-Anzeige
- [x] **TC-6.8.1**: Öffnen des Stadtchats "Berlin - Tonight" zeigt die Gesamtzahl online aktiver Nutzer an (z. B. "🟢 248 online").
- [x] **TC-6.8.2**: Der Präsenz-Badge am Header leuchtet neon-grün und signalisiert den Live-Zustand.

### 2. Ausklappbare Teilnehmerliste
- [x] **TC-6.8.3**: Klick auf den Chat-Header öffnet die ausklappbare Teilnehmerliste (`GroupPresenceParticipantSheet`).
- [x] **TC-6.8.4**: Die Teilnehmerliste hebt Mitglieder mit Avataren, Präsenz-Badges, Rollen (`HOST`, `MOD`, `VIP`) und Distanz hervor.
- [x] **TC-6.8.5**: Die Suchleiste filtert Teilnehmer in Echtzeit nach Namenseingabe.

### 3. Eigenen Präsenz-Status verändern
- [x] **TC-6.8.6**: Auswahl von "Abwesend" oder "Invisible" aktualisiert den eigenen Status und reflektiert die Änderung in der Online-Anzahl.

### 4. Unit Tests & Architektur
- [x] **TC-6.8.7**: `GroupPresenceViewModelTest` durchgeführt – 100 % Bestanden.
- [x] **TC-6.8.8**: `GroupPresenceRepositoryTest` durchgeführt – 100 % Bestanden.
- [x] **TC-6.8.9**: MVVM-Architektur und High-Contrast Lila/Dark-Mode-Design konform.
