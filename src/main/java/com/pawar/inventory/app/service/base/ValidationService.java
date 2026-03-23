package com.pawar.inventory.app.service.base;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 * Service for centralized validation logic.
 * Provides reusable validation methods for all domain entities.
 * 
 * Consolidates validation rules to prevent duplication across service layer.
 */
@Service
public class ValidationService extends AbstractBaseService {
	
	// Validation patterns
	private static final Pattern EMAIL_PATTERN = 
		Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
	private static final Pattern ALPHANUMERIC_PATTERN = 
		Pattern.compile("^[a-zA-Z0-9_\\-]+$");
	
	/**
	 * Validates that a string is not null or empty
	 * 
	 * @param value string to validate
	 * @param fieldName name of field for error message
	 * @return true if valid, false otherwise
	 */
	public boolean isNotEmpty(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			logWarning(fieldName + " cannot be null or empty");
			return false;
		}
		return true;
	}
	
	/**
	 * Validates that an integer is greater than zero
	 * 
	 * @param value integer to validate
	 * @param fieldName name of field for error message
	 * @return true if valid, false otherwise
	 */
	public boolean validateGreaterThanZero(int value, String fieldName) {
		if (value <= 0) {
			logWarning(fieldName + " must be greater than zero. Received: " + value);
			return false;
		}
		return true;
	}
	
	/**
	 * Validates email format
	 * 
	 * @param email email address to validate
	 * @return true if valid email format, false otherwise
	 */
	public boolean validateEmail(String email) {
		if (email == null || email.isEmpty()) {
			logWarning("Email cannot be null or empty");
			return false;
		}
		
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			logWarning("Invalid email format: " + email);
			return false;
		}
		
		logInfo("Email validated: " + email);
		return true;
	}
	
	/**
	 * Validates that a string contains only alphanumeric characters and underscores/hyphens
	 * 
	 * @param value string to validate
	 * @param fieldName name of field
	 * @return true if valid, false otherwise
	 */
	public boolean validateAlphanumeric(String value, String fieldName) {
		if (value == null || value.isEmpty()) {
			logWarning(fieldName + " cannot be null or empty");
			return false;
		}
		
		if (!ALPHANUMERIC_PATTERN.matcher(value).matches()) {
			logWarning(fieldName + " contains invalid characters: " + value);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Validates string length constraints
	 * 
	 * @param value string to validate
	 * @param minLength minimum length (inclusive)
	 * @param maxLength maximum length (inclusive)
	 * @param fieldName name of field for error message
	 * @return true if valid length, false otherwise
	 */
	public boolean validateLength(String value, int minLength, int maxLength, String fieldName) {
		if (value == null) {
			logWarning(fieldName + " cannot be null");
			return false;
		}
		
		int length = value.length();
		if (length < minLength || length > maxLength) {
			logWarning(fieldName + " length must be between " + minLength + " and " + 
				maxLength + ". Received: " + length);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Validates menu request data
	 * 
	 * @param menuName menu name
	 * @param menuUrl menu URL
	 * @param roleId role ID
	 * @return true if valid, false otherwise
	 */
	public boolean validateMenuRequest(String menuName, String menuUrl, int roleId) {
		boolean isValid = true;
		
		if (!isNotEmpty(menuName, "Menu Name")) {
			isValid = false;
		}
		
		if (!isNotEmpty(menuUrl, "Menu URL")) {
			isValid = false;
		}
		
		if (!validateGreaterThanZero(roleId, "Role ID")) {
			isValid = false;
		}
		
		if (isValid) {
			logInfo("Menu request validation passed");
		}
		
		return isValid;
	}
	
	/**
	 * Validates category request data
	 * 
	 * @param categoryName category name
	 * @param categoryCode category code
	 * @return true if valid, false otherwise
	 */
	public boolean validateCategoryRequest(String categoryName, String categoryCode) {
		boolean isValid = true;
		
		if (!isNotEmpty(categoryName, "Category Name")) {
			isValid = false;
		}
		
		if (!isNotEmpty(categoryCode, "Category Code")) {
			isValid = false;
		}
		
		if (!validateAlphanumeric(categoryCode, "Category Code")) {
			isValid = false;
		}
		
		if (isValid) {
			logInfo("Category request validation passed");
		}
		
		return isValid;
	}
	
	/**
	 * Validates item request data
	 * 
	 * @param itemName item name
	 * @param itemSku item SKU
	 * @param categoryId category ID
	 * @return true if valid, false otherwise
	 */
	public boolean validateItemRequest(String itemName, String itemSku, int categoryId) {
		boolean isValid = true;
		
		if (!isNotEmpty(itemName, "Item Name")) {
			isValid = false;
		}
		
		if (!isNotEmpty(itemSku, "Item SKU")) {
			isValid = false;
		}
		
		if (!validateGreaterThanZero(categoryId, "Category ID")) {
			isValid = false;
		}
		
		if (isValid) {
			logInfo("Item request validation passed");
		}
		
		return isValid;
	}
	
	/**
	 * Validates location request data
	 * 
	 * @param locationCode location code
	 * @param locationName location name
	 * @return true if valid, false otherwise
	 */
	public boolean validateLocationRequest(String locationCode, String locationName) {
		boolean isValid = true;
		
		if (!isNotEmpty(locationCode, "Location Code")) {
			isValid = false;
		}
		
		if (!isNotEmpty(locationName, "Location Name")) {
			isValid = false;
		}
		
		if (!validateAlphanumeric(locationCode, "Location Code")) {
			isValid = false;
		}
		
		if (isValid) {
			logInfo("Location request validation passed");
		}
		
		return isValid;
	}
	
	/**
	 * Validates LPN (License Plate Number) request data
	 * 
	 * @param lpnNumber LPN number
	 * @param locationId location ID
	 * @return true if valid, false otherwise
	 */
	public boolean validateLpnRequest(String lpnNumber, int locationId) {
		boolean isValid = true;
		
		if (!isNotEmpty(lpnNumber, "LPN Number")) {
			isValid = false;
		}
		
		if (!validateGreaterThanZero(locationId, "Location ID")) {
			isValid = false;
		}
		
		if (isValid) {
			logInfo("LPN request validation passed");
		}
		
		return isValid;
	}
	
	/**
	 * Validates role request data
	 * 
	 * @param roleName role name
	 * @return true if valid, false otherwise
	 */
	public boolean validateRoleRequest(String roleName) {
		if (!isNotEmpty(roleName, "Role Name")) {
			return false;
		}
		
		logInfo("Role request validation passed");
		return true;
	}
	
	/**
	 * Validates that an object is not null
	 * 
	 * @param object object to validate
	 * @param objectName name of object for error message
	 * @return true if valid, false otherwise
	 */
	public boolean isNotNull(Object object, String objectName) {
		if (object == null) {
			logWarning(objectName + " cannot be null");
			return false;
		}
		return true;
	}
}
