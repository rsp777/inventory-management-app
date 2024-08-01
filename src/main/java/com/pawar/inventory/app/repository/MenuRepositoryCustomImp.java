package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONObject;

import org.springframework.http.HttpEntity;
//import org.apache.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.Lpn;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
//import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

@Repository
public class MenuRepositoryCustomImp implements MenuRepositoryCustom {

	private final static Logger logger = Logger.getLogger(MenuRepositoryCustomImp.class.getName());
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;
	private EntityManager entityManager;
	private MenuService menuService;

	public MenuRepositoryCustomImp(EntityManager entityManager, MenuService menuService) {
		httpClient = HttpClients.createDefault();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		this.entityManager = entityManager;
		this.menuService = menuService;
	}

	@Override
	public String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException {

		// boolean isValidLpn = validateLpn(lpn_name);
		logger.info("new lpn : " + lpn_name);
		// if (isValidLpn == false) {
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

		logger.info(" Lpn Payload : " + json);
		String url = "http://localhost:8085/lpns/create";

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.postForObject(url, httpEntity, String.class);

		logger.info("Response : " + response);

		return response;
		// }
		// else {

		// return "Lpn already exists";
		// }

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
		// logger.info("Fetched Categories : " + json);
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

			// logger.info("Response : " + response);

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
		logger.info("Validating Item : " + item);
		String url = "http://localhost:8085/items/list/by-name/" + itemName;
		logger.info("URL : " + url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		logger.info("response : " + response);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Item Json : " + json);

		if (!json.equals("") && !json.contains("404")) {
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
		if (description.contains(" ")) {
			desc = description.replaceAll(" ", "%20");
			logger.info("Item with %20: " + desc);

		}
		if (isValidItem) {
			String url = "http://localhost:8085/items/delete/by-name/" + desc;
			logger.info("url : " + url);
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
	public ResponseEntity<String> itemEdit(String description, String category_name, float length, float width,
			float height) throws ClientProtocolException, IOException {
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
			logger.info("URL : " + url);
			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			logger.info("json : " + httpEntity.getBody());
			RestTemplate restTemplate = new RestTemplate();
			// String response = restTemplate.postForObject(url, httpEntity, String.class);
			// String response = restTemplate.postForObject(url, httpEntity, String.class);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class,
					httpEntity.getHeaders());

			logger.info("Response : " + response);

			return response;
		} else {

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
		logger.info("URL : " + url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Location : " + json);
		logger.info("!json.contains(\"500\") : " + !json.contains(":500"));
		if (!json.equals("") && !json.contains("\"status\":500")) {
			flag = true;
			logger.info("Location already exists : " + locn_barcode + " flag : " + flag);
			return flag;

		} else {
			logger.info("Location does not exist : " + locn_barcode);
			return flag;
		}
	}

	@Override
	public ResponseEntity<String> locationEdit(String locn_brcd, String locn_class, float length, float width,
			float height, float max_volume, float max_qty, float max_weight)
			throws ClientProtocolException, IOException {
		boolean isValidLocation = validateLocation(locn_brcd);
		logger.info("Location : " + locn_brcd);
		logger.info("isValid : " + isValidLocation);
		if (isValidLocation) {
			// JSONObject location = new JSONObject();
			JSONObject location_Json = new JSONObject();

			location_Json.put("locn_brcd", locn_brcd);
			location_Json.put("length", length);
			location_Json.put("width", width);
			location_Json.put("height", height);
			location_Json.put("locn_class", locn_class);
			location_Json.put("max_volume", max_volume);
			location_Json.put("max_qty", max_qty);
			location_Json.put("max_weight", max_weight);
			// location.put("location", location_Json);
			String json = location_Json.toString();
			logger.info("Location Payload : " + json);
			String url = "http://localhost:8085/locations/update/by-name/" + locn_brcd;

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			logger.info("json : " + httpEntity.getBody());
			RestTemplate restTemplate = new RestTemplate();
			;
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class,
					httpEntity.getHeaders());
			logger.info("Response : " + response);
			return response;
		} else {

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

	@Override
	public Iterable<Lpn> getLpns() throws ClientProtocolException, IOException {
		String url = "http://localhost:8085/lpns/list";
		logger.info("URL : " + url);
		logger.info("Fetching Lpns");

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Lpns : " + json);
		// List<Category> fetchedCategory = objectMapper.readValue(json,
		// Category.class);
		List<Lpn> fetchedLpn = objectMapper.readValue(json, new TypeReference<List<Lpn>>() {
		});

		return fetchedLpn;
	}

	@Override
	public String lpnEdit(String lpn_name, String item_desc, float length, float width, float height, int quantity,
			int adjustQty, int lpn_facility_status, float volume) throws ClientProtocolException, IOException {
		Item fetchedItem = getItem(item_desc);
		Category fetchedCategory = fetchedItem.getCategory();
		// boolean isValidLpn = validateLpn(lpn_name);
		logger.info("lpn : " + lpn_name);
		// logger.info("isValid : " + isValidLpn);
		logger.info("Quantity to be adjusted : " + adjustQty);
		// if (isValidLpn) {
		// JSONObject location = new JSONObject();
		JSONObject lpn_Json = new JSONObject();
		JSONObject item = new JSONObject();
		JSONObject category_Json = new JSONObject();
		JSONObject item_Json = new JSONObject();

		category_Json.put("category_name", fetchedItem.getCategory().getCategory_name());
		item_Json.put("category", category_Json);
		item_Json.put("description", fetchedItem.getDescription());
		item_Json.put("unit_length", fetchedItem.getUnit_length());
		item_Json.put("unit_width", fetchedItem.getUnit_width());
		item_Json.put("unit_height", fetchedItem.getUnit_height());
		item_Json.put("category", category_Json);
		// item.put("item", item_Json);

		lpn_Json.put("lpn_name", lpn_name);
		lpn_Json.put("item", item_Json);
		lpn_Json.put("quantity", quantity);
		lpn_Json.put("length", length);
		lpn_Json.put("width", width);
		lpn_Json.put("height", height);
		lpn_Json.put("volume", volume);
		lpn_Json.put("lpn_facility_status", lpn_facility_status);

		// location.put("location", location_Json);
		String json = lpn_Json.toString();
		logger.info("Lpn Payload : " + json);
		String url = "http://localhost:8085/lpns/update/by-name/" + lpn_name + "/" + adjustQty;

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpPut httpPut = new HttpPut(url);
		StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		httpPut.setEntity(entity);
		// HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);

		httpClient.execute(httpPut);
		// logger.info("entity : "+entity);
		String json2 = EntityUtils.toString(entity);
		// RestTemplate restTemplate = new RestTemplate();
		// String response2 = restTemplate.postForObject(url, httpEntity, String.class);
		// String response = restTemplate.exchange(url, null, httpEntity, String.class);
		Lpn updatedLpn = objectMapper.readValue(json2, new TypeReference<Lpn>() {
		});
		logger.info("Response : " + updatedLpn);

		return "Response : " + updatedLpn;
		// } else {

		// return "Lpn already exists";
		// }

	}

	@Override
	public Iterable<Inventory> getInventories() throws ClientProtocolException, IOException {
		String url = "http://localhost:8085/inventory/list";
		logger.info("URL : " + url);
		logger.info("Fetching Inventories");

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Inventories : " + json);
		// List<Category> fetchedCategory = objectMapper.readValue(json,
		// Category.class);
		List<Inventory> fetchedInventory = objectMapper.readValue(json, new TypeReference<List<Inventory>>() {
		});

		return fetchedInventory;
	}

	public Inventory getInventoryByLpn(String lpn_name) throws ClientProtocolException, IOException {

		String url = "http://localhost:8085/inventory/list/by-lpn/" + lpn_name;
		logger.info("URL : " + url);
		logger.info("Fetching Inventory By Lpn");

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Inventory By Lpn : " + json);
		// List<Category> fetchedCategory = objectMapper.readValue(json,
		// Category.class);
		Inventory fetchedInventory = objectMapper.readValue(json, new TypeReference<Inventory>() {
		});

		return fetchedInventory;

	}

	@Override
	public String locateLpnToResv(String lpn_name, String resv_locn) throws ClientProtocolException, IOException {

		boolean isValidLpn = validateLpn(lpn_name);
		boolean isValidLocation = validateLocation(resv_locn);
		String response = "Invalid LPN " + lpn_name + " and Location" + resv_locn;
		if ((isValidLocation == true && isValidLpn == true)) {

			Inventory fetchedInventoryByLpn = getInventoryByLpn(lpn_name);
			logger.info(" Fetched Inventory By Lpn " + fetchedInventoryByLpn);
			JSONObject inventory_json = new JSONObject();
			JSONObject inventory = new JSONObject();
			JSONObject lpn = new JSONObject();
			JSONObject location = new JSONObject();

			location.put("locn_brcd", resv_locn);
			// category_Json.put("category", category_name_Json);
			lpn.put("lpn_name", lpn_name);
			inventory.put("lpn", lpn);
			inventory.put("location", location);
			inventory_json.put("inventory", inventory);
			logger.info("" + inventory_json);

			String json = inventory_json.toString();
			logger.info("Reserve Inventory Payload : " + json);
			String url = "http://localhost:8085/inventory/createReserve";

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);
			return response;
		}
		return response;

	}

	@Override
	public String locateLpnToActive(String lpn_name, String active_locn) throws ClientProtocolException, IOException {
		String response = "";
		boolean isValidLpn = validateLpn(lpn_name);
		boolean isValidLocation = validateLocation(active_locn);
		logger.info("isValidLpn : " + isValidLpn);
		logger.info("isValidLocation : " + isValidLocation);
		logger.info(
				"isValidLocation == true && isValidLpn == true : " + (isValidLocation == true && isValidLpn == true));
		if ((isValidLocation == true && isValidLpn == true)) {

			Inventory fetchedInventoryByLpn = getInventoryByLpn(lpn_name);
			logger.info(" Fetched Inventory By Lpn " + fetchedInventoryByLpn);
			JSONObject inventory_json = new JSONObject();
			JSONObject inventory = new JSONObject();
			JSONObject lpn = new JSONObject();
			JSONObject location = new JSONObject();

			location.put("locn_brcd", active_locn);
			// category_Json.put("category", category_name_Json);
			lpn.put("lpn_name", lpn_name);
			inventory.put("lpn", lpn);
			inventory.put("location", location);
			inventory_json.put("inventory", inventory);
			logger.info("" + inventory_json);

			String json = inventory_json.toString();
			logger.info("Reserve Inventory Payload : " + json);
			String url = "http://localhost:8085/inventory/createActive";

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);
			return response;
		}
		return response;
	}

	@Override
	public String signIn(String user_name, String password) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		String url = getUrl("Login");
		logger.info("URL : " + url);
		JSONObject user_json = new JSONObject();
		user_json.put("username", user_name);
		user_json.put("passwordHash", password);
		String user_payload = user_json.toString();
		HttpEntity<String> httpEntity = new HttpEntity<String>(user_payload, httpHeaders);
		RestTemplate restTemplate = new RestTemplate();

		String response = restTemplate.postForObject(url, httpEntity, String.class);

		logger.info("Response : " + response);
		return response;
	}

	@Override
	public String signout(HttpSession httpSession) throws ClientProtocolException, IOException {
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		// HttpHeaders httpHeaders = new HttpHeaders();
		// httpHeaders.set("Authorization", decodedToken);
		// httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		String url = getUrl("Signout");
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Authorization", decodedToken);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		// String response = httpResponse.get;
		logger.info("httpResponse : " + httpResponse);
		return "Logged out";
	}

	@Override
	public Menu addMenu(Menu newMenu) {
		Session currentSession = entityManager.unwrap(Session.class);
		// Query<Menu> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS
		// = 1", Menu.class);
		logger.info("Menu " + newMenu);
		logger.info("Parent Menu : "+newMenu.getParent());
		// query.executeUpdate();
		newMenu.setCreatedDttm(LocalDateTime.now());
		newMenu.setLastUpdatedDttm(LocalDateTime.now());
		newMenu.setCreatedSource("System");
		newMenu.setLastUpdatedSource("System");
		currentSession.saveOrUpdate(newMenu);
		return newMenu;
	}

	@Override
	public void updateMenu(Menu updatedMenu) {
		if (updatedMenu != null) {
			Session currentSession = entityManager.unwrap(Session.class);

			Menu fetchedMenu = menuService.getMenu(updatedMenu.getMenu_name());
			// Query<Menu> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS
			// = 1", Menu.class);
			logger.info("Fetched Menu" + fetchedMenu);
			// query.executeUpdate();
			if (fetchedMenu != null) {
				fetchedMenu.setProtocol(updatedMenu.getProtocol());
				fetchedMenu.setMenu_name(updatedMenu.getMenu_name());
				fetchedMenu.setHostname(updatedMenu.getHostname());
				fetchedMenu.setMenu_link(updatedMenu.getMenu_link());
				fetchedMenu.setMenu_type(updatedMenu.getMenu_type());
				fetchedMenu.setCreatedDttm(LocalDateTime.now());
				fetchedMenu.setLastUpdatedDttm(LocalDateTime.now());
				fetchedMenu.setCreatedSource("System");
				fetchedMenu.setLastUpdatedSource("System");
				currentSession.merge(fetchedMenu);

				logger.info("Updated Menu" + fetchedMenu);
			}
		} else {
			logger.info("Menu is null : " + updatedMenu);
		}
	}

	public Menu getMenu(String menuName) {
		logger.info("Menu Name : " + menuName);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Menu> query = currentSession.createQuery("from Menu where menuName = :menuName", Menu.class);
		query.setParameter("menuName", menuName);
		try {
			Menu menu = query.getSingleResult();
			return menu;
		} catch (NoResultException e) {
			return null;
		}
	
	}

	public String getUrl(String menuName) {
		String protocol = "";
		String hostname = "";
		String menuLink = "";
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Object[]> query = currentSession.createNativeQuery(
				"select protocol,hostname,menu_link from menu where menu_name = :menu_name", Object[].class);
		query.setParameter("menu_name", menuName);
		List<Object[]> result = query.getResultList();
		logger.info("" + result.toString());
		List<Object[]> results = query.getResultList();
		for (Object[] row : results) {
			protocol = (String) row[0];
			hostname = (String) row[1];
			menuLink = (String) row[2];
			// Process the data as needed
		}

		String regex = "%s%s%s";
		String url = String.format(regex, protocol, hostname, menuLink);
		return url;
	}

	@Override
	public String userAdd(String firstname, String middlename, String lastname, String username, String password,
			String email) {
//		boolean isValidItem = validateItem(description);
//		logger.info("isValid : " + isValidItem);
		
			JSONObject user = new JSONObject();
			

			// category_Json.put("category", category_name_Json);
			user.put("username", username);
			user.put("email", email);
			user.put("passwordHash", password);
			user.put("firstName", firstname);
			user.put("middleName", middlename);
			user.put("lastName", lastname);
//			item.put("item", item_Json);
			String json = user.toString();
			logger.info("User Payload : " + json);
			String url = getUrl("Register");

			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.postForObject(url, httpEntity, String.class);

			logger.info("Response : " + response);

			return response;
		

	}
}
