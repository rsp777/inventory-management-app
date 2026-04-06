package com.pawar.inventory.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequestDTO {

	@JsonProperty("category_name")
	@NotBlank(message = "Category name is required")
	@Size(max = 255, message = "Category name must not exceed 255 characters")
	private String categoryName;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
}