# Book Exchange API Tests

[![API Tests](https://github.com/vladwpnz/book-exchange-api-tests/actions/workflows/api-tests.yml/badge.svg)](https://github.com/vladwpnz/book-exchange-api-tests/actions/workflows/api-tests.yml)

Automated black-box API tests for [book-exchange-api](https://github.com/vladwpnz/book-exchange-api).

This repository is intentionally separate from the main Spring Boot API repository. It contains only the Maven test project, API testing documentation, a Postman collection, and GitHub Actions workflow files.

## Tech Stack

- Java 17
- Maven
- JUnit 5
- RestAssured
- AssertJ
- Jackson
- GitHub Actions

## Tested API

The tests were created after inspecting the current `vladwpnz/book-exchange-api` controller and security configuration.

Current API behavior:

| Area | Endpoint |
|---|---|
| Registration | `POST /register` |
| Basic Auth verification | `GET /owned` |
| Books | `POST /book/add`, `GET /owned`, `GET /held` |
| Transfers | `POST /book/share`, `POST /book/give`, `POST /book/return` |
| Admin | `GET /items`, `DELETE /book/delete?id={id}`, `POST /book/return/force?id={id}` |

The API currently uses Spring Security HTTP Basic authentication. There is no dedicated `/login` endpoint, so the "login" tests verify valid and invalid credentials by calling an authenticated endpoint.

## Configuration

The API base URL is configured with:

```text
BOOK_EXCHANGE_API_BASE_URL
```

Default:

```text
http://localhost:8080
```

Optional:

```text
BOOK_EXCHANGE_API_REQUIRE_AVAILABLE=true
```

By default, tests are skipped when the API is unreachable. Set `BOOK_EXCHANGE_API_REQUIRE_AVAILABLE=true` when you want the build to fail if the target API is down.

## CI Behavior

GitHub Actions checks out this test repository and the real backend repository, [book-exchange-api](https://github.com/vladwpnz/book-exchange-api), side by side. The workflow starts a MySQL service, creates the `friendssharing` database if needed, starts the backend from the backend repository, waits for `http://localhost:8080`, and then runs these Maven API tests with `BOOK_EXCHANGE_API_REQUIRE_AVAILABLE=true`.

## Run Locally
### Windows with XAMPP

If you use XAMPP instead of Docker:

1. Open XAMPP Control Panel.
2. Start MySQL.
3. Make sure MySQL is running on port `3306`.
4. Make sure the `friendssharing` database exists.

Create the database if needed:

```powershell
& "C:\xampp\mysql\bin\mysql.exe" -h 127.0.0.1 -P 3306 -u root -e "CREATE DATABASE IF NOT EXISTS friendssharing;"
```

Start the main API:

```powershell
cd C:\Projects\book-exchange-api
mvn spring-boot:run
```

Then run the API tests:

```powershell
cd C:\Projects\book-exchange-api-tests
mvn test
```

Start the main API first:

```bash
docker compose up -d
./mvnw spring-boot:run
```

Then run these tests from this repository:

```bash
mvn test
```

Run against a custom environment:

```bash
BOOK_EXCHANGE_API_BASE_URL=https://example.test mvn test
```

On Windows PowerShell:

```powershell
$env:BOOK_EXCHANGE_API_BASE_URL = "http://localhost:8080"
mvn test
```

## Latest Local Validation

Latest run in this workspace compiled the test suite successfully, but did not execute the API scenarios because `http://localhost:8080` was unreachable and API availability is optional by default.

```text
AdminApiTest: 10 tests
AuthApiTest: 7 tests
BookApiTest: 7 tests
BookTransferApiTest: 6 tests
Total: 30 tests

mvn test:
Tests run: 0
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
API execution: skipped by availability check because the backend was unreachable
```

## Project Structure

```text
.
|-- .github/workflows/api-tests.yml
|-- postman/Book_Exchange_API.postman_collection.json
|-- src/test/java/com/vladwpnz/bookexchange/apitests
|   |-- BaseApiTest.java
|   |-- AuthApiTest.java
|   |-- BookApiTest.java
|   |-- BookTransferApiTest.java
|   |-- AdminApiTest.java
|   |-- config
|   |-- helpers
|   `-- models
|-- TEST_PLAN.md
|-- TEST_CASES.md
`-- BUG_REPORTS.md
```

## Notes For Future Maintenance

Some endpoint names and JSON field names may need small updates after running against the real API locally. The tests include comments near those assumptions, especially around:

- Basic Auth standing in for "login"
- `book_id`, `holder_id`, and `owner_id` response fields
- `POST /book/return/force` admin-only security behavior
- Admin book not-found cases returning `404 Not Found` with `Book not found`
