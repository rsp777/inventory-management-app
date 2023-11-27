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

@Service
public class MenuServiceImp implements MenuService {

	@Autowired
	private MenuRepository menuRepository;
	
	@Autowired
	private MenuRepositoryCustom menuRepositoryCustom;
	
	@Override
	public List<Menu> getAllMenus() {
		// TODO Auto-generated method stub
		return menuRepository.findAll();
	}

	@Override
	public String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return menuRepositoryCustom.newLpn(lpn_name,item_name,quantity);
	}

	@Override
	public Item getItem(String item_name) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return menuRepositoryCustom.getItem(item_name);
	}

	@Override
	public Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return menuRepositoryCustom.getfindAllCategories();
	}

	@Override
	public String categoryAdd(String category_name) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return menuRepositoryCustom.categoryAdd(category_name);
	}

	@Override
	public void categoryEdit(String category_name, Category category) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return menuRepositoryCustom.getItems();
	}

}
