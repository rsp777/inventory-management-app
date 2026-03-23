# ExternalApiService Migration Guide

> Program Status Sync (March 23, 2026): See PROGRAM_STATUS.md for the latest cross-phase status and TOMORROW_TODO.md for the next-session resume checklist.

## Overview
The `ExternalApiService` provides a cleaner, centralized way to call external APIs by replacing direct `httpCall()` invocations with service-level methods. URLs are fetched from the database via `MenuRepositoryCustom.getUrl()`, ensuring all endpoints are configurable without code changes.

---

## Migration Pattern: LPN Operations

### **Before: Using MenuRepositoryCustomImp.httpCall()**

```java
// Current implementation in MenuRepositoryCustomImp
public String newLpn(String lpn_name, String item_name, int quantity) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("lpn_name", lpn_name);
    
    JSONObject itemObject = new JSONObject();
    itemObject.put("itemName", item_name);
    jsonObject.put("item", itemObject);
    jsonObject.put("quantity", quantity);
    
    JSONObject lpnObject = new JSONObject();
    lpnObject.put("lpn", jsonObject);
    String json = lpnObject.toString();
    
    logger.info("Lpn Payload : " + json);
    String url = getUrl("CreateLpn");  // Fetch URL from database
    String response = httpCall(null, url, HttpMethod.POST, json, null).getBody().toString();
    logger.info("Response : " + response);
    
    return response;
}
```

### **After: Using ExternalApiService**

**Option 1: Direct Method (Recommended for LPN-specific operations)**
```java
@Service
public class LpnRepositoryService {
    
    @Autowired
    private ExternalApiService externalApiService;
    
    public String createLpn(String lpn_name, String item_name, int quantity) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lpn_name", lpn_name);
        
        JSONObject itemObject = new JSONObject();
        itemObject.put("itemName", item_name);
        jsonObject.put("item", itemObject);
        jsonObject.put("quantity", quantity);
        
        JSONObject lpnObject = new JSONObject();
        lpnObject.put("lpn", jsonObject);
        String json = lpnObject.toString();
        
        logger.info("Lpn Payload : " + json);
        // Uses ExternalApiService.createLpn() - URL resolved from database
        return externalApiService.createLpn(json);
    }
}
```

**Option 2: Generic HTTP Wrapper (For when direct method doesn't exist)**
```java
public String createLpn(String lpn_name, String item_name, int quantity) {
    JSONObject jsonObject = new JSONObject();
    // ... build JSON ...
    String json = lpnObject.toString();
    
    logger.info("Lpn Payload : " + json);
    // Use generic wrapper - URL resolved from database
    ResponseEntity<String> response = externalApiService.callExternalApi(
        null, "CreateLpn", HttpMethod.POST, json
    );
    
    return response.getBody();
}
```

---

## ExternalApiService API Reference

### **Specific Methods (Recommended)**

```java
// Create new LPN
public String createLpn(String lpnPayload)

// Update existing LPN
public String updateLpn(String lpnName, int adjustQty, String lpnPayload)

// Get all LPNs
public String getLpns()

// Validate LPN
public String validateLpn(String lpnName)

// Move to reserve location
public String locateLpnToReserve(String lpnName, String reserveLocation)

// Move to active location
public String locateLpnToActive(String lpnName, String activeLocation)
```

### **Generic Methods (For other operations)**

```java
// Call any endpoint by name - URL fetched from database
public ResponseEntity<String> callExternalApi(
    String token, String menuName, HttpMethod httpMethod, String jsonPayload)

// Call with explicit URL (for placeholder replacement)
public ResponseEntity<String> callExternalApiWithUrl(
    String token, String url, HttpMethod httpMethod, String jsonPayload)

// Get URL for any endpoint
public String getApiUrl(String menuName)
```

---

## Benefits of Migration

| Aspect | Before | After |
|--------|--------|-------|
| **URL Management** | Hardcoded or via getUrl() in each method | Centralized in ExternalApiService |
| **HTTP Handling** | Direct httpCall() with httpService | Unified HTTP wrapper methods |
| **Code Reusability** | Duplicated httpCall() logic | Single place, reused by all |
| **Error Handling** | Scattered try-catch | Centralized error logging |
| **Testability** | Hard to mock httpCall() | Easy to mock ExternalApiService |
| **Maintainability** | HTTP logic mixed with business logic | Clear separation of concerns |

---

## Step-by-Step Migration for MenuRepositoryCustomImp

### Step 1: Add ExternalApiService dependency
```java
@Component
@Configuration
public class MenuRepositoryCustomImp implements MenuRepositoryCustom {
    
    @Autowired
    private ExternalApiService externalApiService;
    
    // ... rest of the class
}
```

### Step 2: Replace httpCall() with ExternalApiService methods

**Old Code:**
```java
String url = getUrl("CreateLpn");
String response = httpCall(null, url, HttpMethod.POST, json, null).getBody().toString();
```

**New Code:**
```java
String response = externalApiService.createLpn(json);
```

### Step 3: For operations without specific methods, use generic wrapper

**Old Code:**
```java
String url = getUrl("AddCategory");
String response = httpCall(null, url, HttpMethod.POST, json, null).getBody().toString();
```

**New Code:**
```java
ResponseEntity<String> response = externalApiService.callExternalApi(
    null, "AddCategory", HttpMethod.POST, json
);
String result = response.getBody();
```

---

## URL Placeholder Replacement

For URLs with placeholders like `{lpn_name}` or `{adjustQty}`:

```java
// Fetch base URL and replace placeholders
String baseUrl = externalApiService.getApiUrl("UpdateLpn");
String url = baseUrl
    .replace("{lpn_name}", lpn_name)
    .replace("{adjustQty}", String.valueOf(adjustQty));

// Use the explicit URL version
ResponseEntity<String> response = externalApiService.callExternalApiWithUrl(
    null, url, HttpMethod.PUT, json
);
```

Or use the dedicated method:
```java
String response = externalApiService.updateLpn(lpn_name, adjustQty, json);
```

---

## Database URL Format

The `MenuRepositoryCustom.getUrl()` queries the database:
```sql
SELECT protocol, hostname, menu_link FROM menu WHERE menu_name = ?
```

Example result:
| Column | Value |
|--------|-------|
| protocol | `http://` |
| hostname | `192.168.1.100:8085` |
| menu_link | `/api/lpn/create` |
| **Full URL** | `http://192.168.1.100:8085/api/lpn/create` |

---

## Summary

✅ **Use ExternalApiService for:**
- All external API calls
- Centralized URL management
- Consistent error handling
- Better testability

❌ **Don't use httpCall() directly**
- It's a low-level wrapper that should be in a service layer
- Mixing it with business logic makes code harder to maintain

🎯 **Next Steps:**
1. Update MenuRepositoryCustomImp to use ExternalApiService
2. Create domain-specific repository services (LpnRepository, CategoryRepository, etc.)
3. Eventually phase out the direct httpCall() usage
