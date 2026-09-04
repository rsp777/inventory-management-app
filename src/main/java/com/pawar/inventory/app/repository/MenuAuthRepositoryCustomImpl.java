package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.ResponseEntity;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.util.SessionUtil;
import com.pawar.sop.http.service.HttpService;

import jakarta.servlet.http.HttpSession;

@Component
public class MenuAuthRepositoryCustomImpl implements MenuAuthRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(MenuAuthRepositoryCustomImpl.class);

	private final MenuRepository menuRepository;
	private final HttpService httpService;

	public MenuAuthRepositoryCustomImpl(MenuRepository menuRepository, HttpService httpService) {
		this.menuRepository = menuRepository;
		this.httpService = httpService;
	}

	@Override
	@Transactional
	public Menu addMenu(Menu newMenu) {
		newMenu.setCreatedDttm(LocalDateTime.now());
		newMenu.setLastUpdatedDttm(LocalDateTime.now());
		newMenu.setCreatedSource(AppConstants.Application.AUDIT_SOURCE);
		newMenu.setLastUpdatedSource(AppConstants.Application.AUDIT_SOURCE);
		logger.info("Saving new menu: {}", newMenu.getMenuName());
		return menuRepository.save(newMenu);
	}

	@Override
	@Transactional
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
		existingMenu.setLastUpdatedSource(AppConstants.Application.AUDIT_SOURCE);
		logger.info("Updating menu: {}", existingMenu.getMenuName());
		return menuRepository.save(existingMenu);
	}

	@Override
	public String signIn(String username, String password) throws ClientProtocolException, IOException {
		String url = requireUrl(AppConstants.MenuEndpoint.LOGIN);
		logger.info("Sign-in URL : {}", url);

		JSONObject userJson = new JSONObject();
		userJson.put("username", username);
		userJson.put("passwordHash", password);

		return extractBody(httpService.restCall(null, url, HttpMethod.POST, userJson.toString(), null));
	}

	@Override
	public String signout(HttpSession httpSession) throws ClientProtocolException, IOException {
		String decodedToken = SessionUtil.getSessionToken(httpSession);
		String url = getUrl(AppConstants.MenuEndpoint.SIGNOUT);
		logger.info("Sign-out URL : {}", url);

		httpService.restCall(decodedToken, url, HttpMethod.GET, null, null);
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
			logger.warn("No endpoint configuration found in menu table for key: '{}'", menuName);
			return "";
		}

		String protocol = menu.getProtocol() == null ? "" : menu.getProtocol();
		String hostname = menu.getHostname() == null ? "" : menu.getHostname();
		String menuLink = menu.getMenu_link() == null ? "" : menu.getMenu_link();
		return String.format("%s%s%s", protocol, hostname, menuLink);
	}

	private String requireUrl(String menuName) throws IOException {
		String url = getUrl(menuName);
		if (url == null || url.isBlank()) {
			throw new IOException("Endpoint '" + menuName + "' is not configured. Add a menu table row with that name.");
		}
		return url;
	}

	private String extractBody(ResponseEntity<?> response) {
		return (response != null && response.getBody() != null) ? response.getBody().toString() : "";
	}

	@Override
	public String userAdd(String firstname, String middlename, String lastname, String username, String password,
			String email, List<String> roles, List<String> permissions) throws ClientProtocolException, IOException {
		JSONObject userJson = new JSONObject();
		userJson.put("username", username);
		userJson.put("email", email);
		userJson.put("passwordHash", password);
		userJson.put("firstName", firstname);
		userJson.put("middleName", middlename);
		userJson.put("lastName", lastname);
		userJson.put("roles", roles);
		userJson.put("permissions", permissions);
		String url = getUrl(AppConstants.MenuEndpoint.REGISTER);
		logger.info("User registration URL : {}", url);
		return extractBody(httpService.restCall(null, url, HttpMethod.POST, userJson.toString(), null));
	}

	@Override
	public String assignRoleToUser(Integer userId, Integer roleId) throws ClientProtocolException, IOException {
		String url = requireUrl(AppConstants.MenuEndpoint.ASSIGN_ROLE_TO_USER)
				.replace("{userId}", String.valueOf(userId))
				.replace("{roleId}", String.valueOf(roleId));
		logger.info("Assign-role URL : {}", url);
		return extractBody(httpService.restCall(null, url, HttpMethod.POST, null, null));
	}

	@Override
	public String unassignRoleFromUser(Integer userId, Integer roleId) throws ClientProtocolException, IOException {
		String url = requireUrl(AppConstants.MenuEndpoint.UNASSIGN_ROLE_FROM_USER)
				.replace("{userId}", String.valueOf(userId))
				.replace("{roleId}", String.valueOf(roleId));
		logger.info("Unassign-role URL : {}", url);
		return extractBody(httpService.restCall(null, url, HttpMethod.DELETE, null, null));
	}

	@Override
	public String getUserRoles(Integer userId) throws ClientProtocolException, IOException {
		String url = requireUrl(AppConstants.MenuEndpoint.GET_USER_ROLES)
				.replace("{userId}", String.valueOf(userId));
		logger.info("Get user roles URL : {}", url);
		return extractBody(httpService.restCall(null, url, HttpMethod.GET, null, null));
	}

	@Override
	public String deleteUser(Integer userId) throws ClientProtocolException, IOException {
		String url = requireUrl(AppConstants.MenuEndpoint.DELETE_USER)
				.replace("{userId}", String.valueOf(userId));
		logger.info("Delete user URL : {}", url);
		return extractBody(httpService.restCall(null, url, HttpMethod.DELETE, null, null));
	}
}