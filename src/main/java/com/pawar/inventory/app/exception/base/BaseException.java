package com.pawar.inventory.app.exception.base;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Abstract base exception class for all custom application exceptions.
 * Provides standardized error handling with HTTP status codes and timestamps.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public abstract class BaseException extends RuntimeException {
    
    private final HttpStatus httpStatus;
    private final LocalDateTime timestamp;
    private final String errorCode;
    
    public BaseException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = null;
        this.timestamp = LocalDateTime.now();
    }
    
    public BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = null;
        this.timestamp = LocalDateTime.now();
    }
    
    public BaseException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = null;
        this.timestamp = LocalDateTime.now();
    }
    
    public BaseException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = null;
        this.timestamp = LocalDateTime.now();
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
