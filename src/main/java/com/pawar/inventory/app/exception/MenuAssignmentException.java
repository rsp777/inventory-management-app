package com.pawar.inventory.app.exception;

import org.springframework.http.HttpStatus;
import com.pawar.inventory.app.exception.base.BaseException;

public class MenuAssignmentException extends BaseException {

    public MenuAssignmentException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "MENU_ASSIGNMENT_ERROR");
    }

    public MenuAssignmentException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
}

