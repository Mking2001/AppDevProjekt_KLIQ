# QA Checklist: Kapitel 6.2 - UI für den Chat-Screen (Sprechblasen, Lila Design)

## 1. UI & Visual Requirements
- [x] High-Contrast Kliq Lila/Dark-Mode Design
- [x] Ausgehende Nachrichten (eigene) rechtsbündig mit lila Akzent (`PurplePrimary`), asymmetrisch abgerundeten Ecken und weißem Text
- [x] Eingehende Nachrichten linksbündig in dunklem SurfaceVariant-Ton mit Sender-Name im Lila-Farbton
- [x] Zeitstempel (HH:mm) und Gelesen-Status-Checkmarks (`DoneAll` / `Done`)
- [x] Datum-Trennlinien (`ChatDateDivider`) für Tages-Abschnitte
- [x] Stylischer Send-Button mit Farbanimation bei Text-Eingabe
- [x] Abgerundete Eingabeleiste mit `imePadding()` für nahtloses Tastatur-Handling

## 2. Autoscroll & Interaktion
- [x] Automatisches Autoscrolling zur neusten Nachricht beim Öffnen des Screens
- [x] Automatisches Smooth-Scrolling beim Absenden einer neuen Nachricht
- [x] Tastatur-Handling ohne Verdecken von Nachrichteninhalten
