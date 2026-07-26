# QA Test Plan & Scenario: Feature 5.2 - Sterne-Rating-System

## 📋 Überblick
Dieses Dokument beschreibt das vollständige Test-Szenario, die Test-Abdeckung sowie die Schritt-für-Schritt-Anleitung zur Verifikation des **Sterne-Rating-Systems (Kapitel 5.2)** der Kliq Native Mobile App (Jetpack Compose / MVVM).

---

## 🎯 Test-Szenario Abdeckung

| Test-Fall ID | Test-Name | Ziel & Abdeckung | Erwartetes Ergebnis |
| :--- | :--- | :--- | :--- |
| **TC-5.2-01** | **Initialer Zustand** | Prüfen, ob die Komponente im leeren Zustand lädt (0 Sterne). | 5 graue Sternen-Outlines sichtbar. Absende-Button ist deaktiviert mit Text *"Stern auswählen zum Absenden"*. |
| **TC-5.2-02** | **Interaktions- & Gesten-Test** | Stern-Auswahl per Tippen (3 Sterne -> 5 Sterne). | Exakt 3 Sterne leuchten auf (Kliq-Gold/Lila High-Contrast Design). Beim Tippen auf den 5. Stern aktualisiert sich der Zustand flüssig auf 5 Sterne. |
| **TC-5.2-03** | **Text-Eingabe & Success State** | Kommentar eingeben (z. B. "Tolle Stimmung!") & Absenden. | Zeichenzähler aktualisiert sich (*"16 / 300 Zeichen"*). Button wird aktiviert. Beim Klick wird kurz ein Lade-Spinner (`CircularProgressIndicator`) angezeigt, danach schaltet die UI in den Erfolgs-State mit Häkchen & *"Vielen Dank für deine Bewertung!"*. |
| **TC-5.2-04** | **Error-Handling** | Simulieren eines Repository-/Netzwerkfehlers. | Die UI fängt den Fehler ab und blendet ein rotes Error-Banner mit verständlicher Fehlermeldung ein. |

---

## 🖥️ Schritt-für-Schritt-Anleitung zur Ausführung im Emulator

### Option A: Automatisierte UI-Tests auf dem Emulator ausführen

1. **Emulator starten**:
   - Öffne Android Studio -> **Device Manager** -> Starte ein AVD (z. B. *Pixel 6*, API 34 / Android 14).

2. **Automatisierte UI-Tests ausführen**:
   - In Android Studio: Rechtsklick auf `StarRatingSystemE2ETest.kt` (`app/src/androidTest/java/com/kliq/app/ui/components/StarRatingSystemE2ETest.kt`) -> **Run 'StarRatingSystemE2ETest'**.
   - Oder via PowerShell / Terminal:
     ```powershell
     $env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
     .\gradlew connectedAndroidTest --tests "com.kliq.app.ui.components.StarRatingSystemE2ETest"
     ```

---

### Option B: Manuelle Verifikation via QA Test-Bench Screen

Für eine interaktive Live-Verifikation im Emulator oder Compose-Preview wurde der `StarRatingTestScreen` erstellt.

1. **Preview in Android Studio**:
   - Öffne [`StarRatingTestScreen.kt`](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/rating/StarRatingTestScreen.kt).
   - Klicke auf **Split** / **Design**, um das interaktive Preview-Fenster zu aktivieren.

2. **Manuelle Test-Schritte im Emulator**:
   - **Schritt 1**: Klicke auf **"Rating Bottom Sheet öffnen"**.
   - **Schritt 2**: Verifiziere, dass 0 Sterne ausgewählt sind und der Button *"Stern auswählen zum Absenden"* deaktiviert ist.
   - **Schritt 3**: Tippe auf den **3. Stern**. Prüfe, dass 3 Sterne aufleuchten und der Button zu *"Bewertung absenden"* wechselt.
   - **Schritt 4**: Wische mit der Maus / dem Finger über die Sterne bis zum **5. Stern**. Prüfe die visuelle Animation.
   - **Schritt 5**: Gib den Text *"Super Party-Erlebnis!"* ein. Prüfe die Zeichenzählung (`22 / 300`).
   - **Schritt 6**: Klicke auf **"Bewertung absenden"**. Beobachte den Lade-Spinner und die grüne Erfolgsbestätigung.
   - **Schritt 7**: Wähle in der Test-Konfiguration *"Repository-Fehler simulieren"* und wiederhole den Absendevorgang. Prüfe das rote Error-Banner.

---

## 📜 Automatisierter Test-Code Index

- **E2E UI Test-Suite**: [`StarRatingSystemE2ETest.kt`](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/components/StarRatingSystemE2ETest.kt)
- **Component Unit Test-Suite**: [`InteractiveStarRatingTest.kt`](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/components/InteractiveStarRatingTest.kt)
- **ViewModel Unit Test-Suite**: [`RatingViewModelTest.kt`](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/app/src/test/java/com/kliq/app/viewmodel/RatingViewModelTest.kt)
- **QA Test-Bench Screen**: [`StarRatingTestScreen.kt`](file:///c:/Users/1312m/OneDrive/Dokumente/GitHub/AppDevProjekt_KLIQ/app/src/main/java/com/kliq/app/ui/screens/rating/StarRatingTestScreen.kt)
