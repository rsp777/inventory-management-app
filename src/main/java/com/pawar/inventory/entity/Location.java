package com.pawar.inventory.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

    @JsonProperty("locn_id")
    private Integer locnId;

    @JsonProperty("locn_brcd")
    private String locnBrcd;

    @JsonProperty("grp")
    private String grp;

    @JsonProperty("locn_class")
    private String locnClass;

    @JsonProperty("length")
    private Float length;

    @JsonProperty("width")
    private Float width;

    @JsonProperty("height")
    private Float height;

    @JsonProperty("max_volume")
    private Float maxVolume;

    @JsonProperty("max_qty")
    private Float maxQty;

    @JsonProperty("max_weight")
    private Float maxWeight;

    public Location() {
    }

    public Integer getLocnId() {
        return locnId;
    }

    public void setLocnId(Integer locnId) {
        this.locnId = locnId;
    }

    public Integer getLocn_id() {
        return locnId;
    }

    public void setLocn_id(Integer locnId) {
        this.locnId = locnId;
    }

    public Integer getLocationId() {
        return locnId;
    }

    public void setLocationId(Integer locationId) {
        this.locnId = locationId;
    }

    public Integer getId() {
        return locnId;
    }

    public void setId(Integer id) {
        this.locnId = id;
    }

    public String getLocnBrcd() {
        return locnBrcd;
    }

    public void setLocnBrcd(String locnBrcd) {
        this.locnBrcd = locnBrcd;
    }

    public String getLocn_brcd() {
        return locnBrcd;
    }

    public void setLocn_brcd(String locnBrcd) {
        this.locnBrcd = locnBrcd;
    }

    public String getGrp() {
        return grp;
    }

    public void setGrp(String grp) {
        this.grp = grp;
    }

    public String getLocnClass() {
        return locnClass;
    }

    public void setLocnClass(String locnClass) {
        this.locnClass = locnClass;
    }

    public String getLocn_class() {
        return locnClass;
    }

    public void setLocn_class(String locnClass) {
        this.locnClass = locnClass;
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
