package com.pawar.inventory.app.dto;

import java.time.LocalDateTime;

public class PermissionDto {

    private Integer id;
    private String name;
    private LocalDateTime createdDttm;
    private LocalDateTime lastUpdatedDttm;
    private String createdSource;
    private String lastUpdatedSource;

    public PermissionDto() {
    }

    public PermissionDto(String name) {
        this.name = name;
    }

    public PermissionDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public PermissionDto(Integer id, String name, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm,
                         String createdSource, String lastUpdatedSource) {
        this.id = id;
        this.name = name;
        this.createdDttm = createdDttm;
        this.lastUpdatedDttm = lastUpdatedDttm;
        this.createdSource = createdSource;
        this.lastUpdatedSource = lastUpdatedSource;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "PermissionDto [id=" + id + ", name=" + name + ", createdDttm=" + createdDttm
                + ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource=" + createdSource
                + ", lastUpdatedSource=" + lastUpdatedSource + "]";
    }
}
