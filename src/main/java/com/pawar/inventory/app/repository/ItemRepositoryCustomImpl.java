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
import com.pawar.inventory.entity.Item;

@Component
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

	private final ExternalApiService externalApiService;
	private final ObjectMapper objectMapper;

	public ItemRepositoryCustomImpl(ExternalApiService externalApiService, ObjectMapper objectMapper) {
		this.externalApiService = externalApiService;
		this.objectMapper = objectMapper;
	}

	@Override
	public Item getItem(String item_name) throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.GET_ITEM)
				.replace("{itemName}", item_name);
		ResponseEntity<String> response = externalApiService.callExternalApiWithUrl(null, url, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank() || json.contains(AppConstants.ExternalApiResponse.STATUS_NOT_FOUND) || json.contains(AppConstants.ExternalApiResponse.STATUS_SERVER_ERROR)) {
			return null;
		}
		return objectMapper.readValue(json, Item.class);
	}

	@Override
	public Iterable<Item> getItems() throws ClientProtocolException, IOException {
		ResponseEntity<String> response = externalApiService.callExternalApi(null, AppConstants.MenuEndpoint.GET_ITEMS,
				HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<Item>>() {
		});
	}

	@Override
	public String itemAdd(String description, String category, float length, float width, float height)
			throws ClientProtocolException, IOException {
		JSONObject payload = new JSONObject();
		JSONObject itemJson = new JSONObject();
		JSONObject categoryJson = new JSONObject();

		categoryJson.put("category_name", category);
		itemJson.put("description", description);
		itemJson.put("unit_length", length);
		itemJson.put("unit_width", width);
		itemJson.put("unit_height", height);
		itemJson.put("category", categoryJson);
		payload.put("item", itemJson);

		ResponseEntity<String> response = externalApiService.callExternalApi(null, AppConstants.MenuEndpoint.ADD_ITEM,
				HttpMethod.POST, payload.toString());
		return response.getBody();
	}

	@Override
	public void deleteItem(int item_id) throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.DELETE_ITEM_BY_ID)
				.replace("{itemId}", String.valueOf(item_id));
		externalApiService.callExternalApiWithUrl(null, url, HttpMethod.DELETE, null);
	}

	@Override
	public ResponseEntity<String> itemEdit(int itemId, String description, String category, float length, float width,
			float height) throws ClientProtocolException, IOException {
		JSONObject payload = new JSONObject();
		JSONObject itemJson = new JSONObject();
		JSONObject categoryJson = new JSONObject();

		categoryJson.put("category_name", category);
		itemJson.put("description", description);
		itemJson.put("unit_length", length);
		itemJson.put("unit_width", width);
		itemJson.put("unit_height", height);
		itemJson.put("category", categoryJson);
		payload.put("item", itemJson);

		return externalApiService.callExternalApi(null, AppConstants.MenuEndpoint.UPDATE_ITEM, HttpMethod.PUT,
				payload.toString());
	}
}
