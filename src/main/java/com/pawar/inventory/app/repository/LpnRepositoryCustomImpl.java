package com.pawar.inventory.app.repository;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pawar.inventory.app.service.base.ExternalApiService;
import com.pawar.inventory.app.util.ControllerReflectionUtil;
import com.pawar.inventory.entity.Lpn;

/**
 * Implementation of LPN repository using ExternalApiService.
 * 
 * Consolidates all LPN-specific database and external API operations.
 * Uses ExternalApiService for cleaner, centralized HTTP call handling.
 */
@Component
public class LpnRepositoryCustomImpl implements LpnRepositoryCustom {
	
	private static final Logger logger = LoggerFactory.getLogger(LpnRepositoryCustomImpl.class);
	
	@Autowired
	private ExternalApiService externalApiService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * Creates a new LPN via external API
	 * Builds LPN JSON payload and calls CreateLpn endpoint via ExternalApiService
	 * 
	 * @param lpn_name LPN name/number
	 * @param item_name item name associated with LPN
	 * @param quantity quantity of items
	 * @return API response as string
	 */
	@Override
	public String createLpn(String lpn_name, String item_name, int quantity) {
		try {
			logger.info("Creating new LPN: {}", lpn_name);
			
			// Build JSON payload
			JSONObject itemObject = new JSONObject();
			itemObject.put("itemName", item_name);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lpn_name", lpn_name);
			jsonObject.put("item", itemObject);
			jsonObject.put("quantity", quantity);
			
			JSONObject lpnObject = new JSONObject();
			lpnObject.put("lpn", jsonObject);
			String json = lpnObject.toString();
			
			logger.info("LPN Payload: {}", json);
			
			// Call ExternalApiService - URL resolved from database
			String response = externalApiService.createLpn(json);
			logger.info("LPN created successfully: {}", response);
			
			return response;
		} catch (Exception e) {
			logger.error("Error creating LPN: {}", e.getMessage());
			throw new RuntimeException("Failed to create LPN", e);
		}
	}
	
	/**
	 * Updates an existing LPN via external API
	 * Builds LPN JSON payload with item details and calls UpdateLpn endpoint
	 * 
	 * @param lpn_name LPN name/number
	 * @param item_desc item description
	 * @param length item length
	 * @param width item width
	 * @param height item height
	 * @param quantity quantity
	 * @param adjustQty quantity adjustment
	 * @param lpn_facility_status facility status code
	 * @param volume item volume
	 * @return API response as string
	 */
	@Override
	public String updateLpn(String lpn_name, String item_desc, float length, float width,
			float height, int quantity, int adjustQty, int lpn_facility_status, float volume) {
		try {
			logger.info("Updating LPN: {}", lpn_name);
			
			// Build JSON payload
			JSONObject itemJson = new JSONObject();
			itemJson.put("description", item_desc);
			
			JSONObject lpnJson = new JSONObject();
			lpnJson.put("lpn_name", lpn_name);
			lpnJson.put("item", itemJson);
			lpnJson.put("quantity", quantity);
			lpnJson.put("length", length);
			lpnJson.put("width", width);
			lpnJson.put("height", height);
			lpnJson.put("volume", volume);
			lpnJson.put("lpn_facility_status", lpn_facility_status);
			
			String json = lpnJson.toString();
			logger.info("LPN Update Payload: {}", json);
			
			// Call ExternalApiService - URL resolved from database with placeholder replacement
			String response = externalApiService.updateLpn(lpn_name, adjustQty, json);
			logger.info("LPN updated successfully: {}", response);
			
			return response;
		} catch (Exception e) {
			logger.error("Error updating LPN: {}", e.getMessage());
			throw new RuntimeException("Failed to update LPN", e);
		}
	}
	
	/**
	 * Fetches all LPNs from external API
	 * Calls GetLpns endpoint and parses response to Lpn objects
	 * 
	 * @return iterable collection of LPN entities
	 */
	@Override
	public Iterable<Lpn> getAllLpns() {
		try {
			logger.info("Fetching all LPNs");
			
			// Call ExternalApiService - URL resolved from database
			String json = externalApiService.getLpns();
			logger.info("Response received from API");
			
			// Parse JSON response to Lpn list
			Iterable<Lpn> lpns = objectMapper.readValue(json, new TypeReference<Iterable<Lpn>>() {});
			logger.info("LPNs parsed successfully");
			
			return lpns;
		} catch (Exception e) {
			logger.error("Error fetching LPNs: {}", e.getMessage());
			throw new RuntimeException("Failed to fetch LPNs", e);
		}
	}

	@Override
	public Lpn getLpnById(int id) {
		return ControllerReflectionUtil.toList(getAllLpns()).stream()
				.filter(candidate -> {
					Integer lpnId = ControllerReflectionUtil.extractInt(candidate, "getLpn_id", "getLpnId", "getId");
					return lpnId != null && lpnId == id;
				})
				.findFirst()
				.orElse(null);
	}

	@Override
	public List<Lpn> searchLpns(String lpnNumber) {
		String search = lpnNumber == null ? "" : lpnNumber.trim().toLowerCase();
		if (search.isEmpty()) {
			return List.of();
		}

		Lpn exactMatch = tryFetchByExactLpnName(search);
		if (exactMatch != null) {
			return List.of(exactMatch);
		}

		return ControllerReflectionUtil.toList(getAllLpns()).stream()
				.filter(lpn -> {
					String lpnName = ControllerReflectionUtil.extractString(lpn, "getLpn_name", "getLpnName",
							"getLpnNumber");
					return lpnName != null && lpnName.toLowerCase().contains(search);
				})
				.toList();
	}

	private Lpn tryFetchByExactLpnName(String normalizedLpnName) {
		try {
			String response = externalApiService.validateLpn(normalizedLpnName);
			if (response == null || response.isBlank() || response.contains("\"status\":404")
					|| response.contains("\"status\":500")) {
				return null;
			}

			Lpn lpn = objectMapper.readValue(response, Lpn.class);
			String fetchedName = ControllerReflectionUtil.extractString(lpn, "getLpn_name", "getLpnName", "getLpnNumber");
			if (fetchedName != null && fetchedName.equalsIgnoreCase(normalizedLpnName)) {
				return lpn;
			}
		} catch (Exception ignored) {
			// Non-exact and parse failures safely fall back to list-based search.
		}

		return null;
	}
	
	/**
	 * Validates if an LPN exists via external API
	 * Calls validateLpn endpoint and checks response
	 * 
	 * @param lpn_name LPN name/number to validate
	 * @return true if LPN is valid, false otherwise
	 */
	@Override
	public boolean validateLpn(String lpn_name) {
		try {
			logger.info("Validating LPN: {}", lpn_name);
			
			// Call ExternalApiService - URL resolved from database with placeholder replacement
			String response = externalApiService.validateLpn(lpn_name);
			
			// Parse response to determine validity
			boolean isValid = response != null && !response.isEmpty() && !response.contains("error");
			logger.info("LPN validation result: {}", isValid);
			
			return isValid;
		} catch (Exception e) {
			logger.warn("Error validating LPN: {}", e.getMessage());
			return false;
		}
	}
	
	/**
	 * Moves LPN to reserve location via external API
	 * Calls locateLpnToResv endpoint
	 * 
	 * @param lpn_name LPN name/number
	 * @param reserve_location reserve location code
	 * @return API response
	 */
	@Override
	public String moveLpnToReserve(String lpn_name, String reserve_location) {
		try {
			logger.info("Moving LPN {} to reserve location: {}", lpn_name, reserve_location);
			
			// Call ExternalApiService - URL resolved from database with placeholder replacement
			String response = externalApiService.locateLpnToReserve(lpn_name, reserve_location);
			logger.info("LPN moved to reserve successfully");
			
			return response;
		} catch (Exception e) {
			logger.error("Error moving LPN to reserve: {}", e.getMessage());
			throw new RuntimeException("Failed to move LPN to reserve", e);
		}
	}
	
	/**
	 * Moves LPN to active location via external API
	 * Calls locateLpnToActive endpoint
	 * 
	 * @param lpn_name LPN name/number
	 * @param active_location active location code
	 * @return API response
	 */
	@Override
	public String moveLpnToActive(String lpn_name, String active_location) {
		try {
			logger.info("Moving LPN {} to active location: {}", lpn_name, active_location);
			
			// Call ExternalApiService - URL resolved from database with placeholder replacement
			String response = externalApiService.locateLpnToActive(lpn_name, active_location);
			logger.info("LPN moved to active successfully");
			
			return response;
		} catch (Exception e) {
			logger.error("Error moving LPN to active: {}", e.getMessage());
			throw new RuntimeException("Failed to move LPN to active", e);
		}
	}
	
	/**
	 * Checks active inventory for an LPN via external API
	 * Calls checkActiveInventory endpoint
	 * 
	 * @param lpn_name LPN name/number
	 * @return inventory check result
	 */
	@Override
	public String checkActiveInventory(String lpn_name) {
		try {
			logger.info("Checking active inventory for LPN: {}", lpn_name);
			
			// Note: This method may need a dedicated ExternalApiService method if it's frequently used
			// For now, using generic wrapper
			org.springframework.http.ResponseEntity<String> response = 
				externalApiService.callExternalApi(null, "CheckActiveInventory", 
					org.springframework.http.HttpMethod.GET, null);
			
			logger.info("Active inventory check completed");
			return response.getBody();
		} catch (Exception e) {
			logger.error("Error checking active inventory: {}", e.getMessage());
			throw new RuntimeException("Failed to check active inventory", e);
		}
	}
}
