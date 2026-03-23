# Inventory Management App

Inventory Management App is a Spring Boot 3 backend and server-rendered UI for warehouse inventory operations, including LPN management, item and category management, location management, inquiry screens, putaway workflows, user access, and operational health endpoints.

## Highlights

- Multi-domain inventory operations: LPN, item, category, location, inventory, settings, and SOP flows
- Spring MVC + Thymeleaf UI with REST-style endpoints under a shared context path
- MySQL-backed persistence with JPA and custom repository adapters
- Actuator health and build metadata endpoints
- Test configuration with in-memory H2 database for repeatable local test runs

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

A Jenkinsfile is included with stages:

- Preparation
- Build (mvn clean package)
- Archive Artifacts (target/*.jar)

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
