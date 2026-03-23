package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.service.base.ExternalApiService;
import com.pawar.inventory.app.util.ControllerReflectionUtil;
import com.pawar.inventory.entity.Inventory;

@Component
public class InventoryRepositoryCustomImpl implements InventoryRepositoryCustom {

	@Autowired
	private ExternalApiService externalApiService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LpnRepositoryCustom lpnRepositoryCustom;

	@Override
	public Iterable<Inventory> getInventories() throws ClientProtocolException, IOException {
		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.GET_INVENTORIES, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<Inventory>>() {
		});
	}

	@Override
	public Inventory getInventoryById(int id) throws ClientProtocolException, IOException {
		return ControllerReflectionUtil.toList(getInventories()).stream()
				.filter(candidate -> {
					Integer inventoryId = ControllerReflectionUtil.extractInt(candidate, "getInventoryId", "getInventory_id",
							"getId");
					return inventoryId != null && inventoryId == id;
				})
				.findFirst()
				.orElse(null);
	}

	@Override
	public List<Inventory> getInventoriesByItemId(int itemId) throws ClientProtocolException, IOException {
		return ControllerReflectionUtil.toList(getInventories()).stream()
				.filter(candidate -> {
					Integer directItemId = ControllerReflectionUtil.extractInt(candidate, "getItem_id", "getItemId");
					if (directItemId != null) {
						return directItemId == itemId;
					}
					Object item = ControllerReflectionUtil.invokeGetter(candidate, "getItem");
					Integer nestedItemId = ControllerReflectionUtil.extractInt(item, "getItem_id", "getItemId", "getId");
					return nestedItemId != null && nestedItemId == itemId;
				})
				.toList();
	}

	@Override
	public List<Inventory> getInventoriesByLocationId(int locationId) throws ClientProtocolException, IOException {
		return ControllerReflectionUtil.toList(getInventories()).stream()
				.filter(candidate -> {
					Integer directLocationId = ControllerReflectionUtil.extractInt(candidate, "getLocation_id", "getLocationId");
					if (directLocationId != null) {
						return directLocationId == locationId;
					}
					Object location = ControllerReflectionUtil.invokeGetter(candidate, "getLocation");
					Integer nestedLocationId = ControllerReflectionUtil.extractInt(location, "getLocn_id", "getLocationId",
							"getId");
					return nestedLocationId != null && nestedLocationId == locationId;
				})
				.toList();
	}

	@Override
	public List<Inventory> getInventoriesByLpnId(int lpnId) throws ClientProtocolException, IOException {
		return loadInventoriesByLpnIdFast(lpnId);
	}

	@Override
	public List<Inventory> searchInventories(Integer itemId, Integer locationId, Integer lpnId)
			throws ClientProtocolException, IOException {
		if (itemId == null && locationId == null && lpnId != null) {
			return loadInventoriesByLpnIdFast(lpnId);
		}

		return ControllerReflectionUtil.toList(getInventories()).stream()
				.filter(candidate -> itemId == null || matchesItemId(candidate, itemId))
				.filter(candidate -> locationId == null || matchesLocationId(candidate, locationId))
				.filter(candidate -> lpnId == null || matchesLpnId(candidate, lpnId))
				.toList();
	}

	private List<Inventory> loadInventoriesByLpnIdFast(int lpnId) throws ClientProtocolException, IOException {
		var lpn = lpnRepositoryCustom.getLpnById(lpnId);
		if (lpn == null) {
			return List.of();
		}

		String lpnName = ControllerReflectionUtil.extractString(lpn, "getLpn_name", "getLpnName", "getLpnNumber");
		if (lpnName == null || lpnName.isBlank()) {
			return List.of();
		}

		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.GET_INVENTORY_BY_LPN)
				.replace("{lpn_name}", lpnName);
		ResponseEntity<String> response = externalApiService.callExternalApiWithUrl(null, url, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank() || json.contains("\"status\":404") || json.contains("\"status\":500")) {
			return List.of();
		}

		Inventory inventory = objectMapper.readValue(json, Inventory.class);
		if (inventory == null) {
			return List.of();
		}

		return List.of(inventory);
	}

	private boolean matchesItemId(Inventory inventory, int itemId) {
		Integer directItemId = ControllerReflectionUtil.extractInt(inventory, "getItem_id", "getItemId");
		if (directItemId != null) {
			return directItemId == itemId;
		}
		Object item = ControllerReflectionUtil.invokeGetter(inventory, "getItem");
		Integer nestedItemId = ControllerReflectionUtil.extractInt(item, "getItem_id", "getItemId", "getId");
		return nestedItemId != null && nestedItemId == itemId;
	}

	private boolean matchesLocationId(Inventory inventory, int locationId) {
		Integer directLocationId = ControllerReflectionUtil.extractInt(inventory, "getLocation_id", "getLocationId");
		if (directLocationId != null) {
			return directLocationId == locationId;
		}
		Object location = ControllerReflectionUtil.invokeGetter(inventory, "getLocation");
		Integer nestedLocationId = ControllerReflectionUtil.extractInt(location, "getLocn_id", "getLocationId", "getId");
		return nestedLocationId != null && nestedLocationId == locationId;
	}

	private boolean matchesLpnId(Inventory inventory, int lpnId) {
		Integer directLpnId = ControllerReflectionUtil.extractInt(inventory, "getLpn_id", "getLpnId");
		if (directLpnId != null) {
			return directLpnId == lpnId;
		}
		Object lpn = ControllerReflectionUtil.invokeGetter(inventory, "getLpn");
		Integer nestedLpnId = ControllerReflectionUtil.extractInt(lpn, "getLpn_id", "getLpnId", "getId");
		return nestedLpnId != null && nestedLpnId == lpnId;
	}
}