package com.pawar.inventory.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.Permission;
import com.pawar.inventory.app.model.Role;
import com.pawar.inventory.app.repository.PermissionRepository;
import com.pawar.inventory.app.repository.RoleRepository;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Role> getRoleById(Integer id) {
		return roleRepository.findById(id);
	}

	@Override
	public Role addRole(String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Role name cannot be blank");
		}
		String trimmed = name.trim();
		if (roleRepository.findByName(trimmed).isPresent()) {
			throw new IllegalArgumentException("Role already exists: " + trimmed);
		}
		Role role = new Role();
		role.setName(trimmed);
		role.setCreatedDttm(LocalDateTime.now());
		role.setLastUpdatedDttm(LocalDateTime.now());
		role.setCreatedSource(AppConstants.Application.AUDIT_SOURCE_SYSTEM);
		role.setLastUpdatedSource(AppConstants.Application.AUDIT_SOURCE_SYSTEM);
		Role saved = roleRepository.save(role);
		logger.info("Created role: {}", saved.getName());
		return saved;
	}

	@Override
	public void deleteRole(Integer id) {
		Role role = roleRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));
		roleRepository.delete(role);
		logger.info("Deleted role id: {}", id);
	}

	@Override
	public void assignPermission(Integer roleId, Integer permissionId) {
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
		Permission permission = permissionRepository.findById(permissionId)
				.orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionId));
		role.getPermissions().add(permission);
		roleRepository.save(role);
		logger.info("Assigned permission {} to role {}", permissionId, roleId);
	}

	@Override
	public void unassignPermission(Integer roleId, Integer permissionId) {
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
		role.getPermissions().removeIf(p -> p.getId().equals(permissionId));
		roleRepository.save(role);
		logger.info("Unassigned permission {} from role {}", permissionId, roleId);
	}
}
