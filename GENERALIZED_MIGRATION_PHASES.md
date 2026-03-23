# Generalized Migration Phases

This document defines a reusable migration workflow that can be applied across Java, .NET, Python, and mixed-service projects. It is intentionally technology-agnostic at the phase level and specific at the deliverable level so it can be reused on future projects without rewriting the plan from scratch.

## How To Use This

Use these phases in order unless a project has a hard constraint that forces a different sequence.

For each phase, capture:

- objective
- scope
- entry criteria
- key activities
- deliverables
- exit criteria
- validation method

## Phase 0: Discovery And Baseline

Objective: understand the current system well enough to migrate without breaking ownership, runtime assumptions, or deployment expectations.

Typical activities:

- inventory applications, modules, services, databases, queues, external APIs, and scheduled jobs
- document current architecture, environments, and deployment topology
- identify framework versions, runtime versions, and critical dependencies
- map core user flows and business-critical paths
- capture current build, test, and deployment commands
- record known defects, operational risks, and technical debt

Deliverables:

- system inventory
- dependency inventory
- environment matrix
- baseline build/test report
- risk register

Exit criteria:

- the team can build the system reproducibly
- critical runtime dependencies are known
- top migration risks are documented

Validation:

- successful baseline build
- smoke test of current application behavior

## Phase 1: Foundation Refactoring

Objective: reduce structural friction before changing behavior or platform dependencies.

Typical activities:

- remove dead code and obsolete adapters where safe
- normalize dependency injection and component wiring
- standardize logging and exception handling patterns
- isolate cross-cutting concerns such as auth, config, session handling, and utilities
- reduce controller or entrypoint bloat by moving logic into services/helpers

Deliverables:

- cleaner project structure
- reduced duplicate logic
- standardized wiring and logging patterns

Exit criteria:

- core flows still behave the same
- refactored code compiles cleanly
- no new warnings or regressions in touched areas

Validation:

- compile/build passes
- targeted regression tests or manual flow checks

## Phase 2: Service And Domain Normalization

Objective: stabilize business logic boundaries so later migration work is performed in the correct layer.

Typical activities:

- clarify service responsibilities and ownership
- split oversized services into focused units where necessary
- normalize naming and public contracts
- standardize DTO, mapper, and domain model boundaries
- move placeholder or duplicated logic to the right service layer

Deliverables:

- focused service boundaries
- stable domain contracts
- updated internal call graph documentation

Exit criteria:

- business logic is no longer spread arbitrarily across controllers, repositories, and utilities
- duplicated service logic is removed or intentionally documented

Validation:

- compile/build passes
- service-level regression checks pass

## Phase 3: Repository And Integration Improvements

Objective: make persistence and external integrations explicit, testable, and migration-ready.

Typical activities:

- decompose monolithic repository or gateway classes
- separate database access from external API access
- standardize transactions and error propagation
- normalize repository contracts and query ownership
- remove hidden service-to-repository or circular dependency patterns

Deliverables:

- repository ownership map
- integration adapter inventory
- transaction boundary review

Exit criteria:

- data access and external integration flows are understandable and isolated
- repository changes do not alter expected behavior

Validation:

- compile/build passes
- integration smoke checks pass
- affected queries and adapters are manually or automatically verified

## Phase 4: Error Handling, Reliability, And Observability

Objective: make failures diagnosable and predictable before deeper migration steps.

Typical activities:

- standardize custom exceptions and response models
- unify validation and error translation
- add structured logs and meaningful warning/error messages
- confirm health endpoints, readiness checks, and shutdown behavior
- improve null handling, default behavior, and retry boundaries

Deliverables:

- error-handling guide
- controller/service exception map
- operational observability checklist

Exit criteria:

- failures surface consistently
- operational debugging no longer depends on ad hoc print statements or hidden exceptions

Validation:

- compile/build passes
- negative-path tests or manual checks pass

## Phase 5: Contract, DTO, And Entity Hardening

Objective: stabilize the application contract before version, platform, or infrastructure migration.

Typical activities:

- clean up DTOs, entities, request models, and response models
- remove ambiguous fields and naming drift
- centralize constants and repeated literal values
- confirm serialization, date/time, enum, and validation behavior
- preserve backward compatibility where external consumers depend on it

Deliverables:

- contract matrix
- DTO/entity cleanup summary
- compatibility notes

Exit criteria:

- contracts are explicit
- repeated literals and drift-prone fields are minimized
- compatibility assumptions are documented

Validation:

- compile/build passes
- API or UI contract checks pass

## Phase 6: Configuration, Deployment, And Runtime Readiness

Objective: make the migrated system buildable, deployable, and operable across environments.

Typical activities:

- centralize configuration keys, environment variables, and secrets handling
- validate packaging, startup, and deployment artifacts
- verify Docker, Kubernetes, CI/CD, or hosting configuration as applicable
- confirm database, queue, cache, and external service connectivity assumptions
- test startup, shutdown, and basic runtime health endpoints

Deliverables:

- deployment artifact
- runtime configuration checklist
- environment-specific overrides
- smoke test report

Exit criteria:

- deployable artifact is created successfully
- application starts in the target environment or blockers are documented precisely
- smoke tests demonstrate expected baseline behavior

Validation:

- package/build succeeds
- application starts successfully
- health or smoke endpoint responds as expected

## Cross-Phase Rules

- keep changes incremental and compile-safe
- validate after every meaningful batch
- do not mix large behavior changes with wide structural refactors unless necessary
- keep one authoritative status file for phase tracking
- document blockers with evidence, not guesses
- preserve compatibility routes and contracts until cutover is complete

## Reusable Status Template

Use this table in any project:

| Phase | Name | Status | Notes | Validation |
|---|---|---|---|---|
| 0 | Discovery And Baseline | Not Started |  |  |
| 1 | Foundation Refactoring | Not Started |  |  |
| 2 | Service And Domain Normalization | Not Started |  |  |
| 3 | Repository And Integration Improvements | Not Started |  |  |
| 4 | Error Handling, Reliability, And Observability | Not Started |  |  |
| 5 | Contract, DTO, And Entity Hardening | Not Started |  |  |
| 6 | Configuration, Deployment, And Runtime Readiness | Not Started |  |  |

## Minimum Evidence To Close A Phase

- code changes linked to the phase objective
- build or compile output showing the touched code is valid
- notes on what was intentionally left unchanged
- known risks or blockers for the next phase

## Recommended Final Exit Criteria For Any Migration

- the target artifact builds cleanly
- the application starts successfully in a representative environment
- critical user paths are smoke-tested
- operational dependencies are verified or explicitly called out as blockers
- the final migration summary names completed phases, remaining risks, and follow-up work

## Reusable Validation Status Block

Use this block verbatim in project reports after verification:

| Validation Area | Result | Evidence |
|---|---|---|
| Build | Pass | `mvn clean package` -> BUILD SUCCESS (or equivalent build command) |
| Automated Tests | Pass/Fail | `Tests run: X, Failures: Y, Errors: Z` |
| Application Startup | Pass/Fail | App starts and binds expected port/context path |
| Health Check | Pass/Fail | Health endpoint returns `UP` |
| Critical Manual Routes | Pass/Fail | Key GET/POST flows return expected status/redirect |
| External Dependencies | Pass/Fail/Blocked | DB, queue, API, auth provider verified or explicitly blocked |

Status note template:

- Validation completed on <date>
- Overall status: <Green/Amber/Red>
- Open blockers: <none or list>