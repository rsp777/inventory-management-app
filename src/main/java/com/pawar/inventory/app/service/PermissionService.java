package com.pawar.inventory.app.service;

import java.util.List;
import java.util.Optional;

import com.pawar.inventory.app.model.Permission;

public interface PermissionService {

	List<Permission> getAllPermissions();

	Optional<Permission> getById(Integer id);

	Permission addPermission(String name);

	void deletePermission(Integer id);
}
