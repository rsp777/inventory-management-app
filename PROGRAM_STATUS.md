# Program Status (Authoritative)

**Date**: March 23, 2026  
**Scope**: Inventory Management App refactoring and migration workstream

## Phase Status

| Phase | Name | Status |
|---|---|---|
| 1 | Foundation Refactoring | Completed |
| 2 | Service Normalization | Completed |
| 3 | Repository Improvements | Completed |
| 4 | Exception Handling | Completed |
| 5 | DTO and Entity Improvements | Completed |
| 6 | Configuration and Constants | Completed |

## Recently Completed Work

- **Endpoint Page Access Issue Tracked For Dev_v2.5 (March 29, 2026)**: created GitHub issue [#6](https://github.com/rsp777/inventory-management-app/issues/6) to track inability to access the Endpoint page from the new UI flow on branch `Dev_v2.5`. Initial investigation scope includes menu mapping visibility, role/menu authorization, and endpoint/controller route binding validation.
- **Menu Hierarchy Regression Tracked For Dev_v2.5 (March 29, 2026)**: issue #5 was updated with detailed UI findings and fix notes for mixed menu groups where slotting `PARENT_UI/CHILD_UI` entries appeared with admin `PARENT/CHILD` entries. Logged observed behavior (submenu child-type leakage), root-cause analysis (top-level categorization and template submenu filtering), and fix notes (source-level parent-only grouping with UI-layer child filtering guidance). Tracking issue: [#5](https://github.com/rsp777/inventory-management-app/issues/5) (closed as fixed).
- **Header Username Display Regression Resolved (March 29, 2026)**: fixed top-right user label rendering role payload text after login by replacing brittle JWT split parsing in `CustomExceptionHandler` with `SessionUtil` primary lookup and `TokenService` fallback username resolution. Validation: `mvn compile -DskipTests` -> BUILD SUCCESS. Tracking issue: [#4](https://github.com/rsp777/inventory-management-app/issues/4) (closed as fixed).
- **Token Decode Regression Resolved (March 29, 2026)**: fixed a refactor regression where login responses wrapped as JSON (`{"token":"..."}`/`{"access_token":"..."}`) were not consistently normalized before decode, causing token parse failures or single-component plain-string decode results. Implemented token normalization in `TokenService`, added null-safe extraction in `AuthController`, and corrected username extraction indexing. User login is now confirmed working. Tracking issue: [#3](https://github.com/rsp777/inventory-management-app/issues/3).
- **Build/Test/Manual Validation Completed (March 23, 2026)**: full package validation passed with `mvn clean package` -> BUILD SUCCESS; test summary `Tests run: 39, Failures: 0, Errors: 0, Skipped: 0`. Manual runtime verification passed: `/inventory-ui/actuator/health` returned `UP`, DB health component returned `UP`, focused routes (`/api/auth/index`, `/settings`, `/lpn/create`) returned HTTP 200, and compatibility aliases (`/api/index`, `/api/createLpn`, `/api/signIn`, `/api/logout`) returned expected HTTP 302 redirects.
- **Phase 6 Closeout: Shared View And Redirect Constants**: centralized high-duplication template and redirect names in `AppConstants` and adopted them across focused controllers and `MenuController`, eliminating the audited repeated return literals for category/item/location/LPN/inventory/settings/SOP/user/cubiscan flows.
- **Phase 6 Closeout: Session Access Consolidation**: completed the migration from ad hoc session reads to `SessionUtil` for both token lookup and user-name lookup across focused controllers, leaving only the intentional auth-time session writes in `AuthController` and the compatibility fallback inside `SessionUtil` itself.
- **Phase 6 Closeout: Audit Source Normalization**: centralized both mixed-case and uppercase system-source defaults under `AppConstants.Application` and reused them in menu-auth and transaction-log paths, removing residual raw `System` and `SYSTEM` duplication from the audited code paths.
- **Phase 6 Closeout: Verification And Regression Fix**: corrected the legacy `MenuController` location-add failure fallback to return `location` instead of `item`, then validated the full slice with `mvn -DskipTests compile` -> BUILD SUCCESS (113 files).
- Phase 6 standardization progressed further by introducing centralized view/redirect constants in `AppConstants` for high-duplication legacy controller flows and reusing them in `MenuController`, reducing repeated template/redirect literals for category/item/location/LPN/SOP/user/cubiscan handlers while preserving endpoint behavior.
- Phase 6 session cleanup was extended from token fallback to user-name access by reusing `SessionUtil` in focused location/LPN/inventory/settings controllers, eliminating repeated direct session reads of legacy username attributes.
- A residual regression introduced during cleanup was corrected: the `MenuController` legacy location-add failure path once again returns the `location` view instead of the unrelated `item` view. Build: SUCCESS (113 files).
- Phase 6 standardization advanced across focused controllers by replacing repeated session-token fallback blocks with `SessionUtil#getSessionToken(HttpSession)` in item/category/menu-navigation/location/LPN/inventory/settings flows, reducing duplicated session access logic while preserving behavior. Build: SUCCESS (113 files).
- Phase 6 audit-source cleanup progressed by centralizing remaining uppercase system defaults under `AppConstants.Application` and reusing them in transaction-log model/service defaults (`TransactionLog`, `TransactionLogServiceImpl`) instead of raw `"SYSTEM"` literals.
- Phase 3 was completed by removing the remaining legacy category/item forward adapters from `MenuController` and replacing them with direct helper-backed handlers for category add/edit/delete, delete-by-id, item add/edit, delete-item, and new-LPN creation. The only remaining `forward:` in `MenuController` is the intentional POST handoff to `/api/auth/signIn` to preserve request-body semantics. Build: SUCCESS (113 files).
- Phase 3 cleanup also fixed two residual defects discovered during the final audit: the ambiguous duplicate delete mapping in `CategoryController` was split so delete-by-name now uses `/api/categoryDeleteByName/{category_name}`, and the legacy location add failure path in `MenuController` now returns the correct `location` view.
- Phase 3 repository/controller normalization was finalized by converting the remaining focused controllers and custom repository implementations to constructor injection, adding transactional boundaries to menu write methods, normalizing `/all` API routes under focused controllers, and making SOP cubiscan log fetches return an empty list with logging instead of `null`.
- Phase 6 standardization advanced by centralizing the audit source (`System`) and external API status markers (`"status":404`, `"status":500`) in `AppConstants` and reusing them in menu auth and repository adapters.
- Phase 3 adapter thinning in `MenuController` completed: all remaining forward-hop aliases converted to direct handler delegation — location write ops (`/locationAdd`, `/locationEdit`, `/deleteLocation/{locn_brcd}`), lpn edit (`/lpnEdit`), putaway POSTs (`/locateLpnToReserve`, `/checkActiveInventory`, `/locateLpnToActive`), and SOP range/UPC ops (`/sop/location-range/add`, `/sop/location-range/update`, `/sop/getEligibleUpcsForSop/{category}`). Zero `forward:` returns remain for `/api/legacy/*` aliases. Build: SUCCESS (113 files).
- Phase 3 legacy adapter reduction progressed in `MenuController` by removing additional internal forward hops for utility/view aliases (`/api/userlist`, `/api/sopConfig`, `/api/cubiscanLog`, `/api/endpoint`) and delegating directly to existing legacy handlers while preserving endpoint contracts.
- Phase 3 adapter thinning continued in `MenuController` by replacing additional POST alias forwards with direct delegation (`/api/userAdd`, `/api/sop/runBatch`) to reduce internal routing hops while preserving compatibility mappings.
- Phase 6 standardization progressed by introducing `SessionUtil#getSessionToken(HttpSession)` and reusing it across navigation/auth/error and legacy SOP flows (`NavigationService`, `MenuAuthRepositoryCustomImpl`, `CustomExceptionHandler`, `MenuController`) to reduce duplicated token fallback logic.
- Phase 3 repository decomposition advanced.
- Adapter pass-through patterns replaced with direct `ExternalApiService` endpoint calls in:
  - `CategoryRepositoryCustomImpl`
  - `ItemRepositoryCustomImpl`
  - `LocationRepositoryCustomImpl`
  - `InventoryRepositoryCustomImpl`
  - `SopRepositoryCustomImpl`
  - `MenuAuthRepositoryCustomImpl`
- `MenuAccessServiceImpl#getUsers()` moved to centralized external call flow.
- `ExternalApiService` decoupled from `MenuRepositoryCustom` and now routes URL lookup via `MenuAuthRepositoryCustom` plus HTTP execution via `HttpService`.
- `MenuRepositoryCustomImpl` no longer depends on `MenuService` (service-layer dependency removed; direct menu lookup now uses `MenuRepository`).
- Legacy `MenuRepositoryCustom` interface contract pruned; `MenuRepositoryCustomImpl` now runs as a concrete component with no external interface bindings.
- Legacy `MenuRepositoryCustomImpl` monolith removed after decomposition; auth/menu and SOP responsibilities remain in focused repository components.
- Duplicate legacy `/api` item/category routes in `MenuController` were moved to `/api/legacy/*`, leaving primary `/api` ownership to focused `ItemController` and `CategoryController` mappings.
- Additional legacy `MenuController` view/inquiry routes were redirected to focused controllers (`/location`, `/lpn`, `/inventory`, `/location/inquiry`) to reduce duplicated page-load logic.
- Additional legacy entrypoint routes were redirected to focused controllers (`/api/index` -> `/api/auth/index`, `/api/createLpn` -> `/lpn/create`, `/api/settings` -> `/settings`).
- Duplicate `/api/menulist` ownership was resolved by moving `MenuController` handler to `/api/legacy/menulist` and redirecting to the focused `MenuNavigationController` route.
- Legacy `/api/logout` now delegates to dedicated auth flow (`/api/auth/logout`) to reduce duplicate logout logic.
- Legacy `/api/signIn` now forwards to dedicated auth flow (`/api/auth/signIn`) to remove duplicate login handling logic from `MenuController`.
- Additional `MenuController` legacy handlers were simplified by removing unused request/model/session parameters (no behavior change), and compile validation remained green.
- Remaining putaway view ownership overlap was reduced by moving page ownership to `LpnController` (`/lpn/putaway-reserve`, `/lpn/putaway-active`, `/lpn/putaway-active-sys`) while preserving old `MenuController` routes as compatibility redirects.
- Additional legacy GET alias coupling in `MenuController` was reduced by switching focused-route aliases (`itemInquiry`, `itemInfo`, `categoryInfo`) from internal forwards to redirects so ownership remains explicit in focused controllers.
- Legacy category-delete alias in `MenuController` was further simplified by removing a redundant internal `/legacy` hop and forwarding directly to the focused delete route while keeping compatibility mapping intact.
- Additional Phase 6 constants/config standardization was applied by introducing centralized session attribute constants in `AppConstants` and adopting them in auth/navigation/legacy SOP flows (`AuthController`, `NavigationService`, `MenuController`) to reduce session-key drift while preserving compatibility aliases.
- Phase 6 session-key normalization was further expanded across controller and exception layers (`ItemController`, `CategoryController`, `MenuNavigationController`, `LocationController`, `LpnController`, `InventoryController`, `SettingsController`, `CustomExceptionHandler`) by replacing hardcoded token/user session keys with centralized constants and fallback reads.
- Phase 6 normalization was completed for remaining signout/settings session-key access points (`MenuAuthRepositoryCustomImpl`, `SettingsController`) to remove residual hardcoded session key lookups.
- Phase 6 session-token robustness was extended in `NavigationService` and `MenuController` legacy SOP helpers by adding fallback reads from `JWT_TOKEN` when `DECODED_TOKEN` is absent, reducing null-token risk during mixed session states.
- Additional `MenuController` write routes were namespaced under `/api/legacy/*` with forward-compatible aliases retained on original URLs (`/locationAdd`, `/locationEdit`, `/deleteLocation/{locn_brcd}`, `/lpnEdit`).
- Putaway routes in `MenuController` were also namespaced under `/api/legacy/*` with forward-compatible aliases retained on original URLs (`/putawayLpnToReserve`, `/locateLpnToReserve`, `/putawayLpnToActive`, `/putawayLpnToActiveSys`, `/checkActiveInventory`, `/locateLpnToActive`).
- Remaining user/SOP utility routes in `MenuController` were namespaced under `/api/legacy/*` with forward-compatible aliases retained on original URLs (`/userlist`, `/userAdd`, `/sopConfig`, `/sop/runBatch`, `/sop/location-range/add`, `/sop/location-range/update`, `/sop/getEligibleUpcsForSop/{category}`, `/cubiscanLog`, `/endpoint`).
- Remaining non-legacy mutation routes in `MenuController` were namespaced under `/api/legacy/*` with forward-compatible aliases retained on original URLs (`/newLpn`, `/deleteCategory/{category_id}`).
- Legacy item/category handlers in `MenuController` were further reduced from duplicated business logic to focused-controller forwards (including `itemInquiry`, `itemInfo`, `categoryInfo`, `categoryAdd`, `categoryEdit`, `categoryDelete`, `itemAdd`, `itemEdit`, `deleteItem`).
- Legacy `MenuController` item lookup shim (`/api/legacy/getItem/{item_name}`) was reduced to a focused inquiry forward to remove residual duplicate service-call logic.
- Legacy `MenuController` category-delete-by-id handler (`/api/legacy/deleteCategory/{category_id}`) was reduced to a focused-controller forward (`/api/categoryDelete/{category_id}`) to remove duplicate delete logic.
- Repeated SOP model-loading logic in `MenuController` was consolidated into a shared helper method used by `sopConfig`, `runBatch`, `addLocationRange`, `updatecationRange`, and `getEligibleUpcsForSop` handlers.
- `MenuController` imports were cleaned up to remove stale dependencies after route/body forward refactors, keeping the controller compile-clean and easier to maintain.
- Remaining legacy location/putaway bodies in `MenuController` were simplified by extracting dedicated private helpers (`handleLegacyLocationAdd`, `handleLegacyLocationEdit`, `handleLegacyPutawayReserve`, `handleLegacyPutawayActive`) to reduce method complexity while preserving behavior.
- Additional remaining legacy bodies in `MenuController` were simplified by extracting private helpers for location/LPN/inventory/user/cubiscan operations (`handleLegacyDeleteLocation`, `handleLegacyLpnEdit`, `handleLegacyCheckActiveInventory`, `handleLegacyUserList`, `handleLegacyUserAdd`, `handleLegacyCubiscanLog`) so endpoint methods are thin wrappers.
- Remaining endpoint-level direct business logic in `MenuController` was further removed by extracting legacy `newLpn` and SOP flows into dedicated helpers (`handleLegacyNewLpn`, `handleLegacySopConfig`, `handleLegacyRunBatch`, `handleLegacyAddLocationRange`, `handleLegacyUpdateLocationRange`, `handleLegacyEligibleUpcs`), leaving request mappings as thin adapters.
- Phase 1 controller hygiene migration advanced in focused legacy controllers (`ItemController`, `CategoryController`) by replacing `printStackTrace` and string-concatenated logs with structured logger usage, preserving all methods/endpoints while standardizing error handling patterns.
- Phase 1 legacy cleanup was extended to `AuthController`, `MenuNavigationController`, and `MenuAccessController` by migrating nested try/catch and `printStackTrace` patterns to explicit structured exception handling with preserved method signatures and route contracts.
- Phase 1 controller cleanup continued in `LocationController`, `LpnController`, and `InventoryController` by replacing remaining string-concatenated logs with structured logging; `MenuAccessController` was also aligned to SLF4J and constructor injection while preserving all public methods and endpoints.
- Phase 1 cleanup was extended to `SettingsController` by migrating field injection to constructor injection, standardizing structured logging, and extracting placeholder response builders into private helpers while preserving every endpoint and method.
- Phase 1 residual placeholder cleanup advanced by introducing `SettingsService` and `SettingsServiceImp`, migrating settings/user-preferences/SOP/health placeholder data assembly out of `SettingsController` into a dedicated service while preserving all controller methods and routes.
- `SettingsController` was further tightened by removing obsolete direct `ExternalApiProperties` wiring, fixing method-body formatting consistency, and clearing residual TODO annotations in controller code to keep Phase 1 controller cleanup baseline clean.
- Phase 2 service normalization was completed by standardizing constructor-based dependency injection across service and base-service classes (`MenuServiceImpl`, `MenuAccessServiceImpl`, `ExternalApiService`, `NavigationService`, `ListenerServiceImpl`) and eliminating remaining service-layer field `@Autowired` patterns.
- Phase 2 naming consistency was finalized by normalizing settings implementation naming from `SettingsServiceImp` to `SettingsServiceImpl` and validating no remaining `*Imp` service implementations.
- Compile checks stayed green after each incremental slice.
- Report files aligned with a single status reference model.

## Remaining Work by Phase (Codebase-Driven)

- Phase 1: no active remaining work; completed and retained for historical traceability.
- Phase 3: no active remaining work; completed and retained for historical traceability.
- Phase 6: no active remaining work; completed and retained for historical traceability.

## Effort, Time, and Cost Snapshot (Estimated)

| Item | Estimate |
|---|---|
| Total engineering effort | 72 to 102 hours |
| Active workstream window | March 22 to March 23, 2026 |
| Elapsed calendar time (current window) | 2 days |

| Blended rate | Estimated cost range |
|---|---|
| USD 35/hour | USD 2,520 to USD 3,570 |
| USD 50/hour | USD 3,600 to USD 5,100 |
| USD 75/hour | USD 5,400 to USD 7,650 |

## Notes

- This file is the source of truth for cross-phase status when report files diverge.
- Use `TOMORROW_TODO.md` to resume execution in priority order.
