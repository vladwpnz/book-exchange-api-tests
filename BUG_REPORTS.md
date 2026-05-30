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

## BUG-002: Force Return Endpoint Security Matcher Uses The Wrong HTTP Method

Severity: High

Endpoint:

```text
POST /book/return/force?id={id}
```

Expected:

Only `ADMIN` users should access force return.

Actual:

The controller exposes `POST /book/return/force`, but the inspected security configuration protects `DELETE /book/return/force`. Because `POST /book/**` is allowed for all authenticated authorities, a normal user may be able to call the force-return endpoint.

Suggested fix:

Protect `POST /book/return/force` with `ADMIN`, and keep the method consistent between controller, security rules, tests, and documentation.

## NOTE-001: No Dedicated Login Endpoint

Severity: Informational

The API currently uses HTTP Basic authentication instead of a token-based login endpoint. The automated "login" tests validate credentials by calling `GET /owned`.

If a real `/login` endpoint is added later, update:

- `EndpointPaths`
- `AuthApiTest`
- Postman collection
- README and test case documentation

## NOTE-002: Not-Found Cases Return 400 Instead Of 404

Severity: Informational

Examples:

- `DELETE /book/delete?id=999999999`
- `POST /book/return/force?id=999999999`
- Sharing a book with an unknown username

Current behavior:

```text
400 Bad Request
```

This may be acceptable for the current project, but `404 Not Found` can be clearer for missing resources.
