package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LocationRequestDTO {

	private Integer id;

	@NotBlank(message = "Location code is required")
	@Size(max = 255, message = "Location code must not exceed 255 characters")
	private String locationCode;

	@NotBlank(message = "Location name is required")
	@Size(max = 255, message = "Location name must not exceed 255 characters")
	private String locationName;

	@Size(max = 100, message = "Group must not exceed 100 characters")
	private String grp = "DEFAULT";

	@Size(max = 100, message = "Location class must not exceed 100 characters")
	private String locationClass = "DEFAULT";

	@NotNull(message = "Length is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Length must be zero or greater")
	private Float length = 0f;

	@NotNull(message = "Width is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Width must be zero or greater")
	private Float width = 0f;

	@NotNull(message = "Height is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Height must be zero or greater")
	private Float height = 0f;

	@NotNull(message = "Max volume is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Max volume must be zero or greater")
	private Float maxVolume = 0f;

	@NotNull(message = "Max quantity is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Max quantity must be zero or greater")
	private Float maxQty = 0f;

	@NotNull(message = "Max weight is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Max weight must be zero or greater")
	private Float maxWeight = 0f;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getGrp() {
		return grp;
	}

	public void setGrp(String grp) {
		this.grp = grp;
	}

	public String getLocationClass() {
		return locationClass;
	}

	public void setLocationClass(String locationClass) {
		this.locationClass = locationClass;
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

	public Float getMaxVolume() {
		return maxVolume;
	}

	public void setMaxVolume(Float maxVolume) {
		this.maxVolume = maxVolume;
	}

	public Float getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(Float maxQty) {
		this.maxQty = maxQty;
	}

	public Float getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(Float maxWeight) {
		this.maxWeight = maxWeight;
	}
}