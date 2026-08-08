package com.pawar.inventory.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {

    @JsonProperty("item_id")
    private Integer itemId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category")
    private Category category;

    @JsonProperty("unit_length")
    private Float unitLength;

    @JsonProperty("unit_width")
    private Float unitWidth;

    @JsonProperty("unit_height")
    private Float unitHeight;

    public Item() {
    }

    public Item(Integer itemId, String description) {
        this.itemId = itemId;
        this.description = description;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getItem_id() {
        return itemId;
    }

    public void setItem_id(Integer itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Float getUnitLength() {
        return unitLength;
    }

    public void setUnitLength(Float unitLength) {
        this.unitLength = unitLength;
    }

    public Float getUnit_width() {
        return unitLength;
    }

    public void setUnit_width(Float unitLength) {
        this.unitLength = unitLength;
    }

    public Float getUnitWidth() {
        return unitWidth;
    }

    public void setUnitWidth(Float unitWidth) {
        this.unitWidth = unitWidth;
    }

    public Float getUnit_height() {
        return unitHeight;
    }

    public void setUnit_height(Float unitHeight) {
        this.unitHeight = unitHeight;
    }

    public Float getUnitHeight() {
        return unitHeight;
    }

    public void setUnitHeight(Float unitHeight) {
        this.unitHeight = unitHeight;
    }

    @Override
    public String toString() {
        return "Item{" + "itemId=" + itemId + ", description='" + description + '\'' + '}';
    }
}
