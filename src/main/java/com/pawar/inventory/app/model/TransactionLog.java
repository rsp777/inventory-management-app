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
@Table(name = "transaction_log")
public class TransactionLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int menu_log_id;
	
	@Column(name = "transaction_name")
	private String transaction_name;
	
	@Column(name = "data")
	private String data;
	
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
	
}
