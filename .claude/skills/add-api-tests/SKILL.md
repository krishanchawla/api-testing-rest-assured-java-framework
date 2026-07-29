---
name: add-api-tests
description: Turn business requirements (plus a spec or example request/responses for technical grounding) into a traceable, compiling TestNG test suite scaffolded into this framework's conventions. Use when a team member wants to add or update automated API tests for a service/module in this repo.
argument-hint: --requirements <path|url|"interactive"> --service <key> [--spec <path|url>] [--examples <path>] [--env dev|staging|prod]
---

# add-api-tests

Scaffolds a new (or updates an existing) service test module into this framework, driven by
business requirements rather than an API spec. Requirements define what "correct" behavior is and
therefore what gets tested; a spec or example request/responses are only used as technical grounding
(endpoint paths, field names, status codes) for making the requirement-derived tests executable.

**Do not generate tests directly from a spec's shape alone.** A spec (especially one auto-generated
from the implementation) describes what the code currently does, not what it's supposed to do. If no
requirements are available for an operation, ask for them (see step 1) rather than falling back to
spec-only generation.

## Procedure

### 1. Resolve inputs

Parse `--requirements`, `--service`, `--spec`, `--examples`, `--env` (default `dev`) from the
invocation. If invoked with no arguments, ask for at least `--requirements` and `--service` via
`AskUserQuestion` before doing anything else.

- **`--requirements` (required).**
  - A file path or URL: read it directly (`Read` for a local path, `WebFetch` for a URL).
  - The literal string `interactive`, or omitted entirely: walk the user through describing the
    module's behavior and workflows via `AskUserQuestion`, one topic at a time (list the operations
    you believe are in scope, or ask them to name the operations first if unknown). Write down
    exactly what they say - this transcript becomes the requirements source for citation purposes.
  - Either way, end this step with a list of atomic, ID-tagged requirement statements (e.g. `R1`,
    `R2`, ...). If the source text is unstructured prose, split it into these atomic statements
    yourself; don't demand the user pre-format it.

- **Technical grounding (`--spec` and/or `--examples`).** Accept either or both. Neither is strictly
  required up front - if an operation referenced by a requirement has no matching spec entry or
  example, you will ask for one when you get to that requirement in step 3, not before.

- **`--service`.** If omitted, derive a kebab-case key from the requirements doc's title or the
  spec's `info.title`. Always confirm the derived key with the user via `AskUserQuestion` before
  proceeding - it becomes a permanent config key (`service.<key>.*`) and is annoying to rename later.

### 2. Detect new vs. update mode

Check `src/main/resources/config/common.properties` and `src/main/resources/config/<env>.properties`
for an existing `service.<key>.baseUrl` entry.
- **Not found → new-service mode.**
- **Found → update mode.** The regeneration trigger is a *changed requirement set*, not a changed
  spec - diff the newly extracted requirement IDs/text against the requirement citations already
  present in `src/test/java/TC_<Key>.java`'s Javadoc (see the subagent's update-mode behavior).

### 3. Create a working branch

```
git checkout -b agent/add-api-tests-<service-key>
```

Never operate directly on `main`/`master`. If the working tree is dirty with unrelated changes, stop
and tell the user rather than branching over uncommitted work.

### 4. Delegate generation to the `api-test-generator` subagent

Invoke it via the Agent tool with a **self-contained** prompt (it has no memory of this
conversation) that includes:
- The full requirement list (ID + text) from step 1.
- Any spec content/path and any example request/response content/path from step 1.
- The resolved service key and target env.
- Explicit pointers to the framework files it should pattern-match against:
  `src/main/java/framework/utils/common/RestUtil.java`,
  `src/main/java/framework/service/AuthTokenService.java`,
  `src/main/java/framework/auth/AuthStrategyFactory.java`,
  `src/test/resources/schemas/apiauth/token-pair.schema.json`,
  `src/test/java/TC_ApiAuthRefresh.java` (for the `@DataProvider` + JSON testdata pattern),
  `src/main/java/framework/config/EnvironmentConfig.java`.
- Whether this is new-service or update mode (and, if update mode, the existing generated files to
  reconcile against).

### 5. Verification gate (hard requirement - do not skip or soften this)

1. `mvn -q -DskipTests compile` - must succeed. (Already pre-approved in
   `.claude/settings.local.json`, so this won't prompt for permission.)
2. `mvn -q -DskipTests test-compile` - must succeed.
3. Best-effort: if `service.<key>.baseUrl` is reachable, run
   `mvn test -Dtest=TC_<Key>` and report the *actual* pass/fail result. If the target is
   unreachable, say so explicitly in the summary - never imply a test passed without having run it.

If step 1 or 2 fails, send the compiler output back to the subagent to fix before proceeding; do not
hand a non-compiling module to the user.

### 6. Commit and open a PR

```
git add <changed files>
git commit -m "Add <service-key> test module (requirements-driven)"
git push -u origin agent/add-api-tests-<service-key>
gh pr create --title "Add <service-key> API tests" --body "<summary>"
```

The PR body must include:
- A **requirement → test traceability table** (requirement ID/text → test method(s) that verify it).
- Which requirements came from the doc vs. interactive elicitation.
- Files added/changed.
- Required environment variables for auth secrets, if any (never the secret values themselves - see
  `AuthStrategyFactory`'s `SERVICE_<KEY>_AUTH_*` convention).
- The actual verification results from step 5, including whether the live test run happened or was
  skipped (and why).

**Stop here.** This skill never merges the PR - that's a human decision.

## Guardrails (apply throughout, not just at the steps above)

- Never invent a business rule, field name, or endpoint path. Every test traces to a cited
  requirement; every technical detail traces to a provided spec/example or a direct answer from the
  user.
- Never describe generated tests as "contract tests" - they are requirement-driven tests that happen
  to use a spec/examples for technical grounding, not an independently negotiated contract.
- Never write a secret value into any `*.properties` file.
- Never push directly to `main`/`master`, and never merge the PR this skill opens.
- In update mode, never silently delete a test whose requirement disappeared - flag it in the PR
  summary for a human to decide.
