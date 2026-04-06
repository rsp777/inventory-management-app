package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MenuDefinitionRequestDTO {

	@NotBlank(message = "Protocol is required")
	@Size(max = 20, message = "Protocol must not exceed 20 characters")
	private String newProtocol;

	@NotBlank(message = "Menu name is required")
	@Size(max = 255, message = "Menu name must not exceed 255 characters")
	private String newMenuName;

	@NotBlank(message = "Menu link is required")
	@Size(max = 1000, message = "Menu link must not exceed 1000 characters")
	private String newMenuLink;

	@NotBlank(message = "Hostname is required")
	@Size(max = 255, message = "Hostname must not exceed 255 characters")
	private String newHostname;

	@NotBlank(message = "Menu type is required")
	@Size(max = 100, message = "Menu type must not exceed 100 characters")
	private String newMenuType;

	private String newParentMenuName;

	public String getNewProtocol() {
		return newProtocol;
	}

	public void setNewProtocol(String newProtocol) {
		this.newProtocol = newProtocol;
	}

	public String getNewMenuName() {
		return newMenuName;
	}

	public void setNewMenuName(String newMenuName) {
		this.newMenuName = newMenuName;
	}

	public String getNewMenuLink() {
		return newMenuLink;
	}

	public void setNewMenuLink(String newMenuLink) {
		this.newMenuLink = newMenuLink;
	}

	public String getNewHostname() {
		return newHostname;
	}

	public void setNewHostname(String newHostname) {
		this.newHostname = newHostname;
	}

	public String getNewMenuType() {
		return newMenuType;
	}

	public void setNewMenuType(String newMenuType) {
		this.newMenuType = newMenuType;
	}

	public String getNewParentMenuName() {
		return newParentMenuName;
	}

	public void setNewParentMenuName(String newParentMenuName) {
		this.newParentMenuName = newParentMenuName;
	}
}