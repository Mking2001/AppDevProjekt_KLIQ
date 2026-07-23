# QA Checkliste & Test-Szenario: Kliq Konsum-Gewohnheiten Screen (Kapitel 3.5)

Diese Dokumentation beschreibt die manuellen und automatisierten Test-Szenarien zur Abnahme des Onboarding-Screens zur Erfassung des Rauch- und Trinkverhaltens der Nutzer in der nativen Kliq Mobile-App.

---

## 🎨 1. Layout- & Rendering-Test (High-Contrast Lila/Dark-Mode)

- [x] **Dark-Mode Design & Farbschema:**
  - Der Hintergrund ist im tiefen Dark-Mode (`DarkBackground` `#0F0B15`) gestaltet.
  - Karten & Elemente nutzen `DarkSurface` (`#1A1523`) mit deutlichen Kontrastgrenzen (`DarkOutline`).
  - Schritt-Indikator oben ("SCHRITT 3 VON 3 • KONSUM-GEWOHNHEITEN") mit lila Akzentfarbe.
- [x] **Vollständigkeit aller Optionen:**
  - **Sektion Rauchverhalten:**
    - Option 1: "Nie" ("Ich rauche gar nicht")
    - Option 2: "Gelegentlich" ("Ab und zu beim Feiern oder in Gesellschaft")
    - Option 3: "Regelmäßig" ("Regelmäßiger Raucher im Alltag")
  - **Sektion Trinkverhalten:**
    - Option 1: "Nie" ("Ich trinke keinen Alkohol")
    - Option 2: "Gesellschaftlich" ("Ab und zu in Gesellschaft oder bei Events")
    - Option 3: "Oft" ("Gerne und regelmäßig beim Ausgehen")

---

## 👆 2. Interaktions-Test & Visuelles Feedback

- [x] **Chip- & Card-Highlighting:**
  - Klick auf eine Option löst eine sanfte Skalierungsanimation (`1.01x`) aus.
  - Das Icon schaltet auf die Primärfarbe (`PurplePrimary`) um und ein Fuchsia/Violett-Gradientenrahmen wird aktiviert.
  - Ein visuelles Häkchen-Badge (`CheckCircle` in `FuchsiaTertiary`) signalisiert die aktive Auswahl.
- [x] **Umschalten & Deselektieren:**
  - Erneutes Klicken auf ein bereits ausgewähltes Element hebt die Auswahl auf.
  - Das Klicken auf eine andere Option innerhalb derselben Kategorie schaltet die Auswahl korrekt um.

---

## 💾 3. State- & Daten-Persistenz

- [x] **Validierung vor dem Absenden:**
  - Ist nur eine oder keine Kategorie ausgewählt, bleibt der Button "Auswahl speichern & Weiter" deaktiviert (`disabled`) und der Hinweistext `* Bitte wähle für beide Kategorien eine Option aus.` ist sichtbar.
  - Erst wenn sowohl für Rauchen als auch für Trinken eine Option gewählt wurde, schaltet der Button in den aktiven Zustand um.
- [x] **Persistierung im lokalen Speicher (Room DB):**
  - Beim Klick auf den Weiter-Button wird der Ladezustand (`CircularProgressIndicator`) angezeigt.
  - Die Werte (`smokingHabit` & `drinkingHabit`) werden an den `UserRepository` übergeben und in der Room-Datenbank (Tabelle `user_preferences`) via Schema-Migration v9 gespeichert.
  - Überprüfung per Debugger/Logcat:
    ```
    [RoomDatabase] INSERT INTO user_preferences (userId, searchIntent, smokingHabit, drinkingHabit) VALUES ('usr_123', 'BOTH', 'REGULARLY', 'SOCIAL')
    ```

---

## 🔄 4. Navigation & State-Recovery

- [x] **Nahtlose Flow-Navigation:**
  - Bei erfolgreicher Speicherung navigiert die App automatisch zum nächsten Schritt im Onboarding bzw. zum Home-Screen.
- [x] **Zustandserhaltung (State Recovery):**
  - Beim Zurücknavigieren im Onboarding-Flow bleiben die bereits ausgewählten Werte im ViewModel/Room-Speicher erhalten und werden in der UI automatisch vorausgewählt wiedergegeben.

---

## 🧪 5. Automatisierte Test-Abdeckung

- **Unit-Tests:** `ConsumptionHabitsViewModelTest.kt` (Zustandsprüfungen, Toggle-Logik, Validierungsfehler, Async-Save).
- **Migration-Test:** `DatabaseMigrationTest.kt` (`migrate8To9_addsSmokingAndDrinkingHabitColumns`).
- **UI-Test:** `ConsumptionHabitsScreenTest.kt` (Compose UI-Test für Rendering, User-Clicks, Selection-States & Save-Callback).
