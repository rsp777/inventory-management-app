package com.pawar.inventory.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MenuAssignmentException extends RuntimeException {
    public MenuAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}

