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
import com.pawar.inventory.app.repository.PermissionRepository;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

	private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

	private final PermissionRepository permissionRepository;

	public PermissionServiceImpl(PermissionRepository permissionRepository) {
		this.permissionRepository = permissionRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Permission> getAllPermissions() {
		return permissionRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Permission> getById(Integer id) {
		return permissionRepository.findById(id);
	}

	@Override
	public Permission addPermission(String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Permission name cannot be blank");
		}
		String trimmed = name.trim();
		if (permissionRepository.findByName(trimmed).isPresent()) {
			throw new IllegalArgumentException("Permission already exists: " + trimmed);
		}
		Permission p = new Permission();
		p.setName(trimmed);
		p.setCreatedDttm(LocalDateTime.now());
		p.setLastUpdatedDttm(LocalDateTime.now());
		p.setCreatedSource(AppConstants.Application.AUDIT_SOURCE_SYSTEM);
		p.setLastUpdatedSource(AppConstants.Application.AUDIT_SOURCE_SYSTEM);
		Permission saved = permissionRepository.save(p);
		logger.info("Created permission: {}", saved.getName());
		return saved;
	}

	@Override
	public void deletePermission(Integer id) {
		if (!permissionRepository.existsById(id)) {
			throw new IllegalArgumentException("Permission not found: " + id);
		}
		permissionRepository.deleteById(id);
		logger.info("Deleted permission id: {}", id);
	}
}
