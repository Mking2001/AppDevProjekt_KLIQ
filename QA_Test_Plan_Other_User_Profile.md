# QA-Test-Plan & Emulator-Anleitung: Kapitel 5.1 – Profil-Detailansicht für andere Nutzer

**Projekt:** Kliq Mobile App  
**Modul:** User Profile System (`OtherUserProfileScreen`, `OtherUserProfileViewModel`, `OtherUserProfileUiIntegrationTest`)  
**Dokument-Typ:** Qualitätssicherungs-Spezifikation & Emulator-Test-Anleitung  
**Datum:** 26. Juli 2026  

---

## 📌 1. Überblick & Test-Ziele

Diese Test-Spezifikation und Emulator-Anleitung beschreibt die systematische Verifikation des Moduls **Kapitel 5.1: Profil-Detailansicht für andere Nutzer** für die Kliq Android-App im MVVM-Architekturmuster. Ziel ist die Überprüfung von:

1. **Initialer Ladezustand & State Initialization**:
   - Korrektes Rendering des Lade-Indikators in Kliq High-Contrast-Farben.
   - Reaktives Laden der Benutzerdaten aus dem Repository (`UserRepository` & `ReviewRepository`).
2. **Dynamic UI & Content-Check**:
   - Vollständige Anzeige aller Profilfelder (Name, Alter, Heimatstadt, Bio, Verifizierungs-Badge).
   - **Intent-Matching Badge**: Korrekte Farbhervorhebung und Icon-Darstellung für "Freunde", "Dating / Liebe" oder "Beides".
   - **Lifestyle-Indikatoren**: Visuell abgegrenzte Badges für Konsumgewohnheiten (Rauch- und Trinkverhalten).
   - **Reputation-Header**: Prominente Anzeige der 5-Sterne-Skala, des mathematischen Durchschnitts (z. B. `4.8 ★`) sowie der Gesamtzahl abgegebener Bewertungen.
   - **Robuste Fehlerbehandlung**: Fehlende optionale Daten (z. B. leere Bio oder kein Profilbild) dürfen zu keinen Layout-Sprüngen, UI-Jitter oder App-Crashes führen.
3. **Interaktions-Check**:
   - **Bewerten**: Klick auf "Nutzer bewerten" öffnet das interactive Rating Bottom Sheet mit 5-Sterne-Selektor und Kommentarfeld.
   - **Melden & Blockieren**: Klick auf den Optionen-Button öffnet das Overflow-Menü zum Melden von Profilen und Blockieren des Nutzers inklusive dynamischer Statusanzeige.

---

## 💻 2. Test-Umgebung & Vorbereitung

### Emulator Setup
- **Android Studio Emulator**: Pixel 7 Pro (API 34 / Android 14) oder Pixel 6 (API 33).
- **Design Mode**: Dark Theme High-Contrast (#121212 / Kliq Purple #7C3AED).

### Automated Test Execution Command
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.OtherUserProfileViewModelTest"
```

---

## 🧪 3. Schritt-für-Schritt Emulator Test-Szenarien

### 🔹 Szenario 1: Profilaufruf & Data-Binding Check
1. Starte die Kliq App im Android Studio Emulator.
2. Navigiere zu einem Nutzerprofil (z. B. durch Antippen eines Nutzer-Markers auf der Karte oder eines Chat-Partners).
3. **Erwartetes Ergebnis**:
   - Während des Datenabrufs zeigt der Screen zentriert einen lila Lade-Indikator (`CircularProgressIndicator`).
   - Sobald die Daten geladen sind, erscheint das Profil flackerfrei im Kliq High-Contrast Design.
   - Der Avatar zeigt einen Lila/Teal-Farbverlauf-Rand. Bei verifizierten Profilen erscheint das `Verified`-Icon in der Namenszeile.
   - Der Name, das Alter (z. B. `24 Jahre`), die Heimatstadt (`München 📍`) und die Bio werden lesbar in weißer/hellgrauer Schrift gerendert.

---

### 🔹 Szenario 2: Intent-Matching & Lifestyle Indicators Check
1. Scrolle zum Abschnitt **Suchabsicht**.
2. **Erwartetes Ergebnis**:
   - Die Suchabsicht ("Freunde", "Dating / Liebe", "Beides") wird als abgerundete Surface-Pill mit Icon dargestellt.
3. Scrolle weiter zum Abschnitt **Lifestyle & Konsumgewohnheiten**.
4. **Erwartetes Ergebnis**:
   - Zwei nebeneinander liegende Cards zeigen das Rauchverhalten (z. B. "Rauchen: Gelegentlich") und das Trinkverhalten (z. B. "Alkohol: Gesellschaftlich") an.

---

### 🔹 Szenario 3: Reputation & Nutzer bewerten (Bottom Sheet)
1. Scrolle zum Abschnitt **Reputation & Bewertungen**.
2. **Erwartetes Ergebnis**:
   - Der Notendurchschnitt (z. B. `4.8`) wird groß dargestellt, gefolgt von der 5-Sterne-Leiste und der Anzahl abgegebener Reviews (`12 Bewertungen`).
   - Bereits abgegebene Erfahrungsberichte werden als abgerundete Dark-Surface-Cards gerendert. Bei GPS/QR-verifizierten Reviews erscheint der grüne Haken.
3. Tippe auf die Schaltfläche **"Bewerten"**.
4. **Erwartetes Ergebnis**:
   - Das **User Rating Bottom Sheet** schiebt sich von unten in den Bildschirm.
   - Tippe auf 5 Sterne und gib den Text *"Sehr gute Begleitung beim Club-Event"* ein.
   - Tippe auf **"Bewertung absenden"**.
   - Das Sheet schließt sich und die Bewertung erscheint in der Liste, während die Gesamtanzahl um 1 steigt.

---

### 🔹 Szenario 4: Profil melden & Nutzer blockieren
1. Tippe in der Top-Bar oben rechts auf das **Optionen-Icon (3 Punkte)**.
2. Wähle im Dropdown-Menü den Punkt **"Profil melden"**.
3. **Erwartetes Ergebnis**:
   - Ein Modal-Dialog öffnet sich und bietet Meldegründe (z. B. *Unangemessenes Verhalten*, *Fake-Profil*, *Spam*) an.
   - Wähle einen Grund aus und tippe auf **"Meldung absenden"**. Eine Erfolgsmeldung wird angezeigt.
4. Tippe erneut auf das Optionen-Icon und wähle **"Nutzer blockieren"**.
5. **Erwartetes Ergebnis**:
   - Am oberen Bildschirmrand erscheint die Warn-Card *"Du hast diesen Nutzer blockiert"*.
   - Ein erneuter Klick hebt die Blockierung auf.

---

## 📊 4. Automatisiertes UI-Testskript (Compose Test Rule)

Das vollständige automatisierte UI-Test-Skript liegt in der Datei:
`app/src/androidTest/java/com/kliq/app/ui/screens/profile/OtherUserProfileUiIntegrationTest.kt`
