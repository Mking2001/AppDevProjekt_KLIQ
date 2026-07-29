# QA-Test-Skript & Emulator-Anleitung: Kapitel 6.1 – Chat-Listen-Übersicht (Öffentlich/Privat)

**Projekt:** Kliq Native Mobile App (Android / Kotlin)  
**Modul:** Kapitel 6.1 – Chat-Listen-Übersicht (Öffentlich/Privat)  
**Architektur:** MVVM, Jetpack Compose, Room DB, Hilt Dependency Injection  
**Dokument-Typ:** Emulator Test-Skript & QA-Verifikations-Anleitung  
**Datum:** 29. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Dieses Test-Skript dient der direkten Überprüfung der **Chat-Listen-Übersicht** (Kapitel 6.1) im Android Emulator / Simulator. 
Es verifiziert:
1. **Mock-Daten-Setup**: Korrektes Laden und Sortieren von öffentlichen Stadt-Chats (z. B. *"Berlin - Tonight"*, *"München - Party Radar"*) und privaten 1-zu-1-Nachrichten mit unterschiedlichen Zeitstempeln und Ungelesen-Zuständen.
2. **Kategorie-Tab-Wechsel**: Flüssige Umschaltung zwischen den Tabs „Öffentliche Stadt-Chats“ und „Private Nachrichten“.
3. **Kliq Design & High-Contrast Style**: Korrektes Rendern der Avatar-Platzhalter mit Farbverlauf-Rahmen (öffentliche Stadt-Chats), Online-Präsenz-Indikatoren (Privatnachrichten) und Lila-Ungelesen-Badges (`unreadCount`).
4. **Interaktive Navigation**: Klick auf ein Chat-Item führt direkt zur zugehörigen `ChatDetailScreen`-Ansicht.

---

## 💻 2. Test-Umgebung & Vorbereitung

### Emulator-Spezifikationen
- **Gerät**: Android Studio Emulator Pixel 7 Pro / Pixel 6 (API 34 / Android 14).
- **Design-System**: High-Contrast Kliq Dark Mode (`#121212` Background / `#7C3AED` Purple Accent / `#22C55E` Online Green).
- **Vorbereitungs-Befehl (Unit & UI Tests)**:
  ```powershell
  $env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  ./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.ChatListViewModelTest"
  ```

---

## ⚙️ 3. Mock-Daten-Setup

Die Chat-Übersicht verwendet strukturiertes Mock-Seeding für Verifikation und Fallback-Tests:

### A. Öffentliche Stadt-Chats (`PUBLIC_CITY`)
| ID | Titel / Stadt | Letzte Nachricht | Zeitstempel | Ungelesen-Badge | Visueller Rahmen |
|---|---|---|---|---|---|
| `pub_1` | **Berlin - Tonight** | *"Heute ab 23 Uhr im Watergate! 🎶"* | vor 10 Min. (`now - 600.000ms`) | **5** (Ungelesen) | Purple Gradient Ring |
| `pub_2` | **München - Party Radar** | *"Hat jemand noch Tickets für Rote Sonne?"* | vor 1 Stunde (`now - 3.600.000ms`) | **12** (Ungelesen) | Purple Gradient Ring |
| `pub_3` | **Hamburg - Reeperbahn** | *"Line-up steht! Schaut mal rein 👀"* | vor 24 Stunden (`now - 86.400.000ms`) | **0** (Gelesen) | Purple Gradient Ring |

### B. Private Nachrichten (`PRIVATE`)
| ID | Name / Kontakt | Letzte Nachricht | Zeitstempel | Status | Ungelesen-Badge |
|---|---|---|---|---|---|
| `priv_1` | **Lisa W.** | *"Treffen wir uns vor dem Eingang?"* | vor 15 Min. (`now - 900.000ms`) | `ONLINE` (Grüner Punkt) | **2** (Ungelesen) |
| `priv_2` | **Max K.** | *"War ein geiler Abend! 🔥"* | vor 2 Stunden (`now - 7.200.000ms`) | `ONLINE` (Grüner Punkt) | **0** (Gelesen) |

---

## 🧪 4. Interaktiver Test-Ablauf im Emulator

### 🔹 Schritt 1: App starten & Chat-Übersicht aufrufen
1. Starte die Kliq-App im Emulator (`./gradlew installDebug` oder Run in Android Studio).
2. Tippe in der unteren Navigation auf das **Chat-Icon** (oder navigiere über den Home-Screen Button).
3. **Soll-Ergebnis**:
   - Die TopBar zeigt den Titel **„Chats“** und oben rechts das Lupe-Icon für die Suche.
   - Der Tab-Indikator steht standardmäßig auf **„Öffentliche Stadt-Chats“**.

---

### 🔹 Schritt 2: Umschalten zwischen Tabs "Öffentlich" und "Privat"
1. Tippe auf den Tab **„Private Nachrichten“**.
2. **Soll-Ergebnis**:
   - Die Tab-Unterstreichung animiert geschmeidig zum rechten Tab.
   - Die Liste aktualisiert sich sofort und zeigt die 1-zu-1-Chats (*Lisa W.*, *Max K.*).
3. Tippe zurück auf **„Öffentliche Stadt-Chats“**.
4. **Soll-Ergebnis**: Die Liste schaltet zurück auf die Stadt-Chats (*Berlin - Tonight*, *München - Party Radar*, *Hamburg - Reeperbahn*).

---

### 🔹 Schritt 3: Visual Design & High-Contrast Details prüfen
1. Im Tab **„Öffentliche Stadt-Chats“**:
   - Prüfe die Avatare: Ein zweifarbiger Lila-Farbverlauf (`Brush.linearGradient`) umrandet kreisförmig das Avatar-Icon mit dem Buchstaben-Initial.
   - Ungelesen-Badge: Bei *Berlin - Tonight* wird ein lila Kreis-Badge mit der Zahl **5** angezeigt; bei *München - Party Radar* die Zahl **12**.
2. Im Tab **„Private Nachrichten“**:
   - Prüfe den Online-Status: Am unteren rechten Rand des Avatars von *Lisa W.* und *Max K.* ist ein leuchtend grüner Punkt (`#22C55E`) für `UserStatus.ONLINE` sichtbar.

---

### 🔹 Schritt 4: Sortierung nach Aktualität validieren
1. Überprüfe die Reihenfolge im Tab **„Öffentliche Stadt-Chats“**:
   - Pos 1: *Berlin - Tonight* (vor 10 Min.)
   - Pos 2: *München - Party Radar* (vor 1 Std.)
   - Pos 3: *Hamburg - Reeperbahn* (vor 24 Std.)
2. Überprüfe die Reihenfolge im Tab **„Private Nachrichten“**:
   - Pos 1: *Lisa W.* (vor 15 Min.)
   - Pos 2: *Max K.* (vor 2 Std.)
3. **Soll-Ergebnis**: Alle Einträge sind strikt chronologisch absteigend nach dem Zeitstempel der letzten Nachricht sortiert.

---

### 🔹 Schritt 5: Chat-Navigation simulieren
1. Tippe im Tab „Private Nachrichten“ auf den Eintrag **„Lisa W.“**.
2. **Soll-Ergebnis**:
   - Die App navigiert nahtlos zur Detailansicht `ChatDetailScreen(chatId = "priv_1")`.
   - Die TopBar zeigt den Namen *„Lisa W.“* sowie deren Online-Status an.
3. Tippe oben links auf den **Zurück-Pfeil**.
4. **Soll-Ergebnis**: Die App kehrt zur Chat-Listen-Übersicht zurück, wobei der Tab-Zustand erhalten bleibt.

---

## 📱 5. Ausführungs-Anleitung im Emulator

1. **Emulator starten**:
   Open Android Studio -> Device Manager -> Select **Pixel 7 Pro API 34** -> Click **Play**.
2. **App bauen und ausführen**:
   ```powershell
   ./gradlew installDebug
   adb shell am start -n com.kliq.app/.MainActivity
   ```
3. **UI Integrationstest automatisieren**:
   ```powershell
   ./gradlew connectedAndroidTest --tests "com.kliq.app.ui.ChatListEmulatorTest"
   ```
