# QA Test Plan: Push-Benachrichtigungen für neue Chat-Nachrichten (Kapitel 6.5)

## 1. Testziel & Geltungsbereich
Dieser Testplan definiert die Verifikationsschritte für Push-Benachrichtigungen bei 1-zu-1 Direktnachrichten und Stadt-Chat-Erwähnungen sowie die Validierung des Deep-Linking-Routings in der nativen Kliq-App.

## 2. Test-Szenarien

### Testfall 1: Channel-Initialisierung beim App-Start
- **Vorbedingung**: App neu installiert / gestartet.
- **Schritte**:
  1. App starten.
  2. System-Einstellungen -> Apps -> Kliq -> Benachrichtigungen öffnen.
- **Erwartetes Ergebnis**:
  - Kanäle „Kliq Direct Messages“ und „Kliq City Chats“ sind vorhanden und aktiv.

### Testfall 2: Eingehende Direktnachricht-Push
- **Schritte**:
  1. FCM Data Payload mit `chat_id="chat_usr_456"`, `sender_name="Sarah M."`, `preview_text="Hi, bist du im Club?"` senden.
- **Erwartetes Ergebnis**:
  - Push-Benachrichtigung erscheint in der Statusleiste mit Kliq-Icon und Lila-Farbe.

### Testfall 3: Deep-Link Routing bei Klick auf Benachrichtigung
- **Schritte**:
  1. Auf die eingetroffene Benachrichtigung tippen.
- **Erwartetes Ergebnis**:
  - App öffnet sich und navigiert ohne Zwischenstopp direkt zum Chat-Screen von `chat_usr_456`.
