package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleRequestDTO {

	@NotBlank(message = "Role name is required")
	private String name;

	public RoleRequestDTO() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
