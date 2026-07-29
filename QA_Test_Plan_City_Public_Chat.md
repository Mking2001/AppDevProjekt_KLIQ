# QA Test Plan: Kapitel 6.3 - Stadt-basierter öffentlicher Chat

## Testumgebung
- **Plattform**: Native Android (Kotlin, Jetpack Compose)
- **Architektur**: MVVM, Room DB, LocationRepository, Hilt DI
- **Branch**: `feature/city-public-chat`

---

## Testfälle

### Testfall 1: Automatische Zuweisung des Stadt-Chats via GPS
- **Schritte**:
  1. Setze im Emulator die GPS-Koordinaten auf Berlin (52.5200, 13.4050).
  2. Öffne den Tab "Öffentliche Stadt-Chats" in der Kliq-App.
- **Erwartetes Ergebnis**: Der Header-Banner zeigt *"📍 Standorte-Chat: Berlin - Tonight"* mit geringer Kilometermeldung und "248 Feiernde online".

### Testfall 2: Manueller Stadt-Wechsel über Bottom Sheet
- **Schritte**:
  1. Tippe auf den Button *"Wechseln"* im Header-Banner.
  2. Wähle *"München - Party Radar"* aus der Liste des Bottom Sheets.
- **Erwartetes Ergebnis**: Das Bottom Sheet schließt sich. Der obere Chat-Eintrag wechselt sofort auf *"München - Party Radar"* und passt die Online-Mitgliederzahl an.

### Testfall 3: Empfangene Gruppen-Nachrichten & Sender-Identifikation
- **Schritte**:
  1. Öffne einen öffentlichen Stadt-Chat.
  2. Betrachte empfangene Nachrichten anderer Nutzer.
- **Erwartetes Ergebnis**: Jede empfangene Nachricht zeigt den Absendernamen in Lila (`PurplePrimaryLight`) oberhalb des Nachrichtentextes zur eindeutigen Wiedererkennung.
