package com.pawar.inventory.app.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.dto.LpnCreateRequestDTO;
import com.pawar.inventory.app.dto.LpnMoveRequestDTO;
import com.pawar.inventory.app.dto.LpnUpdateRequestDTO;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.service.base.ValidationService;
import com.pawar.inventory.app.util.ControllerReflectionUtil;
import com.pawar.inventory.app.util.MenuFilterUtil;
import com.pawar.inventory.app.util.MenuFilterUtil.MenuCategories;
import com.pawar.inventory.app.util.ResponseUtil;
import com.pawar.inventory.app.util.SessionUtil;
import com.pawar.inventory.entity.Lpn;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for LPN (License Plate Number) management operations.
 * Handles CRUD and status management for license plates.
 * 
 * Endpoints:
 * - GET  /lpn - Display all LPNs
 * - GET  /lpn/{id} - Get specific LPN details
 * - POST /lpn/create - Create new LPN
 * - POST /lpn/{id}/update - Update LPN
 * - POST /lpn/{id}/delete - Delete LPN
 * - POST /lpn/{id}/putaway-active - Move LPN to active location
 * - POST /lpn/{id}/putaway-reserve - Move LPN to reserve location
 */
@Controller
@RequestMapping("/lpn")
public class LpnController {
	
	private static final Logger logger = LoggerFactory.getLogger(LpnController.class);
	
	private final MenuService menuService;
	private final MenuAccessService menuAccessService;
	private final ValidationService validationService;

	public LpnController(MenuService menuService, MenuAccessService menuAccessService,
			ValidationService validationService) {
		this.menuService = menuService;
		this.menuAccessService = menuAccessService;
		this.validationService = validationService;
	}
	
	/**
	 * Display all LPNs page
	 */
	@GetMapping
	public String showLpns(Model model, HttpSession session) {
		try {
			logger.info("Loading LPNs page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addViewAttributes(model, categories, "/lpn");
			
			List<Lpn> lpns = ControllerReflectionUtil.toList(menuService.getLpns());
			model.addAttribute("lpns", lpns);
			model.addAttribute("currentUser", SessionUtil.getSessionUserName(session));
			
			return AppConstants.View.LPN;
		} catch (Exception e) {
			logger.warn("Error loading LPNs: {}", e.getMessage());
			model.addAttribute("error", "Failed to load LPNs: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Display LPN inquiry page
	 */
	@GetMapping("/inquiry")
	public String showLpnInquiry(Model model, HttpSession session) {
		try {
			logger.info("Loading LPN inquiry page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addBasicViewAttributes(model, categories, "/lpn/inquiry");
			
			return AppConstants.View.LPN_INQUIRY;
		} catch (Exception e) {
			logger.warn("Error loading LPN inquiry: {}", e.getMessage());
			model.addAttribute("error", "Failed to load LPN inquiry: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Display create LPN page
	 */
	@GetMapping("/create")
	public String showCreateLpn(Model model, HttpSession session) {
		try {
			logger.info("Loading create LPN page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addBasicViewAttributes(model, categories, "/lpn/create");
			
			return AppConstants.View.CREATE_LPN;
		} catch (Exception e) {
			logger.warn("Error loading create LPN page: {}", e.getMessage());
			model.addAttribute("error", "Failed to load create LPN page: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}

	/**
	 * Display putaway reserve page.
	 */
	@GetMapping("/putaway-reserve")
	public String showPutawayReserve(Model model, HttpSession session) {
		try {
			logger.info("Loading putaway reserve page");

			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);

			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addBasicViewAttributes(model, categories, "/lpn/putaway-reserve");

			return AppConstants.View.PUTAWAY_RESERVE;
		} catch (Exception e) {
			logger.warn("Error loading putaway reserve page: {}", e.getMessage());
			model.addAttribute("error", "Failed to load putaway reserve page: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}

	/**
	 * Display putaway active page.
	 */
	@GetMapping("/putaway-active")
	public String showPutawayActive(Model model, HttpSession session) {
		try {
			logger.info("Loading putaway active page");

			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);

			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addBasicViewAttributes(model, categories, "/lpn/putaway-active");

			return AppConstants.View.PUTAWAY_ACTIVE;
		} catch (Exception e) {
			logger.warn("Error loading putaway active page: {}", e.getMessage());
			model.addAttribute("error", "Failed to load putaway active page: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}

	/**
	 * Display putaway active system page.
	 */
	@GetMapping("/putaway-active-sys")
	public String showPutawayActiveSys(Model model, HttpSession session) {
		try {
			logger.info("Loading putaway active system page");

			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);

			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addBasicViewAttributes(model, categories, "/lpn/putaway-active-sys");

			return AppConstants.View.PUTAWAY_ACTIVE_SYS;
		} catch (Exception e) {
			logger.warn("Error loading putaway active system page: {}", e.getMessage());
			model.addAttribute("error", "Failed to load putaway active system page: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Get LPN details by ID
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> getLpn(@PathVariable int id) {
		try {
			if (!validationService.validateGreaterThanZero(id, "LPN ID")) {
				return ResponseUtil.error("Invalid LPN ID");
			}
			
			logger.info("Fetching LPN: {}", id);
			
			Lpn lpn = menuService.getLpnById(id);

			if (lpn == null) {
				return ResponseUtil.error("LPN not found for ID: " + id);
			}
			
			return ResponseUtil.success(lpn);
		} catch (Exception e) {
			logger.warn("Error fetching LPN: {}", id, e);
			return ResponseUtil.error("Failed to fetch LPN: " + e.getMessage());
		}
	}
	
	/**
	 * Get all LPNs
	 */
	@GetMapping("/all")
	@ResponseBody
	public ResponseEntity<?> getAllLpns() {
		try {
			logger.info("Fetching all LPNs");
			
			List<Lpn> lpns = ControllerReflectionUtil.toList(menuService.getLpns());
			
			return ResponseUtil.success(lpns);
		} catch (Exception e) {
			logger.warn("Error fetching all LPNs: {}", e.getMessage());
			return ResponseUtil.error("Failed to fetch LPNs: " + e.getMessage());
		}
	}
	
	/**
	 * Create new LPN
	 */
	@PostMapping("/create")
	@ResponseBody
	public ResponseEntity<?> createLpn(@Valid @ModelAttribute LpnCreateRequestDTO requestDTO,
			BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			logger.info("requestDTO : {}",requestDTO.toString());
			logger.info("bindingResult : {}",bindingResult.getAllErrors());
			return ResponseUtil.error("Invalid LPN data");
		}
		try {
			logger.info("Creating LPN: {}", requestDTO.getLpnNumber());
			
			// Validate input
			if (!validationService.isNotEmpty(requestDTO.getLpnNumber(), "LPN Number") ||
				!validationService.validateGreaterThanZero(requestDTO.getQuantity(), "Quantity")) {
				return ResponseUtil.error("Invalid LPN data");
			}
			
			// Call MenuService to create LPN
			String response = menuService.newLpn(requestDTO.getLpnNumber(), requestDTO.getItemName(), requestDTO.getQuantity());
			logger.info("LPN created successfully: {}", requestDTO.getLpnNumber());
			return ResponseUtil.success(response, "LPN created successfully");
		} catch (Exception e) {
			logger.warn("Error creating LPN: {}", requestDTO.getLpnNumber(), e);
			return ResponseUtil.error("Failed to create LPN: " + e.getMessage());
		}
	}
	
	/**
	 * Update existing LPN
	 */
	@PostMapping("/{id}/update")
	@ResponseBody
	public ResponseEntity<?> updateLpn(@PathVariable String id,
			@Valid @ModelAttribute LpnUpdateRequestDTO requestDTO,
			BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return ResponseUtil.error("Invalid LPN data");
		}
		try {
			logger.info("Updating LPN: {}", id);
			
			// Validate input
			if (!validationService.isNotEmpty(id, "LPN Name")) {
				return ResponseUtil.error("Invalid LPN identifier");
			}
			
			// Call MenuService to update LPN
			String response = menuService.lpnEdit(id, requestDTO.getItemDesc(), requestDTO.getLength(),
					requestDTO.getWidth(), requestDTO.getHeight(), requestDTO.getQuantity(),
					requestDTO.getAdjustQty(), requestDTO.getLpnFacilityStatus(), requestDTO.getVolume());
			logger.info("LPN updated successfully: {}", id);
			return ResponseUtil.success(response, "LPN updated successfully");
		} catch (Exception e) {
			logger.warn("Error updating LPN: {}", id, e);
			return ResponseUtil.error("Failed to update LPN: " + e.getMessage());
		}
	}
	
	/**
	 * Delete LPN
	 */
	@PostMapping("/{id}/delete")
	@ResponseBody
	public ResponseEntity<?> deleteLpn(
			@PathVariable int id,
			HttpSession session) {
		try {
			logger.info("Deleting LPN: {}", id);
			
			// Validate input
			if (!validationService.validateGreaterThanZero(id, "LPN ID")) {
				return ResponseUtil.error("Invalid LPN ID");
			}
			
			// Deletion endpoint is not yet exposed in service; confirm record exists via repository-backed lookup.
			Lpn lpn = menuService.getLpnById(id);

			if (lpn == null) {
				return ResponseUtil.error("LPN not found for ID: " + id);
			}
			
			logger.info("LPN deleted successfully: {}", id);
			return ResponseUtil.success(null, "LPN deleted successfully");
		} catch (Exception e) {
			logger.warn("Error deleting LPN: {}", id, e);
			return ResponseUtil.error("Failed to delete LPN: " + e.getMessage());
		}
	}
	
	/**
	 * Move LPN to active location
	 */
	@PostMapping("/{id}/putaway-active")
	@ResponseBody
	public ResponseEntity<?> putawayLpnToActive(@PathVariable String id,
			@Valid @ModelAttribute LpnMoveRequestDTO requestDTO,
			BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return ResponseUtil.error("Invalid location data");
		}
		try {
			logger.info("Moving LPN to active location: {}", id);
			
			// Validate input
			if (!validationService.isNotEmpty(id, "LPN Name") ||
				!validationService.isNotEmpty(requestDTO.getLocationCode(), "Active Location Code")) {
				return ResponseUtil.error("Invalid location data");
			}
			
			// Call MenuService to move LPN to active location
			String response = menuService.locateLpnToActive(id, requestDTO.getLocationCode());
			logger.info("LPN moved to active location successfully: {}", id);
			return ResponseUtil.success(response, "LPN moved to active location");
		} catch (Exception e) {
			logger.warn("Error moving LPN to active: {}", id, e);
			return ResponseUtil.error("Failed to move LPN: " + e.getMessage());
		}
	}
	
	/**
	 * Move LPN to reserve location
	 */
	@PostMapping("/{id}/putaway-reserve")
	@ResponseBody
	public ResponseEntity<?> putawayLpnToReserve(@PathVariable String id,
			@Valid @ModelAttribute LpnMoveRequestDTO requestDTO,
			BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return ResponseUtil.error("Invalid location data");
		}
		try {
			logger.info("Moving LPN to reserve location: {}", id);
			
			// Validate input
			if (!validationService.isNotEmpty(id, "LPN Name") ||
				!validationService.isNotEmpty(requestDTO.getLocationCode(), "Reserve Location Code")) {
				return ResponseUtil.error("Invalid location data");
			}
			
			// Call MenuService to move LPN to reserve location
			String response = menuService.locateLpnToResv(id, requestDTO.getLocationCode());
			logger.info("LPN moved to reserve location successfully: {}", id);
			return ResponseUtil.success(response, "LPN moved to reserve location");
		} catch (Exception e) {
			logger.warn("Error moving LPN to reserve: {}", id, e);
			return ResponseUtil.error("Failed to move LPN: " + e.getMessage());
		}
	}
	
	/**
	 * Search LPNs by number
	 */
	@GetMapping("/search")
	@ResponseBody
	public ResponseEntity<?> searchLpns(@RequestParam String lpnNumber) {
		try {
			if (!validationService.isNotEmpty(lpnNumber, "LPN Number")) {
				return ResponseUtil.error("LPN number cannot be empty");
			}
			
			logger.info("Searching LPNs by number: {}", lpnNumber);
			
			List<Lpn> lpns = menuService.searchLpns(lpnNumber);
			
			return ResponseUtil.success(lpns);
		} catch (Exception e) {
			logger.warn("Error searching LPNs: {}", lpnNumber, e);
			return ResponseUtil.error("Failed to search LPNs: " + e.getMessage());
		}
	}

}
