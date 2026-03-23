package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;

public class MenuEligibleUpcsRequestDTO {

	@NotBlank(message = "Category is required")
	private String category;

	@NotBlank(message = "Active tab is required")
	private String activeTab;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}
}