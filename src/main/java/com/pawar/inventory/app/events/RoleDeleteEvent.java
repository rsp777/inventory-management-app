package com.pawar.inventory.app.events;

public class RoleDeleteEvent {
	private Integer roleId;

	public RoleDeleteEvent() {
	}

	public RoleDeleteEvent(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	@Override
	public String toString() {
		return "RoleDeleteEvent [roleId=" + roleId + "]";
	}

}
