package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class LpnCreateRequestDTO {

	@NotBlank(message = "LPN number is required")
	@Size(max = 255, message = "LPN number must not exceed 255 characters")
	private String lpnNumber;

	@NotBlank(message = "Item name is required")
	@Size(max = 255, message = "Item name must not exceed 255 characters")
	private String itemName = "Default Item";

	@Positive(message = "Quantity must be greater than zero")
	private int quantity = 1;

	public String getLpnNumber() {
		return lpnNumber;
	}

	public void setLpnNumber(String lpnNumber) {
		this.lpnNumber = lpnNumber;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}