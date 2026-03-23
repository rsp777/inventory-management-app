package com.pawar.inventory.app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionLogDTO {

	private Long id;

	@JsonProperty("created_dttm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private LocalDateTime createdDttm;

	@JsonProperty("last_updated_dttm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private LocalDateTime lastUpdatedDttm;

	@JsonProperty("created_source")
	private String createdSource;

	@JsonProperty("last_updated_source")
	private String lastUpdatedSource;

	private String data;

	@JsonProperty("transaction_name")
	private String transactionName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
}