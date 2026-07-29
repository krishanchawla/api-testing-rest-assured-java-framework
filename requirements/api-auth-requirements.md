# Playground Auth Token Lifecycle - Business Requirements

Source of truth for the `api-auth` service module: this repo's live demo target,
[`playground.krishanchawla.com/scenarios/api-auth`](https://playground.krishanchawla.com/scenarios/api-auth/).
There is no OpenAPI spec for this sandbox, so the requirements below combine the
scenario page's own description with behavior verified live against the running
sandbox (`curl`/PowerShell, 2026-07-29) - the verified examples are the technical
grounding in place of a spec.

Base URL: `https://playground.krishanchawla.com`. All paths below are relative to it.

## Login (`POST /api/scenarios/api-auth/token`)

- **R1** - Given valid credentials, the endpoint returns `200` with a fresh
  `accessToken`, `refreshToken`, `expiresIn` (seconds), and `tokenType` (`Bearer`).
  Verified: `{"username":"standard_user","password":"Password123!"}` -> 200
  `{"accessToken":"...","refreshToken":"...","expiresIn":20,"tokenType":"Bearer"}`.
- **R2** - Given an incorrect password for a known username, the endpoint returns `401`
  with `{"error":"invalid_credentials"}` rather than silently issuing a token.
- **R3** - Given a request body missing the `password` field, the endpoint responds the
  same way as R2 (`401 invalid_credentials`) - there is no separate 400-level "missing
  field" validation path for this endpoint.
- **R4** - Given a request body that is not valid JSON, the endpoint returns `400` with
  `{"error":"invalid_body"}`, distinguishing malformed requests from bad credentials.

## Accessing the protected resource (`GET /api/scenarios/api-auth/protected`)

- **R5** - Given a valid, unexpired access token in the `Authorization: Bearer <token>`
  header, the endpoint returns `200` with a message and the requesting `username`.
- **R6** - Given no `Authorization` header at all, the endpoint returns `401` with
  `{"error":"missing_token"}`.
- **R7** - Given a syntactically invalid/garbage bearer token, the endpoint returns `401`
  with `{"error":"invalid_token"}`, distinct from the missing-header case (R6).
- **R8** - Given an access token whose `expiresIn` window has elapsed, the endpoint
  returns `401` with `{"error":"expired_token"}`, distinct from both R6 and R7.

## Refreshing a session (`POST /api/scenarios/api-auth/refresh`)

- **R9** - Given a valid, non-expired, non-revoked refresh token, the endpoint returns
  `200` with a brand-new `accessToken`/`refreshToken` pair (rotation), not a reused
  access token.
- **R10** - Once a refresh token has been used to mint a new pair (R9), that same
  (now-superseded) refresh token is immediately invalid for further use: retrying it
  returns `401` with `{"error":"revoked_token"}`. Refresh tokens are single-use.
- **R11** - Given a refresh token that was explicitly invalidated via the revoke
  endpoint, the refresh endpoint returns `401` with `{"error":"revoked_token"}` - the
  same error code as the rotation case in R10.
- **R12** - Given a syntactically invalid/garbage refresh token, the endpoint returns
  `401` with `{"error":"invalid_token"}`.
- **R13** - Given an access token submitted where a refresh token is expected, the
  endpoint rejects it with `401` and `{"error":"wrong_type"}` rather than treating it as
  an unrecognized/invalid token - the two token kinds are distinguished server-side.

## Revoking a session (`POST /api/scenarios/api-auth/revoke`)

- **R14** - Given a valid, not-yet-expired refresh token, revoking it invalidates it for
  future use immediately, ahead of its natural expiry (verified via R11: a revoked
  token's subsequent `/refresh` call fails before the 5-minute refresh-token window
  elapses).
- **R15** - The revoke endpoint always returns `200 {"ok":true}`, including when given a
  refresh token that was never valid to begin with (e.g. a bogus string) - it is
  idempotent/acknowledgment-only rather than validating the token first.
- **R16** - Revoking a refresh token does **not** retroactively invalidate the access
  token that was issued alongside it in the same login/refresh response: a still-live
  access token from that pair continues to be accepted by `/protected` (per R5) until it
  naturally expires (per R8). Access-token validity is self-contained (signature +
  expiry), independent of refresh-token state.

## Out of scope for this pass

- Any endpoint or behavior not listed on the scenario page or observed above (e.g. rate
  limiting, concurrent-session limits) is **not yet specified** - do not infer it from
  response shape alone; ask rather than guess if a generated test would need it.
