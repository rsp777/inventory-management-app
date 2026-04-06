package com.pawar.inventory.app.exception;

import org.springframework.http.HttpStatus;
import com.pawar.inventory.app.exception.base.BaseException;

public class DuplicateResourceException extends BaseException {
    
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
    
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
}
