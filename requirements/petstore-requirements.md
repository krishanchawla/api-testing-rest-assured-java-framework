# Petstore - Business Requirements (demo)

This is a sample **requirements doc** for the `add-api-tests` skill, meant to be run
live rather than read. Nothing in this repo is generated from it yet - that's the point:
feed it to the agent yourself and watch it produce a traceable test module.

```
/add-api-tests --requirements requirements/petstore-requirements.md --service petstore \
  --spec https://petstore.swagger.io/v2/swagger.json
```

**Technical grounding:** the public Swagger Petstore OpenAPI spec at
`https://petstore.swagger.io/v2/swagger.json` (endpoints, field names, status codes).
Everything below it, the spec cannot tell you - it only describes the current shape of
the API, not what correct behavior is. That's what these requirements are for.

## Pet lifecycle

- **R1** - A pet is created with `status = available` by default if no status is given.
- **R2** - A pet's `status` must be one of `available`, `pending`, `sold`. Any other
  value is rejected with a 400, not silently accepted.
- **R3** - A pet cannot transition directly from `available` to `sold`; it must pass
  through `pending` first (e.g. an order is placed, then confirmed).
- **R4** - Once a pet's status is `sold`, it cannot be modified back to `available` or
  `pending` - a sold pet is terminal.
- **R5** - Fetching a pet by an ID that does not exist returns 404, never a 500.
- **R6** - Fetching a pet by a non-numeric/malformed ID returns 400, not a 500.

## Orders

- **R7** - An order's `quantity` must be a positive integer, minimum 1.
- **R8** - An order's `quantity` cannot exceed 5 per order (a business limit, not a
  spec-declared constraint) - requests above that are rejected with 400.
- **R9** - An order can only be placed for a pet whose current `status` is `available`;
  placing an order for a `pending` or `sold` pet is rejected.
- **R10** - An order can only be marked `shipped` if it was previously `approved` - an
  order cannot jump straight from `placed` to `shipped`.
- **R11** - Deleting an order that does not exist returns 404, never a 500.

## Out of scope for this demo doc

- Store inventory counts and user account endpoints - not covered by these
  requirements; add them as their own `R`-numbered statements if a future pass should
  cover them.
- Anything not stated above should be treated as **not yet specified** rather than
  inferred from the spec - per the skill's guardrails, the agent should ask rather than
  guess if it needs a rule that isn't listed here.
