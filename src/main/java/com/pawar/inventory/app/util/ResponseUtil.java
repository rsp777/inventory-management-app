package com.pawar.inventory.app.util;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

/**
 * Utility class for standardizing controller response handling.
 * Provides methods for common response patterns and model setup.
 */
public class ResponseUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
    
    private ResponseUtil() {
        // Prevent instantiation
    }
    
    /**
     * Creates a success response entity
     */
    public static <T> ResponseEntity<T> success(T data) {
        return ResponseEntity.ok(data);
    }
    
    /**
     * Creates a success response entity with message
     */
    public static <T> ResponseEntity<Map<String, Object>> success(T data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message", message);
        response.put("status", 200);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Creates a success response entity with custom status code
     */
    public static <T> ResponseEntity<T> success(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(data);
    }
    
    /**
     * Creates an error response entity with default HTTP status
     */
    public static ResponseEntity<Map<String, Object>> error(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Creates an error response entity
     */
    public static ResponseEntity<Map<String, Object>> error(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("status", status.value());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Creates an error response entity with additional details
     */
    public static ResponseEntity<Map<String, Object>> error(
            String message, String details, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("details", details);
        response.put("status", status.value());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Safely handles exceptions and returns error response
     */
    public static ResponseEntity<Map<String, Object>> handleException(
            Exception ex, HttpStatus status) {
        logger.error("Exception occurred: {}", ex.getMessage(), ex);
        return error(ex.getMessage(), status);
    }
    
    /**
     * Safely handles exceptions and returns error response with details
     */
    public static ResponseEntity<Map<String, Object>> handleException(
            Exception ex, String details, HttpStatus status) {
        logger.error("Exception occurred: {}", ex.getMessage(), ex);
        return error(ex.getMessage(), details, status);
    }
    
    /**
     * Adds common attributes to Thymeleaf model for views
     * Used by controllers to standardize model attributes
     */
    public static void addViewAttributes(Model model, 
            MenuFilterUtil.MenuCategories menuCategories,
            String currentUri) {
        
        model.addAttribute("menus", menuCategories.rightFrameMenus);
        model.addAttribute("nav_menus", menuCategories.navigationMenus);
        model.addAttribute("side_menus", menuCategories.sideMenus);
        model.addAttribute("currentMenu", currentUri);
        
        logger.debug("Added view attributes to model");
    }
    
    /**
     * Adds common attributes for pages with basic navigation
     */
    public static void addBasicViewAttributes(Model model,
            MenuFilterUtil.MenuCategories menuCategories,
            String currentUri) {
        
        model.addAttribute("nav_menus", menuCategories.navigationMenus);
        model.addAttribute("currentMenu", currentUri);
        
        logger.debug("Added basic view attributes to model");
    }
}
