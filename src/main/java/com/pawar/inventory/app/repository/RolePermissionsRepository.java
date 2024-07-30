package com.pawar.inventory.app.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.app.model.RolePermissions;
import com.pawar.inventory.app.model.RolePermissionsId;



@Repository
public interface RolePermissionsRepository extends CrudRepository<RolePermissions, RolePermissionsId> {

	@Query("SELECT rp FROM RolePermissions rp WHERE rp.roleId = :roleId and rp.permissionId = :permissionId")
	RolePermissions findRolesPermissionById(@Param("roleId") Integer roleId, @Param("permissionId") Long permissionId);

}
