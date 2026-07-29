# QA-Test-Skript & Emulator-Anleitung: Kapitel 6.3 – Stadt-basierter öffentlicher Chat

**Projekt:** Kliq Native Mobile App (Android / Kotlin)  
**Modul:** Kapitel 6.3 – Stadt-basierter öffentlicher Chat  
**Architektur:** MVVM, Jetpack Compose, Room DB, LocationRepository, Hilt DI  
**Dokument-Typ:** Emulator Test-Skript & QA-Verifikations-Anleitung  
**Datum:** 29. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Dieses Test-Skript führt Schritt für Schritt durch die Verifikation des **stadt-basierten öffentlichen Chats (City Public Chat)** auf dem Android Emulator:
1. **Automatische GPS-Zuweisung**: Verifikation, dass GPS-Koordinaten im Emulator direkt dem passenden Stadt-Chat zugewiesen werden.
2. **City Banner & Entfernungsanzeige**: Überprüfung der Header-Karte (`CityChatHeaderBanner`) mit Distanz in km und aktiven Feiernden.
3. **Manuelles Stadt-Switching**: Test des Modal Bottom Sheets (`CityChatSwitcherSheet`) zum Wechseln zwischen Berlin, München, Hamburg, Köln und Frankfurt.
4. **Gruppen-Sender-Identifikation**: Überprüfung der Absender-Namen und Avatare in den empfangenen Nachrichten des öffentlichen Stadt-Chats.

---

## 💻 2. Emulator Test-Ablauf

### 🔹 Schritt 1: GPS-Standort setzen & Stadt-Banner prüfen
1. Starte die Kliq-App im Android-Emulator.
2. Öffne die Extended Controls des Emulators (`...` Menu -> Location) und setze die Koordinaten auf **Lat: 52.5200, Lng: 13.4050** (Berlin).
3. Wechsle in Kliq zum Tab **"Öffentliche Stadt-Chats"**.
4. **Soll-Ergebnis**:
   - Die `CityChatHeaderBanner`-Karte zeigt an: **„Berlin - Tonight“**.
   - Die Zusatzzeile meldet: **„⚡ 248 Feiernde online • 0.0 km entfernt“**.

---

### 🔹 Schritt 2: Manuellen Stadt-Wechsel durchführen
1. Tippe im Header-Banner auf den Button **„Wechseln“**.
2. **Soll-Ergebnis**: Das Bottom Sheet `CityChatSwitcherSheet` schiebt sich von unten ein und listet alle unterstützten Metropolen (Berlin, München, Hamburg, Köln, Frankfurt) mit deren aktiven Feiernden auf.
3. Tippe auf **„Hamburg - Reeperbahn“**.
4. **Soll-Ergebnis**:
   - Das Bottom Sheet schließt sich.
   - Der zugewiesene Stadt-Chat wechselt sofort auf **„Hamburg - Reeperbahn“**.
   - Der Chat-Eintrag wird oberhalb in der Liste platziert.

---

### 🔹 Schritt 3: Öffentlichen Stadt-Chat betreten & Nachrichten prüfen
1. Tippe auf den Eintrag **„Hamburg - Reeperbahn“** (oder **„Berlin - Tonight“**).
2. **Soll-Ergebnis**:
   - Der Gruppen-Chat wird geöffnet.
   - Bei Nachrichten anderer Nutzer wird der Absender-Name in Lila (`PurplePrimaryLight`) hervorgehoben.
   - Eigene gesendete Nachrichten erscheinen rechtsbündig in Kliq Lila (`#7C3AED`).

---

## 📱 3. Ausführungs-Befehle im Terminal

```powershell
# Unit Test suite für Location Mapping & ViewModel ausführen
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.util.CityChatLocationMapperTest" --tests "com.kliq.app.viewmodel.CityPublicChatViewModelTest"

# Compose UI Integrationstest für den City Public Chat auf dem Emulator ausführen
./gradlew connectedAndroidTest --tests "com.kliq.app.ui.CityPublicChatEmulatorTest"
```
