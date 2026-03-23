package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MenuLpnCreateRequestDTO {

	@NotBlank(message = "LPN name is required")
	@Size(max = 255, message = "LPN name must not exceed 255 characters")
	private String lpn_name;

	@NotBlank(message = "Item name is required")
	@Size(max = 255, message = "Item name must not exceed 255 characters")
	private String item_name;

	@Positive(message = "Quantity must be greater than zero")
	private int quantity;

	public String getLpn_name() {
		return lpn_name;
	}

	public void setLpn_name(String lpn_name) {
		this.lpn_name = lpn_name;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}