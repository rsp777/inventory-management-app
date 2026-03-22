package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.relation.RoleNotFoundException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.model.MenuAccess;
import com.pawar.inventory.app.model.Role;

import com.pawar.inventory.app.repository.MenuAccessRepository;
import com.pawar.inventory.app.repository.MenuRepository;
import com.pawar.inventory.app.repository.MenuRepositoryCustom;
import com.pawar.inventory.app.repository.RoleRepository;
import com.pawar.todo.dto.UserDto;

@Service
@Transactional
public class MenuAccessServiceImpl implements MenuAccessService {

	private static final Logger logger = LoggerFactory.getLogger(MenuAccessServiceImpl.class);

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

	@Value("${jwt.secret}")
	private String jwtSecret;

	public MenuAccessServiceImpl() {
		httpClient = HttpClients.createDefault();
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}

	@Override
	public List<Menu> getAccessibleMenus(String jwtToken)
			throws JsonMappingException, JsonProcessingException, MenuNotFoundException {
		logger.info("Getting accessible menus for user: {}", getUserName(jwtToken));
		List<Menu> accessibleMenus = new ArrayList<>();

		List<Menu> allmenus = menuService.getAllMenus();
		logger.info("Fetched All menus successfully. Total menus: {}", allmenus.size());
		logger.debug("Fetched All menus successfully. All menus:{}", allmenus);
		String[] decodedToken = decodeToken(jwtToken);
		Set<Role> userRoles = getRoles(decodedToken);
		logger.info("Fetched totel User roles: {}", userRoles.size());
		logger.debug("Fetched User roles: {}", userRoles);

		for (Menu menu : allmenus) {
			if (hasAccess(menu, userRoles)) {
				accessibleMenus.add(menu);
			}
		}
		logger.info("Total Accessible menus: {}", accessibleMenus.size());
		logger.debug("Accessible menus: {}", accessibleMenus);
		return accessibleMenus;
	}

	public boolean hasAccess(Menu menu, Set<Role> userRoles) {
		logger.debug("Checking access for menu: {}", menu.getMenuName());

		if (userRoles == null || userRoles.isEmpty()) {
			return false;
		}

		int menuId = menu.getMenu_id();
		// Optimization: Collect all User Role IDs into a Set for faster lookup
		Set<Integer> userRoleIds = userRoles.stream()
				.map(Role::getRole_id)
				.collect(Collectors.toSet());

		// Fetch all access rules for this menu
		List<MenuAccess> menuAccesses = menuAccessRepository.findMenuAccessesByMenuId(menuId);

		for (MenuAccess access : menuAccesses) {
			if (userRoleIds.contains(access.getRoleId())) {
				logger.debug("Access GRANTED for menu: {} (Matched Role ID: {})",
						menu.getMenuName(), access.getRoleId());
				return true; // Match found, exit immediately with success
			}
		}

		logger.debug("Access DENIED for menu: {}. No matching roles found.", menu.getMenuName());
		return false; // Only return false after checking EVERY possibility
	}

	@Override
	public String[] decodeToken(String token) {
		DecodedJWT decodedJWT = JWT.decode(token);
		String[] decodedToken = decodedJWT.getSubject().split("\\|");
		return decodedToken;
	}

	public Set<Role> getRoles(String[] decodedToken) throws JsonMappingException, JsonProcessingException {

		Set<Role> userRoles = new HashSet<>();
		logger.info("decodedToken[2] : {}", decodedToken[2]);

		for (int i = 0; i < decodedToken.length - 1; i++) {
			if (decodedToken[i].contains("Role")) {
				String result = decodedToken[i].replaceAll("^\\[", "").replaceAll("\\]$", "");
				String json = "{" +
						"\"role_id\":" + result.substring(result.indexOf("id=") + 3, result.indexOf(", name")).trim()
						+ "," +
						"\"name\":\""
						+ result.substring(result.indexOf("name=") + 5, result.indexOf(", permissions")).trim() + "\","
						+
						"\"permissions\":[{\"id\":"
						+ result.substring(result.indexOf("id=") + 3, result.indexOf(", name")).trim() + "," +
						"\"name\":\""
						+ result.substring(result.indexOf("name=") + 5, result.indexOf(", createdDttm")).trim() + "\"}]"
						+
						"}";
				logger.info("result in loop : {}", json);

				Role role = mapper.readValue(json, Role.class);
				userRoles.add(role);
			}
		}
		return userRoles;
	}

	public String getUserName(String jwtToken) {
		DecodedJWT decodedJWT = JWT.decode(jwtToken);
		String[] decodedString = decodedJWT.getSubject().split("\\|");
		String user_name = decodedString[0];
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

		logger.info("Updated Role : {}", savedRole);

		logger.info("Menus {} assigned successfully to Role ID: {}", assignedMenu.getMenuName(), roleId);

	}

	@Override
	public void unassignMenusToRole(int menuId, Integer roleId)
			throws RoleNotFoundException, MenuNotFoundException, JsonProcessingException {

		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId));

		role.getMenus().removeIf(m -> m.getMenu_id() == menuId);
		Role savedRole = roleRepository.save(role);

		logger.info("Updated Role : {}", savedRole);

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

	@Override
	public List<MenuAccess> getMenuAccesses() throws MenuNotFoundException {
		List<MenuAccess> menuAccesses = menuAccessRepository.findAll();
		return menuAccesses;

	}
}
