package com.pawar.inventory.app.model;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "menu")
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int menu_id;
	
	@Column(name = "menu_name")
	private String menu_name;
	
	@Column(name = "menu_link")
	private String menu_link;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("created_dttm")
	@Column(name = "created_dttm")
	private LocalDateTime createdDttm;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("created_dttm")
	@Column(name = "last_updated_dttm")
	private LocalDateTime lastUpdatedDttm;
	
	@Column(name = "created_source")
	private String createdSource;
	
	@Column(name = "last_updated_source")
	private String lastUpdatedSource;

	public Menu() {
	}
	
	public Menu(int menu_id, String menu_name, String menu_link, LocalDateTime createdDttm,
			LocalDateTime lastUpdatedDttm, String createdSource, String lastUpdatedSource) {
		this.menu_id = menu_id;
		this.menu_name = menu_name;
		this.menu_link = menu_link;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	public int getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(int menu_id) {
		this.menu_id = menu_id;
	}

	public String getMenu_name() {
		return menu_name;
	}

	public void setMenu_name(String menu_name) {
		this.menu_name = menu_name;
	}

	public String getMenu_link() {
		return menu_link;
	}

	public void setMenu_link(String menu_link) {
		this.menu_link = menu_link;
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
		return "Menu [menu_id=" + menu_id + ", menu_name=" + menu_name + ", menu_link=" + menu_link + ", createdDttm="
				+ createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource=" + createdSource
				+ ", lastUpdatedSource=" + lastUpdatedSource + "]";
	}
}
