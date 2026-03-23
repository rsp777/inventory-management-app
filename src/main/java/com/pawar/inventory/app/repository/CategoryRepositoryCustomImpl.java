package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.service.base.ExternalApiService;
import com.pawar.inventory.entity.Category;

@Component
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

	@Autowired
	private ExternalApiService externalApiService;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException {
		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.GET_ALL_CATEGORIES, HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		return objectMapper.readValue(json, new TypeReference<List<Category>>() {
		});
	}

	@Override
	public String categoryAdd(String category_name) throws ClientProtocolException, IOException {
		JSONObject payload = new JSONObject();
		payload.put("category_name", category_name);
		ResponseEntity<String> response = externalApiService.callExternalApi(null,
				AppConstants.MenuEndpoint.ADD_CATEGORY, HttpMethod.POST, payload.toString());
		return response.getBody();
	}

	@Override
	public void categoryEdit(String category_name, String updatedCategoryName)
			throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.EDIT_CATEGORY)
				.replace("{category_name}", category_name);
		JSONObject payload = new JSONObject();
		payload.put("category_name", updatedCategoryName);
		externalApiService.callExternalApiWithUrl(null, url, HttpMethod.PUT, payload.toString());
	}

	@Override
	public void categoryDelete(String category_name) throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.DELETE_CATEGORY)
				.replace("{category_name}", category_name);
		externalApiService.callExternalApiWithUrl(null, url, HttpMethod.DELETE, null);
	}

	@Override
	public void deleteCategory(int category_id) throws ClientProtocolException, IOException {
		String url = externalApiService.getApiUrl(AppConstants.MenuEndpoint.DELETE_CATEGORY_BY_ID)
				.replace("{category_id}", String.valueOf(category_id));
		externalApiService.callExternalApiWithUrl(null, url, HttpMethod.DELETE, null);
	}
}
