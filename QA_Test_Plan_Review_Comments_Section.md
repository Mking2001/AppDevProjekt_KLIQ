# QA Test-Szenario & Dokumentation: Kapitel 5.5 - Kommentarsektion für Bewertungen

**App:** Kliq Mobile App  
**Modul:** Review & Comment Section (`ReviewViewModel`, `ReviewRepository`, `ReviewCommentSection`)  
**Architektur:** MVVM Pattern (StateFlow, Jetpack Compose, Room SQL, Hilt DI)

---

## 🎯 Test-Szenarien & Simulationsabläufe

### Szenario 1: Anzeige einer leeren Kommentarsektion
* **Ausgangssituation**: Ein Nutzer-Profil ohne bisherige schriftliche Text-Reviews wird in der Datenbank aufgerufen.
* **Simulierter Ablauf**:
  1. Aufrufen des Profils via `loadCommentsForUser("user_no_comments")`.
  2. `ReviewViewModel` beobachtet den reaktiven `Flow` aus der Room-Datenbank.
* **Erwartetes Ergebnis**:
  - `ReviewViewModel` setzt `isSectionEmpty = true` und `commentReviews = emptyList()`.
  - Die UI rendert den stilvollen Platzhalter-Text im Kliq Dark-Mode (`#1E1B2E` Card Container, `#7C3AED` Purple Accent):
    `"Noch keine schriftlichen Kommentare vorhanden. Sei der Erste mit einer verifizierten Bewertung!"`
  - Das rote Anti-Spam Sperr-Banner weist den Nutzer auf die erforderliche Verifizierung hin.

---

### Szenario 2: Verfassen und Absenden eines Kommentars (Verifizierter Status)
* **Ausgangssituation**: Der Nutzer befindet sich am selben Club-Standort (`GPS_GEOFENCE_MATCH`) oder hat den QR-Code gescannt (`isVerificationLocked = false`).
* **Simulierter Ablauf**:
  1. Der Nutzer gibt Text in das Eingabefeld ein (`"Mega Club-Abend mit der Kliq Crew!"`).
  2. Auswahl von 5 Sternen.
  3. Klick auf den Absenden-Button ("Veröffentlichen").
* **Erwartetes Ergebnis**:
  - `ReviewViewModel` validiert die Eingabe und übergibt die Daten an `ReviewRepository.submitVerifiedUserComment(...)`.
  - Der Kommentar wird in der Room-Datenbank persistiert (`isVerified = true`).
  - Die Liste aktualisiert sich sofort und flüssig ohne Screen-Flackern.
  - Das Eingabefeld wird auf den Leerzustand zurückgesetzt und eine Erfolgsmeldung angezeigt.

---

### Szenario 3: Validierung der Zeichenbegrenzung (280 Zeichen Limit)
* **Ausgangssituation**: Der Nutzer versucht, einen Text einzugeben, der das Limit von 280 Zeichen überschreitet (z. B. 320 Zeichen).
* **Simulierter Ablauf**:
  1. Eingabe von 320 Zeichen im Textfeld.
* **Erwartetes Ergebnis**:
  - Die `ReviewViewModel.onCommentInputChanged`-Logik schneidet Eingaben in Echtzeit bei genau 280 Zeichen ab (`text.take(280)`).
  - Der Zeichenzähler signalisiert `"0 / 280 Zeichen übrig"` und wird rot hervorgehoben.
  - Ein Absenden von Texten mit > 280 Zeichen wird strikt blockiert.

---

## 💻 Ausführbare Test-Skripte

### 1. ViewModel & Repository Unit-Test-Skript
Das Test-Skript befindet sich in [`ReviewCommentsSectionScenarioTest.kt`](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/test/java/com/kliq/app/viewmodel/ReviewCommentsSectionScenarioTest.kt).

**Ausführung via Terminal**:
```bash
cmd /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& set PATH=%JAVA_HOME%\bin;%PATH%&& gradlew.bat testDebugUnitTest --tests com.kliq.app.viewmodel.ReviewCommentsSectionScenarioTest"
```

### 2. Emulator UI-Instrumentierungstest-Skript
Das UI-Test-Skript befindet sich in [`ReviewCommentsSectionEmulatorTest.kt`](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/ReviewCommentsSectionEmulatorTest.kt).

**Ausführung auf dem Emulator**:
```bash
cmd /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& set PATH=%JAVA_HOME%\bin;%PATH%&& gradlew.bat connectedDebugAndroidTest --tests com.kliq.app.ui.ReviewCommentsSectionEmulatorTest"
```
