package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import org.springframework.http.HttpEntity;
//import org.apache.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;

@Repository
public class MenuRepositoryCustomImp implements MenuRepositoryCustom {

	private final static Logger logger = Logger.getLogger(MenuRepositoryCustomImp.class.getName());
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public MenuRepositoryCustomImp(EntityManager entityManager) {
		httpClient = HttpClients.createDefault();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException {

		boolean isValidLpn = validateLpn(lpn_name);
		logger.info("isValid : " + isValidLpn);
		if (isValidLpn == false) {
			JSONObject jsonObject = new JSONObject();

			// Add key-value pairs to the JSONObject
			jsonObject.put("lpn_name", lpn_name);

			// Create another JSONObject for the nested object
			JSONObject itemObject = new JSONObject();
			itemObject.put("item_name", item_name);

			// Add the nested object to the main JSONObject
			jsonObject.put("item", itemObject);

			// Add the remaining key-value pair to the main JSONObject
			jsonObject.put("quantity", quantity);

			JSONObject lpnObject = new JSONObject();
			lpnObject.put("lpn", jsonObject);
			// Convert the JSONObject to a string
			String json = lpnObject.toString();

			logger.info(json);
			String url = "http://localhost:8085/lpns/create";

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);

			return response;
		} else {

			return "Lpn already exists";
		}

	}

	public boolean validateLpn(String lpn_name) throws ClientProtocolException, IOException {

		boolean flag = false;

		logger.info("Validating LPN : " + lpn_name);
		String url = "http://localhost:8085/lpns/list/by-name/" + lpn_name;

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Lpn : " + json);
		// objectMapper.setSerializationInclusion(Include.NON_NULL);
		// Lpn fetchedLpn = (Lpn) objectMapper.readValue(json, Lpn.class);
		// logger.info("" + fetchedLpn);

		if (json.contains(lpn_name)) {
			flag = true;
			logger.info("Valid Lpn : " + lpn_name + " flag : " + flag);
			return flag;

		} else {
			logger.info("Lpn does not exist : " + lpn_name);
			return flag;
			// throw new LpnNotFoundException("Lpn Not Found ; " + lpn_name);

		}

	}

	@Override
	public Item getItem(String item_name) throws ClientProtocolException, IOException {
		String item_brcode = item_name.replace(" ", "%20");
		logger.info("item_brcode : " + item_brcode);
		String url = "http://localhost:8085/items/list/by-name/" + item_brcode;
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Item : " + json);
		Item fetchedItem = objectMapper.readValue(json, Item.class);

		return fetchedItem;
	}

	@Override
	public Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException {
		String url = "http://localhost:8085/category/list";
		logger.info("URL : " + url);
		logger.info("Fetching Categories");

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Categories : " + json);
		// List<Category> fetchedCategory = objectMapper.readValue(json,
		// Category.class);
		List<Category> fetchedCategory = objectMapper.readValue(json, new TypeReference<List<Category>>() {
		});

		return fetchedCategory;
	}

	@Override
	public String categoryAdd(String category_name) throws ClientProtocolException, IOException {

		boolean isValidCategory = validateCategory(category_name);
		logger.info("isValid : " + isValidCategory);
		if (isValidCategory == false) {
			JSONObject jsonObject = new JSONObject();

			// Add key-value pairs to the JSONObject
			jsonObject.put("category_name", category_name);

			String json = jsonObject.toString();

			logger.info(json);
			String url = "http://localhost:8085/category/add";

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);

			return response;
		} else {

			return "Category already exists";
		}

	}

	public boolean validateCategory(String category_name) throws ClientProtocolException, IOException {
		boolean flag = false;
		logger.info("Validating Category : " + category_name);
		String url = "http://localhost:8085/category/list/by-name/" + category_name;

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Category : " + json);

		if (!json.equals("")) {
			flag = true;
			logger.info("Category already exists : " + category_name + " flag : " + flag);
			return flag;

		} else {
			logger.info("Category does not exist : " + category_name);
			return flag;
		}

	}

	@Override
	public void categoryEdit(String category_name, Category category) {
		logger.info("" + category);
		String url = "http://localhost:8085/category/update/by-name/" + category_name;
		logger.info("URL :" + url);
		JSONObject jsonObject = new JSONObject();

		// Add key-value pairs to the JSONObject
		jsonObject.put("category_name", category.getCategory_name());

		String json = jsonObject.toString();

		logger.info(json);

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
		logger.info("httpEntity : " + httpEntity);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.put(url, httpEntity, String.class);
		//
		// logger.info("Category updated : " + response);
		//
		// return response;

	}

	@Override
	public void categoryDelete(String category_name) throws ClientProtocolException, IOException {

		String url = "http://localhost:8085/category/delete/by-name/" + category_name;
		HttpDelete request = new HttpDelete(url);
		HttpResponse response = httpClient.execute(request);
		// logger.info("response : "+response);
		org.apache.http.HttpEntity entity = response.getEntity();
		// logger.info("entity : "+entity);
		String json = EntityUtils.toString(entity);
		logger.info("json : " + json);

	}

	@Override
	public void deleteCategory(int category_id) throws ClientProtocolException, IOException {
		String url = "http://localhost:8085/category/delete/by-id/" + category_id;
		HttpDelete request = new HttpDelete(url);
		HttpResponse response = httpClient.execute(request);
		// logger.info("response : "+response);
		org.apache.http.HttpEntity entity = response.getEntity();
		// logger.info("entity : "+entity);
		String json = EntityUtils.toString(entity);
		logger.info("json : " + json);

	}

	@Override
	public Iterable<Item> getItems() throws ClientProtocolException, IOException {
		String url = "http://localhost:8085/items/list";
		logger.info("URL : " + url);
		logger.info("Fetching Items");

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Items : " + json);
		// List<Category> fetchedCategory = objectMapper.readValue(json,
		// Category.class);
		List<Item> fetchedItem = objectMapper.readValue(json, new TypeReference<List<Item>>() {
		});

		return fetchedItem;
	}

	@Override
	public String itemAdd(String description, String category_name, float length, float width, float height)
			throws ClientProtocolException, IOException {
		boolean isValidItem = validateItem(description);
		logger.info("isValid : " + isValidItem);
		if (isValidItem == false) {
			JSONObject item = new JSONObject();
			JSONObject item_Json = new JSONObject();
			JSONObject category_Json = new JSONObject();

			category_Json.put("category_name", category_name);
			// category_Json.put("category", category_name_Json);
			item_Json.put("description", description);
			item_Json.put("unit_length", length);
			item_Json.put("unit_width", width);
			item_Json.put("unit_height", height);
			item_Json.put("category", category_Json);
			item.put("item", item_Json);
			String json = item.toString();
			logger.info("Item Payload : " + json);
			String url = "http://localhost:8085/items/add";

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);

			return response;
		} else {

			return "Item already exists";
		}

	}

	public boolean validateItem(String item) throws ClientProtocolException, IOException {
		String itemName = "";
		logger.info("Item : " + item);
		if (item.contains(" ")) {
			itemName = item.replaceAll(" ", "%20");
			logger.info("Item with %20: " + itemName);

		}
		boolean flag = false;
		logger.info("Validating Item : " + itemName);
		String url = "http://localhost:8085/items/list/by-name/" + itemName;

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Item Category : " + json);

		if (!json.equals("")) {
			flag = true;
			logger.info("Item already exists : " + itemName + " flag : " + flag);
			return flag;

		} else {
			logger.info("Item does not exist : " + itemName);
			return flag;
		}
	}

	@Override
	public void deleteItem(String description) throws ClientProtocolException, IOException {
		boolean isValidItem = validateItem(description);
		String desc = "";
		if (isValidItem) {
			String url = "http://localhost:8085/items/delete/by-name/" + desc;
			HttpDelete request = new HttpDelete(url);
			HttpResponse response = httpClient.execute(request);
			// logger.info("response : "+response);
			org.apache.http.HttpEntity entity = response.getEntity();
			// logger.info("entity : "+entity);
			String json = EntityUtils.toString(entity);
			logger.info("json : " + json);
		} else {
			logger.info("Item does not exists : " + description);
		}

	}

	@Override
	public String itemEdit(String description, String category_name, float length, float width, float height)
			throws ClientProtocolException, IOException {
		boolean isValidItem = validateItem(description);
		String newdescription = "";
		logger.info("Item : " + description);
		if (description.contains(" ")) {
			newdescription = description.replaceAll(" ", "%20");
			logger.info("Item with %20: " + description);

		}
		logger.info("isValid : " + isValidItem);
		if (isValidItem) {
			JSONObject item = new JSONObject();
			JSONObject item_Json = new JSONObject();
			JSONObject category_Json = new JSONObject();

			category_Json.put("category_name", category_name);
			// category_Json.put("category", category_name_Json);
			item_Json.put("description", description);
			item_Json.put("unit_length", length);
			item_Json.put("unit_width", width);
			item_Json.put("unit_height", height);
			item_Json.put("category", category_Json);
			item.put("item", item_Json);
			String json = item.toString();
			logger.info("Item Payload : " + json);
			String url = "http://localhost:8085/items/update/by-name/" + newdescription;

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);

			return response;
		} else {

			return "Item already exists";
		}

	}

	@Override
	public Iterable<Location> getLocations() throws ClientProtocolException, IOException {
		String url = "http://localhost:8085/locations/list";
		logger.info("URL : " + url);
		logger.info("Fetching Locations");

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Locations : " + json);
		// List<Category> fetchedCategory = objectMapper.readValue(json,
		// Category.class);
		List<Location> fetchedLocation = objectMapper.readValue(json, new TypeReference<List<Location>>() {
		});

		return fetchedLocation;
	}

	@Override
	public String locationAdd(String locn_brcd, String locn_class, float length, float width, float height,
			float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException {
		boolean isValidLocation = validateLocation(locn_brcd);
		logger.info("isValid : " + isValidLocation);
		if (isValidLocation == false) {
			JSONObject location = new JSONObject();
			JSONObject location_Json = new JSONObject();

			location_Json.put("locn_brcd", locn_brcd);
			location_Json.put("locn_class", locn_class);
			location_Json.put("length", length);
			location_Json.put("width", width);
			location_Json.put("height", height);
			location_Json.put("max_volume", max_volume);
			location_Json.put("max_qty", max_qty);
			location_Json.put("max_weight", max_weight);
			location.put("location", location_Json);
			String json = location.toString();
			logger.info("Location Payload : " + json);
			String url = "http://localhost:8085/locations/add";

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);

			return response;
		} else {

			return "Location already exists";
		}

	}

	public boolean validateLocation(String locn_barcode) throws ClientProtocolException, IOException {
		logger.info("Location : " + locn_barcode);
		boolean flag = false;
		logger.info("Validating Location : " + locn_barcode);
		String url = "http://localhost:8085/locations/list/by-name/" + locn_barcode;
		logger.info("URL : "+url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Location : " + json);

		if (!json.equals("")) {
			flag = true;
			logger.info("Location already exists : " + locn_barcode + " flag : " + flag);
			return flag;

		} else {
			logger.info("Location does not exist : " + locn_barcode);
			return flag;
		}
	}

	@Override
	public String locationEdit(String locn_brcd, String locn_class, float length, float width, float height,
			float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException {
		boolean isValidLocation = validateLocation(locn_brcd);
		logger.info("Location : " + locn_brcd);
		logger.info("isValid : " + isValidLocation);
		if (isValidLocation) {
			JSONObject location = new JSONObject();
			JSONObject location_Json = new JSONObject();

			location_Json.put("locn_brcd", locn_brcd);
			location_Json.put("length", length);
			location_Json.put("width", width);
			location_Json.put("height", height);
			location_Json.put("max_volume", max_volume);
			location_Json.put("max_qty", max_qty);
			location_Json.put("max_weight", max_weight);
			location.put("location", location_Json);
			String json = location.toString();
			logger.info("Location Payload : " + json);
			String url = "http://localhost:8085/locations/update/by-name/" + locn_brcd;

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);

			return response;
		} else {

			return "Location already exists";
		}

	}

	@Override
	public void deleteLocation(String locn_brcd) throws ClientProtocolException, IOException {

		boolean isValidLocation = validateLocation(locn_brcd);
		logger.info("Location : " + locn_brcd);
		logger.info("isValid : " + isValidLocation);

		if (isValidLocation) {
			String url = "http://localhost:8085/locations/delete/by-name/" + locn_brcd;
			HttpDelete request = new HttpDelete(url);
			HttpResponse response = httpClient.execute(request);
			// logger.info("response : "+response);
			org.apache.http.HttpEntity entity = response.getEntity();
			// logger.info("entity : "+entity);
			String json = EntityUtils.toString(entity);
			logger.info("json : " + json);
		} else {
			logger.info("Location does not exists : " + locn_brcd);
		}

	}

}
