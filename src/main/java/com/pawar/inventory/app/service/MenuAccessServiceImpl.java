package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.management.relation.RoleNotFoundException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.app.controller.MenuController;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.model.MenuAccess;
import com.pawar.inventory.app.model.Permission;
import com.pawar.inventory.app.model.Role;

import com.pawar.inventory.app.repository.MenuAccessRepository;
import com.pawar.inventory.app.repository.MenuRepository;
import com.pawar.inventory.app.repository.MenuRepositoryCustom;
import com.pawar.inventory.app.repository.RoleRepository;
import com.pawar.inventory.entity.Category;
import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.dto.UserDto;

@Service
public class MenuAccessServiceImpl implements MenuAccessService {

	private final static Logger logger = Logger.getLogger(MenuAccessServiceImpl.class);

	@Autowired
	private MenuService menuService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private MenuRepository menuRepository;
	
	@Autowired
	private MenuRepositoryCustom menuRepositoryCustom;

	@Autowired
	private MenuAccessRepository menuAccessRepository;

	private final ObjectMapper mapper;
	private final HttpClient httpClient;


	public MenuAccessServiceImpl() {
		httpClient = HttpClients.createDefault();
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}

	@Override
	public List<Menu> getAccessibleMenus(String jwtToken) throws JsonMappingException, JsonProcessingException,MenuNotFoundException {

		List<Menu> accessibleMenus = new ArrayList<>();

		List<Menu> allmenus = menuService.getAllMenus();
		String[] decodedToken = decodeToken(jwtToken);
		Set<Role> userRoles = getRoles(decodedToken);
		for (Menu menu : allmenus) {
			if (hasAccess(menu, userRoles)) {
				accessibleMenus.add(menu);
			}
		}

		return accessibleMenus;
	}

	public boolean hasAccess(Menu menu, Set<Role> userRoles) {
		int menuId = menu.getMenu_id();
		List<MenuAccess> menuAccesses = menuAccessRepository.findMenuAccessesByMenuId(menuId);

		for (MenuAccess menuAccess : menuAccesses) {
			for (Role userRole : userRoles) {

				if (userRole.getRole_id() == menuAccess.getRoleId()) {
					return true;
				} else {
					return false;
				}

			}
		}
		return false;
	}

	@Override
	public String[] decodeToken(String token) {
		DecodedJWT decodedJWT = JWT.decode(token);
		String[] decodedToken = decodedJWT.getSubject().split("\\|");
		return decodedToken;
	}

	public Set<Role> getRoles(String[] decodedToken) throws JsonMappingException, JsonProcessingException {

		Set<Role> userRoles = new HashSet<>();
		logger.infof("decodedToken[3] : {}",decodedToken[2]);
		for (int i = 0; i < decodedToken.length - 1; i++) {
			if (decodedToken[i].contains("Role")) {
//				logger.info("decodedToken in loop : {}",decodedToken[i]);
				String result = decodedToken[i].replaceAll("^\\[", "").replaceAll("\\]$", ""); 
				String json = "{" +
	                    "\"role_id\":" + result.substring(result.indexOf("id=") + 3, result.indexOf(", name")).trim() + "," +
	                    "\"name\":\"" + result.substring(result.indexOf("name=") + 5, result.indexOf(", permissions")).trim() + "\"," +
	                    "\"permissions\":[{\"id\":" + result.substring(result.indexOf("id=") + 3, result.indexOf(", name")).trim() + "," +
	                    "\"name\":\"" + result.substring(result.indexOf("name=") + 5, result.indexOf(", createdDttm")).trim() + "\"}]" +
	                    "}";
				logger.infof("result in loop : {}",json);
				Role role =mapper.readValue(json, Role.class);
				userRoles.add(role);
			}
		}
		return userRoles;
	}

	public String getUserName(String[] decodedString) {
		String user_name = "";
		for (int i = 0; i < decodedString.length - 1; i++) {
			user_name = user_name + " " + decodedString[i];
		}
		logger.info("User name : {}" + user_name);
		return user_name;
	}

	@Override
	public void assignMenusToRole(int menuId, Integer roleId)
			throws RoleNotFoundException, MenuNotFoundException, JsonProcessingException {

		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleId));

		Menu assignedMenu = menuRepository.findById(menuId)
				.orElseThrow(() -> new MenuNotFoundException("Menu not found with id: " + menuId));

		Set<Menu> assignedMenus = new HashSet<>(role.getMenus());
		assignedMenus.add(assignedMenu);
		role.setMenus(assignedMenus);
		Role savedRole = roleRepository.save(role);

		logger.infof("Updated Role : {} ", savedRole);

		logger.infof("Menus {} assigned successfully to Role ID: {}", assignedMenu.getMenu_name(), roleId);

	}

	@Override
	public void unassignMenusToRole(int menuId, Integer roleId)
			throws RoleNotFoundException, MenuNotFoundException, JsonProcessingException {

		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId));

		Menu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new MenuNotFoundException("Menu not found with id: " + menuId));

		role.getMenus().removeIf(m -> m.getMenu_id() == menuId);
		Role savedRole = roleRepository.save(role);

		logger.infof("Updated Role : {} ", savedRole);
	}

	@Override
	public List<UserDto> getUsers() throws ClientProtocolException, IOException {
		String url = menuRepositoryCustom.getUrl("getUsers");
		logger.info("URL : " + url);
		
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info(json);
		List<UserDto> fetchedUsers = mapper.readValue(json, new TypeReference<List<UserDto>>() {
		});

		return fetchedUsers;
	}
}
