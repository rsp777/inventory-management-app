package com.pawar.inventory.app.dto;

public class RolePermissionsDto {

    private Integer roleId;
    private Integer permissionId;

    public RolePermissionsDto() {
    }

    public RolePermissionsDto(Integer roleId, Integer permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public String toString() {
        return "RolePermissionsDto [roleId=" + roleId + ", permissionId=" + permissionId + "]";
    }
}
