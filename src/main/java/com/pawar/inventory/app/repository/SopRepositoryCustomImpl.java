package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.CubiscanLog;
import com.pawar.inventory.app.service.base.ExternalApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Grp;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopLocationRangeDto;

@Component
public class SopRepositoryCustomImpl implements SopRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(SopRepositoryCustomImpl.class);

	private final ExternalApiService externalApiService;
	private final ObjectMapper objectMapper;

	public SopRepositoryCustomImpl(ExternalApiService externalApiService, ObjectMapper objectMapper) {
		this.externalApiService = externalApiService;
		this.objectMapper = objectMapper;
	}

	@Override
	public Iterable<Grp> getGrps()
			throws JsonMappingException, JsonProcessingException, ClientProtocolException, IOException {
		ResponseEntity<String> response = externalApiService.callExternalApi(null, AppConstants.MenuEndpoint.GET_GRPS,
				HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<Grp>>() {
		});
	}

	@Override
	public List<SopLocationRangeDto> getLocationRanges() throws ClientProtocolException, IOException {
		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.LOCATION_RANGE, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<SopLocationRangeDto>>() {
		});
	}

	@Override
	public List<SopActionTypeDto> getSopActionTypes() throws ClientProtocolException, IOException {
		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.GET_ACTION_TYPES, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<SopActionTypeDto>>() {
		});
	}

	@Override
	public List<Category> getCategories() throws ClientProtocolException, IOException {
		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.GET_CATEGORIES, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<Category>>() {
		});
	}

	@Override
	public String assignBatch(String actionType, String batchAssign, String category)
			throws ClientProtocolException, IOException {
		JSONObject payload = new JSONObject();
		payload.put("sopActionType", actionType);
		payload.put("batchType", batchAssign);
		payload.put("category", category);

		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.ASSIGN_BATCH, HttpMethod.POST, payload.toString());
		return response.getBody();
	}

	@Override
	public String addLocationRange(String actionType, String category_name, String fromLocation, String toLocation,
			String isActive, String username) throws ClientProtocolException, IOException {
		SopActionTypeDto sopActionTypeDto = getSopActionType(actionType);
		JSONObject sopActionTypeJson = new JSONObject();
		sopActionTypeJson.put("sopActionTypeId", sopActionTypeDto.getSopActionTypeId());

		JSONObject payload = new JSONObject();
		payload.put("sopActionType", sopActionTypeJson);
		payload.put("category", category_name);
		payload.put("fromLocation", fromLocation);
		payload.put("toLocation", toLocation);
		payload.put("isActive", isActive);
		payload.put("createdDttm", LocalDateTime.now());
		payload.put("lastUpdatedDttm", LocalDateTime.now());
		payload.put("createdSource", username);
		payload.put("lastUpdatedSource", username);

		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.LOCATION_RANGE_ADD, HttpMethod.POST, payload.toString());
		return response.getBody();
	}

	@Override
	public List<String> getEligibleUpcsForSop(String category) throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.GET_ELIGIBLE_UPCS_FOR_SOP_BY_CATEGORY)
				.replace("{category}", category);
		ResponseEntity<String> response = externalApiService.callExternalApiWithUrl(null, url, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<String>>() {
		});
	}

	@Override
	public String updateLocationRange(String id, String actionType, String category_name, String fromLocation,
			String toLocation, String isActive, String username) throws ClientProtocolException, IOException {
		SopActionTypeDto sopActionTypeDto = getSopActionType(actionType);
		JSONObject sopActionTypeJson = new JSONObject();
		sopActionTypeJson.put("sopActionTypeId", sopActionTypeDto.getSopActionTypeId());

		JSONObject payload = new JSONObject();
		payload.put("sopActionType", sopActionTypeJson);
		payload.put("category", category_name);
		payload.put("fromLocation", fromLocation);
		payload.put("toLocation", toLocation);
		payload.put("isActive", isActive);
		payload.put("createdDttm", LocalDateTime.now());
		payload.put("lastUpdatedDttm", LocalDateTime.now());
		payload.put("createdSource", username);
		payload.put("lastUpdatedSource", username);

		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.LOCATION_RANGE_UPDATE).replace("{id}", id);
		ResponseEntity<String> response = externalApiService.callExternalApiWithUrl(null, url, HttpMethod.PUT,
				payload.toString());
		return response.getBody();
	}

	@Override
	public void unassignBatch(String sopActionType, String batchAssign, String category_name)
			throws ClientProtocolException, IOException {
		JSONObject payload = new JSONObject();
		payload.put("sopActionType", sopActionType);
		payload.put("batchType", batchAssign);
		payload.put("category", category_name);

		externalApiService.callExternalApi(null, AppConstants.MenuEndpoint.UNASSIGN_BATCH, HttpMethod.POST,
				payload.toString());
	}

	@Override
	public List<CubiscanLog> getCubiscanLogs() {
		try {
			ResponseEntity<String> response = externalApiService.callExternalApi(null,
					AppConstants.MenuEndpoint.GET_CUBISCAN_LOGS, HttpMethod.GET, null);
			String json = response.getBody();
			if (json == null || json.isBlank()) {
				return List.of();
			}
			return objectMapper.readValue(json, new TypeReference<List<CubiscanLog>>() {
			});
		} catch (Exception e) {
			logger.error("Failed to fetch cubiscan logs", e);
			return List.of();
		}
	}

	private SopActionTypeDto getSopActionType(String actionType)
			throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.GET_ACTION_TYPE)
				.replace("{actionType}", actionType);
		ResponseEntity<String> response = externalApiService.callExternalApiWithUrl(null, url, HttpMethod.GET, null);
		String json = response.getBody();
		return objectMapper.readValue(json, SopActionTypeDto.class);
	}
}