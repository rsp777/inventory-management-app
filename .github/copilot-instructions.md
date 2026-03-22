# Copilot Instructions for Inventory Management App

## Project Overview
Spring Boot 3.1.12 inventory management application (Java 17) with multi-tenant warehouse operations. Serves as a backend/UI for LPN (License Plate Number), item, and location management with role-based access control and transaction logging.

## Architecture Patterns

### Layered MVC Structure
- **Controllers** (`src/main/java/com/pawar/inventory/app/controller/`): Handle HTTP requests, use `@Controller` with Thymeleaf templates or `@ResponseBody` for REST endpoints
- **Services** (`service/`): Implement business logic with `@Service` and `@Transactional` annotations. Follow naming: interface (`MenuService.java`) + implementation (`MenuServiceImp.java`)
- **Repositories** (`repository/`): Extend `JpaRepository<Entity, ID>` for basic CRUD; use `MenuRepositoryCustom` interface + `MenuRepositoryCustomImp` for complex queries
- **Models** (`model/`): JPA entities with `@Entity` annotations; defines domain objects like `Menu`, `Lpn`, `Item`, `Location`
- **DTOs** (`dto/`): Transfer objects for request/response (e.g., `ListenerDTO`, `ListenerRequestDTO`)

### Data Flow Example
1. Controller receives HTTP request → calls Service
2. Service calls Repository (standard or custom) → executes business logic
3. Repository queries MySQL database (via JPA/JDBC)
4. Results mapped to Model entities → optionally converted to DTOs → returned to Controller
5. Controller renders Thymeleaf template or returns JSON via `@ResponseBody`

### External Dependencies
- **MySQL Database**: URL `jdbc:mysql://100.66.109.82:3306/menu` (configurable in `application.properties`)
- **sop-http2**: External dependency (`com.pawar.inventory:sop-http2:Dev_v2.1`) for SOP (Standard Operating Procedure) operations
- **common**: Shared library (`com.pawar.todo:common`) for cross-project utilities
- **Spring Kafka** (v3.2.2): Disabled in config but configured for async message processing if activated
- **JWT Authentication**: Dual token libraries (`io.jsonwebtoken:jjwt` v0.9.1 + `com.auth0:java-jwt` v4.2.1)

## Key Conventions

### Exception Handling
- Custom exceptions with `@ResponseStatus` in `exceptionhandler/` package: `MenuNotFoundException`, `ParentMenuNotFoundException`, `UnauthorizedException`
- Handled by `CustomExceptionHandler.java` (likely global exception handler)
- Always throw domain-specific exceptions rather than generic `Exception`

### Service Implementation Naming
- **Inconsistent naming**: Interface `MenuService.java` but implementation is `MenuServiceImp.java` (not `MenuServiceImpl`)
- When adding services, follow this pattern: `*Imp` suffix for implementations

### Repository Custom Queries
- For complex queries, create custom interface (`MenuRepositoryCustom`) implementing helper methods
- Suffix implementation with `Imp`: `MenuRepositoryCustomImp`
- Custom repository methods called by services (e.g., `newLpn()`, `getItem()`, `getfindAllCategories()`)

### View Layer (Thymeleaf)
- Templates in `src/main/resources/templates/`: `.html` files processed by Thymeleaf
- Supports dynamic content via Spring model binding: `model.addAttribute("key", value)`
- JS assets in `static/js/` (e.g., `settings.js` for client-side logic)
- Images in `static/images/`

### Logging Configuration
- **Logback** (`logback.xml`): Logs to console (INFO level) + rolling daily files (`logs/invnapp.log`)
- Java logging via `java.util.logging.Logger` in classes
- Spring/Kafka logs disabled (level OFF) to reduce noise

### Database Configuration
- **Hikari connection pool**: min 10, max 100 connections; keepalive 240000ms
- **JPA DDL**: `update` mode (auto-creates/updates tables)
- **MySQL 8.0.33 connector**; timestamp serialization as ISO-8601
- Multiple datasource URLs commented for environment switching

## Build & Deployment

### Maven Build
```bash
mvn clean package  # Creates JAR in target/
./mvnw clean package  # Cross-platform wrapper
```

### Docker Deployment
- Base image: `openjdk:22-jdk-slim`
- JAR name: `InventoryManagementApp-0.0.1-SNAPSHOT.jar`
- Container exposes port 8086
- Run: `docker build -t inventory-app . && docker run -p 8086:8086 inventory-app`

### Kubernetes Manifests
- `deployment.yaml`: Pod spec with container config
- `service.yaml`: Exposes app via K8s service
- `kustomization.yaml`: Kustomize overlays
- Base image in Dockerfile must match JAR filename in deployment.yaml

### CI/CD (Jenkins)
- `Jenkinsfile` (Groovy): Stages = Preparation (git clone) → Build (mvn clean package) → Archive Artifacts
- Checkout branch: `feature-regsisteration/invnapp` (note: typo in original)
- Maven tool configured as 'M3' in Jenkins

## Development Workflow

### Project Structure
- Source: `src/main/java/com/pawar/inventory/app/`
- Resources: `src/main/resources/` (application.properties, logback.xml, templates/, static/)
- Tests: `src/test/java/` (limited coverage in repo)
- Package: `com.pawar.inventory.app`

### Common Tasks
1. **Add new entity**: Create model in `model/` → add JPA annotations (`@Entity`, `@Table`, `@Id`)
2. **Add new endpoint**: Create controller method with routing (`@GetMapping`, `@PostMapping`, etc.) → call service → return view or JSON
3. **Add custom query**: Extend `MenuRepositoryCustom` interface + implement in `MenuRepositoryCustomImp`
4. **Add event listener**: Create event class in `events/` → implement listener in `listeners/`
5. **Handle new exception**: Create exception in `exception/` with `@ResponseStatus` → throw from service

### Key Configuration Properties
- `server.port=8086`
- `server.servlet.context-path=/inventory-ui`
- `spring.datasource.url`: Change for different database instances
- `spring.jpa.hibernate.ddl-auto=update`: Auto-schema updates (use `validate` for production)

## Important Notes
- Application scans base packages: `com.pawar.inventory.app` + `com.pawar.sop.http` (external SOP library)
- Tomcat excluded from spring-boot-starter-web (uses embedded server or other container)
- Logback custom (not Spring default logging)
- No unit tests visible in provided structure; add tests to `src/test/java/`
