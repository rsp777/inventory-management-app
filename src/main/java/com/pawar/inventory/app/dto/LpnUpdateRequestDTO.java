package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LpnUpdateRequestDTO {

	@Size(max = 255, message = "Item description must not exceed 255 characters")
	private String itemDesc;

	@NotNull(message = "Length is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Length must be zero or greater")
	private Float length = 0f;

	@NotNull(message = "Width is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Width must be zero or greater")
	private Float width = 0f;

	@NotNull(message = "Height is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Height must be zero or greater")
	private Float height = 0f;

	@Min(value = 0, message = "Quantity must be zero or greater")
	private int quantity = 1;

	@Min(value = 0, message = "Adjust quantity must be zero or greater")
	private int adjustQty = 0;

	@Min(value = 0, message = "Facility status must be zero or greater")
	private int lpnFacilityStatus = 0;

	@NotNull(message = "Volume is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Volume must be zero or greater")
	private Float volume = 0f;

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
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

	public int getLpnFacilityStatus() {
		return lpnFacilityStatus;
	}

	public void setLpnFacilityStatus(int lpnFacilityStatus) {
		this.lpnFacilityStatus = lpnFacilityStatus;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}
}