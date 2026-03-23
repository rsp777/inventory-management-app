package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;

public class MenuReserveLpnRequestDTO {

	@NotBlank(message = "LPN name is required")
	private String lpn_name;

	@NotBlank(message = "Reserve location is required")
	private String resv_locn;

	public String getLpn_name() {
		return lpn_name;
	}

	public void setLpn_name(String lpn_name) {
		this.lpn_name = lpn_name;
	}

	public String getResv_locn() {
		return resv_locn;
	}

	public void setResv_locn(String resv_locn) {
		this.resv_locn = resv_locn;
	}
}