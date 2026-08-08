package com.pawar.inventory.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Inventory {

    @JsonProperty("inventory_id")
    private Integer inventoryId;

    @JsonProperty("item_id")
    private Integer itemId;

    @JsonProperty("location_id")
    private Integer locationId;

    @JsonProperty("lpn_id")
    private Integer lpnId;

    @JsonProperty("quantity")
    private Integer quantity;

    private Item item;

    private Location location;

    private Lpn lpn;

    public Inventory() {
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getInventory_id() {
        return inventoryId;
    }

    public void setInventory_id(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getId() {
        return inventoryId;
    }

    public void setId(Integer id) {
        this.inventoryId = id;
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

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getLocation_id() {
        return locationId;
    }

    public void setLocation_id(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getLpnId() {
        return lpnId;
    }

    public void setLpnId(Integer lpnId) {
        this.lpnId = lpnId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Lpn getLpn() {
        return lpn;
    }

    public void setLpn(Lpn lpn) {
        this.lpn = lpn;
    }
}
