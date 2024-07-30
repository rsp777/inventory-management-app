package com.pawar.inventory.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles_permissions")
@IdClass(RolePermissionsId.class)
public class RolePermissions {

	@Id
	@Column(name = "role_id")
	private Integer roleId;

	@Id
	@Column(name = "permission_id")
	private Integer permissionId;

	public RolePermissions() {
		// TODO Auto-generated constructor stub
	}

	public RolePermissions(Integer roleId, Integer permissionId) {
		super();
		this.roleId = roleId;
		this.permissionId = permissionId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Integer permissionId) {
		this.permissionId = permissionId;
	}

	@Override
	public String toString() {
		return "RolePermissions [roleId=" + roleId + ", permissionId=" + permissionId + "]";
	}

}
