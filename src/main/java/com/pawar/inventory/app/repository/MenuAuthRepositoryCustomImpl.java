package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.Menu;
import com.pawar.sop.http.service.HttpService;

import jakarta.servlet.http.HttpSession;

@Component
public class MenuAuthRepositoryCustomImpl implements MenuAuthRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(MenuAuthRepositoryCustomImpl.class);

	@Autowired
	private MenuRepository menuRepository;

	@Autowired
	private HttpService httpService;

	@Override
	public Menu addMenu(Menu newMenu) {
		newMenu.setCreatedDttm(LocalDateTime.now());
		newMenu.setLastUpdatedDttm(LocalDateTime.now());
		newMenu.setCreatedSource("System");
		newMenu.setLastUpdatedSource("System");
		logger.info("Saving new menu: {}", newMenu.getMenuName());
		return menuRepository.save(newMenu);
	}

	@Override
	public Menu updateMenu(Menu updatedMenu) {
		if (updatedMenu == null || updatedMenu.getMenuName() == null) {
			return updatedMenu;
		}

		Menu existingMenu = menuRepository.findMenuByMenuName(updatedMenu.getMenuName());
		if (existingMenu == null) {
			logger.warn("No menu found to update for name: {}", updatedMenu.getMenuName());
			return null;
		}

		existingMenu.setProtocol(updatedMenu.getProtocol());
		existingMenu.setMenuName(updatedMenu.getMenuName());
		existingMenu.setHostname(updatedMenu.getHostname());
		existingMenu.setMenu_link(updatedMenu.getMenu_link());
		existingMenu.setMenu_type(updatedMenu.getMenu_type());
		existingMenu.setLastUpdatedDttm(LocalDateTime.now());
		existingMenu.setLastUpdatedSource("System");
		logger.info("Updating menu: {}", existingMenu.getMenuName());
		return menuRepository.save(existingMenu);
	}

	@Override
	public String signIn(String username, String password) throws ClientProtocolException, IOException {
		String url = getUrl(AppConstants.MenuEndpoint.LOGIN);
		logger.info("Sign-in URL : {}", url);

		JSONObject userJson = new JSONObject();
		userJson.put("username", username);
		userJson.put("passwordHash", password);

		return httpService.restCall(null, url, HttpMethod.POST, userJson.toString(), null).getBody().toString();
	}

	@Override
	public String signout(HttpSession httpSession) throws ClientProtocolException, IOException {
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		String url = getUrl(AppConstants.MenuEndpoint.SIGNOUT);
		logger.info("Sign-out URL : {}", url);

		httpService.restCall(decodedToken, url, HttpMethod.POST, null, null);
		return "Logged out";
	}

	@Override
	public Menu getMenu(String menuName) {
		return menuRepository.findMenuByMenuName(menuName);
	}

	@Override
	public String getUrl(String menuName) {
		Menu menu = menuRepository.findMenuByMenuName(menuName);
		if (menu == null) {
			return "";
		}

		String protocol = menu.getProtocol() == null ? "" : menu.getProtocol();
		String hostname = menu.getHostname() == null ? "" : menu.getHostname();
		String menuLink = menu.getMenu_link() == null ? "" : menu.getMenu_link();
		return String.format("%s%s%s", protocol, hostname, menuLink);
	}

	@Override
	public String userAdd(String firstname, String middlename, String lastname, String username, String password,
			String email) {
		JSONObject userJson = new JSONObject();
		userJson.put("username", username);
		userJson.put("email", email);
		userJson.put("passwordHash", password);
		userJson.put("firstName", firstname);
		userJson.put("middleName", middlename);
		userJson.put("lastName", lastname);

		String url = getUrl(AppConstants.MenuEndpoint.REGISTER);
		logger.info("User registration URL : {}", url);
		return httpService.restCall(null, url, HttpMethod.POST, userJson.toString(), null).getBody().toString();
	}
}