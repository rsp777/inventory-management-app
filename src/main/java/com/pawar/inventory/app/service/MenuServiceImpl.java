package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.exception.ParentMenuNotFoundException;
import com.pawar.inventory.app.model.CubiscanLog;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.repository.CategoryRepositoryCustom;
import com.pawar.inventory.app.repository.InventoryRepositoryCustom;
import com.pawar.inventory.app.repository.ItemRepositoryCustom;
import com.pawar.inventory.app.repository.LocationRepositoryCustom;
import com.pawar.inventory.app.repository.LpnRepositoryCustom;
import com.pawar.inventory.app.repository.MenuAuthRepositoryCustom;
import com.pawar.inventory.app.repository.MenuRepository;
import com.pawar.inventory.app.repository.SopRepositoryCustom;
import com.pawar.inventory.app.service.base.AbstractBaseService;
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
public class MenuServiceImpl extends AbstractBaseService implements MenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final MenuRepository menuRepository;
    private final CategoryRepositoryCustom categoryRepositoryCustom;
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final LocationRepositoryCustom locationRepositoryCustom;
    private final LpnRepositoryCustom lpnRepositoryCustom;
    private final InventoryRepositoryCustom inventoryRepositoryCustom;
    private final SopRepositoryCustom sopRepositoryCustom;
    private final MenuAuthRepositoryCustom menuAuthRepositoryCustom;

    public MenuServiceImpl(MenuRepository menuRepository, CategoryRepositoryCustom categoryRepositoryCustom,
            ItemRepositoryCustom itemRepositoryCustom, LocationRepositoryCustom locationRepositoryCustom,
            LpnRepositoryCustom lpnRepositoryCustom, InventoryRepositoryCustom inventoryRepositoryCustom,
            SopRepositoryCustom sopRepositoryCustom, MenuAuthRepositoryCustom menuAuthRepositoryCustom) {
        this.menuRepository = menuRepository;
        this.categoryRepositoryCustom = categoryRepositoryCustom;
        this.itemRepositoryCustom = itemRepositoryCustom;
        this.locationRepositoryCustom = locationRepositoryCustom;
        this.lpnRepositoryCustom = lpnRepositoryCustom;
        this.inventoryRepositoryCustom = inventoryRepositoryCustom;
        this.sopRepositoryCustom = sopRepositoryCustom;
        this.menuAuthRepositoryCustom = menuAuthRepositoryCustom;
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuRepository.findAllOrderedByMenuId();
    }

    @Override
    public String newLpn(String lpn_name, String item_name, int quantity) throws ClientProtocolException, IOException {
        return lpnRepositoryCustom.createLpn(lpn_name, item_name, quantity);
    }

    @Override
    public Item getItem(String item_name) throws ClientProtocolException, IOException {
        return itemRepositoryCustom.getItem(item_name);
    }

    @Override
    public Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException {
        return categoryRepositoryCustom.getfindAllCategories();
    }

    @Override
    public String categoryAdd(String category_name) throws ClientProtocolException, IOException {
        return categoryRepositoryCustom.categoryAdd(category_name);
    }

    @Override
    public void categoryEdit(String category_name, String updatedCategoryName)
            throws ClientProtocolException, IOException {
        categoryRepositoryCustom.categoryEdit(category_name, updatedCategoryName);
    }

    @Override
    public void categoryDelete(String category_name) throws ClientProtocolException, IOException {
        categoryRepositoryCustom.categoryDelete(category_name);
    }

    @Override
    public void deleteCategory(int category_id) throws ClientProtocolException, IOException {
        categoryRepositoryCustom.deleteCategory(category_id);
    }

    @Override
    public Iterable<Item> getItems() throws ClientProtocolException, IOException {
        return itemRepositoryCustom.getItems();
    }

    @Override
    public String itemAdd(String description, String category, float length, float width, float height)
            throws ClientProtocolException, IOException {
        return itemRepositoryCustom.itemAdd(description, category, length, width, height);
    }

    @Override
    public void deleteItem(int item_id) throws ClientProtocolException, IOException {
        itemRepositoryCustom.deleteItem(item_id);
    }

    @Override
    public ResponseEntity<String> itemEdit(int itemId,String description, String category, float length, float width, float height)
            throws ClientProtocolException, IOException {
        return itemRepositoryCustom.itemEdit(itemId, description, category, length, width, height);
    }

    @Override
    public Iterable<Location> getLocations() throws ClientProtocolException, IOException {
        return locationRepositoryCustom.getLocations();
    }

    @Override
    public Location getLocationById(int id) throws ClientProtocolException, IOException {
        return locationRepositoryCustom.getLocationById(id);
    }

    @Override
    public List<Location> searchLocationsByCode(String code) throws ClientProtocolException, IOException {
        return locationRepositoryCustom.searchLocationsByCode(code);
    }

    @Override
    public String locationAdd(String locn_brcd, String grp, String locn_class, float length, float width, float height,
            float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException {
        return locationRepositoryCustom.locationAdd(locn_brcd, grp, locn_class, length, width, height, max_volume, max_qty,
                max_weight);
    }

    @Override
    public ResponseEntity<String> locationEdit(String locn_brcd, String grp, String locn_class, float length, float width,
            float height, float max_volume, float max_qty, float max_weight)
            throws ClientProtocolException, IOException {
        return locationRepositoryCustom.locationEdit(locn_brcd, grp, locn_class, length, width, height, max_volume, max_qty,
                max_weight);
    }

    @Override
    public void deleteLocation(String locn_brcd) throws ClientProtocolException, IOException {
        locationRepositoryCustom.deleteLocation(locn_brcd);
    }

    @Override
    public Iterable<Lpn> getLpns() throws ClientProtocolException, IOException {
        return lpnRepositoryCustom.getAllLpns();
    }

    @Override
    public Lpn getLpnById(int id) throws ClientProtocolException, IOException {
        return lpnRepositoryCustom.getLpnById(id);
    }

    @Override
    public List<Lpn> searchLpns(String lpnNumber) throws ClientProtocolException, IOException {
        return lpnRepositoryCustom.searchLpns(lpnNumber);
    }

    @Override
    public Iterable<Inventory> getInventories() throws ClientProtocolException, IOException {
        return inventoryRepositoryCustom.getInventories();
    }

    @Override
    public Inventory getInventoryById(int id) throws ClientProtocolException, IOException {
        return inventoryRepositoryCustom.getInventoryById(id);
    }

    @Override
    public List<Inventory> getInventoriesByItemId(int itemId) throws ClientProtocolException, IOException {
        return inventoryRepositoryCustom.getInventoriesByItemId(itemId);
    }

    @Override
    public List<Inventory> getInventoriesByLocationId(int locationId) throws ClientProtocolException, IOException {
        return inventoryRepositoryCustom.getInventoriesByLocationId(locationId);
    }

    @Override
    public List<Inventory> getInventoriesByLpnId(int lpnId) throws ClientProtocolException, IOException {
        return inventoryRepositoryCustom.getInventoriesByLpnId(lpnId);
    }

    @Override
    public List<Inventory> searchInventories(Integer itemId, Integer locationId, Integer lpnId)
            throws ClientProtocolException, IOException {
        return inventoryRepositoryCustom.searchInventories(itemId, locationId, lpnId);
    }

    @Override
    public String locateLpnToResv(String lpn_name, String resv_locn) throws ClientProtocolException, IOException {
        return lpnRepositoryCustom.moveLpnToReserve(lpn_name, resv_locn);
    }

    @Override
    public String locateLpnToActive(String lpn_name, String active_locn) throws ClientProtocolException, IOException {
        return lpnRepositoryCustom.moveLpnToActive(lpn_name, active_locn);
    }

    @Override
    public String lpnEdit(String lpn_name, String item_desc, float length, float width, float height, int quantity,
            int adjustQty, int lpn_facility_status, float volume) throws ClientProtocolException, IOException {
        return lpnRepositoryCustom.updateLpn(lpn_name, item_desc, length, width, height, quantity, adjustQty,
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
        logInfo("Creating new menu: " + newMenu);

        if (AppConstants.MenuType.PARENT.equals(newMenu.getMenu_type())
                || AppConstants.MenuType.PARENT_UI.equals(newMenu.getMenu_type())) {
            logger.info("Menu is Parent");
            return menuAuthRepositoryCustom.addMenu(newMenu);
        } else if (AppConstants.MenuType.CHILD.equals(newMenuType)
                || AppConstants.MenuType.CHILD_UI.equals(newMenuType)) {
            Menu parentMenu = menuRepository.findMenuByMenuName(newParentMenuName);
            if (parentMenu == null) {
                logger.error("Parent menu does not exist: {}", newParentMenuName);
                throw new ParentMenuNotFoundException("Parent menu does not exist: " + newParentMenuName);
            }
            newMenu.setParent(parentMenu);
            logInfo("Parent Menu is set: " + newMenu.getParent());
            return menuAuthRepositoryCustom.addMenu(newMenu);
        }

        logInfo("Menu " + newMenuName + " is neither Parent nor Child.");
        return menuAuthRepositoryCustom.addMenu(newMenu);
    }

    @Override
    public Menu updateMenu(String newProtocol, String newMenuName, String newMenuLink, String newHostname,
            String newMenuType) {
        Menu updatedMenu = new Menu(newProtocol, newMenuName, newMenuLink, newHostname, newMenuType);
        return menuAuthRepositoryCustom.updateMenu(updatedMenu);
    }

    @Override
    public String signIn(String username, String password) throws ClientProtocolException, IOException {
        String signInResponse = menuAuthRepositoryCustom.signIn(username, password);
        return signInResponse;
    }

    @Override
    public String signout(HttpSession httpSession) throws ClientProtocolException, IOException {
        return menuAuthRepositoryCustom.signout(httpSession);
    }

    @Override
    public Menu getMenu(String menuName) {
        return menuAuthRepositoryCustom.getMenu(menuName);
    }

    @Override
    public String getUrl(String menuName) {
        return menuAuthRepositoryCustom.getUrl(menuName);
    }

    @Override
    public String userAdd(String firstname, String middlename, String lastname, String username, String password,
            String email, List<String> roles, List<String> permissions) throws ClientProtocolException, IOException {
        return menuAuthRepositoryCustom.userAdd(firstname, middlename, lastname, username, password, email, roles, permissions);
    }

    @Override
    public String assignRoleToUser(Integer userId, Integer roleId) throws ClientProtocolException, IOException {
        return menuAuthRepositoryCustom.assignRoleToUser(userId, roleId);
    }

    @Override
    public String unassignRoleFromUser(Integer userId, Integer roleId) throws ClientProtocolException, IOException {
        return menuAuthRepositoryCustom.unassignRoleFromUser(userId, roleId);
    }

    @Override
    public String getUserRoles(Integer userId) throws ClientProtocolException, IOException {
        return menuAuthRepositoryCustom.getUserRoles(userId);
    }

    @Override
    public String deleteUser(Integer userId) throws ClientProtocolException, IOException {
        return menuAuthRepositoryCustom.deleteUser(userId);
    }

    @Override
    public String checkActiveInventory(String lpn_name) throws ClientProtocolException, IOException {
        return lpnRepositoryCustom.checkActiveInventory(lpn_name);
    }

    @Override
    public Iterable<Grp> getGrps() throws JsonMappingException, JsonProcessingException, ClientProtocolException, IOException {
        return sopRepositoryCustom.getGrps();
    }

    @Override
    public List<SopLocationRangeDto> getLocationRanges() throws ClientProtocolException, IOException {
        return sopRepositoryCustom.getLocationRanges();
    }

    @Override
    public List<SopActionTypeDto> getSopActionTypes() throws ClientProtocolException, IOException {
        return sopRepositoryCustom.getSopActionTypes();
    }

    @Override
    public List<Category> getCategories() throws ClientProtocolException, IOException {
        return sopRepositoryCustom.getCategories();
    }

    @Override
    public String assignBatch(String actionType,String batchAssign, String category) throws ClientProtocolException, IOException {
        return sopRepositoryCustom.assignBatch(actionType,batchAssign, category);
    }

	@Override
	public String addLocationRange(String actionType, String category_name, String fromLocation, String toLocation,
			 String isActive,String username) throws ClientProtocolException, IOException {
         return sopRepositoryCustom.addLocationRange(actionType, category_name, fromLocation, toLocation,
					isActive,username);
	}

@Override
	public List<String> getEligibleUpcsForSop(String category) throws ClientProtocolException, IOException {
        return sopRepositoryCustom.getEligibleUpcsForSop(category);
	}

	@Override
	public String updateLocationRange(String id,String actionType, String category_name, String fromLocation, String toLocation,
			String isActive, String username) throws ClientProtocolException, IOException {
        return sopRepositoryCustom.updateLocationRange(id,actionType, category_name, fromLocation, toLocation,
				isActive,username);
	}

    @Override
    public void unassignBatch(String sopActionType, String batchAssign, String category_name) throws ClientProtocolException, IOException {
         sopRepositoryCustom.unassignBatch(sopActionType,batchAssign, category_name);

    }

    @Override
    public List<CubiscanLog> getCubiscanLogs() {
        return sopRepositoryCustom.getCubiscanLogs();
    }
}
