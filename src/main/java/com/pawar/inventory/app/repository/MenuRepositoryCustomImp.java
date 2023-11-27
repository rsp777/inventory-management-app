package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import org.springframework.http.HttpEntity;
//import org.apache.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
//import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.model.Lpn;

import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;

@Repository
public class MenuRepositoryCustomImp implements MenuRepositoryCustom {

	private final static Logger logger = Logger.getLogger(MenuRepositoryCustomImp.class.getName());


	private EntityManager entityManager;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public MenuRepositoryCustomImp(EntityManager entityManager) {
		this.entityManager = entityManager;
		httpClient = HttpClients.createDefault();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException {
		
		boolean isValidLpn = validateLpn(lpn_name);
		logger.info("isValid : "+isValidLpn);
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
		} 
		else {
			
			return "Lpn already exists";
		}
		
	}

	public boolean validateLpn(String lpn_name) throws ClientProtocolException, IOException {

		boolean flag = false;

		logger.info("Validating LPN : " + lpn_name);
		String url = "http://localhost:8085/lpns/list/by-name/"+lpn_name;

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Lpn : "+json);
//		objectMapper.setSerializationInclusion(Include.NON_NULL);
//		Lpn fetchedLpn = (Lpn) objectMapper.readValue(json, Lpn.class);
//		logger.info("" + fetchedLpn);

		if (json.contains(lpn_name)) {
			flag = true;
			logger.info("Valid Lpn : "+lpn_name+" flag : "+flag);
			return flag;
			
		} 
		else {
			logger.info("Lpn does not exist : " + lpn_name);
			return flag;
//			throw new LpnNotFoundException("Lpn Not Found ; " + lpn_name);
			
		}

	}

	@Override
	public Item getItem(String item_name) throws ClientProtocolException, IOException {
		String item_brcode = item_name.replace(" ","%20");
		logger.info("item_brcode : "+item_brcode);
		String url = "http://localhost:8085/items/list/by-name/"+item_brcode;
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Item : "+json);
		Item fetchedItem = objectMapper.readValue(json, Item.class);
		
		return fetchedItem;
	}

	@Override
	public Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException {
		String url = "http://localhost:8085/category/list";
		logger.info("URL : "+url);
		logger.info("Fetching Categories");
		       
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Categories : "+json);
//		List<Category> fetchedCategory = objectMapper.readValue(json, Category.class);
		List<Category> fetchedCategory = objectMapper.readValue(json, new TypeReference<List<Category>>(){});

		
		return fetchedCategory;
	}

	@Override
	public String categoryAdd(String category_name) throws ClientProtocolException, IOException {
		
		boolean isValidCategory = validateCategory(category_name);
		logger.info("isValid : "+isValidCategory);
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
		} 
		else {
			
			return "Category already exists";
		}
		
	}

	public boolean validateCategory(String category_name) throws ClientProtocolException, IOException {
		boolean flag = false;
		logger.info("Validating Category : " + category_name);
		String url = "http://localhost:8085/category/list/by-name/"+category_name;

		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Category : "+json);

		if (!json.equals("")) {
			flag = true;
			logger.info("Category already exists : "+category_name+" flag : "+flag);
			return flag;
			
		} 
		else {
			logger.info("Category does not exist : " + category_name);
			return flag;	
		}

	}

	@Override
	public void categoryEdit(String category_name,Category category) {
		logger.info(""+category);
		String url = "http://localhost:8085/category/update/by-name/"+category_name;
		logger.info("URL :"+url);
		JSONObject jsonObject = new JSONObject();

		// Add key-value pairs to the JSONObject
		jsonObject.put("category_name", category.getCategory_name());

		String json = jsonObject.toString();

		logger.info(json);

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
		logger.info("httpEntity : "+httpEntity);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.put(url, httpEntity, String.class);
//		
//		logger.info("Category updated : " + response);
//
//		return response;

	}

	@Override
	public void categoryDelete(String category_name) throws ClientProtocolException, IOException {
		
		String url = "http://localhost:8085/category/delete/by-name/"+category_name;
		HttpDelete request = new HttpDelete(url);
		HttpResponse response = httpClient.execute(request);
//		logger.info("response : "+response);
		org.apache.http.HttpEntity entity = response.getEntity();
//		logger.info("entity : "+entity);
		String json = EntityUtils.toString(entity);
//		logger.info("json : "+json);
		
	}

	@Override
	public void deleteCategory(int category_id) throws ClientProtocolException, IOException{
		String url = "http://localhost:8085/category/delete/by-id/"+category_id;
		HttpDelete request = new HttpDelete(url);
		HttpResponse response = httpClient.execute(request);
//		logger.info("response : "+response);
		org.apache.http.HttpEntity entity = response.getEntity();
//		logger.info("entity : "+entity);
		String json = EntityUtils.toString(entity);
//		logger.info("json : "+json);
		
	}

	@Override
	public Iterable<Item> getItems() throws ClientProtocolException, IOException {
		String url = "http://localhost:8085/items/list";
		logger.info("URL : "+url);
		logger.info("Fetching Items");
		       
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("Fetched Items : "+json);
//		List<Category> fetchedCategory = objectMapper.readValue(json, Category.class);
		List<Item> fetchedItem = objectMapper.readValue(json, new TypeReference<List<Item>>(){});

		
		return fetchedItem;
	}

}
