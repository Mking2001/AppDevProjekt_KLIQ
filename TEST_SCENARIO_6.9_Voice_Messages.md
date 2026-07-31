# Test-Szenario & Manual Test-Script: Kapitel 6.9 Sprachnachrichten-Funktion (Kliq App)

## 1. Übersicht & Test-Ziel
Dieses Test-Script dient der manuellen und automatisierten Überprüfung der Sprachnachrichten-Funktion in der nativen Android-App **Kliq**.
Geprüft werden die Berechtigungsabfrage (`RECORD_AUDIO`), die sturzfreie Aufnahme im Chat-Screen (inkl. Live-Overlay, Timer & Wellenform), die Speicherung der Audiodatei im Cache, die Speicherung in der Room SQLite Datenbank (v16) sowie die fehlerfreie Wiedergabe und Slider-Steuerung im Kliq High-Contrast Dark-Mode.

---

## 2. Test-Vorbedingungen & Setup

1. **Test-Umgebung**:
   - **Android Emulator**: Pixel 6 / Pixel 7 (Android 13 / API 33 oder Android 14 / API 34).
   - **Audio-Input im Emulator**: In den Emulator-Einstellungen unter `Settings -> Microphone -> Virtual Microphone Uses Host Audio Input` auf `Enabled` stellen, um echte Audiodaten des Host-PCs einzuspeisen.
   - **Physisches Gerät**: Android Smartphone (ab Android 10, API 29+) mit funktionierendem Mikrofon und Lautsprecher.
2. **Build-Vorbereitung**:
   - App-Build auf dem Testgerät/Emulator installieren via Android Studio (`Run 'app'`) oder Terminal:
     ```powershell
     .\gradlew installDebug
     ```

---

## 3. Schritt-für-Schritt Manuelles Test-Szenario

### Test-Fall 1: Audio-Berechtigung (`RECORD_AUDIO`) beim Erstanruf
| Schritt | Aktion | Erwartetes Verhalten | Status |
| :--- | :--- | :--- | :--- |
| **1.1** | App neu installieren oder Berechtigungen zurücksetzen (`adb shell pm reset-permissions com.kliq.app`). | App startet sauber im Dark-Mode. | [ ] Pass |
| **1.2** | Zu einem Chat-Screen navigieren (z. B. Stadt-Gruppe "Berlin - Tonight" oder Privat-Chat). | Chat-Eingabeleiste zeigt bei leerem Textfeld das **Mikrofon-Icon** anstelle des Senden-Icons. | [ ] Pass |
| **1.3** | Auf das Mikrofon-Icon tippen. | Das Android-System-Dialogfenster zur Abfrage der Berechtigung `RECORD_AUDIO` ("Beim Nutzen der App") wird angezeigt. | [ ] Pass |
| **1.4** | Berechtigung erteilen. | Der Aufnahme-Modus startet sofort und das Live-Aufnahme-Overlay wird eingeblendet. | [ ] Pass |

---

### Test-Fall 2: Sprachaufnahme, Timer, Wellenform & Versenden
| Schritt | Aktion | Erwartetes Verhalten | Status |
| :--- | :--- | :--- | :--- |
| **2.1** | Mikrofon-Icon tippen, um eine Sprachaufnahme zu starten. | Die Eingabeleiste wechselt sanft animiert in das **Aufnahme-Overlay**: <br>- Pulsierender roter Indikator-Punkt.<br>- Timer startet bei `0:00` und zählt Sekunden hoch (`0:01`, `0:02` ...).<br>- Wellenform-Ausschläge bewegen sich analog zur Amplitudenlautstärke.<br>- Buttons zum Verwerfen (Mülleimer) und Senden (Papierflieger) sind sichtbar. | [ ] Pass |
| **2.2** | Für mindestens 5 Sekunden sprechen (`0:05` auf dem Timer erreichen). | Timer zeigt `0:05` oder höher an. Keine Hänger oder Abstürze im UI-Thread. | [ ] Pass |
| **2.3** | Auf den Senden-Button (lila Kreis mit Papierflieger) tippen. | - Die Aufnahme wird beendet und im Cache als `.m4a`-Datei gespeichert.<br>- Die Sprachnachricht wird als eigene Sprechblase (`VoiceMessageBubble`) im Chat-Verlauf gerendert.<br>- Der Chat scrollt automatisch zum Ende.<br>- Das Eingabefeld kehrt in den normalen Zustand zurück. | [ ] Pass |

---

### Test-Fall 3: Verwerfen einer Aufnahme (Cancel-Flow)
| Schritt | Aktion | Erwartetes Verhalten | Status |
| :--- | :--- | :--- | :--- |
| **3.1** | Aufnahme erneut starten und 3 Sekunden aufzeichnen. | Timer läuft auf `0:03`. | [ ] Pass |
| **3.2** | Auf das rote Mülleimer-Icon (Verwerfen) tippen. | - Die Aufnahme wird abgebrochen.<br>- Die temporäre `.m4a`-Datei wird unverzüglich vom Dateisystem gelöscht.<br>- Es wird **keine** Nachricht im Chat-Verlauf eingefügt. | [ ] Pass |

---

### Test-Fall 4: Dateisystem- & Datenbank-Überprüfung
| Schritt | Aktion | Erwartetes Verhalten | Status |
| :--- | :--- | :--- | :--- |
| **4.1** | Mit Android Studio Device File Explorer oder ADB den Cache-Ordner prüfen:<br>`/data/data/com.kliq.app/cache/chat_voice/` | Die aufgezeichnete `.m4a`-Datei existiert und hat eine Dateigröße > 0 Bytes. | [ ] Pass |
| **4.2** | App schließen und neu öffnen, denselben Chat laden. | Die Sprachnachricht wird dank Room-Datenbank (DB v16) mit identischer Audiodauer (`audioDurationMs`) aus dem lokalen SQLite-Speicher nachgeladen. | [ ] Pass |

---

### Test-Fall 5: Audiowiedergabe, Pause & Slider-Steuerung
| Schritt | Aktion | Erwartetes Verhalten | Status |
| :--- | :--- | :--- | :--- |
| **5.1** | Auf den Play-Button der Sprachnachrichten-Sprechblase tippen. | - Der Play-Button wechselt auf das Pause-Icon.<br>- Der Ton wird über den Lautsprecher/Headset abgespielt.<br>- Der Timer in der Sprechblase zählt die aktuelle Abspielposition hoch (`0:01`, `0:02`...).<br>- Der Fortschritts-Slider bewegt sich flüssig nach rechts. | [ ] Pass |
| **5.2** | Während der Wiedergabe auf das Pause-Icon tippen. | Der Ton stoppt unverzüglich, die aktuelle Position auf dem Slider bleibt stehen und das Icon wechselt zurück auf Play. | [ ] Pass |
| **5.3** | Auf Play tippen und den Slider manuell mit dem Finger/Mauszeiger verziehen (Seek). | Die Abspielposition springt exakt an den gewählten Zeitpunkt und setzt die Wiedergabe dort fort. | [ ] Pass |
| **5.4** | Die Nachricht bis zum Ende durchlaufen lassen. | - Nach Ablauf der Gesamtdauer stoppt die Wiedergabe automatisch.<br>- Der Button springt zurück auf Play.<br>- Die Positionsanzeige wird zurückgesetzt. | [ ] Pass |

---

## 4. Automatisierter UI-Test Execution Command

Der automatisierte Compose Instrumentation UI-Test kann direkt im Emulator ausgeführt werden:

```powershell
.\gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.VoiceMessageUITest
```
