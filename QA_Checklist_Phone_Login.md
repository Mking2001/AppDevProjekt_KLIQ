# QA Checkliste & Test-Szenarien: Kliq Telefonnummer-Login UI (MVVM & High-Contrast)

Diese Checkliste und Test-Spezifikation dient der Qualitätssicherung des Telefonnummer-Logins als ersten Schritt des Onboarding-Prozesses in der Kliq Mobile-App.

---

## 📋 Test-Szenario 1: Ungültige Telefonnummer-Eingaben & Fehler-Rendering

### Ziel
Verifizieren, dass bei Fehleingaben (zu kurz, zu lang, ungültige Zeichen) präzise Inline-Fehlermeldungen gerendert werden und der Absende-Button zuverlässig deaktiviert bleibt.

### Testschritte & Erwartetes Verhalten
1. **Initialzustand**:
   - Die Eingabe für die Telefonnummer ist leer.
   - Der Button `"SMS-Code anfordern"` ist **deaktiviert** (`isEnabled == false`).
   - Es wird **keine** Fehlermeldung angezeigt.
2. **Zu kurze Telefonnummer (< 7 Ziffern)**:
   - **Aktion**: Eingabe von `"12345"`.
   - **Erwartung**: Die Fehlermeldung `"Telefonnummer zu kurz (mindestens 7 Ziffern)."` wird rot unter dem Textfeld gerendert. Der Button bleibt **deaktiviert**.
3. **Zu lange Telefonnummer (> 15 Ziffern gemäß ITU E.164 Standard)**:
   - **Aktion**: Eingabe von `"1234567890123456"`.
   - **Erwartung**: Die Fehlermeldung `"Telefonnummer zu lang (maximal 15 Ziffern)."` wird gerendert. Der Button bleibt **deaktiviert**.
4. **Korrektur der Eingabe**:
   - **Aktion**: Löschen der überschüssigen Ziffern auf ein gültiges Format.
   - **Erwartung**: Die Fehlermeldung verschwindet sofort (reaktives State-Update).

---

## 🚀 Test-Szenario 2: Gültige Telefonnummer, Button-Aktivierung & Ladezustand

### Ziel
Verifizieren, dass bei einer validen Eingabe der Absende-Button aktiviert wird, die OTP-Anforderung ausgelöst wird und der Ladeindikator sowie der Übergang zur OTP-Eingabe reibungslos funktionieren.

### Testschritte & Erwartetes Verhalten
1. **Gültige Telefonnummer-Eingabe**:
   - **Aktion**: Eingabe einer gültigen Handynummer (z. B. `"1512345678"`).
   - **Erwartung**: Der Button `"SMS-Code anfordern"` wechselt reaktiv in den aktiven Zustand (`isEnabled == true`).
2. **Auslösen der OTP-Anforderung**:
   - **Aktion**: Klick auf `"SMS-Code anfordern"`.
   - **Erwartung**:
     - Das ViewModel setzt `isLoading = true`.
     - Der Button zeigt einen rotierenden Ladeindikator (`CircularProgressIndicator`).
     - Die Eingabefelder sind während der Anfrage für Interaktionen gesperrt.
3. **Übergang zur OTP-Verifikation**:
   - **Erwartung**:
     - Nach erfolgreicher Antwort wird der OTP-Eingabebereich eingeblendet (`isOtpSent == true`).
     - Die gewählte Nummer wird formatiert angezeigt (z. B. `"+491512345678"`).
     - Ein 6-stelliges OTP-Code Feld erscheint.
4. **Fehlerbehandlung bei Netzwerkfehlern**:
   - **Erwartung**: Bei Server- oder Verbindungsfehlern wird eine Fehlermeldungskarte am unteren Bildschirmrand gerendert.

---

## 🎨 Test-Szenario 3: Visuelles Layout, High-Contrast Design & Tastaturtyp

### Ziel
Überprüfung der optischen Konformität mit dem Kliq High-Contrast Lila/Dark-Mode Theme sowie der korrekten Tastatursteuerung auf unterschiedlichen Displaygrößen (Smartphone, Phablet, Tablet).

### Testschritte & Erwartetes Verhalten
1. **High-Contrast Lila/Dark-Mode Aesthetic**:
   - **Hintergrund**: Dunkler Lila-Verlauf (`DarkBackground` `#0F0B15`).
   - **Surface Card**: Abgerundete Container (`DarkSurface` `#1A1523`) mit deutlichen Kontrasten.
   - **Akzente & Buttons**: Primäres Lila (`PurplePrimary` `#7C3AED`, `#BB86FC`) mit hoher Lesbarkeit (`TextPrimaryHighContrast` `#FFFFFF`).
   - **Sicherheits-Badge**: Schloss-Icon mit DSGVO- und Verschlüsselungs-Hinweis gerendert.
2. **Tastaturtyp-Validierung (Soft-Keyboard)**:
   - **Telefonnummer-Feld**: Öffnet die numerische Wähltastatur (`KeyboardType.Phone`).
   - **OTP-Bestätigungsfeld**: Öffnet die ziffernbasierte Code-Tastatur (`KeyboardType.NumberPassword`).
3. **Multi-Display Responsivität**:
   - Getestet auf Standard-Smartphones (z. B. Pixel 8, 1080x2400), Kompaktgeräten sowie Tablets im Hoch- und Querformat.
   - **Erwartung**: Das Card-Layout bleibt zentriert, zeigt keine Text-Overflows oder fehlerhaften Zeilenumbrüche und passt sich flüssig an.

---

## 🧪 Abdeckungsstatus
- [x] **Ungültige Telefonnummern**: Vollständig abgedeckt durch `testInvalidPhoneNumberShowsErrorAndDisablesButton`
- [x] **Gültige Telefonnummer & Ladezustand**: Vollständig abgedeckt durch `testValidPhoneNumberEnablesButtonAndSubmitsOtp`
- [x] **Ländervorwahl-Auswahl**: Abgedeckt durch `testCountryCodeDropdownSelection`
- [x] **High-Contrast Design & Badges**: Abgedeckt durch `testHighContrastLayoutAndSecurityBadge`
