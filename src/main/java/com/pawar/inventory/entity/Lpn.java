package com.pawar.inventory.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Lpn {

    @JsonProperty("lpn_id")
    private Integer lpnId;

    @JsonProperty("lpn_name")
    private String lpnName;

    @JsonProperty("lpn_number")
    private String lpnNumber;

    @JsonProperty("item_desc")
    private String itemDesc;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("adjust_qty")
    private Integer adjustQty;

    @JsonProperty("lpn_facility_status")
    private Integer lpnFacilityStatus;

    @JsonProperty("volume")
    private Float volume;

    public Lpn() {
    }

    public Integer getLpnId() {
        return lpnId;
    }

    public void setLpnId(Integer lpnId) {
        this.lpnId = lpnId;
    }

    public Integer getLpn_id() {
        return lpnId;
    }

    public void setLpn_id(Integer lpnId) {
        this.lpnId = lpnId;
    }

    public Integer getId() {
        return lpnId;
    }

    public void setId(Integer id) {
        this.lpnId = id;
    }

    public String getLpnName() {
        return lpnName;
    }

    public void setLpnName(String lpnName) {
        this.lpnName = lpnName;
    }

    public String getLpn_name() {
        return lpnName;
    }

    public void setLpn_name(String lpnName) {
        this.lpnName = lpnName;
    }

    public String getLpnNumber() {
        return lpnNumber;
    }

    public void setLpnNumber(String lpnNumber) {
        this.lpnNumber = lpnNumber;
    }

    public String getLpnNumberValue() {
        return lpnNumber;
    }

    public void setLpnNumberValue(String lpnNumber) {
        this.lpnNumber = lpnNumber;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAdjustQty() {
        return adjustQty;
    }

    public void setAdjustQty(Integer adjustQty) {
        this.adjustQty = adjustQty;
    }

    public Integer getLpnFacilityStatus() {
        return lpnFacilityStatus;
    }

    public void setLpnFacilityStatus(Integer lpnFacilityStatus) {
        this.lpnFacilityStatus = lpnFacilityStatus;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }
}
