package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.repository.MenuRepository;
import com.pawar.inventory.app.repository.MenuRepositoryCustom;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;

@Service
public class MenuServiceImp implements MenuService {

	@Autowired
	private MenuRepository menuRepository;
	
	@Autowired
	private MenuRepositoryCustom menuRepositoryCustom;
	
	@Override
	public List<Menu> getAllMenus() {
		
		return menuRepository.findAll();
	}

	@Override
	public String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException {
		
		return menuRepositoryCustom.newLpn(lpn_name,item_name,quantity);
	}

	@Override
	public Item getItem(String item_name) throws ClientProtocolException, IOException {
		
		return menuRepositoryCustom.getItem(item_name);
	}

	@Override
	public Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException {
		
		return menuRepositoryCustom.getfindAllCategories();
	}

	@Override
	public String categoryAdd(String category_name) throws ClientProtocolException, IOException {
		
		return menuRepositoryCustom.categoryAdd(category_name);
	}

	@Override
	public void categoryEdit(String category_name, Category category) {
		
		 menuRepositoryCustom.categoryEdit(category_name,category);
	}

	@Override
	public void categoryDelete(String category_name) throws ClientProtocolException, IOException {
		menuRepositoryCustom.categoryDelete(category_name);
		
	}

	@Override
	public void deleteCategory(int category_id) throws ClientProtocolException, IOException {
		menuRepositoryCustom.deleteCategory(category_id);
		
	}

	@Override
	public Iterable<Item> getItems() throws ClientProtocolException, IOException {
		
		return menuRepositoryCustom.getItems();
	}

	@Override
	public String itemAdd(String description,String category,float length,float width,float height) throws ClientProtocolException, IOException {
		
		return menuRepositoryCustom.itemAdd(description,category,length,width,height);
	}

	@Override
	public void deleteItem(String description) throws ClientProtocolException, IOException {
		menuRepositoryCustom.deleteItem(description);
	}

	@Override
	public String itemEdit(String description, String category, float length, float width, float height) throws ClientProtocolException, IOException {
		return menuRepositoryCustom.itemEdit(description,category,length,width,height);
	}

	@Override
	public Iterable<Location> getLocations() throws ClientProtocolException, IOException {
		return menuRepositoryCustom.getLocations();
	}

	@Override
	public String locationAdd(String locn_brcd, String locn_class, float length, float width, float height,
			float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException  {
			return menuRepositoryCustom.locationAdd(locn_brcd, locn_class, length, width, height, max_volume, max_qty, max_weight);	
	}

	@Override
	public String locationEdit(String locn_brcd, String locn_class, float length, float width, float height,
			float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException {
			return menuRepositoryCustom.locationEdit(locn_brcd, locn_class, length, width, height, max_volume, max_qty, max_weight);		
	}

	@Override
	public void deleteLocation(String locn_brcd) throws ClientProtocolException, IOException {
		menuRepositoryCustom.deleteLocation(locn_brcd);
	}

}
