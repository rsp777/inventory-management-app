package com.pawar.inventory.app.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawar.todo.dto.PermissionDto;
import com.pawar.todo.dto.RoleDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "permission")
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String name;

	@JsonInclude(value = Include.CUSTOM)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("created_dttm")
	@Column(name = "created_dttm")
	private LocalDateTime createdDttm;

//	@JsonIgnore
	@JsonInclude(value = Include.CUSTOM)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("last_updated_dttm")
	@Column(name = "last_updated_dttm")
	private LocalDateTime lastUpdatedDttm;

//	@JsonIgnore
	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "created_source")
	private String createdSource;

//	@JsonIgnore
	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "last_updated_source")
	private String lastUpdatedSource;

	public Permission() {
	}

	public Permission(Integer id, String name, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm,
			String createdSource, String lastUpdatedSource) {
		this.id = id;
		this.name = name;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

//	public Permission(Long id, String name, LocalDateTime createdDttm,
//			LocalDateTime lastUpdatedDttm, String createdSource, String lastUpdatedSource) {
//		super();
//		this.id = id;
//		this.name = name;
//		this.createdDttm = createdDttm;
//		this.lastUpdatedDttm = lastUpdatedDttm;
//		this.createdSource = createdSource;
//		this.lastUpdatedSource = lastUpdatedSource;
//	}

	public Permission(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;

	}

	public Permission(@Valid PermissionDto permissionDto) {
		this.id = permissionDto.getId();
		this.name = permissionDto.getName();

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

//	public Set<RolePermission> convertDtoToEntityRolePermission(Set<RolePermissionDto> rolePermissionDtos) {
//
//		System.out.println("rolePermissionDtos : {}" + rolePermissionDtos);
//		Set<RolePermission> rolePermissions = new HashSet<>();
//		for (RolePermissionDto rolePermissionDto : rolePermissionDtos) {
//			RolePermission rolePermission = new RolePermission();
//			rolePermission.setId(rolePermissionDto.getId());
//			rolePermission.setPermission(convertDtoToEntity(rolePermissionDto.getPermissionDto()));
//			rolePermissions.add(rolePermission);
//		}
//		return rolePermissions;
//	}

	public Permission convertDtoToEntity(PermissionDto permissionDto) {
		Permission permission = new Permission(permissionDto);
		return permission;
	}

	public Set<Permission> convertDtoToEntity(Set<PermissionDto> permissionDtos) {

		System.out.println("permissionDtos : {}" + permissionDtos);
		Set<Permission> permissions = new HashSet<>();
		for (PermissionDto permissionDto : permissionDtos) {
			Permission permission = new Permission();
			permission.setId(permissionDto.getId());
			permission.setName(permissionDto.getName());
			permissions.add(permission);
		}
		return permissions;
	}

	public Set<PermissionDto> convertEntityToDto(Set<Permission> permissions) {

		Set<PermissionDto> permissionDtos = new HashSet<>();

		for (Permission permission : permissions) {
			PermissionDto permissionDto = new PermissionDto(permission.getName());
			permissionDtos.add(permissionDto);
		}
		return permissionDtos;
	}

	@Override
	public String toString() {
		return "Permission [id=" + id + ", name=" + name + ", createdDttm=" + createdDttm + ", lastUpdatedDttm="
				+ lastUpdatedDttm + ", createdSource=" + createdSource + ", lastUpdatedSource=" + lastUpdatedSource
				+ "]";
	}

}
