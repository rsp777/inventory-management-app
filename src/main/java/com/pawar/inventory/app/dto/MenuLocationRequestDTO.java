package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MenuLocationRequestDTO {

	@NotBlank(message = "Location barcode is required")
	@Size(max = 255, message = "Location barcode must not exceed 255 characters")
	private String locn_brcd;

	@NotBlank(message = "Group is required")
	@Size(max = 100, message = "Group must not exceed 100 characters")
	private String grp;

	@NotBlank(message = "Location class is required")
	@Size(max = 100, message = "Location class must not exceed 100 characters")
	private String locn_class;

	@NotNull(message = "Length is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Length must be zero or greater")
	private Float length;

	@NotNull(message = "Width is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Width must be zero or greater")
	private Float width;

	@NotNull(message = "Height is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Height must be zero or greater")
	private Float height;

	@NotNull(message = "Max volume is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Max volume must be zero or greater")
	private Float max_vol;

	@NotNull(message = "Max quantity is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Max quantity must be zero or greater")
	private Float max_qty;

	@NotNull(message = "Max weight is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Max weight must be zero or greater")
	private Float max_weight;

	public String getLocn_brcd() {
		return locn_brcd;
	}

	public void setLocn_brcd(String locn_brcd) {
		this.locn_brcd = locn_brcd;
	}

	public String getGrp() {
		return grp;
	}

	public void setGrp(String grp) {
		this.grp = grp;
	}

	public String getLocn_class() {
		return locn_class;
	}

	public void setLocn_class(String locn_class) {
		this.locn_class = locn_class;
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

	public Float getMax_vol() {
		return max_vol;
	}

	public void setMax_vol(Float max_vol) {
		this.max_vol = max_vol;
	}

	public Float getMax_qty() {
		return max_qty;
	}

	public void setMax_qty(Float max_qty) {
		this.max_qty = max_qty;
	}

	public Float getMax_weight() {
		return max_weight;
	}

	public void setMax_weight(Float max_weight) {
		this.max_weight = max_weight;
	}
}