# QA Checkliste: Kliq Database Migration Strategy (MVVM & Data Centricity)

Diese Checkliste dient der Qualitätssicherung, Architektur-Überprüfung und formalen Abnahme der Datenbank-Migrations-Strategie für die lokale Room-Datenbank (`KliqDatabase`) des Projekts **Kliq**.

---

## 🏗️ 1. Architektur-Check (MVVM & Clean Data Layer)

- [x] **Saubere Trennung der Verantwortlichkeiten (Separation of Concerns):**
  - Migrationslogik (`DatabaseMigrations`) und DDL-Operationen sind strikt im Bereich `data.local` gekapselt.
  - Das UI-Layer (Compose) und ViewModels besitzen keinerlei Wissen über SQL-Migrationspfade oder Versionierungs-Details.
- [x] **Repository-Abstraktion:**
  - Repositories (`UserRepositoryImpl`, `ClubRepositoryImpl`, `EventRepositoryImpl`, `ReviewRepositoryImpl`, `ChatRepositoryImpl`) greifen ausschließlich über DAOs auf die Datenbank zu.
  - Änderungen an Schema-Versionen oder Datenbank-Tabellen wirken sich nicht auf Repositories oder Domain-Models aus.
- [x] **Dependency Injection (Hilt Integration):**
  - Die Datenbank-Erstellung wird zentral über `DatabaseMigrationManager` bereitgestellt und im `AppModule` via Hilt (`@Provides @Singleton`) in den App-Graph injiziert.
- [x] **Zentrale Migrationsverwaltung:**
  - Alle Migrationsschritte (`MIGRATION_1_2` bis `MIGRATION_6_7`) sind im skalierbaren Array `DatabaseMigrations.ALL_MIGRATIONS` bündelt und versioniert hinterlegt.

---

## 🛡️ 2. Datenintegrität & Absturzsicherheit (Data Centricity)

- [x] **Verlustfreie Datentransformation:**
  - Schema-Erweiterungen nutzen präzise `ALTER TABLE ADD COLUMN`-Befehle mit typsicheren Standardwerten (`DEFAULT 0`, `DEFAULT ''`, `DEFAULT NULL`).
  - Komplexe Schema-Strukturänderungen (z. B. Fremdschlüssel und Indizes bei `reviews`, `chats`, `messages`) nutzen das sichere Recreate-Table-Muster (`RENAME TO _old` -> `CREATE TABLE` -> `INSERT INTO ... SELECT ...` -> `DROP TABLE _old`).
- [x] **Vollständige Abdeckung aller Entitäten:**
  - Bestätigter Datenerhalt ohne Datenverlust für:
    - **User-Profile** (`users`)
    - **Clubs & Events** (`clubs`, `events`)
    - **Reviews** (`reviews`)
    - **Chats & Nachrichten** (`chats`, `messages`)
- [x] **Automatisierter Fallback-Schutz:**
  - `fallbackToDestructiveMigrationOnDowngrade()` schützt vor Abstürzen bei unerwartetem Schema-Downgrade.
  - Schema-Validierung via `PRAGMA quick_check` prüft die Konsistenz beim Öffnen der Datenbank.

---

## ⚡ 3. Code-Qualität, Performance & Wartbarkeit

- [x] **Index-Optimierung:**
  - Fremdschlüssel-Spalten (`clubId`, `reviewerUserId`, `eventId`, `chatId`, `senderUserId`) sind mit sekundären Indizes (`@Index`) ausgestattet, um verlangsamte Join-Operationen bei großen Datensätzen zu verhindern.
- [x] **Performante DDL-Ausführung:**
  - Migrationsskripte laufen in einer einzigen SQLite-Transaktion ab.
- [x] **KSP Schema-Export:**
  - Raum-Schema wird automatisch nach `app/schemas/com.kliq.app.data.local.KliqDatabase/7.json` exportiert und versioniert nachverfolgt.

---

## 🧪 4. Testing & Verifikation

- [x] **Automatisierter JVM-UnitTest (`DatabaseMigrationTest.kt`):**
  - Simuliert sowohl gezielte Schema-Upgrades (`6 -> 7`) als auch die vollständige Migrationskette (`1 -> 7`) via Robolectric.
- [x] **Automatisierter Emulator/Device Integrationstest (`KliqDatabaseMigrationAndroidTest.kt`):**
  - Instrumented Integrationstest zur Ausführung direkt auf dem Android-Emulator / physischen Testgerät.
  - Simuliert V1-Bestandsdatenbanken und verifiziert die verlustfreie Transformation auf Schema V7.
- [x] **Executable Automation Script (`run_migration_test.ps1` / `run_migration_test.sh`):**
  - Standalone-Skript für Entwickler und CI/CD-Pipelines zur automatisierten Ausführung aller Migrationsprüfungen.

---

> **Ergebnis der qualitativen Abnahme:**  
> Die Datenbank-Migrations-Strategie erfüllt alle Vorgaben für MVVM-Architektur, Datenintegrität, Performance und Testabdeckung. Das Testergebnis bestätigt die verlustfreie Migration sowie die Stabilität des Systems.
