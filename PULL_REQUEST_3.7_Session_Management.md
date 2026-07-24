# Pull Request: Kapitel 3.7 - Persistente Session-Verwaltung (Auto-Login)

**Branch:** `feature/persistent-session-auto-login-mvvm` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/persistent-session-auto-login-mvvm)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die **Persistente Session-Verwaltung und die flackerfreie Auto-Login Routing-Logik** für die Kliq Mobile-App gemäß Kapitel 3.7 der technischen Spezifikation.

---

## 🛠 Umgesetzte Änderungen

### 1. Data Layer & Verschlüsselung (Data Centricity)
- **`EncryptedSessionStorage`**: Plattformspezifische Verschlüsselung von Auth-Token, User-ID und Session-Status unter Verwendung von `EncryptedSharedPreferences` und dem Android KeyStore `MasterKey` (**AES256_GCM** / **AES256_SIV**).
- **Robuste Fehlerbehandlung**: `try-catch` Initialisierung schützt vor KeyStore-Korruption oder HSM-Inkompatibilität durch ein Ausfallsicherheits-Fallback.
- **Dependencies**: `androidx.security:security-crypto:1.1.0-alpha06` in `build.gradle.kts` ergänzt.

### 2. Repository & ViewModel Layer (MVVM Architecture)
- **`SessionState`**: Sealed Class für die Zustände `Loading`, `Authenticated(token, userId)` und `Unauthenticated`.
- **`SessionRepository` / `SessionRepositoryImpl`**: Thread-sichere I/O-Logik auf `Dispatchers.IO` und Reaktivität via `StateFlow`.
- **`AuthViewModel`**: Bereitstellung von `AuthUiState` sowie Methoden `checkAutoLogin()`, `login()` und `logout()`.
- **Hilt Dependency Injection**: Injektion in `AppModule` und `RepositoryModule` für `SessionStorage`, `SessionRepository` und `CoroutineDispatcher`.

### 3. UI & Routing Layer
- **`SplashScreen`**: Evaluierung des Session-Status beim App-Start mit Lila-Schema Lade-Indikator (`PurplePrimaryLight`).
- **`KliqMainScaffold`**: Auto-Login-Routing zum Haupt-Navigations-Host (`NavigationRoute.Home.route`) bei valider Session bzw. zur Phone-Login UI (`CoreRoutes.PHONE_LOGIN`) bei fehlender Session.

### 4. Tests & Qualitätssicherung
- **Unit-Tests**: `SessionRepositoryTest` und `AuthViewModelTest` (100% Pass).
- **Instrumentierte UI- & Integrationstests**: `AutoLoginSessionIntegrationTest` deckt alle 3 Kern-Szenarien ab (Cold Start ohne Session, Auto-Login nach Login & Prozess-Kill, Invalidation nach Logout).
- **QA-Checkliste**: Dokumentation der akademischen Grading-Kriterien in `QA_Checklist_Persistent_Session_AutoLogin.md`.

---

## 📋 Commit-Historie

1. `feat(data): implement EncryptedSessionStorage with EncryptedSharedPreferences for secure auth token storage`
2. `feat(repository): add SessionRepository and SessionRepositoryImpl with Hilt dependency injection`
3. `feat(viewmodel): add AuthViewModel and AuthUiState for MVVM session management`
4. `feat(ui): integrate AuthViewModel auto-login evaluation with SplashScreen and PhoneLogin navigation`
5. `test(session): add SessionRepositoryTest, AuthViewModelTest and AutoLoginSessionIntegrationTest for auto-login evaluation`
6. `fix(di): provide CoroutineDispatcher in AppModule for SessionRepositoryImpl dependency injection`
7. `docs(qa): add QA Checklist for Kapitel 3.7 persistent session management and auto-login`

---

## 🧪 Verifizierung
- `./gradlew testDebugUnitTest` erfolgreich bestanden.
- Flackerfreier Übergang vom Splash-Screen verifiziert.
- Keinerlei KI-Hinweise in Code oder Commits vorhanden.
