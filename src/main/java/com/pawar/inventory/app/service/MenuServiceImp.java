package com.pawar.inventory.app.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pawar.inventory.app.exception.ParentMenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.repository.MenuRepository;
import com.pawar.inventory.app.repository.MenuRepositoryCustom;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
	public void categoryEdit(String category_name, Category category) {

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
	public void deleteItem(String description) throws ClientProtocolException, IOException {
		menuRepositoryCustom.deleteItem(description);
	}

	@Override
	public ResponseEntity<String> itemEdit(String description, String category, float length, float width, float height)
			throws ClientProtocolException, IOException {
		return menuRepositoryCustom.itemEdit(description, category, length, width, height);
	}

	@Override
	public Iterable<Location> getLocations() throws ClientProtocolException, IOException {
		return menuRepositoryCustom.getLocations();
	}

	@Override
	public String locationAdd(String locn_brcd, String locn_class, float length, float width, float height,
			float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException {
		return menuRepositoryCustom.locationAdd(locn_brcd, locn_class, length, width, height, max_volume, max_qty,
				max_weight);
	}

	@Override
	public ResponseEntity<String> locationEdit(String locn_brcd, String locn_class, float length, float width,
			float height, float max_volume, float max_qty, float max_weight)
			throws ClientProtocolException, IOException {
		return menuRepositoryCustom.locationEdit(locn_brcd, locn_class, length, width, height, max_volume, max_qty,
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
			String newMenuType,String newParentMenuName) {

		Menu newMenu = new Menu(newProtocol, newMenuName, newMenuLink, newHostname, newMenuType);
		logger.info("" + newMenu);
		Menu parentMenu = menuRepository.findMenuByMenuName(newParentMenuName);
		if (newMenu.getMenu_type().equals("PARENT")) {
			logger.info("Menu is Parent");
			return menuRepositoryCustom.addMenu(newMenu);
		} 
		else if (newMenuType.equals("CHILD")) {
			if (parentMenu == null) {
				throw new RuntimeException("Parent menu does not exist");
			}
			newMenu.setParent(parentMenu);
			logger.info("Parent Menu is set : " + newMenu.getParent());
		}
		
			logger.info("Menu "+newMenuName+" is neither Parent nor Child.");
			return menuRepositoryCustom.addMenu(newMenu);
		
	}

	@Override
	public void updateMenu(String newProtocol, String newMenuName, String newMenuLink, String newHostname,
			String newMenuType) {
		Menu updatedMenu = new Menu(newProtocol, newMenuName, newHostname, newMenuLink, newMenuType);

		menuRepositoryCustom.updateMenu(updatedMenu);
	}

	@Override
	public String signIn(String username, String password) {
		String signInResponse = menuRepositoryCustom.signIn(username, password);
		// String encodeToken = encodeToken(signInResponse);

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
		return menuRepositoryCustom.userAdd(firstname, middlename, lastname, username,password, email);
	}
}
