# REST API Test Automation Framework

A Java + TestNG + REST Assured framework for testing HTTP APIs, built around a
config → auth → client → service → test layering so a new service can usually
be onboarded with a config entry and a thin service class rather than new
framework code.

## Architecture

```
EnvironmentConfig   resolves config with precedence: env var > -D system property > properties file
        |
AuthStrategy         pluggable auth (none / apiKey / bearer / basic / oauth2), resolved per service
        |
RestUtil              generic fluent HTTP client (builder pattern), timeouts, retry, schema validation
        |
AuthTokenService       domain-specific wrapper for one service's endpoints (the pattern to copy for a 2nd service)
        |
TC_*.java              TestNG test classes
```

Reporting (ExtentReports) and logging (Log4j2) are cross-cutting: `TestInit`
wires them into TestNG's `@BeforeSuite`/`@AfterMethod`/`@AfterSuite` hooks.

## Pre-requisites
* Java 17 or higher
* Apache Maven 3 or higher
* IntelliJ or Eclipse (optional)

## Configuration & environments

Config lives in `src/main/resources/config/`:
* `common.properties` - shared, non-secret defaults (timeouts, retry policy)
* `dev.properties` - safe-to-commit defaults for local/dev use (base URLs, non-secret settings)

`staging.properties` / `prod.properties` are **not** committed (see `.gitignore`) -
those environments are configured entirely through environment variables.

Select the active environment with `-Dapp.env=dev|staging|prod` (defaults to `dev`).
Any config key can be overridden without touching a file:

```
# override a value for one run
mvn test -Dapp.env=staging -Dservice.api-auth.baseUrl=https://staging.example.com

# or via env var (SCREAMING_SNAKE_CASE of the dotted key)
SERVICE_API_AUTH_BASEURL=https://staging.example.com mvn test -Dapp.env=staging
```

**Secrets must never be placed in a properties file.** They resolve exclusively
via environment variables (or a matching `-D` flag), e.g.:

| Auth type | Required env vars |
|---|---|
| `apiKey`  | `SERVICE_<KEY>_AUTH_APIKEY` |
| `bearer`  | `SERVICE_<KEY>_AUTH_TOKEN` |
| `basic`   | `SERVICE_<KEY>_AUTH_USERNAME`, `SERVICE_<KEY>_AUTH_PASSWORD` |
| `oauth2`  | `SERVICE_<KEY>_AUTH_CLIENTID`, `SERVICE_<KEY>_AUTH_CLIENTSECRET` |

(`<KEY>` is the service key, e.g. `USER-SERVICE` → `SERVICE_USER_SERVICE_AUTH_TOKEN`.)
The current demo service (`api-auth`, a bearer-token auth lifecycle sandbox at
[playground.krishanchawla.com](https://playground.krishanchawla.com/scenarios/api-auth/))
uses `authType=none`: the token itself is what's under test, minted and rotated
within each test via `login`/`refresh` rather than injected statically, so there's
no static secret to configure. See `AuthTokenService` for how it attaches the
`Authorization` header per-call instead.

## Adding a second service

1. Add `service.<key>.baseUrl` (and `authType` if not `none`) to the relevant
   `config/*.properties` file.
2. Write a service class following `AuthTokenService`'s shape: call
   `RestUtil.init("<key>").auth(AuthStrategyFactory.forService("<key>"))`,
   then the usual `path/body/expectedStatusCode/...` builder chain.
3. Add JSON schemas for its responses under `src/test/resources/schemas/` and
   wire them in via `.expectedSchema(...)`.

No changes to `RestUtil`, `EnvironmentConfig`, or `AuthStrategyFactory` are needed.

## Schema validation

Each service call validates the response body against a JSON Schema (via
`io.rest-assured:json-schema-validator`), in addition to status code and content
type - see `src/test/resources/schemas/`. This is a lightweight, broker-free
substitute for full consumer-driven contract testing; adopting Pact (or similar)
against a real contract broker is a natural next step but is out of scope here.

## Resilience

`RestUtil` retries a request on transient network failures (connect/socket
timeout) using `http.retry.maxAttempts` / `http.retry.backoffMs` from config.
Assertion failures (wrong status code, schema mismatch, etc.) are never retried.

## Running the tests

```
mvn clean test
```

`testng.xml` declares two `<test>` blocks. `Testing-Classes` runs the fast
`api-auth` tests with `parallel="methods" thread-count="5"`. `Testing-Classes-Slow`
runs `TC_ApiAuthProtectedExpiry` on its own, sequentially - that class does a real
~21s `Thread.sleep` to wait out the sandbox's access-token TTL (verifying the
`expired_token` behavior), which would otherwise tie up a shared thread in the
parallel pool or race other timing-sensitive assertions. The reporting layer
(`ExtentReporter`/`ExtentTestManager`) is thread-safe under parallel execution.

## Reporting

![ExtentReport](https://user-images.githubusercontent.com/28475979/102895706-31b4af80-448b-11eb-94d1-c1e6ef8220b2.PNG)

An ExtentReports HTML report (with full request/response logging - sensitive
headers like `Authorization`/`Cookie` are automatically masked) is generated
per run under `Output/Output_<timestamp>/Reports/`. This directory is
git-ignored; do not commit run artifacts.

## AI-assisted test generation (Claude Code agent)

This repo includes a Claude Code skill/agent pair that turns **business requirements**
- not just an API spec - into a traceable, compiling TestNG module:

* `.claude/skills/add-api-tests/SKILL.md` - the workflow: resolve requirements, detect
  new-service vs. update mode, branch, delegate generation, run a compile-and-verify
  gate, then open a PR (it never merges).
* `.claude/agents/api-test-generator.md` - the subagent that extracts atomic
  requirements, grounds each one technically against a provided OpenAPI spec/examples,
  applies equivalence-partitioning/boundary/decision-table/state-transition test design,
  and generates the config entries, POJOs, schemas, service class and TestNG suite
  following the `AuthTokenService`/`TC_ApiAuthRefresh` conventions above.

A spec (especially one auto-generated from an implementation) describes what an API
*currently does*, not what it's *supposed to do* - so the agent refuses to invent a
business rule, field name, or endpoint from spec shape alone, and every generated test
method's Javadoc cites the requirement it verifies.

Try it against the bundled sample requirements doc:

```
/add-api-tests --requirements requirements/petstore-requirements.md --service petstore \
  --spec https://petstore.swagger.io/v2/swagger.json
```

No pre-generated Petstore test code ships in this repo on purpose - the sample doc is
there so you can run the agent yourself and see the traceability table it produces,
rather than being shown output someone else generated. It always works on a new
`agent/add-api-tests-<service>` branch and opens a PR; it never pushes to or merges
into `main`/`master`.

## Explicitly out of scope for this pass

* CI pipeline wiring (GitHub Actions/Jenkins/etc.)
* Full consumer-driven contract testing (Pact broker)
