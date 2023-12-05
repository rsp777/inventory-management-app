package com.pawar.inventory.app.repository;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;


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

	String itemEdit(String description, String category, float length, float width, float height) throws ClientProtocolException, IOException;

	Iterable<Location> getLocations() throws ClientProtocolException, IOException;

	String locationAdd(String locn_brcd, String locn_class, float length, float width, float height, float max_volume,
            float max_qty, float max_weight) throws ClientProtocolException, IOException;

    String locationEdit(String locn_brcd, String locn_class, float length, float width, float height, float max_volume,
            float max_qty, float max_weight)throws ClientProtocolException, IOException;

    void deleteLocation(String locn_brcd)throws ClientProtocolException, IOException;

    Iterable<Lpn> getLpns() throws ClientProtocolException, IOException;

    Iterable<Inventory> getInventories() throws ClientProtocolException, IOException;

}

