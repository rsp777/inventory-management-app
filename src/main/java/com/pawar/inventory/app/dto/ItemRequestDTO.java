package com.pawar.inventory.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ItemRequestDTO {

	@JsonProperty("item_id")
	private Integer itemId;

	@NotBlank(message = "Description is required")
	@Size(max = 255, message = "Description must not exceed 255 characters")
	private String description;

	@NotBlank(message = "Category is required")
	@Size(max = 255, message = "Category must not exceed 255 characters")
	private String category;

	@NotNull(message = "Length is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Length must be zero or greater")
	private Float length;

	@NotNull(message = "Width is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Width must be zero or greater")
	private Float width;

	@NotNull(message = "Height is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Height must be zero or greater")
	private Float height;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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
}