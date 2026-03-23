package com.pawar.inventory.app.exception;

import org.springframework.http.HttpStatus;
import com.pawar.inventory.app.exception.base.BaseException;

public class RoleDeletionException extends BaseException {

    public RoleDeletionException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "ROLE_DELETION_ERROR");
    }
	
	public RoleDeletionException(String message, Exception e) {
        super(message, HttpStatus.BAD_REQUEST, e);
    }
	
	
}
