package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MenuLpnEditRequestDTO {

	@NotBlank(message = "LPN name is required")
	@Size(max = 255, message = "LPN name must not exceed 255 characters")
	private String lpn_name;

	@NotBlank(message = "Item description is required")
	@Size(max = 255, message = "Item description must not exceed 255 characters")
	private String item_desc;

	@NotNull(message = "Length is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Length must be zero or greater")
	private Float length;

	@NotNull(message = "Width is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Width must be zero or greater")
	private Float width;

	@NotNull(message = "Height is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Height must be zero or greater")
	private Float height;

	@Min(value = 0, message = "Quantity must be zero or greater")
	private int quantity;

	@Min(value = 0, message = "Adjust quantity must be zero or greater")
	private int adjustQty;

	@Min(value = 0, message = "Facility status must be zero or greater")
	private int lpn_facility_status;

	@NotNull(message = "Volume is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Volume must be zero or greater")
	private Float volume;

	public String getLpn_name() {
		return lpn_name;
	}

	public void setLpn_name(String lpn_name) {
		this.lpn_name = lpn_name;
	}

	public String getItem_desc() {
		return item_desc;
	}

	public void setItem_desc(String item_desc) {
		this.item_desc = item_desc;
	}

	public Float getLength() {
		return length;
	}

	public void setLength(Float length) {
		this.length = length;
	}

	public Float getWidth() {
		return width;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getAdjustQty() {
		return adjustQty;
	}

	public void setAdjustQty(int adjustQty) {
		this.adjustQty = adjustQty;
	}

	public int getLpn_facility_status() {
		return lpn_facility_status;
	}

	public void setLpn_facility_status(int lpn_facility_status) {
		this.lpn_facility_status = lpn_facility_status;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}
}