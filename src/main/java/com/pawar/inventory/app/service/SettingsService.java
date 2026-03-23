package com.pawar.inventory.app.service;

import java.util.Map;

import com.pawar.inventory.app.dto.SopConfigRequestDTO;
import com.pawar.inventory.app.dto.UserPreferencesRequestDTO;

public interface SettingsService {

	Map<String, Object> getApplicationSettings();

	Map<String, Object> getSetting(String key);

	Map<String, Object> updateSetting(String key, String value, Object updatedBy);

	Map<String, Object> getAllSettings();

	Map<String, Object> getUserPreferences(String userName);

	Map<String, Object> updateUserPreferences(String userName, UserPreferencesRequestDTO requestDTO);

	Map<String, Object> getSopConfiguration();

	Map<String, Object> updateSopConfiguration(SopConfigRequestDTO requestDTO, Object updatedBy);

	Map<String, Object> getApplicationHealth();
}