package com.pawar.inventory.app.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PermissionDeleteEvent {
	private Integer permissionId;

	public PermissionDeleteEvent() {
	}
	
	public PermissionDeleteEvent(Integer permissionId) {
		this.permissionId = permissionId;
	}

	public Integer getPermissionId() {
		return permissionId;
	}
	
	@JsonProperty("permissionId")
    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

	@Override
	public String toString() {
		return "PermissionDeleteEvent [permissionId=" + permissionId + "]";
	}

}
