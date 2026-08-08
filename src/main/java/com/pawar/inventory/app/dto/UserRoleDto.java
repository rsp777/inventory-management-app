package com.pawar.inventory.app.dto;

public class UserRoleDto {

    private Long userId;
    private Integer roleId;

    public UserRoleDto() {
    }

    public UserRoleDto(Long userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "UserRoleDto [userId=" + userId + ", roleId=" + roleId + "]";
    }
}
