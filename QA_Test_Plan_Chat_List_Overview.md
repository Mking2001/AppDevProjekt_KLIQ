# QA Test Plan: Kapitel 6.1 - Chat-Listen-Übersicht (Öffentlich/Privat)

## Testumgebung
- **Plattform**: Native Android (Kotlin, Jetpack Compose)
- **Architektur**: MVVM (Model-View-ViewModel) + Hilt DI
- **Branch**: `feature/chat-list-overview`

---

## Testfälle

### Testfall 1: Tab-Navigation (Öffentlich vs. Privat)
- **Schritte**:
  1. Öffne die Chat-Listen-Übersicht.
  2. Klicke auf den Tab „Private Nachrichten“.
  3. Klicke zurück auf den Tab „Öffentliche Stadt-Chats“.
- **Erwartetes Ergebnis**: Der untere Lila-Indikator wechselt flüssig. Die angezeigte Liste aktualisiert sich sofort auf die jeweilige Kategorie.

### Testfall 2: Echtzeit-Suchfunktion
- **Schritte**:
  1. Klicke auf das Lupe-Icon in der TopBar.
  2. Gib den Suchbegriff „Berlin“ ein.
  3. Lösche die Eingabe mit dem Clear-Button.
  4. Beende die Suche mit dem Zurück-Pfeil.
- **Erwartetes Ergebnis**: Während der Eingabe werden nur relevante Chats angezeigt. Nach dem Löschen/Beenden erscheint wieder die vollständige Liste.

### Testfall 3: Ausfilterung blockierter Nutzer
- **Schritte**:
  1. Blockiere den Nutzer „Lisa W.“ über das Nutzerprofil.
  2. Navigiere zur Chat-Listen-Übersicht im Tab „Private Nachrichten“.
- **Erwartetes Ergebnis**: Der 1-zu-1-Chat mit „Lisa W.“ wird automatisch aus der Liste gefiltert.

### Testfall 4: Wisch-Geste & Undo-Mechanismus
- **Schritte**:
  1. Wische ein Chat-Item nach links oder rechts.
  2. Tippe im erscheinenden Aktionen-Menü auf „Löschen“.
  3. Tippe in der erscheinenden Snackbar auf „Rückgängig“.
- **Erwartetes Ergebnis**: Das Item verschwindet beim Löschen und wird durch Tippen auf „Rückgängig“ an exakt gleicher Stelle wiederhergestellt.
