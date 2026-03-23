package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pawar.inventory.app.exception.ParentMenuNotFoundException;
import com.pawar.inventory.app.model.CubiscanLog;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Grp;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.Lpn;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopLocationRangeDto;

import jakarta.servlet.http.HttpSession;

public interface MenuService {

    List<Menu> getAllMenus();

    String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException;

    Item getItem(String item_name) throws ClientProtocolException, IOException;

    Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException;

    String categoryAdd(String category_name) throws ClientProtocolException, IOException;

        void categoryEdit(String category_name, String updatedCategoryName) throws ClientProtocolException, IOException;

    void categoryDelete(String category_name) throws ClientProtocolException, IOException;

    void deleteCategory(int category_id) throws ClientProtocolException, IOException;

    Iterable<Item> getItems() throws ClientProtocolException, IOException;

    String itemAdd(String description, String category, float length, float width, float height)
            throws ClientProtocolException, IOException;

    void deleteItem(int description) throws ClientProtocolException, IOException;

    ResponseEntity<String> itemEdit(int itemId,String description, String category, float length, float width, float height)
            throws ClientProtocolException, IOException;

    Iterable<Location> getLocations() throws ClientProtocolException, IOException;

        Location getLocationById(int id) throws ClientProtocolException, IOException;

        List<Location> searchLocationsByCode(String code) throws ClientProtocolException, IOException;

    String locationAdd(String locn_brcd, String grp, String locn_class, float length, float width, float height,
            float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException;

    ResponseEntity<String> locationEdit(String locn_brcd, String grp, String locn_class, float length, float width,
            float height, float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException;

    void deleteLocation(String locn_brcd) throws ClientProtocolException, IOException;

    Iterable<Lpn> getLpns() throws ClientProtocolException, IOException;

        Lpn getLpnById(int id) throws ClientProtocolException, IOException;

        List<Lpn> searchLpns(String lpnNumber) throws ClientProtocolException, IOException;

    Iterable<Inventory> getInventories() throws ClientProtocolException, IOException;

        Inventory getInventoryById(int id) throws ClientProtocolException, IOException;

        List<Inventory> getInventoriesByItemId(int itemId) throws ClientProtocolException, IOException;

        List<Inventory> getInventoriesByLocationId(int locationId) throws ClientProtocolException, IOException;

        List<Inventory> getInventoriesByLpnId(int lpnId) throws ClientProtocolException, IOException;

        List<Inventory> searchInventories(Integer itemId, Integer locationId, Integer lpnId)
			throws ClientProtocolException, IOException;

    String locateLpnToResv(String lpn_name, String resv_locn) throws ClientProtocolException, IOException;

    String locateLpnToActive(String lpn_name, String active_locn) throws ClientProtocolException, IOException;

    String lpnEdit(String lpn_name, String item_desc, float length, float width, float height, int quantity,
            int adjustQty, int lpn_facility_status, float volume) throws ClientProtocolException, IOException;

    Menu addMenu(String newProtocol, String newMenuName, String newMenuLink, String newHostname, String newMenuType,
            String newParentMenuName) throws ParentMenuNotFoundException;

    String signIn(String username, String password) throws ClientProtocolException, IOException;

    String signout(HttpSession httpSession) throws ClientProtocolException, IOException;

    Menu updateMenu(String newProtocol, String newMenuName, String newMenuLink, String newHostname, String newMenuType);

    Menu getMenu(String menuName);

    String getUrl(String menuName);

    String userAdd(String firstname, String middlename, String lastname, String username, String password, String email);

    String checkActiveInventory(String lpn_name) throws ClientProtocolException, IOException;

    Iterable<Grp> getGrps() throws JsonMappingException, JsonProcessingException, ClientProtocolException, IOException;

    List<SopLocationRangeDto> getLocationRanges() throws ClientProtocolException, IOException;

    List<SopActionTypeDto> getSopActionTypes() throws ClientProtocolException, IOException;

List<Category> getCategories() throws ClientProtocolException, IOException;

String assignBatch(String actionType,String batchAssign, String category) throws ClientProtocolException, IOException;

String addLocationRange(String actionType, String category_name, String fromLocation, String toLocation,
		 String isActive, String username) throws ClientProtocolException, IOException;

List<String> getEligibleUpcsForSop(String category) throws ClientProtocolException, IOException;

String updateLocationRange(String id,String actionType, String category_name, String fromLocation, String toLocation,
		String isActive, String username) throws ClientProtocolException, IOException;

void unassignBatch(String sopActionType, String batchAssign, String category_name) throws ClientProtocolException, IOException;

List<CubiscanLog> getCubiscanLogs();
}
