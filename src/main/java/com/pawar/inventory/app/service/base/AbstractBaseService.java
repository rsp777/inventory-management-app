package com.pawar.inventory.app.service.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract base service class providing common service-level operations.
 * 
 * All services should extend this class to provide:
 * - Consistent logging
 * - Transaction management
 * - Exception handling patterns
 * - Common utility methods
 */
@Service
@Transactional
public abstract class AbstractBaseService {
    
    protected final Logger logger;
    
    protected AbstractBaseService() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
    
    /**
     * Logs an info message with the service context
     */
    protected void logInfo(String message) {
        logger.info("[{}] {}", this.getClass().getSimpleName(), message);
    }
    
    /**
     * Logs a warning message with the service context
     */
    protected void logWarning(String message) {
        logger.warn("[{}] {}", this.getClass().getSimpleName(), message);
    }
    
    /**
     * Logs an error message with the service context and exception
     */
    protected void logError(String message, Exception e) {
        logger.error("[{}] {}", this.getClass().getSimpleName(), message, e);
    }
    
    /**
     * Validates that an object is not null, throws RuntimeException if null
     */
    protected void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new RuntimeException(fieldName + " cannot be null");
        }
    }
    
    /**
     * Validates that a string is not null or empty
     */
    protected void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(fieldName + " cannot be null or empty");
        }
    }
}
