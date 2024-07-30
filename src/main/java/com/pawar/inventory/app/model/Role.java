package com.pawar.inventory.app.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Integer role_id;

	@Column(nullable = false, unique = true)
	private String name;

	@JsonInclude(value = Include.CUSTOM)
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "roles_permissions", // This should be your association table
			joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private Set<Permission> permissions = new HashSet<>();

	@JsonInclude(value = Include.CUSTOM)
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "menu_access", // This should be your association table
			joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"))
	private Set<Menu> menus = new HashSet<>();

//
//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "roles_permissions", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
//			@JoinColumn(name = "permission_id")
//
//	})
//	private Set<Permission> permissions = new HashSet<>();

	@JsonInclude(value = Include.CUSTOM)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("created_dttm")
	@Column(name = "created_dttm")
	private LocalDateTime createdDttm;

	@JsonInclude(value = Include.CUSTOM)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("last_updated_dttm")
	@Column(name = "last_updated_dttm")
	private LocalDateTime lastUpdatedDttm;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "created_source")
	private String createdSource;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "last_updated_source")
	private String lastUpdatedSource;

	public Role() {
	}

	public Role(Integer role_id, String name, Set<Menu> menus, Set<Permission> permissions, LocalDateTime createdDttm,
			LocalDateTime lastUpdatedDttm, String createdSource, String lastUpdatedSource) {
		super();
		this.role_id = role_id;
		this.name = name;
		this.menus = this.menus;
		this.permissions = permissions;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	public Role(Integer role_id, String name) {
		super();
		this.role_id = role_id;
		this.name = name;
	}

	public Role(Integer role_id, String name, Set<Menu> menus) {
		super();
		this.role_id = role_id;
		this.name = name;
		this.menus = menus;
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

	public Set<Menu> getMenus() {
		return menus;
	}

	public void setMenus(Set<Menu> menus) {
		this.menus = menus;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
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
		return "Role [role_id=" + role_id + ", name=" + name + ", permissions=" + permissions + ", menus=" + menus
				+ ", createdDttm=" + createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource="
				+ createdSource + ", lastUpdatedSource=" + lastUpdatedSource + "]";
	}

}
