package com.pawar.inventory.app.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.service.base.ValidationService;
import com.pawar.inventory.app.dto.LocationRequestDTO;
import com.pawar.inventory.app.util.ControllerReflectionUtil;
import com.pawar.inventory.app.util.MenuFilterUtil;
import com.pawar.inventory.app.util.MenuFilterUtil.MenuCategories;
import com.pawar.inventory.app.util.ResponseUtil;
import com.pawar.inventory.app.util.SessionUtil;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for Location management operations.
 * Handles CRUD operations for warehouse locations and location inquiries.
 * 
 * Endpoints:
 * - GET  /location - Display all locations
 * - GET  /location/{id} - Get specific location details
 * - POST /location/create - Create new location
 * - POST /location/{id}/update - Update location
 * - POST /location/{id}/delete - Delete location
 */
@Controller
@RequestMapping("/location")
public class LocationController {
	
	private static final Logger logger = LoggerFactory.getLogger(LocationController.class);
	
	private final MenuService menuService;
	private final MenuAccessService menuAccessService;
	private final ValidationService validationService;

	public LocationController(MenuService menuService, MenuAccessService menuAccessService,
			ValidationService validationService) {
		this.menuService = menuService;
		this.menuAccessService = menuAccessService;
		this.validationService = validationService;
	}
	
	/**
	 * Display all locations page
	 */
	@GetMapping
	public String showLocations(Model model, HttpSession session) {
		try {
			logger.info("Loading locations page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addViewAttributes(model, categories, "/location");
			
			model.addAttribute("locations", menuService.getLocations());
			model.addAttribute("grps", menuService.getGrps());
			model.addAttribute("currentUser", SessionUtil.getSessionUserName(session));
			
			return AppConstants.View.LOCATION;
		} catch (Exception e) {
			logger.warn("Error loading locations", e);
			model.addAttribute("error", "Failed to load locations: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Display location inquiry page
	 */
	@GetMapping("/inquiry")
	public String showLocationInquiry(Model model, HttpSession session) {
		try {
			logger.info("Loading location inquiry page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addBasicViewAttributes(model, categories, "/location/inquiry");
			
			return AppConstants.View.LOCATION_INQUIRY;
		} catch (Exception e) {
			logger.warn("Error loading location inquiry", e);
			model.addAttribute("error", "Failed to load location inquiry: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Get location details by ID
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> getLocation(@PathVariable int id) {
		try {
			if (!validationService.validateGreaterThanZero(id, "Location ID")) {
				return ResponseUtil.error("Invalid location ID");
			}
			
			logger.info("Fetching location: {}", id);
			
			Object location = menuService.getLocationById(id);

			if (location == null) {
				return ResponseUtil.error("Location not found for ID: " + id);
			}
			
			return ResponseUtil.success(toMap(location));
		} catch (Exception e) {
			logger.warn("Error fetching location: {}", id, e);
			return ResponseUtil.error("Failed to fetch location: " + e.getMessage());
		}
	}
	
	/**
	 * Get all locations
	 */
	@GetMapping("/all")
	@ResponseBody
	public ResponseEntity<?> getAllLocations() {
		try {
			logger.info("Fetching all locations");
			
			List<java.util.Map<String, Object>> locations = toMapList(menuService.getLocations());
			
			return ResponseUtil.success(locations);
		} catch (Exception e) {
			logger.warn("Error fetching all locations", e);
			return ResponseUtil.error("Failed to fetch locations: " + e.getMessage());
		}
	}
	
	/**
	 * Create new location
	 */
	@PostMapping("/create")
	@ResponseBody
    public ResponseEntity<?> createLocation(@Valid @ModelAttribute LocationRequestDTO requestDTO,
            BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return ResponseUtil.error("Invalid location data");
		}

		try {
			logger.info("Creating location: {}", requestDTO.getLocationCode());
			
			// Validate input
			if (!validationService.validateLocationRequest(requestDTO.getLocationCode(), requestDTO.getLocationName())) {
				return ResponseUtil.error("Invalid location data");
			}
			
			String response = menuService.locationAdd(requestDTO.getLocationCode(), requestDTO.getGrp(),
					requestDTO.getLocationClass(), requestDTO.getLength(), requestDTO.getWidth(), requestDTO.getHeight(),
					requestDTO.getMaxVolume(), requestDTO.getMaxQty(), requestDTO.getMaxWeight());
			java.util.Map<String, Object> location = new java.util.HashMap<>();
			location.put("locationCode", requestDTO.getLocationCode());
			location.put("locationName", requestDTO.getLocationName());
			location.put("response", response);
			
			logger.info("Location created successfully: {}", requestDTO.getLocationCode());
			return ResponseUtil.success(location, "Location created successfully");
		} catch (Exception e) {
			logger.warn("Error creating location: {}", requestDTO.getLocationCode(), e);
			return ResponseUtil.error("Failed to create location: " + e.getMessage());
		}
	}
	
	/**
	 * Update existing location
	 */
	@PostMapping("/{id}/update")
	@ResponseBody
	    public ResponseEntity<?> updateLocation(@PathVariable int id, @Valid @ModelAttribute LocationRequestDTO requestDTO,
			BindingResult bindingResult, HttpSession session) {
			if (bindingResult.hasErrors()) {
				return ResponseUtil.error("Invalid location data");
			}

		try {
			logger.info("Updating location: {}", id);
			
			// Validate input
			if (!validationService.validateGreaterThanZero(id, "Location ID")) {
				return ResponseUtil.error("Invalid location ID");
			}
			
			if (!validationService.validateLocationRequest(requestDTO.getLocationCode(), requestDTO.getLocationName())) {
				return ResponseUtil.error("Invalid location data");
			}
			
			ResponseEntity<String> response = menuService.locationEdit(requestDTO.getLocationCode(), requestDTO.getGrp(),
					requestDTO.getLocationClass(), requestDTO.getLength(), requestDTO.getWidth(), requestDTO.getHeight(),
					requestDTO.getMaxVolume(), requestDTO.getMaxQty(), requestDTO.getMaxWeight());
			java.util.Map<String, Object> location = new java.util.HashMap<>();
			location.put("locationId", id);
			location.put("locationCode", requestDTO.getLocationCode());
			location.put("locationName", requestDTO.getLocationName());
			location.put("response", response.getBody());
			
			logger.info("Location updated successfully: {}", id);
			return ResponseUtil.success(location, "Location updated successfully");
		} catch (Exception e) {
			logger.warn("Error updating location: {}", id, e);
			return ResponseUtil.error("Failed to update location: " + e.getMessage());
		}
	}
	
	/**
	 * Delete location
	 */
	@PostMapping("/{id}/delete")
	@ResponseBody
	public ResponseEntity<?> deleteLocation(
			@PathVariable int id,
			HttpSession session) {
		try {
			logger.info("Deleting location: {}", id);
			
			// Validate input
			if (!validationService.validateGreaterThanZero(id, "Location ID")) {
				return ResponseUtil.error("Invalid location ID");
			}
			
			Object location = menuService.getLocationById(id);
			java.util.Map<String, Object> locationMap = location == null ? null : toMap(location);

			if (locationMap == null || locationMap.get("locationCode") == null) {
				return ResponseUtil.error("Location not found for ID: " + id);
			}

			menuService.deleteLocation(String.valueOf(locationMap.get("locationCode")));
			
			logger.info("Location deleted successfully: {}", id);
			return ResponseUtil.success(null, "Location deleted successfully");
		} catch (Exception e) {
			logger.warn("Error deleting location: {}", id, e);
			return ResponseUtil.error("Failed to delete location: " + e.getMessage());
		}
	}
	
	/**
	 * Search locations by code
	 */
	@GetMapping("/search")
	@ResponseBody
	public ResponseEntity<?> searchLocations(@RequestParam String code) {
		try {
			if (!validationService.isNotEmpty(code, "Location Code")) {
				return ResponseUtil.error("Location code cannot be empty");
			}
			
			logger.info("Searching locations by code: {}", code);
			
			List<java.util.Map<String, Object>> locations = toMapList(menuService.searchLocationsByCode(code));
			
			return ResponseUtil.success(locations);
		} catch (Exception e) {
			logger.warn("Error searching locations: {}", code, e);
			return ResponseUtil.error("Failed to search locations: " + e.getMessage());
		}
	}

	private List<java.util.Map<String, Object>> toMapList(Iterable<?> source) {
		List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
		if (source == null) {
			return result;
		}

		source.forEach(location -> {
			java.util.Map<String, Object> mapped = new java.util.HashMap<>();
			mapped.put("locationId", ControllerReflectionUtil.extractInt(location, "getLocn_id", "getLocationId", "getId"));
			mapped.put("locationCode", ControllerReflectionUtil.extractString(location, "getLocn_brcd", "getLocationCode", "getLocnBrcd"));
			mapped.put("locationName", ControllerReflectionUtil.extractString(location, "getLocn_name", "getLocationName", "getLocnName"));
			mapped.put("raw", location);
			result.add(mapped);
		});

		return result;
	}

	private java.util.Map<String, Object> toMap(Object location) {
		java.util.Map<String, Object> mapped = new java.util.HashMap<>();
		mapped.put("locationId", ControllerReflectionUtil.extractInt(location, "getLocn_id", "getLocationId", "getId"));
		mapped.put("locationCode", ControllerReflectionUtil.extractString(location, "getLocn_brcd", "getLocationCode", "getLocnBrcd"));
		mapped.put("locationName", ControllerReflectionUtil.extractString(location, "getLocn_name", "getLocationName", "getLocnName"));
		mapped.put("raw", location);
		return mapped;
	}
}
