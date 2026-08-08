package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.management.relation.RoleNotFoundException;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.model.MenuAccess;
import com.pawar.inventory.app.model.Permission;
import com.pawar.inventory.app.model.Role;

import com.pawar.inventory.app.repository.MenuAccessRepository;
import com.pawar.inventory.app.repository.MenuRepository;
import com.pawar.inventory.app.repository.RoleRepository;
import com.pawar.inventory.app.service.base.ExternalApiService;
import com.pawar.inventory.app.service.base.TokenService;
import com.pawar.inventory.app.dto.UserDto;

@Service
@Transactional
public class MenuAccessServiceImpl implements MenuAccessService {

	private static final Logger logger = LoggerFactory.getLogger(MenuAccessServiceImpl.class);

	private final RoleRepository roleRepository;
	private final MenuRepository menuRepository;
	private final ExternalApiService externalApiService;
	private final MenuAccessRepository menuAccessRepository;
	private final TokenService tokenService;

	private final ObjectMapper mapper;

	public MenuAccessServiceImpl(RoleRepository roleRepository, MenuRepository menuRepository,
			ExternalApiService externalApiService, MenuAccessRepository menuAccessRepository,
			TokenService tokenService) {
		this.roleRepository = roleRepository;
		this.menuRepository = menuRepository;
		this.externalApiService = externalApiService;
		this.menuAccessRepository = menuAccessRepository;
		this.tokenService = tokenService;
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}

	@Override
	public List<Menu> getAccessibleMenus(String jwtToken)
			throws JsonMappingException, JsonProcessingException, MenuNotFoundException {
		logger.info("Getting accessible menus for user: {}", tokenService.getUserName(jwtToken));
		String[] decodedToken = tokenService.decodeToken(jwtToken);
		Set<Role> userRoles = getPersistedRoles(decodedToken);
		logger.info("Fetched total User roles: {}", userRoles.size());
		logger.debug("Fetched User roles: {}", userRoles);

		if (userRoles.isEmpty()) {
			return new ArrayList<>();
		}

		Set<Integer> userRoleIds = userRoles.stream()
				.map(Role::getRole_id)
				.collect(Collectors.toSet());

		List<MenuAccess> menuAccesses = menuAccessRepository.findByRoleIdIn(userRoleIds);
		Set<Integer> accessibleMenuIds = menuAccesses.stream()
				.map(MenuAccess::getMenuId)
				.collect(Collectors.toSet());

		List<Menu> accessibleMenus = menuRepository.findAllById(accessibleMenuIds);
		accessibleMenus.sort(Comparator.comparingInt(Menu::getMenu_id));
		logger.info("Accessible menu count for user: {}", accessibleMenus.size());
		logger.debug("Accessible menu IDs: {}", accessibleMenuIds);
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
		return tokenService.decodeToken(token);
	}

	public Set<Role> getRoles(String[] decodedToken) throws JsonMappingException, JsonProcessingException {

		Set<Role> userRoles = new HashSet<>();
		logger.info("decodedToken[2] : {}", decodedToken[2]);

		for (int i = 0; i < decodedToken.length; i++) {
			logger.info("decodedToken[{}] : {}", i, decodedToken[i]);
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
				logger.info("result in loop : {}", result);

				Role role = mapper.readValue(json, Role.class);
				userRoles.add(role);
			}
		}
		return userRoles;
	}

	public String getUserName(String jwtToken) {
		return tokenService.getUserName(jwtToken);
	}

	@Override
	public Set<String> getRoleNames(String jwtToken) {
		if (jwtToken == null || jwtToken.isBlank()) {
			return Set.of();
		}

		try {
			String[] decodedToken = tokenService.decodeToken(jwtToken);
			return getPersistedRoles(decodedToken).stream()
					.map(Role::getName)
					.filter(name -> name != null && !name.isBlank())
					.collect(Collectors.toCollection(LinkedHashSet::new));
		} catch (Exception exception) {
			logger.warn("Unable to extract role names from token", exception);
			return Set.of();
		}
	}

	@Override
	public Map<String, Boolean> getUiActions(String jwtToken) {
		Set<String> normalizedPermissions = getPermissionNames(jwtToken).stream()
				.map(permission -> permission.toLowerCase(Locale.ROOT))
				.collect(Collectors.toCollection(LinkedHashSet::new));

		boolean canAdminister = containsKeyword(normalizedPermissions,
				"admin", "role", "permission", "user.manage", "user.write", "settings.write");
		boolean canManageReferenceData = canAdminister || containsKeyword(normalizedPermissions,
				"category", "item", "location", "reference", "menu", "write", "edit", "delete", "create");
		boolean canManageOperations = canManageReferenceData || containsKeyword(normalizedPermissions,
				"lpn", "inventory", "putaway", "allocate", "deallocate", "warehouse");
		boolean canManageRuntime = canAdminister || containsKeyword(normalizedPermissions,
				"listener", "endpoint", "runtime", "toggle", "activate", "deactivate");

		Map<String, Boolean> uiActions = new LinkedHashMap<>();
		uiActions.put("manageCategories", canManageReferenceData);
		uiActions.put("manageItems", canManageReferenceData);
		uiActions.put("manageLocations", canManageReferenceData);
		uiActions.put("manageLpns", canManageOperations);
		uiActions.put("manageMenus", canAdminister || containsKeyword(normalizedPermissions, "menu"));
		uiActions.put("manageUsers", canAdminister);
		uiActions.put("manageSopConfig", canAdminister || containsKeyword(normalizedPermissions, "sop", "slotting", "batch"));
		uiActions.put("manageRuntime", canManageRuntime);
		uiActions.put("manageSettings", canAdminister);
		return uiActions;
	}

	// Regex patterns for extracting role names from token segments
	private static final Pattern ROLE_NAME_PATTERN = Pattern.compile("name=([A-Z][A-Z0-9_]*)");
	private static final Pattern PLAIN_ROLE_PATTERN = Pattern.compile("\\b([A-Z][A-Z0-9_]{2,})\\b");

	/**
	 * Resolves fully-loaded Role entities (with permissions) from the decoded token.
	 * Roles are in token segments starting at index 2.
	 * Strategy 1: extract via "name=ROLENAME" pattern (toString format).
	 * Strategy 2: fall back to plain UPPER_SNAKE_CASE word matching, each checked
	 * against the DB so only real role names match.
	 */
	private Set<Role> getPersistedRoles(String[] decodedToken) {
		if (decodedToken == null || decodedToken.length < 3) {
			logger.warn("Token does not contain a role segment (length={})",
					decodedToken == null ? 0 : decodedToken.length);
			return Set.of();
		}

		Set<Role> persistedRoles = new LinkedHashSet<>();

		for (int i = 1; i < decodedToken.length; i++) {
			String segment = decodedToken[i];
			if (segment == null || segment.isBlank()) continue;
			// logger.info("Processing token segment[{}]: {}", i, segment);
			// Strategy 1: parse "name=ROLENAME" present in toString() format
			Matcher nameMatcher = ROLE_NAME_PATTERN.matcher(segment);
			boolean foundAny = false;
			while (nameMatcher.find()) {
				String roleName = nameMatcher.group(1);
				roleRepository.findByName(roleName).ifPresent(r -> {
					persistedRoles.add(r);
					logger.info("Resolved role '{}' via name= pattern", r.getName());
				});
				foundAny = true;
			}

			// Strategy 2: plain UPPER_SNAKE_CASE tokens (e.g. "ADMIN" or "[ADMIN, OPERATIONS]")
			if (!foundAny) {
				Matcher plainMatcher = PLAIN_ROLE_PATTERN.matcher(segment);
				while (plainMatcher.find()) {
					String candidate = plainMatcher.group(1);
					roleRepository.findByName(candidate).ifPresent(r -> {
						persistedRoles.add(r);
						logger.info("Resolved role '{}' via plain pattern", r.getName());
					});
				}
			}
		}

		logger.info("Resolved {} persisted role(s) from token", persistedRoles.size());
		return persistedRoles;
	}

	private Set<String> getPermissionNames(String jwtToken) {
		if (jwtToken == null || jwtToken.isBlank()) {
			return Set.of();
		}

		try {
			String[] decodedToken = tokenService.decodeToken(jwtToken);
			return getPersistedRoles(decodedToken).stream()
					.map(Role::getPermissions)
					.filter(permissions -> permissions != null && !permissions.isEmpty())
					.flatMap(Set::stream)
					.map(Permission::getName)
					.filter(name -> name != null && !name.isBlank())
					.collect(Collectors.toCollection(LinkedHashSet::new));
		} catch (Exception exception) {
			logger.warn("Unable to extract permissions from token roles", exception);
			return Set.of();
		}
	}

	private boolean containsKeyword(Set<String> normalizedValues, String... keywords) {
		for (String value : normalizedValues) {
			for (String keyword : keywords) {
				if (value.contains(keyword)) {
					return true;
				}
			}
		}
		return false;
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
		ResponseEntity<String> response = externalApiService.callExternalApi(null, AppConstants.MenuEndpoint.GET_USERS,
				HttpMethod.GET, null);
		String json = response.getBody();
		if (json == null || json.isBlank()) {
			return List.of();
		}
		// logger.info(json);
		List<UserDto> fetchedUsers = mapper.readValue(json, new TypeReference<List<UserDto>>() {
		});

		return fetchedUsers;
	}

	@Override
	public List<MenuAccess> getMenuAccesses() throws MenuNotFoundException {
		List<MenuAccess> menuAccesses = menuAccessRepository.findAllByOrderByMenuIdAscRoleIdAsc();
		return menuAccesses;

	}
}
