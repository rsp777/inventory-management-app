package com.pawar.inventory.app.repository;

import java.util.List;

import com.pawar.inventory.entity.Lpn;

/**
 * Custom repository interface for LPN (License Plate Number) operations.
 * 
 * Consolidates all LPN-specific database and external API operations.
 * Uses ExternalApiService for HTTP calls with database-driven URLs.
 */
public interface LpnRepositoryCustom {
	
	/**
	 * Creates a new LPN via external API
	 * 
	 * @param lpn_name LPN name/number
	 * @param item_name item name associated with LPN
	 * @param quantity quantity of items
	 * @return API response
	 */
	String createLpn(String lpn_name, String item_name, int quantity);
	
	/**
	 * Updates an existing LPN via external API
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
	 * @return API response
	 */
	String updateLpn(String lpn_name, String item_desc, float length, float width, 
			float height, int quantity, int adjustQty, int lpn_facility_status, float volume);
	
	/**
	 * Fetches all LPNs from external API
	 * 
	 * @return list of LPNs
	 */
	Iterable<Lpn> getAllLpns();

	/**
	 * Fetch one LPN by its identifier from current LPN collection.
	 *
	 * @param id LPN id
	 * @return matched LPN or null if none found
	 */
	Lpn getLpnById(int id);

	/**
	 * Search LPNs by LPN name/number.
	 *
	 * @param lpnNumber partial or full lpn string
	 * @return matched LPN list
	 */
	List<Lpn> searchLpns(String lpnNumber);
	
	/**
	 * Validates if an LPN exists via external API
	 * 
	 * @param lpn_name LPN name/number
	 * @return validation result
	 */
	boolean validateLpn(String lpn_name);
	
	/**
	 * Moves LPN to reserve location via external API
	 * 
	 * @param lpn_name LPN name/number
	 * @param reserve_location reserve location code
	 * @return API response
	 */
	String moveLpnToReserve(String lpn_name, String reserve_location);
	
	/**
	 * Moves LPN to active location via external API
	 * 
	 * @param lpn_name LPN name/number
	 * @param active_location active location code
	 * @return API response
	 */
	String moveLpnToActive(String lpn_name, String active_location);
	
	/**
	 * Checks active inventory for an LPN via external API
	 * 
	 * @param lpn_name LPN name/number
	 * @return inventory check result
	 */
	String checkActiveInventory(String lpn_name);
}
