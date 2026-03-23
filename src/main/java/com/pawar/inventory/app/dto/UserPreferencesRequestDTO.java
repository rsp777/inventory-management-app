package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPreferencesRequestDTO {

	@NotBlank(message = "Theme is required")
	@Size(max = 50, message = "Theme must not exceed 50 characters")
	private String theme;

	@NotBlank(message = "Language is required")
	@Size(max = 20, message = "Language must not exceed 20 characters")
	private String language;

	@NotBlank(message = "Timezone is required")
	@Size(max = 100, message = "Timezone must not exceed 100 characters")
	private String timezone;

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
}