# QA Checklist: Kommentarsektion für Bewertungen (Kapitel 5.5)

## 📋 Testabdeckung & Akzeptanzkriterien

### 1. Datenhaltung & Koppelung
- [x] Schriftliche Kommentare sind in `ReviewEntity` fest an die 1-5 Sterne-Bewertung gekoppelt.
- [x] `ReviewRepository` kapselt das Laden und Einfügen verifizierter Nutzerkommentare off-main-thread (`Dispatchers.IO`).
- [x] Ein Aufruf von `submitVerifiedUserComment` mit `UNVERIFIED` Methode wirft eine `IllegalStateException` und bricht den DB-Schreibvorgang ab.

### 2. ViewModel-Logik & Anti-Spam Sperre
- [x] `ReviewViewModel` steuert den Sperrzustand (`isVerificationLocked = true` als Standard).
- [x] Bei aktiver Sperre ist das Eingabefeld gesperrt und der Absenden-Button deaktiviert. Ein rot hervorgehobenes Sperr-Banner weist den Nutzer darauf hin.
- [x] Die Eingabe wird in Echtzeit auf maximal 280 Zeichen begrenzt (`remainingCharacters` Counter).

### 3. UI/UX-Design (Kliq Dark Mode)
- [x] `ReviewCommentCard` stellt Verfasser-Avatar, Name, Zeitstempel, Sterne, Verifizierungs-Badge und Kommentartext dar.
- [x] `ReviewCommentSection` zeigt bei 0 Kommentaren einen sauberen Platzhalter ("Noch keine schriftlichen Kommentare vorhanden").
- [x] Kontrastreiches Lila/Dark-Mode-Schema (`#7C3AED` Primary Purple, `#1E1B2E` Card Container).

### 4. Automated Testing Verification
- [x] Unit Test `ReviewCommentsSectionUnitTest`: 100 % Erfolgsquote bei Anti-Spam-Sperre, Zeichenbegrenzung und Veröffentlichung.
