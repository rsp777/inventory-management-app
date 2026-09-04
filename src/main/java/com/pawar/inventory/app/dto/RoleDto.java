package com.pawar.inventory.app.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class RoleDto {

    private Integer role_id;
    private String name;
    private Set<PermissionDto> permissions = new HashSet<>();
    private LocalDateTime createdDttm;
    private LocalDateTime lastUpdatedDttm;
    private String createdSource;
    private String lastUpdatedSource;

    public RoleDto() {
    }

    public RoleDto(String name) {
        this.name = name;
    }

    public RoleDto(Integer role_id, String name) {
        this.role_id = role_id;
        this.name = name;
    }

    public RoleDto(Integer role_id, String name, Set<PermissionDto> permissions) {
        this.role_id = role_id;
        this.name = name;
        this.permissions = permissions != null ? permissions : new HashSet<>();
    }

    public RoleDto(Integer role_id, String name, Set<PermissionDto> permissions, LocalDateTime createdDttm,
                   LocalDateTime lastUpdatedDttm, String createdSource, String lastUpdatedSource) {
        this.role_id = role_id;
        this.name = name;
        this.permissions = permissions != null ? permissions : new HashSet<>();
        this.createdDttm = createdDttm;
        this.lastUpdatedDttm = lastUpdatedDttm;
        this.createdSource = createdSource;
        this.lastUpdatedSource = lastUpdatedSource;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDto> permissions) {
        this.permissions = permissions != null ? permissions : new HashSet<>();
    }

    public LocalDateTime getCreatedDttm() {
        return createdDttm;
    }

    public void setCreatedDttm(LocalDateTime createdDttm) {
        this.createdDttm = createdDttm;
    }

    public LocalDateTime getLastUpdatedDttm() {
        return lastUpdatedDttm;
    }

    public void setLastUpdatedDttm(LocalDateTime lastUpdatedDttm) {
        this.lastUpdatedDttm = lastUpdatedDttm;
    }

    public String getCreatedSource() {
        return createdSource;
    }

    public void setCreatedSource(String createdSource) {
        this.createdSource = createdSource;
    }

    public String getLastUpdatedSource() {
        return lastUpdatedSource;
    }

    public void setLastUpdatedSource(String lastUpdatedSource) {
        this.lastUpdatedSource = lastUpdatedSource;
    }

    @Override
    public String toString() {
        return "RoleDto [role_id=" + role_id + ", name=" + name + ", permissions=" + permissions + ", createdDttm="
                + createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource=" + createdSource
                + ", lastUpdatedSource=" + lastUpdatedSource + "]";
    }
}
