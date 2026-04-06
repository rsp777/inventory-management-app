package com.pawar.inventory.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "external.api")
public class ExternalApiProperties {

	private String url = AppConstants.ExternalApi.DEFAULT_URL;
	private int timeout = AppConstants.ExternalApi.DEFAULT_TIMEOUT;
	private int retryAttempts = AppConstants.ExternalApi.DEFAULT_RETRY_ATTEMPTS;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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