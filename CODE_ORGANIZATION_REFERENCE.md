# Code Organization Quick Reference

> Program Status Sync (March 23, 2026): See PROGRAM_STATUS.md for the latest cross-phase status and TOMORROW_TODO.md for the next-session resume checklist.

## Project Structure Overview

```
src/main/java/com/pawar/inventory/app/
├── config/                    # Spring configuration classes
├── controller/                # Request handlers (now split by domain)
│   ├── AuthController.java          ✅ Authentication (NEW)
│   ├── MenuNavigationController.java ✅ Menu management (NEW)
│   ├── CategoryController.java       ✅ Categories (NEW)
│   ├── ItemController.java           ✅ Items (NEW)
│   ├── LocationController.java       ⏳ Pending
│   ├── LpnController.java            ⏳ Pending
│   ├── InventoryController.java      ⏳ Pending
│   ├── SettingsController.java       ⏳ Pending
│   ├── MenuController.java           ⚠️ Legacy (being retired)
│   ├── ListenerController.java
│   └── TransactionLogController.java
├── dto/                       # Data transfer objects
├── events/                    # Domain events
├── exception/                 # Custom exceptions
│   ├── base/
│   │   └── BaseException.java ✅ All exceptions extend this (NEW)
│   ├── MenuNotFoundException.java
│   ├── UnauthorizedException.java
│   └── ...other exceptions
├── exceptionhandler/          # Global exception handling
├── listeners/                 # Event listeners
├── model/                     # JPA entities
├── repository/                # Data access layer
│   ├── base/
│   │   └── AbstractBaseRepository.java ✅ HTTP & serialization utils (NEW)
│   ├── MenuRepository.java
│   ├── MenuRepositoryCustom.java
│   └── MenuRepositoryCustomImp.java
├── service/                   # Business logic layer
│   ├── base/
│   │   └── AbstractBaseService.java ✅ Common service utilities (NEW)
│   ├── MenuService.java
│   ├── MenuServiceImp.java   ⚠️ Should be MenuServiceImpl
│   ├── MenuAccessService.java
│   └── MenuAccessServiceImpl.java
└── util/                      # Utility classes
    ├── MenuFilterUtil.java    ✅ Menu categorization (NEW)
    └── ResponseUtil.java      ✅ Response handling (NEW)
```

## Key Design Patterns

### 1. Menu Filtering (Replaces 15+ duplicated loops)

```java
// OLD WAY (Repeated throughout MenuController):
List<Menu> rf = new ArrayList<>();
List<Menu> nav = new ArrayList<>();
List<Menu> side = new ArrayList<>();
for (Menu menu : menus) {
    if (menu.getMenu_type().equals("RF")) {
        rf.add(menu);
    } else if (menu.getMenu_type().equals("UI")) {
        nav.add(menu);
    } else if (!menu.getMenu_type().equals("AUTH")) {
        side.add(menu);
    }
}

// NEW WAY (Single utility call):
MenuFilterUtil.MenuCategories categories = MenuFilterUtil.categorizeMenus(menus);
// Access: categories.rightFrameMenus, categories.navigationMenus, categories.sideMenus
```

### 2. Response Handling (Standardized)

```java
// Set view attributes
MenuCategories categories = MenuFilterUtil.categorizeMenus(menus);
ResponseUtil.addViewAttributes(model, categories, request.getRequestURI());

// Return success/error responses
ResponseEntity<?> response = ResponseUtil.success(data);
ResponseEntity<?> error = ResponseUtil.error("Message", HttpStatus.BAD_REQUEST);
```

### 3. HTTP Operations (Centralized)

```java
// OLD: Direct HTTP calls with boilerplate in every method
HttpGet request = new HttpGet(url);
HttpResponse response = httpClient.execute(request);
String json = EntityUtils.toString(response.getEntity());
List<Category> categories = objectMapper.readValue(json, new TypeReference<>() {});

// NEW: Single method call
List<Category> categories = httpGetAsList(url, new TypeReference<List<Category>>() {});
```

### 4. Service Implementation

```java
// All services extend AbstractBaseService
@Service
public class ItemServiceImpl extends AbstractBaseService implements ItemService {
    
    @Autowired
    private ItemRepository repository;
    
    @Override
    public Item findById(Long id) {
        validateNotNull(id, "Item ID");
        logInfo("Finding item with ID: " + id);
        return repository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException("Item not found"));
    }
}
```

### 5. Exception Handling

```java
// All custom exceptions extend BaseException
public class ItemNotFoundException extends BaseException {
    public ItemNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND");
    }
}

// Thrown in service
throw new ItemNotFoundException("Item with ID " + id + " not found");

// Caught by CustomExceptionHandler automatically
```

## Common Tasks

### Add a New CRUD Feature

1. **Create Controller**
   ```java
   @Controller
   @RequestMapping("/api/feature")
   public class FeatureController {
       @GetMapping("/list")
       public String list(Model model, HttpSession session) {
           List<Menu> menus = menuAccessService.getAccessibleMenus(...);
           MenuCategories cats = MenuFilterUtil.categorizeMenus(menus);
           ResponseUtil.addViewAttributes(model, cats, request.getRequestURI());
           return "feature";
       }
   }
   ```

2. **Create Service (extending AbstractBaseService)**
   ```java
   @Service
   public class FeatureServiceImpl extends AbstractBaseService implements FeatureService {
       public Feature create(String name) {
           validateNotEmpty(name, "Feature name");
           logInfo("Creating feature: " + name);
           return repository.save(new Feature(name));
       }
   }
   ```

3. **Create Repository (extending AbstractBaseRepository if API calls needed)**
   ```java
   @Repository
   public class FeatureRepositoryImpl extends AbstractBaseRepository {
       public List<Feature> fetchFromExternal() throws IOException {
           return httpGetAsList("http://api.com/features", 
               new TypeReference<List<Feature>>() {});
       }
   }
   ```

### Handle Exceptions

```java
try {
    service.doSomething();
} catch (ItemNotFoundException e) {
    return ResponseUtil.error(e.getMessage(), HttpStatus.NOT_FOUND);
} catch (Exception e) {
    return ResponseUtil.handleException(e, HttpStatus.INTERNAL_SERVER_ERROR);
}
```

### Log Messages

```java
// Using inherited logger from AbstractBaseService
logInfo("User logged in: " + username);
logWarning("Invalid login attempt for user: " + username);
logError("Database connection failed", exception);
```

## Naming Conventions

| Component | Pattern | Example |
|-----------|---------|---------|
| Controller | `[Domain]Controller` | `ItemController`, `CategoryController` |
| Service Interface | `[Domain]Service` | `ItemService`, `CategoryService` |
| Service Impl | `[Domain]ServiceImpl` | `ItemServiceImpl`, `CategoryServiceImpl` |
| Repository | `[Domain]Repository` | `ItemRepository` |
| Custom Repository | `[Domain]RepositoryCustom` | `ItemRepositoryCustom` |
| Custom Impl | `[Domain]RepositoryCustomImp` | `ItemRepositoryCustomImp` |
| Exception | `[Domain]Exception` | `ItemNotFoundException` |
| Utility | `[Domain]Util` | `MenuFilterUtil` |
| DTO | `[Domain]Dto` | `ItemDto` |

## Debugging Tips

### 1. Menu Filtering Issues
```java
MenuCategories cats = MenuFilterUtil.categorizeMenus(menus);
if (cats.isEmpty()) {
    logger.warning("No menus categorized!");
}
```

### 2. HTTP Call Issues
```java
// Check logs in AbstractBaseRepository
// All HTTP requests are logged with:
logger.info("GET Response from " + url + ": " + json);
```

### 3. Service Logging
```java
// All services use standardized logging
// Look for: [FeatureServiceImpl] Your message
```

### 4. Exception Handling
```java
// Check CustomExceptionHandler for caught exceptions
// Each exception includes:
// - httpStatus
// - errorCode (for frontend handling)
// - timestamp (when it occurred)
```

## Code Review Checklist

Before committing code:
- [ ] Controller uses `MenuFilterUtil` for menu categorization?
- [ ] Controller uses `ResponseUtil` for response handling?
- [ ] Service extends `AbstractBaseService`?
- [ ] Custom exceptions extend `BaseException`?
- [ ] No inline HTTP calls (use `AbstractBaseRepository` methods)?
- [ ] No duplicate code (use utilities)?
- [ ] Proper exception handling (try-catch with logging)?
- [ ] Javadoc comments on public methods?
- [ ] Consistent naming conventions followed?

## Common Errors & Fixes

| Error | Cause | Fix |
|-------|-------|-----|
| `Method not found: getMenu_name()` | Using old camelCase name | Use `getMenuName()` |
| Duplicate menu filtering loops | Not using MenuFilterUtil | Replace with `MenuFilterUtil.categorizeMenus()` |
| No logger in Service | Didn't extend AbstractBaseService | Extend `AbstractBaseService` |
| HTTP calls scattered | Not using AbstractBaseRepository | Move to custom repository extending base class |
| Inconsistent responses | Not using ResponseUtil | Use `ResponseUtil` methods |
| Missing exception handling | Catching generic Exception | Throw/catch specific exceptions extending BaseException |

---

**Last Updated**: March 22, 2026
