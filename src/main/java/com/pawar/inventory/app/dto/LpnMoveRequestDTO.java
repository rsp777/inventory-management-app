package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LpnMoveRequestDTO {

	@NotBlank(message = "Location code is required")
	@Size(max = 255, message = "Location code must not exceed 255 characters")
	private String locationCode;

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
}