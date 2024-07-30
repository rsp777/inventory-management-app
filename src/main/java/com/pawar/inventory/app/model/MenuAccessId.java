package com.pawar.inventory.app.model;

import java.io.Serializable;
import java.util.Objects;

public class MenuAccessId implements Serializable {
	private Integer roleId;
	private int menuId;

	public MenuAccessId() {
		// TODO Auto-generated constructor stub
	}

	public MenuAccessId(Integer roleId, int menuId) {

		this.menuId = menuId;
		this.roleId = roleId;
	}

	public int getMenuId() {
		return menuId;
	}

	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleId, menuId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuAccessId other = (MenuAccessId) obj;
		return Objects.equals(roleId, other.roleId) && Objects.equals(menuId, other.menuId);
	}

	@Override
	public String toString() {
		return "MenuAccessId [roleId=" + roleId + ", menuId=" + menuId + "]";
	}

}