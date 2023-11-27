package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;

public interface MenuService {

	List<Menu> getAllMenus();

	String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException;

	Item getItem(String item_name) throws ClientProtocolException, IOException;

	Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException;

	String categoryAdd(String category_name) throws ClientProtocolException, IOException;

	void categoryEdit(String category_name, Category category);

	void categoryDelete(String category_name) throws ClientProtocolException, IOException;

	void deleteCategory(int category_id) throws ClientProtocolException, IOException;

	Iterable<Item> getItems() throws ClientProtocolException, IOException;

}
