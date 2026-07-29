# QA-Test-Skript & Emulator-Anleitung: Kapitel 6.2 – UI für den Chat-Screen (Sprechblasen, Lila Design)

**Projekt:** Kliq Native Mobile App (Android / Kotlin)  
**Modul:** Kapitel 6.2 – UI für den Chat-Screen (Sprechblasen, Lila Design)  
**Architektur:** MVVM, Jetpack Compose, Hilt Dependency Injection  
**Dokument-Typ:** Emulator Test-Skript & QA-Verifikations-Anleitung  
**Datum:** 29. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Dieses Test-Skript führt Schritt für Schritt durch die Verifikation der **Chat-Detailansicht (ChatScreen / ChatDetailScreen)** auf dem Android Emulator:
1. **Mock-Daten-Setup**: Testreihe mit kurzen, mittleren und mehrzeiligen Nachrichten, unterschiedlichen Zeitstempeln und Absendern (eigene vs. empfangene Nachrichten).
2. **Sprechblasen-Layout & Kontraste**: Rechtsbündige ausgehende Nachrichten in Kliq Lila (`PurplePrimary`), linksbündige eingehende Nachrichten in dunklem Contrast-Surface (`surfaceVariant`) mit Sender-Namen im Lila-Farbton.
3. **Absenden & Autoscroll**: Sofortiges Rendern neu geschriebener Nachrichten und automatisches Smooth-Scrolling am Ende der Liste.
4. **Tastatur-Handling & Insets**: Nahtlose Ausrichtung der Eingabeleiste oberhalb der Bildschirmtastatur via `imePadding()`.
5. **Kliq Design & High-Contrast Style**: Validierung des Lila/Dark-Mode-Farbkonzepts auf Lesbarkeit und Kontrast.

---

## ⚙️ 2. Mock-Daten-Setup

Die Chat-Detailansicht verwendet folgendes strukturierte Testreihen-Setup:

| Msg-ID | Absender | Text-Typ & Inhalt | Zeitstempel | Ausrichtung | Hintergrund & Stil | Status-Icon |
|---|---|---|---|---|---|---|
| `msg_1` | **Du** (Eigen) | *Kurz*: „Hey Lisa! Kommst du heute Abend?“ | `14:15` | **Rechtsbündig** | Kliq Lila (`#7C3AED`), Weißer Text | `DoneAll` (Gelesen) |
| `msg_2` | **Lisa W.** (Empfangen) | *Mittel*: „Hey! Ja klar, freue mich schon 🥳“ | `14:18` | **Linksbündig** | Dark Surface Variant (`#262626`), Sender-Header | — |
| `msg_3` | **Lisa W.** (Empfangen) | *Mehrzeilig / Lang*: „Ich komme etwas später, weil der Bus Verspätung hat. Warte bitte auf mich!“ | `14:20` | **Linksbündig** | Dark Surface Variant (`#262626`), Sender-Header | — |
| `msg_4` | **Du** (Eigen) | *Mittel*: „Kein Problem, wir sichern uns einen Tisch am Fenster!“ | `14:22` | **Rechtsbündig** | Kliq Lila (`#7C3AED`), Weißer Text | `DoneAll` (Gelesen) |

---

## 🧪 3. Interaktiver UI-Test im Emulator

### 🔹 Schritt 1: Chat-Detailansicht öffnen & Sprechblasen prüfen
1. Starte die Kliq-App im Emulator.
2. Wechsle zur Chat-Übersicht und tippe auf die Konversation **„Lisa W.“**.
3. **Soll-Ergebnis**:
   - Die TopBar zeigt den Namen *„Lisa W.“* und einen leuchtend grünen Online-Status-Punkt (`#22C55E`).
   - Eigene Nachrichten (`isMine = true`) werden **rechtsbündig** in leuchtendem Kliq-Lila (`PurplePrimary`) gerendert.
   - Empfangene Nachrichten (`isMine = false`) werden **linksbündig** in dunklem SurfaceVariant gerendert, mit dem Namen *„Lisa W.“* in helles Lila oberhalb des Texts.

---

### 🔹 Schritt 2: Tastatur-Handling & Insets prüfen (`imePadding()`)
1. Tippe in das Textfeld *"Nachricht schreiben…"*.
2. **Soll-Ergebnis**:
   - Die Bildschirmtastatur schiebt sich von unten ein.
   - Die Eingabeleiste hebt sich dank `imePadding()` nahtlos mit der Tastatur an und verdeckt keine Nachrichten.
   - Das Eingabefeld hat abgerundete Ecken (`RoundedCornerShape(24.dp)`).

---

### 🔹 Schritt 3: Neue Nachricht absenden & Autoscroll verifizieren
1. Gib den Text ein: `"Ich stehe bereits vor dem Club! 🎟️"`
2. **Soll-Ergebnis**: Der runde Send-Button wechselt sanft seine Farbe zu leuchtend Lila.
3. Tippe auf den Send-Button (oder die Tastatur-Senden-Taste).
4. **Soll-Ergebnis**:
   - Die neue Nachricht wird **sofort** am Ende der Liste gerendert.
   - Das Eingabefeld wird geleert.
   - Die `LazyColumn` führt eine flüssige Smooth-Scroll-Animation (`listState.animateScrollToItem()`) am Ende der Liste aus.

---

### 🔹 Schritt 4: Farbkonzept & Lesbarkeits-Check (Dark Mode)
1. Überprüfe die Textkontraste:
   - Reinweißer Text auf Kliq-Lila-Hintergrund (ausgehende Nachrichten).
   - Hellgrauer Text auf dunklem Surface-Hintergrund (eingehende Nachrichten).
   - Subtile Datums-Trennlinie („Heute“) mit feinen Linien.
2. **Soll-Ergebnis**: Hohe Lesbarkeit ohne visuelle Artefakte oder Überlappungen.

---

## 📱 4. Terminal-Ausführungsbefehle

```powershell
# Unit Test suite ausführen
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.ChatDetailViewModelTest"

# Compose UI Integrationstest auf dem Emulator ausführen
./gradlew connectedAndroidTest --tests "com.kliq.app.ui.ChatDetailScreenEmulatorTest"
```
