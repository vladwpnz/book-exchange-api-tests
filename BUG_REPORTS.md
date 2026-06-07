# Bug Reports And Contract Notes

These notes were identified while preparing the external API test suite. They should be verified against the running API before being filed as issues in the main repository.

## BUG-001: Public Registration Can Create Admin Users

Severity: High

Endpoint:

```text
POST /register
```

Steps:

1. Send a registration request with `"authority": "admin"`.
2. Authenticate with the newly created user.
3. Call an admin endpoint such as `GET /items`.

Expected:

Public self-registration should not grant admin authority.

Actual:

The inspected service accepts any enum value from `Authorities`, including `ADMIN`.

Risk:

Any unauthenticated caller may create an admin account.

## NOTE-001: No Dedicated Login Endpoint

Severity: Informational

The API currently uses HTTP Basic authentication instead of a token-based login endpoint. The automated "login" tests validate credentials by calling `GET /owned`.

If a real `/login` endpoint is added later, update:

- `EndpointPaths`
- `AuthApiTest`
- Postman collection
- README and test case documentation

## NOTE-002: Force Return Endpoint Is Restricted To Admin Users

Severity: Informational

Endpoint:

```text
POST /book/return/force?id={id}
```

Current tested behavior:

Regular users receive `403 Forbidden`, and anonymous callers receive `401 Unauthorized`.

## NOTE-003: Missing Book IDs Return 404

Severity: Informational

Examples:

- `DELETE /book/delete?id=999999999`
- `POST /book/return/force?id=999999999`

Current behavior:

```text
404 Not Found
Book not found
```

Earlier builds returned `400 Bad Request` with `Wrong id` for these missing book cases. The backend API now returns a clearer `404 Not Found` response. Business-rule and validation errors, such as sharing a book with an unknown username, still return `400 Bad Request`.
