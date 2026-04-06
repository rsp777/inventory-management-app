package com.pawar.inventory.app.service;

import java.util.List;
import java.util.Optional;

import com.pawar.inventory.app.model.Role;

public interface RoleService {

	List<Role> getAllRoles();

	Optional<Role> getRoleById(Integer id);

	Role addRole(String name);

	void deleteRole(Integer id);

	void assignPermission(Integer roleId, Integer permissionId);

	void unassignPermission(Integer roleId, Integer permissionId);
}
