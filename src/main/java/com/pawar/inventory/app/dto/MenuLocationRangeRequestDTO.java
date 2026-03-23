package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;

public class MenuLocationRangeRequestDTO {

	private String id;

	@NotBlank(message = "Action type is required")
	private String actionType;

	@NotBlank(message = "Category is required")
	private String category;

	@NotBlank(message = "From location is required")
	private String fromLocation;

	@NotBlank(message = "To location is required")
	private String toLocation;

	@NotBlank(message = "isActive is required")
	private String isActive;

	@NotBlank(message = "Active tab is required")
	private String activeTab;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}
}