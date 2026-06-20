package com.pawar.inventory.app.service.base;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.config.ExternalApiProperties;
import com.pawar.inventory.app.repository.MenuAuthRepositoryCustom;
import com.pawar.inventory.app.repository.base.AbstractBaseRepository;
import com.pawar.sop.http.service.HttpService;

/**
 * Service for external API operations.
 * Centralizes all HTTP calls to external SOP service and provides wrapper methods.
 * 
 * Fetches complete API endpoint URLs from database via MenuAuthRepositoryCustom.getUrl()
 * Database returns full URL: protocol + hostname + menu_link (e.g., http://host:8080/api/endpoint)
 * 
 * Extends AbstractBaseRepository to use centralized HTTP client utilities.
 */
@Service
public class ExternalApiService extends AbstractBaseRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);

	private final MenuAuthRepositoryCustom menuAuthRepositoryCustom;
	private final HttpService httpService;
	private final ExternalApiProperties externalApiProperties;

	public ExternalApiService(MenuAuthRepositoryCustom menuAuthRepositoryCustom, HttpService httpService,
			ExternalApiProperties externalApiProperties) {
		this.menuAuthRepositoryCustom = menuAuthRepositoryCustom;
		this.httpService = httpService;
		this.externalApiProperties = externalApiProperties;
	}
	
	/**
	 * Fetches URL from database for given menu name (API endpoint name)
	 * Database query returns: protocol + hostname + menu_link as complete URL
	 * Example: "http://192.168.1.100:8085/api/lpn/create"
	 * 
	 * @param menuName the menu/endpoint name (e.g., "CreateLpn", "UpdateLpn", "GetLpns")
	 * @return complete full URL from database including protocol, host, and path
	 */
	private String getUrl(String menuName) {
		try {
			String url = menuAuthRepositoryCustom.getUrl(menuName);
			if (url != null && !url.isEmpty()) {
				logger.info("Resolved URL for {}: {}", menuName, url);
				return url;
			}
			logger.warn("No URL found for menu name: {}. Returning empty string.", menuName);
			return "";
		} catch (Exception e) {
			logger.warn("Error fetching URL for {}", menuName, e);
			return "";
		}
	}
	
	/**
	 * Creates new LPN via external API
	 * Fetches "CreateLpn" endpoint URL from database
	 * 
	 * @param lpnPayload LPN data as JSON string
	 * @return response from API
	 */
	public String createLpn(String lpnPayload) {
		try {
			String url = getUrl(AppConstants.MenuEndpoint.CREATE_LPN);
			logger.info("Creating LPN at: {}", url);
			ResponseEntity<String> response = httpService.restCall(null, url, HttpMethod.POST, lpnPayload, null);
			return response.getBody();
		} catch (Exception e) {
			logger.warn("Error creating LPN", e);
			throw new RuntimeException("Failed to create LPN", e);
		}
	}
	
	/**
	 * Updates existing LPN via external API
	 * Fetches "UpdateLpn" endpoint URL from database (with {lpn_name} and {adjustQty} placeholders)
	 * 
	 * @param lpnName LPN name/number
	 * @param adjustQty adjustment quantity
	 * @param lpnPayload LPN data as JSON string
	 * @return response from API
	 */
	public String updateLpn(String lpnName, int adjustQty, String lpnPayload) {
		try {
			String url = getUrl(AppConstants.MenuEndpoint.UPDATE_LPN)
					.replace("{lpn_name}", lpnName)
					.replace("{adjustQty}", String.valueOf(adjustQty));
			logger.info("Updating LPN at: {}", url);
			ResponseEntity<String> response = httpService.restCall(null, url, HttpMethod.PUT, lpnPayload, null);
			return response.getBody();
		} catch (Exception e) {
			logger.warn("Error updating LPN", e);
			throw new RuntimeException("Failed to update LPN", e);
		}
	}
	
	/**
	 * Gets all LPNs from external API
	 * Fetches "GetLpns" endpoint URL from database
	 * 
	 * @return LPNs data as JSON string
	 */
	public String getLpns() {
		try {
			String url = getUrl(AppConstants.MenuEndpoint.GET_LPNS);
			logger.info("Fetching LPNs from: {}", url);
			ResponseEntity<String> response = httpService.restCall(null, url, HttpMethod.GET, null, null);
			return response.getBody();
		} catch (Exception e) {
			logger.warn("Error fetching LPNs", e);
			throw new RuntimeException("Failed to fetch LPNs", e);
		}
	}
	
	/**
	 * Validates LPN existence via external API
	 * Fetches "validateLpn" endpoint URL from database
	 * 
	 * @param lpnName LPN name/number
	 * @return validation result as JSON string
	 */
	public String validateLpn(String lpnName) {
		try {
			String url = getUrl(AppConstants.MenuEndpoint.VALIDATE_LPN).replace("{lpn_name}", lpnName);
			logger.info("Validating LPN at: {}", url);
			ResponseEntity<String> response = httpService.restCall(null, url, HttpMethod.GET, null, null);
			return response.getBody();
		} catch (Exception e) {
			logger.warn("Error validating LPN", e);
			throw new RuntimeException("Failed to validate LPN", e);
		}
	}
	
	/**
	 * Moves LPN to reserve location via external API
	 * Fetches "locateLpnToResv" endpoint URL from database
	 * 
	 * @param lpnName LPN name/number
	 * @param reserveLocation reserve location code
	 * @return response from API
	 */
	public String locateLpnToReserve(String lpnName, String reserveLocation) {
		try {
			String url = getUrl(AppConstants.MenuEndpoint.LOCATE_LPN_TO_RESERVE)
					.replace("{lpn_name}", lpnName)
					.replace("{resv_locn}", reserveLocation);
			logger.info("Moving LPN to reserve location at: {}", url);
			ResponseEntity<String> response = httpService.restCall(null, url, HttpMethod.POST, null, null);
			return response.getBody();
		} catch (Exception e) {
			logger.warn("Error moving LPN to reserve", e);
			throw new RuntimeException("Failed to move LPN to reserve", e);
		}
	}
	
	/**
	 * Moves LPN to active location via external API
	 * Fetches "locateLpnToActive" endpoint URL from database
	 * 
	 * @param lpnName LPN name/number
	 * @param activeLocation active location code
	 * @return response from API
	 */
	public String locateLpnToActive(String lpnName, String activeLocation) {
		try {
			String url = getUrl(AppConstants.MenuEndpoint.LOCATE_LPN_TO_ACTIVE)
					.replace("{lpn_name}", lpnName)
					.replace("{active_locn}", activeLocation);
			logger.info("Moving LPN to active location at: {}", url);
			ResponseEntity<String> response = httpService.restCall(null, url, HttpMethod.GET, null, null);
			return response.getBody();
		} catch (Exception e) {
			logger.warn("Error moving LPN to active", e);
			throw new RuntimeException("Failed to move LPN to active", e);
		}
	}
	
	/**
	 * Generic method to fetch API endpoint URL from database
	 * Used for fetching specific endpoint definitions stored in menu table
	 * 
	 * @param menuName the endpoint name (e.g., "GetItem", "AddCategory")
	 * @return full URL including protocol and hostname
	 */
	public String getApiUrl(String menuName) {
		return getUrl(menuName);
	}
	
	/**
	 * Generic HTTP call wrapper for all HTTP methods
	 * Delegates to HttpService.restCall() for centralized HTTP handling
	 * 
	 * @param token authorization token (JWT)
	 * @param menuName endpoint name to resolve URL from database
	 * @param httpMethod HTTP method (GET, POST, PUT, DELETE)
	 * @param jsonPayload request body (null for GET/DELETE)
	 * @return HTTP response entity
	 */
	public ResponseEntity<String> callExternalApi(
			String token, String menuName, HttpMethod httpMethod, String jsonPayload) {
		try {
			String url = getUrl(menuName);
			logger.info("Calling external API: {} at URL: {}", menuName, url);
			return httpService.restCall(token, url, httpMethod, jsonPayload, null);
		} catch (Exception e) {
			logger.warn("Error calling external API: {}", menuName, e);
			throw new RuntimeException("Failed to call external API: " + menuName, e);
		}
	}
	
	/**
	 * Generic HTTP call wrapper with explicit URL
	 * Delegates to HttpService.restCall() for centralized HTTP handling
	 * For cases where URL contains placeholders that need pre-replacement
	 * 
	 * @param token authorization token (JWT)
	 * @param url complete URL (can have placeholders like {id}, {name})
	 * @param httpMethod HTTP method (GET, POST, PUT, DELETE)
	 * @param jsonPayload request body (null for GET/DELETE)
	 * @return HTTP response entity
	 */
	public ResponseEntity<String> callExternalApiWithUrl(
			String token, String url, HttpMethod httpMethod, String jsonPayload) {
		try {
			logger.info("Calling external API at URL: {} with method: {}", url, httpMethod);
			return httpService.restCall(token, url, httpMethod, jsonPayload, null);
		} catch (Exception e) {
			logger.warn("Error executing HTTP call", e);
			throw new RuntimeException("Failed to execute HTTP call", e);
		}
	}
	
	/**
	 * Gets the configured external API URL (fallback)
	 * 
	 * @return external API base URL from properties
	 */
	public String getExternalApiUrl() {
		return externalApiProperties.getUrl();
	}
	
	/**
	 * Sets the external API URL
	 * 
	 * @param url new API base URL
	 */
	public void setExternalApiUrl(String url) {
		externalApiProperties.setUrl(url);
		logger.info("External API URL updated to: {}", url);
	}
}
