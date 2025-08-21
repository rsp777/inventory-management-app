package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pawar.inventory.app.exception.ParentMenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.repository.MenuRepository;
import com.pawar.inventory.app.repository.MenuRepositoryCustom;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Grp;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.Lpn;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopLocationRangeDto;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MenuServiceImp implements MenuService {

    private final static Logger logger = Logger.getLogger(MenuServiceImp.class.getName());

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
        return menuRepositoryCustom.newLpn(lpn_name, item_name, quantity);
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
    public void categoryEdit(String category_name, Category category) throws ClientProtocolException, IOException {
        menuRepositoryCustom.categoryEdit(category_name, category);
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
    public String itemAdd(String description, String category, float length, float width, float height)
            throws ClientProtocolException, IOException {
        return menuRepositoryCustom.itemAdd(description, category, length, width, height);
    }

    @Override
    public void deleteItem(int item_id) throws ClientProtocolException, IOException {
        menuRepositoryCustom.deleteItem(item_id);
    }

    @Override
    public ResponseEntity<String> itemEdit(int itemId,String description, String category, float length, float width, float height)
            throws ClientProtocolException, IOException {
        return menuRepositoryCustom.itemEdit(itemId, description, category, length, width, height);
    }

    @Override
    public Iterable<Location> getLocations() throws ClientProtocolException, IOException {
        return menuRepositoryCustom.getLocations();
    }

    @Override
    public String locationAdd(String locn_brcd, String grp, String locn_class, float length, float width, float height,
            float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException {
        return menuRepositoryCustom.locationAdd(locn_brcd, grp, locn_class, length, width, height, max_volume, max_qty,
                max_weight);
    }

    @Override
    public ResponseEntity<String> locationEdit(String locn_brcd, String grp, String locn_class, float length, float width,
            float height, float max_volume, float max_qty, float max_weight)
            throws ClientProtocolException, IOException {
        return menuRepositoryCustom.locationEdit(locn_brcd, grp, locn_class, length, width, height, max_volume, max_qty,
                max_weight);
    }

    @Override
    public void deleteLocation(String locn_brcd) throws ClientProtocolException, IOException {
        menuRepositoryCustom.deleteLocation(locn_brcd);
    }

    @Override
    public Iterable<Lpn> getLpns() throws ClientProtocolException, IOException {
        return menuRepositoryCustom.getLpns();
    }

    @Override
    public Iterable<Inventory> getInventories() throws ClientProtocolException, IOException {
        return menuRepositoryCustom.getInventories();
    }

    @Override
    public String locateLpnToResv(String lpn_name, String resv_locn) throws ClientProtocolException, IOException {
        return menuRepositoryCustom.locateLpnToResv(lpn_name, resv_locn);
    }

    @Override
    public String locateLpnToActive(String lpn_name, String active_locn) throws ClientProtocolException, IOException {
        return menuRepositoryCustom.locateLpnToActive(lpn_name, active_locn);
    }

    @Override
    public String lpnEdit(String lpn_name, String item_desc, float length, float width, float height, int quantity,
            int adjustQty, int lpn_facility_status, float volume) throws ClientProtocolException, IOException {
        return menuRepositoryCustom.lpnEdit(lpn_name, item_desc, length, width, height, quantity, adjustQty,
                lpn_facility_status, volume);
    }

    @Override
    @Transactional
    public Menu addMenu(String newProtocol, String newMenuName, String newMenuLink, String newHostname,
            String newMenuType, String newParentMenuName) throws ParentMenuNotFoundException {
        if (newMenuName == null || newMenuName.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu name cannot be null or empty");
        }

        Menu newMenu = new Menu(newProtocol, newMenuName, newMenuLink, newHostname, newMenuType);
        logger.info("Creating new menu: " + newMenu);

        if (newMenu.getMenu_type().equals("PARENT") || newMenu.getMenu_type().equals("PARENT_UI")) {
            logger.info("Menu is Parent");
            return menuRepositoryCustom.addMenu(newMenu);
        } else if (newMenuType.equals("CHILD") || newMenuType.equals("CHILD_UI")) {
            Menu parentMenu = menuRepository.findMenuByMenuName(newParentMenuName);
            if (parentMenu == null) {
                logger.severe("Parent menu does not exist: " + newParentMenuName);
                throw new ParentMenuNotFoundException("Parent menu does not exist: " + newParentMenuName);
            }
            newMenu.setParent(parentMenu);
            logger.info("Parent Menu is set: " + newMenu.getParent());
            return menuRepositoryCustom.addMenu(newMenu);
        }

        logger.info("Menu " + newMenuName + " is neither Parent nor Child.");
        return menuRepositoryCustom.addMenu(newMenu);
    }

    @Override
    public void updateMenu(String newProtocol, String newMenuName, String newMenuLink, String newHostname,
            String newMenuType) {
        Menu updatedMenu = new Menu(newProtocol, newMenuName, newHostname, newMenuLink, newMenuType);
        menuRepositoryCustom.updateMenu(updatedMenu);
    }

    @Override
    public String signIn(String username, String password) throws ClientProtocolException, IOException {
        String signInResponse = menuRepositoryCustom.signIn(username, password);
        return signInResponse;
    }

    @Override
    public String signout(HttpSession httpSession) throws ClientProtocolException, IOException {
        return menuRepositoryCustom.signout(httpSession);
    }

    @Override
    public Menu getMenu(String menuName) {
        return menuRepositoryCustom.getMenu(menuName);
    }

    @Override
    public String getUrl(String menuName) {
        return menuRepositoryCustom.getUrl(menuName);
    }

    @Override
    public String userAdd(String firstname, String middlename, String lastname, String username, String password,
            String email) {
        return menuRepositoryCustom.userAdd(firstname, middlename, lastname, username, password, email);
    }

    @Override
    public String checkActiveInventory(String lpn_name) throws ClientProtocolException, IOException {
        return menuRepositoryCustom.checkActiveInventory(lpn_name);
    }

    @Override
    public Iterable<Grp> getGrps() throws JsonMappingException, JsonProcessingException, ClientProtocolException, IOException {
        return menuRepositoryCustom.getGrps();
    }

    @Override
    public List<SopLocationRangeDto> getLocationRanges() throws ClientProtocolException, IOException {
        return menuRepositoryCustom.getLocationRanges();
    }

    @Override
    public List<SopActionTypeDto> getSopActionTypes() throws ClientProtocolException, IOException {
        return menuRepositoryCustom.getSopActionTypes();
    }

    @Override
    public List<Category> getCategories() throws ClientProtocolException, IOException {
        return menuRepositoryCustom.getCategories();
    }

    @Override
    public String assignBatch(String actionType,String batchAssign, String category) throws ClientProtocolException, IOException {
        return menuRepositoryCustom.assignBatch(actionType,batchAssign, category);
    }

	@Override
	public String addLocationRange(String actionType, String category_name, String fromLocation, String toLocation,
			 String isActive,String username) throws ClientProtocolException, IOException {
		 return menuRepositoryCustom.addLocationRange(actionType, category_name, fromLocation, toLocation,
					isActive,username);
	}

	@Override
	public List<String> getEligibleUpcsForSop(String category) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return menuRepositoryCustom.getEligibleUpcsForSop(category);
	}

	@Override
	public String updateLocationRange(String id,String actionType, String category_name, String fromLocation, String toLocation,
			String isActive, String username) throws ClientProtocolException, IOException {
		return menuRepositoryCustom.updateLocationRange(id,actionType, category_name, fromLocation, toLocation,
				isActive,username);
	}

    @Override
    public void unassignBatch(String sopActionType, String batchAssign, String category_name) throws ClientProtocolException, IOException {
         menuRepositoryCustom.unassignBatch(sopActionType,batchAssign, category_name);

    }
}
