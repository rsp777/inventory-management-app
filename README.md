# Inventory Management App
Build Status
[![Build and Depploy Artifact to Maven Registry](https://github.com/rsp777/inventory-management-app/actions/workflows/maven-publish-artifact.yml/badge.svg)](https://github.com/rsp777/inventory-management-app/actions/workflows/maven-publish-artifact.yml)

Inventory Management App is a Spring Boot 3 backend and server-rendered UI for warehouse inventory operations, including LPN management, item and category management, location management, inquiry screens, putaway workflows, user access, and operational health endpoints.

## Highlights

- Multi-domain inventory operations: LPN, item, category, location, inventory, settings, and SOP flows
- Spring MVC + Thymeleaf UI with REST-style endpoints under a shared context path
- MySQL-backed persistence with JPA and custom repository adapters
- Actuator health and build metadata endpoints
- Test configuration with in-memory H2 database for repeatable local test runs
- Listener and endpoint management UI with runtime health visibility
- Kafka listener enable/disable support with local and remote control sync
- Bulk listener actions with partial-success reporting and realtime status refresh

## Tech Stack

- Java 17
- Spring Boot 3.1.12
- Spring MVC, Spring Data JPA, Spring Data JDBC
- Thymeleaf
- MySQL 8 connector
- Maven
- JUnit 5 (Spring Boot Starter Test)
- H2 (test scope)

## Project Structure

- src/main/java/com/pawar/inventory/app
  - controller: web and API controllers
  - service: domain services and base services
  - repository: Spring Data repositories and custom implementations
  - model: JPA entities
  - dto: request and response DTOs
- src/main/resources
  - application.properties
  - templates
  - static
- src/test/java
  - controller and context tests
- src/test/resources
  - test application.properties (H2-based)

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL (for local runtime outside tests)

## Configuration

Primary runtime settings are in src/main/resources/application.properties.

Important defaults in this repository:

- Port: 8086
- Context path: /inventory-ui
- Datasource URL: jdbc:mysql://100.66.109.82:3306/menu
- Actuator endpoints exposed: management.endpoints.web.exposure.include=*

Recommended local override:

1. Keep repository defaults untouched.
2. Create an environment-specific override (for example, using JVM system properties or Spring profile file) for:
   - spring.datasource.url
   - spring.datasource.username
   - spring.datasource.password
   - external.api.url

Listener runtime control settings:

- listener.runtime.monitor.enabled=true
- listener.runtime.monitor.interval-ms=30000
- listener.runtime.connect-timeout-ms=2000
- listener.runtime.control-enabled=true
- listener.runtime.control-base-urls=inventory-management-system=http://localhost:8085,inventory-management-app=http://localhost:8086

The `listener.runtime.control-base-urls` property supports:

- `key=value` mappings for service-based listener control
- plain URLs, which are auto-mapped as `service1`, `service2`, and so on

## Build

Run a full build:

mvn clean package

Build output:

- target/*.jar

## Run Locally

Run from Maven:

mvn spring-boot:run

Or run jar directly:

java -jar target/inventory-management-app-*.jar

Application base URL:

- http://localhost:8086/inventory-ui

## Test

Run all tests:

mvn test

Run full package with tests:

mvn clean package

Notes:

- Test configuration uses H2 in-memory datasource from src/test/resources/application.properties.
- Kafka autoconfiguration is disabled in tests.

## Manual Smoke Checks

Health endpoint:

- http://localhost:8086/inventory-ui/actuator/health

Example checks (PowerShell):

Invoke-WebRequest -UseBasicParsing http://localhost:8086/inventory-ui/actuator/health
Invoke-WebRequest -UseBasicParsing http://localhost:8086/inventory-ui/api/auth/index
Invoke-WebRequest -UseBasicParsing http://localhost:8086/inventory-ui/settings
Invoke-WebRequest -UseBasicParsing http://localhost:8086/inventory-ui/lpn/create

## API and UI Notes

- Many compatibility routes in MenuController redirect to focused domain controllers.
- Main user-facing pages are Thymeleaf templates under src/main/resources/templates.
- OpenAPI dependency is present (springdoc), so Swagger UI can be enabled/used based on runtime config.

### Listener Management UI

The endpoint/listener page now includes:

- realtime listener runtime refresh every 10 seconds
- runtime status badges: `connected`, `disconnected`, `disabled`, `unknown`
- bulk actions: `Enable Selected`, `Disable Selected`, `Enable All`, `Disable All`
- partial-success bulk result summaries with failed and skipped item details
- selection persistence across search, filter changes, and realtime refreshes
- listener copy support and service-key based remote-control routing

### Endpoint Read Model

Endpoint records shown in the UI are derived from menu definitions and exposed through `/inventory-ui/api/endpoints`.
They are intended as an operational read view, not a separate editable endpoint registry.

## Container and Deployment

### Docker

A Dockerfile is included and exposes port 8086.

Build image:

docker build -t inventory-management-app .

Run container:

docker run -p 8086:8086 inventory-management-app

Note: Ensure Dockerfile jar name matches actual generated jar in target.

### Kubernetes

- deployment.yaml
- service.yaml
- kustomization.yaml

Apply manifests:

kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

## CI

GitHub Actions workflow `.github/workflows/ci.yml` runs on:

- pull requests targeting `main`
- pushes to `main`

Pipeline steps:

- install dependencies (`bash ./mvnw dependency:go-offline`)
- run tests (`bash ./mvnw test`)
- build package (`bash ./mvnw -DskipTests package`)
- run a safe deploy placeholder job on pushes to `main` (no external deployment is executed)

A Jenkinsfile is also included with stages:

- Preparation
- Build (mvn clean package)
- Archive Artifacts (target/*.jar)

## Branching Strategy

Recommended workflow for this repository:

- `main`: production-approved baseline only
- `Dev_v2.6`: current stable integration branch for the Dev_v2.6 release line
- feature branches created from `Dev_v2.6` for isolated work

Suggested branch naming:

- `feature/listener-management-ui`
- `feature/ui-foundation`
- `feature/common-table-ux`
- `feature/role-aware-actions`
- `feature/saved-filters`

Branching rule:

1. create feature branches from `Dev_v2.6`
2. keep `Dev_v2.6` merge-ready and stable
3. merge tested features back into `Dev_v2.6`
4. promote `Dev_v2.6` to `main` only after validation

## Troubleshooting

- Port in use:
  - change server.port or stop the process using 8086
- Database connection errors:
  - verify datasource URL, credentials, and network access
- External API dependency errors:
  - verify external.api.url and timeout settings
- Build/test failures:
  - start with mvn clean package and inspect target/surefire-reports

## Documentation

Additional project documentation is available in repository root, including:

- REFACTORING_GUIDE.md
- PROGRAM_STATUS.md
- GENERALIZED_MIGRATION_PHASES.md
- COMPLETION_REPORT.md

## License

No license file is currently defined in this repository.
