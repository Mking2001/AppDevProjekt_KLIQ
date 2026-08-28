# Pull Request: Firebase SDK & Infrastructure Integration

## Zusammenfassung
Dieses Feature bindet das offizielle Firebase SDK und die Google Services Pipeline nahtlos in das Projekt **Kliq** (`com.kliq.app`) ein. 

### Kern-Änderungen:
1. **Build-Konfiguration (`build.gradle.kts` & `app/build.gradle.kts`):**
   - Registrierung des Google Services Gradle Plugins (`com.google.gms.google-services:4.4.1`)
   - Registrierung des Firebase Crashlytics Gradle Plugins (`com.google.firebase.crashlytics:2.9.9`)
   - Einbindung der Dependencies:
     - `com.google.firebase:firebase-analytics-ktx:21.5.1`
     - `com.google.firebase:firebase-messaging-ktx:23.4.1`
     - `com.google.firebase:firebase-crashlytics-ktx:18.6.2`

2. **Dienst-Konfiguration (`app/google-services.json`):**
   - Hinterlegung der Firebase Client-Konfiguration für das Projekt `kliq-app-d215a` und Paket `com.kliq.app`.

3. **Architektur & Dependency Injection (`UseCaseModule.kt`, `AppModule.kt`):**
   - Beseitigung redundanter Dagger/Hilt Bindings für UseCases.
   - Saubere Kapselung der Firebase-Dienste über asynchrone Initialisierung in `KliqApplication`.

---

## Verifikationsplan
- [x] Gradle Build & Kompilierung: `compileDebugSources` erfolgreich (`BUILD SUCCESSFUL`).
- [x] Ausführung der Tasks `processDebugGoogleServices` und `injectCrashlyticsMappingFileIdDebug`.
- [x] Hilt Dagger Component Generation erfolgreich validiert.

## Commit-Historie
- `feat(firebase): integrate google-services and crashlytics plugins with configuration`
- `fix(di): resolve duplicate usecase binding and update model imports`
- `docs: add pull request documentation for firebase integration`
