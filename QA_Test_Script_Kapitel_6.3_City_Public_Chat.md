# QA-Test-Skript & Emulator-Anleitung: Kapitel 6.3 – Stadt-basierter öffentlicher Chat

**Projekt:** Kliq Native Mobile App (Android / Kotlin)  
**Modul:** Kapitel 6.3 – Stadt-basierter öffentlicher Chat  
**Architektur:** MVVM, Jetpack Compose, Room DB, LocationRepository, Hilt DI  
**Dokument-Typ:** Emulator Test-Skript & QA-Verifikations-Anleitung  
**Datum:** 29. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Dieses Test-Skript führt Schritt für Schritt durch die Verifikation des **stadt-basierten öffentlichen Chats (City Public Chat)** auf dem Android Emulator:
1. **Mock-Daten & Standort-Setup**: Simulation verschiedener GPS-Standorte (Berlin, München, Hamburg) mit dynamisch zugewiesenen öffentlichen Gruppenchats.
2. **Automatische Zuweisung & Live-Empfang**: Verifikation, dass der zugehörige Stadt-Chat bei Standortänderung geladen wird und eingehende Gruppen-Nachrichten reaktiv gerendert werden.
3. **Absenden & Room DB Caching**: Testen des Absendens öffentlicher Nachrichten inklusive lokaler Room-Datenbank-Speicherung (`ChatDao`) und Offline-Caching bei Netzunterbrechungen.
4. **Broadcast & Offline-Verhalten**: Verifikation der Nachrichtensynchronisation beim Wechsel in den Offline-Modus und automatischer Wiederverbindung.

---

## ⚙️ 2. Mock-Daten & Standort-Setup

Das Szenario nutzt folgendes Standort- und Gruppenchat-Setup:

| Stadt / Region | GPS-Koordinaten (Lat, Lng) | Öffentlicher Gruppenchat | Initiale Feiernde-Anzahl | Vorschau Letzte Nachricht |
|---|---|---|---|---|
| **Berlin** | `52.5200, 13.4050` | **Berlin - Tonight** | `248` Feiernde | *„Heute ab 23 Uhr im Watergate! 🎶“* |
| **München** | `48.1351, 11.5820` | **München - Party Radar** | `184` Feiernde | *„P1 Club Warm-up ab 22 Uhr! 🍾“* |
| **Hamburg** | `53.5511, 9.9937` | **Hamburg - Reeperbahn** | `192` Feiernde | *„Treffpunkt Reeperbahn 15 um 23:30!“* |

---

## 🧪 3. Interaktiver Funktionstest im Emulator

### 🔹 Schritt 1: Standortsimulation (Berlin vs. München) & Auto-Assignment
1. Starte die Kliq-App im Android-Emulator.
2. Öffne die Emulator-Standortsteuerung (`...` Extended Controls -> Location) und gib die Koordinaten für **Berlin** ein (`52.5200, 13.4050`).
3. Navigiere in der Kliq-App zum Tab **"Öffentliche Stadt-Chats"**.
4. **Soll-Ergebnis**:
   - Das `CityChatHeaderBanner` zeigt: **„📍 Berlin - Tonight“**.
   - Die Zusatzzeile meldet: **„⚡ 248 Feiernde online • 0.0 km entfernt“**.
5. Ändere die GPS-Koordinaten auf **München** (`48.1351, 11.5820`).
6. **Soll-Ergebnis**:
   - Die App reagiert auf den Standortwechsel; der Banner aktualisiert sich automatisch auf **„📍 München - Party Radar“**.

---

### 🔹 Schritt 2: Empfang von Live-Nachrichten anderer Gruppenmitglieder
1. Öffne den Chat **„München - Party Radar“**.
2. Simuliere das Eintreffen einer neuen Nachricht von *Max K.* (`usr_1`).
3. **Soll-Ergebnis**:
   - Die Nachricht erscheint reaktiv am Ende der Liste.
   - Der Absender-Name *„Max K.“* steht in Lila (`PurplePrimaryLight`) oberhalb des Nachrichtentexts zur schnellen Wiedererkennung.
   - In der Hauptübersicht wird der Unread-Badge erhöht und die Nachricht als neueste Vorschau gerendert.

---

### 🔹 Schritt 3: Absenden einer öffentlichen Nachricht & Room DB Speicherung
1. Tippe in das Eingabefeld *"Nachricht schreiben…"*.
2. Gib den Text ein: `"Wer ist später im P1 dabei? 🥂"` und tippe auf Senden.
3. **Soll-Ergebnis**:
   - Die Nachricht wird sofort rechtsbündig in Kliq Lila (`#7C3AED`) gerendert.
   - Das System schreibt den Eintrag synchron in die lokale Room-Datenbank (`MessageEntity` & `ChatEntity`), sodass der Zustand beim App-Neustart erhalten bleibt.

---

### 🔹 Schritt 4: Offline-Caching & Netzwerkunterbrechung
1. Aktiviere den Flugmodus (Offline Simulation) im Emulator.
2. Sende eine Nachricht ab: `"Bin im Club ohne Empfang 📱"`.
3. **Soll-Ergebnis**:
   - Die Nachricht wird lokal in Room mit Status `SENT` gecacht.
   - Es tritt kein App-Crash auf; die Nachrichtenliste bleibt vollständig lesbar aus dem lokalen Room DB Cache.
4. Deaktiviere den Flugmodus.
5. **Soll-Ergebnis**: Die Nachricht wird automatisch mit dem Netzwerk synchronisiert und der Status auf `READ` aktualisiert.

---

## 📱 4. Terminal-Ausführungsbefehle

```powershell
# Unit Test suite für Location Mapping & ViewModel ausführen
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.util.CityChatLocationMapperTest" --tests "com.kliq.app.viewmodel.CityPublicChatViewModelTest"

# Compose UI Integrationstest für den City Public Chat auf dem Emulator ausführen
./gradlew connectedAndroidTest --tests "com.kliq.app.ui.CityPublicChatEmulatorTest"
```
