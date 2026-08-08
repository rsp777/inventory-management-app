package com.pawar.inventory.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category {

    @JsonProperty("category_id")
    private Integer categoryId;

    @JsonProperty("category_name")
    private String categoryName;

    public Category() {
    }

    public Category(Integer categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategory_id() {
        return categoryId;
    }

    public void setCategory_id(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategory_name() {
        return categoryName;
    }

    public void setCategory_name(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "Category{" + "categoryId=" + categoryId + ", categoryName='" + categoryName + '\'' + '}';
    }
}
