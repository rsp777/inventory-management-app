package com.pawar.inventory.app.controller;

import java.util.List;

import javax.management.relation.RoleNotFoundException;

import org.jboss.logging.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pawar.inventory.app.exception.MenuAssignmentException;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.model.MenuAccess;
import com.pawar.inventory.app.service.MenuAccessService;

@RestController
@RequestMapping("/inventory-ui-rest")
public class MenuAccessController {

	private static final Logger logger = Logger.getLogger(MenuAccessController.class);

	@Autowired
	private MenuAccessService menuAccessService;

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/getAccessibleMenus")
	public ResponseEntity<List<Menu>> getAccessibleMenus(@RequestHeader(value = "Authorization") String jwtToken) {
		try {
			List<Menu> menu = menuAccessService.getAccessibleMenus(jwtToken);
			logger.info("Accessible Menus retrieved successfully");
			return ResponseEntity.ok(menu);
		} catch (MenuNotFoundException e) {
			logger.error("Menu not found");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Failed to retrieve Menus", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User retrieval failed", e);
		}
	}

	@PostMapping("/assign/menus/{menuId}/roles/{roleId}")
	public ResponseEntity<?> assignMenusToRole(@PathVariable int menuId, @PathVariable Integer roleId) {
		try {
			try {

				menuAccessService.assignMenusToRole(menuId, roleId);
			} catch (RoleNotFoundException e) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found : {}" + e.getMessage());
			} catch (MenuNotFoundException e) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu not found : {}" + e.getMessage());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
						.body("Exception in Json Processing : {}" + e.getMessage());
			}
			logger.infof("Menus assigned successfully to Role ID: %d", roleId);
			return ResponseEntity.ok("Menus assigned successfully to Role ");

		} catch (IllegalArgumentException e) {

			logger.errorf("Error assigning Menu: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (MenuAssignmentException e) {

			logger.errorf("Menu assignment failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Menu assignment failed.");
		}
	}

	@DeleteMapping("/unassign/menus/{menuId}/roles/{roleId}")
	public ResponseEntity<?> unassignMenusToRole(@PathVariable int menuId, @PathVariable Integer roleId) {
		try {
			try {

				menuAccessService.unassignMenusToRole(menuId, roleId);
			} catch (RoleNotFoundException e) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found : {}" + e.getMessage());
			} catch (MenuNotFoundException e) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu not found : {}" + e.getMessage());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
						.body("Exception in Json Processing : {}" + e.getMessage());
			}
			logger.infof("Menus unassigned successfully to Role ID: {}", roleId);
			return ResponseEntity.ok("Menus unassigned successfully to Role ");

		} catch (IllegalArgumentException e) {

			logger.errorf("Error unassigning Menu: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (MenuAssignmentException e) {

			logger.errorf("Menu unassignment failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Menu unassignment failed.");
		}
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/getMenuAccesses")
	public ResponseEntity<List<MenuAccess>> getMenuAccesses() {
		try {
			List<MenuAccess> menu = menuAccessService.getMenuAccesses();
			logger.info("Menus Accesses retrieved successfully");
			return ResponseEntity.ok(menu);
		} catch (MenuNotFoundException e) {
			logger.error("Menu not found");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Failed to retrieve Menus Accesses", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User retrieval failed", e);
		}
	}


}
