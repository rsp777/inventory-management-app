package com.pawar.inventory.app.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.dto.SettingUpdateRequestDTO;
import com.pawar.inventory.app.dto.SopConfigRequestDTO;
import com.pawar.inventory.app.dto.UserPreferencesRequestDTO;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.SettingsService;
import com.pawar.inventory.app.service.base.ValidationService;
import com.pawar.inventory.app.util.MenuFilterUtil;
import com.pawar.inventory.app.util.MenuFilterUtil.MenuCategories;
import com.pawar.inventory.app.util.ResponseUtil;
import com.pawar.inventory.app.util.SessionUtil;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for Application Settings and Configuration.
 * Manages system-wide settings, user preferences, and SOP configuration.
 * 
 * Endpoints:
 * - GET  /settings - Display settings page
	@GetMapping("/all")
 * - POST /settings/{key}/update - Update setting
 * - GET  /settings/user - Get user preferences
 * - POST /settings/user/update - Update user preferences
 * - GET  /settings/sop-config - Get SOP configuration
 * - POST /settings/sop-config/update - Update SOP configuration
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {
	
	private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

	private final MenuAccessService menuAccessService;
	private final SettingsService settingsService;
	private final ValidationService validationService;

	public SettingsController(MenuAccessService menuAccessService, SettingsService settingsService,
			ValidationService validationService) {
		this.menuAccessService = menuAccessService;
		this.settingsService = settingsService;
		this.validationService = validationService;
	}
	
	/**
	 * Display application settings page
	 */
	@GetMapping
	public String showSettings(Model model, HttpSession session) {
		try {
			logger.info("Loading settings page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addViewAttributes(model, categories, "/settings");
			
			Map<String, Object> appSettings = settingsService.getApplicationSettings();
			
			model.addAttribute("appSettings", appSettings);
			model.addAttribute("currentUser", SessionUtil.getSessionUserName(session));
			
			return AppConstants.View.SETTINGS;
		} catch (Exception e) {
			logger.warn("Error loading settings", e);
			model.addAttribute("error", "Failed to load settings: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Display SOP configuration page
	 */
	@GetMapping("/sop-config")
	public String showSopConfig(Model model, HttpSession session) {
		try {
			logger.info("Loading SOP configuration page");
			
			String jwtToken = SessionUtil.getSessionToken(session);
			List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(jwtToken);
			
			MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
			ResponseUtil.addBasicViewAttributes(model, categories, "/settings/sop-config");
			
			Map<String, Object> sopConfig = settingsService.getSopConfiguration();
			
			model.addAttribute("sopConfig", sopConfig);
			
			return AppConstants.View.SOP_CONFIG;
		} catch (Exception e) {
			logger.warn("Error loading SOP config", e);
			model.addAttribute("error", "Failed to load SOP configuration: " + e.getMessage());
			return AppConstants.View.ERROR;
		}
	}
	
	/**
	 * Get specific setting value
	 */
	@GetMapping("/{key}")
	@ResponseBody
	public ResponseEntity<?> getSetting(@PathVariable String key) {
		try {
			if (!validationService.isNotEmpty(key, "Setting Key")) {
				return ResponseUtil.error("Invalid setting key");
			}

			logger.info("Fetching setting: {}", key);
			Map<String, Object> setting = settingsService.getSetting(key);
			
			return ResponseUtil.success(setting);
		} catch (Exception e) {
			logger.warn("Error fetching setting: {}", key, e);
			return ResponseUtil.error("Failed to fetch setting: " + e.getMessage());
		}
	}
	
	/**
	 * Update setting value
	 */
	@PostMapping("/{key}/update")
	@ResponseBody
	public ResponseEntity<?> updateSetting(@PathVariable String key,
			@Valid @ModelAttribute SettingUpdateRequestDTO requestDTO,
			BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return ResponseUtil.error("Invalid setting value");
		}
		try {
			logger.info("Updating setting: {}", key);
			
			// Validate input
			if (!validationService.isNotEmpty(key, "Setting Key")) {
				return ResponseUtil.error("Invalid setting key");
			}
			
			if (!validationService.isNotEmpty(requestDTO.getValue(), "Setting Value")) {
				return ResponseUtil.error("Invalid setting value");
			}
			
			Map<String, Object> updated = settingsService.updateSetting(key, requestDTO.getValue(),
					SessionUtil.getSessionUserName(session));
			
			logger.info("Setting updated successfully: {}", key);
			return ResponseUtil.success(updated, "Setting updated successfully");
		} catch (Exception e) {
			logger.warn("Error updating setting: {}", key, e);
			return ResponseUtil.error("Failed to update setting: " + e.getMessage());
		}
	}
	
	/**
	 * Get all application settings
	 */
	@GetMapping("/all")
	@ResponseBody
	public ResponseEntity<?> getAllSettings() {
		try {
			logger.info("Fetching all settings");
			
			Map<String, Object> allSettings = settingsService.getAllSettings();
			
			return ResponseUtil.success(allSettings);
		} catch (Exception e) {
			logger.warn("Error fetching all settings", e);
			return ResponseUtil.error("Failed to fetch settings: " + e.getMessage());
		}
	}
	
	/**
	 * Get user preferences
	 */
	@GetMapping("/user")
	@ResponseBody
	public ResponseEntity<?> getUserPreferences(HttpSession session) {
		try {
			String userName = SessionUtil.getSessionUserName(session);
			logger.info("Fetching user preferences for: {}", userName);
			
			Map<String, Object> preferences = settingsService.getUserPreferences(userName);
			
			return ResponseUtil.success(preferences);
		} catch (Exception e) {
			logger.warn("Error fetching user preferences", e);
			return ResponseUtil.error("Failed to fetch user preferences: " + e.getMessage());
		}
	}
	
	/**
	 * Update user preferences
	 */
	@PostMapping("/user/update")
	@ResponseBody
	public ResponseEntity<?> updateUserPreferences(@Valid @ModelAttribute UserPreferencesRequestDTO requestDTO,
			BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return ResponseUtil.error("Invalid preferences data");
		}
		try {
			String userName = SessionUtil.getSessionUserName(session);
			logger.info("Updating user preferences for: {}", userName);
			
			// Validate input
			
			if (!validationService.isNotEmpty(requestDTO.getTheme(), "Theme")) {
				return ResponseUtil.error("Invalid theme");
			}
			if (!validationService.isNotEmpty(requestDTO.getLanguage(), "Language")) {
				return ResponseUtil.error("Invalid language");
			}
			
			Map<String, Object> updated = settingsService.updateUserPreferences(userName, requestDTO);
			
			logger.info("User preferences updated successfully for: {}", userName);
			return ResponseUtil.success(updated, "Preferences updated successfully");
		} catch (Exception e) {
			logger.warn("Error updating user preferences", e);
			return ResponseUtil.error("Failed to update preferences: " + e.getMessage());
		}
	}
	
	/**
	 * Get SOP configuration settings
	 */
	@GetMapping("/sop-config/api/get")
	@ResponseBody
	public ResponseEntity<?> getSopConfiguration() {
		try {
			logger.info("Fetching SOP configuration");

			Map<String, Object> sopConfig = settingsService.getSopConfiguration();
			
			return ResponseUtil.success(sopConfig);
		} catch (Exception e) {
			logger.warn("Error fetching SOP configuration", e);
			return ResponseUtil.error("Failed to fetch SOP configuration: " + e.getMessage());
		}
	}
	
	/**
	 * Update SOP configuration settings
	 */
	@PostMapping("/sop-config/update")
	@ResponseBody
	public ResponseEntity<?> updateSopConfiguration(@Valid @ModelAttribute SopConfigRequestDTO requestDTO,
			BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return ResponseUtil.error("Invalid SOP configuration data");
		}
		try {
			logger.info("Updating SOP configuration");
			
			// Validate input
			
			if (!validationService.isNotEmpty(requestDTO.getExternalApiUrl(), "External API URL")) {
				return ResponseUtil.error("Invalid external API URL");
			}
			if (!validationService.validateGreaterThanZero(requestDTO.getTimeout(), "Timeout")) {
				return ResponseUtil.error("Invalid timeout value");
			}
			
			if (!validationService.validateGreaterThanZero(requestDTO.getRetryAttempts(), "Retry Attempts")) {
				return ResponseUtil.error("Invalid retry attempts value");
			}

			Map<String, Object> updated = settingsService.updateSopConfiguration(requestDTO,
					SessionUtil.getSessionUserName(session));
			
			logger.info("SOP configuration updated successfully");
			return ResponseUtil.success(updated, "SOP configuration updated successfully");
		} catch (Exception e) {
			logger.warn("Error updating SOP configuration", e);
			return ResponseUtil.error("Failed to update SOP configuration: " + e.getMessage());
		}
	}
	
	/**
	 * Get application health/status
	 */
	@GetMapping("/health")
	@ResponseBody
	public ResponseEntity<?> getApplicationHealth() {
		try {
			logger.info("Fetching application health");
			
			Map<String, Object> health = settingsService.getApplicationHealth();
			
			return ResponseUtil.success(health);
		} catch (Exception e) {
			logger.warn("Error checking health", e);
			return ResponseUtil.error("Failed to check application health: " + e.getMessage());
		}
	}

}
