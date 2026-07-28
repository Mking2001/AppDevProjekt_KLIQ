# QA Test Plan: Kapitel 5.9 - Nutzer-Reporting & Blockier-Funktion

## 1. Testübersicht & Zielsetzung
Dieser Testplan definiert die manuellen und automatisierten QA-Tests zur Verifikation des Features **Kapitel 5.9: Nutzer-Reporting & Blockier-Funktion** in der Kliq-App.
Ziel ist die Sicherstellung der vollumfänglichen Funktionalität von Melde- und Blockierprozessen, der Benutzeroberfläche im High-Contrast Lila/Dark-Theme, der lokalen Room-Datenbankpersistenz sowie der automatischen reaktiven Filterung blockierter Nutzer über alle Hauptbereiche der App (Karte, Chat, Explore).

---

## 2. Testfälle (Manual & Automated Test Suites)

### TC-5.9-01: Kontextmenü auf fremder Profilansicht & Chat-Aktionsleiste
- **Ziel**: Überprüfung der Erreichbarkeit der Aktionen „Nutzer melden“ und „Nutzer blockieren“.
- **Schritte**:
  1. Öffne das Profil eines anderen Nutzers oder öffne einen Einzelchat.
  2. Tippe auf das Drei-Punkte-Menü (`Icon(Icons.Default.MoreVert)`) in der TopBar.
  3. Prüfe, ob die Menüeinträge „Nutzer melden“ und „Nutzer blockieren“ angezeigt werden.
- **Erwartetes Ergebnis**: Das Kontextmenü öffnet sich flüssig im Kliq Lila/Dark-Theme und bietet beide Aktionen an.

### TC-5.9-02: Reporting-Prozess (Modal Bottom Sheet)
- **Ziel**: Überprüfung der Funktionalität des Reporting Bottom Sheets.
- **Schritte**:
  1. Wähle „Nutzer melden“ aus dem Kontextmenü.
  2. Überprüfe die Anzeige der vordefinierten Meldegründe: *Spam*, *Beleidigung*, *Unangebrachte Inhalte*, *Fake-Profil*.
  3. Wähle einen Meldegrund aus und gib optional eine Begründung im Textfeld ein.
  4. Tippe auf „Meldung absenden“.
- **Erwartetes Ergebnis**: Das Bottom Sheet schließt sich. Eine Bestätigungsnachricht („Nutzer wurde gemeldet. Das Kliq-Sicherheitsteam prüft die Meldung.“) wird angezeigt. Remote API Sync wird ausgeführt.

### TC-5.9-03: Blockier-Bestätigungsdialog & Entblocken
- **Ziel**: Überprüfung der Schutzfunktion gegen versehentliches Blockieren und die Aufhebung der Sperre.
- **Schritte**:
  1. Tippe auf „Nutzer blockieren“.
  2. Prüfe die Warnung im Bestätigungsdialog.
  3. Bestätige mit „Blockieren“.
  4. Überprüfe die Aktualisierung des UI-States (Button wechselt zu „Entblocken“).
  5. Tippe erneut auf „Entblocken“.
- **Erwartetes Ergebnis**: Bei Bestätigung wird der Nutzer lokal in Room (`blocked_users`) persistiert und im Backend synchronisiert. Entblocken hebt die Sperre sauber auf.

### TC-5.9-04: Automatische Reaktiv-Ausfilterung (Karte, Chat, Feed)
- **Ziel**: Überprüfung der systemweiten Ausblendung blockierter Nutzer.
- **Schritte**:
  1. Blockiere einen Testnutzer.
  2. Navigiere zur Karte: Prüfe, ob der Marker des Nutzers ausgeblendet wird.
  3. Navigiere zum Chat-Posteingang: Prüfe, ob der Chat mit dem Nutzer gefiltert wird.
  4. Navigiere zum Explore Feed: Prüfe, ob Beiträge des Nutzers ausgeblendet werden.
- **Erwartetes Ergebnis**: Reaktiver `Flow` schließt blockierte Nutzer ohne App-Neustart aus allen Listen aus.

---

## 3. Verifikation & Ausführung
Automatisierte Unit-Tests können mit folgendem Gradle-Befehl ausgeführt werden:
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; ./gradlew testDebugUnitTest --tests "com.kliq.app.data.repository.UserRepositoryReportingTest" --tests "com.kliq.app.viewmodel.OtherUserProfileViewModelTest"
```
