package com.pawar.inventory.app.model;

import java.io.Serializable;
import java.util.Objects;

public class RolePermissionsId implements Serializable {
	private Integer roleId;
	private Integer permissionId;

	public RolePermissionsId() {
	}

	public RolePermissionsId(Integer roleId, Integer permissionId) {
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
	public int hashCode() {
		return Objects.hash(roleId, permissionId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RolePermissionsId other = (RolePermissionsId) obj;
		return Objects.equals(roleId, other.roleId) && Objects.equals(permissionId, other.permissionId);
	}

	@Override
	public String toString() {
		return "RolePermissionsId [roleId=" + roleId + ", permissionId=" + permissionId + "]";
	}

}
