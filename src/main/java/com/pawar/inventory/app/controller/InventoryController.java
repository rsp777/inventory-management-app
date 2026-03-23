package com.pawar.inventory.app.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.service.base.ValidationService;
import com.pawar.inventory.app.util.ControllerReflectionUtil;
import com.pawar.inventory.app.util.MenuFilterUtil;
import com.pawar.inventory.app.util.MenuFilterUtil.MenuCategories;
import com.pawar.inventory.app.util.ResponseUtil;
import com.pawar.inventory.app.util.SessionUtil;
import com.pawar.inventory.entity.Inventory;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for Inventory inquiry and reporting operations.
 * Provides views and APIs for checking inventory by various dimensions.
 * 
 * Endpoints:
 * - GET /inventory - Display inventory summary
	@GetMapping("/all")
 * - GET /inventory/by-location - View inventory by location
 * - GET /inventory/by-lpn - View inventory by LPN
 * - GET /inventory/{id} - Get specific inventory details
 * - GET /inventory/search - Search inventory
 */
@Controller
@RequestMapping("/inventory")
public class InventoryController {
	
	private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
	
	private final MenuService menuService;
	private final MenuAccessService menuAccessService;
	private final ValidationService validationService;

	public InventoryController(MenuService menuService, MenuAccessService menuAccessService,
			ValidationService validationService) {
		this.menuService = menuService;
		this.menuAccessService = menuAccessService;
		this.validationService = validationService;
	}
	
	/**
	 * Display inventory summary page
	 */
	@GetMapping
	public String showInventory(Model model, HttpSession session) {
		try {
			logger.info("Loading inventory summary page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addViewAttributes(model, categories, "/inventory");
			
			List<Inventory> inventories = ControllerReflectionUtil.toList(menuService.getInventories());
			model.addAttribute("inventorySummary", inventories);
			model.addAttribute("currentUser", SessionUtil.getSessionUserName(session));
			
			return AppConstants.View.INVENTORY;
		} catch (Exception e) {
			logger.warn("Error loading inventory", e);
			model.addAttribute("error", "Failed to load inventory: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Display inventory by item view
	 */
	@GetMapping("/by-item")
	public String showInventoryByItem(Model model, HttpSession session) {
		try {
			logger.info("Loading inventory by item page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addViewAttributes(model, categories, "/inventory/by-item");
			
			model.addAttribute("inventoryByItem", ControllerReflectionUtil.toList(menuService.getInventories()));
			model.addAttribute("currentUser", SessionUtil.getSessionUserName(session));
			
			return AppConstants.View.INVENTORY_BY_ITEM;
		} catch (Exception e) {
			logger.warn("Error loading inventory by item: {}", e.getMessage());
			model.addAttribute("error", "Failed to load inventory by item: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Display inventory by location view
	 */
	@GetMapping("/by-location")
	public String showInventoryByLocation(Model model, HttpSession session) {
		try {
			logger.info("Loading inventory by location page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addViewAttributes(model, categories, "/inventory/by-location");
			
			model.addAttribute("inventoryByLocation", ControllerReflectionUtil.toList(menuService.getInventories()));
			model.addAttribute("currentUser", SessionUtil.getSessionUserName(session));
			
			return AppConstants.View.INVENTORY_BY_LOCATION;
		} catch (Exception e) {
			logger.warn("Error loading inventory by location: {}", e.getMessage());
			model.addAttribute("error", "Failed to load inventory by location: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Display inventory by LPN view
	 */
	@GetMapping("/by-lpn")
	public String showInventoryByLpn(Model model, HttpSession session) {
		try {
			logger.info("Loading inventory by LPN page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addViewAttributes(model, categories, "/inventory/by-lpn");
			
			model.addAttribute("inventoryByLpn", ControllerReflectionUtil.toList(menuService.getInventories()));
			model.addAttribute("currentUser", SessionUtil.getSessionUserName(session));
			
			return AppConstants.View.INVENTORY_BY_LPN;
		} catch (Exception e) {
			logger.warn("Error loading inventory by LPN: {}", e.getMessage());
			model.addAttribute("error", "Failed to load inventory by LPN: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Get inventory details by ID
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> getInventory(@PathVariable int id) {
		try {
			if (!validationService.validateGreaterThanZero(id, "Inventory ID")) {
				return ResponseUtil.error("Invalid inventory ID");
			}
			
			logger.info("Fetching inventory: {}", id);
			
			Inventory inventory = menuService.getInventoryById(id);

			if (inventory == null) {
				return ResponseUtil.error("Inventory not found for ID: " + id);
			}
			
			return ResponseUtil.success(inventory);
		} catch (Exception e) {
			logger.warn("Error fetching inventory: {}", id, e);
			return ResponseUtil.error("Failed to fetch inventory: " + e.getMessage());
		}
	}
	
	/**
	 * Get all inventory
	 */
	@GetMapping("/all")
	@ResponseBody
	public ResponseEntity<?> getAllInventory() {
		try {
			logger.info("Fetching all inventory");
			
			List<Inventory> inventory = ControllerReflectionUtil.toList(menuService.getInventories());
			
			return ResponseUtil.success(inventory);
		} catch (Exception e) {
			logger.warn("Error fetching all inventory", e);
			return ResponseUtil.error("Failed to fetch inventory: " + e.getMessage());
		}
	}
	
	/**
	 * Get inventory by item ID
	 */
	@GetMapping("/item/{itemId}")
	@ResponseBody
	public ResponseEntity<?> getInventoryByItem(@PathVariable int itemId) {
		try {
			if (!validationService.validateGreaterThanZero(itemId, "Item ID")) {
				return ResponseUtil.error("Invalid item ID");
			}
			
			logger.info("Fetching inventory for item: {}", itemId);
			
			List<Inventory> inventory = menuService.getInventoriesByItemId(itemId);
			
			return ResponseUtil.success(inventory);
		} catch (Exception e) {
			logger.warn("Error fetching inventory by item: {}", itemId, e);
			return ResponseUtil.error("Failed to fetch inventory: " + e.getMessage());
		}
	}
	
	/**
	 * Get inventory by location ID
	 */
	@GetMapping("/location/{locationId}")
	@ResponseBody
	public ResponseEntity<?> getInventoryByLocation(@PathVariable int locationId) {
		try {
			if (!validationService.validateGreaterThanZero(locationId, "Location ID")) {
				return ResponseUtil.error("Invalid location ID");
			}
			
			logger.info("Fetching inventory for location: {}", locationId);
			
			List<Inventory> inventory = menuService.getInventoriesByLocationId(locationId);
			
			return ResponseUtil.success(inventory);
		} catch (Exception e) {
			logger.warn("Error fetching inventory by location: {}", locationId, e);
			return ResponseUtil.error("Failed to fetch inventory: " + e.getMessage());
		}
	}
	
	/**
	 * Get inventory by LPN ID
	 */
	@GetMapping("/lpn/{lpnId}")
	@ResponseBody
	public ResponseEntity<?> getInventoryByLpn(@PathVariable int lpnId) {
		try {
			if (!validationService.validateGreaterThanZero(lpnId, "LPN ID")) {
				return ResponseUtil.error("Invalid LPN ID");
			}
			
			logger.info("Fetching inventory for LPN: {}", lpnId);
			
			List<Inventory> inventory = menuService.getInventoriesByLpnId(lpnId);
			
			return ResponseUtil.success(inventory);
		} catch (Exception e) {
			logger.warn("Error fetching inventory by LPN: {}", lpnId, e);
			return ResponseUtil.error("Failed to fetch inventory: " + e.getMessage());
		}
	}
	
	/**
	 * Search inventory
	 */
	@GetMapping("/search")
	@ResponseBody
	public ResponseEntity<?> searchInventory(
			@RequestParam(required = false) Integer itemId,
			@RequestParam(required = false) Integer locationId,
			@RequestParam(required = false) Integer lpnId) {
		try {
			logger.info("Searching inventory with itemId: {}, locationId: {}, lpnId: {}", itemId, locationId, lpnId);
			
			List<Inventory> inventory = menuService.searchInventories(itemId, locationId, lpnId);
			
			return ResponseUtil.success(inventory);
		} catch (Exception e) {
			logger.warn("Error searching inventory", e);
			return ResponseUtil.error("Failed to search inventory: " + e.getMessage());
		}
	}
	
	/**
	 * Get inventory count summary
	 */
	@GetMapping("/summary")
	@ResponseBody
	public ResponseEntity<?> getInventorySummary() {
		try {
			logger.info("Fetching inventory summary");
			
			List<Inventory> inventories = ControllerReflectionUtil.toList(menuService.getInventories());
			java.util.Map<String, Object> summary = new java.util.HashMap<>();
			summary.put("totalItems", inventories.size());
			summary.put("totalLocations", inventories.stream()
					.map(inv -> ControllerReflectionUtil.extractInt(inv, "getLocation_id", "getLocationId"))
					.filter(java.util.Objects::nonNull)
					.distinct()
					.count());
			summary.put("totalLpns", inventories.stream()
					.map(inv -> ControllerReflectionUtil.extractInt(inv, "getLpn_id", "getLpnId"))
					.filter(java.util.Objects::nonNull)
					.distinct()
					.count());
			summary.put("totalQuantity", inventories.stream()
					.map(inv -> ControllerReflectionUtil.extractInt(inv, "getQuantity", "getQty"))
					.filter(java.util.Objects::nonNull)
					.mapToInt(Integer::intValue)
					.sum());
			
			return ResponseUtil.success(summary);
		} catch (Exception e) {
			logger.warn("Error fetching inventory summary", e);
			return ResponseUtil.error("Failed to fetch inventory summary: " + e.getMessage());
		}
	}

}
