# QA Checklist: Kapitel 6.1 - Chat-Listen-Übersicht (Öffentlich/Privat)

## 1. UI & Visual Requirements
- [x] High-Contrast Dark-Mode Design mit Lila Akzenten (`PurplePrimary`)
- [x] Kategorisierung nach „Öffentliche Stadt-Chats“ und „Private Nachrichten“
- [x] Avatar-Platzhalter mit Initial-Buchstaben
- [x] Farbverlauf-Rahmen bei öffentlichen Stadt-Chats
- [x] Grüner Online-Indikator bei aktiven 1-zu-1-Chats
- [x] Vorschau der letzten Nachricht mit Text-Abkürzung (`TextOverflow.Ellipsis`)
- [x] Formatierter Zeitstempel
- [x] Ungelesen-Badge mit Zähler und `99+` Überlaufbehandlung

## 2. Interaktion & Suche
- [x] Tab-Wechsel mit geschmeidiger Indikator-Animation
- [x] Dynamisches Suchfeld in der TopAppBar zum Filtern nach Titel & Nachrichten
- [x] Wisch-Geste zum Löschen und Archivieren mit Rückgängig-Snackbar
- [x] FloatingActionButton zum Erstellen eines neuen Chats
- [x] Navigation zu `ChatDetailScreen` beim Klick auf ein Chat-Item

## 3. Datenlogik & Sicherheit
- [x] Reaktive Filterung blockierter Nutzer aus den 1-zu-1-Chats
- [x] Leere Zustände (Empty States) bei leeren Listen oder ohne Suchtreffer
- [x] Abwärtskompatibilität der Datenmodelle (`ChatListItem` & `ChatConversation`)
