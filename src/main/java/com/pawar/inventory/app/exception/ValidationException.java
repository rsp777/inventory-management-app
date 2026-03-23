package com.pawar.inventory.app.exception;

import org.springframework.http.HttpStatus;
import com.pawar.inventory.app.exception.base.BaseException;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends BaseException {
    
    private List<String> errors;
    
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }
    
    public ValidationException(List<String> errors) {
        super("Validation failed", HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
