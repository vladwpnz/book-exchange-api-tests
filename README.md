<div align="center">

# 🧪 Book Exchange API Tests

### Independent black-box test suite for the Book Exchange REST API

[![API Tests](https://github.com/vladwpnz/book-exchange-api-tests/actions/workflows/api-tests.yml/badge.svg)](https://github.com/vladwpnz/book-exchange-api-tests/actions/workflows/api-tests.yml)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk\&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-5.10.2-25A162?logo=junit5\&logoColor=white)
![RestAssured](https://img.shields.io/badge/RestAssured-5.4.0-4B8BBE)
![AssertJ](https://img.shields.io/badge/AssertJ-3.25.3-orange)
![Maven](https://img.shields.io/badge/Maven-Test_Project-C71A36?logo=apachemaven\&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI-2088FF?logo=githubactions\&logoColor=white)

</div>

---

## Overview

This repository contains automated black-box HTTP tests for the Book Exchange Spring Boot API.

The suite is intentionally separated from the backend implementation. Tests interact with the application only through its public REST interface and do not import backend services, repositories, entities or internal classes.

This structure demonstrates:

* external REST API validation;
* authentication testing;
* business workflow testing;
* negative test scenarios;
* role-based authorization checks;
* reusable test helpers;
* CI integration;
* independent API contract verification.

## Related Repositories

| Repository                                                                     | Purpose                          |
| ------------------------------------------------------------------------------ | -------------------------------- |
| [book-exchange-ui](https://github.com/vladwpnz/book-exchange-ui)               | React and TypeScript frontend    |
| [book-exchange-api](https://github.com/vladwpnz/book-exchange-api)             | Spring Boot REST API             |
| [book-exchange-api-tests](https://github.com/vladwpnz/book-exchange-api-tests) | Independent black-box test suite |

---

## Testing Approach

The suite treats the backend as an external system.

```text
┌──────────────────────────────────┐
│     JUnit + RestAssured tests     │
└────────────────┬─────────────────┘
                 │
                 │ HTTP requests
                 │ Basic Auth
                 │ JSON payloads
                 ▼
┌──────────────────────────────────┐
│       Book Exchange REST API      │
│                                  │
│ Registration • Books • Transfers │
│ Administration • Security        │
└────────────────┬─────────────────┘
                 │
                 ▼
┌──────────────────────────────────┐
│              MySQL               │
└──────────────────────────────────┘
```

The test project verifies:

* status codes;
* authentication behavior;
* response bodies;
* JSON contracts;
* ownership transitions;
* holder transitions;
* validation errors;
* administrator restrictions;
* invalid business operations.

---

## Technology Stack

| Technology           | Purpose                                |
| -------------------- | -------------------------------------- |
| Java 17              | Test implementation language           |
| Maven                | Build and dependency management        |
| JUnit 5.10.2         | Test execution framework               |
| RestAssured 5.4.0    | HTTP API testing                       |
| AssertJ 3.25.3       | Fluent assertions                      |
| Jackson 2.17.2       | JSON serialization and deserialization |
| Maven Surefire 3.2.5 | Test discovery and reports             |
| Postman              | Manual API exploration                 |
| GitHub Actions       | Continuous integration                 |
| MySQL                | CI database service                    |

---

## Tested API Areas

### Registration and authentication

| Area                    | Method and endpoint                            |
| ----------------------- | ---------------------------------------------- |
| User registration       | `POST /register`                               |
| Credential verification | `GET /owned`                                   |
| Unauthorized access     | Protected endpoints without credentials        |
| Invalid credentials     | Protected endpoints with incorrect credentials |

The backend uses HTTP Basic authentication.

There is no dedicated `/login` endpoint. Authentication tests verify valid and invalid credentials by calling protected API resources.

### Books

| Area             | Method and endpoint |
| ---------------- | ------------------- |
| Add book         | `POST /book/add`    |
| List owned books | `GET /owned`        |
| List held books  | `GET /held`         |

### Transfers

| Area                     | Method and endpoint |
| ------------------------ | ------------------- |
| Share a book temporarily | `POST /book/share`  |
| Give a book permanently  | `POST /book/give`   |
| Return a borrowed book   | `POST /book/return` |

### Administration

| Area                    | Method and endpoint               |
| ----------------------- | --------------------------------- |
| View complete inventory | `GET /items`                      |
| Delete a book           | `DELETE /book/delete?id={id}`     |
| Force a book return     | `POST /book/return/force?id={id}` |

---

## Test Suites

### `AuthApiTest`

Covers registration, authentication and authorization behavior.

Verified scenarios include:

* successful registration;
* duplicate registration;
* valid credentials;
* invalid password;
* unknown user;
* missing authentication;
* access to protected endpoints.

### `BookApiTest`

Covers physical book creation and retrieval.

Verified scenarios include:

* adding a valid book;
* invalid payload validation;
* retrieving owned books;
* retrieving held books;
* authentication requirements;
* created-book response contracts.

### `BookTransferApiTest`

Covers Share, Give and Return workflows.

Verified scenarios include:

* temporary sharing;
* permanent ownership transfer;
* returning a borrowed copy;
* invalid recipient;
* invalid book identifier;
* invalid transfer state.

### `AdminApiTest`

Covers administrator-only operations.

Verified scenarios include:

* inventory access;
* user access restrictions;
* deleting books;
* missing-book behavior;
* force-return workflow;
* administrative authorization.

---

## Latest Verified Result

The suite has been executed against the real backend application.

```text
AdminApiTest: 10 passed
AuthApiTest: 7 passed
BookApiTest: 7 passed
BookTransferApiTest: 6 passed

Tests run: 30
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

---

## Configuration

### API base URL

The target API is configured with:

```text
BOOK_EXCHANGE_API_BASE_URL
```

Default value:

```text
http://localhost:8080
```

### API availability behavior

Use:

```text
BOOK_EXCHANGE_API_REQUIRE_AVAILABLE
```

Supported behavior:

| Value            | Result when API is unreachable |
| ---------------- | ------------------------------ |
| unset or `false` | API scenarios are skipped      |
| `true`           | Build fails immediately        |

The test base class checks API availability before executing scenarios.

When validation fails, RestAssured logs the request and response automatically.

---

## Running Locally

### Requirements

Install:

* Java 17
* Maven
* Docker Desktop
* Git

The backend application must be running before the black-box tests start.

---

### 1. Clone both repositories

```bash
git clone https://github.com/vladwpnz/book-exchange-api.git
git clone https://github.com/vladwpnz/book-exchange-api-tests.git
```

---

### 2. Start the backend database

```bash
cd book-exchange-api
docker compose up -d
```

Check the MySQL container:

```bash
docker compose ps
```

Wait until the database reports a healthy state.

---

### 3. Start the backend API

```bash
mvn spring-boot:run
```

The backend should become available at:

```text
http://localhost:8080
```

---

### 4. Run the API tests

Open another terminal:

```bash
cd book-exchange-api-tests
mvn clean test
```

---

## PowerShell Configuration

Force the build to fail when the API is unavailable:

```powershell
$env:BOOK_EXCHANGE_API_BASE_URL = "http://localhost:8080"
$env:BOOK_EXCHANGE_API_REQUIRE_AVAILABLE = "true"

mvn clean test
```

Clear the environment variables after testing:

```powershell
Remove-Item Env:BOOK_EXCHANGE_API_BASE_URL
Remove-Item Env:BOOK_EXCHANGE_API_REQUIRE_AVAILABLE
```

---

## Linux and macOS Configuration

```bash
export BOOK_EXCHANGE_API_BASE_URL=http://localhost:8080
export BOOK_EXCHANGE_API_REQUIRE_AVAILABLE=true

mvn clean test
```

---

## Running Against Another Environment

PowerShell:

```powershell
$env:BOOK_EXCHANGE_API_BASE_URL = "https://example.test"
$env:BOOK_EXCHANGE_API_REQUIRE_AVAILABLE = "true"

mvn clean test
```

Linux or macOS:

```bash
BOOK_EXCHANGE_API_BASE_URL=https://example.test \
BOOK_EXCHANGE_API_REQUIRE_AVAILABLE=true \
mvn clean test
```

The target environment must implement the same HTTP contract as the Book Exchange API.

---

## Continuous Integration

The GitHub Actions workflow runs on:

* pushes to `main`;
* `feature/**` branches;
* `fix/**` branches;
* `chore/**` branches;
* `test/**` branches;
* `docs/**` branches;
* pull requests;
* manual workflow dispatch.

### CI Pipeline

```text
Checkout API tests
        │
        ▼
Start MySQL 8.0 service
        │
        ▼
Checkout book-exchange-api
        │
        ▼
Set up Temurin Java 17
        │
        ▼
Ensure friendssharing database exists
        │
        ▼
Start Spring Boot backend
        │
        ▼
Wait until localhost:8080 responds
        │
        ▼
Run Maven API tests
        │
        ▼
Upload Surefire reports
```

The workflow uses:

```text
BOOK_EXCHANGE_API_BASE_URL=http://localhost:8080
BOOK_EXCHANGE_API_REQUIRE_AVAILABLE=true
```

This means CI never reports success when the backend is unavailable.

### CI Database

```text
Image: mysql:8.0
Database: friendssharing
Port: 3306
Root password: empty
```

### Test reports

Surefire reports are uploaded as the workflow artifact:

```text
maven-surefire-reports
```

---

## Project Structure

```text
book-exchange-api-tests
├── .github
│   └── workflows
│       └── api-tests.yml
├── postman
│   └── Book_Exchange_API.postman_collection.json
├── src
│   └── test
│       └── java
│           └── com.vladwpnz.bookexchange.apitests
│               ├── AdminApiTest.java
│               ├── AuthApiTest.java
│               ├── BaseApiTest.java
│               ├── BookApiTest.java
│               ├── BookTransferApiTest.java
│               ├── config
│               ├── helpers
│               └── models
├── BUG_REPORTS.md
├── TEST_CASES.md
├── TEST_PLAN.md
├── pom.xml
└── README.md
```

---

## Supporting Documentation

### `TEST_PLAN.md`

Describes:

* scope;
* testing objectives;
* environments;
* test levels;
* entry and exit criteria;
* risks and assumptions.

### `TEST_CASES.md`

Contains documented API test scenarios and expected results.

### `BUG_REPORTS.md`

Stores defects and contract mismatches found during API validation.

### Postman collection

The `postman` directory contains a reusable collection for manually inspecting the same API workflows.

---

## Test Design Principles

### Black-box verification

The suite validates observable HTTP behavior rather than internal Java implementation.

### Independent repository

The tests remain separate from the Spring Boot source code and can target another compatible deployment.

### Reusable setup

Shared configuration, authentication helpers and model classes reduce duplication between test classes.

### Isolated test data

Tests create their own users and books instead of depending on manually prepared accounts.

### Positive and negative scenarios

The suite checks successful operations together with invalid credentials, invalid payloads, missing resources and forbidden actions.

### Contract-focused assertions

Assertions verify API responses, status codes and ownership state transitions.

---

## Example Test Flow

A transfer scenario generally follows this structure:

```text
1. Register the owner
2. Register the recipient
3. Add a book as the owner
4. Share or give the book
5. Read the resulting API state
6. Assert owner and holder values
7. Verify invalid repeated operations when applicable
```

---

## Engineering Value

This repository demonstrates:

* REST API testing;
* JUnit lifecycle management;
* RestAssured request construction;
* Basic Auth testing;
* JSON serialization;
* typed response models;
* reusable test fixtures;
* dynamic test data;
* business workflow assertions;
* role-based security validation;
* CI service containers;
* external backend startup;
* artifact collection;
* separation between product code and test automation.

---

## Author

### Vladyslav Spyrydonov

GitHub: [@vladwpnz](https://github.com/vladwpnz)
