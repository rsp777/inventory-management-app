package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class SopConfigRequestDTO {

	@NotBlank(message = "External API URL is required")
	@Size(max = 1000, message = "External API URL must not exceed 1000 characters")
	private String externalApiUrl;

	@Positive(message = "Timeout must be greater than zero")
	private int timeout;

	@Positive(message = "Retry attempts must be greater than zero")
	private int retryAttempts;

	public String getExternalApiUrl() {
		return externalApiUrl;
	}

	public void setExternalApiUrl(String externalApiUrl) {
		this.externalApiUrl = externalApiUrl;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getRetryAttempts() {
		return retryAttempts;
	}

	public void setRetryAttempts(int retryAttempts) {
		this.retryAttempts = retryAttempts;
	}
}