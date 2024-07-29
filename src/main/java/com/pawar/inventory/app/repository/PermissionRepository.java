package com.pawar.inventory.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.app.model.Permission;



@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
//	Optional<Permission> findByName(String name);
}
