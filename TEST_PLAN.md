# Test Plan

## Objective

Verify the public behavior of the Book Exchange REST API from a separate black-box API testing project.

## Scope

Covered:

- User registration
- HTTP Basic authentication behavior
- Request validation errors
- Unauthorized access
- Forbidden user access to admin endpoints
- Book creation
- Reading owned and held books
- Sharing books
- Giving books permanently
- Returning borrowed books
- Admin listing, deletion, and force-return actions
- Current not-found behavior for invalid book IDs and missing users/books

Not covered:

- Internal service, repository, or controller unit tests
- Database migrations
- UI or Swagger UI rendering
- Performance, load, or security penetration testing
- Direct modification of the main API repository

## Test Levels

| Level | Tool | Purpose |
|---|---|---|
| API integration | JUnit 5 + RestAssured | Validate real HTTP behavior |
| Assertions | AssertJ + RestAssured | Verify status codes, response bodies, and JSON fields |
| Manual exploration | Postman | Provide a beginner-friendly collection for exploratory testing |
| CI | GitHub Actions | Run the Maven test suite against a configured API URL |

## Environment

Default target:

```text
http://localhost:8080
```

Override with:

```text
BOOK_EXCHANGE_API_BASE_URL
```

The tests generate unique users and book titles so they can run repeatedly against a development database without hard-coding existing records.

## Entry Criteria

- Java 17 is installed.
- Maven is installed.
- The Book Exchange API is running.
- The database required by the API is available.
- `BOOK_EXCHANGE_API_BASE_URL` points to the running API, or the default local URL is correct.

## Exit Criteria

- All API tests pass against the selected environment.
- Any failed tests are reviewed and classified as:
  - application defect,
  - test data issue,
  - environment/configuration issue,
  - expected contract change.

## Risks And Assumptions

- The API currently has no `/login` endpoint; authentication tests use HTTP Basic credentials against `/owned`.
- Missing book IDs on admin delete and force-return return `404 Not Found` with `Book not found`; validation and business-rule errors remain `400 Bad Request`.
- Admin users can currently be created through public registration by sending `"authority": "admin"`. Tests use that capability to create admin credentials, but it is listed in `BUG_REPORTS.md` as a security concern.
- The current tests verify that `POST /book/return/force` is restricted to admin users.

## CI Strategy

The GitHub Actions workflow runs `mvn test`.

- On push and pull request, tests are allowed to skip if the API is unreachable.
- On manual workflow dispatch, `BOOK_EXCHANGE_API_REQUIRE_AVAILABLE=true` is set so a missing target API fails the build clearly.
