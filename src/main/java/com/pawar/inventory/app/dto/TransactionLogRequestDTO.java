package com.pawar.inventory.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TransactionLogRequestDTO {

	@JsonProperty("transaction_name")
	@NotBlank(message = "Transaction name is required")
	@Size(max = 255, message = "Transaction name must not exceed 255 characters")
	private String transactionName;

	@Size(max = 4000, message = "Data must not exceed 4000 characters")
	private String data;

	@Size(max = 100, message = "Source must not exceed 100 characters")
	private String source;

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}