# Test Cases

## Authentication And Registration

| ID | Scenario | Expected Result |
|---|---|---|
| AUTH-001 | Register a unique user with `authority=user` | `201 Created`, success message |
| AUTH-002 | Register the same email twice | `400 Bad Request`, duplicate user message |
| AUTH-003 | Register with unsupported authority | `400 Bad Request`, wrong authority message |
| AUTH-004 | Register with missing email | `400 Bad Request`, validation error |
| AUTH-005 | Use valid Basic Auth credentials after registration | Authenticated endpoint returns `200 OK` |
| AUTH-006 | Use invalid Basic Auth password | Authenticated endpoint returns `401 Unauthorized` |
| AUTH-007 | Read owned books without authentication | `401 Unauthorized` |

## Book API

| ID | Scenario | Expected Result |
|---|---|---|
| BOOK-001 | Add a book as an authenticated user | `201 Created`, response contains author, title, and user |
| BOOK-002 | Add a book without authentication | `401 Unauthorized` |
| BOOK-003 | Add a book without author | `400 Bad Request`, validation error |
| BOOK-004 | Add a book without title | `400 Bad Request`, validation error |
| BOOK-005 | Read owned books after adding a book | `200 OK`, list contains created title |
| BOOK-006 | Read held books after adding a book | `200 OK`, list contains created title |
| BOOK-007 | Read held books without authentication | `401 Unauthorized` |

## Book Transfer API

| ID | Scenario | Expected Result |
|---|---|---|
| TRANSFER-001 | Share a book with another user | `200 OK`, returned person is the borrower |
| TRANSFER-002 | Give a book permanently to another user | `200 OK`, recipient owns the book |
| TRANSFER-003 | Borrower returns a shared book | `200 OK`, return confirmation message |
| TRANSFER-004 | Share a book with an unknown user | `400 Bad Request`, user not found error |
| TRANSFER-005 | Share a book that is already shared | `400 Bad Request`, already given error |
| TRANSFER-006 | Return a book the user does not hold | `400 Bad Request`, book not held error |

## Admin API

| ID | Scenario | Expected Result |
|---|---|---|
| ADMIN-001 | Admin lists all items | `200 OK`, created book is present |
| ADMIN-002 | Regular user lists all items | `403 Forbidden` |
| ADMIN-003 | Anonymous user lists all items | `401 Unauthorized` |
| ADMIN-004 | Regular user deletes a book | `403 Forbidden` |
| ADMIN-005 | Admin deletes an existing book | `200 OK`, book is deleted |
| ADMIN-006 | Admin deletes an unknown book ID | `404 Not Found`, `Book not found` |
| ADMIN-007 | Admin force-returns a shared book | `200 OK`, book is returned to owner |
| ADMIN-008 | Admin force-returns an unknown book ID | `404 Not Found`, `Book not found` |
| ADMIN-009 | Regular user force-returns a book | `403 Forbidden` |
| ADMIN-010 | Anonymous user force-returns a book | `401 Unauthorized` |

## Manual Postman Coverage

The Postman collection includes the same high-level request groups:

- Auth
- Books
- Transfers
- Admin

Use it for exploration and bug reproduction. The JUnit suite remains the source of truth for automated regression checks.
