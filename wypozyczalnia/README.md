# Wypożyczalnia książek — Backend

## Temat projektu
Backend aplikacji **„Wypożyczalnia książek”** udostępnia REST API do zarządzania książkami, czytelnikami (member) oraz wypożyczeniami (loan). Aplikacja jest przygotowana do pracy w środowisku kontenerowym (Docker Compose) i korzysta z Keycloak jako dostawcy tożsamości.

## Funkcjonalności (skrót)
- **Książki (Books)**: przeglądanie i zarządzanie katalogiem książek.
- **Czytelnicy (Members)**: zarządzanie kontami czytelników w bazie danych.
- **Wypożyczenia (Loans)**: obsługa wypożyczeń jako zasobu zagnieżdżonego w członku (`/members/{id}/loans`).
- **Publiczne dane demo**: endpoint `GET /public/demo-data` zwracający listę książek (do frontu).
- **Uwierzytelnianie**: JWT/OAuth2 z Keycloak (bearer token w nagłówku `Authorization`).
- **Negocjacja treści**: obsługa co najmniej dwóch formatów odpowiedzi (JSON i XML) zależnie od `Accept`.

## Technologie
- **Java 24**
- **Spring Boot 3.5.x**
- **Spring Web (REST)**
- **Spring Security + OAuth2 Resource Server (JWT)**
- **JPA / Hibernate**
- **PostgreSQL** (produkcyjnie w Docker)
- **Liquibase** (migracje i seed danych)
- **MapStruct** (mapowanie encji ↔ DTO)
- **Keycloak** (Docker, realm import)
- **Docker Compose** (uruchamianie Postgres + Keycloak + backend)

## Uruchomienie
### Docker Compose (zalecane)
W katalogu głównym repozytorium:

```bash
docker compose up --build
```

Domyślne porty:
- Backend: `http://localhost:8080`
- Keycloak: `http://localhost:8086`
- Postgres: `localhost:5432`

### Lokalnie (Gradle)
W katalogu `wypozyczalnia/`:

```bash
./gradlew bootRun
```

> Uwaga: lokalne uruchomienie wymaga działającego Postgresa i Keycloak (albo odpowiednich zmiennych środowiskowych).

## Konfiguracja (wybrane)
Plik: `wypozyczalnia/src/main/resources/application.properties`

Najważniejsze zmienne:
- `server.port` — port backendu
- `spring.datasource.*` — konfiguracja bazy
- `keycloak.auth-server-url`, `keycloak.realm`, `keycloak.resource` — konfiguracja Keycloak
- `spring.security.oauth2.resourceserver.jwt.issuer-uri` — issuer JWT

## Endpointy (skrót)
> Dokładne ścieżki mogą się różnić w zależności od konfiguracji i wersji.

### Publiczne
- `GET /public/demo-data` — dane demo (książki + timestamp)

### Autoryzacja
- `POST /auth/register` — rejestracja użytkownika (tworzenie w Keycloak)
- `POST /auth/login` — logowanie (token JWT z Keycloak)
- `POST /auth/refresh` — odświeżenie tokenu
- `POST /auth/logout` — wylogowanie

### Books
- `GET /api/books` — lista książek
- `GET /api/books/{id}` — szczegóły książki
- `POST /api/books` — utworzenie książki
- `PUT /api/books/{id}` — aktualizacja
- `DELETE /api/books/{id}` — usunięcie

### Members
- `GET /api/members` — lista czytelników
- `GET /api/members/{id}` — szczegóły czytelnika
- `POST /api/members` — utworzenie
- `PUT /api/members/{id}` — aktualizacja
- `DELETE /api/members/{id}` — usunięcie

### Loans (zasób zagnieżdżony)
- `POST /api/members/{memberId}/loans` — utworzenie wypożyczenia dla czytelnika
- `GET /api/members/{memberId}/loans` — lista wypożyczeń czytelnika
- `GET /api/members/{memberId}/loans/{loanId}` — pojedyncze wypożyczenie
- `PUT /api/members/{memberId}/loans/{loanId}` — aktualizacja
- `DELETE /api/members/{memberId}/loans/{loanId}` — usunięcie

## Bezpieczeństwo i role
- API używa JWT wydawanego przez Keycloak.
- Token wysyłamy w nagłówku:
  - `Authorization: Bearer <token>`
- Przykładowe role (zależnie od konfiguracji Keycloak):
  - `MEMBER` — odczyt (np. `GET /api/books/**`)
  - `ASSISTANT` — operacje zapisu na książkach (`POST/PUT`)
  - `LIBRARIAN` — pełne uprawnienia, w tym `DELETE` oraz zarządzanie `members` i `loans`

## Negocjacja treści (JSON/XML)
Backend wspiera XML dzięki zależności `com.fasterxml.jackson.dataformat:jackson-dataformat-xml`.

Przykłady:
- JSON:
  - `Accept: application/json`
- XML:
  - `Accept: application/xml`

## Schemat bazy danych
Schemat zarządzany jest przez Liquibase.

Plik: `wypozyczalnia/src/main/resources/database/changelog/001-create-tables.sql`

### Tabela `books`
| Kolumna | Typ | Uwagi |
|---|---|---|
| id | UUID | PK |
| title | VARCHAR(255) | NOT NULL |
| author | VARCHAR(255) | NOT NULL |
| isbn | VARCHAR(20) | UNIQUE, NOT NULL |
| published_year | INTEGER |  |
| genre | VARCHAR(255) |  |
| description | TEXT |  |
| total_copies | INTEGER | NOT NULL |
| available_copies | INTEGER | NOT NULL |
| image_url | VARCHAR(1024) |  |

### Tabela `members`
| Kolumna | Typ | Uwagi |
|---|---|---|
| id | UUID | PK |
| first_name | VARCHAR(128) | NOT NULL |
| last_name | VARCHAR(128) | NOT NULL |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| active | BOOLEAN | NOT NULL |

### Tabela `loans`
| Kolumna | Typ | Uwagi |
|---|---|---|
| id | UUID | PK |
| book_id | UUID | FK → books(id) |
| member_id | UUID | FK → members(id) |
| loan_date | DATE | NOT NULL |
| due_date | DATE | NOT NULL |
| return_date | DATE | NULL |
| status | VARCHAR(32) | NOT NULL |

Indeksy:
- `idx_loans_member` na `member_id`
- `idx_loans_book` na `book_id`
- `idx_loans_status` na `status`

Ograniczenia:
- `uq_member_book_status` unikalność `(member_id, book_id, status)`

## Autorzy / metadane uczelniane
**Przedmiot:** _[Programowanie we frameworkach internetowych]_  
**Rok akademicki:** _[2025/2026]_  

**Autorzy:**
- _[Patryk Treszczotko]_ — nr indeksu: _[84794]_   

> Uwaga: nie znalazłem w repo pliku z nazwą przedmiotu/rokiem/autorami, więc zostawiłem pola do uzupełnienia.

---

### Szybki test API (curl)
Przykład pobrania danych demo:

```bash
curl -H "Accept: application/json" http://localhost:8080/public/demo-data
```

