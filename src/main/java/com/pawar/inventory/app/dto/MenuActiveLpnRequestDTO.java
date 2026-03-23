package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;

public class MenuActiveLpnRequestDTO {

	@NotBlank(message = "LPN name is required")
	private String lpn_name;

	@NotBlank(message = "Active location is required")
	private String active_locn;

	public String getLpn_name() {
		return lpn_name;
	}

	public void setLpn_name(String lpn_name) {
		this.lpn_name = lpn_name;
	}

	public String getActive_locn() {
		return active_locn;
	}

	public void setActive_locn(String active_locn) {
		this.active_locn = active_locn;
	}
}