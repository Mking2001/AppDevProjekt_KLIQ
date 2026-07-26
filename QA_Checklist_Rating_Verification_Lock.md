# QA Checklist: Anti-Spam Logik-Sperre für Nutzerbewertungen (Kapitel 5.3)

## 📋 Testabdeckung & Akzeptanzkriterien

### 1. Default-Sperrzustand (UI & Domain)
- [x] Bei Initialisierung des Bewertungsbildschirms ist die Bewertungseingabe (Sterne 1-5 und Textkommentar) **standardmäßig gesperrt** (`isRatingLocked = true`).
- [x] Ein deutliches Anti-Spam Sperr-Banner signalisiert die fehlende Verifizierung.

### 2. Dynamische Freischaltung via GPS-Geofence
- [x] Sobald beide Nutzer sich am selben Standort/Club befanden (über `GeofenceRepository.visitedHistory` oder aktiven Geofence), streamt `VerificationService` ein verifiziertes Ergebnis (`GPS_GEOFENCE_MATCH`).
- [x] `RatingViewModel` schaltet die Eingabefelder dynamisch frei (`isRatingLocked = false`).
- [x] Sternebewertung (1-5) und Kommentarzeile werden interaktiv aktivierbar.

### 3. Dynamische Freischaltung via QR-Scan
- [x] Das Scannen eines persönlichen QR-Code-Tokens des zu bewertenden Nutzers wird von `VerificationService` validiert (`QR_CODE_SCAN`).
- [x] Die Eingabe entperrt sich augenblicklich und erlaubt die Veröffentlichung.

### 4. Hard-Validation auf Repository- & Daten-Ebene
- [x] `RatingRepositoryImpl` führt vor `reviewDao.insertReview` eine strikte Hard-Validation durch.
- [x] Der Versuch, ohne gültige Verifizierung direkt auf Repository-Ebene in die Datenbank zu schreiben, wird mit einer `IllegalStateException` abgebrochen.

### 5. Automated Testing Verification
- [x] Unit Test `VerificationServiceTest`: Alle Proximity- und QR-Scan-Matchings bestanden.
- [x] Unit Test `RatingRepositoryTest`: Hard-Validation-Repositoryschutz bestanden.
- [x] Unit Test `RatingViewModelTest`: Reaktiver UI-Sperrstream bestanden.
- [x] Integration Test `RatingProximityIntegrationTest`: End-to-End Test bestanden.
