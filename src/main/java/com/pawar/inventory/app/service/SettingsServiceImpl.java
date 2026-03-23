package com.pawar.inventory.app.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pawar.inventory.app.config.ExternalApiProperties;
import com.pawar.inventory.app.dto.SopConfigRequestDTO;
import com.pawar.inventory.app.dto.UserPreferencesRequestDTO;

@Service
public class SettingsServiceImpl implements SettingsService {

	private final ExternalApiProperties externalApiProperties;

	public SettingsServiceImpl(ExternalApiProperties externalApiProperties) {
		this.externalApiProperties = externalApiProperties;
	}

	@Override
	public Map<String, Object> getApplicationSettings() {
		Map<String, Object> appSettings = new HashMap<>();
		appSettings.put("appName", "Inventory Management Application");
		appSettings.put("version", "0.0.1");
		appSettings.put("environment", "dev");
		return appSettings;
	}

	@Override
	public Map<String, Object> getSetting(String key) {
		Map<String, Object> setting = new HashMap<>();
		setting.put("key", key);
		setting.put("value", null);
		return setting;
	}

	@Override
	public Map<String, Object> updateSetting(String key, String value, Object updatedBy) {
		Map<String, Object> updated = new HashMap<>();
		updated.put("key", key);
		updated.put("value", value);
		updated.put("updatedBy", updatedBy);
		return updated;
	}

	@Override
	public Map<String, Object> getAllSettings() {
		return getApplicationSettings();
	}

	@Override
	public Map<String, Object> getUserPreferences(String userName) {
		Map<String, Object> preferences = new HashMap<>();
		preferences.put("userName", userName);
		preferences.put("theme", "light");
		preferences.put("language", "en");
		preferences.put("timezone", "UTC");
		return preferences;
	}

	@Override
	public Map<String, Object> updateUserPreferences(String userName, UserPreferencesRequestDTO requestDTO) {
		Map<String, Object> updated = new HashMap<>();
		updated.put("userName", userName);
		updated.put("theme", requestDTO.getTheme());
		updated.put("language", requestDTO.getLanguage());
		updated.put("timezone", requestDTO.getTimezone());
		return updated;
	}

	@Override
	public Map<String, Object> getSopConfiguration() {
		Map<String, Object> sopConfig = new HashMap<>();
		sopConfig.put("externalApiUrl", externalApiProperties.getUrl());
		sopConfig.put("timeout", externalApiProperties.getTimeout());
		sopConfig.put("retryAttempts", externalApiProperties.getRetryAttempts());
		return sopConfig;
	}

	@Override
	public Map<String, Object> updateSopConfiguration(SopConfigRequestDTO requestDTO, Object updatedBy) {
		externalApiProperties.setUrl(requestDTO.getExternalApiUrl().trim());
		externalApiProperties.setTimeout(requestDTO.getTimeout());
		externalApiProperties.setRetryAttempts(requestDTO.getRetryAttempts());

		Map<String, Object> updated = getSopConfiguration();
		updated.put("updatedBy", updatedBy);
		return updated;
	}

	@Override
	public Map<String, Object> getApplicationHealth() {
		Map<String, Object> health = new HashMap<>();
		health.put("status", "UP");
		health.put("database", "CONNECTED");
		health.put("externalApi", "CONNECTED");
		health.put("timestamp", System.currentTimeMillis());
		return health;
	}
}
