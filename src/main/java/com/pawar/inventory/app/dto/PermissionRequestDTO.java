package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;

public class PermissionRequestDTO {

	@NotBlank(message = "Permission name is required")
	private String name;

	public PermissionRequestDTO() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
