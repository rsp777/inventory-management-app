# Inventory Management App - Codebase Reorganization & Refactoring Guide

> Program Status Sync (March 23, 2026): See PROGRAM_STATUS.md for the latest cross-phase status and TOMORROW_TODO.md for the next-session resume checklist.

## Latest Update (March 23, 2026)

This section reflects the latest status from the ongoing implementation chat and supersedes older progress notes where they conflict.

### Current Phase Status (Authoritative)

| Phase | Name | Status |
|---|---|---|
| 1 | Foundation Refactoring | Completed |
| 2 | Service Normalization | Completed |
| 3 | Repository Improvements | In Progress (active focus) |
| 4 | Exception Handling | Completed |
| 5 | DTO and Entity Improvements | Completed |
| 6 | Configuration and Constants | Started / In Progress |

### Phase 3 Progress Since Last Report

- Replaced remaining adapter pass-through patterns with direct ExternalApiService usage in:
  - CategoryRepositoryCustomImpl
  - ItemRepositoryCustomImpl
  - LocationRepositoryCustomImpl
  - InventoryRepositoryCustomImpl
  - SopRepositoryCustomImpl
  - MenuAuthRepositoryCustomImpl
- Added/used narrow endpoint-based fast paths to reduce broad read/filter behavior.
- Updated MenuAccessServiceImpl getUsers path to call ExternalApiService instead of manual HTTP client + direct URL plumbing.
- Kept compile green after each incremental slice (latest checks successful).
- Maintained strict isolated staging for Phase 3 files to avoid unrelated worktree noise.

### Current Phase 3 Isolated Slice

The active isolated Phase 3 slice currently includes repository/service files centered on decomposition and read-path narrowing:

- src/main/java/com/pawar/inventory/app/repository/MenuRepositoryCustom.java
- src/main/java/com/pawar/inventory/app/repository/MenuRepositoryCustomImpl.java
- src/main/java/com/pawar/inventory/app/repository/CategoryRepositoryCustomImpl.java
- src/main/java/com/pawar/inventory/app/repository/ItemRepositoryCustomImpl.java
- src/main/java/com/pawar/inventory/app/repository/LocationRepositoryCustomImpl.java
- src/main/java/com/pawar/inventory/app/repository/InventoryRepositoryCustomImpl.java
- src/main/java/com/pawar/inventory/app/repository/LpnRepositoryCustomImpl.java
- src/main/java/com/pawar/inventory/app/repository/SopRepositoryCustomImpl.java
- src/main/java/com/pawar/inventory/app/repository/MenuAuthRepositoryCustomImpl.java
- src/main/java/com/pawar/inventory/app/service/MenuAccessServiceImpl.java

## Overview
This document tracks the ongoing reorganization, generalization, and normalization work across the Inventory Management App codebase. The main goals are to reduce duplication, improve maintainability, clarify service and repository boundaries, and make future feature work safer.

## Current Snapshot

### Completed or Largely Completed
- Base support classes exist: `AbstractBaseRepository`, `AbstractBaseService`, `BaseException`
- Shared utilities exist: `MenuFilterUtil`, `ResponseUtil`, `ControllerReflectionUtil`
- Domain controllers exist for Auth, Menu Navigation, Category, Item, Location, LPN, Inventory, Settings, Listener, and TransactionLog
- Domain repository adapters exist for Category, Item, Location, LPN, Inventory, SOP workflows, and menu/auth workflows
- `ExternalApiService` now routes HTTP execution through centralized repository HTTP handling
- TransactionLog has been improved with DTO-based request and response handling, validation, safer service semantics, and entity audit lifecycle hooks

### In Progress
- Controller normalization is partially complete, but some read-heavy view flows still use broad dataset loading
- Repository consolidation is in progress, but `MenuRepositoryCustomImp` still owns mixed responsibilities
- Exception handling is centralized, and `BaseException` handler wiring has started
- DTO adoption has started, but there is no shared mapping strategy yet

Latest milestone:
- `MenuServiceImpl` no longer has direct `MenuRepositoryCustom` calls; domain adapters now mediate Category, Item, Location, LPN, Inventory, SOP, and Menu/Auth flows

### Not Yet Standardized
- Full adoption of `AbstractBaseRepository`
- Full adoption of `AbstractBaseService`
- Unified logging strategy across the application
- Shared DTO base class or mapper utility
- Full split of auth/menu mutation flows away from `MenuRepositoryCustomImp`

## Phase 1: Foundation Refactoring (Completed)

### 1.1 Base Classes

#### AbstractBaseRepository
Path: `repository/base/AbstractBaseRepository.java`

Purpose:
- Centralize HTTP client operations and JSON serialization

Benefits:
- Reduces repeated HTTP client setup
- Standardizes GET, POST, PUT, and DELETE helpers
- Centralizes JSON conversion behavior

Key methods:
```java
httpGetAsString(String url)
httpGetAsObject(String url, Class<T> typeClass)
httpGetAsList(String url, TypeReference<T> typeRef)
httpDelete(String url)
httpPostForObject(String url, String jsonPayload)
httpPutForObject(String url, String jsonPayload)
toJson(Object object)
fromJson(String json, Class<T> typeClass)
```

Current status:
1. Implemented.
2. Used by `ExternalApiService`.
3. Not yet fully adopted by `MenuRepositoryCustomImp`.

#### AbstractBaseService
Path: `service/base/AbstractBaseService.java`

Purpose:
- Provide shared service-level logging and validation helpers

Benefits:
- Creates a consistent place for common service behavior
- Avoids repeating simple validation utilities

Key methods:
```java
logInfo(String message)
logWarning(String message)
logError(String message, Exception e)
validateNotNull(Object obj, String fieldName)
validateNotEmpty(String value, String fieldName)
```

Current status:
1. Implemented.
2. Extended by `ValidationService`.
3. Not yet broadly adopted by service implementations.

#### BaseException
Path: `exception/base/BaseException.java`

Purpose:
- Provide a common exception base with HTTP status and metadata

Current status:
1. Implemented.
2. Not yet used as the main parent for existing domain exceptions.

### 1.2 Shared Utilities

#### MenuFilterUtil
Path: `util/MenuFilterUtil.java`

Purpose:
- Extract repeated menu categorization logic from controllers

Impact:
- Reduces repeated `RF` and `UI` menu grouping code
- Keeps controller methods smaller and more consistent

#### ResponseUtil
Path: `util/ResponseUtil.java`

Purpose:
- Standardize common response and view helper behavior

Impact:
- Reduces repeated response boilerplate
- Supports consistent view-model setup

#### ControllerReflectionUtil
Path: `util/ControllerReflectionUtil.java`

Purpose:
- Remove duplicated reflection helper logic from controllers that work with generic service return types

Impact:
- Centralizes fallback getter access
- Caches method lookups to reduce repeated reflection cost
- Removes duplicate helper code from `LpnController`, `LocationController`, and `InventoryController`

Key methods:
```java
toList(Iterable<T> iterable)
invokeGetter(Object target, String getterName)
extractInt(Object target, String... getterNames)
extractString(Object target, String... getterNames)
```

### 1.3 Controller Reorganization

The original `MenuController` was too large and mixed multiple domains. The split into domain-focused controllers is now mostly complete.

#### Created Controllers
- `AuthController`
- `MenuNavigationController`
- `CategoryController`
- `ItemController`
- `LocationController`
- `LpnController`
- `InventoryController`
- `SettingsController`
- `ListenerController`
- `TransactionLogController`

#### Current Status by Controller

Auth:
- Responsibility: Authentication and session management
- Status: Created

MenuNavigation:
- Responsibility: Menu display and menu administration flows
- Status: Created

Category:
- Responsibility: Category CRUD operations
- Status: Created

Item:
- Responsibility: Item CRUD operations
- Status: Created

Location:
- Responsibility: Location CRUD operations
- Status: Created and service-backed
- Progress: location get-by-id and search-by-code lookup logic moved from controller to repository/service

LPN:
- Responsibility: LPN management flows
- Status: Created and partially normalized to service-backed reads and writes
- Progress: get-by-id and search lookup logic moved from controller to repository/service

Inventory:
- Responsibility: Inventory inquiry and aggregation flows
- Status: Created and service-backed
- Progress: query/filter paths moved from controller to repository/service methods

Settings:
- Responsibility: User and system settings screens
- Status: Created
- Remaining issue: still a candidate for cleanup during controller normalization

Listener:
- Responsibility: Listener CRUD and status operations
- Status: Created with DTO-based request handling

TransactionLog:
- Responsibility: Transaction log CRUD API
- Status: Created and recently improved
- Improvements already applied:
  - DTO-based request and response models
  - `@Valid` validation on write endpoints
  - safer create, update, and delete behavior
  - audit timestamp handling with entity lifecycle hooks

## Phase 2: Service Normalization (Completed)

### Current State
- `MenuService` uses `MenuServiceImpl`
- `MenuAccessService` uses `MenuAccessServiceImpl`
- Repository implementations now use `Impl` naming consistently (for example, `MenuRepositoryCustomImpl`)
- `ValidationService` exists
- `ExternalApiService` exists
- TransactionLog now uses a cleaner DTO-based service boundary

Recent progress:
- Repository implementations were migrated from `Imp` to `Impl` naming for consistency across the codebase
- Obsolete duplicate `*Imp.java` repository implementation files were removed so only `*Impl.java` sources remain
- `AbstractBaseService` now uses SLF4J-style logging helpers instead of `java.util.logging`
- `TokenService` and `ValidationService` now rely fully on shared base logging utilities
- `MenuServiceImpl` now extends `AbstractBaseService` and uses shared logging helpers for core menu mutation flow
- `ExternalApiService` now uses SLF4J logging patterns consistently
- `AbstractBaseRepository`, `MenuFilterUtil`, `ResponseUtil`, and `LpnRepositoryCustomImpl` now use SLF4J logging patterns consistently
- Remaining controller classes (`AuthController`, `CategoryController`, `ItemController`, `LocationController`, `LpnController`, `InventoryController`, `MenuNavigationController`, and `SettingsController`) now use SLF4J logging patterns instead of `java.util.logging`
- `MenuAccessServiceImpl#getAccessibleMenus(...)` now queries access by user role IDs and fetches only matching menus, replacing the previous full-menu in-memory filtering path
- `MenuServiceImpl#getAllMenus`, `MenuAccessServiceImpl#getMenuAccesses`, and `ListenerServiceImpl#getAllListeners` now use ordered repository methods for deterministic database-side retrieval

### Action Items
1. Keep `Impl` naming as the standard for all new repository implementation classes.
2. Continue applying `AbstractBaseService` only to services where shared validation/logging provides clear value.
3. Continue replacing remaining broad read paths with purpose-built repository queries where those flows still fetch full datasets for UI rendering.

## Phase 3: Repository Improvements (In Progress)

### Current State
- `MenuRepositoryCustomImp` still mixes endpoint resolution, HTTP orchestration, and domain-specific repository logic
- Domain-specific adapters exist for Category, Item, Location, LPN, Inventory, and SOP workflows
- `httpCall(...)` is now the central HTTP execution path for several external flows
- Some create-style HTTP methods in `MenuRepositoryCustomImp` were corrected from `GET` to `POST`

### Action Items
1. Continue shrinking `MenuRepositoryCustomImp`.
2. Move remaining domain-specific operations behind focused repository interfaces (especially auth/menu mutation flows).
3. Continue reviewing HTTP verb correctness where any create-like flow may still be using `GET`.
4. Keep controller methods focused on orchestration by delegating lookups/searches to service/repository contracts.

## Phase 4: Exception Handling (Completed)

### Current State
- `CustomExceptionHandler` centralizes not found, duplicate, validation, and generic exception handling
- Validation handling was corrected to catch Spring Web `MethodArgumentNotValidException`
- `BaseException` is now explicitly handled in `CustomExceptionHandler`
- Core runtime exceptions (`ResourceNotFoundException`, `DuplicateResourceException`, `ValidationException`, `UnauthorizedException`) now extend `BaseException`
- Remaining domain exceptions (`MenuNotFoundException`, `ParentMenuNotFoundException`, `MenuAssignmentException`, `RoleDeletionException`) now also extend `BaseException`

### Action Items
1. Expand `BaseException` adoption to any newly introduced domain exceptions.
2. Reduce overlapping specific exception handlers where appropriate.
3. Standardize error payload structure and add stable error codes where useful.

## Phase 5: DTO and Entity Improvements (Completed)

### Current Progress
- `ListenerDTO` and `ListenerRequestDTO` exist and are in use
- `TransactionLogDTO` and `TransactionLogRequestDTO` exist and are in use
- `CategoryRequestDTO` is now used for category update write endpoints instead of direct entity binding
- `ItemRequestDTO` and `LocationRequestDTO` are now used for item/location write endpoints instead of request-parameter-heavy bindings
- `LpnCreateRequestDTO`, `LpnUpdateRequestDTO`, `LpnMoveRequestDTO`, `SettingUpdateRequestDTO`, `UserPreferencesRequestDTO`, and `SopConfigRequestDTO` now cover the remaining LPN and settings write endpoints
- Legacy `MenuController` item, location, and LPN write flows now also bind validated DTOs instead of raw form parameter lists
- Legacy `MenuController` menu definition, sign-in, user creation, batch, and SOP location-range flows now also bind validated DTOs instead of raw request parameter bundles
- `MenuNavigationController` is now the single owner of menu definition routes (`/showMenu`, `/addMenu`, `/updateMenu`) and uses DTO-backed menu bindings
- `AuthController` sign-in now also binds a validated DTO instead of raw request parameters
- `TransactionLog` now manages audit timestamps with `@PrePersist` and `@PreUpdate`

Recent progress:
- Item and location write endpoints now bind validated DTOs instead of many individual request parameters
- LPN and settings write endpoints now bind validated DTOs instead of raw request parameters

### Remaining Work
1. Introduce a shared mapper utility only if more slices begin repeating the same manual mapping logic.
2. Add a base DTO only if it clearly reduces duplication instead of adding abstraction for its own sake.

## Phase 6: Configuration and Constants (Started)

Current progress:
- Added `config/AppConstants.java` to centralize menu type constants, SOP batch constants, and default external API settings
- Added `AppConstants.MenuEndpoint` keys for commonly used external API menu-name lookups (LPN flows and user list)
- Replaced repeated menu-type literals in `MenuFilterUtil`, `NavigationService`, and `MenuServiceImpl` with `AppConstants.MenuType`
- Replaced SOP batch action/job literals in `MenuController` with `AppConstants.SopBatch`
- Replaced hardcoded SOP configuration defaults in `SettingsController` with property-backed values using `AppConstants.ExternalApi`
- Replaced string-literal endpoint lookups in `ExternalApiService` and `MenuAccessServiceImpl` with `AppConstants.MenuEndpoint`
- Replaced all `getUrl("...")` endpoint string literals in `MenuRepositoryCustomImp` with `AppConstants.MenuEndpoint` keys
- Added typed `ExternalApiProperties` (`@ConfigurationProperties(prefix="external.api")`) and wired `ExternalApiService` and `SettingsController` to use it instead of scattered `@Value` fields
- `SettingsController` SOP configuration update now applies validated values to `ExternalApiProperties` at runtime to keep read/update endpoints consistent

Potential additions:
- `ExternalApiConfig` for expanded external endpoint configuration

These should only be added where they remove real duplication.

## Recommended Next Steps

1. Continue repository consolidation by extracting auth/menu mutation methods from `MenuRepositoryCustomImp`.
2. Audit remaining controllers for direct entity binding and convert the high-risk ones to DTO-based contracts.
3. Standardize service logging and inheritance decisions before expanding the service layer further.
4. Migrate the exception hierarchy onto `BaseException` after the error payload contract is finalized.
5. Add targeted tests around refactored Inventory, LPN, Location, TransactionLog, and Listener slices.

## Files Added During Refactoring

### Base and Utility Files
- `repository/base/AbstractBaseRepository.java`
- `service/base/AbstractBaseService.java`
- `exception/base/BaseException.java`
- `util/MenuFilterUtil.java`
- `util/ResponseUtil.java`
- `util/ControllerReflectionUtil.java`

### Controllers
- `controller/AuthController.java`
- `controller/MenuNavigationController.java`
- `controller/CategoryController.java`
- `controller/ItemController.java`
- `controller/LocationController.java`
- `controller/LpnController.java`
- `controller/InventoryController.java`
- `controller/SettingsController.java`
- `controller/ListenerController.java`
- `controller/TransactionLogController.java`

### Repository Adapters
- `repository/CategoryRepositoryCustom.java`
- `repository/CategoryRepositoryCustomImp.java`
- `repository/ItemRepositoryCustom.java`
- `repository/ItemRepositoryCustomImp.java`
- `repository/LocationRepositoryCustom.java`
- `repository/LocationRepositoryCustomImp.java`
- `repository/LpnRepositoryCustom.java`
- `repository/LpnRepositoryCustomImp.java`
- `repository/InventoryRepositoryCustom.java`
- `repository/InventoryRepositoryCustomImp.java`
- `repository/SopRepositoryCustom.java`
- `repository/SopRepositoryCustomImp.java`
- `repository/MenuAuthRepositoryCustom.java`
- `repository/MenuAuthRepositoryCustomImp.java`

### DTOs
- `dto/ListenerDTO.java`
- `dto/ListenerRequestDTO.java`
- `dto/TransactionLogDTO.java`
- `dto/TransactionLogRequestDTO.java`

## Files That Still Need Follow-Up
- `MenuRepositoryCustomImp.java`
- `CustomExceptionHandler.java`
- Remaining domain exceptions that should move onto `BaseException`
- Controllers and services that still bind entities directly or rely on broad in-memory filtering

## Testing Strategy

### Unit Tests
- Utility behavior in `MenuFilterUtil`, `ResponseUtil`, and `ControllerReflectionUtil`
- Validation and mapping helpers in refactored services

### Integration Tests
- Service-to-repository behavior for refactored flows
- External API delegation through centralized HTTP helpers
- Exception handling behavior for validation and not-found cases

### Controller Tests
- Request mapping coverage
- DTO validation responses
- Expected status codes for CRUD flows

## Documentation Standards

All new classes should include:
1. Javadoc on the class when the responsibility is not obvious.
2. Javadoc on public methods when the behavior is non-trivial.
3. Inline comments only where the logic is genuinely difficult to read without them.
4. Examples in the guide or README only for reusable patterns and utilities.

---

**Document Version**: 1.1  
**Last Updated**: March 22, 2026  
**Prepared by**: Code Refactoring Initiative
