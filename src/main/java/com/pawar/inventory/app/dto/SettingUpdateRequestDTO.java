package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SettingUpdateRequestDTO {

	@NotBlank(message = "Setting value is required")
	@Size(max = 1000, message = "Setting value must not exceed 1000 characters")
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}