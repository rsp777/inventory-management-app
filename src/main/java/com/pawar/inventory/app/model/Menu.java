package com.pawar.inventory.app.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "menu")
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_id")
	private int menu_id;

	@Column(name = "parent_id", insertable = false, updatable = false)
	private Integer parentId;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Menu parent;

	@JsonManagedReference
	@OneToMany(mappedBy = "parent")
	private List<Menu> children;

	@Column(name = "menu_name")
	private String menuName;

	@Column(name = "protocol")
	private String protocol;

	@Column(name = "hostname")
	private String hostname;

	@Column(name = "menu_link")
	private String menu_link;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("created_dttm")
	@Column(name = "created_dttm")
	private LocalDateTime createdDttm;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("last_updated_dttm")
	@Column(name = "last_updated_dttm")
	private LocalDateTime lastUpdatedDttm;

	@Column(name = "created_source")
	private String createdSource;

	@Column(name = "last_updated_source")
	private String lastUpdatedSource;

	@Column(name = "menu_type")
	private String menu_type;

	public Menu() {
	}

	public Menu(int menu_id, Integer parentId, Menu parent, List<Menu> children, String menu_name, String protocol,
			String hostname, String menu_link, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm,
			String createdSource, String lastUpdatedSource, String menu_type) {
		super();
		this.menu_id = menu_id;
		this.parentId = parentId;
		this.parent = parent;
		this.children = children;
		this.menuName = menu_name;
		this.protocol = protocol;
		this.hostname = hostname;
		this.menu_link = menu_link;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
		this.menu_type = menu_type;
	}

	public Menu(String protocol, String newMenuName, String newMenuLink, String newMenuType) {
		this.protocol = protocol;
		this.menuName = newMenuName;
		this.menu_link = newMenuLink;
		this.menu_type = newMenuType;
	}

	public Menu(String protocol, String newMenuName, String newMenuLink, String newHostname, String newMenuType) {
		this.protocol = protocol;
		this.menuName = newMenuName;
		this.menu_link = newMenuLink;
		this.hostname = newHostname;
		this.menu_type = newMenuType;
	}

	public int getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(int menu_id) {
		this.menu_id = menu_id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

	public String getMenu_name() {
		return menuName;
	}

	public void setMenu_name(String menu_name) {
		this.menuName = menu_name;
	}

	public String getMenu_link() {
		return menu_link;
	}

	public void setMenu_link(String menu_link) {
		this.menu_link = menu_link;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getMenu_type() {
		return menu_type;
	}

	public void setMenu_type(String menu_type) {
		this.menu_type = menu_type;
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
		return "Menu [menu_id=" + menu_id + ", parentId=" + parentId + ", children=" + children
				+ ", menu_name=" + menuName + ", protocol=" + protocol + ", hostname=" + hostname + ", menu_link="
				+ menu_link + ", createdDttm=" + createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm
				+ ", createdSource=" + createdSource + ", lastUpdatedSource=" + lastUpdatedSource + ", menu_type="
				+ menu_type + "]";
	}

}