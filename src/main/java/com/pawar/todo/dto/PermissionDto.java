package com.pawar.todo.dto;

public class PermissionDto {
    private Integer id;
    private String name;

    public PermissionDto() {
    }

    public PermissionDto(String name) {
        this.name = name;
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
}
