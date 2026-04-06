package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.service.base.ExternalApiService;
import com.pawar.inventory.app.util.ControllerReflectionUtil;
import com.pawar.inventory.entity.Location;

@Component
public class LocationRepositoryCustomImpl implements LocationRepositoryCustom {

	private final ExternalApiService externalApiService;
	private final ObjectMapper objectMapper;

	public LocationRepositoryCustomImpl(ExternalApiService externalApiService, ObjectMapper objectMapper) {
		this.externalApiService = externalApiService;
		this.objectMapper = objectMapper;
	}

	@Override
	public Iterable<Location> getLocations() throws ClientProtocolException, IOException {
		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.GET_LOCATIONS, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<Location>>() {
		});
	}

	@Override
	public Location getLocationById(int id) throws ClientProtocolException, IOException {
		return ControllerReflectionUtil.toList(getLocations()).stream()
				.filter(candidate -> {
					Integer locId = ControllerReflectionUtil.extractInt(candidate, "getLocn_id", "getLocationId", "getId");
					return locId != null && locId == id;
				})
				.findFirst()
				.orElse(null);
	}

	@Override
	public List<Location> searchLocationsByCode(String code) throws ClientProtocolException, IOException {
		if (code == null || code.trim().isEmpty()) {
			return List.of();
		}

		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.GET_LOCATION_BY_BARCODE)
				.replace("{locn_brcd}", code.trim());
		ResponseEntity<String> response = externalApiService.callExternalApiWithUrl(null, url, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank() || json.contains(AppConstants.ExternalApiResponse.STATUS_NOT_FOUND) || json.contains(AppConstants.ExternalApiResponse.STATUS_SERVER_ERROR)) {
			return List.of();
		}
		Location location = objectMapper.readValue(json, Location.class);
		if (location == null) {
			return List.of();
		}

		return List.of(location);
	}

	@Override
	public String locationAdd(String locn_brcd, String grp, String locn_class, float length, float width, float height,
			float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException {
		JSONObject payload = new JSONObject();
		JSONObject locationJson = new JSONObject();

		locationJson.put("locn_brcd", locn_brcd);
		locationJson.put("grp", grp);
		locationJson.put("locn_class", locn_class);
		locationJson.put("length", length);
		locationJson.put("width", width);
		locationJson.put("height", height);
		locationJson.put("max_volume", max_volume);
		locationJson.put("max_qty", max_qty);
		locationJson.put("max_weight", max_weight);
		payload.put("location", locationJson);

		ResponseEntity<String> response = externalApiService.callExternalApi(null, AppConstants.MenuEndpoint.ADD_LOCATION,
				HttpMethod.POST, payload.toString());
		return response.getBody();
	}

	@Override
	public ResponseEntity<String> locationEdit(String locn_brcd, String grp, String locn_class, float length,
			float width, float height, float max_volume, float max_qty, float max_weight)
			throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.UPDATE_LOCATION_BY_BARCODE)
				.replace("{locn_brcd}", locn_brcd);

		JSONObject locationJson = new JSONObject();
		locationJson.put("locn_brcd", locn_brcd);
		locationJson.put("grp", grp);
		locationJson.put("locn_class", locn_class);
		locationJson.put("length", length);
		locationJson.put("width", width);
		locationJson.put("height", height);
		locationJson.put("max_volume", max_volume);
		locationJson.put("max_qty", max_qty);
		locationJson.put("max_weight", max_weight);

		return externalApiService.callExternalApiWithUrl(null, url, HttpMethod.PUT, locationJson.toString());
	}

	@Override
	public void deleteLocation(String locn_brcd) throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.DELETE_LOCATION_BY_BARCODE)
				.replace("{locn_brcd}", locn_brcd);
		externalApiService.callExternalApiWithUrl(null, url, HttpMethod.DELETE, null);
	}
}
