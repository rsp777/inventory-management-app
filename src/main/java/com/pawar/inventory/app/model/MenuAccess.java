package com.pawar.inventory.app.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_access")
@IdClass(MenuAccessId.class)
public class MenuAccess {

	@Id
	@Column(name = "menu_id")
	private int menuId;

	@Id
	@Column(name = "role_id")
	private Integer roleId;

//
//	@JsonInclude(value = Include.CUSTOM)
//	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
//	@JsonProperty("created_dttm")
//	@Column(name = "created_dttm")
//	private LocalDateTime createdDttm;
//
//	@JsonInclude(value = Include.CUSTOM)
//	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
//	@JsonProperty("last_updated_dttm")
//	@Column(name = "last_updated_dttm")
//	private LocalDateTime lastUpdatedDttm;
//
//	@JsonInclude(value = Include.CUSTOM)
//	@Column(name = "created_source")
//	private String createdSource;
//
//	@JsonInclude(value = Include.CUSTOM)
//	@Column(name = "last_updated_source")
//	private String lastUpdatedSource;

	public MenuAccess() {
	}

	public MenuAccess(int menuId, Integer roleId) {
		super();
		this.menuId = menuId;
		this.roleId = roleId;
	}

	public int getMenuId() {
		return menuId;
	}

	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	@Override
	public String toString() {
		return "MenuAccess [menuId=" + menuId + ", roleId=" + roleId + "]";
	}

}
