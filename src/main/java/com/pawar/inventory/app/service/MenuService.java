package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.http.ResponseEntity;

import com.pawar.inventory.app.exception.ParentMenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.Lpn;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

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

	String itemAdd(String description, String category, float length, float width, float height)
			throws ClientProtocolException, IOException;

	void deleteItem(String description) throws ClientProtocolException, IOException;

	ResponseEntity<String> itemEdit(String description, String category, float length, float width, float height)
			throws ClientProtocolException, IOException;

	Iterable<Location> getLocations() throws ClientProtocolException, IOException;

	String locationAdd(String locn_brcd, String locn_class, float length, float width, float height, float max_volume,
			float max_qty, float max_weight) throws ClientProtocolException, IOException;

	ResponseEntity<String> locationEdit(String locn_brcd, String locn_class, float length, float width, float height,
			float max_volume,
			float max_qty, float max_weight) throws ClientProtocolException, IOException;;

	void deleteLocation(String locn_brcd) throws ClientProtocolException, IOException;

	Iterable<Lpn> getLpns() throws ClientProtocolException, IOException;

	Iterable<Inventory> getInventories() throws ClientProtocolException, IOException;

	String locateLpnToResv(String lpn_name, String resv_locn) throws ClientProtocolException, IOException;

	String locateLpnToActive(String lpn_name, String active_locn) throws ClientProtocolException, IOException;

	String lpnEdit(String lpn_name, String item_desc, float length, float width, float height, int quantity,
			int adjustQty,
			int lpn_facility_status, float volume) throws ClientProtocolException, IOException;

	Menu addMenu(String newProtocol, String newMenuName, String newMenuLink, String newHostname,String newMenuType,String newParentMenuName);

	String signIn(String username, String password);

	String signout(HttpSession httpSession) throws ClientProtocolException, IOException;
	// String encodeToken(String jwtToken);

	// Map<String, String> decodeToken(String jwtToken);

	void updateMenu(String newProtocol, String newMenuName, String newMenuLink, String newHostname,	String newMenuType);

	Menu getMenu(String menuName);

	String getUrl(String menuName);

	String userAdd(String firstname, String middlename, String lastname, String username, String password,
			String email);

	String checkActiveInventory(String lpn_name);

}
