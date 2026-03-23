package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;

public class MenuBatchRequestDTO {

	@NotBlank(message = "Action type is required")
	private String actionType;

	@NotBlank(message = "Category name is required")
	private String category_name;

	@NotBlank(message = "Active tab is required")
	private String activeTab;

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}
}