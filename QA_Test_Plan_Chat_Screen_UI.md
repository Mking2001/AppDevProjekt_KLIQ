# QA Test Plan: Kapitel 6.2 - UI für den Chat-Screen (Sprechblasen, Lila Design)

## Testumgebung
- **Plattform**: Native Android (Kotlin, Jetpack Compose)
- **Architektur**: MVVM (Model-View-ViewModel) + Hilt DI
- **Branch**: `feature/chat-screen-ui`

---

## Testfälle

### Testfall 1: Visuelle Ausrichtung der Sprechblasen & Status-Indikatoren
- **Schritte**:
  1. Öffne die Chat-Detailansicht mit einem Kontakt (z. B. „Lisa W.“).
  2. Überprüfe die Ausrichtung eigener (ausgehender) Nachrichten.
  3. Überprüfe die Ausrichtung fremder (eingehender) Nachrichten.
- **Erwartetes Ergebnis**: Eigene Nachrichten sind rechtsbündig in Kliq-Lila mit weißem Text, Zeitstempel und Doppel-Checkmark dargestellt. Fremde Nachrichten sind linksbündig in dunklem Kontrastton mit Sender-Name dargestellt.

### Testfall 2: Tastatur-Einblendung & Autoscroll
- **Schritte**:
  1. Tippe in das Textfeld *"Nachricht schreiben…"*.
  2. Gib den Text *"Bin gleich da!"* ein.
  3. Tippe auf den Send-Button oder den Tastatur-Send-Key.
- **Erwartetes Ergebnis**: Die Tastatur wird eingeblendet, ohne die Eingabeleiste zu überlagern (`imePadding()`). Beim Senden animiert die Liste automatisch zum neuen Nachrichteneintrag (`animateScrollToItem()`), und das Eingabefeld wird geleert.

### Testfall 3: Datum-Trennlinien (Date Dividers)
- **Schritte**:
  1. Scrolle durch die Nachrichtenliste.
- **Erwartetes Ergebnis**: Abschnitte unterschiedlicher Tage sind durch zentrierte Datums-Pills (z. B. "Heute") getrennt.
