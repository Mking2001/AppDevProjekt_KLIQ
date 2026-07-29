# QA-Test-Skript & Emulator-Anleitung: Kapitel 6.2 – UI für den Chat-Screen (Sprechblasen, Lila Design)

**Projekt:** Kliq Native Mobile App (Android / Kotlin)  
**Modul:** Kapitel 6.2 – UI für den Chat-Screen (Sprechblasen, Lila Design)  
**Architektur:** MVVM, Jetpack Compose, Hilt Dependency Injection  
**Dokument-Typ:** Emulator Test-Skript & QA-Verifikations-Anleitung  
**Datum:** 29. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Dieses Test-Skript führt Schritt für Schritt durch die Verifikation der **Chat-Detailansicht (ChatScreen / ChatDetailScreen)** auf dem Android Emulator:
1. **Sprechblasen-Layout & Kontraste**: Rechtsbündige ausgehende Nachrichten in Kliq Lila (`PurplePrimary`), linksbündige eingehende Nachrichten in dunklem Contrast-Surface (`surfaceVariant`) mit Sender-Namen.
2. **Zeitstempel & Checkmarks**: Zeitangabe HH:mm und Doppel-Checkmark (`DoneAll`) bei gelesenen eigenen Nachrichten.
3. **Autoscroll**: Flüssiges automatisches Scrollen zur neusten Nachricht beim Öffnen, Tastatureinblendung und Senden.
4. **Input Bar & Tastatur-Handling**: Abgerundete Eingabeleiste mit Farbanimation des Send-Buttons und `imePadding()`.

---

## 💻 2. Emulator Test-Ablauf

### 🔹 Schritt 1: Chat-Detailansicht öffnen
1. Starte die Kliq-App im Emulator.
2. Wechsle zur Chat-Übersicht und tippe auf die Konversation **„Lisa W.“**.
3. **Soll-Ergebnis**:
   - Die TopBar zeigt den Namen *„Lisa W.“* und einen leuchtend grünen Online-Status-Punkt.
   - Der Nachrichtenverlauf schiebt sich flüssig auf den Schirm.

---

### 🔹 Schritt 2: Sprechblasen & Datum-Trennlinien prüfen
1. Betrachte die erste Nachricht von *Lisa W.*:
   - Sie ist **linksbündig** in einem dunklen SurfaceVariant-Bubble platziert.
   - Oberhalb des Texts steht der Name *„Lisa W.“* in Kliq Lila.
2. Betrachte deine eigene Nachricht darunter:
   - Sie ist **rechtsbündig** in einem leuchtend lila Bubble (`PurplePrimary`) platziert.
   - Der Text ist reinweiß für maximalen Kontrast.
   - Neben der Uhrzeit ist ein blau/lila Doppel-Häkchen Icon (`DoneAll`) für den Gelesen-Status sichtbar.
3. Überprüfe die Datums-Trennlinie („Heute“) zwischen den Nachrichtengruppen.

---

### 🔹 Schritt 3: Nachricht schreiben, Tastatur & Autoscroll prüfen
1. Tippe in das Eingabefeld *"Nachricht schreiben…"*.
2. **Soll-Ergebnis**: Die Tastatur wird eingeblendet; die Eingabeleiste schiebt sich dank `imePadding()` nahtlos über die Tastatur.
3. Gib den Text ein: `"Super, ich freue mich schon auf später! 🎉"`
4. **Soll-Ergebnis**: Der Send-Button wechselt sanft seine Farbe zu leuchtend Lila (`PurplePrimary`).
5. Tippe auf den Send-Button.
6. **Soll-Ergebnis**:
   - Die Nachricht wird sofort am Ende der Liste angefügt.
   - Das Eingabefeld wird geleert.
   - Die Liste führt eine flüssige Smooth-Scroll-Animation (`listState.animateScrollToItem()`) durch, sodass die neuste Nachricht sofort vollständig sichtbar ist.

---

## 📱 3. Ausführungs-Befehle im Terminal

```powershell
# Unit Test suite ausführen
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.ChatDetailViewModelTest"

# Compose UI Integrationstest auf dem Emulator ausführen
./gradlew connectedAndroidTest --tests "com.kliq.app.ui.ChatDetailScreenEmulatorTest"
```
