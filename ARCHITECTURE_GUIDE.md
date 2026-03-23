# ExternalApiService Architecture

> Program Status Sync (March 23, 2026): See PROGRAM_STATUS.md for the latest cross-phase status and TOMORROW_TODO.md for the next-session resume checklist.

## High-Level Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         LPN Controller                          │
│  - createLpn() endpoint                                         │
│  - updateLpn() endpoint                                         │
│  - getLpns() endpoint                                           │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      MenuService (Interface)                    │
│  - newLpn(name, item, qty)                                     │
│  - lpnEdit(name, desc, dims, qty, adjust, status, volume)      │
│  - getLpns()                                                    │
│  - locateLpnToResv(name, location)                             │
│  - locateLpnToActive(name, location)                           │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   MenuService Implementation                    │
│  (OR Direct Repository Usage)                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│              LpnRepositoryCustom (NEW - Interface)              │
│  - createLpn(name, item, qty)                                  │
│  - updateLpn(name, desc, length, width, height, qty...)        │
│  - getAllLpns()                                                 │
│  - validateLpn(name)                                           │
│  - moveLpnToReserve(name, location)                            │
│  - moveLpnToActive(name, location)                             │
│  - checkActiveInventory(name)                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│          LpnRepositoryCustomImp (NEW - Implementation)          │
│  ✓ Builds LPN JSON payloads                                    │
│  ✓ Validates input parameters                                  │
│  ✓ Delegates HTTP calls to ExternalApiService                 │
│  ✓ Parses responses using ObjectMapper                         │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────────┐
│                  ExternalApiService (ENHANCED)                  │
│                                                                  │
│  Specific Methods (LPN):                                        │
│  ├─ createLpn(json) ─────┐                                    │
│  ├─ updateLpn(name, qty, json) ──┐                            │
│  ├─ getLpns() ───────────────┐    │                           │
│  ├─ validateLpn(name) ──────┐│    │                           │
│  ├─ locateLpnToReserve(name, loc) │    │                       │
│  └─ locateLpnToActive(name, loc)  │    │                       │
│                               │    │    │                       │
│  Generic Methods:              │    │    │                       │
│  ├─ callExternalApi(token, menuName, method, payload)           │
│  ├─ callExternalApiWithUrl(token, url, method, payload)         │
│  └─ getApiUrl(menuName) ──────────┐    │                       │
│                                   │    │                        │
│  URL Resolution:                  │    │                        │
│  └─ getUrl(menuName) ────────────┐│    │                        │
│                                  ││    │                        │
└──────────────────────────────────┼┼────┼──────────────────────────┘
                                   │││    │
                 ┌─────────────────┼┼─────┘
                 │                 │ │
                 ↓                 ↓ ↓
        ┌─────────────────────────────────────┐
        │ MenuRepositoryCustom.getUrl(name)   │
        │ (Database URL Resolution)            │
        └─────────────────────────────────────┘
                         ↓
        ┌─────────────────────────────────────┐
        │  SELECT protocol, hostname,         │
        │  menu_link FROM menu                │
        │  WHERE menu_name = ?                │
        └─────────────────────────────────────┘
                         ↓
        ┌─────────────────────────────────────┐
        │  Result: Complete URL               │
        │  http://host:8085/api/lpn/create    │
        └─────────────────────────────────────┘
                         ↓
        ┌─────────────────────────────────────────────┐
        │  AbstractBaseRepository                     │
        │  (HTTP Client Utilities)                    │
        │  - httpGetAsString(url)                     │
        │  - httpPostForObject(url, json)             │
        │  - httpPutForObject(url, json)              │
        │  - httpDelete(url)                          │
        └─────────────────────────────────────────────┘
                         ↓
        ┌─────────────────────────────────────────────┐
        │  RestTemplate / HttpClient                  │
        │  (Spring HTTP Infrastructure)               │
        └─────────────────────────────────────────────┘
                         ↓
        ┌─────────────────────────────────────────────┐
        │  External SOP Service                       │
        │  (REST API)                                 │
        │  http://192.168.1.100:8085/api/lpn/*        │
        └─────────────────────────────────────────────┘
```

---

## Method Call Examples

### Example 1: Create LPN

```
LpnController.createLpn(lpnNumber, locationId)
    │
    └─→ MenuService.newLpn(lpn_name, item_name, qty)
            │
            └─→ LpnRepositoryCustom.createLpn(lpn_name, item_name, qty)
                    │
                    ├─1. Build JSON: { "lpn": { "lpn_name": "...", "item": {...} } }
                    │
                    └─→ ExternalApiService.createLpn(json)
                            │
                            ├─2. Get URL: getUrl("CreateLpn")
                            │        │
                            │        └─→ MenuRepositoryCustom.getUrl("CreateLpn")
                            │                └─→ Database query
                            │                    Result: "http://host:8085/api/lpn/create"
                            │
                            └─3. Call API: httpPostForObject(url, json)
                                    │
                                    └─→ AbstractBaseRepository
                                            │
                                            └─→ RestTemplate.postForObject()
                                                    │
                                                    └─→ Response: "LPN created successfully"
                                                            │
                                                            └─→ Return to caller
```

### Example 2: Update LPN

```
LpnController.updateLpn(id, lpnNumber, locationId)
    │
    └─→ MenuService.lpnEdit(lpn_name, item_desc, length, width, height, qty, adjustQty, status, volume)
            │
            └─→ LpnRepositoryCustom.updateLpn(...)
                    │
                    ├─1. Build JSON with dimensions and status
                    │
                    └─→ ExternalApiService.updateLpn(lpn_name, adjustQty, json)
                            │
                            ├─2. Get URL: getUrl("UpdateLpn")
                            │    Result: "http://host:8085/api/lpn/update/{lpn_name}/{adjustQty}"
                            │
                            ├─3. Replace placeholders:
                            │    ".../{lpn_name}/{adjustQty}"
                            │    └─→ ".../LPN001/5"
                            │
                            └─4. Call API: httpPutForObject(url, json)
                                    └─→ Response: "LPN updated successfully"
```

---

## Component Interactions

### ExternalApiService Methods

#### Specific LPN Methods (Type-Safe)
```
❶ createLpn(String lpnPayload)
   └─→ Returns: String (API response)
   └─→ Uses: POST to "CreateLpn" endpoint
   └─→ Best for: Type-safe, documented interface

❷ updateLpn(String lpnName, int adjustQty, String lpnPayload)
   └─→ Returns: String (API response)
   └─→ Uses: PUT to "UpdateLpn" endpoint
   └─→ Handles: Placeholder replacement {lpn_name}, {adjustQty}

❸ getLpns()
   └─→ Returns: String (JSON array)
   └─→ Uses: GET from "GetLpns" endpoint

❹ validateLpn(String lpnName)
   └─→ Returns: String (validation result)
   └─→ Uses: GET from "validateLpn" endpoint
   └─→ Handles: Placeholder replacement {lpn_name}

❺ locateLpnToReserve(String lpnName, String reserveLocation)
   └─→ Returns: String (API response)
   └─→ Uses: GET from "locateLpnToResv" endpoint
   └─→ Handles: Placeholders {lpn_name}, {resv_locn}

❻ locateLpnToActive(String lpnName, String activeLocation)
   └─→ Returns: String (API response)
   └─→ Uses: GET from "locateLpnToActive" endpoint
   └─→ Handles: Placeholders {lpn_name}, {active_locn}
```

#### Generic Methods (Flexible)
```
❶ callExternalApi(String token, String menuName, HttpMethod method, String payload)
   └─→ Returns: ResponseEntity<String>
   └─→ Usage: When specific method doesn't exist
   └─→ Example: externalApiService.callExternalApi(
                   null, 
                   "AddCategory", 
                   HttpMethod.POST, 
                   categoryJson
               )

❷ callExternalApiWithUrl(String token, String url, HttpMethod method, String payload)
   └─→ Returns: ResponseEntity<String>
   └─→ Usage: Direct URL with manual placeholder replacement
   └─→ Example: String finalUrl = baseUrl.replace("{id}", "123");
               externalApiService.callExternalApiWithUrl(
                   null, 
                   finalUrl, 
                   HttpMethod.PUT, 
                   jsonPayload
               )

❸ getApiUrl(String menuName)
   └─→ Returns: String (complete URL from database)
   └─→ Usage: When you need just the URL
   └─→ Example: String url = externalApiService.getApiUrl("GetLpns");
```

---

## Database URL Schema

```sql
-- Menu table structure (simplified)
CREATE TABLE menu (
    id INT PRIMARY KEY,
    menu_name VARCHAR(50) UNIQUE,    -- "CreateLpn", "UpdateLpn", etc.
    protocol VARCHAR(20),             -- "http://" or "https://"
    hostname VARCHAR(100),            -- "192.168.1.100:8085"
    menu_link VARCHAR(100),           -- "/api/lpn/create"
    ...
);

-- Example records:
INSERT INTO menu (menu_name, protocol, hostname, menu_link) VALUES
('CreateLpn', 'http://', '192.168.1.100:8085', '/api/lpn/create'),
('UpdateLpn', 'http://', '192.168.1.100:8085', '/api/lpn/update/{lpn_name}/{adjustQty}'),
('GetLpns', 'http://', '192.168.1.100:8085', '/api/lpn/all'),
('validateLpn', 'http://', '192.168.1.100:8085', '/api/lpn/validate/{lpn_name}'),
('locateLpnToResv', 'http://', '192.168.1.100:8085', '/api/lpn/{lpn_name}/reserve/{resv_locn}'),
('locateLpnToActive', 'http://', '192.168.1.100:8085', '/api/lpn/{lpn_name}/active/{active_locn}');

-- Query to resolve URL:
SELECT CONCAT(protocol, hostname, menu_link) as full_url 
FROM menu 
WHERE menu_name = 'CreateLpn';

-- Result: http://192.168.1.100:8085/api/lpn/create
```

---

## Error Handling Flow

```
LpnRepositoryCustomImp.createLpn()
    │
    ├─ Try to build JSON
    │   ├─ Success → Continue
    │   └─ Error → Catch, log "Error creating LPN: ...", throw RuntimeException
    │
    └─→ ExternalApiService.createLpn()
            │
            ├─ Try to get URL
            │   ├─ Success → Continue
            │   └─ Error → Log, throw RuntimeException
            │
            ├─ Try HTTP POST
            │   ├─ Success → Return response
            │   └─ Error → Log, throw RuntimeException
            │
            └─→ Response returned to repository
                    │
                    └─→ Parse and return to service/controller
```

---

## Scalability Example

### Current (LPN Done)
```
ExternalApiService
├─ createLpn()
├─ updateLpn()
├─ getLpns()
├─ validateLpn()
├─ locateLpnToReserve()
├─ locateLpnToActive()
└─ callExternalApi() [generic]
```

### Future (Add Category)
```
ExternalApiService
├─ LPN methods (as above)
│
├─ createCategory()           ← New
├─ updateCategory()           ← New
├─ getAllCategories()         ← New
├─ deleteCategory()           ← New
│
└─ callExternalApi() [generic - handles everything]
```

### Future (Add Item)
```
ExternalApiService
├─ LPN methods
├─ Category methods
│
├─ createItem()               ← New
├─ updateItem()               ← New
├─ getAllItems()              ← New
├─ deleteItem()               ← New
│
└─ callExternalApi() [generic - handles everything]
```

Each repository (LpnRepositoryCustomImp, CategoryRepositoryCustomImp, etc.) follows the same pattern:
1. Build domain-specific JSON
2. Call ExternalApiService method (specific or generic)
3. Parse response
4. Return typed result

---

## Key Takeaways

✅ **Clean Separation** - HTTP layer separate from business logic  
✅ **Database-Driven** - Endpoints configurable without code changes  
✅ **Type-Safe** - Specific methods with clear contracts  
✅ **Flexible** - Generic methods for ad-hoc operations  
✅ **Maintainable** - Single place for HTTP logic  
✅ **Extensible** - Easy to add more repositories  
✅ **Testable** - Mock ExternalApiService for unit tests  
