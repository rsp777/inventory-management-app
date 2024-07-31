package com.pawar.inventory.app.repository;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.hibernate.Session;
import org.springframework.http.ResponseEntity;

import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.Lpn;

import jakarta.servlet.http.HttpSession;


public interface MenuRepositoryCustom  {

	String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException;

	Item getItem(String item_name) throws ClientProtocolException, IOException;

	Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException;

	String categoryAdd(String category_name) throws ClientProtocolException, IOException;

	void categoryEdit(String category_name, Category category);

	void categoryDelete(String category_name) throws ClientProtocolException, IOException;

	void deleteCategory(int category_id) throws ClientProtocolException, IOException;

	Iterable<Item> getItems() throws ClientProtocolException, IOException;

    String itemAdd(String description,String category,float length,float width,float height) throws ClientProtocolException, IOException;

	void deleteItem(String description) throws ClientProtocolException, IOException;

	ResponseEntity<String> itemEdit(String description, String category, float length, float width, float height) throws ClientProtocolException, IOException;

	Iterable<Location> getLocations() throws ClientProtocolException, IOException;

	String locationAdd(String locn_brcd, String locn_class, float length, float width, float height, float max_volume,
            float max_qty, float max_weight) throws ClientProtocolException, IOException;

	ResponseEntity<String> locationEdit(String locn_brcd, String locn_class, float length, float width, float height, float max_volume,
            float max_qty, float max_weight)throws ClientProtocolException, IOException;

    void deleteLocation(String locn_brcd)throws ClientProtocolException, IOException;

    Iterable<Lpn> getLpns() throws ClientProtocolException, IOException;

    Iterable<Inventory> getInventories() throws ClientProtocolException, IOException;

    String locateLpnToResv(String lpn_name, String resv_locn) throws ClientProtocolException, IOException;

	String locateLpnToActive(String lpn_name, String active_locn) throws ClientProtocolException, IOException;

	String lpnEdit(String lpn_name, String item_desc, float length, float width, float height, int quantity,int adjustQty,
			int lpn_facility_status, float volume) throws ClientProtocolException, IOException;

    Menu addMenu(Menu newMenu);

	String signIn(String username, String password);

	void updateMenu(Menu updatedMenu);

	Menu getMenu(String menuName);

    String getUrl(String menuName);

	String signout(HttpSession httpSession) throws ClientProtocolException, IOException;

	String userAdd(String firstname, String middlename, String lastname, String username, String password,
			String email);

}

