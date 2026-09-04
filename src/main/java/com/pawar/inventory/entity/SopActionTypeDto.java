package com.pawar.inventory.entity;

public class SopActionTypeDto {
    private Integer sopActionTypeId;
    private String actionType;
    private String description;

    public SopActionTypeDto() {
    }

    public Integer getSopActionTypeId() {
        return sopActionTypeId;
    }

    public void setSopActionTypeId(Integer sopActionTypeId) {
        this.sopActionTypeId = sopActionTypeId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
