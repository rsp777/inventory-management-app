package com.pawar.inventory.app.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.model.Permission;
import com.pawar.inventory.app.model.Role;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.service.PermissionService;
import com.pawar.inventory.app.service.RoleService;
import com.pawar.inventory.app.util.MenuFilterUtil;
import com.pawar.inventory.app.util.MenuFilterUtil.MenuCategories;
import com.pawar.inventory.app.util.ResponseUtil;
import com.pawar.inventory.app.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for Role and Permission management pages.
 *
 * Pages:
 *   GET  /api/roles                          – role list
 *   POST /api/roles/add                      – add role (Axios)
 *   DEL  /api/roles/{id}                     – delete role (Axios)
 *   GET  /api/permissions                    – permission list
 *   POST /api/permissions/add               – add permission (Axios)
 *   DEL  /api/permissions/{id}              – delete permission (Axios)
 *   GET  /api/role-permissions?roleId=      – assign perms to a role
 *   POST /api/role-permissions/assign       – assign (Axios)
 *   DEL  /api/role-permissions/unassign     – unassign (Axios)
 *   GET  /api/menu-access-management?roleId=– assign menus to a role
 *   POST /api/menu-access-management/assign – assign (Axios)
 *   DEL  /api/menu-access-management/unassign – unassign (Axios)
 */
@Controller
@RequestMapping("/api")
public class RoleManagementController {

	private static final Logger logger = LoggerFactory.getLogger(RoleManagementController.class);

	private final RoleService roleService;
	private final PermissionService permissionService;
	private final MenuAccessService menuAccessService;
	private final MenuService menuService;

	public RoleManagementController(RoleService roleService, PermissionService permissionService,
			MenuAccessService menuAccessService, MenuService menuService) {
		this.roleService = roleService;
		this.permissionService = permissionService;
		this.menuAccessService = menuAccessService;
		this.menuService = menuService;
	}

	// ──────────────────────────────────── ROLES ────────────────────────────────────

	@GetMapping("/roles")
	public String showRoles(Model model, HttpSession session) {
		try {
			buildNav(model, session, "/api/roles");
			model.addAttribute("roles", roleService.getAllRoles());
			return AppConstants.View.ROLES;
		} catch (Exception e) {
			logger.error("Error loading roles page", e);
			return handleError(model, e);
		}
	}

	@PostMapping("/roles/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addRole(@RequestParam String name) {
		try {
			Role role = roleService.addRole(name);
			return ResponseUtil.success(role, "Role created: " + role.getName());
		} catch (IllegalArgumentException e) {
			return ResponseUtil.error(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			logger.error("Error adding role", e);
			return ResponseUtil.error("Failed to add role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/roles/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Integer id) {
		try {
			roleService.deleteRole(id);
			return ResponseUtil.success(null, "Role deleted");
		} catch (IllegalArgumentException e) {
			return ResponseUtil.error(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Error deleting role id: {}", id, e);
			return ResponseUtil.error("Failed to delete role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/roles/all")
	@ResponseBody
	public ResponseEntity<List<Role>> getAllRoles() {
		return ResponseEntity.ok(roleService.getAllRoles());
	}

	// ──────────────────────────────── PERMISSIONS ──────────────────────────────────

	@GetMapping("/permissions")
	public String showPermissions(Model model, HttpSession session) {
		try {
			buildNav(model, session, "/api/permissions");
			model.addAttribute("permissions", permissionService.getAllPermissions());
			return AppConstants.View.PERMISSIONS;
		} catch (Exception e) {
			logger.error("Error loading permissions page", e);
			return handleError(model, e);
		}
	}

	@PostMapping("/permissions/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addPermission(@RequestParam String name) {
		try {
			Permission perm = permissionService.addPermission(name);
			return ResponseUtil.success(perm, "Permission created: " + perm.getName());
		} catch (IllegalArgumentException e) {
			return ResponseUtil.error(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			logger.error("Error adding permission", e);
			return ResponseUtil.error("Failed to add permission: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/permissions/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deletePermission(@PathVariable Integer id) {
		try {
			permissionService.deletePermission(id);
			return ResponseUtil.success(null, "Permission deleted");
		} catch (IllegalArgumentException e) {
			return ResponseUtil.error(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Error deleting permission id: {}", id, e);
			return ResponseUtil.error("Failed to delete permission: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/permissions/all")
	@ResponseBody
	public ResponseEntity<List<Permission>> getAllPermissions() {
		return ResponseEntity.ok(permissionService.getAllPermissions());
	}

	// ──────────────────────────────── ROLE-PERMISSIONS ─────────────────────────────

	@GetMapping("/role-permissions")
	public String showRolePermissions(@RequestParam(required = false) Integer roleId,
			Model model, HttpSession session) {
		try {
			buildNav(model, session, "/api/role-permissions");
			List<Role> allRoles = roleService.getAllRoles();
			model.addAttribute("allRoles", allRoles);
			model.addAttribute("selectedRoleId", roleId);

			if (roleId != null) {
				Optional<Role> opt = roleService.getRoleById(roleId);
				if (opt.isPresent()) {
					Role role = opt.get();
					Set<Integer> assignedIds = role.getPermissions().stream()
							.map(Permission::getId).collect(Collectors.toSet());
					List<Permission> assigned = role.getPermissions().stream()
							.sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
							.collect(Collectors.toList());
					List<Permission> available = permissionService.getAllPermissions().stream()
							.filter(p -> !assignedIds.contains(p.getId()))
							.sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
							.collect(Collectors.toList());
					model.addAttribute("selectedRole", role);
					model.addAttribute("assignedPermissions", assigned);
					model.addAttribute("availablePermissions", available);
				}
			} else {
				model.addAttribute("assignedPermissions", Collections.emptyList());
				model.addAttribute("availablePermissions", Collections.emptyList());
			}
			return AppConstants.View.ROLE_PERMISSIONS;
		} catch (Exception e) {
			logger.error("Error loading role-permissions page", e);
			return handleError(model, e);
		}
	}

	@PostMapping("/role-permissions/assign")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> assignPermissionToRole(
			@RequestParam Integer roleId, @RequestParam Integer permissionId) {
		try {
			roleService.assignPermission(roleId, permissionId);
			return ResponseUtil.success(null, "Permission assigned");
		} catch (IllegalArgumentException e) {
			return ResponseUtil.error(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Error assigning permission {} to role {}", permissionId, roleId, e);
			return ResponseUtil.error("Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/role-permissions/unassign")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> unassignPermissionFromRole(
			@RequestParam Integer roleId, @RequestParam Integer permissionId) {
		try {
			roleService.unassignPermission(roleId, permissionId);
			return ResponseUtil.success(null, "Permission removed");
		} catch (IllegalArgumentException e) {
			return ResponseUtil.error(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Error unassigning permission {} from role {}", permissionId, roleId, e);
			return ResponseUtil.error("Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ──────────────────────────────── MENU ACCESS ──────────────────────────────────

	@GetMapping("/menu-access-management")
	public String showMenuAccess(@RequestParam(required = false) Integer roleId,
			Model model, HttpSession session) {
		try {
			buildNav(model, session, "/api/menu-access-management");
			List<Role> allRoles = roleService.getAllRoles();
			List<Menu> allMenus = menuService.getAllMenus().stream()
					.sorted((a, b) -> a.getMenuName().compareToIgnoreCase(b.getMenuName()))
					.collect(Collectors.toList());
			model.addAttribute("allRoles", allRoles);
			model.addAttribute("selectedRoleId", roleId);

			if (roleId != null) {
				Optional<Role> opt = roleService.getRoleById(roleId);
				if (opt.isPresent()) {
					Role role = opt.get();
					Set<Integer> assignedMenuIds = role.getMenus().stream()
							.map(Menu::getMenu_id).collect(Collectors.toSet());
					List<Menu> assignedMenus = allMenus.stream()
							.filter(m -> assignedMenuIds.contains(m.getMenu_id()))
							.collect(Collectors.toList());
					List<Menu> availableMenus = allMenus.stream()
							.filter(m -> !assignedMenuIds.contains(m.getMenu_id()))
							.collect(Collectors.toList());
					model.addAttribute("selectedRole", role);
					model.addAttribute("assignedMenus", assignedMenus);
					model.addAttribute("availableMenus", availableMenus);
				}
			} else {
				model.addAttribute("assignedMenus", Collections.emptyList());
				model.addAttribute("availableMenus", Collections.emptyList());
			}
			return AppConstants.View.MENU_ACCESS_MGMT;
		} catch (Exception e) {
			logger.error("Error loading menu-access page", e);
			return handleError(model, e);
		}
	}

	@PostMapping("/menu-access-management/assign")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> assignMenuToRole(
			@RequestParam Integer roleId, @RequestParam Integer menuId) {
		try {
			menuAccessService.assignMenusToRole(menuId, roleId);
			return ResponseUtil.success(null, "Menu assigned");
		} catch (Exception e) {
			logger.error("Error assigning menu {} to role {}", menuId, roleId, e);
			return ResponseUtil.error("Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/menu-access-management/unassign")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> unassignMenuFromRole(
			@RequestParam Integer roleId, @RequestParam Integer menuId) {
		try {
			menuAccessService.unassignMenusToRole(menuId, roleId);
			return ResponseUtil.success(null, "Menu unassigned");
		} catch (Exception e) {
			logger.error("Error unassigning menu {} from role {}", menuId, roleId, e);
			return ResponseUtil.error("Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ────────────────────────────────── HELPERS ────────────────────────────────────

	/**
	 * Called from Thymeleaf via ${@roleManagementController.describeCapability(perm.name)}.
	 * Returns a human-readable description of which UI capability the permission name will activate.
	 */
	public String describeCapability(String permissionName) {
		if (permissionName == null || permissionName.isBlank()) {
			return "—";
		}
		String lower = permissionName.toLowerCase(java.util.Locale.ROOT);
		if (containsAny(lower, "admin", "role", "permission", "user.manage", "user.write", "settings.write")) {
			return "Admin: users, roles, permissions & settings";
		}
		if (containsAny(lower, "sop", "slotting", "batch")) {
			return "SOP configuration & batch operations";
		}
		if (containsAny(lower, "listener", "endpoint", "runtime", "toggle", "activate", "deactivate")) {
			return "Runtime / endpoint management";
		}
		if (containsAny(lower, "menu")) {
			return "Menu management";
		}
		if (containsAny(lower, "lpn", "putaway", "allocate", "deallocate", "warehouse")) {
			return "LPN & warehouse operations";
		}
		if (containsAny(lower, "inventory")) {
			return "Inventory queries";
		}
		if (containsAny(lower, "category", "item", "location", "reference", "write", "edit", "delete", "create")) {
			return "Reference data (categories, items, locations)";
		}
		return "—";
	}

	private boolean containsAny(String value, String... keywords) {
		for (String kw : keywords) {
			if (value.contains(kw)) {
				return true;
			}
		}
		return false;
	}

	private void buildNav(Model model, HttpSession session, String currentUri)
			throws JsonProcessingException, MenuNotFoundException {
		String jwtToken = SessionUtil.getSessionToken(session);
		List<Menu> accessible = menuAccessService.getAccessibleMenus(jwtToken);
		MenuCategories categories = MenuFilterUtil.categorizeMenus(accessible);
		ResponseUtil.addViewAttributes(model, categories, currentUri);
	}

	private String handleError(Model model, Exception e) {
		model.addAttribute("error", e.getMessage());
		return AppConstants.View.ERROR;
	}
}
