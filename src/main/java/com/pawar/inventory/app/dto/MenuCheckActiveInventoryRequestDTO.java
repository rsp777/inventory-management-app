package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;

public class MenuCheckActiveInventoryRequestDTO {

	@NotBlank(message = "LPN name is required")
	private String lpn_name;

	public String getLpn_name() {
		return lpn_name;
	}

	public void setLpn_name(String lpn_name) {
		this.lpn_name = lpn_name;
	}
}